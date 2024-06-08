package com.yremhl.ystgdh.Broadcasts;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.yremhl.ystgdh.Database.Repo;
import com.yremhl.ystgdh.Downloader.Downloader;
import com.yremhl.ystgdh.Models.ScheduleDownload;
import com.yremhl.ystgdh.R;
import com.yremhl.ystgdh.Utilites.Constant;
import com.yremhl.ystgdh.Utilites.Utilities;

public class ScheduleDownloadBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
          if (intent.getAction() != null && intent.getAction().equals(Constant.SCHEDULE_DOWNLOAD_ACTION)) {
              long download_id = intent.getLongExtra(Constant.SCHEDULE_DOWNLOAD_ID , -1);
              Utilities.PrintLog("Schedule download will start " + download_id);
              // 1- first get and remove schedule download from database
              Repo repo = new Repo((Application) context.getApplicationContext());
              ScheduleDownload scheduleDownload = repo.getScheduleDownload(download_id);
              if (scheduleDownload == null) return;
              repo.deleteScheduleDownload(scheduleDownload);
              // extract url , path and start new download
              String url = scheduleDownload.getUrl();
              String path = scheduleDownload.getPath();
              Downloader downloader = new Downloader(context);
              downloader.startDownloading(url , path , scheduleDownload.getFileName());
              Toast.makeText(context.getApplicationContext(), R.string.download_started, Toast.LENGTH_SHORT).show();
          }
    }


}
