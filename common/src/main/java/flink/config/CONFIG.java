package flink.config;

import org.opencv.core.Scalar;

import java.awt.*;

/**
 * CONFIG.java
 * TODO:
 */

public class CONFIG {

    public static int FRAME_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width / 2;
    public static int FRAME_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height / 2;

    public static double MIN_BLOB_AREA = 5000;
    public static double MAX_BLOB_AREA = 100000;

    public static Scalar[] Colors = {new Scalar(255, 0, 0), new Scalar(0, 255, 0),
            new Scalar(0, 0, 255), new Scalar(255, 255, 0),
            new Scalar(0, 255, 255), new Scalar(255, 0, 255),
            new Scalar(255, 127, 255), new Scalar(127, 0, 255),
            new Scalar(127, 0, 127)};

    public static double learningRate = 0.005;

    public static double _dt = 0.2;
    public static double _Accel_noise_mag = 0.5;
    public static double _dist_thres = 200;
    public static int _maximum_allowed_skipped_frames = 50;
    public static int _max_trace_length = 5;

    public static int _skip_frames = 10;
    public static int _output_interval = 200;
    public static boolean _draw_image_flag = true;
    public static double _speed_change_thres = .1;
}

