package com.kits.asli.application;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;


@SuppressLint("SpecifyJobSchedulerIdRange")
public class ExampleJobExample extends JobService {

    private static final String TAG = "ExampleJobExample";
    private boolean jobcansekked = false;


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e(TAG, "jon started");
        doback(params);
        return true;
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e(TAG, "job cansel  ");
        jobcansekked = true;
        return true;
    }


    private void doback(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    Log.e(TAG, "Run : " + i);
                    if (jobcansekked) {
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.e(TAG, "job finish ");

                jobFinished(params, false);
            }
        }).start();

    }


}
