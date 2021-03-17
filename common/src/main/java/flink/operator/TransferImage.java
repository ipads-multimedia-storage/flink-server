package flink.operator;

import flink.tracker.MainTrack;
import flink.source.SourceData;
import flink.tracker.Track;
import flink.tracker.Tracker;
import flink.types.Information;
import flink.utils.ByteTransformer;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class TransferImage implements FlatMapFunction<SourceData, Information> {
    MainTrack main = new MainTrack();
    Tracker tracker;
    Logger LOG = LoggerFactory.getLogger(TransferImage.class);
    File latencyLog;

    public TransferImage() throws IOException {
        latencyLog = new File("latency1.csv");
        if (!latencyLog.exists()) {
            latencyLog.createNewFile();
        }
    }

    @Override
    public void flatMap(SourceData in, Collector<Information> collector) throws Exception {
        long start = System.currentTimeMillis();

        // detect and update the tracking information
        tracker = main.detect(ByteTransformer.Byte2Mat(in.getData()), in.getEventTime());

        // LOG the latency information
        long procTime = System.currentTimeMillis() - start, waitTime = start - in.getStartTime();
        LOG.info("Network takes:{} ms\tProcess takes: {} ms\tWait {} ms after received",
                start - in.getEventTime(), procTime,
                start - in.getStartTime());
        FileWriter writer = new FileWriter(latencyLog.getName(), true);
        writer.write(String.format("%d,%d\n", procTime, waitTime));
        writer.close();

        for (int k = 0; k < tracker.tracks.size(); k++) {
            Track track = tracker.tracks.get(k);

            // generate information of all tracked object
            Information inf = new Information(track.track_id, in.getEventTime());
            inf.setLocation(track.prediction);
            inf.setAngle(track.angle);
            inf.setSpawnTime(System.currentTimeMillis());

            collector.collect(inf);
        }
    }
}
