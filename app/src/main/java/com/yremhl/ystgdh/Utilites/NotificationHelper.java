package com.yremhl.ystgdh.Utilites;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.yremhl.ystgdh.Broadcasts.ForegroundServicesActionsBroadcast;
import com.yremhl.ystgdh.R;
import com.yremhl.ystgdh.UI.ActiveDownloads.MainActivity;

public class NotificationHelper {
    Context context;
    Notification notification ;

    public NotificationHelper(Context context) {
        this.context = context;
    }

    public void showRemovedAdsNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // create channel notification if the device is greater the or equal 26 (oreo) as it`s required
            // and make it priority high so the notification is popping up to the user
            createChannel();
        }
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_checked);
        Log.i("ab_do" , "showNotification");
        NotificationCompat.Builder buildNotification = new NotificationCompat.Builder(context , context.getString(R.string.my_notification_channel_id)); // builder to build the notification
        buildNotification.setContentTitle(context.getString(R.string.congra));
        buildNotification.setContentText(context.getString(R.string.adsRemoved));
        buildNotification.setSmallIcon(R.drawable.ic_email);
        buildNotification.setLargeIcon(largeIcon);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) buildNotification.setPriority(NotificationCompat.PRIORITY_MAX);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Notification notification = buildNotification.build();
        notificationManager.notify(1, notification);
    }

    public  Notification getForegroundServiceNotification(int progress , int numOfActiveDownloads , String fileName) {
        int max = 100 ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // create channel notification if the device is greater the or equal 26 (oreo) as it`s required
            // and make it priority high so the notification is popping up to the user
            createChannel();
        }
        NotificationCompat.Builder buildNotification = new NotificationCompat.Builder(context , context.getString(R.string.my_notification_channel_id)); // builder to build the notification
        buildNotification.setContentTitle(fileName);
        buildNotification.setStyle(new NotificationCompat.BigTextStyle()
                .setSummaryText(String.format(context.getString(R.string.active_downloads) , numOfActiveDownloads))
                .setBigContentTitle(fileName)
        );
        buildNotification.setSmallIcon(R.drawable.direct_download);
        buildNotification.setOnlyAlertOnce(true);
        if (progress == -1) {
            buildNotification.setContentText(context.getString(R.string.paused));
        }
        else
        buildNotification.setProgress(100 , progress , false);
        buildNotification.setContentIntent(PendingIntent.getActivity(context.getApplicationContext(), 0 , new Intent(context , MainActivity.class),  0 | FLAG_IMMUTABLE));
        buildNotification.addAction(new NotificationCompat.Action(0 , context.getString(R.string.stop) , PendingIntent.getBroadcast(context , 0 , new Intent(context , ForegroundServicesActionsBroadcast.class).setAction(Constant.STOP_SERVICE)   , FLAG_IMMUTABLE |PendingIntent.FLAG_UPDATE_CURRENT )));
        buildNotification.addAction(new NotificationCompat.Action(0 , context.getString(R.string.pauseAll) , PendingIntent.getBroadcast(context , 0 , new Intent(context , ForegroundServicesActionsBroadcast.class).setAction(Constant.PAUSE_ALL_ACTION) , FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT)));
        buildNotification.addAction(new NotificationCompat.Action(0 , context.getString(R.string.play_all) , PendingIntent.getBroadcast(context , 0 , new Intent(context , ForegroundServicesActionsBroadcast.class).setAction(Constant.PLAY_ALL_ACTION) , FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT)));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) buildNotification.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notification = buildNotification.build();
        //notificationManager.notify(1, notification);
        return notification;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(context.getString(R.string.my_notification_channel_id), context.getString(R.string.download_service), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
    }

}
