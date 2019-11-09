package com.example.onlinelecturefairy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginActivityViewModel extends ViewModel {
    private MutableLiveData<String> mId;
    private MutableLiveData<String> mPw;

    public LiveData<String> getId() {
        return mId;
    }

    public void setId(String id) {
        if(mId == null) {
            mId = new MutableLiveData<>();
        }
        mId.setValue(id);
    }

    public LiveData<String> getPw() {
        return mPw;
    }

    public void setPw(String pw) {
        if(mPw == null) {
            mPw = new MutableLiveData<>();
        }
        this.mPw.setValue(pw);
    }
}
