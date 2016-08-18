package com.ihateflyingbugs.hsmd.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Date {
	Calendar cal;
	int year;
	int month;
	int day;
	int hour;
	int minute;
	int second;

	public Date() {
		cal = Calendar.getInstance();
	}

	public String get_date() {
		String result = "";
		return result = year + "-" + month + "-" + day + " " + hour + ":"
				+ minute;
	}

	public String get_year() {
		return "" + cal.get(Calendar.YEAR);
	}

	public String get_Month() {
		String Month_S;
		int month = cal.get(Calendar.MONTH) + 1;
		if (month >= 10) {
			Month_S = "" + month;
		} else {
			Month_S = "0" + month;
		}
		return Month_S;
	}

	public String get_day() {

		String day_S;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		if (day >= 10) {
			day_S = "" + day;
		} else {
			day_S = "0" + day;
		}
		return day_S;
	}

	public String[] get_x_day_later(int day) {

		int current_day = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, current_day + day);

		String[] day_later = { get_year(), get_Month(), get_day() };

		cal = Calendar.getInstance();
		return day_later;
	}

	public int get_hour_int() {

		return cal.get(Calendar.HOUR_OF_DAY);

	}

	public int get_min_int() {
		return cal.get(Calendar.MINUTE);

	}

	public String get_hour() {
		String time_S;
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if (hour >= 10) {
			time_S = "" + hour;
		} else {
			time_S = "0" + hour;
		}
		return time_S;
	}

	public String get_minute() {
		String minute_S;
		int minute = cal.get(Calendar.MINUTE);
		if (minute >= 10) {
			minute_S = "" + minute;
		} else {
			minute_S = "0" + minute;
		}
		return minute_S;
	}

	public String get_second() {
		String second_S;
		int second = cal.get(Calendar.SECOND);
		if (second >= 10) {
			second_S = "" + second;
		} else {
			second_S = "0" + second;
		}
		return second_S;
	}

	public String get_yesterday() {
		Calendar cal = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		cal.add(Calendar.DATE, -1);
		return dateFormat.format(cal.getTime());
	}

	public String get_currentTime() {
		cal = Calendar.getInstance();
		return "" + get_year() + "-" + get_Month() + "-" + get_day() + " "
				+ get_hour() + ":" + get_minute() + ":" + get_second();
	}

	public String get_today() {
		
		/*
		 * ���Ŀ� 2:30�� �� �����ؾ���
		 */
		Calendar cal = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		cal.add(Calendar.DATE, 0);
		return dateFormat.format(cal.getTime());
	}

	public static int getMonth() {
		Calendar cale = Calendar.getInstance();
		int month = cale.get(Calendar.MONTH) + 1;
		return month;
	
	

	}
	
	public String get_today_bar() {
		
		/*
		 * ���Ŀ� 2:30�� �� �����ؾ���
		 */
		Calendar cal = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		cal.add(Calendar.DATE, 0);
		return dateFormat.format(cal.getTime());
	}

}
