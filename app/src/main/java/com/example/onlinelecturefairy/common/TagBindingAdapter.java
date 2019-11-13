package com.example.onlinelecturefairy.common;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.example.onlinelecturefairy.ui.tag.TagViewModel;

public class TagBindingAdapter {
    @BindingAdapter("setTagTitle")
    public static void setTagTitle(TextView v, TagViewModel m) {
        v.setText(m.getName().getValue());
    }
}
