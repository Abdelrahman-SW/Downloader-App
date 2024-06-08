package com.yremhl.ystgdh.Database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.yremhl.ystgdh.Models.ActiveDownload;
import com.yremhl.ystgdh.Models.CancelledDownload;
import com.yremhl.ystgdh.Models.CompletedDownload;
import com.yremhl.ystgdh.Models.ScheduleDownload;

import java.util.List;

public class Repo {
private final Dao dao ;
private final LiveData<List<ActiveDownload>> activeDownloads ;
    private final LiveData<List<ActiveDownload>> activeDownloadsBySizeAsc;
    private final LiveData<List<ActiveDownload>> activeDownloadsBySizeDesc;
    private final LiveData<List<ActiveDownload>> activeDownloadsByTimeAsc;
    private final LiveData<List<ActiveDownload>> activeDownloadsByNameAsc;
    private final LiveData<List<ActiveDownload>> activeDownloadsByNameDesc;
private final LiveData<List<CompletedDownload>> completedDownloads ;
private final LiveData<List<CancelledDownload>>  cancelledDownloads ;
private final LiveData<List<ScheduleDownload>> scheduleDownloads;

    public Repo(Application application) {
        RoomDatabase database = RoomDatabase.getInstance(application);
        dao = database.Dao();
        activeDownloads = dao.getAllActiveDownloads();
        activeDownloadsByNameAsc = dao.getAllActiveDownloadsOrderByNameAsc();
        activeDownloadsByNameDesc = dao.getAllActiveDownloadsOrderByNameDesc();
        activeDownloadsBySizeAsc = dao.getAllActiveDownloadsOrderBySizeAsc();
        activeDownloadsBySizeDesc = dao.getAllActiveDownloadsOrderBySizeDesc();
        activeDownloadsByTimeAsc = dao.getAllActiveDownloadsOrderByTimeAsc();
        completedDownloads = dao.getAllCompletedDownloads();
        cancelledDownloads = dao.getAllCancelledDownloads();
        scheduleDownloads = dao.getAllScheduleDownloads();
    }

    // Active download :


    public LiveData<List<ActiveDownload>> getActiveDownloadsByNameAsc() {
        return activeDownloadsByNameAsc;
    }

    public LiveData<List<ActiveDownload>> getActiveDownloadsByNameDesc() {
        return activeDownloadsByNameDesc;
    }

    public LiveData<List<ActiveDownload>> getActiveDownloadsBySizeAsc() {
        return activeDownloadsBySizeAsc;
    }

    public LiveData<List<ActiveDownload>> getActiveDownloadsBySizeDesc() {
        return activeDownloadsBySizeDesc;
    }

    public LiveData<List<ActiveDownload>> getActiveDownloadsByTimeAsc() {
        return activeDownloadsByTimeAsc;
    }

    public List<ActiveDownload> getWaitingDownloads() {
        return dao.getWaitingDownloads();
    }
    public int getActiveDownloadsWithGivenStatues (boolean isPaused , boolean isWaiting) {
        return dao.getActiveDownloadsWithGivenStatues(isPaused , isWaiting);
    }
    public int getAllActiveDownloadsWithGivenStatue (boolean isPaused) {
        return dao.getAllActiveDownloadsWithGivenStatue(isPaused);
    }

    public void updateAllActiveDownloadStatue (boolean isPaused) {
        dao.updateAllActiveDownloadStatue(isPaused);
    }

    public LiveData<List<ActiveDownload>> getActiveDownloads() {
        return activeDownloads;
    }


    public long insertNewDownload (ActiveDownload activeDownload) {
        return dao.insertNewDownload(activeDownload);
    }


    public void updateDownload (ActiveDownload activeDownload) {
        dao.updateDownload(activeDownload);
    }

    public void deleteDownload (ActiveDownload activeDownload) {
        dao.deleteDownload(activeDownload);
    }
    public void deleteAllActiveDownloads (){
        dao.deleteAllActiveDownloads();
    }

    public ActiveDownload getActiveDownload (long id) {
        return dao.getActiveDownload(id);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////

    // completed download

    public LiveData<List<CompletedDownload>> getAllCompletedDownloads() {
        return completedDownloads;
    }

    public long insertNewCompletedDownload (CompletedDownload completedDownload) {
        return dao.insertNewCompletedDownload(completedDownload);
    }

    public void deleteCompletedDownload (CompletedDownload completedDownload) {
        dao.deleteCompletedDownload(completedDownload);
    }

    public void deleteAllCompletedDownloads() {
        dao.deleteAllCompletedDownloads();
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    // cancelled download

    public LiveData<List<CancelledDownload>> getCancelledDownloads() {
        return cancelledDownloads;
    }

    public long insertNewCancelledDownload (CancelledDownload cancelledDownload) {
        return dao.insertNewCancelledDownload(cancelledDownload);
    }

    public void deleteCancelledDownload (CancelledDownload cancelledDownload) {
        dao.deleteCancelledDownload(cancelledDownload);
    }

    public void deleteAllCancelledDownloads() {
        dao.deleteAllCancelledDownloads();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////


    // schedule download

    public long insertNewScheduleDownload (ScheduleDownload Download) {
        return dao.insertNewScheduleDownload(Download);
    }

    public void updateScheduleDownload (ScheduleDownload Download){
        dao.updateScheduleDownload(Download);
    }

    public void deleteScheduleDownload (ScheduleDownload Download){
        dao.deleteScheduleDownload(Download);
    }

    public void deleteAllScheduleDownloads () {
        dao.deleteAllScheduleDownloads();
    }

    public ScheduleDownload getScheduleDownload (long id) {
        return dao.getScheduleDownload(id);
    }

    public LiveData<List<ScheduleDownload>> getAllScheduleDownloads() {
        return scheduleDownloads ;
    }


}
