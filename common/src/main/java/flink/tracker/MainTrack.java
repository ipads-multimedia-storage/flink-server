package flink.tracker;

import flink.config.CONFIG;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.io.Serializable;
import java.util.Vector;

import static flink.tracker.DetectContours.detectionContours;
import static flink.utils.ByteTransformer.Mat2bufferedImage;

public class MainTrack implements Serializable {
    static Mat imag;
    static Vector<RotatedRect> array;
    static Tracker tracker;

    /*
     *  class DisplayVideo
     *  TODO: how to deal with long time no image
     */
    private class DisplayVideo implements Runnable {
        private final JLabel vidpanel;

        DisplayVideo(){
            JFrame jFrame = new JFrame("MULTIPLE-TARGET TRACKING");
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            vidpanel = new JLabel();
            jFrame.setContentPane(vidpanel);
            jFrame.setSize(CONFIG.FRAME_WIDTH, CONFIG.FRAME_HEIGHT);
            jFrame.setLocation(200, 200);
            jFrame.setVisible(true);
        }

        @Override
        public void run() {
            // -----------------------------------------
            // this thread try to fetch image without stop
            // and display both image and detected objects
            // -----------------------------------------
            while (true) {
                // skip empty image
                if (imag.empty()){
                    continue;
                }

                // display all the detected objects
                // rectangle and center and ID
                for (RotatedRect obj : array) {
                    Imgproc.rectangle(imag, obj.boundingRect(),
                            new Scalar(0, 255, 0), 2);
                    Imgproc.circle(imag, obj.center, 1,
                            new Scalar(0, 0, 255), 2);
                }

                for (int k = 0; k < tracker.tracks.size(); k++) {
                    Track track = tracker.tracks.get(k);
                    if (track.skipped_frames < CONFIG._maximum_allowed_skipped_frames) {
                        Imgproc.putText(imag, track.track_id + "", track.prediction,
                                2, 1, new Scalar(255, 255, 255), 1);
                    }
                }

                // repaint the image
                ImageIcon image = new ImageIcon(Mat2bufferedImage(imag));
                vidpanel.setIcon(image);
                vidpanel.repaint();
            }
        }
    }

    public MainTrack() {
        // -----------------------------------------
        // initial global attributes
        // -----------------------------------------
        imag = new Mat();
        array = new Vector<>();
        tracker = new Tracker((float) CONFIG._dt,
                (float) CONFIG._Accel_noise_mag, CONFIG._dist_thres,
                CONFIG._maximum_allowed_skipped_frames,
                CONFIG._max_trace_length);

        // ----------------------------------------
        // if _draw_image_flag is true
        // create a thread to display image
        // ----------------------------------------
        if (CONFIG._draw_image_flag) {
            DisplayVideo display = new DisplayVideo();
            new Thread(display).start();
        }
    }

    public Tracker detect(Mat frame, Long eventTime, Vector<RotatedRect> array) throws Exception {
        /*
        // ----------------------------------------
        // process the image
        // ----------------------------------------
        Imgproc.resize(frame, frame, new Size(CONFIG.FRAME_WIDTH, CONFIG.FRAME_HEIGHT),
                0., 0., Imgproc.INTER_LINEAR);
        imag = frame.clone();

        // ----------------------------------------
        // detect all the existed objects in thia frame
        // ----------------------------------------
        array = detectionContours(frame);
         */

        imag = frame.clone();

        // ----------------------------------------
        // use these information to update tracker
        // ----------------------------------------
        Vector<Point> detections = new Vector<>();
        for (RotatedRect obj : array) {
            detections.add(obj.center);
        }

        if (array.size() > 0) {
            tracker.update(array, detections, imag, eventTime);
        } else { // array is empty
            tracker.updateKalman(imag, detections);
        }

        return tracker;
    }
}
