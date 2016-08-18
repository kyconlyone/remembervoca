package com.ihateflyingbugs.hsmd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.animtuto.HitTutoActivity;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.data.MildangDate;
import com.ihateflyingbugs.hsmd.service.Utils;
import com.ihateflyingbugs.hsmd.tutorial.MainActivity;

import org.andlib.ui.PercentView;

import java.util.HashMap;
import java.util.Map;

public class TimerActivity extends Activity {

	boolean animationFlag = false;

	PercentView percentView;
	PercentView percentView2;
	ImageView timerTop;
	RelativeLayout timerTopLayout;
	RelativeLayout timerLayout;
	RelativeLayout popupLayout;
	RelativeLayout fullLayout;
	RelativeLayout timeView;

	LinearLayout imageLayout;

	TextView goalTimeMin;
	TextView goalTimeSec;
	TextView today;
	TextView todayMove;

	TextView tv_system_ment, tv_uesr_ment;

	TextView goalTime;
	TextView learnTime;
	TextView guideMsg;
	TextView newWord;
	TextView reviewWord;
	TextView reviewNeedWord;
	ImageView timerOff;

	Button showTimer;

	LayoutParams timerParam;
	LayoutParams mRootLayoutParams;

	OvershootInterpolator overShoot;

	boolean showflag;

	float width;
	float height;
	int bar;
	float y;
	int percentHeight;
	int screenWidth;

	Display display;

	MildangDate date;

	int percent;
	float min;
	float sec;

	float density;

	float percentage;
	int percentage_lv;

	int goal;

	boolean hit_tuto;

	private static DBPool db;

	static SharedPreferences mPreference;
	SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(MainValue.isLowDensity(getApplicationContext())){
			setContentView(R.layout.activity_timer_small);
		}else{
			setContentView(R.layout.activity_timer);
		}

		display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();

		db = DBPool.getInstance(this);
		//기기의 dpi 구하기
		density = this.getResources().getDisplayMetrics().density;

		mPreference = getSharedPreferences(MainValue.preName,
				MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);

		Intent i = getIntent();

		width = getIntent().getExtras().getFloat("width");
		height = getIntent().getExtras().getFloat("height");
		//actionBar의 height
		bar = getIntent().getExtras().getInt("bar");
		y = getIntent().getExtras().getFloat("myY");
		percentHeight = getIntent().getExtras().getInt("myHeight");

		//hit tutorial에서 불리는 경우
		hit_tuto = getIntent().getExtras().getBoolean("hit_tuto");

		//사용시간
		min = getIntent().getExtras().getInt("min");
		sec = getIntent().getExtras().getInt("sec");

		//목표시간 대비 목표 달성률(%100)
		percentage = getIntent().getExtras().getFloat("percentage");

		//사용시간을 millisecond로 변환
		float use_time = (min * 60000) + sec * 1000;

		//목표시간
		goal = db.getGoalTime();

		//목표시간 대비 목표 달성률(/100)
		percentage_lv = ((int) use_time) / ((goal * 60000)+1);

		//목표 달성 이미지 레이아웃
		imageLayout = (LinearLayout) findViewById(R.id.imageLayout);

		// 사용시간에 따른 배경 이미지 교체
		if (min < goal) {
			//목표시간 이하
			imageLayout.setBackgroundResource(R.drawable.timer_step1);
		} else if (min >= goal && min < (goal + 10)) {
			//목표시간 ~ 목표시간+10
			imageLayout.setBackgroundResource(R.drawable.timer_step2);
		} else if (min >= (goal + 10) && min < (goal + 30)) {
			//목표시간+10 ~ 목표시간+30
			imageLayout.setBackgroundResource(R.drawable.timer_step3);
		} else {
			//목표시간+30 이상
			imageLayout.setBackgroundResource(R.drawable.timer_step4);

		}

