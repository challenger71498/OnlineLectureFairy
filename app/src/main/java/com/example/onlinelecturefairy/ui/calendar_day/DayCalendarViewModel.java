//package com.example.onlinelecturefairy.ui.calendar_day;
//
//import android.content.Context;
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import androidx.lifecycle.ViewModel;
//
//import com.example.onlinelecturefairy.event.Event;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//
//public class DayCalendarViewModel extends ViewModel {
//    private MutableLiveData<String> mText;
//    private MutableLiveData<ArrayList<Event>> mEventList;
//
//    public DayCalendarViewModel() {
//        mText = new MutableLiveData<>();
//        mEventList = new MutableLiveData<>();
//
//        mText.setValue("This is home fragment");
//    }
//
//    public void initGoal(Context context) {
//
//    }
//
//    public void setCalendar(Calendar calendar) {
//        mText.setValue(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
//    }
//
//    public LiveData<String> getText() {
//        return mText;
//    }
//}
