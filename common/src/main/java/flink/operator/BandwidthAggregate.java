package flink.operator;

import flink.types.SourceData;
import org.apache.flink.api.common.functions.AggregateFunction;

public class BandwidthAggregate implements AggregateFunction<SourceData, Long, Long> {
    @Override
    public Long createAccumulator() {
        return 0L;
    }

    @Override
    public Long add(SourceData value, Long accumulator) {
        return accumulator;
    }

    @Override
    public Long getResult(Long accumulator) {
        return accumulator;
    }

    @Override
    public Long merge(Long a, Long b) {
        return a + b;
    }
}
