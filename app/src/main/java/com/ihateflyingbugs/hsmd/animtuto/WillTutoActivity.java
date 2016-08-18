package com.ihateflyingbugs.hsmd.animtuto;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.data.MildangDate;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.model.StudyInfoData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import org.andlib.ui.CircleAnimationView;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class WillTutoActivity extends Activity {
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 13579) {
			if (resultCode == 97531) {
				if (data.getStringExtra("start").equals("Start")) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							rl_tuto_will_setting_top.setVisibility(View.VISIBLE);
							rl_tuto_will_setting_top.startAnimation(settingTopAnim);
							Date_Step1 = date.get_currentTime();
							Time_Step1 = String.valueOf(System.currentTimeMillis());

						}
					}, 583);
				}else if (data.getStringExtra("start").equals("End")){
					setResult(9652, new Intent());
					finish();
					overridePendingTransition(android.R.anim.fade_in, R.anim.slide_down_bottom);
				}
			}
		}
	}
	private static SharedPreferences mPreference;


	RelativeLayout rl_tuto_will_setting_top;

	TextView tv_tuto_will_finish_reward;
	TextView tv_tuto_will_setting_goal;
	CircleAnimationView tv_tuto_will_low;
	CircleAnimationView tv_tuto_will_mid;
	CircleAnimationView tv_tuto_will_high;
	CircleAnimationView tv_tuto_will_user;
	ImageView iv_tuto_will_clock;
	RelativeLayout graphLayout;

	TextView tv_tuto_will_goal_setting_finish;
	TextView tv_tuto_will_do_not_forget_will;
	TextView tv_tuto_will_finish_reward_after;
	ImageView iv_tuto_will_punch_icon;
	CircleAnimationView bt_tuto_will_quest_finish;


	Animation settingTopAnim;
	Animation expandTopAnim;
	Animation clockInAnim;
	Animation clockOutAnim;
	Animation timerInAnim;
	Animation goalTextInAnim;
	Animation goalTextOutAnim;
	Animation rewardTextOutAnim;
	Animation btn1Anim;
	Animation btn2Anim;
	Animation btn3Anim;
	Animation btn4Anim;

	Animation finishRewardinAnim;
	Animation setGoalFinishInAnim;
	Animation doNotForgetTextInAnim;
	Animation punchIconAnim;
	Animation getRewardBtnAnim;

	DBPool db;


	Map<String, String> Step1Params= new HashMap<String, String>();
	Map<String, String> Step2Params= new HashMap<String, String>();
	Map<String, String> Step3Params= new HashMap<String, String>();

	String Date_Step1;
	String Time_Step1;
	String Date_Step2;
	String Time_Step2;
	String Date_Step3;
	String Time_Step3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(MainValue.isLowDensity(getApplicationContext())){
			setContentView(R.layout.activity_will_tuto_small);
		}else{
			setContentView(R.layout.activity_will_tuto);
		}

		rl_tuto_will_setting_top = (RelativeLayout) findViewById(R.id.rl_tuto_will_setting_top);
		tv_tuto_will_finish_reward = (TextView) findViewById(R.id.tv_tuto_will_finish_reward);
		tv_tuto_will_setting_goal = (TextView) findViewById(R.id.tv_tuto_will_setting_goal);

		tv_tuto_will_low = (CircleAnimationView) findViewById(R.id.tv_tuto_will_low);
		tv_tuto_will_mid = (CircleAnimationView) findViewById(R.id.tv_tuto_will_mid);
		tv_tuto_will_high = (CircleAnimationView) findViewById(R.id.tv_tuto_will_high);
		tv_tuto_will_user = (CircleAnimationView) findViewById(R.id.tv_tuto_will_user);
		iv_tuto_will_clock = (ImageView) findViewById(R.id.iv_tuto_will_clock);

		graphLayout = (RelativeLayout) findViewById(R.id.graphLayout);

		tv_tuto_will_goal_setting_finish = (TextView) findViewById(R.id.tv_tuto_will_goal_setting_finish);
		tv_tuto_will_do_not_forget_will = (TextView) findViewById(R.id.tv_tuto_will_do_not_forget_will);
		tv_tuto_will_finish_reward_after = (TextView) findViewById(R.id.tv_tuto_will_finish_reward_after);
		iv_tuto_will_punch_icon = (ImageView) findViewById(R.id.iv_tuto_will_punch_icon);
		bt_tuto_will_quest_finish = (CircleAnimationView) findViewById(R.id.bt_tuto_will_quest_finish);

		settingTopAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_layout);
		clockInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.clock_icon_in);
		goalTextInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.set_goal_text_in);
		btn1Anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_1_anim);
		btn2Anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_2_anim);
		btn3Anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_3_anim);
		btn4Anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_4_anim);
		clockOutAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.clock_icon_out);
		goalTextOutAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.set_goal_text_out);
		rewardTextOutAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.reward_text_out);
		expandTopAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_layout_expand);

		timerInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.timer_in);

		finishRewardinAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.finish_reward_in);
		setGoalFinishInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.set_goal_finish_text);
		doNotForgetTextInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.do_not_forget_text_in);
		punchIconAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.punch_icon_in);
		getRewardBtnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_in);

		db = DBPool.getInstance(getApplicationContext());
		db.insertDate(new MildangDate().get_today());
		db.createStudyTime();
		db.insertDate(new MildangDate().get_today());

		// tv_tuto_will_low.setOnClickListener(btnClickListener);
		// tv_tuto_will_mid.setOnClickListener(btnClickListener);
		// tv_tuto_will_high.setOnClickListener(btnClickListener);
		// tv_tuto_will_user.setOnClickListener(btnClickListener);

		mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);

		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setTextSize(spTopx(getApplicationContext(), 18));
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.CENTER);

		Paint whiteBtnpPaint = new Paint();
		whiteBtnpPaint.setStyle(Paint.Style.FILL);
		whiteBtnpPaint.setColor(Color.BLACK);
		whiteBtnpPaint.setAlpha(138);
		whiteBtnpPaint.setTextSize(spTopx(getApplicationContext(), 14));
		whiteBtnpPaint.setAntiAlias(true);
		whiteBtnpPaint.setTextAlign(Paint.Align.CENTER);

		bt_tuto_will_quest_finish.setAnimButton(paint, R.drawable.willqwest_reward_btn, new CircleAnimationView.ClickCallbacks() {
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {
						bt_tuto_will_quest_finish.stopAnimation();
						Intent i = new Intent(getApplicationContext(), WillRewardActivity.class);
						i.putExtra("start", false);
						startActivityForResult(i, 13579);

						FlurryAgent.logEvent("WillTutoActivity:Click_Finish", Step3Params);
						Step3Params.put("Start", Date_Step3);
						Step3Params.put("End", date.get_currentTime());
						Step3Params.put(
								"Duration",
								""
										+ ((Long.valueOf(System.currentTimeMillis()) - Long
										.valueOf(Time_Step3))) / 1000);

					}
				});
			}
		});

		tv_tuto_will_low.setAnimButton(whiteBtnpPaint, R.drawable.qwest_choice_btn, new CircleAnimationView.ClickCallbacks() {
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {
						tv_tuto_will_low.stopAnimation();
						iv_tuto_will_clock.startAnimation(clockOutAnim);

						mPreference.edit().putInt(MainValue.GpreGoalTime, 10).commit();

						FlurryAgent.logEvent("WillTutoActivity:Click_10minute", Step1Params);
						Step1Params.put("Start", Date_Step1);
						Step1Params.put("End", date.get_currentTime());
						Step1Params.put(
								"Duration",
								""
										+ ((Long.valueOf(System.currentTimeMillis()) - Long
										.valueOf(Time_Step1))) / 1000);

						db.insertGoalTime(10);

						Date_Step3 = date.get_currentTime();
						Time_Step3 = String.valueOf(System.currentTimeMillis());

						insertGoalTime(db.getStudentId(), "10");

					}
				});
			}
		});
		tv_tuto_will_mid.setAnimButton(whiteBtnpPaint, R.drawable.qwest_choice_btn, new CircleAnimationView.ClickCallbacks() {
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {
						tv_tuto_will_mid.stopAnimation();
						iv_tuto_will_clock.startAnimation(clockOutAnim);
						mPreference.edit().putInt(MainValue.GpreGoalTime, 20).commit();
						db.insertGoalTime(20);
						FlurryAgent.logEvent("WillTutoActivity:Click_20minute", Step1Params);
						Step1Params.put("Start", Date_Step1);
						Step1Params.put("End", date.get_currentTime());
						Step1Params.put(
								"Duration",
								""
										+ ((Long.valueOf(System.currentTimeMillis()) - Long
										.valueOf(Time_Step1))) / 1000);

						Date_Step3 = date.get_currentTime();
						Time_Step3 = String.valueOf(System.currentTimeMillis());
						insertGoalTime(db.getStudentId(), "20");

					}
				});
			}
		});
		tv_tuto_will_high.setAnimButton(whiteBtnpPaint, R.drawable.qwest_choice_btn, new CircleAnimationView.ClickCallbacks() {
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {
						tv_tuto_will_high.stopAnimation();
						iv_tuto_will_clock.startAnimation(clockOutAnim);
						mPreference.edit().putInt(MainValue.GpreGoalTime, 30).commit();
						db.insertGoalTime(30);
						FlurryAgent.logEvent("WillTutoActivity:Click_30minute", Step1Params);
						Step1Params.put("Start", Date_Step1);
						Step1Params.put("End", date.get_currentTime());
						Step1Params.put(
								"Duration",
								""
										+ ((Long.valueOf(System.currentTimeMillis()) - Long
										.valueOf(Time_Step1))) / 1000);

						Date_Step3 = date.get_currentTime();
						Time_Step3 = String.valueOf(System.currentTimeMillis());

						insertGoalTime(db.getStudentId(), "30");

					}
				});
			}
		});

		tv_tuto_will_user.setAnimButton(whiteBtnpPaint, R.drawable.qwest_choice_btn, new CircleAnimationView.ClickCallbacks() {
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {
						tv_tuto_will_user.stopAnimation();
						userDialogShow();

						FlurryAgent.logEvent("WillTutoActivity:Click_UserInput", Step1Params);
						Step1Params.put("Start", Date_Step1);
						Step1Params.put("End", date.get_currentTime());
						Step1Params.put(
								"Duration",
								""
										+ ((Long.valueOf(System.currentTimeMillis()) - Long
										.valueOf(Time_Step1))) / 1000);
						Date_Step2 = date.get_currentTime();
						Time_Step2 = String.valueOf(System.currentTimeMillis());
					}
				});
			}
		});

		tv_tuto_will_low.setText("일단 10분 부터");
		tv_tuto_will_mid.setText("그래도 20분은 해야지");
		tv_tuto_will_high.setText("하면 된다. 30분!");
		tv_tuto_will_user.setText("사용자 설정");
		bt_tuto_will_quest_finish.setText("보상 받기");

		tv_tuto_will_low.setColor(0xea, 0x4e, 0x37);
		tv_tuto_will_mid.setColor(0xea, 0x4e, 0x37);
		tv_tuto_will_high.setColor(0xea, 0x4e, 0x37);
		tv_tuto_will_user.setColor(0xea, 0x4e, 0x37);
		bt_tuto_will_quest_finish.setColor(0xff, 0xff, 0xff);

