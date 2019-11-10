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
        CheckBlackBoard check = new CheckBlackBoard();
        check.setView(v);
        check.execute();
//        try {
//            Thread.sleep(300); //1초 대기
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        if (isInfoCorrect) { //제공한 정보가 일치하면
//            save();
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
//        } else {  //제공한 정보가 일치하지 않으면
//            Snackbar snackbar = Snackbar.make(v, "아이디 또는 패스워드가 잘못되었습니다.", Snackbar.LENGTH_LONG);
//            snackbar
//                    .setAction("Action", null)
//                    .show();
//        }
    }



    private class CheckBlackBoard extends AsyncTask<Void, Void, Void> {
        private View v;
        private AsyncTaskCallBack callBack = new AsyncTaskCallBack() {
            @Override
            public void onSuccess() {  //로그인 성공 시
                isInfoCorrect = true;
                save();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("isInfoCorrect", isInfoCorrect);
                startActivity(intent);
            }

            @Override
            public void onFailure() {    //로그인 실패 시
                Snackbar snackbar = Snackbar.make(v, "아이디 또는 패스워드가 잘못되었습니다.", Snackbar.LENGTH_LONG);
                snackbar
                        .setAction("Action", null)
                        .show();
            }
        };

        //View setting for snack bar.
        protected void setView(View v) {
            this.v = v;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginActivity.this, ProgressDialog.STYLE_SPINNER);
            progressDialog.getWindow().setBackgroundDrawableResource(R.color.colorBackground);
            progressDialog.setMessage("로그인 중..");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //TODO: doInBackground 실행 완료 시 수행
            if(isInfoCorrect) {
                callBack.onSuccess();
            }
            else {
                callBack.onFailure();
            }

            // 제공한 정보의 일치 여부에 따라 향후 자동 로그인 판단.
            appData.edit()
                    .putBoolean("autoLogin", isInfoCorrect)
                    .apply();

            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //로그인 시도
            try {
                String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36";


                Connection.Response loginPageResponse = Jsoup.connect("https://learn.inha.ac.kr/webapps/login/")
                        .timeout(3000)
                        .header("Origin", "https://learn.inha.ac.kr")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .data("user_id", blackboard_user_id, "password", blackboard_user_password)
                        .method(Connection.Method.POST)
                        .execute();
                Map<String, String> loginTryCookie = loginPageResponse.cookies();

                Map<String, String> userData = new HashMap<>();
                userData.put("user_id", blackboard_user_id);
                userData.put("password", blackboard_user_password);

                Connection.Response res = Jsoup.connect("https://learn.inha.ac.kr/webapps/login/")
                        .userAgent(userAgent)
                        .timeout(3000)
                        .header("Origin", "https://learn.inha.ac.kr")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .cookies(loginTryCookie)
                        .data(userData)
                        .method(Connection.Method.POST)
                        .execute();
                loginCookie = res.cookies();
                if (loginCookie.isEmpty()) {    //실패 시
                    isInfoCorrect = false;
                    return null;
                }
                isInfoCorrect = true;
            } catch (IOException o) {
                o.printStackTrace();
            }
            return null;
        }

    }
}
