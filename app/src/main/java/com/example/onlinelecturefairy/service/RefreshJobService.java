package com.example.onlinelecturefairy.service;

import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.onlinelecturefairy.LoginActivity;
import com.example.onlinelecturefairy.R;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.ContentValues.TAG;

public class RefreshJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        RefreshTask task = new RefreshTask(jobParameters);
        task.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;    //Returns true to re-schedule.
    }

    class RefreshTask extends AsyncTask<Void, Void, Void> {
        JobParameters parameters;

        public RefreshTask(JobParameters params) {
            parameters = params;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e(TAG, "JOB FINISHED!");

            //Notifications
            //TODO: 여러 개의 알림이 겹치지 않고 떠야 함.
            if(true) {  //조건을 만족할 때, 일단 true로 박아놓음
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "0417")
                        .setSmallIcon(R.drawable.web_fairy_short)
                        .setContentTitle("작업 완료! " + Math.random())
                        .setContentText("작업을 완료했습니다! " + Math.random())
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("자세한 텍스트는 여기에 작성됩니다. 작업을 완료했습니다."))
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(417, builder.build());
            }

            jobFinished(parameters, true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
