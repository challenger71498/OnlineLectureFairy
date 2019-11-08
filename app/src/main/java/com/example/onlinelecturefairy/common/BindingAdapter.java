package com.example.onlinelecturefairy.common;

import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class BindingAdapter {
    @androidx.databinding.BindingAdapter({"setDayText"})
    public static void setDayText(TextView view, Calendar calendar) {
        try {
            if (calendar != null) {
                GregorianCalendar gregorianCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                view.setText(calendar.get(Calendar.DAY_OF_MONTH));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
