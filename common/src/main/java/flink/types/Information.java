package flink.types;

import org.apache.flink.api.java.tuple.Tuple2;
import org.opencv.core.Point;

public class Information {
    Long objectID;
    Long eventTime;
    Long spawnTime; // for measuring latency

    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }

    Double angle;
    Tuple2<Double, Double> location;

    public Information(Long objectID, Long eventTime, Double posX, Double posY, Double angle) {
        this.objectID = objectID;
        this.eventTime = eventTime;
        this.location = Tuple2.of(posX, posY);
        this.angle = angle;
    }

    public Information(Long objectID, Long eventTime) {
        this.objectID = objectID;
        this.eventTime = eventTime;
    }

    public Information() {

    }

    public void setObjectID(Long objectID) {
        this.objectID = objectID;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public Tuple2<Double, Double> getLocation() {
        return location;
    }

    public void setLocation(Tuple2<Double, Double> location) {
        this.location = location;
    }

    public void setLocation(Point center) {
        this.location = Tuple2.of(center.x, center.y);
    }

    public void showMessage() {
//        System.out.println("id:" + objectID + ",  time:" + eventTime + ", angle:" + angle + ", location: (" + location.f0 + ", " + location.f1 + ")");
    }

    public Double getPosX() {
        return location.f0;
    }

    public Double getPosY() {
        return location.f1;
    }

    public Long getObjectID() {
        return objectID;
    }

    public Long getSpawnTime() {
        return spawnTime;
    }

    public void setSpawnTime(Long spawnTime) {
        this.spawnTime = spawnTime;
    }
}
