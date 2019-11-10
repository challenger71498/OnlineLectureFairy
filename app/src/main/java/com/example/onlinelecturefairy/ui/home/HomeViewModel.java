package com.example.onlinelecturefairy.ui.home;

import com.example.onlinelecturefairy.notice.Notice;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<List<Notice>> mNotices;

    public MutableLiveData<List<Notice>> getNotices() {
        return mNotices;
    }

    public void setNotices(List<Notice> notices) {
        if(mNotices == null) {
            mNotices = new MutableLiveData<>();
        }
        mNotices.setValue(notices);
    }
}