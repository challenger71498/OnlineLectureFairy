package com.example.onlinelecturefairy.day;

import com.example.onlinelecturefairy.event.CalendarEvent;
import com.example.onlinelecturefairy.event.Event;

import java.util.ArrayList;
import java.util.Calendar;

public class Day {
    private final Calendar calendar;
    private ArrayList<CalendarEvent> events;
    private boolean isThisMonth = false;

    public Day(Calendar calendar, boolean isThisMonth) {
        this.calendar = calendar;
        this.events = new ArrayList<>();
        this.isThisMonth = isThisMonth;
    }

    public Day(Calendar calendar, ArrayList<CalendarEvent> events, boolean isThisMonth) {
        this.calendar = calendar;
        this.events = events;
        this.isThisMonth = isThisMonth;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public ArrayList<CalendarEvent> getEvents() {
        return events;
    }

    public boolean getIsThisMonth() { return isThisMonth; }

    public void setEvents(ArrayList<CalendarEvent> events) {
        this.events = events;
    }

    public void addEvent(CalendarEvent event) {
        events.add(event);
    }
}
