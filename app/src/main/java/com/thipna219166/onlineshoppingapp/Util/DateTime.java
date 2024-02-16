package com.thipna219166.onlineshoppingapp.Util;

import android.annotation.SuppressLint;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
public class DateTime {
    public static String getDatetimeNow(){
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return currentDate.format(calendar.getTime());
    }

    public static String getDateTime(Timestamp ts){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ts.getTime());
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return dateFormat.format(calendar.getTime());
    }
}
