package com.example.onlinelecturefairy.day;

import com.example.onlinelecturefairy.event.Event;

import java.util.ArrayList;
import java.util.Calendar;

public class Day {
    private final Calendar calendar;
    private ArrayList<Event> events;

    public Day(Calendar calendar) {
        this.calendar = calendar;
    }

    public Day(Calendar calendar, ArrayList<Event> events) {
        this.calendar = calendar;
        this.events = events;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}
