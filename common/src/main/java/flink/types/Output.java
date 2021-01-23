package flink.types;

import org.apache.flink.api.java.tuple.Tuple2;

import java.util.Date;

public class Output {
    Long objectID;
    Double speed;
    Date eventTime;
    Tuple2<Integer, Integer> location;

    public Output(){}

    public Output(Long objectID, Double speed, Date eventTime, Tuple2<Integer, Integer> location) {
        this.objectID = objectID;
        this.speed = speed;
        this.eventTime = eventTime;
        this.location = location;
    }
    
    public String serialize(){
        return String.valueOf(eventTime) +
                location.f0 +
                location.f1;
    }
    
    public Long getObjectID() {
        return objectID;
    }

    public void setObjectID(Long objectID) {
        this.objectID = objectID;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public Tuple2<Integer, Integer> getLocation() {
        return location;
    }

    public void setLocation(Tuple2<Integer, Integer> location) {
        this.location = location;
    }
}
