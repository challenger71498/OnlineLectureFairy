package com.example.onlinelecturefairy.common;

import android.view.View;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.ui.tag.TagViewModel;

public class TagBindingAdapter {
    @BindingAdapter("setTagTitle")
    public static void setTagTitle(TextView v, TagViewModel m) {
        v.setText(m.getName().getValue());
    }

    @BindingAdapter("setTagColor")
    public static void setTagColor(View v, TagViewModel m) {
        if (m.getName().getValue().equals("휴강") ||
                m.getName().getValue().equals("공강")) {
            v.setBackgroundResource(R.color.colorPrimary);
        } else if (m.getName().getValue().equals("보강")) {
            v.setBackgroundResource(R.color.colorAccent);
        } else {
            v.setBackgroundResource(R.color.colorBackground);
        }
    }
}
