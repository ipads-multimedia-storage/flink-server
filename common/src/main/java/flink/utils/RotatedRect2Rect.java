package flink.utils;

import org.opencv.core.Point;
import org.opencv.core.Rect2d;
import org.opencv.core.RotatedRect;

public class RotatedRect2Rect {
    public static Rect2d rotatedRect2Rect(RotatedRect rotatedRect){
        Point pt[] = new Point[4];
        rotatedRect.points(pt);
        Rect2d r = new Rect2d((int) Math.floor(Math.min(Math.min(Math.min(pt[0].x, pt[1].x), pt[2].x), pt[3].x)),
                (int) Math.floor(Math.min(Math.min(Math.min(pt[0].y, pt[1].y), pt[2].y), pt[3].y)),
                (int) Math.ceil(Math.max(Math.max(Math.max(pt[0].x, pt[1].x), pt[2].x), pt[3].x)),
                (int) Math.ceil(Math.max(Math.max(Math.max(pt[0].y, pt[1].y), pt[2].y), pt[3].y)));
        r.width -= r.x - 1;
        r.height -= r.y - 1;
        return r;
    }
}
