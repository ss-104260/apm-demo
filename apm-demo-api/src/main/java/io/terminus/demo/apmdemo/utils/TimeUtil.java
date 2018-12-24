package io.terminus.demo.apmdemo.utils;

import java.util.Date;

public class TimeUtil {
    private final static java.text.DateFormat simpleDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public static String format(Date time) {
        return simpleDateFormat.format(time);
    }
    public static String nowText() {
        return format(new Date());
    }
}
