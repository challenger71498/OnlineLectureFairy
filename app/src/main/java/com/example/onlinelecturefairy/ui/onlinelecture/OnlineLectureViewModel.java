package com.example.onlinelecturefairy.ui.onlinelecture;

import com.example.onlinelecturefairy.onlinelecture.OnlineLecture;

import androidx.lifecycle.MutableLiveData;

public class OnlineLectureViewModel {
    private MutableLiveData<OnlineLecture> mLecture;

    public void setLecture(OnlineLecture lecture) {
        if (mLecture == null) {
            mLecture = new MutableLiveData<>();
        }

        mLecture.setValue(lecture);
    }

    public MutableLiveData<OnlineLecture> getLecture() {
        return mLecture;
    }
}
