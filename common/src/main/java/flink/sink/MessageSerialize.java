package flink.sink;

import flink.types.Output;
import flink.utils.BandwidthDetection;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageSerialize implements SerializationSchema<Output> {
    @Override
    public byte[] serialize(Output element) {
        JSONObject jsonObject = new JSONObject();
        double avgBand = BandwidthDetection.getAvg();
        if(avgBand > 0) {
            jsonObject.put("bandwidth", avgBand);
        }
        jsonObject.put("speed", element.getSpeed());
        jsonObject.put("sendTime", System.currentTimeMillis());

        List<JSONObject> sList = new ArrayList<>();
        if (element.serialize() != null){
            sList.add(element.serialize());
        }
        jsonObject.put("objects", sList);

        String jsonString = jsonObject.toString();
        return String.format("%-16d%s", jsonString.length(), jsonString).getBytes();
    }
}
