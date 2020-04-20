package com.kits.asli.WManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.kits.asli.R;

public class Wmanager extends Worker {
    public Wmanager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        shownotificationmanager("notificationtest", "des");
        return Result.success();
    }


    void shownotificationmanager(String title, String des){
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=26){
            NotificationChannel ch =new NotificationChannel("Homeandroid","home",NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(ch);

        }
        NotificationCompat.Builder notificationCompat = new
                NotificationCompat.Builder(getApplicationContext(),"homeandrooidnotification")
                .setContentText(des)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher);
        notificationManager.notify(1,notificationCompat.build());



    }

}
