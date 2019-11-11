package com.example.onlinelecturefairy.common;

import com.example.onlinelecturefairy.R;
import com.google.common.collect.HashBiMap;

public class ColorPicker {
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

    public static int getColorByLectureId(String id) {
        try {
            return colorMap.get(lectureMap.get(id));
        } catch (Exception e) {
            return R.color.colorAccent;
        }
    }

    private static HashBiMap<String, Integer> lectureMap = HashBiMap.create();

    public static void addLectureId(String id) {
        if(!lectureMap.containsKey(id)) {
            lectureMap.put(id, lectureMap.size());
        }
    }
}
