package com.rye.catcher.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * CreateBy ShuQin
 * at 2020/1/28
 */
public class DateTimeUtil {
    private static final SimpleDateFormat FORMAT=new SimpleDateFormat("yy-MM-dd", Locale.ENGLISH);

    /**
     * 返回一个
     * @param date
     * @return
     */
    public static String getSampleDate(Date date){
       return FORMAT.format(date);
    }
}
