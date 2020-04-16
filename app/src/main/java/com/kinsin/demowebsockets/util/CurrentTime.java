package com.kinsin.demowebsockets.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by VULCAN on 2018/7/26.
 */

public class CurrentTime {
    public static String getTimes(){
        Calendar calendar=Calendar.getInstance();
        String year="";
        String month="";
        String day="";
        String hour="";
        String minute="";
        String second="";

        year = calendar.get(Calendar.YEAR) +"";
        month = (calendar.get(Calendar.MONTH)+1) +"";
        day = calendar.get(Calendar.DAY_OF_MONTH) +"";
        hour = calendar.get(Calendar.HOUR_OF_DAY) +"";
        minute = calendar.get(Calendar.MINUTE) +"";
        second = calendar.get(Calendar.SECOND) +"";

        if(month.length()==1){
            month = "0"+month;
        }
        if(day.length()==1){
            day = "0"+day;
        }
        if(hour.length()==1){
            hour = "0"+hour;
        }
        if(minute.length()==1){
            minute = "0"+minute;
        }
        if(second.length()==1){
            second = "0"+second;
        }

        String times = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;
//        String times = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second+"分"+calendar.getTimeInMillis();

//        String times = calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH)+" " +
//                ""+calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND)+"分"+calendar.getTimeInMillis();
        return times;
    }


    /**
     * 获得long型时间
     * @return
     */
    public static long getTm(String time){
        long tm = 0;

        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = simpleDateFormat.parse(time);
            tm = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tm;
    }
}
