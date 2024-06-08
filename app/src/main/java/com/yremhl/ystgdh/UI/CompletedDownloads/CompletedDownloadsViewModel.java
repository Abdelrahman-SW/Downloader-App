package com.yremhl.ystgdh.UI.CompletedDownloads;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.yremhl.ystgdh.Database.Repo;
import com.yremhl.ystgdh.Models.CompletedDownload;

import java.util.List;

public class CompletedDownloadsViewModel extends AndroidViewModel {

    private final Repo repo ;
    private final LiveData<List<CompletedDownload>> completedDownloads ;

    public CompletedDownloadsViewModel(@NonNull Application application) {
        super(application);
        repo = new Repo(application);
        completedDownloads = repo.getAllCompletedDownloads();
    }

    public LiveData<List<CompletedDownload>> getAllCompletedDownloads() {
        return completedDownloads;
    }

    public long insertNewCompletedDownload (CompletedDownload completedDownload) {
        return repo.insertNewCompletedDownload(completedDownload);
    }

    void deleteCompletedDownload (CompletedDownload completedDownload) {
        repo.deleteCompletedDownload(completedDownload);
    }

    void deleteAllCompletedDownloads () {
        repo.deleteAllCompletedDownloads();
    }
}
