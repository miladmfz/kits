package com.kits.asli.application;

import android.content.Context;
import android.util.Log;

import com.kits.asli.activity.NavActivity;
import com.kits.asli.adapters.Replication;

public class BackgroundJob implements Runnable {

    Context mcontext;
    Replication replication;

    public BackgroundJob(Context context) {
        this.mcontext = context;
    }

    @Override
    public void run() {

        replication = new Replication(mcontext);
        replication.replicate_all();

    }


}
