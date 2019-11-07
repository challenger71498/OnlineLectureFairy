package com.example.onlinelecturefairy.event;

import com.example.onlinelecturefairy.R;
import com.google.api.client.util.DateTime;
import com.google.common.collect.HashBiMap;

import java.util.Date;

public class CalendarEvent extends Event {
    public static HashBiMap<String, Integer> colorMap;
    static {
        colorMap = HashBiMap.create();
        colorMap.put("1", R.color.lavender);
        colorMap.put("2", R.color.sage);
        colorMap.put("3", R.color.grape);
        colorMap.put("4", R.color.flamingo);
        colorMap.put("5", R.color.banana);
        colorMap.put("6", R.color.mandarin);
        colorMap.put("7", R.color.peacock);
        colorMap.put("8", R.color.graphite);
        colorMap.put("9", R.color.blueberry);
        colorMap.put("10", R.color.basil);
        colorMap.put("11", R.color.tomato);
    }

    private com.google.api.services.calendar.model.Event event;

    public static int switchIdToColor(String id) {
        if (colorMap.containsKey(id)) {
            return colorMap.get(id);
        }
        else throw new NullPointerException();
    }

    public static String switchColorToId(int color) {
        if(colorMap.containsValue(color)) {
            return colorMap.inverse().get(color);
        }
        else throw new NullPointerException();
    }

    @Override
    public void setColor(int id) {
        super.setColor(id);
        event.setColorId(switchColorToId(id));
    }

    public CalendarEvent(Date start, Date end, com.google.api.services.calendar.model.Event event) {
        super(start, end);
        this.event = event;
        setColor(switchIdToColor(event.getColorId()));
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
