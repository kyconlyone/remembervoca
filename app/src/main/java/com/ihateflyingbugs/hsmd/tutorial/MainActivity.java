package com.ihateflyingbugs.hsmd.tutorial;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.ihateflyingbugs.hsmd.GetWillCoachMent;
import com.ihateflyingbugs.hsmd.LineSpacing;
import com.ihateflyingbugs.hsmd.MyScoreFragment;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.TimerActivity;
import com.ihateflyingbugs.hsmd.WordListFragment;
import com.ihateflyingbugs.hsmd.animtuto.PayTutoActivity;
import com.ihateflyingbugs.hsmd.blur.Blur;
import com.ihateflyingbugs.hsmd.blur.ImageUtils;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.Coupon;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.DBPool.word_log;
import com.ihateflyingbugs.hsmd.data.DBState;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.data.MildangDate;
import com.ihateflyingbugs.hsmd.feedback.FAQActivity;
import com.ihateflyingbugs.hsmd.feedback.SettingActivity;
import com.ihateflyingbugs.hsmd.indicator.MainCouponArrayAdapter;
import com.ihateflyingbugs.hsmd.lock.LockService;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.manager.ManagerInfo;
import com.ihateflyingbugs.hsmd.manager.Manager_ReadyExamPopup;
import com.ihateflyingbugs.hsmd.model.AuthorizationData;
import com.ihateflyingbugs.hsmd.model.CouponData;
import com.ihateflyingbugs.hsmd.model.ManagerData;
import com.ihateflyingbugs.hsmd.model.StudyInfoData;
import com.ihateflyingbugs.hsmd.popup.CompleteStudyTimePopup;
import com.ihateflyingbugs.hsmd.popup.GetFreeDayPopup;
import com.ihateflyingbugs.hsmd.popup.JoinFriendListActivity;
import com.ihateflyingbugs.hsmd.popup.KakaostoryPopup;
import com.ihateflyingbugs.hsmd.popup.RequestFreeCouponPopup;
import com.ihateflyingbugs.hsmd.popup.SetLockscreenPopUp;
import com.ihateflyingbugs.hsmd.popup.ShowCard;
import com.ihateflyingbugs.hsmd.popup.WordExamPopup;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.ihateflyingbugs.hsmd.service.ChatHeadService.TrayAnimationTimerTask;
import com.ihateflyingbugs.hsmd.service.CheckStudyRoomService;
import com.ihateflyingbugs.hsmd.service.DBService;
import com.ihateflyingbugs.hsmd.service.LearnService;
import com.ihateflyingbugs.hsmd.service.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;
import com.roomorama.caldroid.CalendarHelper;

import org.andlib.ui.PercentView;
import org.andlib.ui.StudyPercentView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import hirondelle.date4j.DateTime;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;

public class MainActivity extends FragmentActivity implements OnClickListener, OnKeyListener {
	static String TAG = "MainActivity";
	int temp_min = 0;

	TextView memorize_word;
	TextView review_word;
	TextView forgetting_curve_percentage_count;
	ProgressBar forgetting_curve_percentage_bar;

	TextView tvWillMessage, tvMemoryMessage, tvHitMessage;

	LinearLayout progressLayout;

	TextView progressBackground, progressGauge;

	private boolean isLevelChanged = false;

	boolean startFlag = true;

	static public boolean restart_flag = true;

	int timer_sec = 20;
	float percentage;
	public static PercentView percentView1;
	public static CountDownTimer cTimer;
	private TrayAnimationTimerTask trayTimerTask;
	boolean upDownFlag = true;
	public static RelativeLayout timerOff;
	int percentage_lv = 0;

	float myX = 0;
	float myY = 0;

	Boolean timerFlag = true;
	RelativeLayout popupLayout;
	RelativeLayout graphLayout;
	public static RelativeLayout fullLayout;
	public static LinearLayout timeView;
	private int mStartDragX;
	private int mPrevDragX;
	private int mPrevDragY;
	private static final int MARGIN_BOTTOM_DP = 25; // BOTTOM MARGIN of the tray
	// in dps
	private static final int TRAY_DIM_X_DP = 48; // Width of the tray in dps
	private static final int TRAY_DIM_Y_DP = 48; // Height of the tray in dps
	private WindowManager mWindowManager; // Reference to the window
	private LayoutParams mRootLayoutParams; // Parameters of the
	// root layout
	private LayoutParams mRootLayoutParams1;
	private LayoutParams mRootLayoutParams2;
	private LayoutParams mRootLayoutParams3;
	private LayoutParams ProgressParams;

	LinearLayout will;
	LinearLayout memory;
	LinearLayout hit;
	LinearLayout ll_main_offlinelesson_card;

	TextView hit_progress_gauge, hit_progress_background;
	android.widget.LinearLayout.LayoutParams hit_progress_gauge_param;
	android.widget.LinearLayout.LayoutParams hit_progress_backgroud_param;

	private TextView tvSystemName;
	private TextView tvUserName;

	private static int mCurrentFragmentIndex;

	private int mMainWordIndex;

	public final static String BOARD_URL = "http://lnslab.com/notice.php";

	public final static int FRAGMENT_WORD_KNOWN_LIST = 0;
	public final static int FRAGMENT_WORD_SCORE_LIST = 1;
	public static final int FRAGMENT_EXAM_KNOWN_LIST = 2;
	public static final int FRAGMENT_EXAM_SCORE_LIST = 3;

	public final static int FRAGMENT_SETTING = 4;
	public final static int FRAGMENT_MYSCORE = 5;
	public final static int FRAGMENT_MYINFO = 6;
	public final static int FRAGMENT_INVITE = 7;
	public final static int FRAGMENT_BOARD = 8;
	public final static int FRAGMENT_QNA = 9;
	public final static int FRAGMENT_CULC = 10;

	private static final int RESULT_MODE = 100;

	private static final int TOP_HEIGHT = 700;

	private SlidingMenu menu;

	private ActionBar bar;
	private static LinearLayout ll_actionbar;
	public static LinearLayout ll_action_review;
	static private TextView tv_action_review;
	private View actionbar_main;

	TextView today_tv;

	private RelativeLayout rl_main_coupon_card;
	private ListView lv_main_coupon;
	private TextView tv_main_free_period;
	private Button bt_main_free_period_prolong;
	private static List<Coupon> items;

	// KARAM : Exam
	RelativeLayout mTakeExam_Layout;
	Button mTakeExam_Button;



	private AudioManager audio;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("activitygg", "ondestory");
		// Intent serviceIntent = new Intent(this, ChatHeadService.class);
		// serviceIntent
		// .putExtra("msg",
		// "Hello World!!\nHello Hello Hello\nHello Hello\nHello Hello\nHello Hello");
		//
		// Random ran = new Random();
		// int random = ran.nextInt(6);
		//
		// serviceIntent.putExtra("flag", random);
		// startService(serviceIntent);
	}

	private static CheckBox chkSetting;
	private CheckBox chkMode;
	static private TextView tvTitle, tvLevel;

	private boolean isExam = false;
	private String query;

	private boolean isBackPressed = false;

	static SharedPreferences mPreference;
	SharedPreferences.Editor editor;

	private static Activity thisActivity = null;
	private Handler handler;
	static Context context;

	private static DBPool db;
	List<Feed> list;
	static ListView lv_feedback_main;
	MyCustomAdapter adapter;

	String thumbnailURL;

	public static TextView tv_show_reviewexplain;

	public static LinearLayout notice_layout;

	public final int ScrollLearningStatus = 0;

	String feedback_starttime;
	String feedback_startdate;

	Map<String, String> levelParams = new HashMap<String, String>();
	Map<String, String> statusParams = new HashMap<String, String>();
	Map<String, String> lrnStausOrTmLineParams = new HashMap<String, String>();

	MildangDate date = new MildangDate();
	Date Flurry_date = new Date();

	/*
	 * 시계부분
	 */
	public static RelativeLayout timerLayout;
	TextView tv_using_time_min;
	TextView tv_using_time_sec;
	TextView tvDailyUseTime;
	TextView setting;
	TextView tv_faq;
	TextView tvGoalAchievement;

	ProgressBar pb_main_coupon;

	TextView tvToday;
	int use_sec, use_min;
	String use_sec_st, use_min_st;
	int usingTime;

	public static boolean activityFlag = true;


	@Override
	public void onPause() {
		super.onPause();
		SharedPreferences.Editor editor = mPreference.edit();

	}

	Timer Finish_timer = new Timer();
	boolean isFinish = true;
	boolean isKakao = false;
	boolean isGotFreeDate = false;
	public static boolean is1FreeDay = false;


