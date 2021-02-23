package flink.tracker;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.Vector;

/**
 * JTracker.java TODO:
 */

public abstract class JTracker {

    public float dt;

    public float Accel_noise_mag;

    public double dist_thres;

    public int maximum_allowed_skipped_frames;

    public int max_trace_length;

    public Vector<Track> tracks;

    public int track_removed;

    public abstract void update(Vector<Rect> RectArrays, Vector<Point> detections, Mat imag, Long ID);

}
