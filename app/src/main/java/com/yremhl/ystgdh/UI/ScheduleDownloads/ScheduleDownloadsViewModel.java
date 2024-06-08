package com.yremhl.ystgdh.UI.ScheduleDownloads;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.yremhl.ystgdh.Database.Repo;
import com.yremhl.ystgdh.Models.ScheduleDownload;

import java.util.List;

public class ScheduleDownloadsViewModel extends AndroidViewModel {

    private final Repo repo ;
    private final LiveData<List<ScheduleDownload>> scheduleDownloads ;

    public ScheduleDownloadsViewModel(@NonNull Application application) {
        super(application);
        repo = new Repo(application);
        scheduleDownloads = repo.getAllScheduleDownloads();
    }

    public long insertNewScheduleDownload (ScheduleDownload Download) {
        return repo.insertNewScheduleDownload(Download);
    }

    public void updateScheduleDownload (ScheduleDownload Download){
        repo.updateScheduleDownload(Download);
    }

    public void deleteScheduleDownload (ScheduleDownload Download){
        repo.deleteScheduleDownload(Download);
    }

    public void deleteAllScheduleDownloads () {
        repo.deleteAllScheduleDownloads();
    }

    public ScheduleDownload getScheduleDownload (long id) {
        return repo.getScheduleDownload(id);
    }

    public LiveData<List<ScheduleDownload>> getAllScheduleDownloads() {
        return scheduleDownloads ;
    }
}