//		Tracker mTracker;

	@Override
	public void onResume() {

		super.onResume();

		Log.d("check_activity", "onResume");
//				mTracker.setScreenName("MainWordActivity");
//				mTracker.send(new HitBuilders.ScreenViewBuilder().build());
		try {
			Class service = Class
					.forName("com.ihateflyingbugs.hsmd.service.ChatHeadService");
			Intent intent = new Intent(this, service);
			intent.setPackage("com.ihateflyingbugs.hsmd.service");
			stopService(intent);
		} catch (ClassNotFoundException e) {
		}

		try {
			if (getIntent().getExtras().getBoolean("kakao_start") && !isKakao) {
				Toast.makeText(MainActivity.this,
						"카카오톡 차단 기능이 실행되었습니다.\n원치 않으시면 의지페이지 하단에서 차단해제를 해주세요",
						Toast.LENGTH_SHORT).show();
				isKakao = true;
			}
		} catch (NullPointerException e) {
			// TODO: handle exception
		}

		if(db.getMforget() == 0){
			mPreference.edit().putString(MainValue.GpreReviewPeriod, "").commit();
		}

		mPreference.edit().putString(MainValue.GpreContactLog, new MildangDate().get_hour()).commit();
		mPreference.edit().putString(MainValue.GpreLastLoginDate, new MildangDate().get_today()).commit();


		mPreference.edit().putBoolean(MainValue.GpreLast1PushFlag, true).commit();
		mPreference.edit().putBoolean(MainValue.GpreLast2PushFlag, true).commit();

		setProfile();

		try {
			Log.d("pushpush", db.getPushTable(1) + ", " + db.getPushTable(2));
			//하루 연장 푸시

			if(is1FreeDay){
				Intent intent = new Intent(MainActivity.this, GetFreeDayPopup.class);
				startActivityForResult(intent, Activity.RESULT_OK);
			}else if(db.getPushTable(3)){
				startActivity(new Intent(MainActivity.this, WordExamPopup.class));
				//카카오스토리 푸시
			}else if(db.getPushTable(2)){
				startActivity(new Intent(MainActivity.this, KakaostoryPopup.class));
				//친구 가입 푸시
			}else if(db.getPushTable(1)){
				startActivity(new Intent(MainActivity.this, JoinFriendListActivity.class));
			}

		} catch (NullPointerException e) {
			// TODO: handle exception
		}

		try {
			Class service = Class
					.forName("com.ihateflyingbugs.hsmd.service.ChatHeadService");
			Intent intent = new Intent(this, service);
			intent.setPackage("com.ihateflyingbugs.hsmd.service");
			stopService(intent);
		} catch (ClassNotFoundException e) {
		}

		// 2:30을 00:00으로 하는 Date 클래스
		MildangDate m_date = new MildangDate();

		// x일 전 날짜를 구하기 위한 클래스
		DateTime dateTime = CalendarHelper
				.convertDateToDateTime(new java.util.Date());

		String tempMonth, tempDay;

		setActionBar(true);

		// 현재 날짜
		String current_date = m_date.get_year() + m_date.get_Month()
				+ m_date.get_day();
		String current_time = m_date.get_hour() + m_date.get_minute()
				+ m_date.get_second();
		Log.d("date&time", current_date + ", " + current_time);

		// db에 저장된 날짜 _ 저장된 날짜가 없는 경우 당일 날짜를 가져온다.
		String saved_date = db.getDate();

		int tempInteger = 1;

		if (current_date.equals(saved_date)) {
			// preference에 저장된 날짜와 현재 날짜가 같은 경우 아무 것도 하지 않는다.
			Log.d("if_date", current_date + " == " + saved_date);
		} else {
			// preference에 저장된 날짜와 현재 날짜가 다른 경우

			// 에러 발생시 하루에 한번 csv파일을 업로드 하도록하는 플래그 초기화
			mPreference.edit().putBoolean(MainValue.GpreErrorSQLSend, true).commit();
			// csv파일을 업로드한다.

			Log.v("async_list", "Async_upload_sqlite_file : ready");

			new RetrofitService().getStudyInfoService().retroInsertStudentForgettingCurves(db.getStudentId(),
					db.exportForgettingCurvesTableToJson(),
					"0")
					.enqueue(new Callback<StudyInfoData>() {
						@Override
						public void onResponse(Response<StudyInfoData> response, Retrofit retrofit) {

						}

						@Override
						public void onFailure(Throwable t) {

						}
					});
			Log.d("if_date", current_date + " != " + saved_date);
			// 학습시간을 업로드한다
			int useTime[] = db.getTime();

			new RetrofitService().getStudyInfoService().retroInsertStudentCalendarData(db.getStudentId(),
					db.exportCalendarDataTableToJson()[0])
					.enqueue(new Callback<StudyInfoData>() {
						@Override
						public void onResponse(Response<StudyInfoData> response, Retrofit retrofit) {

							Log.e("server_test", "success");
						}

						@Override
						public void onFailure(Throwable t) {

							Log.e("server_test", db.exportCalendarDataTableToJson()[0]);
						}
					});
			// 학습량을 feeds 테이블에 저장한다.
			db.putDay_Of_Study();
			while (true) {

				// 하루 전의 월은 구한다.
				if (dateTime.minusDays(tempInteger).getMonth() > 9) {
					tempMonth = dateTime.minusDays(tempInteger).getMonth().toString();
				} else {
					tempMonth = "0" + dateTime.minusDays(tempInteger).getMonth().toString();
				}

				// 하루 전의 일을 구한다.
				if (dateTime.minusDays(tempInteger).getDay() > 9) {
					tempDay = dateTime.minusDays(tempInteger).getDay().toString();
				} else {
					tempDay = "0" + dateTime.minusDays(tempInteger).getDay().toString();
				}

				// 하루 전의 연을 구하고 이전에 구한 월, 일과 합친다.
				String tempDate = dateTime.minusDays(tempInteger++).getYear().toString() + tempMonth + tempDay;
				Log.d("c_Date", saved_date + " : " + tempDate);

				// 저장된 날짜와 같으면 저장된 날짜에 덮어쓰기 전 break
				if (tempDate.equals(saved_date)||tempDate.equals("20160401")||tempDate.equals("20170101")) {
					break;
				}

				// 저장된 날짜와 현재로부터 x 이전 날짜가 다를 경우 db에 0분 저장
				db.putCalendarData_customTime(tempDate, 0, 0, 0);

			}

			// 저장된 날짜에 공부 시간 저장
			db.putCalendarData(saved_date);

			// 현재 날짜 저장
			db.insertDate(current_date);

			// 학습시간 초기화
			db.removeStudyTime();

			today_tv.setText(new MildangDate().get_Month() + "."
					+ new MildangDate().get_day());

		}

		switch (Config.WORD_TOPIC) {
			case 1:
				Config.MAX_DIFFICULTY = 2;
				Config.MIN_DIFFICULTY = 1;
				choiced_topic = "수능";
				break;
			case 2:
				Config.MAX_DIFFICULTY = 4;
				Config.MIN_DIFFICULTY = 1;
				choiced_topic = "토익";
				break;
			case 3:
				Config.MAX_DIFFICULTY = 6;
				Config.MIN_DIFFICULTY = 1;
				choiced_topic = "토플";
				break;
			default:
				break;
		}

		Config.MAX_DIFFICULTY = 5;

		menu.setOnOpenListener(new SlidingMenu.OnOpenListener() {

			@Override
			public void onOpen() {
				// TODO Auto-generated method stub

				pause(cTimer);
				setProfile();

			}
		});
		menu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {

			@Override
			public void onOpened() {
				// TODO Auto-generated method stub

				restart_flag = false;

				feedback_starttime = String.valueOf(System.currentTimeMillis());
				feedback_startdate = Flurry_date.get_currentTime();
				FlurryAgent.logEvent("BaseActivity:OpenLearningStatus");
				// set_graph_color(check_graph, check_text);


			}
		});

		menu.setOnCloseListener(new SlidingMenu.OnCloseListener() {

			@Override
			public void onClose() {

				// cTimer = timer(percentView1, "Resume_setOnCloseListener");
				cTimer.start();//
				restart_flag = true;
				// TODO Auto-generated method stub
				try {


					Map<String, String> feedbackParam = new HashMap<String, String>();
					feedbackParam.put("Start", feedback_startdate);
					feedbackParam.put("End", Flurry_date.get_currentTime());
					feedbackParam.put(
							"Duration",
							""
									+ ((Long.valueOf(System.currentTimeMillis()) - Long
									.valueOf(feedback_starttime)))
									/ 1000);

					FlurryAgent.logEvent("BaseActivity:CloseLearningStatus",
							feedbackParam);
				} catch (NumberFormatException e) {
					// TODO: handle exception
					Map<String, String> feedbackExeptParam = new HashMap<String, String>();
					feedbackExeptParam.put("Exception", "NumberFomatException");
					FlurryAgent.logEvent("BaseActivity:CloseLearningStatus",
							feedbackExeptParam);
				}
			}
		});
		isFinish = false;
		if (tvLevel != null)
			tvLevel.setText("Level : " + Config.Difficulty);

		getAsyncUserOwnInfo();

		ArrayList<word_log> list =db.get_word_log();
		Log.e("Async_upload_word_log", ""+list.size());
		if(list.size()>10){
			Gson gson = new GsonBuilder().create();
			JsonArray myCustomArray = gson.toJsonTree(list).getAsJsonArray();
			String word_log = myCustomArray.toString();
			Log.e("Async_upload_word_log", ""+word_log);
			//140013340   db.getKakaoId()
			new RetrofitService().getStudyInfoService().retroInsertWordLog(db.getStudentId(),
																			word_log)
					.enqueue(new Callback<StudyInfoData>() {
						@Override
						public void onResponse(Response<StudyInfoData> response, Retrofit retrofit) {
							db.update_word_log();
							Log.e("KARAM", "Resonponse");
						}

						@Override
						public void onFailure(Throwable t) {
							Log.e("KARAM", "Exception");
						}
					});

		}

		Log.d("check_activity", "onResume : end");
	}

	private void getAsyncUserOwnInfo() {
		// TODO Auto-generated method stub

		new RetrofitService().getAuthorizationService().retroGetFinishDate(db.getStudentId())
				.enqueue(new Callback<AuthorizationData>() {
					@Override
					public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {
						Log.v("async_list", "2");
						final String finish_date = response.body().getFinish_date();
						int use_state = response.body().getStudent_use_state();
						if (use_state == 2) {
							handler.post(new Runnable() {
								@Override
								public void run() {
									Log.v("async_list", "3");
									// TODO Auto-generated method stub
									tv_main_free_period.setText(finish_date + "까지");
									rl_main_coupon_card.setVisibility(View.VISIBLE);
									RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) will.getLayoutParams();
									params.topMargin = Utils.dpToPixels(3, getResources());
									will.setLayoutParams(params);
								}
							});
						} else {
							handler.post(new Runnable() {

								@Override
								public void run() {
									Log.v("async_list", "4");
									// TODO Auto-generated method stub
									rl_main_coupon_card.setVisibility(View.GONE);
									RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) will.getLayoutParams();
									params.topMargin = Utils.dpToPixels(15, getResources());
									will.setLayoutParams(params);
								}
							});

						}
					}

					@Override
					public void onFailure(Throwable t) {

					}
				});

		new RetrofitService().getPaymentService().retroGetUsableCoupon(db.getStudentId())
				.enqueue(new Callback<CouponData>() {
					@Override
					public void onResponse(final Response<CouponData> response, Retrofit retrofit) {

						handler.post(new Runnable() {
							@Override
							public void run() {
								items.clear();
								CouponData coupon_data = response.body();
								if( coupon_data.getCount()!=0){
									//수정필요
									List<CouponData.Coupon> couponList = coupon_data.getCoupon();
									for (int i = 0; i < couponList.size(); i++) {
										int prime_num = couponList.get(i).getCoupon_type_id();
										String name = couponList.get(i).getCoupon_type_name();
										String explain_coupon = couponList.get(i).getCoupon_type_info();
										String valid_date = couponList.get(i).getStudent_coupon_valid_date();
										Boolean duplication_flag = couponList.get(i).getIs_duplicated() == 0 ? true : false;

										final Coupon coupon = new Coupon(prime_num, name, duplication_flag, explain_coupon, valid_date);

										items.add(coupon);
									}

									MainCouponArrayAdapter adapter = new MainCouponArrayAdapter(getApplicationContext(), items);
									LayoutParams llparams = (android.widget.RelativeLayout.LayoutParams)lv_main_coupon.getLayoutParams();
									llparams.height = 0;
									for (int i = 0; i < items.size(); i++) {
										llparams.height = llparams.height + Utils.dpToPixels(48, getResources());
										Log.v("async_list", "7");
									}
									lv_main_coupon.setLayoutParams(llparams);
									lv_main_coupon.setAdapter(adapter);

									lv_main_coupon.setVisibility(View.VISIBLE);
									pb_main_coupon.setVisibility(View.GONE);Utils.dpToPixels(48, getResources());
								}else{
									MainCouponArrayAdapter adapter = new MainCouponArrayAdapter(getApplicationContext(), items);
									LayoutParams llparams = (android.widget.RelativeLayout.LayoutParams)lv_main_coupon.getLayoutParams();
									llparams.height = 0;
									for (int i = 0; i < items.size(); i++) {
										llparams.height = llparams.height +  Utils.dpToPixels(48, getResources());
										Log.v("async_list", "7");
									}

									lv_main_coupon.setLayoutParams(llparams);
									lv_main_coupon.setAdapter(adapter);
									lv_main_coupon.setVisibility(View.VISIBLE);
									pb_main_coupon.setVisibility(View.GONE);
								}

							}
						});
					}

					@Override
					public void onFailure(Throwable t) {
						MainCouponArrayAdapter adapter = new MainCouponArrayAdapter(getApplicationContext(), items);
						LayoutParams llparams = (android.widget.RelativeLayout.LayoutParams)lv_main_coupon.getLayoutParams();
						llparams.height = 0;
						for (int i = 0; i < items.size(); i++) {
							llparams.height = llparams.height + Utils.dpToPixels(48, getResources());
							Log.v("async_list", "7");
						}

						lv_main_coupon.setLayoutParams(llparams);
						lv_main_coupon.setAdapter(adapter);
						lv_main_coupon.setVisibility(View.VISIBLE);
						pb_main_coupon.setVisibility(View.GONE);
					}
				});

	}

	ImageView view_topnavi;
	Animation anim;;

	TextView tv_tuto_graph;

	String choiced_topic = "수능";

	float oX;
	float oY;
	float iX;
	float iY;

	float density;

	private int mSlop;

	Display display;


	boolean isTutoStart= true;




	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

