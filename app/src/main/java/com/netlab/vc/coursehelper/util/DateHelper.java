package com.netlab.vc.coursehelper.util;

/**
 * Created by Vc on 2016/12/8.
 */

import java.text.SimpleDateFormat;
import java.util.Date;


public class DateHelper {

    //获取时间表达，精确到日
    public static String getDateAsDay(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy年MM月dd日");
        return formatter.format(date);
    }

    public static String getDateAsHour(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy年MM月dd日 HH:00");
        return formatter.format(date);
    }

    public static String getDateAsMinute(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy年MM月dd日 HH:mm:ss");
        return formatter.format(date);
    }
}
