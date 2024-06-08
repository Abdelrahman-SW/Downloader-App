package com.yremhl.ystgdh.UI.CancelledDownloads;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.yremhl.ystgdh.Database.Repo;
import com.yremhl.ystgdh.Models.CancelledDownload;

import java.util.List;

public class CancelledDownloadViewModel extends AndroidViewModel {

    private final Repo repo;
    private final LiveData<List<CancelledDownload>> cancelledDownloads;

    public CancelledDownloadViewModel(@NonNull Application application) {
        super(application);
        repo = new Repo(application);
        cancelledDownloads = repo.getCancelledDownloads();
    }

    public LiveData<List<CancelledDownload>> getCancelledDownloads() {
        return cancelledDownloads;
    }

    public long insertNewCancelledDownload (CancelledDownload cancelledDownload) {
        return repo.insertNewCancelledDownload(cancelledDownload);
    }

    public void deleteCancelledDownload (CancelledDownload cancelledDownload) {
        repo.deleteCancelledDownload(cancelledDownload);
    }

    public void deleteAllCancelledDownloads() {
        repo.deleteAllCancelledDownloads();
    }

}
