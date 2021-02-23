package flink.types;

import org.json.JSONObject;

public class Output {
    public Information info;
    Double speed;

    public Output() {
    }

    public void setInfo(Information info) {
        this.info = info;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public JSONObject serialize() {
        if (info == null){
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", info.getObjectID());
        jsonObject.put("time", info.getEventTime());
        jsonObject.put("speed", getSpeed());
        jsonObject.put("x", info.getPosX());
        jsonObject.put("y", info.getPosY());
        jsonObject.put("angle", info.getAngle());
        return jsonObject;
    }
}
