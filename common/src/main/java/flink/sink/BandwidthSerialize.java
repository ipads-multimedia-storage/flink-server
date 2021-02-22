package flink.sink;

import flink.utils.BandwidthDetection;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.json.JSONObject;

public class BandwidthSerialize implements SerializationSchema<Long> {
    @Override
    public byte[] serialize(Long element) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("function", "Bandwidth");
        double avgBand = BandwidthDetection.getAvg();
        if(avgBand > 0) {
            jsonObject.put("bandwidth", avgBand);
        }
        jsonObject.put("count", element);
        jsonObject.put("sendTime", System.currentTimeMillis());

        String jsonString = jsonObject.toString();
        return String.format("%-16d%s", jsonString.length(), jsonString).getBytes();
    }
}
