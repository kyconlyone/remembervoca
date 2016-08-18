package com.caldroidsample.day;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.VOCAconfig;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.MildangDate;

import java.util.Calendar;

import hirondelle.date4j.DateTime;

public class DayFragment extends Fragment {
	private TextView dayTextview;
	private TextView newStudyWord;
	private TextView reviewWord;
	private TextView goalTime;
	private TextView studyTime;
	private ImageView studyImage;

	Calendar mThisDayCalendar;
	DateTime currentDay;
	DBPool db;
	MildangDate date;

	public DateTime getDateTime(){
		return currentDay;
	}

	public DayFragment() {
	}


	@SuppressLint("ValidFragment")
	public DayFragment(DateTime dateTime) {
		currentDay = dateTime;
		db = DBPool.getInstance(VOCAconfig.context);
		date = new MildangDate();

		Log.d("DayText", "create : " + getDate());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		Log.d("timedata3", "Begin");

		View view = inflater.inflate(R.layout.day_view, container, false);
		dayTextview = (TextView) view.findViewById(R.id.tvCurrentDay);
		newStudyWord = (TextView) view.findViewById(R.id.new_study_word);
		reviewWord = (TextView) view.findViewById(R.id.review_word);
		goalTime = (TextView) view.findViewById(R.id.real_use_time_countTv);
		studyTime = (TextView) view.findViewById(R.id.all_use_time_countTv);
		studyImage = (ImageView) view.findViewById(R.id.study_gaugeImg);
		Log.d("DayText", getDate());

		String year, month, day;

		year = currentDay.getYear().toString();
		if(currentDay.getMonth() < 10){
			month = "0" + currentDay.getMonth().toString();
		}else{
			month = currentDay.getMonth().toString();
		}
		if(currentDay.getDay() < 10){
			day = "0" + currentDay.getDay().toString();
		}else{
			day = currentDay.getDay().toString();
		}

		int[] timeData;
		int[] wordData;
		timeData = db.getCalendarTimeData(date.get_today());
		wordData = db.getCalendarCountData(date.get_today());

		//Log.d("dbdata", timeData.toString() + " " + wordData.toString());


		dayTextview.setText(getDate());

		if (wordData != null && timeData != null) {
			// Log.d("TimeData", " " + timeData[0] + " " + timeData[1]);
			Log.d("dbdata", date.get_today());
			Log.d("dbdata", ""+timeData[0]);
			Log.d("dbdata", ""+wordData[1]);

			newStudyWord.setText("" + wordData[0]);
			//reviewWord.setText(wordData[1] + "/" + wordData[2]);
			reviewWord.setText(""+wordData[1]);
			studyTime.setText(timeData[0] + "");
			goalTime.setText("/" + timeData[1] + "분");
			if (timeData[0] != 0) {
				if ((timeData[0] >= 0) && (timeData[0] < timeData[1])) {
					studyImage.setImageResource(R.drawable.will_day_step1);
				} else if ((timeData[0] >= timeData[1]) && (timeData[0] < (timeData[1] + 10))) {
					studyImage.setImageResource(R.drawable.will_day_step2);
				} else if ((timeData[0] >= (timeData[1] + 10)) && (timeData[0] < (timeData[1] + 30))) {
					studyImage.setImageResource(R.drawable.will_day_step3);
				} else if (timeData[0] >= (timeData[1] + 30)) {
					studyImage.setImageResource(R.drawable.will_day_step4);
				}
			}else{
				int count =timeData[0]+ timeData[2] + timeData[3] + wordData[0];

				if (count !=0) {
					studyImage.setImageResource(R.drawable.will_day_step1);
				}else{
					studyImage.setImageResource(R.drawable.will_day_step0);
				}
			}
		}else {
			newStudyWord.setText("" + "0");
			reviewWord.setText("0");
			studyTime.setText("0");
			goalTime.setText("/" + "0" + "분");

		}

		Log.d("timedata3", "ENd");
		return view;
	}

	public void setDays(DateTime dateTime){
		Log.d("timedata3", "Begin");
		currentDay = dateTime;
		dayTextview.setText(getDate());

		String year, month, day;

		year = currentDay.getYear().toString();
		if(currentDay.getMonth() < 10){
			month = "0" + currentDay.getMonth().toString();
		}else{
			month = currentDay.getMonth().toString();
		}
		if(currentDay.getDay() < 10){
			day = "0" + currentDay.getDay().toString();
		}else{
			day = currentDay.getDay().toString();
		}

		int[] timeData;
		int[] wordData;
		timeData = db.getCalendarTimeData(year+month+day);
		wordData = db.getCalendarCountData(year+month+day);

		//Log.d("dbdata", timeData.toString() + " " + wordData.toString());


		dayTextview.setText(getDate());

		if (wordData != null && timeData != null) {
			// Log.d("TimeData", " " + timeData[0] + " " + timeData[1]);
			Log.d("dbdata", date.get_today());
			Log.d("dbdata", ""+timeData[0]);
			Log.d("dbdata", ""+wordData[1]);

			newStudyWord.setText("" + wordData[0]);
			reviewWord.setText(""+wordData[1]);
			studyTime.setText(timeData[0] + "");
			goalTime.setText("/" + timeData[1] + "분");
			if (timeData[0] != 0) {
				if ((timeData[0] >= 0) && (timeData[0] < timeData[1])) {
					studyImage.setImageResource(R.drawable.will_day_step1);
				} else if ((timeData[0] >= timeData[1]) && (timeData[0] < (timeData[1] + 10))) {
					studyImage.setImageResource(R.drawable.will_day_step2);
				} else if ((timeData[0] >= (timeData[1] + 10)) && (timeData[0] < (timeData[1] + 30))) {
					studyImage.setImageResource(R.drawable.will_day_step3);
				} else if (timeData[0] >= (timeData[1] + 30)) {
					studyImage.setImageResource(R.drawable.will_day_step4);
				}
			}else{
				int count =timeData[0]+ timeData[2] + timeData[3]+wordData[0];
				if (count !=0) {
					studyImage.setImageResource(R.drawable.will_day_step1);
				}else{
					studyImage.setImageResource(R.drawable.will_day_step0);
				}
			}
		}else {
			newStudyWord.setText("" + "0");
			reviewWord.setText("0");
			studyTime.setText("0");
			goalTime.setText("/" + "0" + "분");
			studyImage.setImageResource(R.drawable.will_day_step0);
		}
		Log.d("timedata3", "ENd");
	}

//	public void plusDay(){
//		currentDay = currentDay.plusDays(1);
//		dayTextview.setText(getDate());
//	}
//	
//	public void minusDay(){
//		currentDay = currentDay.minusDays(1);
//		dayTextview.setText(getDate());
//	}

	public String getDate() {
		int year = currentDay.getYear();
		int month = currentDay.getMonth();
		int day = currentDay.getDay();
		return year + "." + month + "." + day;
	}

}