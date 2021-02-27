package flink.source;

import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.api.java.tuple.Tuple2;

public class TimeAssigner implements SerializableTimestampAssigner<SourceData>{

    @Override
    public long extractTimestamp(SourceData element, long recordTimestamp) {
        return element.getEventTime();
    }
}
