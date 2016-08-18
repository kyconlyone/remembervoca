package com.ihateflyingbugs.hsmd.login;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.util.Log;

public class TimeClass {
	
	public static String getYear() {  
		try {  
			Calendar cale = Calendar.getInstance();

			return String.valueOf(cale.get(Calendar.YEAR));  
		} catch (Exception e) {  

			return "";  
		}  
	}


	public static String getMonth() {  
		try {  
			Calendar cale = Calendar.getInstance(); 
			java.text.DecimalFormat df = new java.text.DecimalFormat();  
			df.applyPattern("00;00");  

			return df.format((cale.get(Calendar.MONTH) + 1));  
		} catch (Exception e) {  
			return "";  
		}  
	}


	public static String getDay() {
		try {  
			GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+09:00"), Locale.KOREA);
			cal.setTime(new Date());
			Date d = cal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			String today = sdf.format(d);
			return today;  
		} catch (Exception e) {  
			return "";  
		}  
	}
	
	
}
