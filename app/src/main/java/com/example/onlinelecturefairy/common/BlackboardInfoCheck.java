package com.example.onlinelecturefairy.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.example.onlinelecturefairy.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class BlackboardInfoCheck {
    public static class CheckBlackBoard extends AsyncTask<Void, Void, Void> {
        private AsyncTaskCallBack callBack;
        private Context context;
        private ProgressDialog progressDialog;
        private Map<String, String> loginCookie;
        private String blackboard_user_id;
        private Boolean isInfoCorrect;

        private String blackboard_user_password;

        public CheckBlackBoard(Context context, String id, String pw, Boolean isInfoCorrect, AsyncTaskCallBack callBack) {
            this.callBack = callBack;
            this.context = context;
            this.blackboard_user_id = id;
            this.blackboard_user_password = pw;
            this.isInfoCorrect = isInfoCorrect;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e(TAG, "onPreExecute: BLACKBOARD_VALIDITY_CHECK");
            if(context != null) {
                progressDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
                progressDialog.getWindow().setBackgroundDrawableResource(R.color.colorBackground);
                progressDialog.setMessage("블랙보드에 로그인 중..");
                progressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //TODO: doInBackground 실행 완료 시 수행
            if (isInfoCorrect) {
                callBack.onSuccess();
            } else {
                callBack.onFailure();
            }

            // 제공한 정보의 일치 여부에 따라 향후 자동 로그인 판단.
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            pref.edit()
                    .putBoolean("autoLogin", isInfoCorrect)
                    .apply();

            progressDialog.dismiss();
            Log.e(TAG, "onPreExecute: BLACKBOARD_VALIDITY_CHECK_END");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.e(TAG, "onPreExecute: BLACKBOARD_VALIDITY_CHECK_START");
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
