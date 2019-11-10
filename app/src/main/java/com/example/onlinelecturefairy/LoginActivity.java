package com.example.onlinelecturefairy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.onlinelecturefairy.databinding.LoginActivityBinding;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences appData;
    private LoginActivityViewModel model;
    InputMethodManager imm;

    private EditText idText;
    private EditText pwText;

    boolean isInfoCorrect;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //Activity initialization
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        //SharedPreferences initialization
        appData = PreferenceManager.getDefaultSharedPreferences(this);

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
        else {
            //아이디 비밀번호 자동완성
            model.getId().observe(this, id -> {
                idText.setText(id);
            });

            model.getPw().observe(this, pw -> {
                pwText.setText(pw);
            });

            // 자동 로그인이 켜져 있을 경우 자동으로 로그인 시도.
            if(appData.getBoolean("autoLogin", false)) {
                login(binding.loginButton.getRootView());
            }
        }

        binding.loginButton.setOnClickListener(v -> {
            imm.hideSoftInputFromWindow(pwText.getWindowToken(), 0);
            login(v);
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

    //로그인을 시도하는 함수.
    private void login(View v) {
        isInfoCorrect = true;   //현재는 무조건 일치하게 해 놓았음.
        // TODO: isInfoCorrect를 설정하는 함수 만들어야 함.

        if(isInfoCorrect) { //제공한 정보가 일치하면
            save();
            Intent intent = new Intent(LoginActivity.this, FragmentActivity.class);
            startActivity(intent);
        }
        else {  //제공한 정보가 일치하지 않으면
            Snackbar snackbar = Snackbar.make(v, "아이디 또는 패스워드가 잘못되었습니다.", Snackbar.LENGTH_LONG);
            snackbar
                    .setAction("Action", null)
                    .show();
        }

        // 제공한 정보의 일치 여부에 따라 향후 자동 로그인 판단.
        appData.edit()
                .putBoolean("autoLogin", isInfoCorrect)
                .apply();
    }
}
