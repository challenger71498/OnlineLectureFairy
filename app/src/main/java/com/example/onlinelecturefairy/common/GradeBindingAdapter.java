package com.example.onlinelecturefairy.common;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.onlinelecturefairy.ui.grade.GradeViewModel;

import androidx.databinding.BindingAdapter;

public class GradeBindingAdapter {
    @BindingAdapter("setGradeScore")
    public static void setGradeScore(TextView v, GradeViewModel m) {
        v.setText(m.getGrade().getValue().getScore());
    }

    @BindingAdapter("setGradeScoreSub")
    public static void setGradeScoreSub(TextView v, GradeViewModel m) {
        v.setText(m.getGrade().getValue().getScoreSub());
    }

    @BindingAdapter("setGradeLecture")
    public static void setGradeLecture(TextView v, GradeViewModel m) {
        v.setText(m.getGrade().getValue().getLecture());
    }

    @BindingAdapter("setGradeDescription")
    public static void setGradeDescription(TextView v, GradeViewModel m) {
        v.setText(m.getGrade().getValue().getDescription());
    }

    @BindingAdapter("setGradeColor")
    public static void setGradeColor(ImageView v, GradeViewModel m) {
        v.setImageResource(ColorPicker.getColorByLectureId(m.getGrade().getValue().getId()));
    }
}
