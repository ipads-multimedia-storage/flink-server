package flink.utils;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ByteTransformer {
    private static OpenCVFrameConverter.ToMat matConverter = new OpenCVFrameConverter.ToMat();
    private static Java2DFrameConverter frameConverter = new Java2DFrameConverter();

    public static Mat Byte2Mat(byte[] bytes) {
        return Imgcodecs.imdecode(new MatOfByte(bytes), -1);
    }

    public static Frame byte2Frame(byte[] bytes) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));
            org.bytedeco.opencv.opencv_core.Mat image = matConverter.convert(frameConverter.convert(bufferedImage));
            return matConverter.convert(image);
        } catch (IOException e) {
            throw new RuntimeException("bufferedImage failure: "+ e.getMessage(), e);
        }
    }
}
