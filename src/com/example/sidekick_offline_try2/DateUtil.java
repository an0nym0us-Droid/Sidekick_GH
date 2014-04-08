package com.example.sidekick_offline_try2;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A bunch of static helpers for formatting date and time. 
 * We can pass time as String, Calendar object or in milliseconds and
 * get Date object or formatted time as a string respectively.
 */
class DateUtil {
	
	public static Date formatTime(String time) throws ParseException{
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    	Date dd = null;
		dd =  dateFormat.parse(time);
		return dd;
    }
	
	public static long formatTime(String time,int type) throws ParseException{
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    	Date dd = null;
		dd =  dateFormat.parse(time);
		return dd.getTime();
    }
    
    public static String formatTime(Calendar cal){
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dd = new Date(cal.getTimeInMillis());
		return dateFormat.format(dd);
    }
    
    public static String formatTime(long time){
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		 Date dd = new Date(time);
		 String formattedTime = dateFormat.format(dd);
		 return formattedTime;
		 
    }
}