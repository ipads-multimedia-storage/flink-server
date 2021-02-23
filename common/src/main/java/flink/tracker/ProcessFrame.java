package flink.tracker;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.videoio.VideoCapture;

public class ProcessFrame {
    // background substractionMOG2
    public static void processFrame(VideoCapture capture, Mat mRgba,
                                    Mat mFGMask, BackgroundSubtractorMOG2 mBGSub) {
        // GREY_FRAME also works and exhibits better performance
        mBGSub.apply(mRgba, mFGMask, CONFIG.learningRate);
        Imgproc.cvtColor(mFGMask, mRgba, Imgproc.COLOR_GRAY2BGRA, 0);
        Mat erode = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                new Size(8, 8));
        Mat dilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                new Size(8, 8));

        Mat openElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                new Size(3, 3), new Point(1, 1));
        Mat closeElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                new Size(7, 7), new Point(3, 3));

        Imgproc.threshold(mFGMask, mFGMask, 127, 255, Imgproc.THRESH_BINARY);
        Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_OPEN, erode);
        Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_OPEN, dilate);
        Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_OPEN, openElem);
        Imgproc.morphologyEx(mFGMask, mFGMask, Imgproc.MORPH_CLOSE, closeElem);
    }
}
