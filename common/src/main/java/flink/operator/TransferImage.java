package flink.operator;

import flink.tracker.MainTrack;
import flink.tracker.Track;
import flink.tracker.Tracker;
import flink.types.Information;
import flink.utils.ByteTransformer;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransferImage implements FlatMapFunction<Tuple2<Long, byte[]>, Information> {
    MainTrack main = new MainTrack();
    Tracker tracker;
    Logger LOG = LoggerFactory.getLogger(TransferImage.class);

    @Override
    public void flatMap(Tuple2<Long, byte[]> in, Collector<Information> collector) throws Exception {
        long start = System.currentTimeMillis();

        // detect and update the tracking information
        tracker = main.detect(ByteTransformer.Byte2Mat(in.f1), in.f0);

        // LOG the latency information
        LOG.info("Network takes:{} ms\tProcess takes: {} ms", start - in.f0, System.currentTimeMillis() - start);

        for (int k = 0; k < tracker.tracks.size(); k++) {
            Track track = tracker.tracks.get(k);

            // generate information of all tracked object
            Information inf = new Information(track.track_id, in.f0);
            inf.setLocation(track.prediction);
            inf.setAngle(track.angle);

            collector.collect(inf);
        }
    }
}
