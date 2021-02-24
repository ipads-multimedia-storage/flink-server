package flink.sink;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.java.tuple.Tuple2;

public class PravegaSerialize implements SerializationSchema<Tuple2<Long, byte[]>> {
    @Override
    public byte[] serialize(Tuple2<Long, byte[]> element) {
        return element.f1;
    }
}
