package com.example.onlinelecturefairy.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

public class GoogleSyncJobService extends JobService {
    @Override
    public void onCreate() {
        Log.e("GOOGLE_SYNC_JOB_SERVICE", "GOOGLE_SYNC_JOB_SERVICE: ON_CREATE");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.e("GOOGLE_SYNC_JOB_SERVICE", "GOOGLE_SYNC_JOB_SERVICE: ON_DESTROY");
        super.onDestroy();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e("GOOGLE_SYNC_JOB_SERVICE", "GOOGLE_SYNC_JOB_SERVICE: ON_START_JOB");
        startService(new Intent(getApplicationContext(), GoogleSyncService.class));
        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e("GOOGLE_SYNC_JOB_SERVICE", "GOOGLE_SYNC_JOB_SERVICE: ON_STOP_JOB");
        return true;    //Set true to re-schedule.
    }
}
