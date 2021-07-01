package com.cm.cloud.commons.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    /**
     * 获取时间间隔日期
     * @param date
     * @param day 天数
     * @return
     */
    public static Date getDay(Date date, Integer day){
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.add(Calendar.DAY_OF_MONTH, day);
        return now.getTime();
    }
}