		timeView = (RelativeLayout) findViewById(R.id.timeView);
		goalTime = (TextView) findViewById(R.id.goal_time);
		learnTime = (TextView) findViewById(R.id.learning_time);
		guideMsg = (TextView) findViewById(R.id.guideMsg);
		newWord = (TextView) findViewById(R.id.new_word);
		reviewWord = (TextView) findViewById(R.id.review_word);
		reviewNeedWord = (TextView) findViewById(R.id.review_need_word);

		percentView = (PercentView) findViewById(R.id.percentView1);
		percentView2 = (PercentView) findViewById(R.id.percentView2);
		timerTop = (ImageView) findViewById(R.id.timer_top);
		fullLayout = (RelativeLayout) findViewById(R.id.fullLayout);
		timerLayout = (RelativeLayout) findViewById(R.id.percentViewLayout);
		popupLayout = (RelativeLayout) findViewById(R.id.popupLayout);
		timerTopLayout = (RelativeLayout) findViewById(R.id.timer_topLayout);
		today = (TextView) findViewById(R.id.today);
		todayMove = (TextView) findViewById(R.id.today_move);
		showTimer = (Button) findViewById(R.id.show_timer);

		timerOff = (ImageView) findViewById(R.id.timer_off);

		//타이머 표시 유무
		showflag = mPreference.getBoolean("showFlag", true);

		if (showflag == true) {
			//타이머 표시 o
			showTimer.setBackground(getResources().getDrawable(
					R.drawable.timer_btn_on));
			timerOff.setVisibility(View.INVISIBLE);
			todayMove.setVisibility(View.INVISIBLE);
			percentView.setVisibility(View.VISIBLE);
			timeView.setVisibility(View.VISIBLE);
		} else {
			//타이머 표시 x
			showTimer.setBackground(getResources().getDrawable(
					R.drawable.timer_btn_off));
			timerOff.setVisibility(View.VISIBLE);
			todayMove.setVisibility(View.VISIBLE);
			percentView.setVisibility(View.INVISIBLE);
			timeView.setVisibility(View.INVISIBLE);
		}

		goalTimeMin = (TextView) findViewById(R.id.tv_goal_time_min);
		goalTimeSec = (TextView) findViewById(R.id.tv_goal_time_sec);

		tv_system_ment = (TextView) findViewById(R.id.system_name);
		tv_uesr_ment = (TextView) findViewById(R.id.user_name);

		tv_system_ment.setText(mPreference.getString(MainValue.GpreSystemName,
				"Riche") + "님이 분석한");
		tv_uesr_ment.setText(mPreference.getString(MainValue.GpreName, "이름없음")
				+ "님의 오늘 학습 성과");

		goalTime.setText("목표 " + Integer.toString(goal) + "분");


		//새 학습단어
		newWord.setText(db.getStudyWord() + "");

		//복습을 통해 기억 연장된 단어
		reviewWord.setText(db.getReviewWord() + "");

		//복습이 필요한 단어
		reviewNeedWord.setText(Integer.toString(db.getMforget()));

		MildangDate mildangDate = new MildangDate();

		//상단 타이머 이미지 내 날짜 표시
		today.setText(mildangDate.get_Month() + "." + mildangDate.get_day());
		//타이머 이미지 내 날짜 표시(에니메이션)
		todayMove
				.setText(mildangDate.get_Month() + "." + mildangDate.get_day());

		//남은 시간
		int remainTime = (int) min * 60 + (int) sec;
		remainTime = goal * 60 - remainTime;

		String remain_min, remain_sec;

		if ((remainTime / 60) < 10 && (remainTime / 60) > -10) {
			remain_min = "0" + Integer.toString(remainTime / 60);
		} else {
			remain_min = Integer.toString(remainTime / 60);
		}

		if ((remainTime % 60) < 10 && (remainTime % 60) > -10) {
			remain_sec = "0" + Integer.toString(remainTime % 60);
		} else {
			remain_sec = Integer.toString(remainTime % 60);
		}

