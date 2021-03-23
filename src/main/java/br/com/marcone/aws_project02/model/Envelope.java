package br.com.marcone.aws_project02.model;

import br.com.marcone.aws_project02.enums.EventType;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Envelope {

    @JsonProperty("event")
    private EventType eventType;
    private String data;

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
