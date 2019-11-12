//package com.example.onlinelecturefairy.legacy.decorator;
//
//import android.graphics.Typeface;
//import android.text.style.StyleSpan;
//
//import com.example.onlinelecturefairy.R;
//import com.example.onlinelecturefairy.legacy.span.TextSpan;
//import com.prolificinteractive.materialcalendarview.CalendarDay;
//import com.prolificinteractive.materialcalendarview.DayViewDecorator;
//import com.prolificinteractive.materialcalendarview.DayViewFacade;
//
//import java.util.HashSet;
//
//public class EventDecorator implements DayViewDecorator {
//    private final HashSet<CalendarDay> dates;
//    private androidx.fragment.app.FragmentActivity activity;
//
//    public EventDecorator(androidx.fragment.app.FragmentActivity activity) {
//        this.activity = activity;
//        dates = new HashSet<CalendarDay>();
//        dates.add(CalendarDay.from(2019,10,8));
//    }
//
//    @Override
//    public boolean shouldDecorate(CalendarDay day) {
//        return dates.contains(day);
//    }
//
//    @Override
//    public void decorate(DayViewFacade view) {
//        view.addSpan(new TextSpan("문화데이터", activity.getResources().getColor(R.color.common_google_signin_btn_text_dark_default), activity.getResources().getColor(R.color.colorPrimary), 0));
//        view.addSpan(new TextSpan("문화데이터 준비하기", activity.getResources().getColor(R.color.common_google_signin_btn_text_dark_default), activity.getResources().getColor(R.color.colorPrimary), 1));
//        view.addSpan(new TextSpan("인성이 만나기", activity.getResources().getColor(R.color.common_google_signin_btn_text_dark_default), activity.getResources().getColor(R.color.colorPrimary), 2));
//        view.addSpan(new TextSpan("작업 시작", activity.getResources().getColor(R.color.common_google_signin_btn_text_dark_default), activity.getResources().getColor(R.color.colorPrimary), 3));
//        view.addSpan(new StyleSpan(Typeface.BOLD));
//    }
//}
