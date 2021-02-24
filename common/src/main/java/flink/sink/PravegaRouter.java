package flink.sink;

import io.pravega.connectors.flink.PravegaEventRouter;
import org.apache.flink.api.java.tuple.Tuple2;

public class PravegaRouter implements PravegaEventRouter<Tuple2<Long, byte[]>> {
    @Override
    public String getRoutingKey(Tuple2<Long, byte[]> event) {
        return "testRoutingKey";
    }
}
