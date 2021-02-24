package image;

import flink.opencv.DetectTargetObject;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.opencv.core.Mat;

import static org.opencv.imgcodecs.Imgcodecs.imread;

public class TestObjectTracking {
    private static Java2DFrameConverter frameConverter = new Java2DFrameConverter();
    private static OpenCVFrameConverter.ToIplImage IplConverter = new OpenCVFrameConverter.ToIplImage();
    private static String path = "D:\\Coding\\Git\\ipads-multimedia-storage\\flink-server\\common\\src\\test\\java\\data\\2.jpg";
    static {
        System.load("C:\\Windows\\System32\\opencv.dll");
    };

    public static void main(String[] args) throws Exception {
        Mat img = imread(path);
        DetectTargetObject obj = new DetectTargetObject();
        int count = 10000;
        long start = System.currentTimeMillis();
        while(count > 0){
            count -= 1;
            obj.detectTargetObject(img);
        }
        long end = System.currentTimeMillis();
        System.out.println((end - start)/10000.0);
    }
}
