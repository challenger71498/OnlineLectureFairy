package com.example.onlinelecturefairy.ui.notice;

import com.example.onlinelecturefairy.notice.Notice;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NoticeViewModel extends ViewModel {
    private MutableLiveData<Notice> mNotice;

    public MutableLiveData<Notice> getNotices() {
        return mNotice;
    }

    public void setNotices(Notice notices) {
        if(mNotice == null) {
            mNotice = new MutableLiveData<>();
        }
        mNotice.setValue(notices);
    }
}
