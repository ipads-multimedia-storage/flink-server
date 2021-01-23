package flink.operator;

import flink.types.Information;
import org.apache.flink.api.java.functions.KeySelector;

public class ObjectIdSelector implements KeySelector<Information, Long> {
    @Override
    public Long getKey(Information value) throws Exception {
        return value.getObjectID();
    }
}