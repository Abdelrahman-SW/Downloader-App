package com.yremhl.ystgdh.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.TypeConverters;

import com.yremhl.ystgdh.Models.ActiveDownload;
import com.yremhl.ystgdh.Models.CancelledDownload;
import com.yremhl.ystgdh.Models.CompletedDownload;
import com.yremhl.ystgdh.Models.ScheduleDownload;

@Database(entities = {ActiveDownload.class , CompletedDownload.class , CancelledDownload.class , ScheduleDownload.class} , version = 1)
@TypeConverters(DatabaseConverter.class)
public abstract class RoomDatabase extends androidx.room.RoomDatabase {
    private static RoomDatabase room ; //singleton
    public abstract Dao Dao() ; // required

    public static synchronized RoomDatabase getInstance (Context context) {
        if (room == null) {
            room = Room.databaseBuilder(context , RoomDatabase.class , "DownloadsDatabase")
                    .fallbackToDestructiveMigration() // re-create the database When the database version on the device does not match the latest schema version
                    // and the Migrations that would migrate old database schemas to the latest schema version are not found.
                    //.addCallback(callback)
                    .allowMainThreadQueries()
                    // for testing only
                    // As we should run any actions on the database on background thread to avoid anr
                    .build();
        }
        return room ;
    }

}
