package com.caldroidsample;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.HashMap;

import hirondelle.date4j.DateTime;

public class CaldroidSampleCustomAdapter extends CaldroidGridAdapter {

	private DBPool db;
	public CaldroidSampleCustomAdapter(Context context, int month, int year,
									   HashMap<String, Object> caldroidData,
									   HashMap<String, Object> extraData) {
		super(context, month, year, caldroidData, extraData);

		db = DBPool.getInstance(context);

		Log.d("TimeData", "Create CustomAdapter");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		long start = SystemClock.currentThreadTimeMillis();
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View cellView = convertView;

		// For reuse
		if (convertView == null) {
			cellView = inflater.inflate(R.layout.custom_cell, null);
		}

//		int topPadding = cellView.getPaddingTop();
//		int leftPadding = cellView.getPaddingLeft();
//		int bottomPadding = cellView.getPaddingBottom();
//		int rightPadding = cellView.getPaddingRight();

		TextView textView = (TextView) cellView.findViewById(R.id.tvDay);
		ImageView imageDay = (ImageView) cellView.findViewById(R.id.imageDay);

		// Get dateTime of this cell
		DateTime dateTime = this.datetimeList.get(position);

		com.ihateflyingbugs.hsmd.data.Date date = new com.ihateflyingbugs.hsmd.data.Date();
//		if(Integer.parseInt(date.get_day()) == 2 && Integer.parseInt(date.get_minute()) < 30){
//			dateTime = dateTime.minusDays(1);
//			dateTime = dateTime.plus(0, 0, 0, Integer.parseInt(date.get_hour()), Integer.parseInt(date.get_minute()), 0, 0, null);
//		} else if (Integer.parseInt(date.get_hour()) < 2){
//			dateTime = dateTime.minusDays(1);
//			dateTime = dateTime.plus(0, 0, 0, Integer.parseInt(date.get_hour()), Integer.parseInt(date.get_minute()), 0, 0, null);
//		} else {
//			dateTime = dateTime.plus(0, 0, 0, Integer.parseInt(date.get_hour()), Integer.parseInt(date.get_minute()), 0, 0, null);
//		}

		Resources resources = context.getResources();

		String dateStr = dateToString(dateTime);

//		if(dateStr.equals(dateToString(getToday()))){ // 데이터베이스에 오늘 날짜의 정보 삽입
//			db.putCalendarData(dateStr);
//		}
		int[] timeData = db.getCalendarTimeData(dateStr);
		int[] wordData = db.getCalendarCountData(date.get_today());

		if (wordData != null && timeData != null) {
			imageDay.setVisibility(View.VISIBLE);
			Log.d("TimeData", dateStr + ", " + timeData[0] + ", " + timeData[1]+", "+timeData[2]+ ", "+ timeData[3]  );
			if (timeData[0] != 0) {
				if ((timeData[0] >= 0) && (timeData[0] < timeData[1])) {
					imageDay.setImageResource(R.drawable.will_month_step1);
				} else if ((timeData[0] >= timeData[1]) && (timeData[0] < (timeData[1] + 10))) {
					imageDay.setImageResource(R.drawable.will_month_step2);
				} else if ((timeData[0] >= (timeData[1] + 10)) && (timeData[0] < (timeData[1] + 30))) {
					imageDay.setImageResource(R.drawable.will_month_step3);
				} else if (timeData[0] >= (timeData[1] + 30)) {
					imageDay.setImageResource(R.drawable.will_month_step4);
				}
			}else{
				int count =timeData[0]+ timeData[2] + timeData[3] + wordData[0];
				if (count !=0) {
					imageDay.setImageResource(R.drawable.will_month_step1);
				}else{
					imageDay.setVisibility(View.INVISIBLE);
				}
			}
		}

		DateTime currentDateTime = getCurrentDay();
		if(Integer.parseInt(date.get_hour()) == 2 && Integer.parseInt(date.get_minute()) < 30){
			currentDateTime = currentDateTime.minusDays(1);
			currentDateTime = currentDateTime.plus(0, 0, 0, Integer.parseInt(date.get_hour()), Integer.parseInt(date.get_minute()), 0, 0, null);
		} else if (Integer.parseInt(date.get_hour()) < 2){
			currentDateTime = currentDateTime.minusDays(1);
			currentDateTime = currentDateTime.plus(0, 0, 0, Integer.parseInt(date.get_hour()), Integer.parseInt(date.get_minute()), 0, 0, null);
		} else {
			currentDateTime = currentDateTime.plus(0, 0, 0, Integer.parseInt(date.get_hour()), Integer.parseInt(date.get_minute()), 0, 0, null);
		}

		DateTime todayDateTime = getToday();
		if(Integer.parseInt(date.get_hour()) == 2 && Integer.parseInt(date.get_minute()) < 30){
			todayDateTime = todayDateTime.minusDays(1);
			todayDateTime = todayDateTime.plus(0, 0, 0, Integer.parseInt(date.get_hour()), Integer.parseInt(date.get_minute()), 0, 0, null);
		} else if (Integer.parseInt(date.get_hour()) < 2){
			todayDateTime = todayDateTime.minusDays(1);
			todayDateTime = todayDateTime.plus(0, 0, 0, Integer.parseInt(date.get_hour()), Integer.parseInt(date.get_minute()), 0, 0, null);
		} else {
			todayDateTime = todayDateTime.plus(0, 0, 0, Integer.parseInt(date.get_hour()), Integer.parseInt(date.get_minute()), 0, 0, null);
		}


		String dateTimeStr = dateToString(dateTime);
		String currentTimeStr = dateToString(currentDateTime);
		String todayStr = dateToString(todayDateTime);



		textView.setTextColor(Color.BLACK);

		// Set color of the dates in previous / next month
		if (dateTime.getMonth() != month) {
			textView.setTextColor(resources
					.getColor(R.color.caldroid_darker_gray));
			//cellView.setVisibility(View.INVISIBLE);
		}

		boolean shouldResetDiabledView = false;
		boolean shouldResetSelectedView = false;

		// Customize for disabled dates and date outside min/max dates
		if ((minDateTime != null && dateTime.lt(minDateTime))
				|| (maxDateTime != null && dateTime.gt(maxDateTime))
				|| (disableDates != null && disableDatesMap
				.containsKey(dateTime))) {

			textView.setTextColor(CaldroidFragment.disabledTextColor);
			if (CaldroidFragment.disabledBackgroundDrawable == -1) {
				//cellView.setBackgroundResource(R.drawable.disable_cell);
			} else {
				textView.setBackgroundResource(CaldroidFragment.disabledBackgroundDrawable);
			}

			if (dateTimeStr.equals(currentTimeStr)) {
				textView.setBackgroundResource(R.drawable.will_pressed_state);
				//cellView.setVisibility(View.GONE);
//				currentTv = cellView;
			}
		} else {
			shouldResetDiabledView = true;
		}

		// Customize for selected dates
		if (selectedDates != null && selectedDatesMap.containsKey(dateTime)) {
			if (CaldroidFragment.selectedBackgroundDrawable != -1) {
				textView.setBackgroundResource(CaldroidFragment.selectedBackgroundDrawable);
			} else {
				textView.setBackgroundColor(resources.getColor(R.color.caldroid_sky_blue));
			}
			textView.setTextColor(CaldroidFragment.selectedTextColor);
		} else {
			shouldResetSelectedView = true;
		}

		if (shouldResetDiabledView && shouldResetSelectedView) {
			// Customize for CurrentDay
			if (dateTimeStr.equals(currentTimeStr) && !(dateTimeStr.equals(todayStr))) {
				cellView.setBackgroundResource(R.drawable.will_pressed_state);
				currentTv = textView;
			}else if(dateTimeStr.equals(currentTimeStr) && dateTimeStr.equals(todayStr)){
				cellView.setBackgroundResource(R.drawable.will_todaytouch_state);
			}else if(dateTimeStr.equals(todayStr)){
				cellView.setBackgroundResource(R.drawable.will_today_state);
			}else {
				cellView.setBackgroundResource(R.drawable.cell_bg);
			}
		}

		textView.setText("" + dateTime.getDay());

		// Somehow after setBackgroundResource, the padding collapse.
		// This is to recover the padding
//		cellView.setPadding(leftPadding, topPadding, rightPadding,
//				bottomPadding);

		// Set custom color if required
//		setCustomResources(dateTime, cellView, tv1);

		GridView.LayoutParams params = new GridView.LayoutParams(LayoutParams.MATCH_PARENT, getDpToPixel(context, 48));

		cellView.setLayoutParams(params);

		Log.d("TimeData2", (SystemClock.currentThreadTimeMillis()-start) + "");
		return cellView;
	}

	public static int getDpToPixel(Context context, int DP) {
		float px = 0;
		try {
			px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP, context.getResources().getDisplayMetrics());
		} catch (Exception e) {

		}
		return (int) px;
	}

	public static String dateToString(DateTime dateTime){

		String year, month, day;

		year = dateTime.getYear().toString();
		if(dateTime.getMonth() < 10){
			month = "0" + dateTime.getMonth().toString();
		}else{
			month = dateTime.getMonth().toString();
		}
		if(dateTime.getDay() < 10){
			day = "0" + dateTime.getDay().toString();
		}else{
			day = dateTime.getDay().toString();
		}
		return year+month+day;
	}
}
