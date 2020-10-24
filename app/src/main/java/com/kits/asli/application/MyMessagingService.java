package com.kits.asli.application;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kits.asli.R;

import java.util.Objects;


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyMessagingService extends FirebaseMessagingService {

    String channel_id = "Kowsarmobile";
    String channel_name = "home";


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotification(Objects.requireNonNull(remoteMessage.getNotification()).getTitle(), remoteMessage.getNotification().getBody());
    }


    public void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
                .setContentText(message);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
        managerCompat.notify(999, builder.build());

    }
}
