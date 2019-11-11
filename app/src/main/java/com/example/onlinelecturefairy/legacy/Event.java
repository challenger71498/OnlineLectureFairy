package com.example.onlinelecturefairy.legacy;

import com.google.api.client.util.DateTime;

import java.util.Date;

public class Event implements IEvent {
    private String summary;
    private String description;
    private String location;
    private int color;

    private Date startDate;
    private Date endDate;
    private DateTime startDateTime;
    private DateTime endDateTime;

    public Event(){
        startDate = null;
        endDate = null;
        startDateTime = null;
        endDateTime = null;
    }

    public Event(Date start, Date end){
        this();
        this.startDate = start;
        this.endDate = end;
    }
    public Event(DateTime start, DateTime end) {
        this();
        this.startDateTime  = start;
        this.endDateTime = end;
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

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public void setColor(int color) {
        this.color = color;
    }


    public Date getStartDate(){return startDate;}
    public Date getEndDate(){return endDate;}

    public DateTime getStartDateTime(){return startDateTime;}
    public DateTime getEndDateTime(){return endDateTime;}

    public void setStartDate(Date start){this.startDate = start;}
    public void setEndDate(Date end){this.endDate = end;}

    public void setStartDateTime(DateTime start){this.startDateTime = start;}
    public void setEndDateTime(DateTime end){this.endDateTime = end;}
}


