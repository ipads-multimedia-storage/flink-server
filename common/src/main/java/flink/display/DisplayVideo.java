package flink.display;

import flink.config.CONFIG;
import flink.tracker.Track;
import flink.tracker.Tracker;
import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

import static flink.operator.TransferImage.*;
import static flink.utils.ByteTransformer.Mat2bufferedImage;

public class DisplayVideo implements Runnable {
    static JLabel vidpanel;
    Tracker tracker;
    Vector<RotatedRect> array;
    Mat imag;

    @Override
    public void run() {
        JFrame jFrame = new JFrame("MULTIPLE-TARGET TRACKING");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        vidpanel = new JLabel();
        jFrame.setContentPane(vidpanel);
        jFrame.setSize(CONFIG.FRAME_WIDTH, CONFIG.FRAME_HEIGHT);
        jFrame.setLocation((3 / 4) * Toolkit.getDefaultToolkit().getScreenSize().width,
                (3 / 4) * Toolkit.getDefaultToolkit().getScreenSize().height);
        jFrame.setVisible(true);

        tracker = getCurrentTracker();
        array = getCurrentArray();
        imag = getCurrentImage();

        while(true){
            tracker = getCurrentTracker();
            array = getCurrentArray();
            imag = getCurrentImage();

            for (RotatedRect obj : array) {
                Imgproc.rectangle(imag, obj.boundingRect(),
                        new Scalar(0, 255, 0), 2);
                Imgproc.circle(imag, obj.center, 1,
                        new Scalar(0, 0, 255), 2);
            }

            for (int k = 0; k < tracker.tracks.size(); k++) {
                Track track = tracker.tracks.get(k);
                Imgproc.putText(imag, track.track_id + "", track.prediction,
                        2, 1, new Scalar(255, 255, 255), 1);
            }

            ImageIcon image = new ImageIcon(Mat2bufferedImage(imag));
            vidpanel.setIcon(image);
            vidpanel.repaint();
        }
    }
}
