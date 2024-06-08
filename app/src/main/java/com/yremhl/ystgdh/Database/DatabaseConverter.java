package com.yremhl.ystgdh.Database;

import androidx.room.TypeConverter;

import java.util.Calendar;

public class DatabaseConverter {
    @TypeConverter
    public static long toLong (Calendar calendar){
        return calendar.getTimeInMillis() ;
    }
    @TypeConverter
    public static Calendar toCalender(long TimeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(TimeInMillis);
        return calendar ;
    }
}
