package com.icegps.autodrive.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 111 on 2018/3/14.
 */

public class DateFormat {
    public static String time2Date(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd  hh:mm:ss");
        String format = simpleDateFormat.format(new Date(time));
        return format;
    }
}
