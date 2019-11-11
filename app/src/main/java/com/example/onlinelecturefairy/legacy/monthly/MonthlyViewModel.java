package com.example.onlinelecturefairy.legacy.monthly;

import android.util.Log;

import com.example.onlinelecturefairy.legacy.day.Day;
import com.example.onlinelecturefairy.event.CalendarEvent;
import com.google.api.client.util.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static android.content.ContentValues.TAG;

public class MonthlyViewModel extends ViewModel {
    private MutableLiveData<TreeMap<Calendar, Day>> mCalendar;

    public MonthlyViewModel() {
        mCalendar = new MutableLiveData<>();
    }

    public MonthlyViewModel(GregorianCalendar cal) {
        mCalendar = new MutableLiveData<>();
        setCalendarDate(cal);
    }

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

    public LiveData<TreeMap<Calendar, Day>> getCalendar() {
        if(mCalendar == null) {
            mCalendar = new MutableLiveData<>();
            setCalendarDate(new GregorianCalendar());    //Set it to today.
        }
        return mCalendar;
    }

    public void setCalendarDate(GregorianCalendar cal) {
        try {
            mCalendar = new MutableLiveData<>();
            TreeMap<Calendar, Day> tempCalendar = new TreeMap<>();  //초기화.

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
                tempCalendar.put(out, new Day(out, false)); //일자 타입
            }

            for (int j = 1; j <= max; j++) {
                GregorianCalendar out = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), j);
                tempCalendar.put(out, new Day(out, true)); //일자 타입
            }

            for (int j = 1; j <= nextMonthDays + (6 - weekCount) * 7; ++j) {
                GregorianCalendar out = new GregorianCalendar(nextCalendar.get(Calendar.YEAR), nextCalendar.get(Calendar.MONTH), j);
                tempCalendar.put(out, new Day(out, false)); //일자 타입
            }
            mCalendar.setValue(tempCalendar);
            Log.e(TAG, String.valueOf(mCalendar.getValue().size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setEvent(ArrayList<CalendarEvent> events) {
        TreeMap<Calendar, Day> map = mCalendar.getValue();
        for(CalendarEvent event : events) {
            //시작 날짜와 종료 날짜 초기화
            Date start;
            Date end;
            DateTime startTime = event.getMyEvent().getStart().getDateTime();
            DateTime endTime = event.getMyEvent().getEnd().getDateTime();
            if(startTime != null) {
                start = new Date(startTime.getValue());
                end = new Date(endTime.getValue());
            }
            else {
                start = new Date(event.getMyEvent().getStart().getDate().getValue());
                end = new Date(event.getMyEvent().getEnd().getDate().getValue());
            }
            GregorianCalendar startDate = new GregorianCalendar();
            GregorianCalendar endDate = new GregorianCalendar();
            startDate.setTime(start);
            endDate.setTime(end);
            //시간차 구하기
            int delta = (int) TimeUnit.MILLISECONDS.toDays(end.getTime() - start.getTime());
            Log.e(TAG, "START DATE: " + startDate.getTime().toString());
            Log.e(TAG, "END DATE:   " + endDate.getTime().toString());
            try {
                for (int i = 0; i <= delta; ++i) {
                    GregorianCalendar cal = new GregorianCalendar(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE) + i);
                    Log.e(TAG, "setEvent: " + cal.getTime().toString());
                    map.get(cal).addEvent(event);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
