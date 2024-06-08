package com.yremhl.ystgdh.Downloader;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yremhl.ystgdh.Utilites.Utilities;
import com.tonyodev.fetch2okhttp.OkHttpDownloader;

import java.util.Set;

import okhttp3.OkHttpClient;

public class HttpDownloader extends OkHttpDownloader {
    Context context ;


    public HttpDownloader(@Nullable OkHttpClient okHttpClient , Context context) {
        super(okHttpClient);
        this.context = context ;
    }

    @Nullable
    @Override
    public Integer getFileSlicingCount(@NonNull ServerRequest request, long contentLength) {
        Utilities.PrintLog("getFileSlicingCount");
        Utilities.PrintLog("count " + Utilities.getFileSlicingCount(context));
        return Utilities.getFileSlicingCount(context); //Return the number of threads/slices. No need to call super.
    }

    @NonNull
    @Override
    public FileDownloaderType getRequestFileDownloaderType(@NonNull ServerRequest request, @NonNull Set<? extends FileDownloaderType> supportedFileDownloaderTypes) {
        return FileDownloaderType.PARALLEL; //For chunk downloading
    }


}
