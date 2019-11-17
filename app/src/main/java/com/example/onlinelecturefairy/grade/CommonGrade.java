package com.example.onlinelecturefairy.grade;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class CommonGrade {
    public static HashMap<String, Float> grades = new HashMap<>();

    public static ArrayList<String> findGrade(String lecture) {
        ArrayList<String> k = new ArrayList<>();
        Set<String> keys = grades.keySet();
        for(String key : keys) {
            if(key.contains(lecture) && key.contains(lecture)) {
                k.add(key);
            }
        }
        return k;
    }

    public static float getScore(String lecture, String term) {
        Set<String> keys = grades.keySet();
        for(String key : keys) {
            if(key.contains(lecture) && key.contains(term)) {
                return grades.get(key);
            }
        }
        return -1;
    }

    public static void setScore(String lecture, String term, float score) {
        if(grades == null) {
            grades = new HashMap<>();
        }
        grades.put(lecture + " " + term, score);
    }

    public static void loadGrades(Context context) {
        grades = new HashMap<>();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> keys = pref.getStringSet("common-grade-keys", null);
        if(keys != null) {
            for(String key : keys) {
                if(pref.contains(key)) {
                    grades.put(key, pref.getFloat(key, -1));
                }
            }
        }
    }

    public static void saveGrades(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> keys = grades.keySet();
        pref.edit()
                .putStringSet("common-grade-keys", keys)
                .apply();
        for(String key : keys) {
            pref.edit()
                    .putFloat(key, grades.get(key))
                    .apply();
        }
    }

//    private String lecture;
//    private float score;
//    private String term;
//
//    public CommonGrade(String lecture, float score, String term) {
//        this.lecture = lecture;
//        this.score = score;
//        this.term = term;
//    }
//
//    public String getLecture() {
//        return lecture;
//    }
//
//    public void setLecture(String lecture) {
//        this.lecture = lecture;
//    }
//
//    public float getScore() {
//        return score;
//    }
//
//    public void setScore(float score) {
//        this.score = score;
//    }
//
//    public String getTerm() {
//        return term;
//    }
//
//    public void setTerm(String term) {
//        this.term = term;
//    }
}
