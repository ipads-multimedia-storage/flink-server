package flink.operator;

import flink.utils.Byte2Frame;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.bytedeco.javacv.*;

public class ShowImage implements FlatMapFunction<byte[], byte[]> {
    private static CanvasFrame canvas;

    public ShowImage() {
        canvas = new CanvasFrame("Canvas0");
    }

    public static boolean showImageOnCanvas(Frame frame) {
        if(frame == null) {
            System.out.println("Null frame");
            return false;
        }
        canvas.showImage(frame);
        return true;
    }

    @Override
    public void flatMap(byte[] bytes, Collector<byte[]> collector) {
        if(showImageOnCanvas(Byte2Frame.byte2Frame(bytes))) {
            collector.collect(bytes);
        }
    }
}
