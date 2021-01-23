package flink.sink;

import org.apache.flink.api.common.serialization.SerializationSchema;

public class MessageSerialize implements SerializationSchema<String> {
    @Override
    public byte[] serialize(String element) {
        return String.format("%-16s", element).getBytes();
    }
}
