package flink.utils;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Byte2Frame {
    private static OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
    private static Java2DFrameConverter frameConverter = new Java2DFrameConverter();

    public static Frame byte2Frame(byte[] bytes) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));
            Mat image = converter.convert(frameConverter.convert(bufferedImage));
            return converter.convert(image);
        } catch (IOException e) {
            throw new RuntimeException("bufferedImage failure: "+ e.getMessage(), e);
        }
    }
}
