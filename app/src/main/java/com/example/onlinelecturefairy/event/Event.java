package com.example.onlinelecturefairy.event;

import com.google.api.client.util.DateTime;

import java.util.Date;

public class Event {
    private String summary;
    private String description;
    private String location;

    private Timeset startTimeSet;

    private Timeset endTimeSet;

    public Event(Date start, Date end) {
        startTimeSet = new Timeset(start);
        endTimeSet = new Timeset(end);
    }

    public Event(DateTime start, DateTime end) {
        startTimeSet = new Timeset(start);
        endTimeSet = new Timeset(end);
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Timeset getStartTimeSet() {
        return startTimeSet;
    }

    public void setStartTimeSet(Timeset startTimeSet) {
        this.startTimeSet = startTimeSet;
    }

    public Timeset getEndTimeSet() {
        return endTimeSet;
    }

    public void setEndTimeSet(Timeset endTimeSet) {
        this.endTimeSet = endTimeSet;
    }
}


class Timeset {
    private Date date;
    private DateTime dateTime;

    Timeset(Date date) {
        this.date = date;
    }

    Timeset(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }
}