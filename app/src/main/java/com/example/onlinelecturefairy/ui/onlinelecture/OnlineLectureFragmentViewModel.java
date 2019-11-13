package com.example.onlinelecturefairy.ui.onlinelecture;

import com.example.onlinelecturefairy.onlinelecture.OnlineLecture;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OnlineLectureFragmentViewModel extends ViewModel {
    private MutableLiveData<List<OnlineLecture>> mLectures;

    public void setLectures(List<OnlineLecture> lectures) {
        if (mLectures == null) {
            mLectures = new MutableLiveData<>();
        }

        mLectures.setValue(lectures);
    }

    public MutableLiveData<List<OnlineLecture>> getLectures() {
        if(mLectures == null){
            mLectures = new MutableLiveData<>();
        }
        return mLectures;
    }

    public void postLectures(List<OnlineLecture> lectures){
        if(mLectures == null){
            mLectures = new MutableLiveData<>();
        }
        mLectures.postValue(lectures);
    }
}
