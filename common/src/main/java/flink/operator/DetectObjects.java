package flink.operator;

import flink.config.CONFIG;
import flink.types.SourceData;
import flink.utils.ByteTransformer;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.util.Collector;
import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;

import static flink.tracker.DetectContours.detectionContours;

public class DetectObjects implements FlatMapFunction<SourceData,
        Tuple3<Mat, Long, Vector<RotatedRect>>> {
    Logger LOG = LoggerFactory.getLogger(DetectObjects.class);
    Vector<RotatedRect> array;

    @Override
    public void flatMap(SourceData in, Collector<Tuple3<Mat, Long, Vector<RotatedRect>>> collector) throws Exception {
        long start = System.currentTimeMillis();

        Mat frame = ByteTransformer.Byte2Mat(in.getData());
        Imgproc.resize(frame, frame, new Size(CONFIG.FRAME_WIDTH, CONFIG.FRAME_HEIGHT),
                0., 0., Imgproc.INTER_LINEAR);

        // ----------------------------------------
        // detect all the existed objects in thia frame
        // ----------------------------------------
        array = detectionContours(frame);

        LOG.info("Network takes: {}ms\t Buffer takes: {}ms\t Detect takes: {}ms",
                start - in.getEventTime(), start - in.getStartTime(),
                System.currentTimeMillis() - start);

        collector.collect(Tuple3.of(frame, in.getEventTime(), array));
    }
}
