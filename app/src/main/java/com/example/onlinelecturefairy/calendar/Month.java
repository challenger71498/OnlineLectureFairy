package com.example.onlinelecturefairy.calendar;

import java.util.ArrayList;

public class Month {
    private ArrayList<Day> dates;

    public Month() {
        dates = new ArrayList<>();
    }

    public ArrayList<Day> getDates() {
        return dates;
    }

    public void addDates(Day day) {
        dates.add(day);
    }
}
