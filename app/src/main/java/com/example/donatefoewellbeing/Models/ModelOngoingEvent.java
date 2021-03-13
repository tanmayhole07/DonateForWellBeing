package com.example.donatefoewellbeing.Models;

public class ModelOngoingEvent {

    private String eventName, eventDescription, eventOrganizationName, date, time, eventLocation, timeStamp, eventImage;

    public ModelOngoingEvent(String eventName, String eventDescription, String eventOrganizationName, String date, String time, String eventLocation, String timeStamp, String eventImage) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventOrganizationName = eventOrganizationName;
        this.date = date;
        this.time = time;
        this.eventLocation = eventLocation;
        this.timeStamp = timeStamp;
        this.eventImage = eventImage;
    }

    public ModelOngoingEvent() {
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventOrganizationName() {
        return eventOrganizationName;
    }

    public void setEventOrganizationName(String eventOrganizationName) {
        this.eventOrganizationName = eventOrganizationName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }
}
