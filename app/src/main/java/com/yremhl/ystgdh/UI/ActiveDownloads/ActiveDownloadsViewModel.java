package com.yremhl.ystgdh.UI.ActiveDownloads;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.yremhl.ystgdh.Database.Repo;
import com.yremhl.ystgdh.Models.ActiveDownload;

import java.util.List;

public class ActiveDownloadsViewModel extends AndroidViewModel {

    private final Repo repo ;
    private final LiveData<List<ActiveDownload>> activeDownloads ;
    private final LiveData<List<ActiveDownload>> activeDownloadsBySizeAsc;
    private final LiveData<List<ActiveDownload>> activeDownloadsBySizeDesc;
    private final LiveData<List<ActiveDownload>> activeDownloadsByTimeAsc;
    private final LiveData<List<ActiveDownload>> activeDownloadsByNameAsc;
    private final LiveData<List<ActiveDownload>> activeDownloadsByNameDesc;

    public ActiveDownloadsViewModel(@NonNull Application application) {
        super(application);
        repo = new Repo(application);
        activeDownloads = repo.getActiveDownloads();
        activeDownloadsByNameAsc = repo.getActiveDownloadsByNameAsc();
        activeDownloadsByNameDesc = repo.getActiveDownloadsByNameDesc();
        activeDownloadsBySizeAsc = repo.getActiveDownloadsBySizeAsc();
        activeDownloadsBySizeDesc = repo.getActiveDownloadsBySizeDesc();
        activeDownloadsByTimeAsc = repo.getActiveDownloadsByTimeAsc();
    }

    public LiveData<List<ActiveDownload>> getActiveDownloadsByTimeAsc() {
        return activeDownloadsByTimeAsc;
    }

    public LiveData<List<ActiveDownload>> getActiveDownloadsBySizeDesc() {
        return activeDownloadsBySizeDesc;
    }

    public LiveData<List<ActiveDownload>> getActiveDownloadsBySizeAsc() {
        return activeDownloadsBySizeAsc;
    }

    public LiveData<List<ActiveDownload>> getActiveDownloadsByNameDesc() {
        return activeDownloadsByNameDesc;
    }

    public LiveData<List<ActiveDownload>> getActiveDownloadsByNameAsc() {
        return activeDownloadsByNameAsc;
    }

    public LiveData<List<ActiveDownload>> getActiveDownloads() {
        return activeDownloads;
    }

    public long insertNewDownload (ActiveDownload activeDownload) {
        return repo.insertNewDownload(activeDownload);
    }


    public void updateDownload (ActiveDownload activeDownload) {
        repo.updateDownload(activeDownload);
    }

    public void deleteDownload (ActiveDownload activeDownload) {
        repo.deleteDownload(activeDownload);
    }
    public void deleteAllActiveDownloads (){
        repo.deleteAllActiveDownloads();
    }

    public ActiveDownload getActiveDownload (long id) {
        return repo.getActiveDownload(id);
    }
}
