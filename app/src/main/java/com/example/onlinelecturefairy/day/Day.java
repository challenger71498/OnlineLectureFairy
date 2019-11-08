package com.example.onlinelecturefairy.day;

import com.example.onlinelecturefairy.event.CalendarEvent;
import com.example.onlinelecturefairy.event.Event;

import java.util.ArrayList;
import java.util.Calendar;

public class Day {
    private final Calendar calendar;
    private ArrayList<CalendarEvent> events;

    public Day(Calendar calendar) {
        this.calendar = calendar;
    }

    public Day(Calendar calendar, ArrayList<CalendarEvent> events) {
        this.calendar = calendar;
        this.events = events;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public ArrayList<CalendarEvent> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<CalendarEvent> events) {
        this.events = events;
    }
}
