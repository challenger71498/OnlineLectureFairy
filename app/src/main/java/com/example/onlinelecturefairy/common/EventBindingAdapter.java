package com.example.onlinelecturefairy.common;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.ui.event.EventViewModel;
import com.google.common.collect.HashBiMap;

import java.util.HashMap;

import androidx.databinding.BindingAdapter;

public class EventBindingAdapter {
    private static final HashBiMap<String, Integer> colorMap = HashBiMap.create();

    static {
        colorMap.put("1", R.color.lavender);
        colorMap.put("2", R.color.sage);
        colorMap.put("3", R.color.grape);
        colorMap.put("4", R.color.flamingo);
        colorMap.put("5", R.color.banana);
        colorMap.put("6", R.color.mandarin);
        colorMap.put("7", R.color.peacock);
        colorMap.put("8", R.color.graphite);
        colorMap.put("9", R.color.blueberry);
        colorMap.put("10", R.color.basil);
        colorMap.put("11", R.color.tomato);
    }

    @BindingAdapter("setEventSumamry")
    public static void setEventSummary(TextView view, EventViewModel model) {
        try {
            view.setText(model.getEvent().getValue().getMyEvent().getSummary());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter("setEventColor")
    public static void setEventColor(View view, EventViewModel model) {
        try {
            String colorId = model.getEvent().getValue().getMyEvent().getColorId();
            if(colorId != null) {
                view.setBackgroundColor(colorMap.get(colorId));
            } else {
                view.setBackgroundColor(view.getResources().getColor(R.color.colorAccent));
            }
        } catch (Exception e) {
            view.setBackgroundColor(view.getResources().getColor(R.color.colorAccent));
            e.printStackTrace();
        }
    }
}
