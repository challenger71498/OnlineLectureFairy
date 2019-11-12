package com.example.onlinelecturefairy.ui.notice;

import com.example.onlinelecturefairy.notice.Notice;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NoticeViewModel extends ViewModel {
    private MutableLiveData<Notice> mNotice;

    public MutableLiveData<Notice> getNotice() {
        return mNotice;
    }

    public void setNotice(Notice notice) {
        if(mNotice == null) {
            mNotice = new MutableLiveData<>();
        }
        mNotice.setValue(notice);
    }
}
