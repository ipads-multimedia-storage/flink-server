package flink.utils;

import org.apache.flink.api.java.tuple.Tuple2;

public class Message {
    Boolean detected;
    Long objectID;
    Tuple2<Integer, Integer> location;

    public Message(Boolean detected, Long objectID, Integer posX, Integer posY) {
        this.detected = detected;
        this.objectID = objectID;
        this.location = Tuple2.of(posX, posY);
    }

    public Boolean getDetected() {
        return detected;
    }

    public void showMessage() {
        System.out.println("x: " + location.f0 + ", y: " + location.f1);
    }

    public Integer getPosX() {
        return location.f0;
    }

    public Integer getPosY() {
        return location.f1;
    }
}
