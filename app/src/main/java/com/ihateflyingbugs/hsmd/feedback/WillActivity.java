package com.ihateflyingbugs.hsmd.feedback;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.caldroidsample.CaldroidSampleCustomFragment;
import com.caldroidsample.day.CaldroidDayFragment;
import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.data.MildangDate;
import com.ihateflyingbugs.hsmd.login.TimeClass;
import com.ihateflyingbugs.hsmd.popup.SetGoalTimePopup;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.roomorama.caldroid.CaldroidListener;
import com.roomorama.caldroid.CalendarHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hirondelle.date4j.DateTime;

public class WillActivity extends FragmentActivity {

	private boolean undo = false;
	private CaldroidSampleCustomFragment caldroidFragment;
	private CaldroidDayFragment caldroidDayFragment;

	private TextView weekTv;
	private TextView monthTv;

	private TextView tvMyAverage;
	private TextView tvUserGroupAverage;
	private TextView tvGradeAverage;
	private TextView tvGoalTime;
	private TextView tvGoalTimeTwo;
	private TextView tvAccumulateUseTime;
	private TextView tv_grade_group;
	private TextView tv_user_group;

	private TextView will_pushTv;
	private TextView block_kakaoTv;

	private ToggleButton block_kakaoBtn;
	private ToggleButton will_pushBtn;
	private DBPool db;

	private Switch willPushSwitch;
	private Switch kakaoPushSwitch;

	SharedPreferences pushPreference;
	SharedPreferences.Editor pushEditor;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(MainValue.isLowDensity(getApplicationContext())){
			setContentView(R.layout.activity_will_small);
		}else{
			setContentView(R.layout.activity_will);
		}

		setActionBar();

