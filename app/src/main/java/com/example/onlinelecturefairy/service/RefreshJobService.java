package com.example.onlinelecturefairy.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.util.Log;

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
            jobFinished(parameters, true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
