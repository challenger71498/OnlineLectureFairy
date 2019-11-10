package com.example.onlinelecturefairy.ui.timetable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TimetableViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TimetableViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}