		if (min < db.getGoalTime()) {
			guideMsg.setText("목표까지 " + remain_min.replaceAll("-", "") + ":" + remain_sec.replaceAll("-", "")
					+ "\n남았어요!");
		} else {
			guideMsg.setText("목표보다 " + remain_min.replaceAll("-", "") + ":"
					+ remain_sec.replaceAll("-", "") + "\n더 공부했어요!");
		}

		if (min < 10) {
			if(sec<10){
				learnTime.setText("0" + Integer.toString((int) min) + "분 "
						+"0"+ Integer.toString((int) sec) + "초");
				goalTimeMin.setText("0" + Integer.toString((int) min));
				goalTimeSec.setText("0" + Integer.toString((int) sec));
			}else{

				learnTime.setText("0" + Integer.toString((int) min) + "분 "
						+ Integer.toString((int) sec) + "초");
				goalTimeMin.setText("0" + Integer.toString((int) min));
				goalTimeSec.setText(""+Integer.toString((int) sec));
			}
		} else {
			if(sec<10){

				learnTime.setText((Integer.toString((int) min).replace("-", "") + "분 "
						+"0"+ Integer.toString((int) sec)+ "초").replace("-", "") );
				goalTimeMin.setText(Integer.toString((int) min).replace("-", ""));
				goalTimeSec.setText("0" + Integer.toString((int) sec).replace("-", ""));
			}else{

				learnTime.setText((Integer.toString((int) min).replace("-", "") + "분 "
						+ Integer.toString((int) sec)+ "초").replace("-", "") );
				goalTimeMin.setText(Integer.toString((int) min).replace("-", ""));
				goalTimeSec.setText(""+Integer.toString((int) sec).replace("-", ""));
			}
		}

		//그래프(애니메이션)
		percentView.setPercentage(percentage, percentage_lv, true);
		//그래프(이미지 위)
		percentView2.setPercentage(percentage, percentage_lv, false);

		timerParam = new LayoutParams(Utils.dpToPixels(48, getResources()),
				Utils.dpToPixels(48, getResources()));

		timerParam.width = (int) width;
		timerParam.height = (int) height + bar;

		Log.e("seokangseok", Float.toString(height) + " : " + Float.toString(display.getHeight()));
		Log.e("seokangseok", Float.toString(32*density));
		if (height == display.getHeight()) {
			//타이머를 초기 상태에서 클릭했을 때(사전에 적용시킨 마진값 유지)
			timerParam.setMargins(0, 0, 31 * (int) density, 32 * (int) density);
		} else {
			//타이머를 움직인 상태에서 클릭했을 때(마진값 삭제)
			timerParam.setMargins(0, 0, 31 * (int) density, 0);
		}

		//기기의 너비
		screenWidth = display.getWidth();

		overShoot = new OvershootInterpolator();

		//타이머 위치 조절
		fullLayout.updateViewLayout(timerLayout, timerParam);
		//애니메이션 종료 전까지 상단 타이머 안보이게
		timerTopLayout.setVisibility(View.INVISIBLE);
		//애니메이션 종료 전까지 팝업창 안보이게
		popupLayout.setVisibility(View.GONE);

