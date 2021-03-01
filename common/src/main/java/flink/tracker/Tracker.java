package flink.tracker;

import org.opencv.core.*;

import java.util.Vector;

/**
 * Tracker.java TODO:
 */

public class Tracker extends JTracker {
    Vector<Integer> assignment = new Vector<>();

    public Tracker(float _dt, float _Accel_noise_mag, double _dist_thres,
                   int _maximum_allowed_skipped_frames, int _max_trace_length) {
        tracks = new Vector<>();
        dt = _dt;
        Accel_noise_mag = _Accel_noise_mag;
        dist_thres = _dist_thres;
        maximum_allowed_skipped_frames = _maximum_allowed_skipped_frames;
        max_trace_length = _max_trace_length;
        track_removed = 0;
    }

    double euclideanDist(Point p, Point q) {
        Point diff = new Point(p.x - q.x, p.y - q.y);
        return Math.sqrt(diff.x * diff.x + diff.y * diff.y);
    }

    public void update(Vector<RotatedRect> rectArray, Vector<Point> detections, Mat imag, Long eventTime) {
        // detection 一定是检测到的
        if (tracks.size() == 0) {
            // 如果没有track任何，注册所有的
            for (Point detection : detections) {
                Track tr = new Track(detection, dt, Accel_noise_mag, eventTime);
                tracks.add(tr);
            }
        }

        // -----------------------------------
        // 利用距离来匹配 当前的 tracks
        // 和 这一帧所有的 detections
        // -----------------------------------

        // -----------------------------------
        // Number of tracks and detections
        // -----------------------------------
        int N = tracks.size();
        int M = detections.size();

        // Cost matrix.
        double[][] Cost = new double[N][M]; // size: N, M
        // Vector<Integer> assignment = new Vector<>(); // assignment according to Hungarian algorithm

        // -----------------------------------
        // Calculate cost matrix (distances)
        // -----------------------------------
        for (int i = 0; i < tracks.size(); i++) {
            for (int j = 0; j < detections.size(); j++) {
                Cost[i][j] = euclideanDist(tracks.get(i).prediction, detections.get(j));
            }
        }

        // -----------------------------------
        // Solving assignment problem (tracks and predictions of Kalman filter)
        // -----------------------------------
        // HungarianAlg APS = new HungarianAlg();
        // APS.Solve(Cost,assignment, HungarianAlg.TMethod.optimal);

        // HungarianAlg2 APS = new HungarianAlg2();
        // APS.Solve(Cost,assignment);

        AssignmentOptimal APS = new AssignmentOptimal();
        APS.Solve(Cost, assignment);
        // -----------------------------------
        // clean assignment from pairs with large distance
        // -----------------------------------
        // Not assigned tracks
        Vector<Integer> not_assigned_tracks = new Vector<>();

        // 对于所有需要 assign 的，计算最小的匹配距离是否大于 threshold
        // 将新出现的记录到 not_assigned_tracks
        for (int i = 0; i < assignment.size(); i++) {
            if (assignment.get(i) != -1) {
                if (Cost[i][assignment.get(i)] > dist_thres) {
                    assignment.set(i, -1);
                    // Mark unassigned tracks, and increment skipped frames counter,
                    // when skipped frames counter will be larger than
                    // threshold, track will be deleted.
                    not_assigned_tracks.add(i);
                }
            } else {
                // If track have no assigned detect, then increment skipped
                // frames counter.
                tracks.get(i).skipped_frames++;
                //not_assigned_tracks.add(i);
            }
        }

        // -----------------------------------
        // If track didn't get detects long time, remove it.
        // -----------------------------------

        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i).skipped_frames > maximum_allowed_skipped_frames) {
                tracks.remove(i);
                assignment.remove(i);
                track_removed++;
                i--;
            }
        }

        // -----------------------------------
        // Search for unassigned detects
        // -----------------------------------
        Vector<Integer> not_assigned_detections = new Vector<>();
        for (int i = 0; i < detections.size(); i++) {
            if (!assignment.contains(i)) {
                not_assigned_detections.add(i);
            }
        }

        // -----------------------------------
        // and start new tracks for them.
        // -----------------------------------
        if (not_assigned_detections.size() > 0) {
            for (int i = 0; i < not_assigned_detections.size(); i++) {
                Track tr = new Track(detections.get(not_assigned_detections.get(i)), dt,
                        Accel_noise_mag, eventTime);
                tracks.add(tr);
            }
        }

        // Update Kalman Filters state
        updateKalman(imag, detections);

        for (int j = 0; j < assignment.size(); j++) {
            if (assignment.get(j) != -1) {
                Point pt = rectArray.get(assignment.get(j)).center;
                if (tracks.get(j).history.size() >= 20) {
                    tracks.get(j).history.remove(0);
                }
                tracks.get(j).history.add(pt);
            }
        }
    }

    public void updateKalman(Mat imag, Vector<Point> detections) {
        // Update Kalman Filters state
        if (detections.size() == 0)
            for (int i = 0; i < assignment.size(); i++)
                assignment.set(i, -1);
        for (int i = 0; i < assignment.size(); i++) {
            // If track updated less than one time, than filter state is not
            // correct.
            tracks.get(i).prediction = tracks.get(i).KF.getPrediction();

            if (assignment.get(i) != -1) // If we have assigned detect, then
            // update using its coordinates,
            {
                tracks.get(i).skipped_frames = 0;
                tracks.get(i).prediction = tracks.get(i).KF.update(
                        detections.get(assignment.get(i)), true);
            } else // if not continue using predictions
            {
                tracks.get(i).skipped_frames ++;
                tracks.get(i).prediction = tracks.get(i).KF.update(new Point(0,
                        0), false);
            }

            tracks.get(i).KF.setLastResult(tracks.get(i).prediction);
        }
    }
}
