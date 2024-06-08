package com.yremhl.ystgdh.Downloader;

import android.app.Application;
import android.content.Context;

import com.yremhl.ystgdh.Database.DatabaseHelper;
import com.yremhl.ystgdh.Database.Repo;
import com.yremhl.ystgdh.Models.ActiveDownload;
import com.yremhl.ystgdh.Utilites.Utilities;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.Request;

import java.util.Calendar;

public class Downloader {

    private final Context context;
    private final Repo repo ;
    public Downloader(Context context) {
        this.context = context ;
        repo = new Repo((Application) context.getApplicationContext());
    }

    public void cancelDownload (ActiveDownload activeDownload) {
        Utilities.updateStopServiceValue(context , false);
        Fetch fetch = DownloaderHelper.getFetch(context);
        Utilities.cancelActiveDownload(context , activeDownload);
        fetch.remove(activeDownload.getDownloadId());
    }


    public void cancelAllDownload () {
        Utilities.updateStopServiceValue(context , false);
        Fetch fetch = DownloaderHelper.getFetch(context);
        Utilities.cancelAllDownloads(context);
        fetch.removeAll();
    }


    public void resumeAllDownload () {
        Utilities.updateStopServiceValue(context , false);
        Fetch fetch = DownloaderHelper.getFetch(context);
        fetch.resumeAll();
        repo.updateAllActiveDownloadStatue(false);
    }

    public void pauseAllDownload () {
        Utilities.updateStopServiceValue(context , false);
        Fetch fetch = DownloaderHelper.getFetch(context);
        fetch.pauseAll();
        repo.updateAllActiveDownloadStatue(true);
    }



    public void resumeDownload (int id) {
        Utilities.updateStopServiceValue(context , false);
        Fetch fetch = DownloaderHelper.getFetch(context);
        fetch.resume(id);
    }

    public void pauseDownload (int id) {
        Utilities.updateStopServiceValue(context , false);
        Fetch fetch = DownloaderHelper.getFetch(context);
        fetch.pause(id);
    }

    public void startDownloading(String url , String path , String fileName) {
        Utilities.updateStopServiceValue(context , false);
        Utilities.PrintLog("startDownloading " + url + " " + path + " " + fileName);
        // 1- save download file into active downloads database
        // 2- retrieve id of the new download
        String filePath = path + "/" + fileName;
        long id = DatabaseHelper.insertNewActiveDownloadOnDatabase(context , new Repo((Application) context.getApplicationContext()) , url , path , filePath , fileName);
        if (!repo.getActiveDownload(id).isWaiting()) {
            // start download only if the download is not should be waiting
            startDownloadProcess(url, fileName, filePath, id);
        }
    }

    private void startDownloadProcess(String url, String fileName, String filePath, long id) {
        Request request = DownloaderHelper.getFetchRequest(context, id, url, filePath);
        // 3- call fetch.enqueue (getFetchRequest(id , url , path))
        DownloaderHelper.enqueueFetch(context, request);
        // start foreground service with progress 0
        Utilities.updateDownloadShouldBeShown(context, id);
        Utilities.startDownloadService(context, 0, fileName);
    }

    public void startDownloadWaitingDownload (long id , String url , String path , String fileName) {
        Utilities.updateStopServiceValue(context , false);
        startDownloadProcess(url, fileName, path, id);
    }

    public void scheduleDownload(Application application , Calendar calendar , String url , String path , String filename) {
        Utilities.updateStopServiceValue(context , false);
         DatabaseHelper.insertNewScheduleDownload(application , calendar , url , path , filename);
    }
}
