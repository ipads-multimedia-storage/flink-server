package flink.tracker;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class DetectContours {

    public static Vector<Rect> detectionContours(Mat outmat) {
        Mat v = new Mat();
        Mat vv = outmat.clone();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(vv, contours, v, Imgproc.RETR_LIST,
                Imgproc.CHAIN_APPROX_SIMPLE);

        int maxAreaIdx = -1;
        Rect r = null;
        Vector<Rect> rect_array = new Vector<Rect>();

        for (int idx = 0; idx < contours.size(); idx++) {
            Mat contour = contours.get(idx);
            double contourarea = Imgproc.contourArea(contour);
            if (contourarea > CONFIG.MIN_BLOB_AREA &&
                    contourarea < CONFIG.MAX_BLOB_AREA) {
                maxAreaIdx = idx;

                /* TODO: 加上角度的检测
                 * @author: promise
                 */
//                MatOfPoint2f areaPoints = new MatOfPoint2f(contours.get(maxAreaIdx).toArray());
//                RotatedRect boundingRect = Imgproc.minAreaRect(areaPoints);

                r = Imgproc.boundingRect(contours.get(maxAreaIdx));
                rect_array.add(r);
            }

        }

        v.release();
        return rect_array;
    }
}
