package com.example.onlinelecturefairy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import com.example.onlinelecturefairy.common.AsyncTaskCallBack;
import com.example.onlinelecturefairy.common.BlackboardInfoCheck;
import com.example.onlinelecturefairy.common.KomoranLoader;
import com.example.onlinelecturefairy.databinding.LoginActivityBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    InputMethodManager imm;
    BlackboardInfoCheck.CheckBlackBoard check;
    KomoranLoader.Loader loader;
    Intent intent = null;

    private SharedPreferences appData;
    private LoginActivityViewModel model;
    private String blackboard_user_id;
    private String blackboard_user_password;
    private EditText idText;
    private EditText pwText;
    boolean isInfoCorrect;
    private Map<String, String> loginCookie;

    // for double back button to exit function.
    private final long INTERNAL_TIME = 1000;
    private long previousTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        if(loader != null && loader.getStatus() == AsyncTask.Status.RUNNING) {
            loader.cancel(true);
        }

        //Removes notifications.
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(417);

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

        //Notification
        createNotificationChannel();

        //Intent
        intent = getIntent();

        //Focuses ID if id is empty.
        if (model.getId().equals("")) {
            focusId();
            assert imm != null;
            imm.showSoftInput(idText, 0);
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

        //Komoran load.
        loader = new KomoranLoader.Loader();
        loader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        binding.loginButton.setOnClickListener(v -> {
            imm.hideSoftInputFromWindow(pwText.getWindowToken(), 0);
            final EditText inputId = findViewById(R.id.idInput);
            final EditText inputPw = findViewById(R.id.pwInput);
            login(v, inputId.getText().toString(), inputPw.getText().toString());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(check != null) {
            if(!check.isCancelled()) {
                check.cancel(true);
            }
        }
    }

    //channeling
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "알림";
            String description = "시스템 전반적인 알림입니다.";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("0417", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - previousTime <= INTERNAL_TIME) {
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
        });
    }

    //로그인을 시도하는 함수.
    private void login(View v, String _id, String _pw) {
        Log.e(null, _id + " " + _pw);
        blackboard_user_id = _id;
        blackboard_user_password = _pw;
        check = new BlackboardInfoCheck.CheckBlackBoard(v.getContext(), _id, _pw, isInfoCorrect, new AsyncTaskCallBack() {
            @Override
            public void onSuccess() {
                isInfoCorrect = true;
                save();

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Log.e("LOGIN", "LOGIN: IS_FIRST_TIME? " + pref.getBoolean("first-time", true));
                if (pref.getBoolean("first-time", true)) {
                    pref.edit()
                            .putBoolean("first-time", false)
                            .apply();
                    startActivity(new Intent(LoginActivity.this, TutorialActivity.class));
                }
                else {
                    Intent sendIntent = new Intent(LoginActivity.this, MainActivity.class);
                    sendIntent.putExtra("isInfoCorrect", isInfoCorrect);
                    // Error intents
                    if(intent != null) {
                        if(intent.getBooleanExtra("account-check", false)) {
                            Log.e("TAG", "onSuccess: LOGIN_ACTIVITY got accuont-check");
                            sendIntent.putExtra("account-check", true);
                            intent.removeExtra("account-check");
                        }
                        else if (intent.getBooleanExtra("permission-check", false)) {
                            Log.e("TAG", "onSuccess: LOGIN_ACTIVITY got permission-check");
                            sendIntent.putExtra("permission-check", true);
                            intent.removeExtra("permission-check");
                        }
                        else if (intent.getBooleanExtra("everytime", false)) {
                            Log.e("TAG", "onSuccess: LOGIN_ACTIVITY got everytime");
                            sendIntent.putExtra("everytime", true);
                            intent.removeExtra("everytime");
                        }
                    }
                    //intent 초기화
                    intent = null;

                    startActivity(sendIntent);
                }
            }

            @Override
            public void onFailure() {
                Snackbar snackbar = Snackbar.make(v, "아이디 또는 패스워드가 잘못되었습니다.", Snackbar.LENGTH_LONG);
                snackbar
                        .setAction("Action", null)
                        .show();
            }
        });
        check.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
