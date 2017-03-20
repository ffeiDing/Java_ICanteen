package com.netlab.vc.coursehelper.util;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vc on 2017/1/3.
 */

public class DateFormatter {
    public static String format(Long date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        return sdf.format(date);
    }
    public static String format(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        return sdf.format(date);
    }
}
