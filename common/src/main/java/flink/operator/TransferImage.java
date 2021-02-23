package flink.operator;

import flink.tracker.CONFIG;
import flink.tracker.Track;
import flink.tracker.Tracker;
import flink.types.Information;
import flink.types.Output;
import flink.utils.ByteTransformer;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import static flink.tracker.DetectContours.detectionContours;
import static flink.tracker.ProcessFrame.processFrame;
import static flink.utils.ByteTransformer.Mat2bufferedImage;

public class TransferImage implements FlatMapFunction<Tuple2<Long, byte[]>, Output> {
    static int i;
    static Mat imag, orgin, kalman, diffFrame, outbox;
    static Vector<Rect> array;
    static JLabel vidpanel, vidpanel2, vidpanel3, vidpanel4;
    static BackgroundSubtractorMOG2 mBGSub;
    public static Tracker tracker;
    static HashSet noticeObj = new HashSet<>();

    public TransferImage() {
        JFrame jFrame = new JFrame("MULTIPLE-TARGET TRACKING");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        vidpanel = new JLabel();
        jFrame.setContentPane(vidpanel);
        jFrame.setSize(CONFIG.FRAME_WIDTH, CONFIG.FRAME_HEIGHT);
        jFrame.setLocation((3 / 4) * Toolkit.getDefaultToolkit().getScreenSize().width,
                (3 / 4) * Toolkit.getDefaultToolkit().getScreenSize().height);
        jFrame.setVisible(true);

        JFrame jFrame2 = new JFrame("BACKGROUND SUBSTRACTION");
        jFrame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        vidpanel2 = new JLabel();
        jFrame2.setContentPane(vidpanel2);
        jFrame2.setSize(CONFIG.FRAME_WIDTH, CONFIG.FRAME_HEIGHT);
        jFrame2.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2,
                (3 / 4) * Toolkit.getDefaultToolkit().getScreenSize().height);
        jFrame2.setVisible(true);

        JFrame jFrame3 = new JFrame("VIDEO INPUT");
        jFrame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        vidpanel3 = new JLabel();
        jFrame3.setContentPane(vidpanel3);
        jFrame3.setSize(CONFIG.FRAME_WIDTH, CONFIG.FRAME_HEIGHT);
        jFrame3.setLocation((3 / 4) * Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height / 2);
        jFrame3.setVisible(false);