//		VOCAconfig application = (VOCAconfig) getApplication();
//				mTracker = application.getDefaultTracker();

		display = ((WindowManager) this.getSystemService(getApplicationContext().WINDOW_SERVICE)).getDefaultDisplay();
		width = display.getWidth();

		density = this.getResources().getDisplayMetrics().density;

		Log.d("check_activity", "onCreate");

		Intent receiveIntent = getIntent();

		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		thisActivity = this;

		bar = thisActivity.getActionBar();
		actionbar_main = thisActivity.getLayoutInflater().inflate(R.layout.main_action_bar, null);

		mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE | MODE_MULTI_PROCESS);

		try {
			isGotFreeDate = getIntent().getExtras().getBoolean("pushresponse");
		} catch (Exception e) {
			// TODO: handle exception
		}

		try {
			is1FreeDay = getIntent().getExtras().getBoolean("1freeday");
		} catch (Exception e) {
			// TODO: handle exception
		}




		/*
		 * 시계부분
		 */


		bar.setCustomView(actionbar_main, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		ll_actionbar = (LinearLayout) actionbar_main.findViewById(R.id.ll_actionbar);
		ll_actionbar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (notice_layout.getVisibility() == View.VISIBLE) {
					notice_layout.setVisibility(View.INVISIBLE);
					notice_layout.startAnimation(new AnimationUtils().loadAnimation(getApplicationContext(), android.R.anim.fade_out));
				}
			}
		});
		ll_action_review = (LinearLayout) actionbar_main.findViewById(R.id.ll_action_review);

		view_topnavi = (ImageView) actionbar_main.findViewById(R.id.view_topnavi);
		view_topnavi.setOnClickListener(this);

		ll_action_review.setOnClickListener(this);
		chkSetting = (CheckBox) actionbar_main.findViewById(R.id.chkSetting);
		chkSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (notice_layout.getVisibility() == View.VISIBLE) {
					notice_layout.setVisibility(View.INVISIBLE);
					notice_layout.startAnimation(new AnimationUtils().loadAnimation(getApplicationContext(), android.R.anim.fade_out));
				}
			}
		});
		tv_action_review = (TextView) actionbar_main.findViewById(R.id.tv_action_review);
		makeActionBar(isExam);
		context = getApplicationContext();
		handler = new Handler();
		List<String> list = new ArrayList<String>();
		// list.add("aa@aa.com");
		// list.add("aaaaaa");
		// list.add(GCMRegistrar.getRegistrationId(getApplicationContext()));
		// list.add(Get_my_uuid.get_Device_id(mContext));
		//
		// new Async_login(list, MainActivity.this).execute();
		Log.e("hahaha", "" + GCMRegistrar.getRegistrationId(getApplicationContext()));
		if (MainValue.isLowDensity(getApplicationContext())) {
			setContentView(R.layout.text_activity_main_small);
		} else {
			setContentView(R.layout.text_activity_main);
		}






		/*
		 * 시계부분
		 */

		tv_using_time_min = (TextView) findViewById(R.id.tv_goal_time_min);
		tv_using_time_sec = (TextView) findViewById(R.id.tv_goal_time_sec);

		tv_show_reviewexplain = (TextView) findViewById(R.id.tv_show_reviewexplain);
		notice_layout = (LinearLayout) findViewById(R.id.notice_layout);

		db = DBPool.getInstance(this);


		//		ArrayList<Integer> code_list = db.get_known_word();
		//
		//		for (int i =0; i<code_list.size(); i++){
		//			db.insertWordLog(code_list.get(i), -1, 1);
		//		}
		//

		sendWorkEachCount(db);


		DBState[] st = db.getForgettingCurvesAsDBState(); // declare DBState
		Log.e("dbservice", "3");
		db.fitForgettingCurves(st); // fitting forgetting curves

		Date fdate = new Date();

		if(isGotFreeDate){
			String[] freedata = db.getFreeTable();
			if(freedata[0].toString().equals("00/00/00")){
				db.setFreeTable("true", ""+fdate.get_Month()+"/"+fdate.get_day()+"/"+fdate.get_hour());
			}
		}


		if (db.getGoalTime() == 0) {
			db.insertGoalTime(20);
		}

		GetWillCoachMent coachMent = new GetWillCoachMent(db.getStudentId(), mPreference.getString(MainValue.GpreBirth, "990202"), mPreference.getInt(
				MainValue.GpreSatGrade, 3));
		//	GetWillCoachMent coachMent = new GetWillCoachMent("140013340", mPreference.getString(MainValue.GpreBirth, "990202"), mPreference.getInt(
		//		MainValue.GpreSatGrade, 3));


		Log.d("Coach Ment",
				db.getStudentId() + " " + mPreference.getString(MainValue.GpreBirth, "990202") + " " + mPreference.getInt(MainValue.GpreSatGrade, 3));

		//TEST








		tv_tuto_graph = (TextView) findViewById(R.id.tv_tuto_graph);
		tv_tuto_graph.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});

		try {
			Log.e("asdfasdf", "" + db.getWordLevel());
			Config.Difficulty = db.getWordLevel();
			db.insertWordLevel(Config.Difficulty);
		} catch (NumberFormatException e) {
			Config.Difficulty = 1;
			db.insertWordLevel(Config.Difficulty);
		}

		try {
			Config.CHANGE_LEVEL_COUNT = Integer.parseInt(mPreference.getString(MainActivitys.GpreLevelCounting, "20"));
			mPreference.edit().putString(MainActivitys.GpreLevelCounting, "" + Config.CHANGE_LEVEL_COUNT).commit();
		} catch (NumberFormatException e) {
			Config.CHANGE_LEVEL_COUNT = 20;
			// setMySharedPreferences(MainActivitys.GpreLevelCounting, 20);
		}
		try {
			Config.WORD_TOPIC = Integer.parseInt(mPreference.getString(MainActivitys.GpreTopic, "1"));
			mPreference.edit().putString(MainActivitys.GpreTopic, "" + Config.WORD_TOPIC).commit();
		} catch (NumberFormatException e) {
			// TODO: handle exception
			Config.WORD_TOPIC = 1;
			setMySharedPreferences(MainActivitys.GpreTopic, "1");
		}

		Log.e("leveltest", "현재 토픽     " + Config.WORD_TOPIC);

		Log.e("level", "" + Config.CHANGE_LEVEL_COUNT);

		anim = new AnimationUtils().loadAnimation(getApplicationContext(), R.anim.zoom_out_tuto_center);
		anim.setRepeatCount(500);
		anim.setRepeatMode(Animation.REVERSE);

		Log.d("firstStart", mPreference.getString("firststart", "0"));
		if (!mPreference.getString("firststart", "0").equals("1") && isTutoStart) {

			Log.d("firstStart", "inininin");
			view_topnavi.setVisibility(View.GONE);
			tv_tuto_graph.setVisibility(View.GONE);
			Intent intent = new Intent(MainActivity.this, PayTutoActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			thisActivity.startActivityForResult(intent, 55555);
			isTutoStart = false;
			// tv_tuto_graph.bringToFront();
		}else if(!mPreference.getString("lockstart", "0").equals("1")&&isTutoStart){
			view_topnavi.setVisibility(View.GONE);
			tv_tuto_graph.setVisibility(View.GONE);
			Intent intent = new Intent(MainActivity.this, SetLockscreenPopUp.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			thisActivity.startActivityForResult(intent, 55555);
			isTutoStart = false;
		}

		items = new ArrayList<Coupon>();

		makeSlidingMenu();

		mCurrentFragmentIndex = FRAGMENT_WORD_SCORE_LIST;

		mMainWordIndex = mCurrentFragmentIndex;
		fragmentReplace(mCurrentFragmentIndex);

		// SharedPreferences.Editor editor = settings.edit();

		// startService(new Intent(this, LockService.class));

		Log.d("kjw", "db service start");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, 10);

		Log.e("truecount", "" + Config.USER_ID + "      " + getMySharedPreferences(MainActivitys.GpreEmail));

		chkSetting.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				menu.toggle();

				// set_graph_color(check_graph, check_text);
			}
		});

		editor = mPreference.edit();

		/*
		 *
		 * 1시간마다 db갱신하는 부분이지만 나중에 따로 체크를 해봐야한다 현재는 다음 단어장 불러오기를 했을때 db갱신을 한다.
		 */
		Config.NAME = getMySharedPreferences(MainActivitys.GpreName);
		Intent intent = new Intent(this, DBService.class);
		PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		if(!isMyServiceRunning(DBService.class)){
			Log.d("SERVISESEES", "DBService, not running");
			alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_HOUR/60, pintent);
		}

		if(mPreference.getBoolean("studyroom", false)){
			//30분에 한번씩 와이파이 체크를 한다.
			Intent intent2 = new Intent(MainActivity.this, CheckStudyRoomService.class);
			intent2.setFlags(intent2.FLAG_ACTIVITY_NEW_TASK|intent2.FLAG_ACTIVITY_CLEAR_TOP);
			PendingIntent pintent2 = PendingIntent.getService(MainActivity.this, 0, intent2, 0);
			AlarmManager alarm2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

			alarm2.cancel(pintent2);
			stopService(intent2);
			alarm2.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), (AlarmManager.INTERVAL_HOUR)/2, pintent2);
		}



		// 시간단위로 작동, 마지막 접속일로부터 지난 날짜 확인
		Intent timeIntent = new Intent(this, LearnService.class);
		PendingIntent pTimeIntent = PendingIntent.getService(this, 0, timeIntent, 0);
		AlarmManager timeAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		if(!isMyServiceRunning(LearnService.class)){
			Log.d("SERVISESEES", "TIMER , not running");
			timeAlarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 3600000, pTimeIntent);
		}

		if(!isMyServiceRunning(LockService.class)){
			Log.e("serviceCheck", "Mainactivity : start service");
			Intent intent1 = new Intent(LockService.LOCK_SERVICE);
			intent1.setPackage("com.ihateflyingbugs.hsmd.lock");
			startService(intent1);
		}

		Log.e("alma", "mainactivity alarm manager");

		tv_using_time_min = (TextView) findViewById(R.id.tv_goal_time_min);
		tv_using_time_sec = (TextView) findViewById(R.id.tv_goal_time_sec);
		// /////////////////////////////////////////////////////////////////////////////////////

		timerOff = (RelativeLayout) findViewById(R.id.timer_off);

		percentView1 = (PercentView) findViewById(R.id.percentView1);
		graphLayout = (RelativeLayout) findViewById(R.id.graphLayout);

		if (mPreference.getBoolean("showFlag", true) == false) {
			timerOff.setVisibility(View.VISIBLE);
			percentView1.setVisibility(View.INVISIBLE);
			Log.d("show_flag", "false");
		} else {
			percentView1.setVisibility(View.VISIBLE);
			timerOff.setVisibility(View.INVISIBLE);
			Log.d("show_flag", "true");
		}

		today_tv = (TextView) findViewById(R.id.today_tv);
		today_tv.setText(new MildangDate().get_Month() + "." + new MildangDate().get_day());

		fullLayout = (RelativeLayout) findViewById(R.id.fullLayout);
		timeView = (LinearLayout) findViewById(R.id.timeView);

		timerLayout = (RelativeLayout) findViewById(R.id.percentViewLayout);

		mRootLayoutParams = new LayoutParams(Utils.dpToPixels(TRAY_DIM_X_DP, getResources()), Utils.dpToPixels(TRAY_DIM_Y_DP, getResources()));

		mRootLayoutParams.width = display.getWidth() - 31 * (int) density;
		mRootLayoutParams.height = display.getHeight();
		mRootLayoutParams.setMargins(0, 0, 0, 32 * (int) density);

		// display.getHeight()

		fullLayout.updateViewLayout(timerLayout, mRootLayoutParams);

		ViewConfiguration vc = ViewConfiguration.get(timerLayout.getContext());
		mSlop = vc.getScaledTouchSlop();

		// dca.setAnimButton("메롱", textPaint, R.drawable.btn_tutorial_start);

		graphLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (notice_layout.getVisibility() == View.VISIBLE) {
					notice_layout.setVisibility(View.INVISIBLE);
					notice_layout.startAnimation(new AnimationUtils().loadAnimation(getApplicationContext(), android.R.anim.fade_out));
				}

				Log.d("activityFlag1", Boolean.toString(activityFlag));

				if (activityFlag) {

					pause(cTimer);
					activityFlag = false;
					Log.d("activityFlag2", Boolean.toString(activityFlag));
					// TODO Auto-generated method stub
					float tmp_width = mRootLayoutParams.width;
					float tmp_height = mRootLayoutParams.height;
					int bar_height = getActionBar().getHeight();
					myY = graphLayout.getY();
					int myHeight = graphLayout.getHeight();

					restart_flag = false;

					Intent i = new Intent(MainActivity.this, TimerActivity.class);

					// 액티비티 생성 애니메이션 삭제
					overridePendingTransition(0, 0);

					// 타이머 위치 정보
					i.putExtra("bar", bar_height);
					i.putExtra("width", tmp_width);
					i.putExtra("height", tmp_height);
					i.putExtra("myY", myY);
					i.putExtra("myHeight", myHeight);

					int[] saved_time = db.getTime();

					i.putExtra("min", saved_time[0]);
					i.putExtra("sec", saved_time[1]);

					i.putExtra("percentage", percentage);
					startActivity(i);
					FlurryAgent.logEvent("BaseActivity:OpenTimerPage");
				}
			}
		});

		// mRootLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;

		graphLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				mRootLayoutParams.setMargins(0, 0, 0, 0);
				// TODO Auto-generated method stub
				boolean onClick = false;

				final int action = event.getActionMasked();

				switch (action) {

					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_MOVE:
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_CANCEL:
						onClick = dragTray(action, (int) event.getRawX(), (int) event.getRawY());

				}
				Log.d("qqqq", "onClick : " + Boolean.toString(onClick));
				return onClick;
			}
		});

		cTimer = timer(percentView1, "Create");
		cTimer.start();

		setActionBar(true);

		db.createStudyTime();

		// db.getCountOfUserStudiedWords();
		// db.getCountOfWordsAsGrade();
		// Log.e("test", "test");

		new RetrofitService().getManagerService().retroIsExistTest2(db.getStudentId())
				.enqueue(new Callback<ManagerData>() {
					@Override
					public void onResponse(final Response<ManagerData> response, Retrofit retrofit) {
						handler.postDelayed(new Runnable() {

							@Override
							public void run() {
								int mReturn_SuccessCode;
								mReturn_SuccessCode = response.body().getResult();
								ManagerInfo.getInstance(context).setAsyncReturnCode(mReturn_SuccessCode);

								Log.e("KARAM", "   ReturnCode : " + mReturn_SuccessCode);

								boolean bExist_Manager = response.body().isExist_manager();
								Log.e("KARAM", "  Manager Exist : " + bExist_Manager);

								if(bExist_Manager) {
									mTakeExam_Layout.setVisibility(View.GONE);
									ManagerInfo.getInstance(getApplicationContext()).setTeacher_id(response.body().getTeacher_id());
									Log.e("KARAM", "  Manager Exist : " + ManagerInfo.getInstance(getApplicationContext()).getTeacher_id());
								}
								else {
									//	mTakeExam_Layout.setVisibility(View.VISIBLE);
									ll_main_offlinelesson_card.setVisibility(View.GONE);
								}


								if(mReturn_SuccessCode ==1||mReturn_SuccessCode ==2){
									startActivity(new Intent(MainActivity.this, Manager_ReadyExamPopup.class));
								}
							}
						}, 2000);
					}

					@Override
					public void onFailure(Throwable t) {
						Log.e(TAG, "retroIsExistTest onFailure : " + t.toString());
					}
				});


	}

	boolean clickflag = false;
	boolean backFlag = false;

	private boolean dragTray(int action, int x, int y) {

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				// Cancel any currently running animations/automatic tray movements.
				if (trayTimerTask != null) {
					trayTimerTask.cancel();
					trayTimerTask.cancel();
				}

				// Store the start points mStartDragX = x; // mStartDragY = y;
				mPrevDragX = x;
				mPrevDragY = y;

				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:

				if (backFlag) {
					AnimationTimerTask timerTask = new AnimationTimerTask(x);
					timerAnimationTimer = new Timer();
					timerAnimationTimer.schedule(timerTask, 0, 20);
					clickflag = true;
					backFlag = false;
				} else {
					clickflag = false;
				}

				break;

			case MotionEvent.ACTION_MOVE:
				// Calculate position of the whole tray according to the drag, and
				// update layout.

				float deltaX = x - mPrevDragX;
				float deltaY = y - mPrevDragY;

				if (!pointInView(deltaX, deltaY, mSlop)) {

					mRootLayoutParams.width = (x + 90);
					mRootLayoutParams.height = (y - 90);

					if (mRootLayoutParams.width < 200) {
						mRootLayoutParams.width = 200;
					}
					if (mRootLayoutParams.height < 200) {
						mRootLayoutParams.height = 200;
					}
					fullLayout.updateViewLayout(timerLayout, mRootLayoutParams);
					backFlag = true;
				} else {
					backFlag = false;
				}

				// case MotionEvent.ACTION_CANCEL:
				//
				// if (!pointInView(x - mPrevDragX, y - mPrevDragY, 150)) {
				// ttt = true;
				// Log.d("aaaaaaaaaaaaa", "ACTION_CANCEL");
				// AnimationTimerTask timerTask = new AnimationTimerTask(x);
				// timerAnimationTimer = new Timer();
				// timerAnimationTimer.schedule(timerTask, 0, 20);
				//
				// }else{
				// ttt = false;
				// Log.d("asdfasdf", "false");
				// }
				//
				//
				// onClick = ttt;
				//
				//
				// break;
		}

		return clickflag;
	}

	public static void anim_ListView() {
		lv_feedback_main.setVisibility(View.VISIBLE);
		lv_feedback_main.startAnimation(new AnimationUtils().loadAnimation(
				context, R.anim.slide_in_right));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// outState.putString("WORKAROUND_FOR_BUG_19917_KEY",
		// "WORKAROUND_FOR_BUG_19917_VALUE");
		// super.onSaveInstanceState(outState);
		// invokeFragmentManagerNoteStateNotSaved();
	}

	public void fragmentReplace(int reqNewFragmentIndex) {

		Fragment fragment = getFragment(reqNewFragmentIndex);

		Log.e("fragments", " " + reqNewFragmentIndex);
		// replace fragment
		final FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.linearFragment, fragment).addToBackStack(null)
				.commit();
	}

	private Fragment getFragment(int idx) {
		Fragment newFragment = null;

		switch (idx) {
			case FRAGMENT_WORD_SCORE_LIST:
				newFragment = new WordListFragment();
				break;
			case FRAGMENT_WORD_KNOWN_LIST:
				newFragment = new WordListFragment();
				break;
			case FRAGMENT_EXAM_KNOWN_LIST:
				newFragment = new WordListFragment();
				break;
			case FRAGMENT_EXAM_SCORE_LIST:
				newFragment = new WordListFragment();
				break;

			case FRAGMENT_MYSCORE:
				newFragment = new MyScoreFragment();
				break;


			default:
				break;
		}

		return newFragment;
	}

	private void makeActionBar(boolean isExam) {

		// if(isExam)
		// {
		// actionbar_main =
		// thisActivity.getLayoutInflater().inflate(R.layout.main_action_bar_exam,
		// null);
		// }
		// else
		// {

		// }

		Log.w("kjw", bar + "   " + actionbar_main);
		chkMode = (CheckBox) actionbar_main.findViewById(R.id.chkMode);
		tvTitle = (TextView) actionbar_main.findViewById(R.id.tvTitle);

		// btnKnown.setOnClickListener(this);
		// btnScore.setOnClickListener(this);
		// chkMode.setOnClickListener(this);
	}

	public static void setActionBar(boolean isExam) {
		// if (isExam) {

		boolean mforget = db.isReviewpriod();

		if (mforget) {
			ll_actionbar.setBackgroundColor(Color.parseColor("#e2aa21"));
			chkSetting.setBackgroundResource(R.drawable.main_actionbar_icon);
			ll_action_review.setVisibility(View.VISIBLE);
			tv_action_review.setText("" + db.getMforget());
			tv_show_reviewexplain
					.setText("현재 수집된 "
							+ getMySharedPreferences(MainValue.GpreName)
							+ "님의 기억 망각 주기 정보를 바탕으로 선정한 곧 잊어버릴지도 모르는 단어들의 갯수입니다.\n\n그러나, 지금 복습하시면 절대 안 잊어버릴 수 있는 단어이기도 합니다.");
		} else {
			mPreference = context.getSharedPreferences(Config.PREFS_NAME,
					MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE
							| MODE_MULTI_PROCESS);

			// 복습구간이 아닌 경우, review_flag를 true로
			// Editor editor = mPreference.edit();
			// editor.putBoolean(MainValue.GpreReviewFlag, true);
			// editor.commit();
			ll_actionbar.setBackgroundResource(R.drawable.main_actionbar);
			chkSetting.setBackgroundResource(R.drawable.main_actionbar_icon);
			ll_action_review.setVisibility(View.GONE);
		}

	}

	View header;

	TextView tv_feedback_username;
	TextView tv_feedback_current_newStudy;
	TextView tv_feedback_current_total;
	TextView tv_feedback_current_rewind;

	TextView tv_feedback_lv;
	LinearLayout ll_testfeed;
	LinearLayout ll_feed_lv;
	LinearLayout ll_level_graph;
	LinearLayout ll_slide_feedback;
	LinearLayout ll_slide_other;

	private View headerView;

	private ImageView mBlurredImage;
	private ImageView mNormalImage;
	private ImageView iv_edit_info;

	private StudyPercentView pv_totalgraph;

	private float alpha;
	private PercentView percentView[] = new PercentView[6];
	private ImageView iv_level[] = new ImageView[6];
	private TextView tv_level[] = new TextView[6];
	private TextView tv_explain_feed[] = new TextView[10];
	private View view_explain_left;
	private View view_explain_right;

	static boolean check_graph = true;
	static boolean check_text = true;

	LinearLayout ll_graph_second;
	private TextView tv_sec_explain_feed[] = new TextView[3];

	TextView tv_graph_main_cate;
	TextView tv_graph_main_count;

	TextView tv_lvcount_current;
	TextView tv_lvcount_total;

	View view_current_level_do;
	View view_current_level_rest;
	TextView tv_total_study_feed_title;

	TextView tv_info_total_study;
	TextView tv_feed_today;

	static public TextView tvCurrentLevel;
	TextView tvCurrentMemorize;
	ProgressBar pbCurrentMemorize;

	private Integer level_counting[][] = new Integer[6][2];

	BufferedInputStream bis;
	URL url;

	RelativeLayout rl_lock_hit;
	RelativeLayout rl_lock_will;
	RelativeLayout rl_lock_memory;
	ImageView iv_lock_hit;
	ImageView iv_lock_will;
	ImageView iv_lock_memory;

	ImageView iv_picture;

	public void makeSlidingMenu() {

		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		menu.setMenu(R.layout.activity_main_feedback);

		ListView lv_feedback_main = (ListView) menu
				.findViewById(R.id.lv_feedback_main);
		menu.setMarginLay(lv_feedback_main);
		list = new ArrayList<Feed>();

		header = getLayoutInflater().inflate(R.layout.tst_header_feedback,
				null, false);
		lv_feedback_main.addHeaderView(header);

		adapter = new MyCustomAdapter(getApplicationContext(), thisActivity,
				lv_feedback_main);

		// //layout lock
		// rl_lock_hit = (RelativeLayout) header.findViewById(R.id.rl_lock_hit);
		// rl_lock_will = (RelativeLayout)
		// header.findViewById(R.id.rl_lock_will);
		// rl_lock_memory = (RelativeLayout) header
		// .findViewById(R.id.rl_lock_memory);
		// iv_lock_hit = (ImageView) header.findViewById(R.id.iv_lock_hit);
		// iv_lock_will = (ImageView) header.findViewById(R.id.iv_lock_will);
		// iv_lock_memory = (ImageView)
		// header.findViewById(R.id.iv_lock_memory);
		//
		// rl_lock_hit.setOnClickListener(this);
		// rl_lock_will.setOnClickListener(this);
		// rl_lock_memory.setOnClickListener(this);

		// rl_lock_hit.setOnClickListener(this);
		// rl_lock_will.setOnClickListener(this);
		// rl_lock_memory.setOnClickListener(this);

		lv_feedback_main.setAdapter(adapter);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// DateTime todayDateTime = CalendarHelper.convertDateToDateTime(today);

		hit_progress_gauge = (TextView) header.findViewById(R.id.hit_progress_gauge);
		hit_progress_background = (TextView) header.findViewById(R.id.hit_progress_background);

		tvSystemName = (TextView) header.findViewById(R.id.tv_system_name);
		tvUserName = (TextView) header.findViewById(R.id.tv_user_name);
		iv_picture = (ImageView) header.findViewById(R.id.iv_picture);
		tvDailyUseTime = (TextView) header.findViewById(R.id.tv_daily_use_time);
		setting = (TextView) header.findViewById(R.id.tv_setting);
		tv_faq = (TextView) header.findViewById(R.id.tv_faq);





		tvGoalAchievement = (TextView) header
				.findViewById(R.id.tv_goal_achievement);
		// TextView tvMainMessage = (TextView)
		// header.findViewById(R.id.tv_main_message);
		tvWillMessage = (TextView) header.findViewById(R.id.will_textTv);
		tvMemoryMessage = (TextView) header.findViewById(R.id.memory_textTv);
		tvHitMessage = (TextView) header.findViewById(R.id.hit_textTv);
		tvToday = (TextView) header.findViewById(R.id.tv_today);
		tvCurrentLevel = (TextView) header.findViewById(R.id.tv_current_level);
		tvCurrentMemorize = (TextView) header
				.findViewById(R.id.tv_current_memorize);
		// pbCurrentMemorize = (ProgressBar) header
		// .findViewById(R.id.progressbar_current_memorize);

		switch (db.getWordLevel()) {
			case 1:
				tvCurrentLevel.setText("적중 기초 단어장");
				break;
			case 2:
				tvCurrentLevel.setText("적중 필수1 단어장");
				break;
			case 3:
				tvCurrentLevel.setText("적중 필수2 단어장");
				break;
			case 4:
				tvCurrentLevel.setText("적중 마스터 단어장");
				break;
			case 5:
				tvCurrentLevel.setText("적중 심화 단어장");
				break;

			default:
				break;
		}

		// for (int i = 0; i < my_grade_count.length; i++) {
		// if (my_grade_count[i][0] == mPreference.getInt(MainValue.GpreLevel,
		// 1))
		// HitActivity.tv_select_word_count.setText(Integer
		// .toString(my_grade_count[mPreference.getInt(
		// MainValue.GpreLevel, 1) - 1][1])
		// + "/"
		// + all_grade_count[mPreference.getInt(
		// MainValue.GpreLevel, 1) - 1][1]);
		// }

		// 강석아 이건 너가 저번에 한 텍스트뷰안에서 속성 다르게 줄수있는부분 추가하면될거같다
		tvUserName.setText(mPreference.getString(MainValue.GpreName, "이름없음")
				+ "님의\n밀당 영단어 분석 결과를 ");

		// ///////////////////////////////////////////////////////////////////////////////////////////////////////////
		// LinearLayout willBottom = (LinearLayout)
		// header.findViewById(R.id.will_bottom_layout);
		//
		//
		// Animation layoutBottomAnim = AnimationUtils.loadAnimation(this,
		// R.anim.main_bottom);
		// layoutBottomAnim.setInterpolator(AnimationUtils.loadInterpolator(thisActivity,
		// android.R.anim.overshoot_interpolator));
		// layoutBottomAnim.setAnimationListener(new AnimationListener() {
		//
		// @Override
		// public void onAnimationStart(Animation animation) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onAnimationRepeat(Animation animation) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onAnimationEnd(Animation animation) {
		// LinearLayout willBottomText = (LinearLayout)
		// header.findViewById(R.id.will_bottom_text_layout);
		//
		// Animation fadeInAnim = AnimationUtils.loadAnimation(thisActivity,
		// android.R.anim.fade_in);
		// fadeInAnim.setDuration(1000);
		// willBottomText.setVisibility(View.VISIBLE);
		// willBottomText.setAnimation(fadeInAnim);
		// }
		// });
		//
		// willBottom.setVisibility(View.VISIBLE);
		// willBottom.setAnimation(layoutBottomAnim);
		// willBottom.startAnimation(layoutBottomAnim);
		// willBottom.clearAnimation();

		// ///////////////////////////////////////////////////////////////////////////////////////////////////////////

		rl_main_coupon_card = (RelativeLayout) findViewById(R.id.rl_main_coupon_card);
		lv_main_coupon = (ListView) findViewById(R.id.lv_main_coupon);
		tv_main_free_period = (TextView) findViewById(R.id.tv_main_free_period);
		bt_main_free_period_prolong = (Button) findViewById(R.id.bt_main_free_period_prolong);

		bt_main_free_period_prolong.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Iterator<Coupon> iterator = items.iterator();
				int primeNum = 1;
				while (iterator.hasNext()) {
					Coupon coupon = iterator.next();
					if (coupon.isDuplicatable()) {
						primeNum *= coupon.getPrimeNum();
						Log.d("PRIMENUM", primeNum + ", " + iterator.hasNext() + coupon.getCouponName());
					}
				}


				Intent intent = new Intent(getApplicationContext(), ShowCard.class);
				intent.putExtra("use_state", 2);
				intent.putExtra("availability", 1);
				intent.putExtra("prime_num", primeNum);
				startActivity(intent);
			}
		});


		//////////////////////////////////////////////////////////////////////////////////////////////////////
		// KARAM : Exam
		mTakeExam_Layout = (RelativeLayout)findViewById(R.id.layout_mainfeedback_wordexam);
	/*	mTakeExam_Button = (Button)findViewById(R.id.btn_mainfeedback_wordexam_takeexam);
		mTakeExam_Button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, Manager_BetaSerivceActivity.class);
				startActivity(intent);
			}
		});

		//////////////////////////////////////////////////////////////////////////////////////////////////////
*/


		int memoryState = mPreference.getInt(Config.MEMORY_STATE, 0);
		int hitState = mPreference.getInt(Config.HIT_STATE, 0);

		db.getReviewCountByCalendar();

		mPreference.edit().putBoolean(MainValue.GpreFirstStart, true).commit();

		MildangDate today = new MildangDate();

		tv_faq.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this, FAQActivity.class);
				Log.d("settingtv", "onclick");
				FlurryAgent.logEvent("BaseActivity:OpenFAQPage");
				startActivity(i);
			}
		});

		setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, SettingActivity.class);
				Log.d("settingtv", "onclick");

				Iterator<Coupon> iterator = items.iterator();
				int primeNum = 1;

				while (iterator.hasNext()) {
					Coupon coupon = iterator.next();
					if (coupon.isDuplicatable()) {
						primeNum *= coupon.getPrimeNum();
						Log.d("PRIMENUM", primeNum + ", " + iterator.hasNext() + coupon.getCouponName());
					}
				}

				i.putExtra("prime_num", primeNum);
				i.putExtra("date", ""+tv_main_free_period.getText().toString());
				FlurryAgent.logEvent("BaseActivity:OpenSettingPage");
				startActivityForResult(i, 55555);
			}
		});

		iv_picture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this,
						com.ihateflyingbugs.hsmd.MyinfoActivity.class);
				FlurryAgent.logEvent("BaseActivity:OpenEditMyInfoPage");
				startActivityForResult(i, 55555);
			}
		});

		will = (LinearLayout) header.findViewById(R.id.ll_main_will_card);
		memory = (LinearLayout) header.findViewById(R.id.ll_main_memory_card);
		hit = (LinearLayout) header.findViewById(R.id.ll_main_hit_card);
		ll_main_offlinelesson_card = (LinearLayout) header.findViewById(R.id.ll_main_offlinelesson_card);

		// Button will = (Button) header.findViewById(R.id.willBtn);
		// Button memory = (Button) header.findViewById(R.id.memoryBtn);
		// Button hit = (Button) header.findViewById(R.id.hitBtn);

		TextView iv_feedback_invite = (TextView) header
				.findViewById(R.id.iv_feedback_invite);
		memorize_word = (TextView) header.findViewById(R.id.memorize_word);

		review_word = (TextView) header.findViewById(R.id.review_word);
		iv_feedback_invite.setOnClickListener(this);

		forgetting_curve_percentage_count = (TextView) header
				.findViewById(R.id.forgetting_curve_percentageTv);
		// forgetting_curve_percentage_bar = (ProgressBar) header
		// .findViewById(R.id.forgetting_curve_percentagePb);

		progressLayout = (LinearLayout) header
				.findViewById(R.id.progrss_layout);
		progressBackground = (TextView) header
				.findViewById(R.id.progrss_background);
		progressGauge = (TextView) header.findViewById(R.id.progrss_gauge);

		// ImageView img = new ImageView;
		// img.setLayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.FILL_PARENT, 1.0f);

		will.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this,
						com.ihateflyingbugs.hsmd.feedback.WillActivity.class);
				FlurryAgent.logEvent("BaseActivity:OpenWillPage");
				startActivity(i);
			}
		});

		memory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),
						com.ihateflyingbugs.hsmd.feedback.MemoryActivity.class);
				FlurryAgent.logEvent("BaseActivity:OpenMemoryPage");
				startActivity(i);
			}
		});

		hit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),
						com.ihateflyingbugs.hsmd.feedback.HitActivity.class);
				FlurryAgent.logEvent("BaseActivity:OpenHitPage");
				startActivityForResult(i, 23568);
			}
		});

		ll_main_offlinelesson_card.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						com.ihateflyingbugs.hsmd.OfflineLesson.OLActivity.MTTabsActivity.class);
				FlurryAgent.logEvent("BaseActivity:OpenOLTabPage");
				startActivity(i);
			}
		});

		final int screenWidth = ImageUtils.getScreenWidth(this);
		pb_main_coupon = (ProgressBar)menu.findViewById(R.id.pb_main_coupon);
		mBlurredImage = (ImageView) menu.findViewById(R.id.blurred_image);
		mNormalImage = (ImageView) menu.findViewById(R.id.normal_image);
		Date mdate = new Date();
		final int month = (mdate.getMonth()) % 4;
		try {
			if (month == Integer
					.parseInt(getMySharedPreferences(MainActivitys.M_DATE))) {
				final File del_Image = new File(getFilesDir()
						+ "blurred_image.png");
				del_Image.delete();
				setMySharedPreferences(MainActivitys.M_DATE,
						String.valueOf(month));
			}
		} catch (NumberFormatException e) {
			// TODO: handle exception
			setMySharedPreferences(MainActivitys.M_DATE, String.valueOf(month));
		}

		final File blurredImage = new File(getFilesDir() + "blurred_image.png");
		Log.e("asdf", "" + getFilesDir().toString());
		if (!blurredImage.exists()) {

			// launch the progressbar in ActionBar
			setProgressBarIndeterminateVisibility(true);

			new Thread(new Runnable() {

				@Override
				public void run() {
					// No image found => let's generate it!
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 4;

					Bitmap image = null;
					switch (month) {
						case 0:
							mNormalImage.setImageResource(R.drawable.background);
							image = BitmapFactory.decodeResource(getResources(),
									R.drawable.background, options);
							break;
						case 1:
							mNormalImage.setImageResource(R.drawable.background);
							image = BitmapFactory.decodeResource(getResources(),
									R.drawable.background, options);
							break;
						case 2:
							mNormalImage.setImageResource(R.drawable.background);
							image = BitmapFactory.decodeResource(getResources(),
									R.drawable.background, options);
							break;
						case 3:
							mNormalImage.setImageResource(R.drawable.background);
							image = BitmapFactory.decodeResource(getResources(),
									R.drawable.background, options);
							break;
						default:
							break;
					}

					Bitmap newImg = Blur.fastblur(MainActivity.this, image, 20);
					ImageUtils.storeImage(newImg, blurredImage);

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							BitmapFactory.Options options = new BitmapFactory.Options();
							options.inSampleSize = 4;

							Bitmap image = null;
							switch (month) {
								case 0:
									mNormalImage
											.setImageResource(R.drawable.background);
									image = BitmapFactory.decodeResource(
											getResources(), R.drawable.background,
											options);
									break;
								case 1:
									mNormalImage
											.setImageResource(R.drawable.background);
									image = BitmapFactory.decodeResource(
											getResources(), R.drawable.background,
											options);
									break;
								case 2:
									mNormalImage
											.setImageResource(R.drawable.background);
									image = BitmapFactory.decodeResource(
											getResources(), R.drawable.background,
											options);
									break;
								case 3:
									mNormalImage
											.setImageResource(R.drawable.background);
									image = BitmapFactory.decodeResource(
											getResources(), R.drawable.background,
											options);
									break;
								default:
									break;
							}

							Bitmap newImg = Blur.fastblur(MainActivity.this,
									image, 20);
							ImageUtils.storeImage(newImg, blurredImage);

							updateView(screenWidth);

							// And finally stop the progressbar
							setProgressBarIndeterminateVisibility(false);
						}
					});

				}
			}).start();

		} else {

			// The image has been found. Let's update the view
			updateView(screenWidth);

		}

		lv_feedback_main
				.setOnScrollListener(new AbsListView.OnScrollListener() {

					@Override
					public void onScrollStateChanged(AbsListView view,
													 int scrollState) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onScroll(AbsListView view,
										 int firstVisibleItem, int visibleItemCount,
										 int totalItemCount) {
						// TODO Auto-generated method stub
				/*
				 * if(firstVisibleItem==ScrollLearningStatus){
				 * lrnStausOrTmLineParams.put("time",
				 * String.valueOf(System.currentTimeMillis()));
				 * FlurryAgent
				 * .logEvent("학습 현황 화면,MainActivity_tv_feedback_bleft,1"
				 * , lrnStausOrTmLineParams, true); }else{ //TimeLine
				 * lrnStausOrTmLineParams.put("time",
				 * String.valueOf(System.currentTimeMillis()));
				 * FlurryAgent
				 * .logEvent("타임 라인 화면,MainActivity_tv_feedback_bleft,1"
				 * , lrnStausOrTmLineParams, true);
				 *
				 * }
				 */
						// Log.e("check_feed", ""+firstVisibleItem+
						// "                "+visibleItemCount);
						alpha = (float) -header.getTop() / (float) TOP_HEIGHT;
						// Apply a ceil
						if (alpha > 1) {
							alpha = 1;
						}
						// Apply on the ImageView if needed

						// alpha값 바뀌는데 걸리는 시간 체크
						Log.e("alpha_val", "1");
						mBlurredImage.setAlpha(alpha);
						Log.d("alpha_val", "1");

						// Parallax effect : we apply half the scroll amount to
						// our
						// three views
						mBlurredImage.setTop(header.getTop() / 2);
						mNormalImage.setTop(header.getTop() / 2);
					}
				});

	}

	protected void setProfile() {
		// TODO Auto-generated method stub

		String title = "";
		;

		readProfile();


		memorize_word.setText(Integer.toString(db.getKnownCount()));

		Integer fcount[] = db.getForgetCount();
		review_word.setText(Integer.toString(db.getMforget()));

		int not_zero_state = mPreference.getInt(MainValue.GpreCheckCurve, 0);

		// int forgetting_curve_percentage = (not_zero_state * 100 / 500) + 1;

		int review_word_count = db.getReviewCountByCalendar();
		int forgetting_curve_percentage = (review_word_count * 100 / 500) + 1;

		// forgetting_curve_percentage_bar
		// .setProgress(forgetting_curve_percentage);

		if (forgetting_curve_percentage > 100) {
			forgetting_curve_percentage_count.setText("100");
		} else {
			forgetting_curve_percentage_count.setText(Integer
					.toString(forgetting_curve_percentage - 1));
		}

		String name = getMySharedPreferences(MainValue.GpreName);
		if (name.equals("")) {
			name = "알수없음";
		}

		MildangDate today = new MildangDate();
		String year = today.get_year();
		String month = today.get_Month();
		String day = today.get_day();

		tvToday.setText(year + "." + month + "." + day);

		String topic = getMySharedPreferences(MainValue.GpreTopic);
		if (topic.length() == 0) {
			topic = "1";
		}

		int currentLevel = db.getWordLevel();
		String currentLevelStr = "";
		String currentLevelCount = "";

		level_counting = db.getLevelCounting();

		int[][] all_grade_count = db.getCountOfWordsAsGrade();
		int[][] my_grade_count = db.getCountOfUserStudiedWords();

		int current_count = 0;
		for (int i = 0; i < my_grade_count.length; i++) {
			if (currentLevel == my_grade_count[i][0]) {
				current_count = my_grade_count[i][1];
			}
		}

		currentLevelCount = Integer.toString(current_count) + "/"
				+ Integer.toString(all_grade_count[currentLevel - 1][1]);

		hit_progress_gauge_param = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		hit_progress_backgroud_param = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		hit_progress_gauge_param.weight = current_count;
		hit_progress_backgroud_param.weight = all_grade_count[currentLevel - 1][1]
				- current_count;

		hit_progress_gauge.setLayoutParams(hit_progress_gauge_param);
		hit_progress_background.setLayoutParams(hit_progress_backgroud_param);
		Log.d("bbbb", Float.toString(hit_progress_gauge_param.weight));
		Log.d("bbbb", Float.toString(hit_progress_backgroud_param.weight));

		switch (currentLevel) {
			case 1:
				currentLevelStr = "적중 기초 단어장";
				break;
			case 2:
				currentLevelStr = "적중 필수1 단어장";
				break;
			case 3:
				currentLevelStr = "적중 필수2 단어장";
				break;
			case 4:
				currentLevelStr = "적중 마스터 단어장";
				break;
			case 5:
				currentLevelStr = "적중 심화 단어장";
				break;
		}
		tvCurrentLevel.setText(currentLevelStr);
		tvCurrentMemorize.setText(currentLevelCount);

		Boolean stepFlag = mPreference.getBoolean(MainValue.GpreFirstStart,
				false);

		// will coach ment
		if (stepFlag == true && db.getGoalAchievement(date.get_today()) > 80) {
			tvWillMessage.setText(mPreference.getString(MainValue.GpreName,
					"이름없음") + "님의 의지가\n나날이 강해지고 있어요");
		} else if (stepFlag == true
				&& db.getGoalAchievement(date.get_today()) < 80) {
			tvWillMessage.setText(mPreference.getString(MainValue.GpreName,
					"이름없음") + "님의 의지가\n흔들리고 있어요.\n오늘부터 다시 불태워 볼까요~!?");
		} else {
			tvWillMessage.setText(mPreference.getString(MainValue.GpreName,
					"이름없음") + "님의 의지를\n확인해보세요.");
		}

		// memory coach ment
		/*
		 * 2015.01.28 수정 - 양대현 db.getNoZeroState()로 비교하는게 아니라 복습한 단어 갯수로 비교해야
		 * 하므로 db.getNoZeroState()를 review_word_count로 변경
		 */
		if (review_word_count != 0 && review_word_count < 500) {

			tvMemoryMessage.setText("망각곡선을 완성시키기\n위해서는 "
					+ Integer.toString((500 - review_word_count))
					+ "개의 단어를\n더 외워야합니다.");
		} else if (review_word_count != 0 && review_word_count >= 500) {
			tvMemoryMessage.setText("망각곡선 측정이 완료되었습니다.\n추출된 복습단어를\n꼭 확인해주세요.");
		} else {
			tvMemoryMessage.setText("망각곡선의 완성도가\n높을수록 정확한 복습단어가\n추출됩니다.");
		}

		// hit coach ment
		if (stepFlag == false) {
			tvHitMessage
					.setText("2016학년도 수능은,\n평년 대비 지문의 길이가 길고,\n단어의 난이도도 높았습니다.");
		} else {
			tvHitMessage.setText("2016학년도 수능 연계\nebs교재 단어가 업데이트 되었습니다.");
		}

		LinearLayout.LayoutParams graphParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		graphParams.weight = forgetting_curve_percentage;
		progressGauge.setLayoutParams(graphParams);

		LinearLayout.LayoutParams tempParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		tempParams.weight = 100 - forgetting_curve_percentage;
		progressBackground.setLayoutParams(tempParams);

		SpannableStringBuilder spb = new SpannableStringBuilder();
		String s = "관리 시스템 ";
		SpannableString s1 = new SpannableString(s);
		s1.setSpan(new ForegroundColorSpan(Color.rgb(25, 183, 110)), 0,
				s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		SpannableString s2 = new SpannableString(mPreference.getString(
				MainValue.GpreSystemName, "Riche"));

		s2.setSpan(new ForegroundColorSpan(Color.rgb(25, 183, 110)), 0,
				mPreference.getString(MainValue.GpreSystemName, "Riche")
						.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		s2.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
				mPreference.getString(MainValue.GpreSystemName, "Riche")
						.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		SpannableString s3 = new SpannableString("asdf\n");
		s3.setSpan(new LineSpacing(20), 0, s3.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		spb.append(s1);
		spb.append(s2);
		spb.append("이 알려드립니다.\n");

		tvSystemName.setText(spb);

		tvGoalAchievement
				.setText("" + db.getGoalAchievement(today.get_today()));

		tvDailyUseTime.setText(Integer.toString(db.getWeekAverageTime(today
				.get_today())));

		tvUserName.setText(mPreference.getString(MainValue.GpreName, "이름없음")
				+ "님의\n밀당 영단어 분석 결과를 ");

	}

	Bitmap bitmap;

	public void readProfile() {

		thumbnailURL = getMySharedPreferences("image");
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				String path = Config.DB_FILE_DIR;

				File directory = new File(path);

				File file = new File(directory, Config.PROFILE_NAME); // or any
				// other
				// format
				// supported

				FileInputStream streamIn;

				try {
					streamIn = new FileInputStream(file);
					bitmap = BitmapFactory.decodeStream(streamIn); // This gets
					// the image
					streamIn.close();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (bitmap != null) {
					bitmap = round(bitmap);
				}

				// url = new URL(thumbnailURL);
				// URLConnection conn = url.openConnection();
				// conn.connect();// url연결
				// bis = new BufferedInputStream(
				// conn.getInputStream()); // 접속한 url로부터 데이터값을 얻어온다
				// bm = BitmapFactory.decodeStream(bis);// 얻어온 데이터 Bitmap 에저장
				// bm = roundBitmap(bm);
				// bis.close();// 사용을 다한 BufferedInputStream 종료

				handler.post(new Runnable() {
					@Override
					public void run() {
						try {
							if (!bitmap.equals(null)) {
								iv_picture.setImageBitmap(bitmap);
							}
						} catch (NullPointerException e) {
							// TODO: handle exception
							iv_picture
									.setImageResource(R.drawable.my_btn_photo_default);
							Log.e("profile", e.toString());
						}
					}
				});
			}
		});
		thread.start();
		// load image
	}

	private Bitmap round(Bitmap source) {
		// TODO Auto-generated method stub
		Bitmap output = Bitmap.createBitmap(source.getWidth(),
				source.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawCircle(source.getWidth() / 2, source.getHeight() / 2,
				source.getWidth() / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(source, rect, rect, paint);
		return output;
	}

	private void updateView(final int screenWidth) {
		try {
			Bitmap bmpBlurred = BitmapFactory.decodeFile(getFilesDir()
					+ "blurred_image.png");
			bmpBlurred = Bitmap
					.createScaledBitmap(
							bmpBlurred,
							screenWidth,
							(int) (bmpBlurred.getHeight()
									* ((float) screenWidth) / (float) bmpBlurred
									.getWidth()), false);

			mBlurredImage.setImageBitmap(bmpBlurred);
			final int month = (date.getMonth()) % 4;
			switch (month) {
				case 0:
					mNormalImage.setImageResource(R.drawable.background);
					break;
				case 1:
					mNormalImage.setImageResource(R.drawable.background);
					break;
				case 2:
					mNormalImage.setImageResource(R.drawable.background);
					break;
				case 3:
					mNormalImage.setImageResource(R.drawable.background);
					break;
				default:
					break;
			}
		} catch (NullPointerException e) {
			// TODO: handle exception

			setProgressBarIndeterminateVisibility(true);

			final File blurredImage = new File(getFilesDir()
					+ "blurred_image.png");

			new Thread(new Runnable() {

				@Override
				public void run() {
					// No image found => let's generate it!
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 4;

					Bitmap image = null;
					final int month = (date.getMonth()) % 4;
					switch (month) {
						case 0:
							mNormalImage.setImageResource(R.drawable.background);
							image = BitmapFactory.decodeResource(getResources(),
									R.drawable.background, options);
							break;
						case 1:
							mNormalImage.setImageResource(R.drawable.background);
							image = BitmapFactory.decodeResource(getResources(),
									R.drawable.background, options);
							break;
						case 2:
							mNormalImage.setImageResource(R.drawable.background);
							image = BitmapFactory.decodeResource(getResources(),
									R.drawable.background, options);
							break;
						case 3:
							mNormalImage.setImageResource(R.drawable.background);
							image = BitmapFactory.decodeResource(getResources(),
									R.drawable.background, options);
							break;
						default:
							break;
					}

					final Bitmap newImg = Blur.fastblur(MainActivity.this,
							image, 20);
					ImageUtils.storeImage(newImg, blurredImage);

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							updateView(screenWidth);

							// And finally stop the progressbar
							setProgressBarIndeterminateVisibility(false);

						}
					});

				}
			}).start();
		}

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(MainActivity.this, ExpainFeedPopup.class);

		switch (v.getId()) {

			case R.id.lv_feedback_main1:

				if (isExam)
					mCurrentFragmentIndex = FRAGMENT_EXAM_SCORE_LIST;
				else
					mCurrentFragmentIndex = FRAGMENT_WORD_SCORE_LIST;

				mMainWordIndex = mCurrentFragmentIndex;
				fragmentReplace(mCurrentFragmentIndex);

				break;

			case R.id.iv_feedback_invite:
				try {
					sendUrlLink(v);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				overridePendingTransition(R.anim.slide_in_left, 0);
				break;

			case R.id.iv_feed_study:
		/*	Intent intent_study = new Intent(MainActivity.this,
					com.ihateflyingbugs.hsmd.SideActivity.class);
			intent_study.putExtra("Fragment", SideActivity.FRAGMENT_Study);
			intent_study.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent_study, 123);*/
				overridePendingTransition(R.anim.slide_in_left, 0);
				lv_feedback_main.startAnimation(new AnimationUtils().loadAnimation(
						getApplicationContext(), R.anim.slide_out_left));
				lv_feedback_main.setVisibility(View.GONE);
				break;
			case R.id.iv_feed_notice:
			/*Intent intent_notice = new Intent(MainActivity.this,
					com.ihateflyingbugs.hsmd.SideActivity.class);
			intent_notice.putExtra("Fragment", SideActivity.FRAGMENT_BOARD);
			intent_notice.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent_notice, 123);*/
				overridePendingTransition(R.anim.slide_in_left, 0);
				lv_feedback_main.startAnimation(new AnimationUtils().loadAnimation(
						getApplicationContext(), R.anim.slide_out_left));
				lv_feedback_main.setVisibility(View.GONE);
				break;
			case R.id.iv_feed_level:

				break;

			case R.id.percentView1:
				levelParams.put("time", String.valueOf(System.currentTimeMillis()));
				FlurryAgent.logEvent("BaseActivity:ClickLevel1");
				setCurrentLevel(1);

				break;

			case R.id.ll_action_review:
				if (notice_layout.getVisibility() == View.VISIBLE) {
					notice_layout.setVisibility(View.GONE);
					notice_layout.startAnimation(new AnimationUtils()
							.loadAnimation(getApplicationContext(),
									android.R.anim.fade_out));
				} else {
					notice_layout.setVisibility(View.VISIBLE);
					notice_layout.startAnimation(new AnimationUtils()
							.loadAnimation(getApplicationContext(),
									android.R.anim.fade_in));
				}
				break;

			case R.id.view_topnavi:
				menu.toggle();

				break;
			// //layout lock
			// case R.id.rl_lock_will:
			// case R.id.rl_lock_hit:
			// case R.id.rl_lock_memory:
			// int flag_will = mPreference.getInt(MainValue.GpreTutorial_Will, 0);
			// int flag_hit = mPreference.getInt(MainValue.GpreTutorial_Hit, 0);
			// int flag_memory = mPreference.getInt(MainValue.GpreTutorial_Memory,
			// 0);
			//
			// if (flag_will == 0) {
			// mPreference.edit().putInt(MainValue.GpreTutorial_Will, 1)
			// .commit();
			// iv_lock_will.setImageResource(R.drawable.actionbar_icon_test);
			// } else {
			// rl_lock_will.setVisibility(View.GONE);
			// if (flag_hit == 0) {
			// mPreference.edit().putInt(MainValue.GpreTutorial_Hit, 1)
			// .commit();
			// iv_lock_hit
			// .setImageResource(R.drawable.actionbar_icon_test);
			// } else {
			// rl_lock_hit.setVisibility(View.GONE);
			// if (flag_memory == 0) {
			// mPreference.edit()
			// .putInt(MainValue.GpreTutorial_Memory, 1)
			// .commit();
			// iv_lock_memory
			// .setImageResource(R.drawable.actionbar_icon_test);
			// } else {
			// rl_lock_memory.setVisibility(View.GONE);
			// mPreference.edit()
			// .putInt(MainValue.GpreTutorial_Will, 0)
			// .commit();
			// mPreference.edit()
			// .putInt(MainValue.GpreTutorial_Hit, 0).commit();
			// mPreference.edit()
			// .putInt(MainValue.GpreTutorial_Memory, 0)
			// .commit();
			// }
			// }
			// }
			//
			// break;

			// case R.id.rl_lock_will:
			// case R.id.rl_lock_hit:
			// case R.id.rl_lock_memory:
			// int flag_will =mPreference.getInt(MainValue.GpreTutorial_Will, 0);
			// int flag_hit =mPreference.getInt(MainValue.GpreTutorial_Hit, 0);
			// int flag_memory =mPreference.getInt(MainValue.GpreTutorial_Memory,
			// 0);
			//
			//
			// if(flag_will==0){
			// mPreference.edit().putInt(MainValue.GpreTutorial_Will, 1).commit();
			// iv_lock_will.setImageResource(R.drawable.actionbar_icon_test);
			// }else{
			// rl_lock_will.setVisibility(View.GONE);
			// if(flag_hit == 0){
			// mPreference.edit().putInt(MainValue.GpreTutorial_Hit, 1).commit();
			// iv_lock_hit.setImageResource(R.drawable.actionbar_icon_test);
			// }else{
			// rl_lock_hit.setVisibility(View.GONE);
			// if(flag_memory == 0){
			// mPreference.edit().putInt(MainValue.GpreTutorial_Memory, 1).commit();
			// iv_lock_memory.setImageResource(R.drawable.actionbar_icon_test);
			// }else{
			// rl_lock_memory.setVisibility(View.GONE);
			// mPreference.edit().putInt(MainValue.GpreTutorial_Will, 0).commit();
			// mPreference.edit().putInt(MainValue.GpreTutorial_Hit, 0).commit();
			// mPreference.edit().putInt(MainValue.GpreTutorial_Memory, 0).commit();
			// }
			// }
			// }
			//
			// break;

		}

	}

	public void setCurrentLevel(final int choiced_level) {
		menu.toggle();

		if (choiced_level <= Config.MAX_DIFFICULTY) {
			new AlertDialog.Builder(MainActivity.this)
					.setMessage("해당 레벨로 이동하시겠습니까?")
					.setPositiveButton("확인",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
													int which) {
									// TODO Auto-generated method stub
									Config.Difficulty = choiced_level;
									// setMySharedPreferences(
									// MainActivitys.GpreLevel, ""
									// + choiced_level);

									db.deleteAllCurrentWord();
									SlidefragmentReplace(FRAGMENT_WORD_SCORE_LIST);

								}
							})
					.setNegativeButton("취소",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
													int which) {
									// TODO Auto-generated method stub
									menu.toggle();

								}
							}).show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			Log.d("YDH", data.toString());
		}
		if (data != null) {
			if (resultCode == 1592 && data.getBooleanExtra("isChanged", false)) {
				isLevelChanged = true;
			}
		} else {
			isLevelChanged = false;
		}

		if (resultCode == 55555) {
			pause(cTimer);
			finish();
		} else if (requestCode == Activity.RESULT_CANCELED) {
			isBackPressed = false;
		}

		setProfile();
		// lv_feedback_main.setVisibility(View.VISIBLE);
		// set_graph_color(check_graph, check_text);

	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {

		return false;
	}

	public void setFeedPage() {

	}

	@Override
	public void onBackPressed() {
		if (menu.isMenuShowing()) {
			menu.toggle();

			return;
		}


		if (!isBackPressed) {
			isBackPressed = true;

			Toast.makeText(MainActivity.this, "뒤로가기 버튼을 한번 더 눌러 종료 하여 주십시오.",
					Toast.LENGTH_SHORT).show();

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					isBackPressed = false;
				}
			}, 2000);
		} else {

			isFinish = true;
			// moveTaskToBack(true);
			// handler.postDelayed(new Runnable() {
			//
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// if(isFinish){
			finish();
			// }
			// }
			// }, 15000);
		}
	}

	public void sendUrlLink(View v) throws NameNotFoundException {
		// Recommended: Use application mContext for parameter.

		// int checkedRadioButtonId = rg.getCheckedRadioButtonId();

		// check, intent is available.

		KakaoLink kakaoLink = null;
		try {
			kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
		} catch (KakaoParameterException e1) {
			// TODO Auto-generated catch block
			Log.e("rrrr", "" + e1);
		}

		final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink
				.createKakaoTalkLinkMessageBuilder();


		String mssg = "전국 53개 고등학교 수험생이 선택한 밀당영단어\n\n수능 '적중 93%단어'를 외우면 당신의 '기억력'이 측정됩니다";
		String linkContents = null;
		try {
			kakaoTalkLinkMessageBuilder.addText(mssg)
					.addWebLink("밀당 영단어 다운", "http://bit.ly/1iLcFUg").build();

		} catch (KakaoParameterException e) {
			// TODO Auto-generated catch block
			Log.e("rrrr", "" + e);
			e.printStackTrace();
		}

		try {
			kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, this);
		} catch (KakaoParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FlurryAgent.logEvent("BaseActivity:InviteFriends");

		// kakaoLink
		// .openKakaoLink(
		// this,
		// "http://bit.ly/MDAppDwns",
		// mssg,
		// getPackageName(),
		// getPackageManager().getPackageInfo(getPackageName(), 0).versionName,
		// "밀당 영단어", "UTF-8");
	}

	private void alert(String message) {
		new AlertDialog.Builder(this).setIcon(R.drawable.icon48)
				.setTitle(R.string.app_name).setMessage(message)
				.setPositiveButton(android.R.string.ok, null).create().show();
	}

	public static String getMySharedPreferences(String _key) {
		if (mPreference == null) {
			mPreference = context.getSharedPreferences(Config.PREFS_NAME,
					MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE
							| MODE_MULTI_PROCESS);
		}
		return mPreference.getString(_key, "");
	}

	public static void setMySharedPreferences(String _key, String _value) {
		if (mPreference == null) {
			mPreference = context.getSharedPreferences(Config.PREFS_NAME,
					MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE
							| MODE_MULTI_PROCESS);
		}
		SharedPreferences.Editor editor = mPreference.edit();
		editor.putString(_key, _value);
		editor.commit();
	}

	private static int index = 0;
	private final static int interval = 50;
	private static boolean isRunning = true;

	public static void set_tvLevel() {
		tvLevel.setText("Level : " + Config.Difficulty);
	}

	public void SlidefragmentReplace(int reqNewFragmentIndex) {

		Fragment fragment = getslideFragment(reqNewFragmentIndex);
		Log.e("fragments", " " + reqNewFragmentIndex);
		// replace fragment
		final FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.linearFragment, fragment).commit();
	}

	private Fragment getslideFragment(int idx) {
		Fragment newFragment = null;

		switch (idx) {
			case FRAGMENT_WORD_SCORE_LIST:
				newFragment = new WordListFragment();
				break;
			case FRAGMENT_WORD_KNOWN_LIST:
				newFragment = new WordListFragment();
				break;
			case FRAGMENT_EXAM_KNOWN_LIST:
				newFragment = new WordListFragment();
				break;
			case FRAGMENT_EXAM_SCORE_LIST:
				newFragment = new WordListFragment();
				break;

			default:
				break;
		}
		return newFragment;
	}

	public static void finish_logout() {
		thisActivity.finish();
	}

	String starttime;
	String startdate;

	Map[] maps = new Map[41];

	Map<String, String> articleParams;

	protected void onStart() {
		super.onStart();

		Log.d("onStart", "onStart");


		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md;
				md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String something = new String(Base64.encode(md.digest(), 0));

				Log.d("Hash key", something);
			}
		} catch (Exception e) {

			// TODO Auto-generated catch block
			Log.e("name not found", e.toString());

		}

		articleParams = new HashMap<String, String>();
		MainActivitys.GpreFlag = true;
		MainActivitys.GpreAccessDuration = 0;
		// writeLog("[MainActivity 시작,MainActivity,1]\r\n");
		FlurryAgent.setUserId(db.getStudentId());
		startdate = Flurry_date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		// 카카오톡에 저 부분만 com.kakao.talk.activity.main.MainTabFragmentActivity만 로그를
		// 보내고
		// 나머지는 안보낸다

		Log.e("activitygg", "Base onstart");
		FlurryAgent.logEvent("BaseActivity", articleParams);
	}

	// 카카오 막기

	String Current_Activity = "asdf";
	boolean isInvite =false;

	protected void onStop() {
		super.onStop();

		Log.d("check_activity", "onStop");
		pause(cTimer);
		Log.d("Timer", "onStop()");

		FlurryAgent.endTimedEvent("BaseActivity");
		setMySharedPreferences(MainActivitys.GpreEndTime, "" + System.currentTimeMillis());
		setMySharedPreferences(MainActivitys.GpreTotalReviewCnt, "" + db.getMforget());

		MainActivitys.GpreFlag = true;

		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			// Do whatever
			articleParams.put("WIFI", "On");
		} else {
			articleParams.put("WIFI", "Off");
		}

		articleParams.put("Start", startdate);
		articleParams.put("End", Flurry_date.get_currentTime());
		articleParams.put("Duration", "" + ((Long.valueOf(System.currentTimeMillis()) - Long.valueOf(starttime))) / 1000);
		articleParams.put("know_action_cnt", "" + Config.know_count);
		articleParams.put("unknow_action_cnt", "" + Config.unknow_count);

		articleParams.put("total_study_cnt", "" + db.getKnownCount());
		articleParams.put("today_review_cnt", "" + db.getReviewWord());
		articleParams.put("today_new_cnt", "" + db.getStudyWord());

		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;

		Log.e("otherapp", "" + componentInfo.getPackageName());
		Log.d("otherapp", "" + componentInfo.getClassName());

		/*
		 * com.android.systemui//홈버튼 길게 com.buzzpia.aqua.launcher
		 * com.pantech.launcher2.Launcher
		 * com.kakao.talk.activity.chat.ChatRoomActivity 초대시에는
		 * com.kakao.talk.activity.TaskRootActivity
		 */

		if (!componentInfo.getPackageName().equals("com.ihateflyingbugs.hsmd") && !isFinish) {
			Map<String, String> Escape = new HashMap<String, String>();
			Escape.put("package", "" + componentInfo.getPackageName());
			FlurryAgent.logEvent("BaseActivity:EscapeApplication", Escape);
			Log.e("bookmark", "" + Escape.get("package") + "           " + componentInfo.getClassName());

			// 카카오 차단기능관련 플레그 넣으면됨
			if (componentInfo.getPackageName().equals("com.kakao.talk")) {

				if (Current_Activity.equals("com.kakao.talk.activity.friend.picker.ConnectBroadcastFriendsPickerActivity")) {
					if (componentInfo.getClassName().equals("com.kakao.talk.activity.chat.ChatRoomActivity")) {
						isInvite = true;
						Log.i("otherapp", " 초대 보냈음");
						Log.e("async_list", "Async_send_click_request : ready");
						new RetrofitService().getPaymentService().retroUpdateClickPayStep(Config.REQUEST_ID);
					} else {
						Log.i("otherapp", "애매함");

					}
				}
				Current_Activity = componentInfo.getClassName().toString();

				if (mPreference.getBoolean(MainValue.GpreKakaoPush, false)) {
					isKakao = false;
					Log.e("bookmark", "카카오 차단기능 작동");
					finish();
					Intent intent = new Intent(this, MainActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("kakao_start", true);
					//startActivity(intent);
				} else {
					Log.e("bookmark", "카카오 차단기능 플레그 false임");
				}
			} else {

			}
		} else if (!componentInfo.getPackageName().equals("com.ihateflyingbugs.hsmd") && isFinish) {
			FlurryAgent.logEvent("BaseActivity:FinishByBackpress");
		}

		Log.e("activitygg", "Base onStop");

		setMySharedPreferences(MainActivitys.GpreFinishTime, String.valueOf(System.currentTimeMillis()));

		Config.know_count = 0;
		Config.unknow_count = 0;

		FlurryAgent.onEndSession(this);

	}



	@Override
	protected void onRestart() {
		super.onRestart();

		if (restart_flag) {
			cTimer.start();
		}
	}

	boolean isCheckPercentage = false;
	int current_level = 1;
	public CountDownTimer timer(final PercentView percentView,
								final String position) {

		// time초 동안 time/100초 마다 onTick 실행
		CountDownTimer cTimer = new CountDownTimer(86400000, 1000) {

			// (time * 1000) / 100마다 실행
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub

				timer_sec = db.getGoalTime();

				// db로부터 저장된 시간을 가져온다.
				int[] saved_time = db.getTime();
				use_min = saved_time[0];
				use_sec = saved_time[1];

				// 사용시간이 갑자기 줄어드는 경우에 따른 예외 처리
				if (use_min < temp_min && use_min != 0) {
					use_min = temp_min;
					Toast.makeText(getApplicationContext(), "Bug",
							Toast.LENGTH_SHORT).show();
				} else {
					temp_min = use_min;
				}

				MildangDate date = new MildangDate();

				String current_time = date.get_hour() + date.get_minute()
						+ date.get_second();

				// 저장된 날짜
				String saved_date = db.getDate();

				// 단어 암기 중 하루가 지날 때
				if (current_time.equals("235959")) {
					// 에러 발생시 하루에 한번 csv파일을 업로드 하도록하는 플래그 초기화
					// csv파일을 업로드한다.

					int useTime[] = db.getTime();

					// 학습량을 feeds테이블에 저장한다.
					db.putDay_Of_Study();

					// 공부 시간 저장
					db.putCalendarData(saved_date);

					// 현재 날짜 저장
					db.insertDate(new Date().get_today());

					// 사용 시간 초기화
					use_min = 0;
					use_sec = 0;
					db.removeStudyTime();

					temp_min = 0;
				}

				// 60초 경과 시마다 1분 증가
				if (use_sec == 60) {
					use_min += 1;
					use_sec = 0;
				}

				// 10이하일 때 두자리 유지
				if (use_sec < 10) {
					use_sec_st = "0" + Integer.toString((int) use_sec++);
				} else {
					use_sec_st = Integer.toString((int) use_sec++);
				}

				if (use_min < 10) {
					use_min_st = "0" + Integer.toString((int) use_min);
				} else {
					use_min_st = Integer.toString((int) use_min);
				}

				tv_using_time_min.setText(use_min_st);
				tv_using_time_sec.setText(use_sec_st);

				// millisecond 단위 총 사용 시간
				float use_time = (use_min * 60000) + use_sec * 1000;

				// 목표시간 달성률 계산
				try {
					percentage_lv = ((int) use_time) / (timer_sec * 60000);

					percentage = ((use_time - ((timer_sec * 60000) * percentage_lv))
							/ ((timer_sec) * 60000) * 100);

					Log.e("gggg", "timer_sec : " + timer_sec + "  "
							+ percentage + "  " + percentage_lv + " "
							+ use_time);

				} catch (ArithmeticException e) {
					// TODO: handle exception

					percentage_lv = ((int) use_time) / (10 * 60000);

					percentage = ((use_time - ((10 * 60000) * percentage_lv))
							/ ((10) * 60000) * 100);

					Log.e("gggg", "ArithmeticException : " + timer_sec + "  "
							+ percentage + "  " + percentage_lv);
				}
				if (percentage >= 100 && percentage_lv < 6) {
					percentage_lv++;
				}

				Log.d("Async_make_willcoupon", ""+percentage_lv + "    "+ current_level);

				if (current_level == percentage_lv) {
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							final String id = db.getStudentId();

							Log.e("async_list", "Async_make_willcoupon : ready");
							new RetrofitService().getPaymentService().retroInsertCoupon(id, "3")
									.enqueue(new Callback<CouponData>() {
										@Override
										public void onResponse(Response<CouponData> response, Retrofit retrofit) {
											Log.v("async_list", "Async_make_willcoupon : Resonponse");
											current_level++;
											if (response.body().getIs_exist_coupon() != 1) {
												Log.e("Async_make_willcoupon", "make coupon");
												startActivity(new Intent(MainActivity.this, CompleteStudyTimePopup.class));
											} else {
												Log.e("Async_make_willcoupon", "already exist");
											}
										}

										@Override
										public void onFailure(Throwable t) {
											handler.post(new Runnable() {
												@Override
												public void run() {
													Toast.makeText(MainActivity.this, "쿠폰발급에 실패하였습니다.", Toast.LENGTH_SHORT).show();
												}
											});
										}
									});

						}
					});
				}


				// 목표시간 타이머 그래프
				percentView.setPercentage(percentage, percentage_lv, true);
				editor = mPreference.edit();
				editor.putFloat(MainValue.GpreUseMin, use_min);
				if (!editor.commit()) {
					Toast.makeText(getApplicationContext(),
							"Commit Bug : GpreUseMin", Toast.LENGTH_SHORT)
							.show();
				}
				editor.putFloat(MainValue.GpreUseSec, use_sec);
				if (!editor.commit()) {
					Toast.makeText(getApplicationContext(),
							"Commit Bug : GpreUseSec", Toast.LENGTH_SHORT)
							.show();
				}
				// 사용시간 db에 저장
				db.insertTime(use_min, use_sec);

				today_tv.setText(new MildangDate().get_Month() + "."
						+ new MildangDate().get_day());

			}

			// 종료시 실행
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
			}
		};

		return cTimer;
	}

	public void pause(CountDownTimer cTimer) {

		cTimer.cancel();
	}

	private static final int TRAY_HIDDEN_FRACTION = 7;
	private static final int TRAY_MOVEMENT_REGION_FRACTION = 7;
	private Handler timerAnimationHandler = new Handler();
	private Timer timerAnimationTimer;
	int width;

	private Object mGoogleApiClient;

	public class AnimationTimerTask extends TimerTask {

		int mDestX;
		int mDestY;

		public AnimationTimerTask(int x) {
			super();

			mDestX = width - 64;

			int screenHeight = getResources().getDisplayMetrics().heightPixels;

			mDestY = Math.max(screenHeight / TRAY_MOVEMENT_REGION_FRACTION,
					mRootLayoutParams.height);

			mDestY = Math.min(
					((TRAY_MOVEMENT_REGION_FRACTION - 1) * screenHeight)
							/ TRAY_MOVEMENT_REGION_FRACTION
							- timerLayout.getWidth(), mDestY);
		}

		@Override
		public void run() {
			timerAnimationHandler.post(new Runnable() {
				@Override
				public void run() {
					if (width / 2 > 1) {
						mRootLayoutParams.width = (2 * (mRootLayoutParams.width - mDestX))
								/ 3 + mDestX;

						fullLayout.updateViewLayout(timerLayout,
								mRootLayoutParams);
						Log.d("run", "run : " + mRootLayoutParams.width);
						// Cancel animation when the destination is reached

						if (Math.abs(mRootLayoutParams.width - mDestX) < 2) {

							Log.d("run", "stop : " + mRootLayoutParams.width);
							AnimationTimerTask.this.cancel();
							timerAnimationTimer.cancel();

							// mRootParams.width값 갱신 후 updateView();
							mRootLayoutParams.width = display.getWidth() - 31
									* (int) density;
							fullLayout.updateViewLayout(timerLayout,
									mRootLayoutParams);
						}
					}
				}
			});
		}
	}

	public static boolean isShowCard= false;


	public static void startPaymentCard(int use_state, int availability, boolean isInvitedFriend){
		if(!is1FreeDay){

			Intent paymentIntent;
			Iterator<Coupon> iterator = items.iterator();
			int primeNum = 1;

			Log.d("student_use_state", "startPaymentCard    "+use_state);
			if(use_state >= 2){
				while (iterator.hasNext()) {
					Coupon coupon = iterator.next();
					if (coupon.isDuplicatable()) {
						primeNum *= coupon.getPrimeNum();
						Log.d("PRIMENUM", primeNum + ", " + iterator.hasNext() + coupon.getCouponName());
					}
				}
				if(isInvitedFriend){
					paymentIntent = new Intent(thisActivity, ShowCard.class);
				}else{
					paymentIntent = new Intent(thisActivity, RequestFreeCouponPopup.class);
				}
				paymentIntent.putExtra("prime_num", primeNum);


			} else {
				paymentIntent = new Intent(thisActivity, RequestPaymentActivity.class);
			}
			paymentIntent.putExtra("use_state", use_state);
			paymentIntent.putExtra("availability", availability);
			paymentIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			Log.d("kjw", "onActivityResult");
			if(!isShowCard){
				thisActivity.startActivityForResult(paymentIntent, 55555);
			}
		}
	}

	private boolean pointInView(float localX, float localY, float slop) {
		Log.e("hahaha",
				""
						+ String.valueOf(localX >= -slop && localY >= -slop
						&& localX < slop && localY < slop));
		return localX >= -slop && localY >= -slop && localX < slop
				&& localY < slop;
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_UP:
				audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
				return true;
			case KeyEvent.KEYCODE_BACK:
				super.onKeyDown(keyCode, event);
				return true;
			default:
				return false;
		}
	}

	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				Log.d("SERVISESEES", service.service.getClassName() + ", running");
				return true;
			}
		}
		return false;
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

		sendWorkEachCount(db);
		super.finish();


	}


	public static void sendWorkEachCount(DBPool dbpool){
		String word_count = "";
		for(int i =0 ; i<5; i++){
			word_count += dbpool.get_each_word_count(i+1)+",";
		}
		Log.e(TAG, "retroInsertWorkbookEachCount word_count : "+ word_count);

		word_count = word_count.substring(0, word_count.length()-1);
		new RetrofitService().getManagerService().retroInsertWorkbookEachCount(dbpool.getStudentId(),
				word_count,
				""+dbpool.getWordLevel())
				.enqueue(new Callback<ManagerData>() {
					@Override
					public void onResponse(Response<ManagerData> response, Retrofit retrofit) {
						Log.e(TAG, "retroInsertWorkbookEachCount response : "+ response.body().getResult());
					}

					@Override
					public void onFailure(Throwable t) {
						Log.e(TAG, "retroInsertWorkbookEachCount onFailure : "+ t.toString());
					}
				});
	}
}
