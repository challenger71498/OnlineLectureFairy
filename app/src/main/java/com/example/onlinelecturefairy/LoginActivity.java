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

import com.example.onlinelecturefairy.databinding.LoginActivityBinding;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

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
    private Map<String,String> loginCookie;
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
                try {
                    Thread.sleep(1000); //1초 대기
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                login(binding.loginButton.getRootView(),model.getId().toString(),model.getPw().toString());
            }
        }

        binding.loginButton.setOnClickListener(v -> {
            imm.hideSoftInputFromWindow(pwText.getWindowToken(), 0);
            final EditText inputId = (EditText) findViewById(R.id.idInput);
            final EditText inputPw = (EditText) findViewById(R.id.pwInput);
            login(v,inputId.getText().toString(),inputPw.getText().toString());
        });
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
            imm.showSoftInput(idText,0);

        });
    }

    //로그인을 시도하는 함수.
    private void login(View v, String _id, String _pw) {
        Log.e(null,_id+ " "+_pw);
        blackboard_user_id = _id;
        blackboard_user_password = _pw;
        CheckBlackBoard check = new CheckBlackBoard();
        check.execute();
        try {
            Thread.sleep(300); //1초 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    private class CheckBlackBoard extends AsyncTask<Void,Void,Void> {

        @Override
        protected  void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);

            progressDialog.dismiss();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36";


                Connection.Response loginPageResponse = Jsoup.connect("https://learn.inha.ac.kr/webapps/login/")
                        .timeout(3000)
                        .header("Origin","https://learn.inha.ac.kr")
                        .header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                        .header("Content-Type","application/x-www-form-urlencoded")
                        .data("user_id",blackboard_user_id,"password",blackboard_user_password)
                        .method(Connection.Method.POST)
                        .execute();
                Map<String,String> loginTryCookie = loginPageResponse.cookies();

                Map<String,String> userData = new HashMap<>();
                userData.put("user_id",blackboard_user_id);
                userData.put("password",blackboard_user_password);

                Connection.Response res =  Jsoup.connect("https://learn.inha.ac.kr/webapps/login/")
                        .userAgent(userAgent)
                        .timeout(3000)
                        .header("Origin","https://learn.inha.ac.kr")
                        .header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                        .header("Content-Type","application/x-www-form-urlencoded")
                        .cookies(loginTryCookie)
                        .data(userData)
                        .method(Connection.Method.POST)
                        .execute();
                loginCookie = res.cookies();
                if(loginCookie.isEmpty()){
                    isInfoCorrect = false;
                    return null;
                }
                isInfoCorrect = true;
            }catch (IOException o){
                o.printStackTrace();
            }
            return null;
        }

    }
}
