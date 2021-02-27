package flink.sink;

import flink.source.SourceData;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.json.JSONObject;

public class BandwidthSerialize implements SerializationSchema<SourceData> {
    @Override
    public byte[] serialize(SourceData element) {
        long currentTime = System.currentTimeMillis();
        long sendTime = element.getSendTime();
        long processTime = currentTime - element.getStartTime();
        int dataLength = element.getData().length;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("function", "Bandwidth");
        jsonObject.put("processTime", processTime);
        jsonObject.put("dataLength", dataLength);
        jsonObject.put("sendTime", sendTime);

        String jsonString = jsonObject.toString();
        return String.format("%-16d%s", jsonString.length(), jsonString).getBytes();
    }
}
