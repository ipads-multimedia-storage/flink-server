package flink.operator;

import flink.opencv.CentroidTracker;
import flink.opencv.DetectTargetObject;
import flink.utils.ByteTransformer;
import flink.types.Information;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransferImage implements FlatMapFunction<Tuple2<Long, byte[]>, Information> {
    DetectTargetObject obj = new DetectTargetObject();
    CentroidTracker tracker = new CentroidTracker(20, 100);

    public HashMap<Long, RotatedRect> detect(Mat image, Long eventTime) throws Exception {
        if (image == null) {
            System.out.println("Null frame");
            return null;
        }
        List<RotatedRect> rects = obj.detectTargetObject(image);
        System.out.println("rects:" + rects.size());
        return tracker.update(rects, eventTime);
    }

    @Override
    public void flatMap(Tuple2<Long, byte[]> in, Collector<Information> collector) throws Exception {
        byte[] bytes = in.f1;
        Long eventTime = in.f0;
        HashMap<Long, RotatedRect> list = detect(ByteTransformer.Byte2Mat(bytes), eventTime);
        for (Map.Entry<Long, RotatedRect> item : list.entrySet()){
            Point center = item.getValue().center;
            Double angle = item.getValue().angle;
            Information output = new Information(item.getKey(), eventTime);
            output.setLocation(center);
            output.setAngle(angle);
            output.showMessage();
            collector.collect(output);
        }
    }
}
