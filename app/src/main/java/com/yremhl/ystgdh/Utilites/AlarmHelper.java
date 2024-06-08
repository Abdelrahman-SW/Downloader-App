package com.yremhl.ystgdh.Utilites;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import com.yremhl.ystgdh.Broadcasts.ScheduleDownloadBroadcast;
import com.yremhl.ystgdh.Models.ScheduleDownload;

public class AlarmHelper {

    public static void prepareAlarm(Context context , ScheduleDownload download) {
        Intent intent = new Intent(context.getApplicationContext(), ScheduleDownloadBroadcast.class) ;
        intent.putExtra(Constant.SCHEDULE_DOWNLOAD_ID , download.getId());
        intent.setAction(Constant.SCHEDULE_DOWNLOAD_ACTION);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (alarmManager != null)
                // setAlarmClock ( AlarmClockInfo (TriggerTime , ShowIntent) , PendingIntent )
                alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(download.getCalendar().getTimeInMillis(), null), GetPendingIntentOfAlarm(context , intent , download.getId()));
        }
        else {
            // before api level 21 use setExact with RTC_WAKEUP :
            alarmManager.setExact(AlarmManager.RTC_WAKEUP , download.getCalendar().getTimeInMillis() , GetPendingIntentOfAlarm(context , intent , download.getId()));
        }
        Log.e("ab_do", "request code on create alarm : " + download.getId());

    }
    public static void CancelAlarm (Context context , long downloadID) {
        Intent intent = new Intent(context.getApplicationContext(), ScheduleDownloadBroadcast.class) ;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = GetPendingIntentOfAlarm( context , intent  , downloadID);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
        Log.e("ab_do" , "request code on cancel alarm : " + downloadID);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private static PendingIntent GetPendingIntentOfAlarm(Context context , Intent intent , long downloadId) {
        return PendingIntent.getBroadcast(context.getApplicationContext(),
                (int) downloadId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
