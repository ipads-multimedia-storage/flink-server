package flink.utils;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_core.Mat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ByteTransformer {
    private static OpenCVFrameConverter.ToMat matConverter = new OpenCVFrameConverter.ToMat();
    private static Java2DFrameConverter frameConverter = new Java2DFrameConverter();
    private static OpenCVFrameConverter.ToIplImage IplConverter = new OpenCVFrameConverter.ToIplImage();

    public static IplImage Byte2IplImage(byte[] bytes) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));
            return IplConverter.convert(frameConverter.convert(bufferedImage));
        } catch (IOException e) {
            throw new RuntimeException("bufferedImage failure: "+ e.getMessage(), e);
        }
    }

    public static Frame byte2Frame(byte[] bytes) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));
            Mat image = matConverter.convert(frameConverter.convert(bufferedImage));
            return matConverter.convert(image);
        } catch (IOException e) {
            throw new RuntimeException("bufferedImage failure: "+ e.getMessage(), e);
        }
    }
}