		//찹업창 외부 선택 시 종료
		fullLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				FlurryAgent.logEvent("TimerActivity:Click_Close_Background");
				finishActivity();
			}
		});

		//'단어장에 타이머 표시'
		showTimer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showflag = mPreference.getBoolean("showFlag", false);
				if(!hit_tuto){
					if (showflag == true) {
						//타이머 표시 o 상태일 때 클릭하면

						FlurryAgent.logEvent("TimerActivity:Click_Off_Timer");
						showTimer.setBackground(getResources().getDrawable(
								R.drawable.timer_btn_off));
						editor.putBoolean("showFlag", false);
						editor.commit();
						MainActivity.timerOff
								.setVisibility(MainActivity.timerLayout.VISIBLE);
						MainActivity.percentView1.setVisibility(View.INVISIBLE);
						MainActivity.timeView.setVisibility(View.INVISIBLE);
						timerOff.setVisibility(View.VISIBLE);
						todayMove.setVisibility(View.VISIBLE);
						percentView.setVisibility(View.INVISIBLE);
						timeView.setVisibility(View.INVISIBLE);
					} else {
						//타이머 표시 x 상태일 때 클릭하면
						FlurryAgent.logEvent("TimerActivity:Click_On_Timer");
						showTimer.setBackground(getResources().getDrawable(
								R.drawable.timer_btn_on));
						editor.putBoolean("showFlag", true);
						editor.commit();
						MainActivity.timerOff
								.setVisibility(MainActivity.timerLayout.INVISIBLE);
						MainActivity.percentView1.setVisibility(View.VISIBLE);
						MainActivity.timeView.setVisibility(View.VISIBLE);
						timerOff.setVisibility(View.INVISIBLE);
						todayMove.setVisibility(View.INVISIBLE);
						percentView.setVisibility(View.VISIBLE);
						timeView.setVisibility(View.VISIBLE);
					}
				}else{
					if (showflag == true) {
						//타이머 표시 o 상태일 때 클릭하면
						FlurryAgent.logEvent("TimerActivity:Click_Off_Timer");
						showTimer.setBackground(getResources().getDrawable(
								R.drawable.timer_btn_off));
						editor.putBoolean("showFlag", false);
						editor.commit();
						HitTutoActivity.timer_off_layout
								.setVisibility(View.VISIBLE);
						HitTutoActivity.percent_view.setVisibility(View.INVISIBLE);
						HitTutoActivity.time_layout.setVisibility(View.INVISIBLE);
						timerOff.setVisibility(View.VISIBLE);
						todayMove.setVisibility(View.VISIBLE);
						percentView.setVisibility(View.INVISIBLE);
						timeView.setVisibility(View.INVISIBLE);
					} else {
						//타이머 표시 x 상태일 때 클릭하면
						FlurryAgent.logEvent("TimerActivity:Click_On_Timer");
						showTimer.setBackground(getResources().getDrawable(
								R.drawable.timer_btn_on));
						editor.putBoolean("showFlag", true);
						editor.commit();
						HitTutoActivity.timer_off_layout
								.setVisibility(View.INVISIBLE);
						HitTutoActivity.percent_view.setVisibility(View.VISIBLE);
						HitTutoActivity.time_layout.setVisibility(View.VISIBLE);
						timerOff.setVisibility(View.INVISIBLE);
						todayMove.setVisibility(View.INVISIBLE);
						percentView.setVisibility(View.VISIBLE);
						timeView.setVisibility(View.VISIBLE);
					}
				}

			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();

		mPreference = getSharedPreferences(MainValue.preName,
				MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		editor = mPreference.edit();

		//activity 실행 시
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				//올라가는 애니메이션
				Animation toTop = new TranslateAnimation(0, 0, 0,
						-(y + bar - 16 * (int) density));
				toTop.setInterpolator(new AnimationUtils().loadInterpolator(
						getApplicationContext(),
						android.R.anim.overshoot_interpolator));
				toTop.setDuration(1000);
				toTop.setFillAfter(true);
				toTop.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub
						if (hit_tuto == true) {
							//hit_tutorial에서 불렀을 때
							HitTutoActivity.timer_layout
									.setVisibility(View.INVISIBLE);
						} else {
							//메인단어장에서 불렀을 때
							MainActivity.timerLayout
									.setVisibility(View.INVISIBLE);
						}
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						// TODO Auto-generated method stub
						MainActivity.activityFlag = true;
						HitTutoActivity.activityFlag = true;
						Animation toScale = AnimationUtils.loadAnimation(
								getApplicationContext(), R.anim.scale_popup);
						// popupLayout.startAnimation(toScale);

						timerParam.width = (int) width;
						timerParam.height = percentHeight + 64;
						fullLayout.updateViewLayout(timerLayout, timerParam);
						fullLayout.removeView(timerLayout);
						fullLayout.addView(timerLayout);

						LayoutParams param = new LayoutParams(Utils.dpToPixels(
								48, getResources()), Utils.dpToPixels(48,
								getResources()));

						param.width = display.getWidth();
						param.height = 75 * (int) density;

						param.setMargins(0, 16 * (int) density,
								(31 * (int) density), 0);

						timerTopLayout.bringToFront();

						fullLayout.updateViewLayout(timerTopLayout, param);
						timerTopLayout.setVisibility(timerTop.VISIBLE);
						timerLayout.setVisibility(View.INVISIBLE);

						popupLayout.setVisibility(View.VISIBLE);
						Animation toGrow = AnimationUtils.loadAnimation(
								getApplicationContext(), R.anim.scale_popup);
						toGrow.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationRepeat(Animation animation) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationEnd(Animation animation) {
								// TODO Auto-generated method stub
								animationFlag = true;
							}
						});
						popupLayout.startAnimation(toGrow);

					}
				});

				timerLayout.startAnimation(toTop);
			}
		}, 100);

	}

	@Override
	public void onPause() {
		super.onPause();
		//activity 종료 애미메미션 없애기
		overridePendingTransition(0, 0);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finishActivity();
	}

	//activity 종료 시 애니메이션
	private void finishActivity() {

		//작아지는 애니메이션
		Animation toSmall = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.scale_popup_re);
		toSmall.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			//팝업창이 작아지고 난 후,
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				popupLayout.setVisibility(View.GONE);
				timerTopLayout.setVisibility(View.GONE);
				timerLayout.setVisibility(View.INVISIBLE);

				//내려가는 애니메이션
				Animation toBottom = new TranslateAnimation(0, 0, 0, y + bar
						- 64);
				toBottom.setDuration(1000);
				toBottom.setInterpolator(new AnimationUtils().loadInterpolator(
						getApplicationContext(),
						android.R.anim.overshoot_interpolator));
				toBottom.setFillAfter(true);
				toBottom.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub
					}

					//타이머가 내려가고 난 뒤,
					@Override
					public void onAnimationEnd(Animation animation) {
						// TODO Auto-generated method stub

						mRootLayoutParams = new LayoutParams(Utils.dpToPixels(
								48, getResources()), Utils.dpToPixels(48,
								getResources()));
						mRootLayoutParams.width = (int) width;
						mRootLayoutParams.height = (int) height;
						timerParam.width = (int) width;
						timerParam.height = (int) height + bar;
						mRootLayoutParams.setMargins(0, 0, 0,
								32 * (int) density);

						if (hit_tuto == true) {
							//hit_tutorial에서 실행했을 때
							HitTutoActivity.hit_tuto_layout.updateViewLayout(
									HitTutoActivity.timer_layout,
									mRootLayoutParams);
							HitTutoActivity.timer_layout
									.setVisibility(View.VISIBLE);
							HitTutoActivity.c_timer.start();
						} else {
							//단어장에서 실행했을 때
							MainActivity.fullLayout
									.updateViewLayout(MainActivity.timerLayout,
											mRootLayoutParams);

							MainActivity.timerLayout
									.setVisibility(MainActivity.timerLayout.VISIBLE);

							MainActivity.restart_flag = true;
							MainActivity.cTimer.start();
						}
						timerLayout.setVisibility(View.GONE);
						animationFlag = true;

						TimerActivity.this.finish();

					}
				});
				timerLayout.startAnimation(toBottom);
			}
		});

		if (animationFlag == true) {
			animationFlag = false;
			popupLayout.startAnimation(toSmall);
		}

	}

	String starttime;
	String startdate;
	Date dates = new Date();

	Map<String, String> articleParams ;
	public void onStart()
	{

		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = dates.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("TimerActivity", articleParams);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 시작,FinishStudyFragment,1]\r\n");

	}

	public void onStop()
	{
		super.onStop();
		FlurryAgent.endTimedEvent("TimerActivity");
		articleParams.put("Start", startdate);
		articleParams.put("End", dates.get_currentTime());
		Log.e("splash", startdate+"        "+dates.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 끝,FinishStudyFragment,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");
	}
}
