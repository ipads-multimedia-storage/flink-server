package flink.sink;

import flink.types.Output;
import org.apache.flink.api.common.serialization.SerializationSchema;

public class MessageSerialize implements SerializationSchema<Output> {
    @Override
    public byte[] serialize(Output element) {
        String results = element.serialize();
        return String.format("%-16s", results).getBytes();
    }
}
