package com.yremhl.ystgdh.Database;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.yremhl.ystgdh.Downloader.DownloaderHelper;
import com.yremhl.ystgdh.Models.ActiveDownload;
import com.yremhl.ystgdh.Models.ScheduleDownload;
import com.yremhl.ystgdh.Utilites.AlarmHelper;
import com.yremhl.ystgdh.Utilites.Utilities;
import com.tonyodev.fetch2.Download;

import java.util.Calendar;

public class DatabaseHelper {

    public static long insertNewActiveDownloadOnDatabase(Context context , Repo repo , String url , String actualPath , String path , String fileName) {
        ActiveDownload download = new ActiveDownload();
        download.setUrl(url);
        download.setPath(path);
        download.setFileName(fileName);
        download.setTimeStart(0);
        download.setStatues(DownloaderHelper.DownloadStatues.WAITING_NETWORK.name());
        download.setPaused(false);
        download.setNum_of_retries(0);
        download.setActualPath(actualPath);
        download.setDownloadId(-1);
        download.setWaiting(Utilities.shouldWaiting(context , repo));
        String type = Utilities.getMimeType(url) ;
        if (type == null) type = "video/mp4";
        Utilities.PrintLog("Type " + type);
        download.setType(type);
        return repo.insertNewDownload(download);
    }


    public static ActiveDownload getActiveDownload(@NonNull Download download, Repo repo) {
        long id = download.getRequest().getIdentifier();
        return repo.getActiveDownload(id);
    }


    public static void insertNewScheduleDownload(Application application , Calendar calendar , String url , String path , String filename) {
        ScheduleDownload scheduleDownload = new ScheduleDownload();
        scheduleDownload.setUrl(url);
        scheduleDownload.setCalendar(calendar);
        scheduleDownload.setPath(path);
        scheduleDownload.setType(Utilities.getMimeType(url));
        scheduleDownload.setFileName(filename);
        Repo repo = new Repo(application);
        long id = repo.insertNewScheduleDownload(scheduleDownload);
        scheduleDownload.setId(id);
        AlarmHelper.prepareAlarm(application.getApplicationContext() , scheduleDownload);
    }
}
