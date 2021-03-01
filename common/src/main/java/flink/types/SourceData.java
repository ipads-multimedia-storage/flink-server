package flink.types;

public class SourceData {
    private Long eventTime;
    private Long startTime;
    private Long sendTime;
    private byte[] data;

    public SourceData(Long eventTime, Long startTime, Long sendTime, byte[] data) {
        this.eventTime = eventTime;
        this.startTime = startTime;
        this.sendTime = sendTime;
        this.data = data;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }
}
