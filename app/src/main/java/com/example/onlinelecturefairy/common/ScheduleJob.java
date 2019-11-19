package com.example.onlinelecturefairy.common;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.service.BackgroundJobService;
import com.example.onlinelecturefairy.service.BackgroundService;
import com.example.onlinelecturefairy.service.GoogleSyncJobService;
import com.example.onlinelecturefairy.service.GoogleSyncService;
import com.google.common.util.concurrent.ServiceManager;

public class ScheduleJob {

    public void refreshBackground(Context context) {
        //Background initialization.
        //TODO 주기 제대로 바꾸기
        long period = 30;  //minutes
        long latency = 2;

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert scheduler != null;

        scheduler.schedule(
                new JobInfo.Builder(context.getResources().getInteger(R.integer.REFRESH_BACKGROUND_TASK), new ComponentName(context, BackgroundJobService.class))
                        .setPeriodic(period * 1000 * 60, latency * 1000 * 60)
                        .setPersisted(true)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .build());
    }

    public void refreshGoogle(Context context) {
        long googlePeriod = 60;
        long googleLatency = 2;

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert scheduler != null;

        //TODO Calendar 이용해서 조건에 맞을때만 해야함
        scheduler.schedule(
                new JobInfo.Builder(2, new ComponentName(context, GoogleSyncJobService.class))
                        .setPeriodic(googlePeriod * 1000 * 60, googleLatency * 1000 * 60)
                        .setPersisted(true)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .build());
    }

    public void startBackground(Context context, boolean isDirect) {
        Intent intent = new Intent(context, BackgroundService.class);
        intent.putExtra("is-direct", isDirect);

        context.startService(intent);
    }

    public void startGoogle(Context context,  boolean isDirect) {
        Intent intent = new Intent(context, GoogleSyncService.class);
        intent.putExtra("is-direct", isDirect);
        context.startService(intent);
    }
}
