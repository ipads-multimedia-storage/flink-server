package flink.source;

import org.apache.flink.api.common.functions.ReduceFunction;

public class BandwidthReduce implements ReduceFunction<SourceData> {
    @Override
    public SourceData reduce(SourceData value1, SourceData value2) {
        return value2;
    }
}
