package com.kits.asli.application;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;


import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.kits.asli.adapters.Replication_Auto;

public class WManager extends Worker {

    Context mcontext;
    Replication_Auto replication_auto;

    public WManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.mcontext = context;


    }

    @NonNull
    @Override
    public Result doWork() {

        test();
        return Result.success();
    }

    void test() {

        replication_auto = new Replication_Auto(mcontext);
        replication_auto.replicate_all();


    }


}
