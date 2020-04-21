package com.kits.asli.webService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.kits.asli.R;

public class Wmanager extends Worker {

    String channelid = "testchennel";

    public Wmanager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Data data =getInputData();
        data.getString("manager");
        shownotificationmanager("notificationtest", data.getString("manager"));

        return Result.success();
    }


    void shownotificationmanager(String title, String des){
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=26){
            NotificationChannel ch =new NotificationChannel(channelid,"home",NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(ch);


        }

        NotificationCompat.Builder notificationCompat = new
                NotificationCompat.Builder(getApplicationContext(),channelid)
                .setContentText(des)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher);
        notificationManager.notify(1,notificationCompat.build());



    }

}
