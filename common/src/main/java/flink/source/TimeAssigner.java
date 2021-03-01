package flink.source;

import flink.types.SourceData;
import org.apache.flink.api.common.eventtime.*;

public class TimeAssigner implements SerializableTimestampAssigner<SourceData>{

    @Override
    public long extractTimestamp(SourceData element, long recordTimestamp) {
        return element.getEventTime();
    }
}
