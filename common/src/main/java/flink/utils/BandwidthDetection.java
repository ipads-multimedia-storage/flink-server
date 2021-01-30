package flink.utils;

public class BandwidthDetection {
    // average bandwidth (unit: KB/s, max: 1GB/s)
    private static double avg = 1000000;
    private static int count = 0;
    private static boolean coldStart = true;

    public static void record(long gap, int length) {
        if(coldStart) {
            if(count < 1000) count++;
            else {
                count = 0;
                coldStart = false;
            }
            return;
        }
        if(gap <= 0) avg = avg * 2;
        else avg = 0.8 * avg + (double)(length/gap) * 0.2;
        if(avg > 1000000) avg = 1000000;
    }

    public static double getAvg() {
        return avg;
    }

}
