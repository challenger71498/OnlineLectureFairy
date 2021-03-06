package com.example.onlinelecturefairy.legacy;

import android.widget.TextView;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.legacy.monthly.dailymonthly.DailyMonthlyViewModel;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DailyMonthlyBindingAdapter {
    public static boolean isToday(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_YEAR) == new GregorianCalendar().get(Calendar.DAY_OF_YEAR);
    }

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
    public static void setDayColor(TextView view, DailyMonthlyViewModel model) {
        try {
            if(model != null) {
                if(isToday(model.getDay().getValue().getCalendar())) {
                    view.setTextColor(view.getResources().getColor(R.color.white));
                    return; //오늘 날짜인 경우 텍스트 색을 흰색으로 설정하고 빠져나감.
                }
                else if (!model.getDay().getValue().getIsThisMonth()) {
                    view.setTextColor(view.getResources().getColor(R.color.textGray));
                    return; //이번 달에 포함되지 않을 경우 텍스트 색을 회색으로 설정하고 빠져나감.
                }

                if(model.getDay().getValue().getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    view.setTextColor(view.getResources().getColor(R.color.tomato));
                }
                else if (model.getDay().getValue().getCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                    view.setTextColor(view.getResources().getColor(R.color.blueberry));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
