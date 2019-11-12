package com.example.onlinelecturefairy.common;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.ui.onlinelecture.OnlineLectureViewModel;

import androidx.databinding.BindingAdapter;

public class OnlineLectureBindingAdapter {
    @BindingAdapter("setOnlineLecture")
    public static void setOnlineLecture(TextView v, OnlineLectureViewModel m) {
        v.setText(m.getLecture().getValue().getLecture());
    }

    @BindingAdapter("setOnlineWeek")
    public static void setOnlineWeek(TextView v, OnlineLectureViewModel m) {
        v.setText(m.getLecture().getValue().getWeek());
    }

    @BindingAdapter("setOnlineDate")
    public static void setOnlineDate(TextView v, OnlineLectureViewModel m) {
        v.setText("마감일: " + m.getLecture().getValue().getDate());
    }

    @BindingAdapter("setOnlinePass")
    public static void setOnlinePass(ImageView v, OnlineLectureViewModel m) {
        if(m.getLecture().getValue().isPassed()) {
            v.setImageResource(R.color.colorPrimary);
        }
        else {
            v.setImageResource(R.color.tomato);
        }
    }

    @BindingAdapter("setOnlinePassText")
    public static void setOnlinePassText(TextView v, OnlineLectureViewModel m) {
        if(m.getLecture().getValue().isPassed()) {
            v.setText("수강 완료");
            v.setTextColor(v.getResources().getColor(R.color.textGray));
        }
        else {
            v.setText("수강하지 않음");
            v.setTextColor(v.getResources().getColor(R.color.white));
        }
    }

    @BindingAdapter("setOnlineIsClicked")
    public static void setOnlineIsClicked(View v, OnlineLectureViewModel m) {
        if(m.getLecture().getValue().isClicked) {
            v.setBackgroundColor(v.getResources().getColor(R.color.colorBackground));
        } else {
            v.setBackgroundColor(v.getResources().getColor(R.color.colorBackgroundDarker));
        }
    }

    @BindingAdapter("setOnlineLectureColor")
    public static void setOnlineLectureColor(ImageView v, OnlineLectureViewModel m) {
        v.setImageResource(ColorPicker.getColorByLectureId(m.getLecture().getValue().getLecture()));
    }
}
