package com.example.onlinelecturefairy.ui.grade;

import com.example.onlinelecturefairy.grade.Grade;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GradeViewModel extends ViewModel {
    private MutableLiveData<Grade> mGrade;

    public MutableLiveData<Grade> getGrade() {
        return mGrade;
    }

    public void setGrade(Grade grade) {
        if(mGrade == null) {
            mGrade = new MutableLiveData<>();
        }
        mGrade.setValue(grade);
    }
}
