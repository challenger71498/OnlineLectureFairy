package com.example.onlinelecturefairy.event;

import com.google.api.client.util.DateTime;

import java.util.Date;

public class CalendarEvent extends Event {
    private com.google.api.services.calendar.model.Event event;

    public CalendarEvent(Date start, Date end, com.google.api.services.calendar.model.Event event) {
        super(start, end);
        this.event = event;
    }

    public CalendarEvent(DateTime start, DateTime end, com.google.api.services.calendar.model.Event event) {
        super(start, end);
        this.event = event;
    }

    public com.google.api.services.calendar.model.Event getEvent() {
        return event;
    }

    public void setEvent(com.google.api.services.calendar.model.Event event) {
        this.event = event;
    }
}
