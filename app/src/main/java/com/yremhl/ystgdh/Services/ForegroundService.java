package com.yremhl.ystgdh.Services;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.yremhl.ystgdh.Database.Repo;
import com.yremhl.ystgdh.Downloader.DownloaderHelper;
import com.yremhl.ystgdh.R;
import com.yremhl.ystgdh.Utilites.Constant;
import com.yremhl.ystgdh.Utilites.NotificationHelper;
import com.yremhl.ystgdh.Utilites.Utilities;


public class ForegroundService extends Service {
    NotificationHelper notificationHelper ;
    BroadcastReceiver receiver ;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // called first time the service is created
        Utilities.PrintLog("Create service");
        notificationHelper = new NotificationHelper(getApplicationContext());
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(getString(R.string.battery_key) , false))
        applyReceiver();
        super.onCreate();
    }

    private void applyReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
                    // the battery is low you should stop the service
                    Utilities.updateStopServiceValue(context , true);
                    stopSelf();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_LOW);
        registerReceiver(receiver , intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // called each time start Services get called
        //Utilities.PrintLog("onStartCommand");
        int progress = intent.getIntExtra(Constant.NOTIFICATION_PROGRESS , 0);
        int activeDownloads = intent.getIntExtra(Constant.NOTIFICATION_ACTIVE_DOWNLOADS , 0);
        String notification_content = intent.getStringExtra(Constant.NOTIFICATION_CONTENT_TEXT);
        Notification notification = notificationHelper.getForegroundServiceNotification(progress , activeDownloads , notification_content);
        startForeground(Constant.DOWNLOAD_FOREGROUND_SERVICE_ID , notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // called when services get destroy
        Utilities.PrintLog("onDestroyService");
        extracted();
        // when the service is destroyed you should pause all active downloads if there is
        DownloaderHelper.getFetch(getApplicationContext()).pauseAll();
        Repo repo = new Repo(getApplication());
        repo.updateAllActiveDownloadStatue(true);
        Utilities.updateDownloadShouldBeShown(getApplicationContext() , -1);
        if (DownloaderHelper.fetch != null) DownloaderHelper.fetch.close();
        DownloaderHelper.fetch = null ;
        super.onDestroy();
    }

    private void extracted() {
        if (receiver !=null) {
            unregisterReceiver(receiver);
            receiver = null ;
        }
    }
}
