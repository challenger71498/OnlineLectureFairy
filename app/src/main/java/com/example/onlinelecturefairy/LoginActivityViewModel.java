package com.example.onlinelecturefairy;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.ViewModel;

public class LoginActivityViewModel extends ViewModel {
    private String id;
    private String pw;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }
}