		pushPreference = getSharedPreferences(Config.PREFS_NAME, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		pushEditor = pushPreference.edit();
		db = DBPool.getInstance(getApplicationContext());



		weekTv = (TextView) findViewById(R.id.week_average);
		monthTv = (TextView) findViewById(R.id.month_average);
		tvMyAverage = (TextView) findViewById(R.id.tv_my_average);
		tvUserGroupAverage = (TextView) findViewById(R.id.tv_user_group_average);
		tvGradeAverage = (TextView) findViewById(R.id.tv_grade_average);
		tvGoalTime = (TextView) findViewById(R.id.goal_time_min_Tv);
		tvAccumulateUseTime = (TextView) findViewById(R.id.accumulate_use_time_countTv);
		tvGoalTimeTwo = (TextView) findViewById(R.id.tv_current_goal_time);
		will_pushTv = (TextView) findViewById(R.id.will_pushTv);
		block_kakaoTv = (TextView) findViewById(R.id.block_kakaoTv);
		// willPushSwitch = (Switch) findViewById(R.id.will_pushBtn);
		// kakaoPushSwitch = (Switch) findViewById(R.id.block_kakaoBtn);
		block_kakaoBtn = (ToggleButton) findViewById(R.id.block_kakaoBtn);
		will_pushBtn = (ToggleButton) findViewById(R.id.will_pushBtn);
		tv_grade_group = (TextView) findViewById(R.id.tv_grade_group);
		tv_user_group = (TextView) findViewById(R.id.tv_user_group);

		// block_kakaoBtn.setChecked(pushPreference.getBoolean(MainValue.GpreKakaoPush,
		// false));
		// will_pushBtn.setChecked(pushPreference.getBoolean(MainValue.GpreWillPush,
		// false));





		final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");

		ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();

		caldroidFragment = new CaldroidSampleCustomFragment();
		caldroidDayFragment = new CaldroidDayFragment();


		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction t = fragmentManager.beginTransaction();
		t.replace(R.id.calendar1, caldroidFragment);
		t.replace(R.id.day1, caldroidDayFragment);
		t.commit();
		fragmentManager.executePendingTransactions();

		caldroidDayFragment.setCaldoridFragment(caldroidFragment);

		int accumulateStudyTime = db.getAccumulateStudyTime();

		int hour = accumulateStudyTime / 60;
		int minute = accumulateStudyTime % 60;
		tvAccumulateUseTime.setText(hour + "h " + minute + "min");

		Button bt_will_settime = (Button) findViewById(R.id.bt_will_settime);
		bt_will_settime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				FlurryAgent.logEvent("WillActivity:Click_Select_Time");
				startActivityForResult(new Intent(WillActivity.this, SetGoalTimePopup.class), 5511);
			}
		});

		final CaldroidListener caldroidListener = new CaldroidListener() {
			public CaldroidGridAdapter adapter;

			protected void getAdapter() {
				adapter = caldroidFragment.getDatePagerAdapters().get(0);
			}

			@Override
			public void onSelectDate(Date date, View view, int type) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);

				DateTime currentDay = adapter.getCurrentDay();

				int currentTimeMillis = (currentDay.getYear() * 12) + currentDay.getMonth();
				int nextTimeMillis = calendar.get(Calendar.YEAR) * 12 + calendar.get(Calendar.MONTH) + 1;
				Log.d("timezone", "current " + currentTimeMillis);
				Log.d("timezone", "next " + nextTimeMillis);

				if (currentTimeMillis == nextTimeMillis) {
					adapter.setCurrentDay(date);
				} else if (currentTimeMillis > nextTimeMillis) {
					adapter.setCurrentDay(date);
					if (type == 0 || type == 2) {
						caldroidFragment.prevMonth();
					}
				} else {
					adapter.setCurrentDay(date);
					if (type == 0 || type == 2) {
						caldroidFragment.nextMonth();
					}
				}
				Log.d("a", adapter.getCurrentDay().toString());

				DateTime currnetTime = CalendarHelper.convertDateToDateTime(date);

				String dateStr = db.dateTimeToString(currnetTime);

				int averageMonth = db.getMonthUseTime(dateStr);

				int minute = averageMonth;
				int hour = minute / 60;
				minute = minute % 60;
				caldroidFragment.setMonthlyUseTime(hour + "h " + minute + "min");

				MildangDate mildangDate = new MildangDate();

				tvMyAverage.setText(Integer.toString(db.getWeekAverageTime(mildangDate.get_today())) + "분");

				caldroidDayFragment.setDays(adapter.getCurrentDay());
				caldroidFragment.refreshView();
			}

			@Override
			public void onChangeMonth(int month) {
			}

			@Override
			public void onLongClickDate(Date date, View view) {
			}

			@Override
			public void onCaldroidViewCreated() {
				getAdapter();
				adapter.setCurrentDay(new Date());

				String dateStr = db.dateTimeToString(adapter.getCurrentDay());

				int averageMonth = db.getMonthUseTime(dateStr);

				int minute = averageMonth;
				int hour = minute / 60;
				minute = minute % 60;

				Log.d("month", averageMonth + " " + minute + " " + " ");
				caldroidFragment.setMonthlyUseTime(hour + "h " + minute + "min");
			}

		};

		caldroidFragment.setCaldroidListener(caldroidListener);



		weekTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				com.ihateflyingbugs.hsmd.data.MildangDate mildangDate = new com.ihateflyingbugs.hsmd.data.MildangDate();
				tvMyAverage.setText(db.getWeekAverageTime(mildangDate.get_today()) + "분");

				// LinearLayout.LayoutParams weekParams = new
				// LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				// LayoutParams.WRAP_CONTENT);
				// weekParams.leftMargin = getDpToPixel(getApplicationContext(),
				// 5);
				// LinearLayout.LayoutParams monthParams = new
				// LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				// LayoutParams.WRAP_CONTENT);
				// monthParams.rightMargin =
				// getDpToPixel(getApplicationContext(), 5);
				//
				// weekTv.setLayoutParams(weekParams);
				// monthTv.setLayoutParams(monthParams);

				weekTv.setTextColor(Color.parseColor("#EA4E37"));
				monthTv.setTextColor(Color.parseColor("#555555"));

				weekTv.setBackgroundResource(R.drawable.will_avg_btn_on_left);
				monthTv.setBackgroundResource(R.drawable.will_avg_btn_right);
				tvUserGroupAverage.setText((int)(Config.averageUseTimeAsAgeAndGrade / 60) +"분");
				tvGradeAverage.setText((int)(Config.averageUseTimeAsAge / 60)+"분");


				FlurryAgent.logEvent("WillActivity:Click_Show_WeekTime");

			}
		});

		monthTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				com.ihateflyingbugs.hsmd.data.MildangDate mildangDate = new com.ihateflyingbugs.hsmd.data.MildangDate();
				int monthValue[] = db.getMonthAverageTime(mildangDate.get_today());
				try {

					tvMyAverage.setText(monthValue[0]/monthValue[1] + "분");
				} catch (ArithmeticException e) {
					// TODO: handle exception
					tvMyAverage.setText(0 + "분");
				}

				// LinearLayout.LayoutParams weekParams = new
				// LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				// LayoutParams.WRAP_CONTENT);
				// weekParams.leftMargin = getDpToPixel(getApplicationContext(),
				// 5);
				// LinearLayout.LayoutParams monthParams = new
				// LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				// LayoutParams.WRAP_CONTENT);
				// monthParams.rightMargin =
				// getDpToPixel(getApplicationContext(), 5);
				//
				// weekTv.setLayoutParams(weekParams);
				// monthTv.setLayoutParams(monthParams);

				weekTv.setTextColor(Color.parseColor("#555555"));
				monthTv.setTextColor(Color.parseColor("#EA4E37"));

				weekTv.setBackgroundResource(R.drawable.will_avg_btn_left);
				monthTv.setBackgroundResource(R.drawable.will_avg_btn_on_right);
				tvUserGroupAverage.setText((int)(Config.averageUseTimeAsAgeAndGradeMonth / 60) +"분");
				tvGradeAverage.setText((int)(Config.averageUseTimeAsAgeMonth / 60)+"분");

				FlurryAgent.logEvent("WillActivity:Click_Show_MonthTime");
			}
		});


		// block_kakaoBtn.setChecked(checked);
		// block_kakaoBtn.setOnCheckedChangeListener(new
		// OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView, boolean
		// isChecked) {
		// // TODO Auto-generated method stub
		//
		// if (isChecked) {
		// pushEditor.putBoolean(MainValue.GpreKakaoPush, true);
		// pushEditor.commit();
		// Intent intent = new Intent(WillActivity.this, OneButtonPopUp.class);
		// intent.putExtra("caller", 13);
		// startActivity(intent);
		// } else {
		// pushEditor.putBoolean(MainValue.GpreKakaoPush, false);
		// pushEditor.commit();
		// Intent intent = new Intent(WillActivity.this, OneButtonPopUp.class);
		// intent.putExtra("caller", 12);
		// startActivity(intent);
		// }
		// }
		// });

		// will_pushBtn.setOnCheckedChangeListener(new OnCheckedChangeListener()
		// {
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView, boolean
		// isChecked) {
		// // TODO Auto-generated method stub
		//
		// if (isChecked) {
		// pushEditor.putBoolean(MainValue.GpreWillPush, true);
		// pushEditor.commit();
		//
		// Intent intent = new Intent(WillActivity.this, OneButtonPopUp.class);
		// intent.putExtra("caller", 11);
		// startActivity(intent);
		// } else {
		// pushEditor.putBoolean(MainValue.GpreWillPush, false);
		// pushEditor.commit();
		// Intent intent = new Intent(WillActivity.this, OneButtonPopUp.class);
		// intent.putExtra("caller", 10);
		// startActivity(intent);
		// }
		// }
		// });

		will_pushBtn = (ToggleButton) findViewById(R.id.will_pushBtn);

		if (pushPreference.getBoolean(MainValue.GpreWillPush, true)) {
			will_pushBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.onbutton));
			will_pushBtn.setChecked(true);
		} else {
			will_pushBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.offbutton));
			will_pushBtn.setChecked(false);
		}

		will_pushBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (pushPreference.getBoolean(MainValue.GpreWillPush, false)) {
					will_pushBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.offbutton));
					pushEditor.putBoolean(MainValue.GpreWillPush, false);
					pushEditor.commit();
					FlurryAgent.logEvent("WillActivity:Click_Off_Push");
				} else {
					will_pushBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.onbutton));
					pushEditor.putBoolean(MainValue.GpreWillPush, true);
					pushEditor.commit();
					FlurryAgent.logEvent("WillActivity:Click_On_Push");
				}

			}
		});

		block_kakaoBtn = (ToggleButton) findViewById(R.id.block_kakaoBtn);

		if (pushPreference.getBoolean(MainValue.GpreKakaoPush, false)) {
			block_kakaoBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.onbutton));
		} else {
			block_kakaoBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.offbutton));
		}

		block_kakaoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (pushPreference.getBoolean(MainValue.GpreKakaoPush, false)) {
					block_kakaoBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.offbutton));
					pushEditor.putBoolean(MainValue.GpreKakaoPush, false);
					pushEditor.commit();
					FlurryAgent.logEvent("WillActivity:Click_Off_Kakao");
				} else {
					block_kakaoBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.onbutton));
					pushEditor.putBoolean(MainValue.GpreKakaoPush, true);
					pushEditor.commit();
					FlurryAgent.logEvent("WillActivity:Click_On_Kakao");
				}

			}
		});

	}

	public static int getDpToPixel(Context context, int DP) {
		float px = 0;
		try {
			px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP,
					context.getResources().getDisplayMetrics());
		} catch (Exception e) {

		}
		return (int) px;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		pushPreference = getSharedPreferences(Config.PREFS_NAME, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		tvGoalTimeTwo.setText("현재 목표시간 " + db.getGoalTime() + "분");
		tvGoalTime.setText("매일 " + db.getGoalTime() + "분");
		MildangDate date = new MildangDate();
		db.putCalendarData(date.get_today());
		Log.d("MildangDate", "WillActivity_onResume");


		MildangDate today = new MildangDate();


		String birth = pushPreference.getString(MainValue.GpreBirth, "900000");

		int sum = 0;

		if (Integer.valueOf((String) birth.subSequence(0, 2)) > 50) {
			sum = 1900;
			int years = Integer.valueOf((String) birth.subSequence(0, 2)) - 1;
			sum += years;
		} else {
			sum = 2000;
			int years = Integer.valueOf((String) birth.subSequence(0, 2)) - 1;
			sum += years;
		}

		if(birth.subSequence(2, 4).equals("01")||birth.subSequence(2, 4).equals("02")){
			sum--;
		}

		int age = Integer.valueOf(TimeClass.getYear()) - sum;

		String students_year;

		int grade = pushPreference.getInt(MainValue.GpreSatGrade, 5);

		if (age <= 16) {
			students_year = "중학생";
		} else if (age == 17) {
			students_year = "고1";
		} else if (age == 18) {
			students_year = "고2";
		} else if (age == 19) {
			students_year = "고3";
		} else if (age > 19) {
			students_year = "재수생";
		} else{
			students_year = "안얄랴줌";
		}



		tv_user_group.setText(students_year + " " + grade + "등급\n단어장 사용자");
		tv_grade_group.setText(students_year + "\n전체");
		tvUserGroupAverage.setText((int)(Config.averageUseTimeAsAgeAndGrade / 60) +"분");
		tvGradeAverage.setText((int)(Config.averageUseTimeAsAge / 60)+"분");

		String coach_str = null;
		String my_group = null, my_rank = null, my_percent = null, group_average_time = null, my_average_time = null, difference_str = null;

		if (Config.averageUseTimeOfUser == -1.0) {
			my_average_time = "측정 중";
		} else {
			if (Config.averageUseTimeOfUser < 60) {
				my_average_time = Integer.toString((int)(Config.averageUseTimeOfUser))
						+ "초";
			} else {
				my_average_time = Integer.toString((int)(Config.averageUseTimeOfUser / 60))
						+ "분 "
						+ Integer.toString((int)(Config.averageUseTimeOfUser % 60))
						+ "초";
			}
		}

		if (Config.averageUseTimeAsAge == -1.0) {
			group_average_time = "측정 중";
		} else {
			if (Config.averageUseTimeAsAge < 60) {
				group_average_time = Integer.toString((int)(Config.averageUseTimeAsAge)) + "초";
			} else {
				group_average_time = Integer
						.toString((int)(Config.averageUseTimeAsAge / 60))
						+ "분 "
						+ Integer.toString((int)(Config.averageUseTimeAsAge % 60))
						+ "초";
			}
		}

		double difference = Config.averageUseTimeAsAge - Config.averageUseTimeOfUser;
		if(Config.averageUseTimeAsAge == -1.0 || Config.averageUseTimeOfUser == -1.0){
			difference_str = "측정 중";
		} else {
			if (difference < 60) {
				difference_str = Integer.toString((int)(Math.abs(difference))) + "초";
			} else {
				difference_str = Integer
						.toString((int)(Math.abs(difference) / 60))
						+ "분 "
						+ Integer.toString((int)(Math.abs(difference) % 60))
						+ "초";
			}
		}


		if(Config.averageUseTimeAsAge > Config.averageUseTimeOfUser){
			coach_str = "보다 " + difference_str + " 적습니다.";
		} else if (Config.averageUseTimeAsAge > Config.averageUseTimeOfUser){
			coach_str = "보다 " + difference_str + " 많습니다.";
		} else {
			coach_str = "와 같습니다. ";
		}

		if (Config.theNumberOfPeople == -1) {
			my_group = "측정 중";
		} else {
			my_group = Integer.toString((int)(Config.theNumberOfPeople));
		}

		if (Config.userRankAsUseTime == -1 ) {
			my_rank = "측정 중";

			my_percent = "측정 중";
		} else {
			my_rank = Integer.toString((int)(Config.userRankAsUseTime));

			my_percent = String.format("%.2f", ((double)(Config.userRankAsUseTime) / (double)(Config.theNumberOfPeople)) * 100);
		}


		MildangDate current = new MildangDate();
		//		DateTime currnetTime = CalendarHelper.convertDateToDateTime(current);

		Log.d("DateTimeToString", current.get_today());

		tvMyAverage.setText(db.getWeekAverageTime(current.get_today()) + "분");
	}


	/**
	 * Save current states of the Caldroid here
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		recreate();
	}

	//
	ActionBar mActionBar;
	View mCustomActionBarView;

	LinearLayout mActionBar_Layout;
	LinearLayout mBack_Layout;
	TextView mTitle_TextView;
	//

	void setActionBar() {
		mActionBar = getActionBar();
		mCustomActionBarView = getLayoutInflater().inflate(R.layout.setting_action_bar, null);
		mActionBar.setCustomView(mCustomActionBarView, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		mActionBar_Layout = (LinearLayout)mCustomActionBarView.findViewById(R.id.layout_actionbar);
		mActionBar_Layout.setBackgroundColor(Color.parseColor("#ea4f37"));

		mBack_Layout = (LinearLayout)mCustomActionBarView.findViewById(R.id.layout_actionbar_back);
		mBack_Layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				WillActivity.this.finish();
			}
		});

		mTitle_TextView = (TextView)mCustomActionBarView.findViewById(R.id.text_actionbar_title);
		mTitle_TextView.setText("의지");
	}


	String starttime;
	String startdate;
	MildangDate date = new MildangDate();
	Map<String, String> articleParams;

	public void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = ((MildangDate) date).get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("WillActivity", articleParams);
	}

	public void onStop()
	{
		super.onStop();
		FlurryAgent.endTimedEvent("WillActivity");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
	}
}
