package flink.opencv;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.highgui.HighGui.*;

public class DetectTargetObject implements Serializable {
    /**
     * Correct the color range- it depends upon the object, camera quality,
     * environment.
     */
    static Scalar rgba_min = new Scalar(0, 151, 100);
    static Scalar rgba_max = new Scalar(255, 255, 255);

    public DetectTargetObject(){
        namedWindow("window");
    }

    public List<RotatedRect> detectTargetObject(Mat img) throws Exception{
        List<MatOfPoint> contours = new ArrayList<>();
        List<RotatedRect> boundingRects = new ArrayList<>();

        if (img != null) {
            // show image on window
            Mat desImg = new Mat();
            //convert frame from BGR to HSV colorspace
            Imgproc.cvtColor(img, desImg, Imgproc.COLOR_BGR2HSV);
            Core.inRange(desImg, rgba_min, rgba_max, desImg);
            Imgproc.findContours(desImg, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

            for (MatOfPoint contour : contours) {
                MatOfPoint2f areaPoints = new MatOfPoint2f(contour.toArray());
                RotatedRect boundingRect = Imgproc.minAreaRect(areaPoints);

                // test min ROI area in pixels
                if (boundingRect.size.area() > 5000) {
                    boundingRects.add(boundingRect);
                }
            }
            drawContours(boundingRects, img);
        }
        return boundingRects;
    }

    private void drawContours(List<RotatedRect> boundingRects, Mat img){
        for (RotatedRect boundingRect: boundingRects){
            Point[] vertices = new Point[4];
            boundingRect.points(vertices);
            List<MatOfPoint> boxContours = new ArrayList<>();
            boxContours.add(new MatOfPoint(vertices));
            Imgproc.drawContours(img, boxContours, 0, new Scalar(255, 0, 0), 2);
        }
        imshow("window", img);
        waitKey(5);
    }
}
