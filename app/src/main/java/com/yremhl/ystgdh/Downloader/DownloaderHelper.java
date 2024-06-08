package com.yremhl.ystgdh.Downloader;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yremhl.ystgdh.Database.DatabaseHelper;
import com.yremhl.ystgdh.Database.Repo;
import com.yremhl.ystgdh.Models.ActiveDownload;
import com.yremhl.ystgdh.Models.CompletedDownload;
import com.yremhl.ystgdh.R;
import com.yremhl.ystgdh.Utilites.Constant;
import com.yremhl.ystgdh.Utilites.Utilities;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.EnqueueAction;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;

public class DownloaderHelper {

    private static Repo repo ;
    public static Fetch fetch ;
    private static FetchConfiguration getFetchConfiguration(Context context) {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(Utilities.enableRetryOnNetworkGain(context))
                .build();
        final HttpDownloader customDownloader = new HttpDownloader(okHttpClient , context);
        return new FetchConfiguration.Builder(context)
                .setDownloadConcurrentLimit(100)
                .enableRetryOnNetworkGain(Utilities.enableRetryOnNetworkGain(context))
                .setHttpDownloader(customDownloader)
                .build();
    }


    public enum DownloadStatues {
        ACTIVE(),
        PAUSED()  ,
        WAITING_NETWORK(),
        ERROR();
        DownloadStatues() {
        }
    }


    private static FetchListener getFetchListener(Context context , Repo repo) {
        return new FetchListener() {
            @Override
            public void onError(@NonNull Download download, @NonNull Error error, @Nullable Throwable throwable) {
                Log.i("ab_do" , "onError " + error.toString());
                updateDownloadStatues(context ,download, DownloadStatues.ERROR);
            }

            @Override
            public void onWaitingNetwork(@NonNull Download download) {
                Log.i("ab_do" , "onWaitingNetwork");
                updateDownloadStatues(context , download, DownloadStatues.WAITING_NETWORK);
            }

            @Override
            public void onStarted(@NonNull Download download, @NonNull List<? extends DownloadBlock> list, int i) {
                ActiveDownload activeDownload = DatabaseHelper.getActiveDownload(download, repo);
                if (activeDownload == null) return;
                initDownload(download, activeDownload);
                Log.i("ab_do" , "onStarted " + download.getRequest().getIdentifier());
            }

            @Override
            public void onResumed(@NonNull Download download) {
                updateDownloadStatues(context , download, DownloadStatues.WAITING_NETWORK);
                Log.i("ab_do" , "onResumed");
            }

            @Override
            public void onRemoved(@NonNull Download download) {
                  Utilities.checkIfShouldStartWaitingDownload(context, repo);
                  Log.i("ab_do" , "onRemoved");
                  ActiveDownload activeDownload = DatabaseHelper.getActiveDownload(download, repo);
                  if (activeDownload == null) {
                      return;
                  }
                  Utilities.cancelActiveDownload(context  , activeDownload);
                  fetch.remove(download.getId());
                  fetch.remove(download.getRequest().getId());
            }

            @Override
            public void onQueued(@NonNull Download download, boolean b) {
                Log.i("ab_do" , "onQueued");
                updateDownloadStatues(context ,download, DownloadStatues.WAITING_NETWORK);
            }

            @Override
            public void onProgress(@NonNull Download download, long l, long l1) {
                ActiveDownload activeDownload = DatabaseHelper.getActiveDownload(download , repo);
                  if (activeDownload == null) return;
                  updateDownloadProgress(download, activeDownload);
                  if (Utilities.getDownloadShouldBeShown(context) == -1 || Utilities.getDownloadShouldBeShown(context) == download.getRequest().getIdentifier() )  {
                      Utilities.startDownloadService(context , download.getProgress() , activeDownload.getFileName());
                      Utilities.updateDownloadShouldBeShown(context , download.getRequest().getIdentifier());
                  }
            }

            @Override
            public void onPaused(@NonNull Download download) {
                Utilities.checkIfShouldStartWaitingDownload(context, repo);
                updateDownloadStatues(context ,download, DownloadStatues.PAUSED);
                ActiveDownload activeDownload = DatabaseHelper.getActiveDownload(download , repo);
                if (activeDownload == null) return;
                if (Utilities.iFStopService(context)) return;
                if ((Utilities.getDownloadShouldBeShown(context) == -1 )|| Utilities.getDownloadShouldBeShown(context) == download.getRequest().getIdentifier() )  {
                    Utilities.startDownloadService(context , -1 , activeDownload.getFileName());
                    Utilities.updateDownloadShouldBeShown(context , download.getRequest().getIdentifier());
                }
                Log.i("ab_do" , "onPaused");
            }


            @Override
            public void onDownloadBlockUpdated(@NonNull Download download, @NonNull DownloadBlock downloadBlock, int i) {
                //Log.i("ab_do" , "onDownloadBlockUpdated");
            }

            @Override
            public void onDeleted(@NonNull Download download) {
                Log.i("ab_do" , "onDeleted");
            }

            @Override
            public void onCompleted(@NonNull Download download) {
                Utilities.checkIfShouldStartWaitingDownload(context, repo);
                Utilities.vibrate(context);
                Log.i("ab_do" , "onCompleted");
                ActiveDownload activeDownload = DatabaseHelper.getActiveDownload(download, repo);
                if (activeDownload == null) return;
                activeDownload.setTimeFinished(System.currentTimeMillis());
                repo.deleteDownload(activeDownload);
                CompletedDownload completedDownload = Utilities.getCompletedDownloadFromActive(context , activeDownload);
                completedDownload.setRequestID(download.getRequest().getId());
                repo.insertNewCompletedDownload(completedDownload);
                Toast.makeText(context.getApplicationContext(), context.getString(R.string.done), Toast.LENGTH_SHORT).show();
                Utilities.checkIfShouldStopService(context);
                if (Utilities.getDownloadShouldBeShown(context) == download.getRequest().getIdentifier()) {
                    // this is the current download shown in the service
                    Utilities.updateDownloadShouldBeShown(context , -1);
                }

            }

            @Override
            public void onCancelled(@NonNull Download download) {
                Log.i("ab_do" , "onCancelled");
            }

            @Override
            public void onAdded(@NonNull Download download) {
                Log.i("ab_do" , "onAdded");
            }

        };
    }

