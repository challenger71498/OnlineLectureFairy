package com.example.onlinelecturefairy;

import com.example.onlinelecturefairy.day.Day;
import com.example.onlinelecturefairy.event.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Objects;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FragmentActivityViewModel extends ViewModel {
    private MutableLiveData<HashMap<Calendar, ArrayList<Event>>> mDays;
    private MutableLiveData<ArrayList<Event>> mEvents;

    public LiveData<HashMap<Calendar,  ArrayList<Event>>> getDays() {
        if(mDays == null) {
            mDays = new MutableLiveData<>();
            loadDays();
        }
        return mDays;
    }

    private void loadDays() {
        // Do an asynchronous operation to fetch days.
        setCalendarList();
    }

    public LiveData<HashMap<Calendar,  ArrayList<Event>>> getDays() {
        if(mDays == null) {
            mDays = new MutableLiveData<>();
            loadDays();
        }
        return mDays;
    }

    private void loadDays() {
        // Do an asynchronous operation to fetch days.
        setCalendarList();
    }

    private void setCalendarList() {
        GregorianCalendar cal = new GregorianCalendar(); // 오늘 날짜

        for (int i = -300; i < 300; i++) {
            try {
                GregorianCalendar calendar = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + i, 1);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; //해당 월에 시작하는 요일 -1
                int weekCount = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);  //해당 월의 최대 주
                int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); //말일

                GregorianCalendar prevCalendar = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + i - 1, 1);
                int prevMonthDays = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH) - dayOfWeek;  //저번 월에서 표시해야 하는 구간

                GregorianCalendar nextCalendar = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + i + 1, 1);
                int nextMonthDays = 7 - new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), max).get(Calendar.DAY_OF_WEEK);  //해당 월의 말일의 요일

                for (int j = prevMonthDays; j <= dayOfWeek; ++j) {
                    Day day = new Day(new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), j));
                    Objects.requireNonNull(mDays.getValue()).put(day.getCalendar(), null); //일자 타입
                }

                for (int j = 1; j <= max; j++) {
                    Day day = new Day(new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), j));
                    Objects.requireNonNull(mDays.getValue()).put(day.getCalendar(), null); //일자 타입
                }

                for (int j = 1; j <= nextMonthDays + (6 - weekCount) * 7; ++j) {
                    Day day = new Day(new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), j));
                    Objects.requireNonNull(mDays.getValue()).put(day.getCalendar(), null); //일자 타입
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void attachEvents() {
        for(Event event : Objects.requireNonNull(mEvents.getValue())) {

            Date startDate = ;
            if (startDate == null) {
                startDate = event.getStartDate();
            }
            for()
            Objects.requireNonNull(mDays.getValue()).get(event.get)
        }
    }
}
