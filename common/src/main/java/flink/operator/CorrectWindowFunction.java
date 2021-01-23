package flink.operator;

import flink.types.Information;
import flink.types.Output;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

public class CorrectWindowFunction implements WindowFunction<Information, Output, Long, GlobalWindow> {
    @Override
    public void apply(Long key, GlobalWindow window, Iterable<Information> values, Collector<Output> out) {
        Integer sumX = 0, sumY = 0;
        Integer count = 0;
        for (Information value : values) {
            sumX += value.getPosX();
            sumY += value.getPosY();
            count ++;
        }

        Output result = new Output();
        result.setLocation(Tuple2.of(sumX/count, sumY/count));
        result.setObjectID(values.iterator().next().getObjectID());
        out.collect(result);
    }
}