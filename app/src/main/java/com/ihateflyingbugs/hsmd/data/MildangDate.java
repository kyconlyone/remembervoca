package com.ihateflyingbugs.hsmd.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.util.Log;


public class MildangDate {
	Calendar cal;
	int year;
	int month;
	int day ;
	int hour;
	int minute;
	int second;

	public MildangDate(){
		
		cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("Asia/Rangoon"));
		
	}

	public String get_date(){
		String result = "";
		return result = year + "-" + month + "-" + day + " " + hour + ":" + minute;
	}

	public String get_year(){
		return ""+cal.get(Calendar.YEAR);
	}
	public String get_Month(){
		String Month_S;
		int month = cal.get(Calendar.MONTH) + 1;
		if(month>=10){
			Month_S = ""+month;
		}else{
			Month_S= "0"+ month;
		}
		return Month_S;
	}
	public String get_day(){

		String day_S;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		if(day>=10){
			day_S = ""+day;
		}else{
			day_S= "0"+ day;
		}
		return day_S;
	}

	public String get_hour(){
		String time_S;
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if(hour>=10){
			time_S = ""+hour;
		}else{
			time_S= "0"+ hour;
		}
		return time_S;
	}
	public String get_minute(){
		String minute_S;
		int minute = cal.get(Calendar.MINUTE);
		if(minute>=10){
			minute_S = ""+minute;
		}else{
			minute_S= "0"+ minute;
		}
		return minute_S;
	}
	public String get_second(){
		String second_S;
		int second = cal.get(Calendar.SECOND);
		if(second>=10){
			second_S = ""+second;
		}else{
			second_S= "0"+ second;
		}
		return second_S;
	}
	
	public String get_yesterday(){
		Calendar cal2 = new GregorianCalendar(TimeZone.getTimeZone("GMT+6:30"));
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Rangoon"));
		cal2.add(Calendar.DAY_OF_MONTH, -1);
		return dateFormat.format(cal2.getTime());
	}
	
	public String get_currentTime(){
		return ""+get_year()+"-"+get_Month()+"-"+get_day()+" "+get_hour()+":"+get_minute()+":"+get_second(); 
	}
	
	
	public String get_today() {
		Calendar cal2 = new GregorianCalendar(TimeZone.getTimeZone("GMT+6:30"));
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Rangoon"));
		cal2.add(Calendar.DAY_OF_MONTH, 0);
		return dateFormat.format(cal2.getTime());
	}
	
	public String get_nextday() {
		Calendar cal2 = new GregorianCalendar(TimeZone.getTimeZone("GMT+6:30"));
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Rangoon"));
		cal2.add(Calendar.DAY_OF_MONTH, 1);
		return dateFormat.format(cal2.getTime());
	}
	
	public int getMonth() {  
          int month = cal.get(Calendar.MONTH)+1;
          return month;
    }

}
