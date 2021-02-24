package flink.sink;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.java.tuple.Tuple2;

import java.nio.ByteBuffer;

public class PravegaSerialize implements SerializationSchema<Tuple2<Long, byte[]>> {
    @Override
    public byte[] serialize(Tuple2<Long, byte[]> element) {
        Long eventTime = element.f0;
        ByteBuffer byteBuffer = ByteBuffer.allocate(8 + element.f1.length);
        byteBuffer.putLong(eventTime);
        byteBuffer.put(element.f1);
        return byteBuffer.array();
    }
}
