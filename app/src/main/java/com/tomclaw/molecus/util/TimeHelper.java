package com.tomclaw.molecus.util;

import android.annotation.SuppressLint;
import android.content.Context;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by solkin on 05/05/14.
 */
@EBean(scope = EBean.Scope.Singleton)
public class TimeHelper {

    @RootContext
    Context context;

    private static final DateFormat DATE_INSTANCE = SimpleDateFormat.getDateInstance();
    @SuppressLint("SimpleDateFormat")
    private static final DateFormat DATE_FORMAT_SHORT = new SimpleDateFormat("dd.MM.yy");
    @SuppressLint("SimpleDateFormat")
    private static final DateFormat TIME_FORMAT_12 = new SimpleDateFormat("h:mm a");
    @SuppressLint("SimpleDateFormat")
    private static final DateFormat TIME_FORMAT_24 = new SimpleDateFormat("HH:mm");
    @SuppressLint("SimpleDateFormat")
    private static final DateFormat SIMPLE_TIME_FORMAT_SECONDS = new SimpleDateFormat("mm:ss");

    private DateFormat getTimeFormat() {
        return android.text.format.DateFormat.is24HourFormat(context) ? TIME_FORMAT_24 : TIME_FORMAT_12;
    }

    public String getShortFormattedDate(long timestamp) {
        return DATE_FORMAT_SHORT.format(timestamp);
    }

    public String getFormattedDate(long timestamp) {
        return DATE_INSTANCE.format(timestamp);
    }

    public String getFormattedTime(long timestamp) {
        return getTimeFormat().format(timestamp);
    }

    public static int getYears(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() - timeStamp);
        return calendar.get(Calendar.YEAR) - 1970;
    }

    public static Calendar clearTimes(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    public String getTime(long timestamp) {
        return SIMPLE_TIME_FORMAT_SECONDS.format(timestamp);
    }
}
