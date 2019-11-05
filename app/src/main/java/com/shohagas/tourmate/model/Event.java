package com.shohagas.tourmate.model;

import java.io.Serializable;

public class Event implements Serializable {
    private String eventId;
    private String eventName;
    private String eventStartLocation;
    private String eventDestinationLocation;
    private Double eventBudget;
    private String eventDepartureDate;
    private String eventCreatedDate;

    public Event() {
    }

    public Event(String eventId, String eventName, String eventStartLocation, String eventDestinationLocation, Double eventBudget, String eventDepartureDate, String eventCreatedDate) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventStartLocation = eventStartLocation;
        this.eventDestinationLocation = eventDestinationLocation;
        this.eventBudget = eventBudget;
        this.eventDepartureDate = eventDepartureDate;
        this.eventCreatedDate = eventCreatedDate;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventStartLocation() {
        return eventStartLocation;
    }

    public void setEventStartLocation(String eventStartLocation) {
        this.eventStartLocation = eventStartLocation;
    }

    public String getEventDestinationLocation() {
        return eventDestinationLocation;
    }

    public void setEventDestinationLocation(String eventDestinationLocation) {
        this.eventDestinationLocation = eventDestinationLocation;
    }

    public Double getEventBudget() {
        return eventBudget;
    }

    public void setEventBudget(Double eventBudget) {
        this.eventBudget = eventBudget;
    }

    public String getEventDepartureDate() {
        return eventDepartureDate;
    }

    public void setEventDepartureDate(String eventDepartureDate) {
        this.eventDepartureDate = eventDepartureDate;
    }

    public String getEventCreatedDate() {
        return eventCreatedDate;
    }

    public void setEventCreatedDate(String eventCreatedDate) {
        this.eventCreatedDate = eventCreatedDate;
    }
}
