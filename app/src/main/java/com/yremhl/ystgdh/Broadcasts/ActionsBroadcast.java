package com.yremhl.ystgdh.Broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yremhl.ystgdh.Downloader.Downloader;
import com.yremhl.ystgdh.Services.ForegroundService;
import com.yremhl.ystgdh.Utilites.Constant;

public class ActionsBroadcast extends BroadcastReceiver {
    Downloader downloader ;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            downloader = new Downloader(context);
            switch (intent.getAction()) {
                case Constant.STOP_SERVICE:
                    context.stopService(new Intent(context, ForegroundService.class));
                    break;
                case Constant.PLAY_ALL_ACTION:
                    downloader.resumeAllDownload();
                    break;
                case Constant.PAUSE_ALL_ACTION:
                    downloader.pauseAllDownload();
                    break;
            }
        }
    }
}
