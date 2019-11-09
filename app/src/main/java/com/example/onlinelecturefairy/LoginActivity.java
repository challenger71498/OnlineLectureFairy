package com.example.onlinelecturefairy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.onlinelecturefairy.databinding.LoginActivityBinding;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences appData;
    private LoginActivityViewModel model;
    InputMethodManager imm;

    private EditText idText;
    private EditText pwText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //Activity initialization
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        //SharedPreferences initialization
        appData = getSharedPreferences("appData", MODE_PRIVATE);

        //Model initialization
        model = new LoginActivityViewModel();
        load();

        //Binding
        LoginActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        idText = binding.idInput;
        pwText = binding.pwInput;

        //Focuses ID if id is empty.
        if(model.getId().equals("")) {
            focusId();
        }


        binding.loginButton.setOnClickListener(v -> {
            boolean isInfoCorrect = false;

            imm.hideSoftInputFromWindow(pwText.getWindowToken(), 0);

            if(isInfoCorrect) { //제공한 정보가 일치하면
                save();
            }
            else {
                Snackbar.make(v, "아이디 또는 패스워드가 잘못되었습니다.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void save() {
        appData.edit()
                .putString("ID", idText.getText().toString().trim())
                .putString("PW", pwText.getText().toString().trim())
                .apply();
    }

    private void load() {
        model.setId(appData.getString("ID", ""));
        model.setPw(appData.getString("PW", ""));
    }

    private void focusId() {
        idText.post(() -> {
            idText.setFocusableInTouchMode(true);
            idText.requestFocus();

            assert imm != null;
            imm.showSoftInput(idText,0);

        });
    }
}
