package com.example.onlinelecturefairy.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;

public class RefreshJobService extends JobService {
    RefreshTask task = new RefreshTask();

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        task.execute();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;    //Returns true to re-schedule.
    }

    class RefreshTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            jobFinished();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
