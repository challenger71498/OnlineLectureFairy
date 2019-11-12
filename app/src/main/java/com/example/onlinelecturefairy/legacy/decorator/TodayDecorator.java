//package com.example.onlinelecturefairy.legacy.decorator;
//
//import android.graphics.Typeface;
//import android.text.style.AbsoluteSizeSpan;
//import android.text.style.StyleSpan;
//
//import com.prolificinteractive.materialcalendarview.CalendarDay;
//import com.prolificinteractive.materialcalendarview.DayViewDecorator;
//import com.prolificinteractive.materialcalendarview.DayViewFacade;
//
//import java.util.Calendar;
//
//public class TodayDecorator implements DayViewDecorator {
//    private final Calendar calendar = Calendar.getInstance();
//    private CalendarDay date;
//    private androidx.fragment.app.FragmentActivity activity;
//
//    public TodayDecorator(androidx.fragment.app.FragmentActivity activity) {
//        date = CalendarDay.from(calendar);
//        this.activity = activity;
//    }
//
//    @Override
//    public boolean shouldDecorate(CalendarDay day) {
//        day.copyTo(calendar);
//        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
//        return day.equals(date);
//    }
//
//    @Override
//    public void decorate(DayViewFacade view) {
//        view.addSpan(new StyleSpan(Typeface.BOLD));
//        view.addSpan(new AbsoluteSizeSpan(16, true));
//    }
//}
