package flink.types;

import org.json.JSONObject;

public class Output {
    Information info;
    Double speed;

    public Output() {
    }

    public Information getInfo() {
        return info;
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
        jsonObject.put("x", info.getPosX());
        jsonObject.put("y", info.getPosY());
        jsonObject.put("angle", info.getAngle());
        return jsonObject;
    }
}
