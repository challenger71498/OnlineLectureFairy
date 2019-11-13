package com.example.onlinelecturefairy.service;

import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.example.onlinelecturefairy.LoginActivity;
import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.common.AsyncTaskCallBack;
import com.example.onlinelecturefairy.common.BlackboardInfoCheck;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

public class BackgroundService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        BackgroundTask task = new BackgroundTask(jobParameters);
        task.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;    //Set true to re-schedule.
    }

    class BackgroundTask extends AsyncTask<Void, Void, Void> {
        JobParameters params;
        Boolean isInfoCorrect;

        BackgroundTask(JobParameters params) {
            this.params = params;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Blackboard ID PW validity check
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String id = pref.getString("ID", "");
            String pw = pref.getString("PW", "");

//            // Blackboard setting 예시
//            pref.edit()
//                    .putString("ID", "12181632")
//                    .apply();

            BlackboardInfoCheck.CheckBlackBoard board
                    = new BlackboardInfoCheck.CheckBlackBoard(getApplicationContext(), id, pw, isInfoCorrect, new AsyncTaskCallBack() {
                @Override
                public void onSuccess() {
                    // 로그인에 성공했을 때의 작업 작성.
                }

                @Override
                public void onFailure() {
                    // 로그인에 실패했을 때의 작업 작성.
                }
            });

            // 웹강 조건을 check

            // 시간표 바꾸어야 하는지 조건을 check


            jobFinished(params,true);   // true로 놓아야 계속 job을 돌림
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //여기에 함수를 실행.
            return null;
        }
    }
}
