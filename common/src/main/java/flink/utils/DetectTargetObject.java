package flink.utils;

import flink.types.Information;
import org.apache.flink.api.java.tuple.Tuple2;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.CvScalar;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_imgproc.CvMoments;

import javax.swing.*;
import java.awt.*;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class DetectTargetObject {
    /**
     * Correct the color range- it depends upon the object, camera quality,
     * environment.
     */
    static CvScalar rgba_min = cvScalar(0, 0, 130, 0);
    static CvScalar rgba_max = cvScalar(80, 80, 255, 0);

    static CanvasFrame canvas = new CanvasFrame("Web Cam Live");
    static CanvasFrame path = new CanvasFrame("Detection");
    static JPanel jp = new JPanel();
    static OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

    public DetectTargetObject(){
        path.setContentPane(jp);
    }

    public static Tuple2<Boolean, Information> detectTargetObject(IplImage img) {
        int posX = 0;
        int posY = 0;

        if (img != null) {
            // show image on window
            cvFlip(img, img, 1); // l-r = 90_degrees_steps_anti_clockwise
            canvas.showImage(converter.convert(img));
            IplImage detectThrs = getThresholdImage(img);

            CvMoments moments = new CvMoments();
            cvMoments(detectThrs, moments, 1);
            double mom10 = cvGetSpatialMoment(moments, 1, 0);
            double mom01 = cvGetSpatialMoment(moments, 0, 1);
            double area = cvGetCentralMoment(moments, 0, 0);
            posX = (int) (mom10 / area);
            posY = (int) (mom01 / area);
            // only if its a valid position
            if (posX > 0 && posY > 0) {
//                paint(img, posX, posY);
                return Tuple2.of(true, new Information(1L, 0L, posX, posY));
            }
        }
        return Tuple2.of(false, new Information(0L, 0L, posX, posY));
    }

    private static void paint(IplImage img, int posX, int posY) {
        Graphics g = jp.getGraphics();
        path.setSize(img.width(), img.height());
        if (g != null) {
            // g.clearRect(0, 0, img.width(), img.height());
            g.setColor(Color.RED);
            // g.fillOval(posX, posY, 20, 20);
            g.drawOval(posX, posY, 20, 20);
        }
    }

    private static IplImage getThresholdImage(IplImage orgImg) {
        IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
        cvInRangeS(orgImg, rgba_min, rgba_max, imgThreshold);// red

        cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 15, 0, 0, 0);
//        cvSaveImage(++ii + "threshold.jpg", imgThreshold);
        return imgThreshold;
    }
}
