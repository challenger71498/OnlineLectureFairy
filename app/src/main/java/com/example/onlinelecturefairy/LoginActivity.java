package com.example.onlinelecturefairy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.onlinelecturefairy.common.AsyncTaskCallBack;
import com.example.onlinelecturefairy.common.BlackboardInfoCheck;
import com.example.onlinelecturefairy.databinding.LoginActivityBinding;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences appData;
    private LoginActivityViewModel model;
    InputMethodManager imm;
    private String blackboard_user_id;
    private String blackboard_user_password;
    private EditText idText;
    private EditText pwText;
    ProgressDialog progressDialog;
    boolean isInfoCorrect;
    private Map<String, String> loginCookie;

    // for double back button to exit function.
    private final long INTERNAL_TIME = 1000;
    private long previousTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //Activity initialization
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

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
        if (model.getId().equals("")) {
            focusId();
        } else {
            //아이디 비밀번호 자동완성
            model.getId().observe(this, id -> {
                idText.setText(id);
            });

            model.getPw().observe(this, pw -> {
                pwText.setText(pw);
            });

            // 자동 로그인이 켜져 있을 경우 자동으로 로그인 시도.
            if (appData.getBoolean("autoLogin", false)) {
                login(binding.loginButton.getRootView(), model.getId().getValue(), model.getPw().getValue());
            }
        }

        binding.loginButton.setOnClickListener(v -> {
            imm.hideSoftInputFromWindow(pwText.getWindowToken(), 0);
            final EditText inputId = findViewById(R.id.idInput);
            final EditText inputPw = findViewById(R.id.pwInput);
            login(v, inputId.getText().toString(), inputPw.getText().toString());
        });
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();

        if(currentTime - previousTime <= INTERNAL_TIME) {
            finishAffinity();
        } else {
            previousTime = currentTime;
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void save() {
        appData.edit()
                .putString("ID", idText.getText().toString())
                .putString("PW", pwText.getText().toString())
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
            imm.showSoftInput(idText, 0);

        });
    }

    //로그인을 시도하는 함수.
    private void login(View v, String _id, String _pw) {
        Log.e(null, _id + " " + _pw);
        blackboard_user_id = _id;
        blackboard_user_password = _pw;
        BlackboardInfoCheck.CheckBlackBoard check = new BlackboardInfoCheck.CheckBlackBoard(v.getContext(), _id, _pw, isInfoCorrect, new AsyncTaskCallBack() {
            @Override
            public void onSuccess() {
                isInfoCorrect = true;
                save();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("isInfoCorrect", isInfoCorrect);
                startActivity(intent);
            }

            @Override
            public void onFailure() {
                Snackbar snackbar = Snackbar.make(v, "아이디 또는 패스워드가 잘못되었습니다.", Snackbar.LENGTH_LONG);
                snackbar
                        .setAction("Action", null)
                        .show();
            }
        });
        check.execute();
    }
}
