package com.example.onlinelecturefairy.ui.grade;

import com.example.onlinelecturefairy.grade.Grade;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GradeFragmentViewModel extends ViewModel {
    public MutableLiveData<List<Grade>> mGrades;

    public MutableLiveData<List<Grade>> getGrades() {
        return mGrades;
    }

    public void setGrades(List<Grade> grades) {
        if(mGrades == null) {
            mGrades = new MutableLiveData<>();
        }
        mGrades.setValue(grades);
    }
}