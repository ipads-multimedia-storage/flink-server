package flink.source;

import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.api.java.tuple.Tuple2;

public class TimeAssigner implements SerializableTimestampAssigner<Tuple2<Long, byte[]>>{

    @Override
    public long extractTimestamp(Tuple2<Long, byte[]> element, long recordTimestamp) {
        return element.f0;
    }
}
