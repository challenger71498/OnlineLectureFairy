package com.example.onlinelecturefairy.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

public class BackgroundJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        startService(new Intent(getApplicationContext(), BackgroundService.class));

        jobFinished(jobParameters, true);   // true로 놓아야 계속 job을 돌림
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;    //Set true to re-schedule.
    }
}
