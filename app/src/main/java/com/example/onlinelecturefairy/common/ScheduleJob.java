package com.example.onlinelecturefairy.common;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.service.BackgroundService;
import com.example.onlinelecturefairy.service.GoogleSyncService;

public class ScheduleJob {

    public void refreshBackground(Context context) {
        //Background initialization.
        //TODO 주기 제대로 바꾸기
        long period = 15;  //minutes
        long latency = 15;

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert scheduler != null;

        scheduler.schedule(
                new JobInfo.Builder(context.getResources().getInteger(R.integer.REFRESH_BACKGROUND_TASK), new ComponentName(context, BackgroundService.class))
                        .setPeriodic(period * 1000 * 60)
                        .setPersisted(true)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .build());
    }

    public void refreshGoogle(Context context) {
        long googlePeriod = 15;
        long googleLatency = 10;

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert scheduler != null;

        //TODO Calendar 이용해서 조건에 맞을때만 해야함
        scheduler.schedule(
                new JobInfo.Builder(2, new ComponentName(context, GoogleSyncService.class))
                        .setPeriodic(googlePeriod * 1000 * 60)
                        .setPersisted(true)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .build());
    }
}
