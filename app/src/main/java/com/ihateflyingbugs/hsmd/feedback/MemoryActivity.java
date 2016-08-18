package com.ihateflyingbugs.hsmd.feedback;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.GetReviewTime;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.graph.PieGraph;
import com.ihateflyingbugs.hsmd.graph.PieSlice;
import com.ihateflyingbugs.hsmd.login.TimeClass;
import com.roomorama.caldroid.CalendarHelper;

import java.util.HashMap;
import java.util.Map;

import hirondelle.date4j.DateTime;

public class MemoryActivity extends Activity {


	SharedPreferences pushPreference;
	SharedPreferences.Editor pushEditor;
	DBPool db;
	Handler handler;



	// Memory Count
	LinearLayout mReviewCount_Layout;
	TextView mReviewCount_TextView;
	View mReviewUnderBar_View;
	LinearLayout mRelaxCount_Layout;
	TextView mRelaxCount_TextView;
	View mRelaxUnderBar_View;
	TextView mMemoryCountExplain_TextView;
	PieGraph mMemoryCount_PieGraph;
	TextView mTotalMemoryCount_TextView;

	// Forget Graph
	ImageView mForgetCurveGraph_ImageView;
	ImageView mForgetcurveDefault_ImageView;
	TextView mForgetRate_TextView;
	ImageButton mForgetCurveInfo_Button;

	TextView mForget_CreateDate_TextView;
	TextView mForget_CreateCount_TextView;
	TextView mForget_RemainDate_TextView;

	// Bottom
	ToggleButton mMemoryAlarm_ToggleButton;
	LinearLayout mMemorizeWord_Layout;



	int percentage;


	GetReviewTime getReviewTime;


	LayoutParams graphParams, tempParam;

	int count = 0;


