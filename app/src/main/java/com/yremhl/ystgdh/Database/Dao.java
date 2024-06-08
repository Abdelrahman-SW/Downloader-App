package com.yremhl.ystgdh.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.yremhl.ystgdh.Models.ActiveDownload;
import com.yremhl.ystgdh.Models.CancelledDownload;
import com.yremhl.ystgdh.Models.CompletedDownload;
import com.yremhl.ystgdh.Models.ScheduleDownload;

import java.util.List;

@androidx.room.Dao
public interface Dao {
    // Active Downloads
    @Insert
    long insertNewDownload (ActiveDownload activeDownload);
    @Update
    void updateDownload (ActiveDownload activeDownload);
    @Delete
    void deleteDownload (ActiveDownload activeDownload);
    @Query("Delete from ActiveDownload")
    void deleteAllActiveDownloads ();
    @Query("Select * from ActiveDownload where id = :id")
    ActiveDownload getActiveDownload (long id);
    @Query("select * from ActiveDownload order by id desc")
    LiveData<List<ActiveDownload>> getAllActiveDownloads();
    @Query("select * from ActiveDownload order by size Asc")
    LiveData<List<ActiveDownload>> getAllActiveDownloadsOrderBySizeAsc();
    @Query("select * from ActiveDownload order by size desc")
    LiveData<List<ActiveDownload>> getAllActiveDownloadsOrderBySizeDesc();
    @Query("select * from ActiveDownload order by fileName Asc")
    LiveData<List<ActiveDownload>> getAllActiveDownloadsOrderByNameAsc();
    @Query("select * from ActiveDownload order by fileName desc")
    LiveData<List<ActiveDownload>> getAllActiveDownloadsOrderByNameDesc();
    @Query("select * from ActiveDownload order by id Asc")
    LiveData<List<ActiveDownload>> getAllActiveDownloadsOrderByTimeAsc();
    @Query("update ActiveDownload set isPaused = :isPaused where isWaiting = 0")
    void updateAllActiveDownloadStatue (boolean isPaused);
    @Query("Select COUNT(*) FROM ActiveDownload where isPaused = :isPaused  || isWaiting = 1")
    int getAllActiveDownloadsWithGivenStatue (boolean isPaused);
    @Query("Select COUNT(*) from ActiveDownload where isPaused = :isPaused and isWaiting = :isWaiting")
    int getActiveDownloadsWithGivenStatues (boolean isPaused , boolean isWaiting);
    @Query("select * from ActiveDownload where isWaiting = 1")
    List<ActiveDownload> getWaitingDownloads();


    // Completed Downloads
    @Insert
    long insertNewCompletedDownload (CompletedDownload completedDownload);
    @Delete
    void deleteCompletedDownload (CompletedDownload completedDownload);
    @Query("Delete from CompletedDownload")
    void deleteAllCompletedDownloads ();
    @Query("select * from CompletedDownload")
    LiveData<List<CompletedDownload>> getAllCompletedDownloads();


    //cancelled Downloads
    @Insert
    long insertNewCancelledDownload (CancelledDownload cancelledDownload);
    @Delete
    void deleteCancelledDownload (CancelledDownload cancelledDownload);
    @Query("Delete from CancelledDownload")
    void deleteAllCancelledDownloads ();
    @Query("select * from CancelledDownload")
    LiveData<List<CancelledDownload>> getAllCancelledDownloads();


    //schedule download
    @Insert
    long insertNewScheduleDownload (ScheduleDownload Download);
    @Update
    void updateScheduleDownload (ScheduleDownload Download);
    @Delete
    void deleteScheduleDownload (ScheduleDownload Download);
    @Query("Delete from ScheduleDownload")
    void deleteAllScheduleDownloads ();
    @Query("Select * from ScheduleDownload where id = :id")
    ScheduleDownload getScheduleDownload (long id);
    @Query("select * from ScheduleDownload")
    LiveData<List<ScheduleDownload>> getAllScheduleDownloads();

}
