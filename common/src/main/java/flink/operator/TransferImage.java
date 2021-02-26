package flink.operator;

import flink.config.CONFIG;
import flink.tracker.Track;
import flink.tracker.Tracker;
import flink.types.Information;
import flink.utils.ByteTransformer;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import static flink.tracker.DetectContours.detectionContours;
import static flink.utils.ByteTransformer.Mat2bufferedImage;

public class TransferImage implements FlatMapFunction<Tuple2<Long, byte[]>, Information> {
    static Mat imag, orgin, kalman, outbox;
    static Vector<RotatedRect> array;
    static JLabel vidpanel;
    public static Tracker tracker;
    Logger LOG = LoggerFactory.getLogger("time");

    public static Vector<RotatedRect> getCurrentArray(){
        return array;
    }

    public static Tracker getCurrentTracker(){
        return tracker;
    }

    public static Mat getCurrentImage(){
        return imag;
    }

    public TransferImage() {
        if (CONFIG._draw_image_flag) {
            JFrame jFrame = new JFrame("MULTIPLE-TARGET TRACKING");
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            vidpanel = new JLabel();
            jFrame.setContentPane(vidpanel);
            jFrame.setSize(CONFIG.FRAME_WIDTH, CONFIG.FRAME_HEIGHT);
            jFrame.setLocation(200, 200);
            jFrame.setVisible(true);
        }

        // initial global attributes
        outbox = new Mat();
        tracker = new Tracker((float) CONFIG._dt,
                (float) CONFIG._Accel_noise_mag, CONFIG._dist_thres,
                CONFIG._maximum_allowed_skipped_frames,
                CONFIG._max_trace_length);
    }

    public void detect(Mat frame, Long eventTime) throws Exception {
        Imgproc.resize(frame, frame, new Size(CONFIG.FRAME_WIDTH, CONFIG.FRAME_HEIGHT),
                0., 0., Imgproc.INTER_LINEAR);
        imag = frame.clone();
        orgin = frame.clone();
        kalman = frame.clone();

        array = detectionContours(frame);

        Vector<Point> detections = new Vector<>();
        // detections.clear();
        for (RotatedRect obj : array) {
            detections.add(obj.center);
        }

        if (array.size() > 0) {
            tracker.update(array, detections, imag, eventTime);
        } else { // array is empty
            tracker.updateKalman(imag, detections);
        }

        // draw object
        if (CONFIG._draw_image_flag) {
            for (RotatedRect obj : array) {
                Imgproc.rectangle(imag, obj.boundingRect(),
                        new Scalar(0, 255, 0), 2);
                Imgproc.circle(imag, obj.center, 1,
                        new Scalar(0, 0, 255), 2);
            }

            for (int k = 0; k < tracker.tracks.size(); k++) {
                Track track = tracker.tracks.get(k);
//                String location = "(" + track.prediction.x + ", "
//                        + track.prediction.y + ")";
                Imgproc.putText(imag, "" + track.track_id,
                        track.prediction, 2, 1,
                        new Scalar(255, 255, 255), 1);
            }

            ImageIcon image = new ImageIcon(Mat2bufferedImage(imag));
            vidpanel.setIcon(image);
            vidpanel.repaint();
        }
    }

    @Override
    public void flatMap(Tuple2<Long, byte[]> in, Collector<Information> collector) throws Exception {
        byte[] bytes = in.f1;
        Long eventTime = in.f0;

        long start = System.currentTimeMillis();

        detect(ByteTransformer.Byte2Mat(bytes), eventTime);

        LOG.info("Network takes:{} ms\tProcess takes: {} ms", start - eventTime, System.currentTimeMillis() - start);

        for (int k = 0; k < tracker.tracks.size(); k++) {
            Track track = tracker.tracks.get(k);

            Information inf = new Information(track.track_id, eventTime);
            inf.setLocation(track.prediction);
            inf.setAngle(track.angle);

            collector.collect(inf);
        }
    }
}
