package flink.sink;

import flink.source.SourceData;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.java.tuple.Tuple2;

public class PravegaSerialize implements SerializationSchema<SourceData> {
    @Override
    public byte[] serialize(SourceData element) {
        return element.getData();
    }
}
