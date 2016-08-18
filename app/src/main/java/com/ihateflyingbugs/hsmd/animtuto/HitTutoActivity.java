package com.ihateflyingbugs.hsmd.animtuto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.Get_my_uuid;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.ShakeEventListener;
import com.ihateflyingbugs.hsmd.SwipeDismissTouchListener;
import com.ihateflyingbugs.hsmd.TTS_Util;
import com.ihateflyingbugs.hsmd.TimerActivity;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.data.Mean;
import com.ihateflyingbugs.hsmd.data.MildangDate;
import com.ihateflyingbugs.hsmd.data.Word;
import com.ihateflyingbugs.hsmd.data.WordItemViewHolder;
import com.ihateflyingbugs.hsmd.flipimageview.FlipImage;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.model.StudyInfoData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.ihateflyingbugs.hsmd.service.ChatHeadService.TrayAnimationTimerTask;
import com.ihateflyingbugs.hsmd.service.Utils;
import com.roomorama.caldroid.CalendarHelper;

import org.andlib.ui.CircleAnimationView;
import org.andlib.ui.PercentView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import hirondelle.date4j.DateTime;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class HitTutoActivity extends Activity {
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 13579) {
			if (resultCode == 97531) {
				if (data.getBooleanExtra("start", false)) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							rl_tuto_hit_setting_top.setVisibility(View.VISIBLE);
							rl_tuto_hit_setting_top
									.startAnimation(settingTopAnim);


							Date_Step1 = date.get_currentTime();
							Time_Step1 = String.valueOf(System.currentTimeMillis());

						}
					}, 583);
				} else {
					setResult(1867, new Intent());
					new RetrofitService().getStudyInfoService().retroInsertClickNextWordSet(db.getStudentId())
							.enqueue(new Callback<StudyInfoData>() {
								@Override
								public void onResponse(Response<StudyInfoData> response, Retrofit retrofit) {

								}

								@Override
								public void onFailure(Throwable t) {

								}
							});
					finish();
					overridePendingTransition(android.R.anim.fade_in,
							R.anim.slide_down_bottom);
				}
			}
		}
	}

	// /////////////////////////////////
	// 타이머 관련
	//
	//
	public static LayoutParams mRootLayoutParams;
	public static RelativeLayout hit_tuto_layout;
	public static RelativeLayout timer_layout;
	public static LinearLayout time_layout;
	public static RelativeLayout percent_view_layout;
	public static RelativeLayout timer_off_layout;
	public static PercentView percent_view;
	public static TextView min_tv, sec_tv, today_tv;
	private int mStartDragX;
	private int mPrevDragX;
	private int mPrevDragY;
	private static final int MARGIN_BOTTOM_DP = 25; // BOTTOM MARGIN of the tray
	// in dps
	private static final int TRAY_DIM_X_DP = 48; // Width of the tray in dps
	private static final int TRAY_DIM_Y_DP = 48; // Height of the tray in dps
	private WindowManager mWindowManager; // Reference to the window
	Display display;
	float density;

	// ////////////////////////////////
	// 첫번째 페이지
	//
	//
	public static  CountDownTimer c_timer;
	RelativeLayout rl_tuto_hit_setting_top;
	TextView tv_tuto_hit_finish_reward; // 1, 2, 4 페이지에서 공통으로 쓰인다.
	TextView tv_tuto_hit_setting_level;
	CircleAnimationView tv_tuto_hit_low;
	CircleAnimationView tv_tuto_hit_mid;
	CircleAnimationView tv_tuto_hit_high;
	CircleAnimationView tv_tuto_hit_master;

	// ////////////////////////////////
	// 번째 페이지
	//
	//
	ImageView iv_tuto_hit_word_card_icon;
	LinearLayout ll_tuto_hit_word_card;
	TextView tv_tuto_hit_card_master_text;
	View dv_tuto_hit_card_divider_1;
	LinearLayout ll_tuto_hit_card_word_count;
	View dv_tuto_hit_card_divider_2;
	LinearLayout ll_tuto_hit_card_level;
	View dv_tuto_hit_card_divider_3;
	LinearLayout ll_tuto_hit_card_hit_rate;
	TextView tv_tuto_hit_test_start_text;
	CircleAnimationView bt_tuto_hit_start_test;
	CircleAnimationView bt_tuto_hit_change_level; // 2, 4 페이지에 공통으로 쓰인다.
	TextView tv_tuto_hit_word_count_value;
	TextView tv_tuto_hit_word_level;
	TextView tv_tuto_hit_rate;
	// ////////////////////////////////
	// 세번째 페이지
	//
	//
	RelativeLayout rl_tuto_hit_wordlist_top;
	TextView tv_tuto_hit_wordlist_text;
	TextView tv_tuto_hit_wordlist_time;
	// ////////////////////////////////
	// 네번째 페이지
	//
	//
	TextView tv_tuto_hit_word_test_end_text;
	TextView tv_tuto_hit_test_level_question;
	LinearLayout ll_tuto_hit_memorize_time;
	LinearLayout ll_tuto_hit_user_average_time;
	View dv_tuto_hit_time_divider;
	TextView tv_owner_time;
	LinearLayout ll_tuto_hit_owner_time;
	CircleAnimationView bt_tuto_hit_confirm_select_level;
	// ////////////////////////////////
	// 다섯번째 페이지
	//
	//
	TextView tv_tuto_hit_customizing_finish;
	TextView tv_tuto_hit_setting_finish_text;
	TextView tv_tuto_hit_finish_reward_after;
	ImageView iv_tuto_hit_arrow_icon;
	CircleAnimationView bt_tuto_hit_quest_finish;
	// ///////////////

	Button bt_finish_test;

	// ////////////////////////////////
	// 첫번째 페이지 애니메이션
	//
	//
	Animation settingTopAnim;
	Animation goalTextInAnim;
	Animation btn1Anim;
	Animation btn2Anim;
	Animation btn3Anim;
	Animation btn4Anim;
	Animation btn1OutAnim;
	Animation btn2OutAnim;
	Animation btn3OutAnim;
	Animation btn4OutAnim;
	Animation levelTextFadeOutAnim;

	// ////////////////////////////////
	// 두번째 페이지 애니메이션
	//
	//
	Animation wordCardInAnim;
	Animation wordCardIconInAnim;
	Animation halfExpandTopAnim;
	Animation masterTextAnim;
	Animation dividerAnim;
	Animation wordCountAnim;
	Animation levelAnim;
	Animation hitRateAnim;
	Animation wordTestStartTextAnim;
	Animation btnAnim;
	Animation btnBottomAnim;
	Animation change_level_anim;
	// ////////////////////////////////
	// 세번째 페이지 애니메이션
	//
	//
	Animation wordListTopInAnim;
	Animation wordListTextInAnim;
	Animation wordListTimeInAnim;
	Animation wordListTopOutAnim;
	Animation wordListTextOutAnim;
	Animation wordListTimeOutAnim;
	// ////////////////////////////////
	// 네번째 페이지 애니메이션
	//
	//
	Animation topExpandZero2HalfAnim;
	Animation averageTimeAnim;
	Animation testFinishTextAnim;
	Animation ownerTimeAnim;
	Animation questionAnim;
	Animation btnAnim2;
	Animation btnBottomAnim2;
	Animation goalTextOutAnim;
	Animation rewardTextOutAnim;
	// ////////////////////////////////
	// 다섯번째 페이지 애니메이션
	//
	//
	Animation expandTopAnim;
	Animation customizingFinishInAnim;
	Animation arrowIconAnim;
	Animation settingFinishInAnim;
	Animation finishRewardinAnim;
	Animation getRewardBtnAnim;
	// ////////////////////////////////
	// 테스트 단어장
	//
	//
	public static int mode;
	private boolean flag_set_swipe_mode = true;
	private String query;
	static final int ANIMATION_DURATION = 400;
	private DisplayMetrics metrics;

	private DBPool db;
	private static ArrayList<Word> words;
	private ListAdapter adapter;
	private ListView listView;
	private SensorManager mSensorManager;
	private ShakeEventListener mSensorListener;

	TTS_Util tts_util;

	Handler handler;

	private RelativeLayout relativeWord;
	// private PullToRefreshListView mPullToRefreshListView;
	private boolean isListAnimaion = true;
	private boolean isStartAnimaion = true;
	static Vibrator vibe;
	// 시작은 0 이므로 즉시 실행되고 진동 200 milliseconds, 멈춤 500 milliseconds 된다
	long[] pattern = { 0, 100, 50, 100 };

	private View actionbar_main;
	private SharedPreferences mPreference;
	private Editor editor;

	boolean finish_test;

	boolean isAnim = false;

	Map<String, String> tutorial2Params = new HashMap<String, String>();

	java.util.Date beforeTime;
	java.util.Date afterTime;
	TextView tv_owner_memorise_time;

	int[][] all_grade_count;

	int ONCE_WORD_COUNT = Config.ONCE_WORD_COUNT;


	public static boolean activityFlag = true;

	Map<String, String> Step1Params= new HashMap<String, String>();
	Map<String, String> Step2Params= new HashMap<String, String>();
	Map<String, String> Step3Params= new HashMap<String, String>();
	Map<String, String> Step4Params= new HashMap<String, String>();

	String Date_Step1;
	String Time_Step1;
	String Date_Step2;
	String Time_Step2;
	String Date_Step3;
	String Time_Step3;
	String Date_Step4;
	String Time_Step4;

	int[] Ex_time;

	private AudioManager audio;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_hit_tuto);



		handler = new Handler();

		db = DBPool.getInstance(this);
		db.createStudyTime();

		mPreference = getSharedPreferences(MainActivitys.preName,
				MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);

		hit_tuto_layout = (RelativeLayout) findViewById(R.id.hit_tuto_layout);
		timer_layout = (RelativeLayout) findViewById(R.id.timer_layout);
		time_layout = (LinearLayout) findViewById(R.id.time_layout);
		percent_view_layout = (RelativeLayout) findViewById(R.id.percent_view_layout);
		timer_off_layout = (RelativeLayout) findViewById(R.id.timer_off_layout);
		percent_view = (PercentView) findViewById(R.id.percent_view);
		min_tv = (TextView) findViewById(R.id.min_tv);
		sec_tv = (TextView) findViewById(R.id.sec_tv);
		today_tv = (TextView) findViewById(R.id.today_tv);

		today_tv.setText(new MildangDate().get_Month() + "."
				+ new MildangDate().get_day());

		display = ((WindowManager) this
				.getSystemService(getApplicationContext().WINDOW_SERVICE))
				.getDefaultDisplay();
		density = this.getResources().getDisplayMetrics().density;

		width = display.getWidth();

		c_timer = timer(percent_view, null);
		mRootLayoutParams = new LayoutParams(Utils.dpToPixels(TRAY_DIM_X_DP,
				getResources()),
				Utils.dpToPixels(TRAY_DIM_Y_DP, getResources()));


		mRootLayoutParams.width = display.getWidth() - 31 * (int) density;
		mRootLayoutParams.height = display.getHeight();
		mRootLayoutParams.setMargins(0, 0, 0, 32 * (int) density);

		// display.getHeight()

		hit_tuto_layout.updateViewLayout(timer_layout, mRootLayoutParams);

		ViewConfiguration vc = ViewConfiguration.get(timer_layout.getContext());
		mSlop = vc.getScaledTouchSlop();

		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		rl_tuto_hit_setting_top = (RelativeLayout) findViewById(R.id.rl_tuto_hit_setting_top);
		tv_tuto_hit_finish_reward = (TextView) findViewById(R.id.tv_tuto_hit_finish_reward);
		tv_tuto_hit_setting_level = (TextView) findViewById(R.id.tv_tuto_hit_setting_level);

		tv_tuto_hit_low = (CircleAnimationView) findViewById(R.id.tv_tuto_hit_low);
		tv_tuto_hit_mid = (CircleAnimationView) findViewById(R.id.tv_tuto_hit_mid);
		tv_tuto_hit_high = (CircleAnimationView) findViewById(R.id.tv_tuto_hit_high);
		tv_tuto_hit_master = (CircleAnimationView) findViewById(R.id.tv_tuto_hit_master);

		iv_tuto_hit_word_card_icon = (ImageView) findViewById(R.id.iv_tuto_hit_word_card_icon);
		ll_tuto_hit_word_card = (LinearLayout) findViewById(R.id.ll_tuto_hit_word_card);
		tv_tuto_hit_card_master_text = (TextView) findViewById(R.id.tv_tuto_hit_card_master_text);
		dv_tuto_hit_card_divider_1 = (View) findViewById(R.id.dv_tuto_hit_card_divider_1);
		ll_tuto_hit_card_word_count = (LinearLayout) findViewById(R.id.ll_tuto_hit_card_word_count);
		dv_tuto_hit_card_divider_2 = (View) findViewById(R.id.dv_tuto_hit_card_divider_2);
		ll_tuto_hit_card_level = (LinearLayout) findViewById(R.id.ll_tuto_hit_card_level);
		dv_tuto_hit_card_divider_3 = (View) findViewById(R.id.dv_tuto_hit_card_divider_3);
		ll_tuto_hit_card_hit_rate = (LinearLayout) findViewById(R.id.ll_tuto_hit_card_hit_rate);
		tv_tuto_hit_test_start_text = (TextView) findViewById(R.id.tv_tuto_hit_test_start_text);
		bt_tuto_hit_start_test = (CircleAnimationView) findViewById(R.id.bt_tuto_hit_start_test);
		bt_tuto_hit_change_level = (CircleAnimationView) findViewById(R.id.bt_tuto_hit_change_level);
		tv_tuto_hit_word_count_value = (TextView) findViewById(R.id.tv_tuto_hit_word_count_value);
		tv_tuto_hit_word_level = (TextView) findViewById(R.id.tv_tuto_hit_word_level);
		tv_tuto_hit_rate = (TextView) findViewById(R.id.tv_tuto_hit_rate);

		rl_tuto_hit_wordlist_top = (RelativeLayout) findViewById(R.id.rl_tuto_hit_wordlist_top);
		tv_tuto_hit_wordlist_text = (TextView) findViewById(R.id.tv_tuto_hit_wordlist_text);
		tv_tuto_hit_wordlist_time = (TextView) findViewById(R.id.tv_tuto_hit_wordlist_time);

		tv_tuto_hit_word_test_end_text = (TextView) findViewById(R.id.tv_tuto_hit_word_test_end_text);
		tv_tuto_hit_test_level_question = (TextView) findViewById(R.id.tv_tuto_hit_test_level_question);
		ll_tuto_hit_memorize_time = (LinearLayout) findViewById(R.id.ll_tuto_hit_memorize_time);
		ll_tuto_hit_user_average_time = (LinearLayout) findViewById(R.id.ll_tuto_hit_user_average_time);
		dv_tuto_hit_time_divider = (View) findViewById(R.id.dv_tuto_hit_time_divider);
		ll_tuto_hit_owner_time = (LinearLayout) findViewById(R.id.ll_tuto_hit_owner_time);
		tv_owner_time = (TextView) findViewById(R.id.tv_owner_time);
		bt_tuto_hit_confirm_select_level = (CircleAnimationView) findViewById(R.id.bt_tuto_hit_confirm_select_level);

		tv_tuto_hit_customizing_finish = (TextView) findViewById(R.id.tv_tuto_hit_customizing_finish);
		tv_tuto_hit_setting_finish_text = (TextView) findViewById(R.id.tv_tuto_hit_setting_finish_text);
		tv_tuto_hit_finish_reward_after = (TextView) findViewById(R.id.tv_tuto_hit_finish_reward_after);
		iv_tuto_hit_arrow_icon = (ImageView) findViewById(R.id.iv_tuto_hit_arrow_icon);
		bt_tuto_hit_quest_finish = (CircleAnimationView) findViewById(R.id.bt_tuto_hit_quest_finish);

		settingTopAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.top_layout);
		goalTextInAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.set_goal_text_in);
		btn1Anim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.btn_1_anim);
		btn2Anim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.btn_2_anim);
		btn3Anim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.btn_3_anim);
		btn4Anim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.btn_4_anim);
		btn1OutAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.btn_1_out_anim);
		btn2OutAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.btn_2_out_anim);
		btn3OutAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.btn_3_out_anim);
		btn4OutAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.btn_4_out_anim);

		levelTextFadeOutAnim = AnimationUtils.loadAnimation(
				getApplicationContext(), android.R.anim.fade_out);
		wordCardInAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.word_card_in_anim);
		wordCardIconInAnim = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.card_icon_in_anim);
		halfExpandTopAnim = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.top_layout_half_expand);
		masterTextAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				android.R.anim.fade_in);
		dividerAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.word_card_divider_anim);
		wordCountAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				android.R.anim.fade_in);
		levelAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				android.R.anim.fade_in);
		hitRateAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				android.R.anim.fade_in);
		wordTestStartTextAnim = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.word_test_start_text);
		btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.btn_in);
		btnBottomAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.btn_in_bottom);
		change_level_anim = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);

		wordListTopInAnim = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.wordlist_top_in);
		wordListTextInAnim = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.wordlist_text_in);
		wordListTimeInAnim = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.wordlist_time_in);
		wordListTopOutAnim = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.wordlist_top_out);
		wordListTextOutAnim = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.wordlist_text_out);
		wordListTimeOutAnim = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.wordlist_time_out);
		tv_owner_memorise_time = (TextView) findViewById(R.id.tv_owner_memorise_time);

		topExpandZero2HalfAnim = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.top_layout_zero_2_half);
		averageTimeAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.average_time_anim);
		testFinishTextAnim = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.set_goal_text_in);
		ownerTimeAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.owner_time_anim);
		questionAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.word_test_start_text);
		btnAnim2 = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.btn_in);
		btnBottomAnim2 = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.btn_in_bottom);
		goalTextOutAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.set_goal_text_out);
		rewardTextOutAnim = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.reward_text_out);

		expandTopAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.top_layout_expand);
		customizingFinishInAnim = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.set_goal_finish_text);
		arrowIconAnim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.punch_icon_in);
		settingFinishInAnim = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.do_not_forget_text_in);
		finishRewardinAnim = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.finish_reward_in);
		getRewardBtnAnim = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.btn_in);

		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setTextSize(WillTutoActivity.spTopx(getApplicationContext(), 12));
		paint.setAntiAlias(true);
		paint.setAlpha(222);
		paint.setTextAlign(Paint.Align.CENTER);

		Paint whiteBtnPaint = new Paint();
		whiteBtnPaint.setStyle(Paint.Style.FILL);
		whiteBtnPaint.setColor(Color.BLACK);
		whiteBtnPaint.setAlpha(138);
		whiteBtnPaint.setTextSize(WillTutoActivity.spTopx(
				getApplicationContext(), 12));
		whiteBtnPaint.setAntiAlias(true);
		whiteBtnPaint.setTextAlign(Paint.Align.CENTER);

		Paint whiteBtnBlueTextPaint = new Paint();
		whiteBtnBlueTextPaint.setStyle(Paint.Style.FILL);
		whiteBtnBlueTextPaint.setColor(new Color().rgb(47, 110, 155));
		whiteBtnBlueTextPaint.setTextSize(WillTutoActivity.spTopx(
				getApplicationContext(), 12));
		whiteBtnBlueTextPaint.setAntiAlias(true);
		whiteBtnBlueTextPaint.setTextAlign(Paint.Align.CENTER);

		percent_view_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Log.d("activityFlag1", Boolean.toString(activityFlag));

				if (activityFlag) {

					pause(c_timer);
					activityFlag = false;
					Log.d("activityFlag2", Boolean.toString(activityFlag));
					// TODO Auto-generated method stub
					float tmp_width = mRootLayoutParams.width;
					float tmp_height = mRootLayoutParams.height;
					int bar_height = 0;
					float myY = percent_view_layout.getY();
					int myHeight = percent_view_layout.getHeight();

					restart_flag = false;

					Intent i = new Intent(HitTutoActivity.this,
							TimerActivity.class);

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
					i.putExtra("hit_tuto", true);
					startActivity(i);
					FlurryAgent.logEvent("BaseActivity:OpenTimerPage");
				}
			}

		});

		percent_view_layout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub


				mRootLayoutParams.setMargins(0, 0, 0, 0);
				// TODO Auto-generated method stub
				boolean onClick = false;

				final int action = event.getActionMasked();

				switch (action) {

					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_MOVE:
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_CANCEL:
						onClick = dragTray(action, (int) event.getRawX(),
								(int) event.getRawY());

				}
				Log.d("qqqq", "onClick : " + Boolean.toString(onClick));
				return onClick;
			}
		});

		bt_tuto_hit_quest_finish.setAnimButton(paint,
				R.drawable.qwest_choice_btn,
				new CircleAnimationView.ClickCallbacks() {
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						Handler handler = new Handler(Looper.getMainLooper());
						handler.post(new Runnable() {
							@Override
							public void run() {
								bt_tuto_hit_quest_finish.stopAnimation();

								Intent i = new Intent(getApplicationContext(),
										HitRewardActivity.class);
								i.putExtra("start", false);
								startActivityForResult(i, 13579);

								FlurryAgent.logEvent("HitTutoActivity:Click_Finish", Step4Params);
								Step4Params.put("Start", Date_Step4);
								Step4Params.put("End", date.get_currentTime());
								Step4Params.put(
										"Duration",
										""
												+ ((Long.valueOf(System.currentTimeMillis()) - Long
												.valueOf(Time_Step4))) / 1000);
							}
						});
					}
				});

		all_grade_count = db.getCountOfWordsAsGrade();

		tv_tuto_hit_low.setAnimButton(whiteBtnPaint,
				R.drawable.qwest_choice_btn,
				new CircleAnimationView.ClickCallbacks() {
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						Handler handler = new Handler(Looper.getMainLooper());
						handler.post(new Runnable() {
							@Override
							public void run() {

								Ex_time = db.getTime();
								tv_tuto_hit_low.stopAnimation();
								db.insertWordLevel(1);
								tv_tuto_hit_card_master_text.setText("적중 기초 단어장");
								bt_tuto_hit_confirm_select_level.setText("적중 기초 단어장으로 결정");
								tv_tuto_hit_word_count_value.setText(all_grade_count[0][1] + "개");
								tv_tuto_hit_word_level.setText("매우 쉬움");
								tv_tuto_hit_rate.setText("50~100%");
								tv_tuto_hit_low.startAnimation(btn1OutAnim);
								tv_tuto_hit_mid.startAnimation(btn2OutAnim);
								tv_tuto_hit_high.startAnimation(btn3OutAnim);
								tv_tuto_hit_master.startAnimation(btn4OutAnim);
								iv_tuto_hit_word_card_icon.setImageResource(R.drawable.wordqwest_ic_book1);

								FlurryAgent.logEvent("HitTutoActivity:Click_BasicWord", Step1Params);
								Step1Params.put("Start", Date_Step1);
								Step1Params.put("End", date.get_currentTime());
								Step1Params.put("Duration", "" + ((Long.valueOf(System.currentTimeMillis()) - Long.valueOf(Time_Step1))) / 1000);

								Date_Step2 = date.get_currentTime();
								Time_Step2 = String.valueOf(System.currentTimeMillis());

							}
						});
					}
				});
		tv_tuto_hit_mid.setAnimButton(whiteBtnPaint,
				R.drawable.qwest_choice_btn,
				new CircleAnimationView.ClickCallbacks() {
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						Handler handler = new Handler(Looper.getMainLooper());
						handler.post(new Runnable() {
							@Override
							public void run() {

								Ex_time = db.getTime();

								tv_tuto_hit_mid.stopAnimation();

								tv_tuto_hit_card_master_text.setText("적중 필수1 단어장");
								bt_tuto_hit_confirm_select_level.setText("적중 필수1 단어장으로 결정");
								tv_tuto_hit_word_count_value.setText(all_grade_count[1][1] + "개");
								tv_tuto_hit_word_level.setText("쉬움");
								tv_tuto_hit_rate.setText("20~50%");
								db.insertWordLevel(2);
								tv_tuto_hit_low.startAnimation(btn1OutAnim);
								tv_tuto_hit_mid.startAnimation(btn2OutAnim);
								tv_tuto_hit_high.startAnimation(btn3OutAnim);
								tv_tuto_hit_master.startAnimation(btn4OutAnim);
								iv_tuto_hit_word_card_icon.setImageResource(R.drawable.wordqwest_ic_book2);

								FlurryAgent.logEvent("HitTutoActivity:Click_EssentielWord_1", Step1Params);
								Step1Params.put("Start", Date_Step1);
								Step1Params.put("End", date.get_currentTime());
								Step1Params.put("Duration", "" + ((Long.valueOf(System.currentTimeMillis()) - Long.valueOf(Time_Step1))) / 1000);

								Date_Step2 = date.get_currentTime();
								Time_Step2 = String.valueOf(System.currentTimeMillis());
							}
						});
					}
				});
		tv_tuto_hit_high.setAnimButton(whiteBtnPaint,
				R.drawable.qwest_choice_btn,
				new CircleAnimationView.ClickCallbacks() {
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						Handler handler = new Handler(Looper.getMainLooper());
						handler.post(new Runnable() {
							@Override
							public void run() {

								Ex_time = db.getTime();

								tv_tuto_hit_high.stopAnimation();
								db.insertWordLevel(3);
								tv_tuto_hit_card_master_text.setText("적중 필수2 단어장");
								bt_tuto_hit_confirm_select_level.setText("적중 필수2 단어장으로 결정");
								tv_tuto_hit_word_count_value.setText(all_grade_count[2][1] + "개");
								tv_tuto_hit_word_level.setText("보통");
								tv_tuto_hit_rate.setText("10~20%");
								tv_tuto_hit_low.startAnimation(btn1OutAnim);
								tv_tuto_hit_mid.startAnimation(btn2OutAnim);
								tv_tuto_hit_high.startAnimation(btn3OutAnim);
								tv_tuto_hit_master.startAnimation(btn4OutAnim);
								iv_tuto_hit_word_card_icon.setImageResource(R.drawable.wordqwest_ic_book3);

								FlurryAgent.logEvent("HitTutoActivity:Click_EssentielWord_2", Step1Params);
								Step1Params.put("Start", Date_Step1);
								Step1Params.put("End", date.get_currentTime());
								Step1Params.put("Duration", "" + ((Long.valueOf(System.currentTimeMillis()) - Long.valueOf(Time_Step1))) / 1000);

								Date_Step2 = date.get_currentTime();
								Time_Step2 = String.valueOf(System.currentTimeMillis());

							}
						});
					}
				});

		tv_tuto_hit_master.setAnimButton(whiteBtnPaint,
				R.drawable.qwest_choice_btn,
				new CircleAnimationView.ClickCallbacks() {
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						Handler handler = new Handler(Looper.getMainLooper());
						handler.post(new Runnable() {
							@Override
							public void run() {

								Ex_time = db.getTime();

								tv_tuto_hit_master.stopAnimation();
								db.insertWordLevel(4);
								tv_tuto_hit_card_master_text.setText("적중 마스터 단어장");
								bt_tuto_hit_confirm_select_level.setText("적중 마스터 단어장으로 결정");
								tv_tuto_hit_word_count_value.setText(all_grade_count[3][1] + "개");
								tv_tuto_hit_word_level.setText("어려움");
								tv_tuto_hit_rate.setText("5~10%");
								tv_tuto_hit_low.startAnimation(btn1OutAnim);
								tv_tuto_hit_mid.startAnimation(btn2OutAnim);
								tv_tuto_hit_high.startAnimation(btn3OutAnim);
								tv_tuto_hit_master.startAnimation(btn4OutAnim);
								iv_tuto_hit_word_card_icon.setImageResource(R.drawable.wordqwest_ic_book4);

								FlurryAgent.logEvent("HitTutoActivity:Click_MasterWord", Step1Params);
								Step1Params.put("Start", Date_Step1);
								Step1Params.put("End", date.get_currentTime());
								Step1Params.put("Duration", "" + ((Long.valueOf(System.currentTimeMillis()) - Long.valueOf(Time_Step1))) / 1000);

								Date_Step2 = date.get_currentTime();
								Time_Step2 = String.valueOf(System.currentTimeMillis());

							}
						});
					}
				});

		bt_tuto_hit_start_test.setAnimButton(paint,
				R.drawable.wordqwest_confirm_btn,
				new CircleAnimationView.ClickCallbacks() {
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						Handler handler = new Handler(Looper.getMainLooper());
						handler.post(new Runnable() {
							@Override
							public void run() {
								bt_tuto_hit_start_test.stopAnimation();

								rl_tuto_hit_setting_top.getAnimation()
										.setFillAfter(false);
								rl_tuto_hit_setting_top
										.setVisibility(View.GONE);

								tv_tuto_hit_finish_reward
										.setVisibility(View.GONE);
								tv_tuto_hit_setting_level
										.setVisibility(View.GONE);
								tv_tuto_hit_low.setVisibility(View.GONE);
								tv_tuto_hit_mid.setVisibility(View.GONE);
								tv_tuto_hit_high.setVisibility(View.GONE);
								tv_tuto_hit_master.setVisibility(View.GONE);

								iv_tuto_hit_word_card_icon
										.setVisibility(View.GONE);
								ll_tuto_hit_word_card.setVisibility(View.GONE);
								tv_tuto_hit_card_master_text
										.setVisibility(View.GONE);
								dv_tuto_hit_card_divider_1
										.setVisibility(View.GONE);
								ll_tuto_hit_card_word_count
										.setVisibility(View.GONE);
								dv_tuto_hit_card_divider_2
										.setVisibility(View.GONE);
								ll_tuto_hit_card_level.setVisibility(View.GONE);
								dv_tuto_hit_card_divider_3
										.setVisibility(View.GONE);
								ll_tuto_hit_card_hit_rate
										.setVisibility(View.GONE);
								tv_tuto_hit_test_start_text
										.setVisibility(View.GONE);
								bt_tuto_hit_start_test.setVisibility(View.GONE);
								bt_tuto_hit_change_level
										.setVisibility(View.GONE);

								rl_tuto_hit_wordlist_top
										.setVisibility(View.VISIBLE);
								rl_tuto_hit_wordlist_top
										.startAnimation(wordListTopInAnim);

						/*
						 * 단어장 시작
						 */
								setTestList();

								FlurryAgent.logEvent("HitTutoActivity:Click_StartTest", Step2Params);
								Step2Params.put("Start", Date_Step2);
								Step2Params.put("End", date.get_currentTime());
								Step2Params.put(
										"Duration",
										""
												+ ((Long.valueOf(System.currentTimeMillis()) - Long
												.valueOf(Time_Step2))) / 1000);


								Date_Step3 = date.get_currentTime();
								Time_Step3 = String.valueOf(System.currentTimeMillis());

							}
						});
					}
				});

		bt_tuto_hit_change_level.setAnimButton(whiteBtnBlueTextPaint,
				R.drawable.qwest_choice_btn,
				new CircleAnimationView.ClickCallbacks() {
					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						Handler handler = new Handler(Looper.getMainLooper());
						handler.post(new Runnable() {
							@Override
							public void run() {
								bt_tuto_hit_change_level.stopAnimation();
							}
						});

						//						rl_tuto_hit_setting_top.clearAnimation();
						rl_tuto_hit_setting_top.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));

						bt_tuto_hit_change_level.clearAnimation();
						bt_tuto_hit_change_level.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));


						if (bt_tuto_hit_start_test.getVisibility() == View.VISIBLE) {
							//							tv_tuto_hit_low.clearAnimation();
							//							tv_tuto_hit_mid.clearAnimation();
							//							tv_tuto_hit_high.clearAnimation();
							//							tv_tuto_hit_master.clearAnimation();

							iv_tuto_hit_word_card_icon.clearAnimation();
							ll_tuto_hit_word_card.clearAnimation();
							tv_tuto_hit_card_master_text.clearAnimation();
							dv_tuto_hit_card_divider_1.clearAnimation();
							ll_tuto_hit_card_word_count.clearAnimation();
							dv_tuto_hit_card_divider_2.clearAnimation();
							ll_tuto_hit_card_level.clearAnimation();
							dv_tuto_hit_card_divider_3.clearAnimation();
							ll_tuto_hit_card_hit_rate.clearAnimation();
							tv_tuto_hit_test_start_text.clearAnimation();
							bt_tuto_hit_start_test.clearAnimation();

							iv_tuto_hit_word_card_icon.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
							ll_tuto_hit_word_card.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
							tv_tuto_hit_card_master_text.startAnimation(AnimationUtils
									.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
							dv_tuto_hit_card_divider_1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
							ll_tuto_hit_card_word_count.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
							dv_tuto_hit_card_divider_2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
							ll_tuto_hit_card_level.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
							dv_tuto_hit_card_divider_3.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
							ll_tuto_hit_card_hit_rate.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
							tv_tuto_hit_test_start_text.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
							bt_tuto_hit_start_test.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));

							FlurryAgent.logEvent("HitTutoActivity:Click_StartReSelect_Word", Step2Params);
							Step2Params.put("Start", Date_Step2);
							Step2Params.put("End", date.get_currentTime());
							Step2Params.put(
									"Duration",
									""
											+ ((Long.valueOf(System.currentTimeMillis()) - Long
											.valueOf(Time_Step2))) / 1000);

							Date_Step1 = date.get_currentTime();
							Time_Step1 = String.valueOf(System.currentTimeMillis());
						} else if (bt_tuto_hit_confirm_select_level.getVisibility() == View.VISIBLE) {
							tv_tuto_hit_word_test_end_text.clearAnimation();
							tv_tuto_hit_test_level_question.clearAnimation();
							ll_tuto_hit_memorize_time.clearAnimation();
							ll_tuto_hit_user_average_time.clearAnimation();
							dv_tuto_hit_time_divider.clearAnimation();
							tv_owner_time.clearAnimation();
							ll_tuto_hit_owner_time.clearAnimation();
							bt_tuto_hit_confirm_select_level.clearAnimation();

							tv_tuto_hit_word_test_end_text.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
									android.R.anim.fade_out));
							tv_tuto_hit_test_level_question.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
									android.R.anim.fade_out));
							ll_tuto_hit_memorize_time.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
							ll_tuto_hit_user_average_time.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
									android.R.anim.fade_out));
							dv_tuto_hit_time_divider.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
							tv_owner_time.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
							ll_tuto_hit_owner_time.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));

							FlurryAgent.logEvent("HitTutoActivity:Click_StartReSelect_Word", Step3Params);
							Step3Params.put("Start", Date_Step3);
							Step3Params.put("End", date.get_currentTime());
							Step3Params.put(
									"Duration",
									""
											+ ((Long.valueOf(System.currentTimeMillis()) - Long
											.valueOf(Time_Step3))) / 1000);

							Date_Step1 = date.get_currentTime();
							Time_Step1 = String.valueOf(System.currentTimeMillis());
						}
						bt_tuto_hit_confirm_select_level.startAnimation(change_level_anim);



					}
				});

		bt_tuto_hit_confirm_select_level.setAnimButton(paint,
				R.drawable.qwest_choice_btn,
				new CircleAnimationView.ClickCallbacks() {
					@Override
					public void onClick() {
						Handler handler = new Handler(Looper.getMainLooper());
						handler.post(new Runnable() {
							@Override
							public void run() {
								rl_tuto_hit_setting_top
										.startAnimation(expandTopAnim);
								tv_tuto_hit_word_test_end_text
										.startAnimation(goalTextOutAnim);
								tv_tuto_hit_finish_reward
										.startAnimation(rewardTextOutAnim);
								ll_tuto_hit_memorize_time
										.startAnimation(goalTextOutAnim);

								FlurryAgent.logEvent("HitTutoActivity:Click_Confirm__Word", Step3Params);
								Step3Params.put("Start", Date_Step3);
								Step3Params.put("End", date.get_currentTime());
								Step3Params.put(
										"Duration",
										""
												+ ((Long.valueOf(System.currentTimeMillis()) - Long
												.valueOf(Time_Step3))) / 1000);


								Date_Step4 = date.get_currentTime();
								Time_Step4 = String.valueOf(System.currentTimeMillis());

							}
						});
					}
				});

		tv_tuto_hit_low.setText("적중 기초 (수능적중률 50% 이상의 필수 단어)");
		tv_tuto_hit_mid.setText("적중 필수1 (수능적중률 20% 이상의 필수 단어)");
		tv_tuto_hit_high.setText("적중 필수2 (수능적중률 10%대의 고득점 단어)");
		tv_tuto_hit_master.setText("적중 마스터 (1등급을 위한 최상급 단어)");
		bt_tuto_hit_start_test.setText("테스트 하기");
		bt_tuto_hit_change_level.setText("다른 적중 단어장으로 변경하기");
		bt_tuto_hit_confirm_select_level.setText("적중 마스터 단어장으로 결정");
		bt_tuto_hit_quest_finish.setText("보상받기");

		tv_tuto_hit_low.setColor(0x31, 0x62, 0x9c);
		tv_tuto_hit_mid.setColor(0x31, 0x62, 0x9c);
		tv_tuto_hit_high.setColor(0x31, 0x62, 0x9c);
		tv_tuto_hit_master.setColor(0x31, 0x62, 0x9c);
		bt_tuto_hit_start_test.setColor(0xff, 0xff, 0xff);
		bt_tuto_hit_change_level.setColor(0x31, 0x62, 0x9c);
		bt_tuto_hit_confirm_select_level.setColor(0xff, 0xff, 0xff);
		bt_tuto_hit_quest_finish.setColor(0xff, 0xff, 0xff);

		// tv_tuto_hit_low.setTextColor(138, 0, 0, 0);
		// tv_tuto_hit_mid.setTextColor(138, 0, 0, 0);
		// tv_tuto_hit_high.setTextColor(138, 0, 0, 0);
		// tv_tuto_hit_master.setTextColor(138, 0, 0, 0);

		bt_tuto_hit_start_test.setTextColor(255, 255, 255, 255);
		// bt_tuto_hit_change_level.setTextColor(255, 47, 110, 155);
		bt_tuto_hit_quest_finish.setTextColor(255, 255, 255, 255);

		// ////////////////////////////////////////////////////////////////////////////////////////////////////
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent i = new Intent(getApplicationContext(),
						HitRewardActivity.class);
				i.putExtra("start", true);
				startActivityForResult(i, 13579);
			}
		}, 900);
		// rl_tuto_hit_setting_top.startAnimation(settingTopAnim); // 전체 애니메이션의
		// 처음 시작 부분
		// ////////////////////////////////////////////////////////////////////////////////////////////////////

		change_level_anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {

				rl_tuto_hit_setting_top.clearAnimation();
				rl_tuto_hit_setting_top.setVisibility(View.INVISIBLE);

				bt_tuto_hit_change_level.clearAnimation();
				bt_tuto_hit_change_level.setVisibility(View.INVISIBLE);

				// tv_tuto_hit_finish_reward.clearAnimation();
				// tv_tuto_hit_setting_level.clearAnimation();
				// tv_tuto_hit_low.clearAnimation();
				// tv_tuto_hit_mid.clearAnimation();
				// tv_tuto_hit_high.clearAnimation();
				// tv_tuto_hit_master.clearAnimation();
				//
				// iv_tuto_hit_word_card_icon.clearAnimation();
				// ll_tuto_hit_word_card.clearAnimation();
				// tv_tuto_hit_card_master_text.clearAnimation();
				// dv_tuto_hit_card_divider_1.clearAnimation();
				// ll_tuto_hit_card_word_count.clearAnimation();
				// dv_tuto_hit_card_divider_2.clearAnimation();
				// ll_tuto_hit_card_level.clearAnimation();
				// dv_tuto_hit_card_divider_3.clearAnimation();
				// ll_tuto_hit_card_hit_rate.clearAnimation();
				// tv_tuto_hit_test_start_text.clearAnimation();
				// bt_tuto_hit_start_test.clearAnimation();

				//				tv_tuto_hit_finish_reward.setVisibility(View.INVISIBLE);
				//				tv_tuto_hit_setting_level.setVisibility(View.INVISIBLE);
				//				tv_tuto_hit_low.setVisibility(View.INVISIBLE);
				//				tv_tuto_hit_mid.setVisibility(View.INVISIBLE);
				//				tv_tuto_hit_high.setVisibility(View.INVISIBLE);
				//				tv_tuto_hit_master.setVisibility(View.INVISIBLE);

				iv_tuto_hit_word_card_icon.setVisibility(View.INVISIBLE);
				ll_tuto_hit_word_card.setVisibility(View.INVISIBLE);
				tv_tuto_hit_card_master_text.setVisibility(View.INVISIBLE);
				dv_tuto_hit_card_divider_1.setVisibility(View.INVISIBLE);
				ll_tuto_hit_card_word_count.setVisibility(View.INVISIBLE);
				dv_tuto_hit_card_divider_2.setVisibility(View.INVISIBLE);
				ll_tuto_hit_card_level.setVisibility(View.INVISIBLE);
				dv_tuto_hit_card_divider_3.setVisibility(View.INVISIBLE);
				ll_tuto_hit_card_hit_rate.setVisibility(View.INVISIBLE);
				tv_tuto_hit_test_start_text.setVisibility(View.INVISIBLE);
				bt_tuto_hit_start_test.setVisibility(View.INVISIBLE);

				// tv_tuto_hit_word_test_end_text.clearAnimation();
				// tv_tuto_hit_test_level_question.clearAnimation();
				// ll_tuto_hit_memorize_time.clearAnimation();
				// ll_tuto_hit_user_average_time.clearAnimation();
				// dv_tuto_hit_time_divider.clearAnimation();
				// tv_owner_time.clearAnimation();
				// ll_tuto_hit_owner_time.clearAnimation();
				// bt_tuto_hit_confirm_select_level.clearAnimation();

				tv_tuto_hit_word_test_end_text.setVisibility(View.INVISIBLE);
				tv_tuto_hit_test_level_question.setVisibility(View.INVISIBLE);
				ll_tuto_hit_memorize_time.setVisibility(View.INVISIBLE);
				ll_tuto_hit_user_average_time.setVisibility(View.INVISIBLE);
				dv_tuto_hit_time_divider.setVisibility(View.INVISIBLE);
				bt_tuto_hit_confirm_select_level.setVisibility(View.INVISIBLE);


				rl_tuto_hit_setting_top.getLayoutParams().height = getDpToPixel(getApplicationContext(), 176);

				rl_tuto_hit_setting_top.setVisibility(View.VISIBLE);
				rl_tuto_hit_setting_top.startAnimation(settingTopAnim);

				halfExpandTopAnim.setFillAfter(true);

				tv_tuto_hit_low.setClickable(true);
				tv_tuto_hit_mid.setClickable(true);
				tv_tuto_hit_high.setClickable(true);
				tv_tuto_hit_master.setClickable(true);
			}
		});

		arrowIconAnim.setAnimationListener(new AnimationListener() {

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
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						bt_tuto_hit_quest_finish.setVisibility(View.VISIBLE);
						bt_tuto_hit_quest_finish
								.startAnimation(getRewardBtnAnim);
					}
				}, 208);
			}
		});

		expandTopAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub

				tv_tuto_hit_customizing_finish.setVisibility(View.VISIBLE);
				tv_tuto_hit_setting_finish_text.setVisibility(View.VISIBLE);
				tv_tuto_hit_finish_reward_after.setVisibility(View.VISIBLE);
				iv_tuto_hit_arrow_icon.setVisibility(View.VISIBLE);

				tv_tuto_hit_customizing_finish
						.startAnimation(customizingFinishInAnim);
				tv_tuto_hit_setting_finish_text
						.startAnimation(settingFinishInAnim);
				tv_tuto_hit_finish_reward_after
						.startAnimation(finishRewardinAnim);
				iv_tuto_hit_arrow_icon.startAnimation(arrowIconAnim);
				bt_tuto_hit_confirm_select_level.setVisibility(View.GONE);
				bt_tuto_hit_change_level.setVisibility(View.GONE);
				tv_tuto_hit_test_level_question.setVisibility(View.GONE);

			}
		});

		rewardTextOutAnim.setAnimationListener(new AnimationListener() {

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
				rl_tuto_hit_setting_top.getLayoutParams().height = LayoutParams.MATCH_PARENT;
				tv_tuto_hit_setting_level.setVisibility(View.GONE);
				tv_tuto_hit_finish_reward.setVisibility(View.GONE);
				ll_tuto_hit_memorize_time.setVisibility(View.GONE);
			}
		});

		questionAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				bt_tuto_hit_confirm_select_level.setVisibility(View.VISIBLE);
				bt_tuto_hit_confirm_select_level.startAnimation(btnAnim2);

				bt_tuto_hit_change_level.setVisibility(View.VISIBLE);
				bt_tuto_hit_change_level.startAnimation(btnBottomAnim2);
			}
		});

		topExpandZero2HalfAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				relativeWord.setVisibility(View.GONE);
				tv_tuto_hit_word_test_end_text.setVisibility(View.VISIBLE);
				tv_tuto_hit_word_test_end_text
						.startAnimation(testFinishTextAnim);

				tv_tuto_hit_finish_reward.setVisibility(View.VISIBLE);
				tv_tuto_hit_finish_reward.startAnimation(testFinishTextAnim);

				c_timer.cancel();
				timer_layout.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				ll_tuto_hit_memorize_time.setVisibility(View.VISIBLE);
				ll_tuto_hit_user_average_time.setVisibility(View.VISIBLE);
				ll_tuto_hit_user_average_time.startAnimation(averageTimeAnim);

				dv_tuto_hit_time_divider.setVisibility(View.VISIBLE);
				dv_tuto_hit_time_divider.startAnimation(ownerTimeAnim);
				ll_tuto_hit_owner_time.setVisibility(View.VISIBLE);
				ll_tuto_hit_owner_time.startAnimation(ownerTimeAnim);

				tv_tuto_hit_test_level_question.setVisibility(View.VISIBLE);
				tv_tuto_hit_test_level_question.startAnimation(questionAnim);

			}
		});

		wordListTopOutAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				Long memoriseTime = (-beforeTime.getTime() + afterTime
						.getTime()) / 1000L;
				Log.d("MemoriseTime",
						beforeTime.getTime() + " " + afterTime.getTime());
				int min = (int) (memoriseTime / 60);
				int sec = (int) (memoriseTime % 60);

				mPreference = getSharedPreferences(MainActivitys.preName,
						MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
				Editor editor = mPreference.edit();

				//db.insertTime(min, sec);

				Log.d("MemoriseTime", min + " " + sec + " " + memoriseTime);

				int[] saved_time = db.getTime();
				if(saved_time[1]-Ex_time[1]<0){
					saved_time[0]--;
					saved_time[1] = 60+saved_time[1]-Ex_time[1];
					saved_time[0] = saved_time[0]-Ex_time[0];
				}else{
					saved_time[1] = saved_time[1]-Ex_time[1];
					saved_time[0] = saved_time[0]-Ex_time[0];
				}
				Log.v("gggg", ""+Ex_time[0]+"  "+Ex_time[1]);
				Log.d("gggg", ""+saved_time[0]+"  "+saved_time[1]);
				String minStr;
				String secStr;

				if (saved_time[0] < 10) {
					minStr = "0" + saved_time[0];
				} else {
					minStr = saved_time[0] + "";
				}

				if (saved_time[1] < 10) {
					secStr = "0" + saved_time[1];
				} else {
					secStr = saved_time[1] + "";
				}

				tv_owner_memorise_time.setText(minStr + " : " + secStr);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				rl_tuto_hit_wordlist_top.setVisibility(View.GONE);
				tv_tuto_hit_wordlist_text.setVisibility(View.GONE);
				tv_tuto_hit_wordlist_time.setVisibility(View.GONE);

				topExpandZero2HalfAnim.setFillAfter(true);
				rl_tuto_hit_setting_top.getLayoutParams().height = getDpToPixel(getApplicationContext(), 228);
				rl_tuto_hit_setting_top.setVisibility(View.VISIBLE);
				rl_tuto_hit_setting_top.startAnimation(topExpandZero2HalfAnim);
				/*
				 * 테스트
				 */

				// new Handler().postDelayed(new Runnable() {
				//
				// @Override
				// public void run() {
				// topExpandZero2HalfAnim.setFillAfter(true);
				// rl_tuto_hit_setting_top.getLayoutParams().height =
				// getDpToPixel(getApplicationContext(), 232);
				// rl_tuto_hit_setting_top.setVisibility(View.VISIBLE);
				// rl_tuto_hit_setting_top.startAnimation(topExpandZero2HalfAnim);
				// }
				// }, 1500);
			}
		});

		wordListTimeInAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {

			}
		});

		wordListTopInAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

				// rl_tuto_hit_setting_top.setVisibility(View.GONE);

				tv_tuto_hit_wordlist_text.setVisibility(View.VISIBLE);
				tv_tuto_hit_wordlist_text.startAnimation(wordListTextInAnim);
				tv_tuto_hit_wordlist_time.setVisibility(View.VISIBLE);
				tv_tuto_hit_wordlist_time.startAnimation(wordListTimeInAnim);

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				beforeTime = new java.util.Date();

				percent_view_layout.setVisibility(View.VISIBLE);
				c_timer.start();
			}
		});

		wordTestStartTextAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				bt_tuto_hit_start_test.setVisibility(View.VISIBLE);
				bt_tuto_hit_start_test.startAnimation(btnAnim);
				Log.d("word", "111111");

				bt_tuto_hit_change_level.setVisibility(View.VISIBLE);
				bt_tuto_hit_change_level.startAnimation(btnBottomAnim);

				//				rl_tuto_hit_setting_top.getLayoutParams().height = getDpToPixel(getApplicationContext(), 228);
				//				rl_tuto_hit_setting_top.getAnimation().setFillAfter(false);
			}
		});

		hitRateAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				tv_tuto_hit_test_start_text.setVisibility(View.VISIBLE);
				tv_tuto_hit_test_start_text
						.startAnimation(wordTestStartTextAnim);
			}
		});

		masterTextAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				dv_tuto_hit_card_divider_1.setVisibility(View.VISIBLE);
				dv_tuto_hit_card_divider_1.startAnimation(dividerAnim);

				wordCountAnim.setStartOffset(125);
				wordCountAnim.setDuration(167);
				dv_tuto_hit_card_divider_2.setVisibility(View.VISIBLE);
				dv_tuto_hit_card_divider_2.startAnimation(wordCountAnim);
				ll_tuto_hit_card_word_count.setVisibility(View.VISIBLE);
				ll_tuto_hit_card_word_count.startAnimation(wordCountAnim);

				levelAnim.setStartOffset(250);
				levelAnim.setDuration(167);
				dv_tuto_hit_card_divider_3.setVisibility(View.VISIBLE);
				dv_tuto_hit_card_divider_3.startAnimation(levelAnim);
				ll_tuto_hit_card_level.setVisibility(View.VISIBLE);
				ll_tuto_hit_card_level.startAnimation(levelAnim);

				hitRateAnim.setStartOffset(375);
				hitRateAnim.setDuration(167);
				ll_tuto_hit_card_hit_rate.setVisibility(View.VISIBLE);
				ll_tuto_hit_card_hit_rate.startAnimation(hitRateAnim);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});
		halfExpandTopAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});

		levelTextFadeOutAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				iv_tuto_hit_word_card_icon.setVisibility(View.VISIBLE);
				iv_tuto_hit_word_card_icon.startAnimation(wordCardIconInAnim);

				rl_tuto_hit_setting_top.startAnimation(halfExpandTopAnim);

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						ll_tuto_hit_word_card.setVisibility(View.VISIBLE);
						ll_tuto_hit_word_card.startAnimation(wordCardInAnim);
					}
				}, 333);

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						tv_tuto_hit_card_master_text.setVisibility(View.VISIBLE);
						tv_tuto_hit_card_master_text.startAnimation(masterTextAnim);
					}
				}, 750);

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				tv_tuto_hit_setting_level.setVisibility(View.GONE);
			}
		});

		btn3OutAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				tv_tuto_hit_high.setClickable(false);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				tv_tuto_hit_high.setVisibility(View.GONE);
			}
		});

		btn2OutAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				tv_tuto_hit_mid.setClickable(false);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				tv_tuto_hit_mid.setVisibility(View.GONE);
			}
		});

		btn1OutAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				tv_tuto_hit_low.setClickable(false);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				tv_tuto_hit_low.setVisibility(View.GONE);
			}
		});

		btn4OutAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				tv_tuto_hit_master.setClickable(false);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						levelTextFadeOutAnim.setDuration(333);
						tv_tuto_hit_setting_level
								.startAnimation(levelTextFadeOutAnim);
					}
				}, 208);

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				tv_tuto_hit_master.setVisibility(View.GONE);
			}
		});

		settingTopAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				tv_tuto_hit_setting_level.setVisibility(View.VISIBLE);
				tv_tuto_hit_setting_level.startAnimation(goalTextInAnim);

				tv_tuto_hit_finish_reward.setVisibility(View.VISIBLE);
				tv_tuto_hit_finish_reward.startAnimation(goalTextInAnim);

				new Handler().postDelayed(new Runnable() {
					public void run() {
						tv_tuto_hit_low.startAnimation(btn1Anim);
						tv_tuto_hit_mid.startAnimation(btn2Anim);
						tv_tuto_hit_high.startAnimation(btn3Anim);
						tv_tuto_hit_master.startAnimation(btn4Anim);
						tv_tuto_hit_low.setVisibility(View.VISIBLE);
						tv_tuto_hit_mid.setVisibility(View.VISIBLE);
						tv_tuto_hit_high.setVisibility(View.VISIBLE);
						tv_tuto_hit_master.setVisibility(View.VISIBLE);
					};
				}, 375);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
			}
		});

		tv_tuto_hit_test_start_text.setText("단어장이 "
				+ mPreference.getString(MainValue.GpreName, "이름없음")
				+ "님의\n수준에 적절한지 간단하게 12개의 단어로\n테스트를 진행해볼게요");
		tv_tuto_hit_setting_finish_text.setText("이제 "
				+ mPreference.getString(MainValue.GpreName, "이름없음")
				+ "님만을 위해 최적화된\n수능 적중 단어장이 셋팅되었습니다.");
		tv_owner_time.setText(mPreference.getString(MainValue.GpreName, "이름없음")
				+ "님 시간");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		try{
			Class service = Class.forName("com.ihateflyingbugs.hsmd.service.ChatHeadService");
			stopService(new Intent(this, service));
		}catch(ClassNotFoundException e){}

		// 2:30을 00:00으로 하는 Date 클래스
		MildangDate m_date = new MildangDate();

		// x일 전 날짜를 구하기 위한 클래스
		DateTime dateTime = CalendarHelper
				.convertDateToDateTime(new java.util.Date());

		String tempMonth, tempDay;


		// 현재 날짜
		String current_date = m_date.get_year()+m_date.get_Month()+m_date.get_day();
		String current_time = m_date.get_hour()+m_date.get_minute()+m_date.get_second();
		Log.d("date&time", current_date + ", "+ current_time);

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
					tempMonth = dateTime.minusDays(tempInteger).getMonth()
							.toString();
				} else {
					tempMonth = "0"
							+ dateTime.minusDays(tempInteger).getMonth()
							.toString();
				}

				// 하루 전의 일을 구한다.
				if (dateTime.minusDays(tempInteger).getDay() > 9) {
					tempDay = dateTime.minusDays(tempInteger).getDay()
							.toString();
				} else {
					tempDay = "0"
							+ dateTime.minusDays(tempInteger).getDay()
							.toString();
				}

				// 하루 전의 연을 구하고 이전에 구한 월, 일과 합친다.
				String tempDate = dateTime.minusDays(tempInteger++).getYear()
						.toString()
						+ tempMonth + tempDay;

				Log.d("c_Date", saved_date + " : " + tempDate);

				// 저장된 날짜와 같으면 저장된 날짜에 덮어쓰기 전 break
				if (tempDate.equals(saved_date)) {
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

		}

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

	/**
	 * 테스트 단어장을 만들기 시작하는 부분.
	 */
	public void setTestList() {

		metrics = new DisplayMetrics();
		vibe = (Vibrator) getApplicationContext().getSystemService(
				Context.VIBRATOR_SERVICE);
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		words = new ArrayList<Word>();

		relativeWord = (RelativeLayout) findViewById(R.id.rl_tuto_hit_test_view);
		relativeWord.setVisibility(View.VISIBLE);
		listView = (ListView) findViewById(R.id.rl_tuto_hit_list);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		int topic = Integer.parseInt(mPreference.getString(
				MainActivitys.GpreTopic, "1"));

		Log.e("getword",
				"uuid = " + Get_my_uuid.get_Device_id(getApplicationContext()));

		Log.d("kjw", "create refresh !!!!!!!");
		refresh();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		mSensorManager = (SensorManager) getApplicationContext()
				.getSystemService(Context.SENSOR_SERVICE);
		mSensorListener = new ShakeEventListener();
		mSensorListener
				.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
					public void onShake() {
						if (!words.isEmpty() && adapter != null && !flag_shake) {
							// 어느 순간에 취소하고 싶다면 아래 코드 cancel() 함수를 실행하면 된다.
							vibe.cancel();
							Collections.shuffle(words);
							listView.setSelection(0);
							adapter.notifyDataSetChanged();
							isListAnimaion = true;
							// repeat은 -1 무반복
							vibe.vibrate(pattern, -1);
						} else {
							Toast.makeText(HitTutoActivity.this, "준비 안됨",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
		tts_util = new TTS_Util(getApplicationContext());
	}

	int start;
	int last;

	private void refresh() {
		words.clear();

		if (words.isEmpty()) {
			words = db.wordsWithLevel(db.getWordLevel(), 12);

		}

		// for(int i=words.size()-1; i>4;i--){
		// words.remove(i);
		// }
		isListAnimaion = true;
		printList();
	}

	private void printList() {
		listView.setVisibility(View.VISIBLE);
		timer_layout.setVisibility(View.VISIBLE);
		isListAnimaion = true;
		adapter = new ListAdapter(getApplicationContext(), R.layout.itemlist_main_word, words);
		listView.setAdapter(adapter);

	}

	private void setWordItemViewHolder(View view) {

		final int NORMAL_KNOWN_COLOR = Color.rgb(0x00, 0xb5, 0x69);
		final int EXAM_KNOWN_COLOR = Color.rgb(0xe6, 0x44, 0x2e);

		WordItemViewHolder vh = new WordItemViewHolder();
		vh.linearForward = (LinearLayout) view.findViewById(R.id.linearForward);
		vh.linearKnown = (LinearLayout) view.findViewById(R.id.linearKnown);
		vh.linearUnknown = (LinearLayout) view.findViewById(R.id.linearUnknown);

		vh.ll_known_first_mean = (LinearLayout) view
				.findViewById(R.id.ll_known_first_mean);
		vh.ll_known_second_mean = (LinearLayout) view
				.findViewById(R.id.ll_known_second_mean);
		vh.ll_known_third_mean = (LinearLayout) view
				.findViewById(R.id.ll_known_third_mean);
		vh.ll_known_forth_mean = (LinearLayout) view
				.findViewById(R.id.ll_known_forth_mean);

		vh.ll_first_mean = (LinearLayout) view.findViewById(R.id.ll_first_mean);
		vh.ll_second_mean = (LinearLayout) view
				.findViewById(R.id.ll_second_mean);
		vh.ll_third_mean = (LinearLayout) view.findViewById(R.id.ll_third_mean);
		vh.ll_forth_mean = (LinearLayout) view.findViewById(R.id.ll_forth_mean);

		vh.ivKnown = (ImageView) view.findViewById(R.id.ivKnown);
		vh.iv_wc = (ImageView) view.findViewById(R.id.iv_wc);

		vh.linearForward.setVisibility(View.VISIBLE);
		vh.tvForward = (TextView) view.findViewById(R.id.tvForward);
		vh.tvKnownWord = (TextView) view.findViewById(R.id.tvKnownWord);
		vh.tvUnknownWord = (TextView) view.findViewById(R.id.tvUnknownWord);
		vh.tvUnknownCount = (ImageView) view.findViewById(R.id.tvUnknownCount);

		vh.tv_known_first_mean_title = (TextView) view
				.findViewById(R.id.tv_known_first_mean_title);
		vh.tv_known_second_mean_title = (TextView) view
				.findViewById(R.id.tv_known_second_mean_title);
		vh.tv_known_third_mean_title = (TextView) view
				.findViewById(R.id.tv_known_third_mean_title);
		vh.tv_known_forth_mean_title = (TextView) view
				.findViewById(R.id.tv_known_forth_mean_title);

		vh.tv_known_first_mean = (TextView) view
				.findViewById(R.id.tv_known_first_mean);
		vh.tv_known_second_mean = (TextView) view
				.findViewById(R.id.tv_known_second_mean);
		vh.tv_known_third_mean = (TextView) view
				.findViewById(R.id.tv_known_third_mean);
		vh.tv_known_forth_mean = (TextView) view
				.findViewById(R.id.tv_known_forth_mean);

		vh.tv_first_mean_title = (TextView) view
				.findViewById(R.id.tv_first_mean_title);
		vh.tv_second_mean_title = (TextView) view
				.findViewById(R.id.tv_second_mean_title);
		vh.tv_third_mean_title = (TextView) view
				.findViewById(R.id.tv_third_mean_title);
		vh.tv_forth_mean_title = (TextView) view
				.findViewById(R.id.tv_forth_mean_title);

		vh.tv_first_mean = (TextView) view.findViewById(R.id.tv_first_mean);
		vh.tv_second_mean = (TextView) view.findViewById(R.id.tv_second_mean);
		vh.tv_third_mean = (TextView) view.findViewById(R.id.tv_third_mean);
		vh.tv_forth_mean = (TextView) view.findViewById(R.id.tv_forth_mean);

		vh.linearKnown.setBackgroundColor(NORMAL_KNOWN_COLOR);


		vh.iv_flip_image = (FlipImage)view.findViewById(R.id.iv_flip_image);
		vh.tvUnknownCount1 = (ImageView)view.findViewById(R.id.tvUnknownCount1);

		vh.needInflate = false;
		view.setTag(vh);
	}

	Integer del_count = 0;

	boolean flag_touch = false;
	boolean flag_shake = false;

	private class ListAdapter extends ArrayAdapter<Word> {
		LayoutInflater vi;
		private ArrayList<Word> items;
		private boolean isWrongContinueShow;
		Context mContext;

		public ListAdapter(Context context, int resourceId,
						   ArrayList<Word> items) {
			super(context, resourceId, items);
			this.items = items;
			this.vi = (LayoutInflater) getApplicationContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			isWrongContinueShow = true;

			mContext = context;

		}

		@Override
		public View getView(final int position, View convertView,
							ViewGroup parent) {

			final WordItemViewHolder vh;
			final View view;
			if (convertView == null) {
				view = vi.inflate(R.layout.word_list_row, parent, false);
				setWordItemViewHolder(view);
			} else if (((WordItemViewHolder) convertView.getTag()).needInflate) {
				view = vi.inflate(R.layout.word_list_row, parent, false);
				setWordItemViewHolder(view);
			} else {
				view = convertView;
			}
			final Word word = items.get(position);
			vh = (WordItemViewHolder) view.getTag();
			vh.tvForward.setText(word.getWord());
			vh.tvUnknownWord.setText(word.getWord());

			vh.ivKnown.setVisibility(View.GONE);


			vh.linearForward.setVisibility(View.VISIBLE);


			vh.iv_flip_image.setImage(word.getState());
			if (word.getState() > 0 && isWrongContinueShow) {
				String mStateImageString;

				if(word.getState() > 10)
					mStateImageString = "img_state_10";
				else
					mStateImageString = "img_state_" + word.getState();

				int mStateImageResourceIdInt = getResources().getIdentifier(mStateImageString, "drawable", getPackageName());

				vh.tvUnknownCount.setVisibility(View.INVISIBLE);

				if(word.getState() > 10)
					mStateImageString = "img_state_10_color";
				else
					mStateImageString = "img_state_" + word.getState() + "_color";

				mStateImageResourceIdInt = getResources().getIdentifier(mStateImageString, "drawable", getPackageName());

				vh.tvUnknownCount1.setBackgroundResource(mStateImageResourceIdInt);
			}
			else if (word.getState() < 0 && isWrongContinueShow) {
				vh.tvUnknownCount.setVisibility(View.INVISIBLE);
				vh.tvUnknownCount1.setBackgroundResource(R.drawable.img_state_forget_color);
			}
			else {
				vh.linearForward.setBackgroundColor(Color.WHITE);
				vh.tvUnknownCount.setVisibility(View.INVISIBLE);
				vh.tvUnknownCount1.setVisibility(View.INVISIBLE);
			}



			vh.linearForward.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					WordStateAnimation(vh, word, 1000, 1000);

					flag_touch = false;
					Animation animation = AnimationUtils.loadAnimation(
							getApplicationContext(), R.anim.zoom_out);
					vh.tvForward.startAnimation(animation);
					AudioManager audioManager = (AudioManager) mContext
							.getSystemService(Context.AUDIO_SERVICE);

					// listenParams.put("word", ""+word.getWord());
					// listenParams.put("time",
					// String.valueOf(System.currentTimeMillis()));
					// FlurryAgent.logEvent("튜토리얼2 단어 듣기,Tutorial_Test_Activity,0",
					// listenParams, true);

					switch (audioManager.getRingerMode()) {
						case AudioManager.RINGER_MODE_VIBRATE:
							// 진
							Toast.makeText(getApplicationContext(),
									"소리 모드로 전환후 다시 시도해주세요.", Toast.LENGTH_SHORT)
									.show();
							break;
						case AudioManager.RINGER_MODE_NORMAL:
							// 소
							if (tts_util.tts_check()) {
								tts_util.tts_reading(vh.tvForward.getText()
										.toString());
							} else {
								Toast.makeText(getApplicationContext(),
										"잠시 후에 다시 시도해주세요.", Toast.LENGTH_SHORT)
										.show();
							}
							break;
						case AudioManager.RINGER_MODE_SILENT:
							// 무
							Toast.makeText(getApplicationContext(),
									"소리 모드로 전환후 다시 시도해주세요.", Toast.LENGTH_SHORT)
									.show();
							break;
					}

				}
			});

			vh.linearUnknown.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					vh.linearForward.setVisibility(View.VISIBLE);
					Animation anim = AnimationUtils.loadAnimation(mContext,
							R.anim.slide_in_left);
					anim.setDuration(150);
					vh.linearForward.startAnimation(anim);

				}
			});

			vh.linearForward.setOnTouchListener(new SwipeDismissTouchListener(
					vh.linearForward, vh.linearUnknown, vh.linearKnown,
					vh.iv_wc, flag_set_swipe_mode, null,
					new SwipeDismissTouchListener.DismissCallbacks() {
						@Override
						public boolean canDismiss(View v,Object token) {

							if (flag_touch) {
								return false;
							}


							if (word.getMeanList().size() != 0) {

								int count = word.getMeanList().size();

								Log.d("count", "" + count);
								vh.ll_first_mean.setVisibility(View.GONE);
								vh.ll_second_mean.setVisibility(View.GONE);
								vh.ll_third_mean.setVisibility(View.GONE);
								vh.ll_forth_mean.setVisibility(View.GONE);

								vh.ll_known_first_mean.setVisibility(View.GONE);
								vh.ll_known_second_mean
										.setVisibility(View.GONE);
								vh.ll_known_third_mean.setVisibility(View.GONE);
								vh.ll_known_forth_mean.setVisibility(View.GONE);

								vh.tv_first_mean.setText("");

								vh.tv_known_first_mean.setText("");
								vh.tv_second_mean.setText("");
								vh.tv_known_second_mean.setText("");
								vh.tv_third_mean.setText("");
								vh.tv_known_third_mean.setText("");
								vh.tv_forth_mean.setText("");
								vh.tv_known_forth_mean.setText("");

								vh.tv_first_mean_title.setText("");
								vh.tv_known_first_mean_title.setText("");
								vh.tv_second_mean_title.setText("");
								vh.tv_known_second_mean_title.setText("");
								vh.tv_third_mean_title.setText("");
								vh.tv_known_third_mean_title.setText("");
								vh.tv_forth_mean_title.setText("");
								vh.tv_known_forth_mean_title.setText("");

								Mean mean;

								String M_N = "";
								String M_V = "";
								String M_A = "";
								String M_AD = "";
								String M_CONJ = "";
								Log.d("test_class", "1");
								for (int i = 0; i < word.getMeanList().size(); i++) {
									mean = word.getMean(i);
									int key = mean.getMClass();
									switch (key) {
										case Word.Class_N: // noun : 1
											M_N += mean.getMeaning() + ", ";
											// vh.ll_first_mean.setVisibility(View.VISIBLE);
											// vh.tv_first_mean.setText(vh.tv_first_mean.getText()+word.getMean(i).getMeaning()+", ");
											// vh.ll_known_first_mean.setVisibility(View.VISIBLE);
											// vh.tv_known_first_mean.setText(vh.tv_known_first_mean.getText()+word.getMean(i).getMeaning()+", ");
											break;
										case Word.Class_V: // verb : 2
											M_V += mean.getMeaning() + ", ";
											// vh.ll_second_mean.setVisibility(View.VISIBLE);
											// vh.tv_second_mean.setText(vh.tv_second_mean.getText()+word.getMean(i).getMeaning()+", ");
											// vh.ll_known_second_mean.setVisibility(View.VISIBLE);
											// vh.tv_known_second_mean.setText(vh.tv_known_second_mean.getText()+word.getMean(i).getMeaning()+", ");
											break;
										case Word.Class_A: // adjective : 3
											M_A += mean.getMeaning() + ", ";
											// vh.ll_third_mean.setVisibility(View.VISIBLE);
											// vh.tv_third_mean.setText(vh.tv_third_mean.getText()+word.getMean(i).getMeaning()+", ");
											// vh.ll_known_third_mean.setVisibility(View.VISIBLE);
											// vh.tv_known_third_mean.setText(vh.tv_known_third_mean.getText()+word.getMean(i).getMeaning()+", ");
											break;
										case Word.Class_Ad: // adverb : 4
											M_AD += mean.getMeaning() + ", ";
											// vh.ll_third_mean.setVisibility(View.VISIBLE);
											// vh.tv_third_mean.setText(vh.tv_third_mean.getText()+word.getMean(i).getMeaning()+", ");
											// vh.ll_known_third_mean.setVisibility(View.VISIBLE);
											// vh.tv_known_third_mean.setText(vh.tv_known_third_mean.getText()+word.getMean(i).getMeaning()+", ");
											break;

										case Word.Class_Conj: // conjunction : 5
											M_CONJ += mean.getMeaning() + ", ";
											// vh.ll_forth_mean.setVisibility(View.VISIBLE);
											// vh.tv_forth_mean.setText(vh.tv_forth_mean.getText()+word.getMean(i).getMeaning()+", ");
											// vh.ll_known_forth_mean.setVisibility(View.VISIBLE);
											// vh.tv_known_forth_mean.setText(vh.tv_known_forth_mean.getText()+word.getMean(i).getMeaning()+", ");

											break;
										default:
											break;
									}
								}
								Log.d("test_class", "2");
								Log.d("test_class", "2  " + word.getP_class());

								switch (word.getP_class()) {
									case Word.Class_N:
										vh.ll_first_mean
												.setVisibility(View.VISIBLE);
										vh.tv_first_mean_title.setText("n");
										vh.tv_first_mean.setText(M_N.substring(0,
												M_N.length() - 2));
										vh.ll_known_first_mean
												.setVisibility(View.VISIBLE);
										vh.tv_known_first_mean_title.setText("n");

										vh.tv_known_first_mean.setText(M_N
												.substring(0, M_N.length() - 2));
										// vh.tv_known_first_mean.setText(chosung.hangulToJaso(M_N.substring(0,
										// M_N.length()-2)));

										Log.d("test_class", "22222222222222");
										break;
									case Word.Class_V:
										vh.ll_first_mean
												.setVisibility(View.VISIBLE);
										vh.tv_first_mean_title.setText("v");
										vh.tv_first_mean.setText(M_V.substring(0,
												M_V.length() - 2));
										vh.ll_known_first_mean
												.setVisibility(View.VISIBLE);
										vh.tv_known_first_mean_title.setText("v");

										vh.tv_known_first_mean.setText(M_V
												.substring(0, M_V.length() - 2));
										// vh.tv_known_first_mean.setText(chosung.hangulToJaso(M_V.substring(0,
										// M_V.length()-2)));

										break;
									case Word.Class_A:
										vh.ll_first_mean
												.setVisibility(View.VISIBLE);
										vh.tv_first_mean_title.setText("a");
										vh.tv_first_mean.setText(M_A.substring(0,
												M_A.length() - 2));
										vh.ll_known_first_mean
												.setVisibility(View.VISIBLE);
										vh.tv_known_first_mean_title.setText("a");
										vh.tv_known_first_mean.setText(M_A
												.substring(0, M_A.length() - 2));
										// vh.tv_known_first_mean.setText(chosung.hangulToJaso(M_A.substring(0,
										// M_A.length()-2)));

										break;
									case Word.Class_Ad:
										vh.ll_first_mean
												.setVisibility(View.VISIBLE);
										vh.tv_first_mean_title.setText("ad");
										vh.tv_first_mean.setText(M_AD.substring(0,
												M_AD.length() - 2));
										vh.ll_known_first_mean
												.setVisibility(View.VISIBLE);
										vh.tv_known_first_mean_title.setText("ad");

										vh.tv_known_first_mean.setText(M_AD
												.substring(0, M_AD.length() - 2));
										// vh.tv_known_first_mean.setText(chosung.hangulToJaso(M_AD.substring(0,
										// M_AD.length()-2)));

										break;
									case Word.Class_Conj:
										vh.ll_first_mean
												.setVisibility(View.VISIBLE);
										vh.tv_first_mean_title.setText("conj");
										vh.tv_first_mean.setText(M_CONJ.substring(
												0, M_CONJ.length() - 2));
										vh.ll_known_first_mean
												.setVisibility(View.VISIBLE);
										vh.tv_known_first_mean_title
												.setText("conj");
										vh.tv_known_first_mean.setText(M_CONJ
												.substring(0, M_CONJ.length() - 2));
										// vh.tv_known_first_mean.setText(chosung.hangulToJaso(M_PREP.substring(0,
										// M_PREP.length()-2)));

										break;
								}
								Log.d("test_class", "3");

								HashMap<Integer, Boolean> hm = word
										.getmClassList();
								hm.remove(word.getP_class());
								Set<Integer> st = hm.keySet();
								Iterator<Integer> it = st.iterator();
								int line = 0;

								while (it.hasNext()) {
									int key = it.next();
									String multi_mean = "";
									String mclass = "";
									switch (key) {
										case Word.Class_A:
											mclass = "a";
											multi_mean = M_A;
											break;
										case Word.Class_Ad:
											mclass = "ad";
											multi_mean = M_AD;
											break;
										case Word.Class_N:
											mclass = "n";
											multi_mean = M_N;
											break;
										case Word.Class_V:
											mclass = "v";
											multi_mean = M_V;
											break;
										case Word.Class_Conj:
											mclass = "conj";
											multi_mean = M_CONJ;
											break;
									}

									if (line == 0) {
										vh.ll_second_mean
												.setVisibility(View.VISIBLE);
										vh.tv_second_mean_title.setText(mclass);
										vh.tv_second_mean.setText(multi_mean
												.substring(0,
														multi_mean.length() - 2));
										vh.ll_known_second_mean
												.setVisibility(View.VISIBLE);
										vh.tv_known_second_mean_title
												.setText(mclass);
										vh.tv_known_second_mean.setText(multi_mean
												.substring(0,
														multi_mean.length() - 2));
										// vh.tv_known_second_mean.setText(chosung.hangulToJaso(multi_mean.substring(0,multi_mean.length()-2)));

										mclass = "";
										multi_mean = "";
										line++;
									} else if (line == 1) {
										vh.ll_third_mean
												.setVisibility(View.VISIBLE);
										vh.tv_third_mean_title.setText(mclass);
										vh.tv_third_mean.setText(multi_mean
												.substring(0,
														multi_mean.length() - 2));
										vh.ll_known_third_mean
												.setVisibility(View.VISIBLE);
										vh.tv_known_third_mean_title
												.setText(mclass);
										vh.tv_known_third_mean.setText(multi_mean
												.substring(0,
														multi_mean.length() - 2));
										// vh.tv_known_third_mean.setText(chosung.hangulToJaso(multi_mean.substring(0,multi_mean.length()-2)));

										mclass = "";
										multi_mean = "";
									}
								}

								Log.d("test_class", "4");

								int text_sp = 0;

								if (word.getmClassList().size() > 1) {
									text_sp = 15;
								} else if (word.getmClassList().size() == 1) {
									if (vh.tv_first_mean.length() > 15
											|| vh.tv_second_mean.length() > 15) {
										// 포문으로 글씨를 계속 줄여본다
										text_sp = 15;
									} else {
										text_sp = 17;
									}
								} else {
									text_sp = 17;
								}

								vh.tv_first_mean_title.setTextSize(
										TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_second_mean_title.setTextSize(
										TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_third_mean_title.setTextSize(
										TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_forth_mean_title.setTextSize(
										TypedValue.COMPLEX_UNIT_SP, text_sp);

								vh.tv_first_mean.setTextSize(
										TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_second_mean.setTextSize(
										TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_third_mean.setTextSize(
										TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_forth_mean.setTextSize(
										TypedValue.COMPLEX_UNIT_SP, text_sp);

								vh.tv_known_first_mean_title.setTextSize(
										TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_known_second_mean_title.setTextSize(
										TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_known_third_mean_title.setTextSize(
										TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_known_forth_mean_title.setTextSize(
										TypedValue.COMPLEX_UNIT_SP, text_sp);

								vh.tv_known_first_mean.setTextSize(
										TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_known_second_mean.setTextSize(
										TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_known_third_mean.setTextSize(
										TypedValue.COMPLEX_UNIT_SP, text_sp);
								vh.tv_known_forth_mean.setTextSize(
										TypedValue.COMPLEX_UNIT_SP, text_sp);

								if (vh.tv_first_mean_title.getText().toString()
										.equals("conj")
										|| vh.tv_first_mean_title.getText()
										.toString().equals("conj")) {
									vh.tv_first_mean_title.setTextSize(
											TypedValue.COMPLEX_UNIT_SP, 11);
									vh.tv_known_first_mean_title.setTextSize(
											TypedValue.COMPLEX_UNIT_SP, 11);
								} else if (vh.tv_second_mean_title.getText()
										.toString().equals("conj")
										|| vh.tv_second_mean_title.getText()
										.toString().equals("conj")) {
									vh.tv_second_mean_title.setTextSize(
											TypedValue.COMPLEX_UNIT_SP, 11);
									vh.tv_known_second_mean_title.setTextSize(
											TypedValue.COMPLEX_UNIT_SP, 11);
								} else if (vh.tv_third_mean_title.getText()
										.toString().equals("conj")
										|| vh.tv_third_mean_title.getText()
										.toString().equals("conj")) {
									vh.tv_third_mean_title.setTextSize(
											TypedValue.COMPLEX_UNIT_SP, 11);
									vh.tv_known_third_mean_title.setTextSize(
											TypedValue.COMPLEX_UNIT_SP, 11);
								}

								Log.d("test_class", "5");
							} else {
								vh.tv_known_first_mean.setTextSize(
										TypedValue.COMPLEX_UNIT_SP, 17);
								vh.tv_known_first_mean.setText("없음");
								vh.ll_known_second_mean
										.setVisibility(View.GONE);
								vh.ll_known_third_mean.setVisibility(View.GONE);
								vh.ll_known_forth_mean.setVisibility(View.GONE);

								vh.tv_first_mean.setTextSize(
										TypedValue.COMPLEX_UNIT_SP, 17);
								vh.tv_first_mean.setText("없음");
								vh.ll_second_mean.setVisibility(View.GONE);
								vh.ll_third_mean.setVisibility(View.GONE);
								vh.ll_forth_mean.setVisibility(View.GONE);
							}

							Log.d("test_class", "6");
							flag_touch = true;
							return true;
						}

						@Override
						public void onLeftDismiss(View v, Object token,
												  boolean flag) {
							// TODO Auto-generated method stub
							int ex_state = items.get(position).getState();
							boolean isKnown = false;

							flag_touch = false;
							vh.linearForward.setVisibility(View.GONE);
							if (!isWrongContinueShow) {
								deleteCell(view, word);
								// words.remove(position);
							}
							word.increaseWrongCount();

							if (!word.isWrong()) {

								word.setWrong(true);
								word.setRight(false);
								db.updateRightWrong(false, word.get_id());
							}

							db.updateForgettingCurvesByNewInputs(
									items.get(position),  Config.MAINWORDBOOK, isKnown);

							// Config.Difficulty = db.calcLevel(10);
							Word word_for_write = db.getWord(items
									.get(position).get_id());

							word.setState(word_for_write.getState());

							if (word.getWrongCount() != 0
									&& isWrongContinueShow) {
								// vh.tvUnknownCount.setText(items.get(position).getWrongCount()
								// + "");
								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										vh.tvUnknownCount
												.setVisibility(View.VISIBLE);
										vh.tvUnknownCount
												.setBackgroundResource(R.drawable.main_cell_forget);
									}
								});
							}

							if (!word.isWrong()) {
								// ONCE_WORD_COUNT--;
								word.setWrong(true);
								word.setRight(false);
								db.updateRightWrong(false, word.get_id());
							}

							db.insertLevel(word, isKnown);
						}


						@Override
						public void onRightDismiss(final View v, Object token,
												   boolean flag) {
							// TODO Auto-generated method stub

							// 사라질 뭔가 다른방법으로 사라져야됨
							// 예를들면 스택에 싸아놨다가 하나씩 없애는 방법으로
							final Word del_word = word;
							flag_touch = false;

							// onRightParams.put("word", ""+word.getWord());
							// onRightParams.put("time",
							// String.valueOf(System.currentTimeMillis()));
							// FlurryAgent.logEvent("튜토리얼2 onRightDismiss,Tutorial_Test_Activity,0",
							// onRightParams);

							vh.linearForward.setVisibility(View.GONE);

							// int ex_state = items.get(position).getState();
							boolean isKnown = true;
							Log.e("swipe",
									"before   isWrong : "
											+ String.valueOf(word.isWrong())
											+ "isRight : "
											+ String.valueOf(word.isRight()));

							if (!del_word.isRight()) {

								del_word.setRight(true);
								del_word.setWrong(false);
								// userDb.deleteCurrentWord(word.get_id());

							}


							items.get(position).setState(-1);

							try {
								db.insertState0FlagTableElement(
										items.get(position), isKnown);
								// update forgetting curves by 'isknown' of word
								db.updateForgettingCurvesByNewInputs(word,  Config.MAINWORDBOOK, isKnown);
								// insert review time to get value of memeory
								// coach ment
								db.insertReviewTimeToGetMemoryCoachMent(word);
								db.insertLevel(word, isKnown);
								// db.updateWordInfo(word, isKnown);

							} catch (Exception e) {
								// TODO: handle exception
								adapter.notifyDataSetChanged();
							}


							deleteCell(view, del_word);

						}

						@Override
						public void onLeftMovement(View v) {
							// TODO Auto-generated method stub
							Config.unknow_count++;

							flag_touch = false;

							int ex_state = items.get(position).getState();
							items.get(position).increaseWrongCount();
							boolean isKnown = false;
							if (!word.isWrong()) {
								ONCE_WORD_COUNT--;
								word.setWrong(true);
								word.setRight(false);
								db.updateRightWrong(false, word.get_id());
							}

							// insert state0 flag table element
							db.insertState0FlagTableElement(
									items.get(position), isKnown);
							// update forgetting curves by 'isknown' of word
							db.updateForgettingCurvesByNewInputs(
									items.get(position),  Config.MAINWORDBOOK,  isKnown);
							// insert review time to get value of memeory coach
							// ment
							db.insertReviewTimeToGetMemoryCoachMent(word);
							// db.updateWordInfo(items.get(position), isKnown);
							db.insertLevel(items.get(position), isKnown);

							// Config.Difficulty =
							// db.calcLevel(Config.CHANGE_LEVEL_COUNT);

							//	word.setState(-1);

							items.get(position).setState(-1);
							vh.iv_flip_image.setImage(word.getState());
							vh.iv_flip_image.setVisibility(View.VISIBLE);

							if (word.getWrongCount() != 0 && isWrongContinueShow) {
								vh.tvUnknownCount.setVisibility(View.INVISIBLE);

								vh.tvUnknownCount1.setVisibility(View.VISIBLE);
								vh.tvUnknownCount1.setBackgroundResource(R.drawable.img_state_forget_color);
							}

							WordForgetAnimation(vh, word, 500);

						}

						@Override
						public void onRightMovement() {
							// TODO Auto-generated method stub
							flag_touch = false;
							vh.iv_flip_image.flip(moved_percent);

						}


						@Override
						public void showFlipAnimation(boolean Direction,
													  float deltaX) {
							// TODO Auto-generated method stub

							if(!Direction){
								float new_percent = Math.abs(deltaX)/(view.getWidth()/8);
								moved_percent = new_percent;
								Log.i("flip_percent", ""+new_percent);

								if(mStateAnim != null) {
									Log.e("KARAM", "FlipAnimation");
									vh.tvUnknownCount1.clearAnimation();
									vh.tvUnknownCount1.animate().cancel();
									vh.tvUnknownCount1.setVisibility(View.GONE);
									vh.iv_flip_image.setVisibility(View.VISIBLE);
									mStateAnim = null;
								}

								if(mForgetAnim != null) {
									vh.tvUnknownCount1.clearAnimation();
									vh.tvUnknownCount1.animate().cancel();
									vh.tvUnknownCount1.setVisibility(View.GONE);
									vh.iv_flip_image.setVisibility(View.VISIBLE);
									mForgetAnim = null;
								}
								if(new_percent<2){
									vh.iv_flip_image.flip_ani(new_percent);
								}else{
									vh.iv_flip_image.flip_ani(2);
								}
							}

						}
					}));
			Log.e("getcount",
					String.valueOf(start) + "   " + String.valueOf(last) + "  "
							+ String.valueOf(position));
			if (isListAnimaion) {
				if (position < 8) {
					Animation animation = null;
					animation = new TranslateAnimation(metrics.widthPixels / 2,
							0, 0, 0);
					animation.setDuration((position * 20) + 800);
					view.startAnimation(animation);


					WordStateAnimation(vh, word, 1000, 1500);
				} else {
					isListAnimaion = false;
				}
			}


			return view;
		}

	}


	float moved_percent;


	public int get_position(String line1, String line2, String line3) {

		int position = 0;
		int min = line1.length();
		String[] line = { line1, line2, line3 };
		for (int i = 0; i < line.length; i++) {
			if (line[i].length() < min) {
				min = line[i].length();
				position = i;
			}
		}

		return position;
	}

	public int get_position(String line1, String line2) {
		int position = 0;
		int min = line1.length();
		String[] line = { line1, line2 };
		for (int i = 0; i < line.length; i++) {
			if (line[i].length() < min) {
				min = line[i].length();
				position = i;
			}
		}
		return position;
	}

	public String Word_split(String means) {

		// 결과값
		String result_mean = "";

		// 단어 쪼갠것
		String[] mean = means.split(", ");

		// 각 라인
		String[] line = { "", "", "" };

		// 단어의 총길이.

		int length = means.length();
		for (int i = 0; i < mean.length - 1; i++) {
			for (int j = 0; j < mean.length - 1 - i; j++) {
				if (mean[j].length() > mean[j + 1].length()) {
					String tmp = mean[j];
					mean[j] = mean[j + 1];
					mean[j + 1] = tmp;
				}
			}
		}

		if (mean.length == 1) {
			result_mean = means;
		} else if (length <= 10) {
			result_mean = means;
		} else if (length > 10 && length <= 20) {
			for (int i = mean.length - 1; i > -1; i--) {
				line[get_position(line[0], line[1])] += mean[i] + ", ";
			}

			result_mean = line[0].substring(0, line[0].length() - 2) + "\n"
					+ line[1].substring(0, line[1].length() - 2);
		} else if (length > 20) {
			for (int i = mean.length - 1; i > -1; i--) {
				line[get_position(line[0], line[1], line[2])] += mean[i] + ", ";
			}

			result_mean = line[0].substring(0, line[0].length() - 2) + "\n"
					+ line[1].substring(0, line[1].length() - 2) + "\n"
					+ line[2].substring(0, line[2].length() - 2);
		}

		return result_mean;
	}

	public static String get_date() {
		String result = "";
		Calendar cal = Calendar.getInstance();

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);

		return result = year + "-" + month + "-" + day + " " + hour + ":"
				+ minute;
	}

	Animation mStateAnim;
	Animation mForgetAnim;

	void WordForgetAnimation(WordItemViewHolder viewholder, final Word word, long duration) {
		final WordItemViewHolder vh = viewholder;

//		mForgetAnim = new ScaleAnimation(0.85f, 1f, 0.85f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		mForgetAnim.setDuration(duration);
//		mForgetAnim.setInterpolator(new DecelerateInterpolator());


		mForgetAnim = AnimationUtils.loadAnimation(this, R.anim.anim_forget);

		vh.tvUnknownCount1.startAnimation(mForgetAnim);

		mForgetAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				vh.tvUnknownCount.setVisibility(View.INVISIBLE);
				vh.iv_flip_image.setVisibility(View.GONE);
				vh.tvUnknownCount1.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				WordStateAnimation(vh, word, 1000, 200);
			}
		});
	}

	void WordStateAnimation(WordItemViewHolder viewholder, Word word, long duration, long starttime) {
		final WordItemViewHolder vh = viewholder;

		mStateAnim = new AlphaAnimation(1.0f, 0.0f);

		mStateAnim.setDuration(duration);
		mStateAnim.setStartOffset(starttime);

		final int mWordstate = word.getState();
		if(mWordstate != 0)
			vh.tvUnknownCount1.startAnimation(mStateAnim);


		mStateAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				vh.tvUnknownCount.setVisibility(View.INVISIBLE);
				vh.iv_flip_image.setVisibility(View.VISIBLE);
				vh.tvUnknownCount1.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				vh.tvUnknownCount1.setVisibility(View.GONE);
			}
		});
	}



	synchronized private void deleteCell(final View v, final Word word) {
		AnimationListener al = new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation arg0) {

				// TODO Auto-generated method stub

				db.insertStudyWord(db.getStudyWord() + 1);

				WordItemViewHolder vh = (WordItemViewHolder) v.getTag();

				vh.needInflate = true;
				isListAnimaion = false;

				words.remove(word);

				flag_shake = true;
				flag_touch = false;

				adapter.remove(word);

				adapter.notifyDataSetChanged();

				if (adapter.getCount() == 0) {
					afterTime = new java.util.Date();
					// Log.e("hittuto", "adapter.getCount()==0");
					// new
					// AlertDialog.Builder(HitTutoActivity.this).setMessage("이제 밀당 영단어의 모든 기능을 마스터하셨습니다")
					// .setPositiveButton("확인", new
					// DialogInterface.OnClickListener() {
					//
					// @Override
					// public void onClick(DialogInterface dialog, int which) {
					// // TODO Auto-generated method stub
					// // adapter.notifyDataSetChanged();
					//
					// // TODO Auto-generated method stub
					adapter.clear();
					listView.setVisibility(View.GONE);
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							rl_tuto_hit_wordlist_top
									.startAnimation(wordListTopOutAnim);
							tv_tuto_hit_wordlist_text
									.startAnimation(wordListTextOutAnim);
							tv_tuto_hit_wordlist_time
									.startAnimation(wordListTimeOutAnim);
						}
					}, 1500);

					// // vh.linearForward.setEnabled(true);
					// }
					//
					// // vh.linearForward.setEnabled(true);
					// }).show();

				}
			};

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

		};

		collapse(v, al);
	}

	synchronized private void collapse(final View v, AnimationListener al) {
		final int initialHeight = v.getHeight();
		// Log.v("kjw", "real initialHeight = " + initialHeight);
		final Animation anim = new Animation() {

			@Override
			protected void applyTransformation(float interpolatedTime,
											   Transformation t) {
				// Log.v("kjw", "real interpolatedTime = " + interpolatedTime);
				if (interpolatedTime == 1) {
					v.setVisibility(View.GONE);
				} else {
					// v.getLayoutParams().height = initialHeight;// -
					// (int)(initialHeight * interpolatedTime);
					v.getLayoutParams().height = initialHeight
							- (int) (initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};
		if (al != null) {
			anim.setAnimationListener(al);
		}
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				anim.setDuration(ANIMATION_DURATION);
				v.startAnimation(anim);
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
		FlurryAgent.logEvent("HitTutoActivity", articleParams);

		// your code
	}

	public void onStop() {
		super.onStop();

		pause(c_timer);


		FlurryAgent.endTimedEvent("HitTutoActivity");
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

	boolean clickflag = false;
	boolean backFlag = false;
	private int mSlop;

	private TrayAnimationTimerTask trayTimerTask;

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

					mRootLayoutParams.width = (x + 40);
					mRootLayoutParams.height = (y);

					if (mRootLayoutParams.width < 200) {
						mRootLayoutParams.width = 200;
					}
					if (mRootLayoutParams.height < 200) {
						mRootLayoutParams.height = 200;
					}
					hit_tuto_layout.updateViewLayout(timer_layout,
							mRootLayoutParams);
					backFlag = true;
				} else {
					backFlag = false;
				}

		}

		return clickflag;
	}

	private static final int TRAY_HIDDEN_FRACTION = 7;
	private static final int TRAY_MOVEMENT_REGION_FRACTION = 7;
	private Handler timerAnimationHandler = new Handler();
	private Timer timerAnimationTimer;
	int width;
	protected int timer_sec;
	protected int use_min;
	protected int use_sec;
	protected int temp_min;
	protected String use_sec_st;
	protected String use_min_st;
	protected float percentage;
	protected int percentage_lv;

	static public boolean restart_flag = true;

	@Override
	protected void onRestart() {
		super.onRestart();

		if (restart_flag) {
			c_timer.start();
		}
	}

	public CountDownTimer timer(final PercentView percentView,
								final String position) {

		// time초 동안 time/100초 마다 onTick 실행
		CountDownTimer cTimer = new CountDownTimer(86400000, 1000) {

			// (time * 1000) / 100마다 실행
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub

				mPreference = getSharedPreferences(MainActivitys.preName,
						MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE
								| MODE_MULTI_PROCESS);

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
					mPreference.edit().putBoolean(MainValue.GpreErrorSQLSend, true).commit();
					// csv파일을 업로드한다.

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

				min_tv.setText(use_min_st);
				sec_tv.setText(use_sec_st);

				// millisecond 단위 총 사용 시간
				float use_time = (use_min * 60000) + use_sec * 1000;

				// 목표시간 달성률 계산
				try {
					percentage_lv = ((int) use_time) / (timer_sec * 60000);

					percentage = ((use_time - ((timer_sec * 60000) * percentage_lv))
							/ ((timer_sec) * 60000) * 100);


				} catch (ArithmeticException e) {
					// TODO: handle exception
					percentage_lv = ((int) use_time) / (10 * 60000);

					percentage = ((use_time - ((10 * 60000) * percentage_lv))
							/ ((10) * 60000) * 100);
				}

				if (percentage >= 100 && percentage_lv < 6) {
					percentage_lv++;
				}

				// 목표시간 타이머 그래프
				percent_view.setPercentage(percentage, percentage_lv, true);
				editor = mPreference.edit();
				editor.putFloat(MainValue.GpreUseMin, use_min);
				if(!editor.commit()){
					Toast.makeText(getApplicationContext(), "Commit Bug : GpreUseMin",
							Toast.LENGTH_SHORT).show();
				}
				editor.putFloat(MainValue.GpreUseSec, use_sec);
				if(!editor.commit()){
					Toast.makeText(getApplicationContext(), "Commit Bug : GpreUseSec",
							Toast.LENGTH_SHORT).show();
				}
				// 사용시간 db에 저장
				db.insertTime(use_min, use_sec);
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
							- timer_layout.getWidth(), mDestY);
		}

		@Override
		public void run() {
			timerAnimationHandler.post(new Runnable() {
				@Override
				public void run() {
					if (width / 2 > 1) {
						mRootLayoutParams.width = (2 * (mRootLayoutParams.width - mDestX))
								/ 3 + mDestX;

						hit_tuto_layout.updateViewLayout(timer_layout,
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
							hit_tuto_layout.updateViewLayout(timer_layout,
									mRootLayoutParams);
						}

					}
				}
			});
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
}
