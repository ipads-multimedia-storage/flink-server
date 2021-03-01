package flink.sink;

import flink.types.SourceData;
import org.apache.flink.api.common.serialization.SerializationSchema;

public class PravegaSerialize implements SerializationSchema<SourceData> {
    @Override
    public byte[] serialize(SourceData element) {
        return element.getData();
    }
}