        JFrame jFrame4 = new JFrame("KALMAN FILTER");
        jFrame4.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        vidpanel4 = new JLabel();
        jFrame4.setContentPane(vidpanel4);
        jFrame4.setSize(CONFIG.FRAME_WIDTH, CONFIG.FRAME_HEIGHT);
        jFrame4.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2,
                Toolkit.getDefaultToolkit().getScreenSize().height / 2);
        jFrame4.setVisible(false);

        // initial global attributes
        i = 0;
        outbox = new Mat();
        mBGSub = Video.createBackgroundSubtractorMOG2();
        tracker = new Tracker((float) CONFIG._dt,
                (float) CONFIG._Accel_noise_mag, CONFIG._dist_thres,
                CONFIG._maximum_allowed_skipped_frames,
                CONFIG._max_trace_length);
    }

    public Vector<Track> detect(Mat frame, Long eventTime) throws Exception {
        if (frame == null) {
            System.out.println("Null frame");
            return null;
        }
        Imgproc.resize(frame, frame, new Size(CONFIG.FRAME_WIDTH, CONFIG.FRAME_HEIGHT),
                0., 0., Imgproc.INTER_LINEAR);
        imag = frame.clone();
        orgin = frame.clone();
        kalman = frame.clone();

        if (i == 0) {
            // jFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
            diffFrame = new Mat(outbox.size(), CvType.CV_8UC1);
            diffFrame = outbox.clone();
        }

        if (i > 0) {
            diffFrame = new Mat(frame.size(), CvType.CV_8UC1);
            processFrame(null, frame, diffFrame, mBGSub);
            frame = diffFrame.clone();

            array = detectionContours(diffFrame);

            Vector<Point> detections = new Vector<>();
            // detections.clear();
            Iterator<Rect> it = array.iterator();
            while (it.hasNext()) {
                Rect obj = it.next();

                int ObjectCenterX = (int) ((obj.tl().x + obj.br().x) / 2);
                int ObjectCenterY = (int) ((obj.tl().y + obj.br().y) / 2);

                Point pt = new Point(ObjectCenterX, ObjectCenterY);
                detections.add(pt);
            }

            if (array.size() > 0) {
                tracker.update(array, detections, imag, eventTime);

                // draw object
                if (i > CONFIG._skip_frames) {
                    Iterator<Rect> it3 = array.iterator();
                    while (it3.hasNext()) {
                        Rect obj = it3.next();

                        int ObjectCenterX = (int) ((obj.tl().x + obj.br().x) / 2);
                        int ObjectCenterY = (int) ((obj.tl().y + obj.br().y) / 2);

                        Point pt = new Point(ObjectCenterX, ObjectCenterY);

                        Imgproc.rectangle(imag, obj.br(), obj.tl(), new Scalar(0, 255, 0), 2);
                        Imgproc.circle(imag, pt, 1, new Scalar(0, 0, 255), 2);
                    }
                }
            } else { // array is empty
                tracker.updateKalman(imag, detections);
            }

            /* draw trace
            if (i > CONFIG._skip_frames) {
                for (int k = 0; k < tracker.tracks.size(); k++) {
                    int traceNum = tracker.tracks.get(k).trace.size();
                    if (traceNum > 1) {
                        // draw trace
                        for (int jt = 1; jt < tracker.tracks.get(k).trace.size(); jt++) {
                            Imgproc.line(imag,
                                    tracker.tracks.get(k).trace.get(jt - 1),
                                    tracker.tracks.get(k).trace.get(jt),
                                    CONFIG.Colors[(int) (tracker.tracks.get(k).track_id % 9)],
                                    2, 4, 0);
                        }
                    }
                }
            }
             */
        }

        i += 1;

        ImageIcon image = new ImageIcon(Mat2bufferedImage(imag));
        vidpanel.setIcon(image);
        vidpanel.repaint();

        ImageIcon image2 = new ImageIcon(Mat2bufferedImage(frame));
        vidpanel2.setIcon(image2);
        vidpanel2.repaint();

        ImageIcon image3 = new ImageIcon(Mat2bufferedImage(orgin));
        vidpanel3.setIcon(image3);
        vidpanel3.repaint();

        ImageIcon image4 = new ImageIcon(Mat2bufferedImage(kalman));
        vidpanel4.setIcon(image4);
        vidpanel4.repaint();

        return tracker.tracks;
    }

    @Override
    public void flatMap(Tuple2<Long, byte[]> in, Collector<Output> collector) throws Exception {
        byte[] bytes = in.f1;
        Long eventTime = in.f0;

        long start = System.currentTimeMillis();
//        LOG.info("Get input image(event_time: {})", eventTime);

        detect(ByteTransformer.Byte2Mat(bytes), eventTime);

//        LOG.info("Network takes:{} ms\tProcess takes: {} ms", start - eventTime, System.currentTimeMillis() - start);

        for (int k = 0; k < tracker.tracks.size(); k++) {
            Track track = tracker.tracks.get(k);
            int traceNum = track.trace.size();
            if (traceNum > 20 && !noticeObj.contains(track.track_id)) {
                noticeObj.add(track.track_id);

                if (i > CONFIG._skip_frames){
                    Double angle = 90.;
                    Information inf = new Information(track.track_id, eventTime);
                    inf.setLocation(track.trace.get(traceNum - 1));
                    inf.setAngle(angle);

                    Output output = new Output();
                    output.setInfo(inf);
                    output.setSpeed((track.trace.get(0).y - track.trace.get(traceNum - 1).y)/(eventTime - track.track_id));

                    collector.collect(output);
                }
            }
        }
    }
}
