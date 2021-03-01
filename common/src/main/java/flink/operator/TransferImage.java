package flink.operator;

import flink.tracker.MainTrack;
import flink.types.SourceData;
import flink.tracker.Track;
import flink.tracker.Tracker;
import flink.types.Information;
import flink.utils.ByteTransformer;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.util.Collector;
import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;

public class TransferImage implements FlatMapFunction<Tuple3<Mat, Long, Vector<RotatedRect>>, Information> {
    MainTrack main = new MainTrack();
    Tracker tracker;
    Logger LOG = LoggerFactory.getLogger(TransferImage.class);

    @Override
    public void flatMap(Tuple3<Mat, Long, Vector<RotatedRect>> in, Collector<Information> collector) throws Exception {
        long start = System.currentTimeMillis();

        // detect and update the tracking information
        tracker = main.detect(in.f0, in.f1, in.f2);

        // LOG the latency information
        LOG.info("Track takes: {}ms", System.currentTimeMillis() - start);

        for (int k = 0; k < tracker.tracks.size(); k++) {
            Track track = tracker.tracks.get(k);

            // generate information of all tracked object
            Information inf = new Information(track.track_id, in.f1);
            inf.setLocation(track.prediction);
            inf.setAngle(track.angle);
            inf.setSkippedFrames(track.skipped_frames);

            collector.collect(inf);
        }
    }
}
