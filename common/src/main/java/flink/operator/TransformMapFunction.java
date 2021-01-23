package flink.operator;

import flink.utils.ByteTransformer;
import flink.types.Information;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.bytedeco.opencv.opencv_core.IplImage;

import static flink.utils.DetectTargetObject.detectTargetObject;

public class TransformMapFunction implements FlatMapFunction<Tuple2<Long, byte[]>, Information> {
    Double speed;

    public static Tuple2<Boolean, Information> detect(IplImage image) {
        if (image == null) {
            System.out.println("Null frame");
            return null;
        }
        return detectTargetObject(image);
    }

    @Override
    public void flatMap(Tuple2<Long, byte[]> in, Collector<Information> collector) {
        byte[] bytes = in.f1;
        Tuple2<Boolean, Information> msg = detect(ByteTransformer.Byte2IplImage(bytes));
        if (msg != null) {
            if (msg.f0) {
                Information info = msg.f1;
                info.setEventTime(in.f0);
                info.showMessage();
                collector.collect(info);
            }
        }
    }
}
