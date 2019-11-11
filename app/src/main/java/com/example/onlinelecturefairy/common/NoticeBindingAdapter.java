package com.example.onlinelecturefairy.common;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onlinelecturefairy.ui.notice.NoticeViewModel;

import java.util.Arrays;

import androidx.databinding.BindingAdapter;

public class NoticeBindingAdapter {
    @BindingAdapter("setNoticeTitle")
    public static void setNoticeTitle(TextView v, NoticeViewModel model) {
        v.setText(model.getNotice().getValue().getTitle());
    }

    @BindingAdapter("setNoticeDescription")
    public static void setNoticeDescription(TextView v, NoticeViewModel model) {
        v.setText(model.getNotice().getValue().getDescription());
    }

    @BindingAdapter("setNoticeCalendar")
    public static void setNoticeCalendar(TextView v, NoticeViewModel model) {
        String calendar = model.getNotice().getValue().getCalendar();
        calendar = TextUtils.join(" " , Arrays.copyOfRange(calendar.split(" "), 0, 4))
                + "\n"
                + TextUtils.join(" " , Arrays.copyOfRange(calendar.split(" "), 4, 8));
        v.setText(calendar);
    }

    @BindingAdapter("setNoticeLecture")
    public static void setNoticeLecture(TextView v, NoticeViewModel model) {
        v.setText(model.getNotice().getValue().getLecture());
    }

    @BindingAdapter("setNoticeColor")
    public static void setNoticeColor(ImageView v, NoticeViewModel model) {
        v.setImageResource(ColorPicker.getColorByLectureId(model.getNotice().getValue().getSubId()));
    }
}
