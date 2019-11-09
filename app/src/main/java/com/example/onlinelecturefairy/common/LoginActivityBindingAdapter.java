package com.example.onlinelecturefairy.common;

import android.widget.EditText;

import com.example.onlinelecturefairy.LoginActivityViewModel;

import androidx.databinding.BindingAdapter;

public class LoginActivityBindingAdapter {
    @BindingAdapter("setIdText")
    public static void setIdText(EditText view, LoginActivityViewModel model) {
        try {
            view.setText(model.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter("setPwText")
    public static void setPwText(EditText view, LoginActivityViewModel model) {
        try {
            view.setText(model.getPw());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
