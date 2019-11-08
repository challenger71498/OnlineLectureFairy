package com.example.onlinelecturefairy.common;

import android.os.Debug;
import android.util.Log;
import android.widget.TextView;

import com.example.onlinelecturefairy.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class BindingAdapter {
    @androidx.databinding.BindingAdapter({"setDayText"})
    public static void setDayText(TextView view, Calendar calendar) {
        try {
            if (calendar != null) {
                view.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @androidx.databinding.BindingAdapter({"setDayColor"})
    public static void setDayColor(TextView view, Calendar calendar) {
        try {
            if(calendar != null) {
                if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    view.setTextColor(view.getResources().getColor(R.color.colorAccent));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