	Integer f_count[];


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memory);


		setActionBar();


		pushPreference = getSharedPreferences(Config.PREFS_NAME, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		pushEditor = pushPreference.edit();
		handler = new Handler();
		db = DBPool.getInstance(this);


		//..
		mReviewCount_Layout = (LinearLayout) findViewById(R.id.layout_feedback_memory_review);
		mReviewCount_TextView = (TextView) findViewById(R.id.text_feedback_memory_review_count);
		mReviewUnderBar_View = (View) findViewById(R.id.view_feedback_memory_review_underbar);
		mRelaxCount_Layout = (LinearLayout) findViewById(R.id.layout_feedback_memory_relax);
		mRelaxCount_TextView = (TextView) findViewById(R.id.text_feedback_memory_relax_count);
		mRelaxUnderBar_View = (View) findViewById(R.id.view_feedback_memory_relax_underbar);
		mMemoryCountExplain_TextView = (TextView) findViewById(R.id.text_feedback_memory_explain);
		mMemoryCount_PieGraph = (PieGraph)findViewById(R.id.graph_feedback_memory_piegraph);
		mTotalMemoryCount_TextView = (TextView) findViewById(R.id.text_feedback_memory_total_count);
		//...
		mForgetCurveGraph_ImageView = (ImageView) findViewById(R.id.img_feedback_memory_forgetgraph);
		mForgetcurveDefault_ImageView = (ImageView) findViewById(R.id.img_feedback_memory_forgetgraph_default);
		mForgetRate_TextView = (TextView) findViewById(R.id.text_feedback_memory_forgetrate);
		mForgetCurveInfo_Button = (ImageButton) findViewById(R.id.btn_feedback_memory_forgetgraph_info);
		mForget_CreateDate_TextView = (TextView) findViewById(R.id.text_feedback_memory_forget_createdate);
		mForget_CreateCount_TextView = (TextView) findViewById(R.id.text_feedback_memory_forget_createcount);
		mForget_RemainDate_TextView = (TextView) findViewById(R.id.text_feedback_memory_forget_remaindate);
		//....
		mMemoryAlarm_ToggleButton = (ToggleButton) findViewById(R.id.toggle_feedback_memory_alarm);
		mMemorizeWord_Layout = (LinearLayout) findViewById(R.id.layout_feedback_memory_memorizeword);


		//
		f_count = db.getForgetCount();

		setNoticeLayout();
		setMemoryCountLayout();
		setForgetCurveLayout();

		//....
		if (pushPreference.getBoolean(MainValue.GpreMemoryPush, true)) {
			mMemoryAlarm_ToggleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.onbutton));
			mMemoryAlarm_ToggleButton.setChecked(true);
		}
		else {
			mMemoryAlarm_ToggleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.offbutton));
			mMemoryAlarm_ToggleButton.setChecked(false);
		}
		mMemoryAlarm_ToggleButton.setOnClickListener(ClickListener);
		mMemorizeWord_Layout.setOnClickListener(ClickListener);
	}

	void setNoticeLayout() {
		String birth = pushPreference.getString(MainValue.GpreBirth, "900101");
		int sum;
		if (Integer.valueOf((String) birth.subSequence(0, 2)) > 50) {
			sum = 1900;
			int years = Integer.valueOf((String) birth.subSequence(0, 2)) - 1;
			sum += years;
		} else {
			sum = 2000;
			int years = Integer.valueOf((String) birth.subSequence(0, 2)) - 1;
			sum += years;
		}

		int age = Integer.valueOf(TimeClass.getYear()) - sum;
		String students_year = "고등학생";
		if (age <= 16) {// && age > 13) {
			students_year = "중학생";
		} else if (age == 17) {
			students_year = "고1";
		} else if (age == 18) {
			students_year = "고2";
		} else if (age == 19) {
			students_year = "고3";
		} else if (age > 19) {
			students_year = "재수생";
		}

	}

	void setMemoryCountLayout() {
		if ((f_count[0] + f_count[1]) < 0) {
			mReviewCount_TextView.setText(0);
		} else {
			mReviewCount_TextView.setText(Integer.toString(f_count[0] + f_count[1]));
		}
		mReviewCount_TextView.setText(Integer.toString(db.getMforget()));
		mRelaxCount_TextView.setText(Integer.toString(db.getPRememberCount()));
		mMemoryCountExplain_TextView.setText(pushPreference.getString(MainValue.GpreName, "이름없음") + "님의 망각 곡선 분석 결과\n지금 복습하면 망각하지 않을 단어 " + f_count[1] + "개와"
				+ "\n복습을 제때 하지 않아 이미 망각해버린 단어 " + f_count[0] + "개가 추출 되었습니다");
		mTotalMemoryCount_TextView.setText(Integer.toString(db.getKnownCount()));
		Log.e("KARAM", "  Review Count  ->  " + Integer.toString(f_count[0] + f_count[1]) + "   " + Integer.toString(db.getMforget()));

		PieSlice slice = new PieSlice();
		slice.setColor(Color.parseColor("#e1aa25"));
		slice.setValue(db.getMforget());
		slice.setTitle("복습단어");
		mMemoryCount_PieGraph.addSlice(slice);

		slice = new PieSlice();
		slice.setColor(Color.parseColor("#757575"));
		slice.setValue(db.getPRememberCount());
		slice.setTitle("안심단어");
		mMemoryCount_PieGraph.addSlice(slice);
		/*
		slice = new PieSlice();
		slice.setColor(Color.BLUE);
		slice.setValue(40);
		slice.setTitle("안심단어");
		mMemoryCount_PieGraph.addSlice(slice);

		slice = new PieSlice();
		slice.setColor(Color.DKGRAY);
		slice.setValue(2);
		slice.setTitle("안심단어");
		mMemoryCount_PieGraph.addSlice(slice);

		slice = new PieSlice();
		slice.setColor(Color.CYAN);
		slice.setValue(15);
		slice.setTitle("안심단어");
		mMemoryCount_PieGraph.addSlice(slice);

		slice = new PieSlice();
		slice.setColor(Color.GREEN);
		slice.setValue(27);
		slice.setTitle("안심단어");
		mMemoryCount_PieGraph.addSlice(slice);
*/
		mMemoryCount_PieGraph.setInnerCircleRatio(190);
		mMemoryCount_PieGraph.setPadding(2);

		mReviewCount_Layout.setOnClickListener(ClickListener);
		mRelaxCount_Layout.setOnClickListener(ClickListener);
	}

	void setForgetCurveLayout() {
		int review_word_count = db.getReviewCountByCalendar();
		int forgetting_curve_percentage = (review_word_count * 100 / 500) + 1;

		LinearLayout.LayoutParams graphParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		graphParams.weight = 100 - forgetting_curve_percentage;
		mForgetCurveGraph_ImageView.setLayoutParams(graphParams);

		LinearLayout.LayoutParams tempParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		tempParams.weight = forgetting_curve_percentage;
		mForgetcurveDefault_ImageView.setLayoutParams(tempParams);

		if (forgetting_curve_percentage > 100) {
			mForgetRate_TextView.setText("100%");
		}
		else {
			mForgetRate_TextView.setText(Integer.toString(forgetting_curve_percentage - 1) + "%");
		}
		mForgetCurveInfo_Button.setOnClickListener(ClickListener);

		if (db.getReviewWordExist()) {
			getReviewTime = new GetReviewTime(db, getApplicationContext());
			String[] reviewTimes = getReviewTime.getReviewTime();

			Date date = new Date();

			// 확인 필요 함 . 00 으로 넘어오는 구간이 어딘지를 봐야합니다.
			if (db.isReviewpriod()) {
				mForget_CreateDate_TextView.setText("현재 복습구간입니다.");
				mForget_CreateCount_TextView.setVisibility(View.INVISIBLE);
				mForget_RemainDate_TextView.setVisibility(View.INVISIBLE);
			}
			else if (reviewTimes[0].equals("-1") || reviewTimes[1].equals("-1")) { // 측정 오류이므로 따로 처리가 필요함
				mForget_CreateDate_TextView.setText("측정할수가 없습니다.");
				mForget_CreateCount_TextView.setVisibility(View.INVISIBLE);
				mForget_RemainDate_TextView.setVisibility(View.INVISIBLE);
			}
			else {
				mForget_CreateCount_TextView.setVisibility(View.VISIBLE);
				mForget_RemainDate_TextView.setVisibility(View.VISIBLE);

				int reviewTime0 = Integer.valueOf(reviewTimes[0]) + date.get_hour_int();
				int reviewTime1 = Integer.valueOf(reviewTimes[1]) + date.get_min_int();

				if (reviewTime1 >= 60) {
					reviewTime0++;
					reviewTime1 -= 60;
				}

				int reviewDay = reviewTime0 / 24;

				if (reviewDay == 0) {
					String min = "" + reviewTime1;
					String hour = "" + reviewTime0;
					if (reviewTime1 < 10) {
						min = "0" + reviewTime1;
					}

					if (reviewTime0 < 10) {
						mForget_CreateDate_TextView.setText("오늘 " + "0" + reviewTime0 + ":" + min);
					} else {
						mForget_CreateDate_TextView.setText("오늘 " + reviewTime0 + ":" + min);
					}
				}
				else if (reviewDay == 1) {
					String min = "" + reviewTime1;
					String hour = "" + (reviewTime0 - 24);
					if (reviewTime1 < 10) {
						min = "0" + reviewTime1;
					}

					if (reviewTime0 - 24 < 10) {
						mForget_CreateDate_TextView.setText("내일 " + "0" + hour + ":" + min);
					} else {
						mForget_CreateDate_TextView.setText("내일 " + hour + ":" + min);
					}
				}
				else {
					DateTime datetime = CalendarHelper.convertDateToDateTime(new java.util.Date());
					datetime = datetime.plusDays(reviewDay);

					String min = "" + reviewTime1;
					String hour = "" + (reviewTime0 % 24);

					if (reviewTime1 < 10) {
						min = "0" + reviewTime1;
					}

					if (reviewTime0 % 24 < 10) {
						mForget_CreateDate_TextView.setText(datetime.getMonth() + "/" + datetime.getDay() + " " + "0" + hour + ":" + min);
					} else {
						mForget_CreateDate_TextView.setText(datetime.getMonth() + "/" + datetime.getDay() + " " + hour + ":" + min);
					}
				}

				mForget_CreateCount_TextView.setText("복습할 단어가 약 " + reviewTimes[2] + "개 생성됩니다.");
				mForget_RemainDate_TextView.setText("(" + date.get_year() + "/" + date.get_Month() + "/" + date.get_day() + " "
						+ date.get_hour() + ":" + date.get_minute() + " 기준, " + reviewTimes[0] + "시간 " + reviewTimes[1] + "분 뒤)");
			}
		}
		else {
			mForget_CreateDate_TextView.setText("단어를 암기해 주세요.");
			mForget_CreateCount_TextView.setText("아직 복습할 단어가 없습니다.");
			mForget_RemainDate_TextView.setText("");
		}
	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	public Animator.AnimatorListener getAnimationListener(){
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
			return new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {
				}

				@Override
				public void onAnimationEnd(Animator animation) {
				}

				@Override
				public void onAnimationCancel(Animator animation) {
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
				}
			};
		else return null;
	}


	OnClickListener ClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()) {


				case R.id.layout_feedback_memory_review: {
					FlurryAgent.logEvent("MemoryActivity:Click_Show_ReviewInfo");

					mReviewCount_TextView.setTextColor(Color.parseColor("#e1a922"));
					mReviewUnderBar_View.setVisibility(View.VISIBLE);
					mRelaxCount_TextView.setTextColor(Color.parseColor("#757575"));
					mRelaxUnderBar_View.setVisibility(View.INVISIBLE);

					mMemoryCountExplain_TextView.setText(pushPreference.getString(MainValue.GpreName, "이름없음")
							+ "님의 망각 곡선 분석 결과\n지금 복습하면 망각하지 않을 단어 " + f_count[1] + "개와"
							+ "\n복습을 제때 하지 않아 이미 망각해버린 단어 "+ f_count[0] + "개가 추출 되었습니다");

					try {
						PieSlice s1 = mMemoryCount_PieGraph.getSlice(0);
						PieSlice s2 = mMemoryCount_PieGraph.getSlice(1);

						s1.setGoalValue(db.getMforget());
						s1.setColor(Color.parseColor("#e1aa25"));
						s2.setGoalValue(db.getPRememberCount());
						s2.setColor(Color.parseColor("#757575"));

						mMemoryCount_PieGraph.setInnerCircleRatio(190);

						mMemoryCount_PieGraph.setDuration(500);//default if unspecified is 300 ms
						mMemoryCount_PieGraph.setInterpolator(new AccelerateDecelerateInterpolator());//default if unspecified is linear; constant speed
						mMemoryCount_PieGraph.setAnimationListener(getAnimationListener());
						mMemoryCount_PieGraph.animateToGoalValues();//animation will always overwrite. Pass true to call the onAnimationCancel Listener with onAnimationEnd
					} catch (Exception e) {
					}
				} break;

				case R.id.layout_feedback_memory_relax: {
					FlurryAgent.logEvent("MemoryActivity:Click_Show_RelaxInfo");
					mRelaxUnderBar_View.setVisibility(View.VISIBLE);
					mReviewUnderBar_View.setVisibility(View.INVISIBLE);
					mReviewCount_TextView.setTextColor(Color.parseColor("#757575"));
					mRelaxCount_TextView.setTextColor(Color.parseColor("#e1a922"));
					mMemoryCountExplain_TextView.setText(pushPreference.getString(MainValue.GpreName, "이름없음") + "님의 망각 곡선 분석 결과\n아직은 복습하지 않아도\n망각 될 위험이 없는 안심 단어들 입니다.");
					try {
						PieSlice s1 = mMemoryCount_PieGraph.getSlice(0);
						PieSlice s2 = mMemoryCount_PieGraph.getSlice(1);

						s1.setGoalValue(db.getPRememberCount());
						s1.setColor(Color.parseColor("#e1aa25"));
						s2.setGoalValue(db.getMforget());
						s2.setColor(Color.parseColor("#757575"));

						mMemoryCount_PieGraph.setInnerCircleRatio(205);
						mMemoryCount_PieGraph.setDuration(500);//default if unspecified is 300 ms
						mMemoryCount_PieGraph.setInterpolator(new AccelerateDecelerateInterpolator());//default if unspecified is linear; constant speed
						mMemoryCount_PieGraph.setAnimationListener(getAnimationListener());
						mMemoryCount_PieGraph.animateToGoalValues();//animation will always overwrite. Pass true to call the onAnimationCancel Listener with onAnimationEnd
					} catch (Exception e) {
					}
				} break;

				case R.id.btn_feedback_memory_forgetgraph_info: {
					Intent intent = new Intent(MemoryActivity.this, com.ihateflyingbugs.hsmd.feedback.MemoryExplainActivity.class);
					startActivity(intent);
				} break;

				case R.id.toggle_feedback_memory_alarm: {
					if (pushPreference.getBoolean(MainValue.GpreMemoryPush, false)) {
						mMemoryAlarm_ToggleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.offbutton));
						pushEditor.putBoolean(MainValue.GpreMemoryPush, false);
						pushEditor.commit();
						FlurryAgent.logEvent("MemoryActivity:Click_Off_MPush");
					} else {
						mMemoryAlarm_ToggleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.onbutton));
						pushEditor.putBoolean(MainValue.GpreMemoryPush, true);
						pushEditor.commit();
						FlurryAgent.logEvent("MemoryActivity:Click_ON_MPush");
					}
				} break;

				case R.id.layout_feedback_memory_memorizeword: {
					FlurryAgent.logEvent("MemoryActivity:Click_Show_RememberWord");
					Intent intent = new Intent(MemoryActivity.this, Known_Activity.class);
					startActivity(intent);
				} break;
			}
		}
	};


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
		mActionBar_Layout.setBackgroundColor(Color.parseColor("#e1aa25"));

		mBack_Layout = (LinearLayout)mCustomActionBarView.findViewById(R.id.layout_actionbar_back);
		mBack_Layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FlurryAgent.logEvent("MemoryActivity:Click_Set_BlogPush");
				MemoryActivity.this.finish();
			}
		});

		mTitle_TextView = (TextView)mCustomActionBarView.findViewById(R.id.text_actionbar_title);
		mTitle_TextView.setText("망각 곡선");
	}


	String starttime;
	String startdate;
	Date date = new Date();
	Map<String, String> articleParams;

	public void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("MemoryActivity", articleParams);
	}

	public void onStop()
	{
		super.onStop();
		FlurryAgent.endTimedEvent("MemoryActivity");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
	}
}
