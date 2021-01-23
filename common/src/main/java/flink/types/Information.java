package flink.types;

import org.apache.flink.api.java.tuple.Tuple2;

public class Information {
    Long objectID;
    Long eventTime;
    Tuple2<Integer, Integer> location;

    public Information(Long objectID, Long eventTime, Integer posX, Integer posY) {
        this.objectID = objectID;
        this.eventTime = eventTime;
        this.location = Tuple2.of(posX, posY);
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

    public Tuple2<Integer, Integer> getLocation() {
        return location;
    }

    public void setLocation(Tuple2<Integer, Integer> location) {
        this.location = location;
    }

    public void showMessage() {
        System.out.println("id:" + objectID + ",  time:" + eventTime + ", (" + location.f0 + ", " + location.f1 + ")");
    }

    public Integer getPosX() {
        return location.f0;
    }

    public Integer getPosY() {
        return location.f1;
    }

    public Long getObjectID() {
        return objectID;
    }
}
