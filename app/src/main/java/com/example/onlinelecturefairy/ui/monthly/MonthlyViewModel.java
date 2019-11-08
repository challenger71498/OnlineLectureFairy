package com.example.onlinelecturefairy.ui.monthly;

import android.util.Log;

import com.example.onlinelecturefairy.event.CalendarEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static android.content.ContentValues.TAG;

public class MonthlyViewModel extends ViewModel {
    private MutableLiveData<TreeMap<Calendar, ArrayList<CalendarEvent>>> mCalendar;

    public void initCalendar() {
        GregorianCalendar cal = new GregorianCalendar();
        setCalendarDate(cal);
    }

    public void setCalendar(GregorianCalendar calendar) {
        if(mCalendar == null) {
            mCalendar = new MutableLiveData<>();
            setCalendarDate(calendar);
        }
    }

    public LiveData<TreeMap<Calendar, ArrayList<CalendarEvent>>> getCalendar() {
        if(mCalendar == null) {
            mCalendar = new MutableLiveData<>();
            setCalendarDate(new GregorianCalendar());    //Set it to today.
        }
        return mCalendar;
    }

    public void setCalendarDate(GregorianCalendar cal) {
        try {
            mCalendar = new MutableLiveData<>();
            TreeMap<Calendar, ArrayList<CalendarEvent>> tempCalendar = new TreeMap<>();  //초기화.

            GregorianCalendar calendar = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; //해당 월에 시작하는 요일 -1
            int weekCount = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);  //해당 월의 최대 주
            int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); //말일

            GregorianCalendar prevCalendar = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) - 1, 1);
            int prevMax = prevCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int prevMonthDays = prevMax - dayOfWeek + 1;  //저번 월에서 표시해야 하는 구간

            GregorianCalendar nextCalendar = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, 1);
            int nextMonthDays = 7 - new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), max).get(Calendar.DAY_OF_WEEK);  //해당 월의 말일의 요일

            for (int j = prevMonthDays; j <= prevMax; ++j) {
                GregorianCalendar out = new GregorianCalendar(prevCalendar.get(Calendar.YEAR), prevCalendar.get(Calendar.MONTH), j);
                tempCalendar.put(out, new ArrayList<>()); //일자 타입
            }

            for (int j = 1; j <= max; j++) {
                GregorianCalendar out = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), j);
                tempCalendar.put(out, new ArrayList<>()); //일자 타입
            }

            for (int j = 1; j <= nextMonthDays + (6 - weekCount) * 7; ++j) {
                GregorianCalendar out = new GregorianCalendar(nextCalendar.get(Calendar.YEAR), nextCalendar.get(Calendar.MONTH), j);
                tempCalendar.put(out, new ArrayList<>()); //일자 타입
            }
            mCalendar.setValue(tempCalendar);
            Log.e(TAG, String.valueOf(mCalendar.getValue().size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
