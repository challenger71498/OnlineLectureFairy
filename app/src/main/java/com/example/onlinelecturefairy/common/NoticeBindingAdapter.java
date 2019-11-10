package com.example.onlinelecturefairy.common;

import android.widget.TextView;

import com.example.onlinelecturefairy.ui.notice.NoticeViewModel;

import androidx.databinding.BindingAdapter;

public class NoticeBindingAdapter {
    @BindingAdapter("setNoticeTitle")
    public static void setNoticeTitle(TextView v, NoticeViewModel model) {
        v.setText(model.getNotices().getValue().getTitle());
    }

    @BindingAdapter("setNoticeDescription")
    public static void setNoticeDescription(TextView v, NoticeViewModel model) {
        v.setText(model.getNotices().getValue().getDescription());
    }

    @BindingAdapter("setNoticeCalendar")
    public static void setNoticeCalendar(TextView v, NoticeViewModel model) {
        v.setText(model.getNotices().getValue().getCalendar());
    }

    @BindingAdapter("setNoticeLecture")
    public static void setNoticeLecture(TextView v, NoticeViewModel model) {
        v.setText(model.getNotices().getValue().getLecture());
    }
}
