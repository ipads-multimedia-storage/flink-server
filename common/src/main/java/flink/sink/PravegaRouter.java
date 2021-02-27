package flink.sink;

import flink.source.SourceData;
import io.pravega.connectors.flink.PravegaEventRouter;
import org.apache.flink.api.java.tuple.Tuple2;

public class PravegaRouter implements PravegaEventRouter<SourceData> {
    @Override
    public String getRoutingKey(SourceData event) {
        return "testRoutingKey";
    }
}
