package com.jp.ponto;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    private static final int id=0;
    NotificationManager notificationManager;
    Notification myNotification;

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Alarme !!!", Toast.LENGTH_LONG).show();

        Intent myIntent = new Intent(context,MainActivity.class );

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                id,
                myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        myNotification = new NotificationCompat.Builder(context)
                .setContentTitle("ALARME DE PONTO")
                .setContentText("Hora de Saída")
                .setTicker("Notification!")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setVibrate(new long[]{1000, 1000})
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setSmallIcon(R.drawable.ic_launcher)
                .build();

        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, myNotification);

        Intent intent2 = new Intent(context, AlarmReceiverActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent2);
    }

}