    private static void initDownload(@NonNull Download download, ActiveDownload activeDownload) {
        activeDownload.setDownloadId(download.getId());
        if (activeDownload.getTimeStart() == 0) // first time start
        activeDownload.setTimeStart(System.currentTimeMillis());
        activeDownload.setSize(download.getTotal());
        activeDownload.setStatues(DownloadStatues.WAITING_NETWORK.name());
        repo.updateDownload(activeDownload);
    }

    private static void updateDownloadProgress(@NonNull Download download, ActiveDownload activeDownload) {
        activeDownload.setDownloaded(download.getDownloaded());
        activeDownload.setProgress(download.getProgress());
        activeDownload.setEta(download.getEtaInMilliSeconds());
        activeDownload.setSpeed(download.getDownloadedBytesPerSecond());
        activeDownload.setNum_of_retries(0);
        activeDownload.setStatues(DownloadStatues.ACTIVE.name());
        repo.updateDownload(activeDownload);
    }

    private static void updateDownloadStatues(Context context , @NonNull Download download, DownloadStatues statue) {
        ActiveDownload activeDownload = DatabaseHelper.getActiveDownload(download , repo);
        if (activeDownload == null) return;
        activeDownload.setStatues(statue.name());
        if (statue == DownloadStatues.ERROR) {
            handleDownloadError(context, download, activeDownload);
        }
        repo.updateDownload(activeDownload);
    }

    private static void handleDownloadError(Context context, @NonNull Download download, ActiveDownload activeDownload) {
        if (activeDownload == null) return;
        if (fetch == null) {fetch = getFetch(context);}
        activeDownload.setNum_of_retries(activeDownload.getNum_of_retries() + 1);
        if (activeDownload.getNum_of_retries() > Constant.MAX_NUMBERS_OF_RETRY) {
            // reach max number of retries ! .. the download should be removed :(
            fetch.remove(download.getId());
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.error) , Toast.LENGTH_SHORT).show();
        }
        else {
            // retry
            fetch.retry(download.getId());
        }
    }


    public static Fetch getFetch(Context context) {
        if (fetch == null || repo == null) {
            repo = new Repo((Application) context.getApplicationContext());
            fetch = Fetch.Impl.getInstance(getFetchConfiguration(context));
            fetch.setDownloadConcurrentLimit(100);
            fetch.addListener(getFetchListener(context , repo));
        }
        return fetch ;
    }

    public static Request getFetchRequest (Context context , long id , String url , String path)  {
        Request request = new Request(url, path) ;
        request.setIdentifier(id);
        request.setPriority(Priority.HIGH);
        request.setNetworkType(Utilities.getNetworkType(context));
        request.setEnqueueAction(EnqueueAction.DO_NOT_ENQUEUE_IF_EXISTING);
        return request ;
    }

    public static void removeRequestByID(Context context , int id) {
        if (fetch == null) getFetch(context);
        fetch.remove(id);
    }

    public static void enqueueFetch(Context context , Request request) {
        if (fetch == null) getFetch(context);
        fetch.enqueue(request, updatedRequest -> {
            //Request was successfully enqueued for download.
            Log.i("ab_do" , "enqueued " + request.getFile());
        }, error -> {
            //An error occurred enqueuing the request.
            if (error.getValue() == Error.REQUEST_WITH_ID_ALREADY_EXIST.getValue()
        || error.getValue() == Error.REQUEST_WITH_FILE_PATH_ALREADY_EXIST.getValue()) {
                Toast.makeText(context, context.getString(R.string.error_download), Toast.LENGTH_SHORT).show();
                ActiveDownload activeDownload = repo.getActiveDownload(request.getIdentifier()) ;
                repo.deleteDownload(activeDownload);
                Utilities.updateDownloadShouldBeShown(context , -1);
                Utilities.checkIfShouldStopService(context);
                Log.i("ab_do" , "error yyyyyyyyyyyyyy " + error.name());
            }
            else enqueueFetch(context, request);
            Log.i("ab_do" , "error name " + error.name());
            Log.i("ab_do" , "error num " + error.getValue());
            if (error.getThrowable() != null) {
                Log.i("ab_do", "error throw " + error.getThrowable().getMessage());
                Log.i("ab_do", "error throw2 " + error.getThrowable().getLocalizedMessage());
                Log.i("ab_do", "error throw3 " + error.getThrowable().toString());
                if (error.getThrowable().getCause() != null)
                Log.i("ab_do", "error throw4 " + error.getThrowable().getCause().getMessage());
            }
            if (error.getHttpResponse() != null)
            Log.i("ab_do" , "error resp " + error.getHttpResponse());
            Log.i("ab_do" , "error on enqueee " + error.toString() + request.getFile());
        });
    }


}