//		tv_tuto_will_low.setTextColor(255, 0, 0, 0);
//		tv_tuto_will_mid.setTextColor(255, 0, 0 ,0);
//		tv_tuto_will_high.setTextColor(255, 0, 0, 0);
//		tv_tuto_will_user.setTextColor(0, 0, 0, 0);
		bt_tuto_will_quest_finish.setTextColor(255, 255, 255, 255);

//		tv_tuto_will_low.setTextSize(14);
//		tv_tuto_will_mid.setTextSize(14);
//		tv_tuto_will_high.setTextSize(14);
//		tv_tuto_will_user.setTextSize(14);
		bt_tuto_will_quest_finish.setTextSize(12);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent i = new Intent(getApplicationContext(), WillRewardActivity.class);
				i.putExtra("start", true);
				startActivityForResult(i, 13579);
			}
		}, 900);

//		rl_tuto_will_setting_top.startAnimation(settingTopAnim);

		punchIconAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						bt_tuto_will_quest_finish.setVisibility(View.VISIBLE);
						bt_tuto_will_quest_finish.startAnimation(getRewardBtnAnim);
					}
				}, 208);
			}
		});

		expandTopAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				tv_tuto_will_goal_setting_finish.setVisibility(View.VISIBLE);
				tv_tuto_will_do_not_forget_will.setVisibility(View.VISIBLE);
				tv_tuto_will_finish_reward_after.setVisibility(View.VISIBLE);
				iv_tuto_will_punch_icon.setVisibility(View.VISIBLE);

				tv_tuto_will_goal_setting_finish.startAnimation(setGoalFinishInAnim);
				tv_tuto_will_do_not_forget_will.startAnimation(doNotForgetTextInAnim);
				tv_tuto_will_finish_reward_after.startAnimation(finishRewardinAnim);
				iv_tuto_will_punch_icon.startAnimation(punchIconAnim);

				tv_tuto_will_low.setVisibility(View.GONE);
				tv_tuto_will_mid.setVisibility(View.GONE);
				tv_tuto_will_high.setVisibility(View.GONE);
				tv_tuto_will_user.setVisibility(View.GONE);
			}
		});

		rewardTextOutAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				rl_tuto_will_setting_top.getLayoutParams().height = LayoutParams.MATCH_PARENT;
				tv_tuto_will_setting_goal.setVisibility(View.GONE);
				tv_tuto_will_finish_reward.setVisibility(View.GONE);
			}
		});

		timerInAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				rl_tuto_will_setting_top.startAnimation(expandTopAnim);
				tv_tuto_will_setting_goal.startAnimation(goalTextOutAnim);
				tv_tuto_will_finish_reward.startAnimation(rewardTextOutAnim);
			}
		});

		clockOutAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				iv_tuto_will_clock.setVisibility(View.GONE);

				graphLayout.setVisibility(View.VISIBLE);
				graphLayout.startAnimation(timerInAnim);
			}
		});

		clockInAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				tv_tuto_will_setting_goal.setVisibility(View.VISIBLE);
				tv_tuto_will_setting_goal.startAnimation(goalTextInAnim);

				tv_tuto_will_finish_reward.setVisibility(View.VISIBLE);
				tv_tuto_will_finish_reward.startAnimation(goalTextInAnim);

				new Handler().postDelayed(new Runnable() {
					public void run() {
						tv_tuto_will_low.startAnimation(btn1Anim);
						tv_tuto_will_mid.startAnimation(btn2Anim);
						tv_tuto_will_high.startAnimation(btn3Anim);
						tv_tuto_will_user.startAnimation(btn4Anim);
						tv_tuto_will_low.setVisibility(View.VISIBLE);
						tv_tuto_will_mid.setVisibility(View.VISIBLE);
						tv_tuto_will_high.setVisibility(View.VISIBLE);
						tv_tuto_will_user.setVisibility(View.VISIBLE);
					};
				}, 375);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {}
		});

		settingTopAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				iv_tuto_will_clock.setVisibility(View.VISIBLE);
				iv_tuto_will_clock.startAnimation(clockInAnim);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {}
		});
	}



	public static float spTopx(Context context, float sp) {
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return sp * scaledDensity;
	}

	public void userDialogShow() {

		final Dialog dialog = new Dialog(WillTutoActivity.this);
		dialog.setContentView(R.layout.popup_tuto_goaltime);
		dialog.setTitle("사용자 설정 시간 선택");
		final NumberPicker np = (NumberPicker) dialog.findViewById(R.id.np_tuto_goaltime_picker);
		Button bt_close_popup = (Button) dialog.findViewById(R.id.bt_close_popup);
		np.setMaxValue(99); // max value 99
		np.setMinValue(10); // min value 10
		np.setWrapSelectorWheel(true);
		np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				np.setValue(newVal);
			}
		});
		bt_close_popup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPreference.edit().putInt(MainValue.GpreGoalTime, np.getValue()).commit();
				db.insertGoalTime(np.getValue());
				iv_tuto_will_clock.startAnimation(clockOutAnim);
				dialog.dismiss();

				FlurryAgent.logEvent("WillTutoActivity:Click_"+db.getGoalTime()+"minute", Step2Params);
				Step2Params.put("Start", Date_Step2);
				Step2Params.put("End", date.get_currentTime());
				Step2Params.put(
						"Duration",
						""
								+ ((Long.valueOf(System.currentTimeMillis()) - Long
								.valueOf(Time_Step2))) / 1000);

				Date_Step3 = date.get_currentTime();
				Time_Step3 = String.valueOf(System.currentTimeMillis());
				insertGoalTime(db.getStudentId(), np.getValue()+"");
			}
		});
		dialog.show();
	}

	public void insertGoalTime(String student_id, String goal_time){
		new RetrofitService().getStudyInfoService().retroInsertStudentGoalTime(student_id,
				goal_time)
				.enqueue(new Callback<StudyInfoData>() {
					@Override
					public void onResponse(Response<StudyInfoData> response, Retrofit retrofit) {

					}

					@Override
					public void onFailure(Throwable t) {

					}
				});
	}

	String starttime;
	String startdate;
	Date date = new Date();
	Map<String, String> articleParams;

	public void onStart() {

		super.onStart();

		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("WillTutoActivity", articleParams);

		// your code
	}

	public void onStop() {
		super.onStop();

		FlurryAgent.endTimedEvent("WillTutoActivity");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put(
				"Duration",
				""
						+ ((Long.valueOf(System.currentTimeMillis()) - Long
						.valueOf(starttime))) / 1000);
		FlurryAgent.onEndSession(this);
		// your code

	}

}
