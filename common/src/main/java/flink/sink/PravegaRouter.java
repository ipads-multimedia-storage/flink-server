package flink.sink;

import flink.types.SourceData;
import io.pravega.connectors.flink.PravegaEventRouter;

public class PravegaRouter implements PravegaEventRouter<SourceData> {
    @Override
    public String getRoutingKey(SourceData event) {
        return "testRoutingKey";
    }
}
