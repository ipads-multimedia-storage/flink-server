package flink.opencv;

import org.opencv.core.Mat;
import org.opencv.core.Rect2d;
import org.opencv.tracking.*;

public class KCFTracker {
    TrackerKCF tracker;
    Rect2d boundingRect;

    public KCFTracker(Mat image, Rect2d roi){
        this.tracker = TrackerKCF.create();
        this.boundingRect = roi;
        tracker.init(image, roi);
    }

    public Rect2d update(Mat image){
        tracker.update(image, this.boundingRect);
        return this.boundingRect;
    }
}
