package flink.sink;

import flink.types.Output;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class MessageSerialize implements SerializationSchema<Output> {
    Logger LOG = LoggerFactory.getLogger(MessageSerialize.class);
    File latencyLog;

    public MessageSerialize() throws IOException {
        latencyLog = new File("latency2.csv");
        if (!latencyLog.exists()) {
            latencyLog.createNewFile();
        }
    }

    @Override
    public byte[] serialize(Output element) {
        JSONObject jsonObject = new JSONObject();
      
        jsonObject.put("object", element.serialize());
        jsonObject.put("speed", element.getSpeed());
        jsonObject.put("sendTime", System.currentTimeMillis());

        // LOG the latency information
        long waitTime = System.currentTimeMillis()-element.getSpawnTime();
        LOG.info("{} ms after process before send", waitTime);
        try {
            FileWriter writer = new FileWriter(latencyLog.getName(), true);
            writer.write(String.format("%d\n", waitTime));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String jsonString = jsonObject.toString();
        return String.format("%-16d%s", jsonString.length(), jsonString).getBytes();
    }
}
