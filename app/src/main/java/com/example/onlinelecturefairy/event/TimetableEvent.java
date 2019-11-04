package com.example.onlinelecturefairy.event;

import com.google.api.client.util.DateTime;

import java.util.Date;

public class TimetableEvent extends Event {
    public TimetableEvent(Date start, Date end) {
        super(start, end);
    }

    public TimetableEvent(DateTime start, DateTime end) {
        super(start, end);
    }
}
