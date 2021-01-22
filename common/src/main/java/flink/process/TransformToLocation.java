package flink.process;

import flink.utils.ByteTransformer;
import flink.utils.Message;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.bytedeco.opencv.opencv_core.IplImage;

import static flink.detection.DetectTargetObject.detectTargetObject;

public class TransformToLocation implements FlatMapFunction<byte[], String> {
    public static Message detect(IplImage image) {
        if (image == null) {
            System.out.println("Null frame");
            return null;
        }
        return detectTargetObject(image);
    }

    @Override
    public void flatMap(byte[] bytes, Collector<String> collector) {
        Message msg = detect(ByteTransformer.Byte2IplImage(bytes));
        if (msg != null) {
            if (msg.getDetected()) {
                msg.showMessage();
                collector.collect("x: " + msg.getPosX() + ", y: " + msg.getPosY());
            }
        }
    }
}
