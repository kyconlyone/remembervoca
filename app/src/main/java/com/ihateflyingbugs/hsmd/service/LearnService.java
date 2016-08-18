package com.ihateflyingbugs.hsmd.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MildangDate;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.roomorama.caldroid.CalendarHelper;

import hirondelle.date4j.DateTime;

public class LearnService extends Service {

	Date date;
	DBPool db;
	public static SharedPreferences mPreference;
	SharedPreferences.Editor editor;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		db = DBPool.getInstance(getApplicationContext());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		mPreference = getSharedPreferences(MainActivitys.preName,
				MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE | MODE_MULTI_PROCESS);

		MildangDate date = new MildangDate();
		DateTime dateTime = CalendarHelper
				.convertDateToDateTime(new java.util.Date());

		String tempMonth, tempDay;

		// 현재 날짜
		String current_date = date.get_year() + date.get_Month()
				+ date.get_day();

		// 저장된 날짜
		String saved_date = db.getDate();

		int tempInteger = 1;

		if (current_date.equals(saved_date)) {
			tempInteger = 0;
		} else {
			// 저장된 날짜와 현재 날짜가 다른 경우
			//db.putDay_Of_Study();
			while (true) {

				if (dateTime.minusDays(tempInteger).getMonth() > 9) {
					tempMonth = dateTime.minusDays(tempInteger).getMonth()
							.toString();
				} else {
					tempMonth = "0"
							+ dateTime.minusDays(tempInteger).getMonth()
							.toString();
				}

				if (dateTime.minusDays(tempInteger).getDay() > 9) {
					tempDay = dateTime.minusDays(tempInteger).getDay()
							.toString();
				} else {
					tempDay = "0"
							+ dateTime.minusDays(tempInteger).getDay()
							.toString();
				}

				// 현재 날짜로부터 x날 전
				String tempDate = dateTime.minusDays(tempInteger++).getYear()
						.toString()
						+ tempMonth + tempDay;

				// 저장된 날짜와 같으면 break
				if (tempDate.equals(saved_date)) {
					break;
				}
			}
		}

		Log.d("LearnService", Integer.toString(tempInteger) + "days");

		return startId;

	}
}
