package flink.types;

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

    public String serialize() {
        return String.valueOf(info.eventTime) +
                info.objectID +
                info.location.f0 +
                info.location.f1 +
                speed;
    }
}
