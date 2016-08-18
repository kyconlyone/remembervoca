package com.ihateflyingbugs.hsmd.animtuto;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.internal.LoadingLayout.OnNextClickListener;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.ShakeEventListener;
import com.ihateflyingbugs.hsmd.SwipeDismissTouchListener;
import com.ihateflyingbugs.hsmd.TTS_Util;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.data.Mean;
import com.ihateflyingbugs.hsmd.data.Word;
import com.ihateflyingbugs.hsmd.data.WordItemViewHolder;
import com.ihateflyingbugs.hsmd.flipimageview.FlipImage;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.service.Utils;

import org.andlib.ui.CircleAnimationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MemoryTutoActivity extends Activity implements OnScrollListener{
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 13579) {
			if (resultCode == 97531) {
				if (data.getBooleanExtra("start", false)) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							rl_tuto_memroy_wordlist.setVisibility(View.VISIBLE);
							ll_tutorial_notice.setVisibility(View.VISIBLE);
							tv_tuto_memory_finish_reward_wordlist.setVisibility(View.VISIBLE);
							tv_tutorial_title.setVisibility(View.VISIBLE);
							ll_tutorial_notice.startAnimation(tutorial_top_in);
							tv_tuto_memory_finish_reward_wordlist.startAnimation(tutorial_text_in);
							tv_tutorial_title.startAnimation(tutorial_text_in);

							Date_Step1 = date.get_currentTime();
							Time_Step1 = String.valueOf(System.currentTimeMillis());
						}
					}, 583);
					Log.e("setupWordList", "start");

					setTestWordlist();
				}else {
					setResult(4683, new Intent());
					finish();
					overridePendingTransition(android.R.anim.fade_in, R.anim.slide_down_bottom);
				}
			}
		}
	}


	////////////////////////////////
	// 첫번째 페이지 레이아웃
	//
	//
	RelativeLayout rl_tuto_memory_setting_top;
	TextView tv_tuto_memory_finish_reward;
	TextView tv_tuto_memory_teach_text;
	ImageView iv_tuto_memory_learnpage;
	CircleAnimationView bt_tuto_memory_learn;
	////////////////////////////////
	// 튜토리얼 페이지 레이아웃
	//
	//
	TextView tv_tuto_memory_right_push_text;
	TextView tv_tuto_memory_tts_text;
	TextView tv_tuto_memory_left_push_text;
	//	ListView lv_tuto_memory_tutorial;
	ImageView iv_tuto_memory_mean_guide;
	ImageView iv_tuto_memory_known_guide;
	ImageView iv_tuto_memory_mean_hand;
	ImageView iv_tuto_memory_tts_hand;
	ImageView iv_tuto_memory_known_hand;
	////////////////////////////////
	// 세번째 페이지 레이아웃
	//
	//
	ImageView iv_tuto_memory_question_mark;
	TextView tv_tuto_memory_when_text;
	View bg_gray;
	////////////////////////////////
	// 네번째 페이지 레이아웃
	//
	//
	TextView tv_tuto_memory_3_hour_text;
	TextView tv_tuto_memory_7_hour_text;
	LinearLayout ll_tuto_memory_card_3;
	ImageView iv_tuto_memory_3_cover;
	LinearLayout ll_tuto_memory_card_7;
	ImageView iv_tuto_memory_7_cover;
	TextView tv_card_3_time;
	TextView tv_card_7_time;
	////////////////////////////////
	// 다섯번째 페이지 레이아웃
	//
	//
	TextView tv_tuto_memory_user_memory_text;
	RelativeLayout rl_tuto_memory_kakao_profile;
	LinearLayout ll_tuto_memory_question_clock;
	View bg_yellow;
	////////////////////////////////
	// 여섯번째 페이지 레이아웃
	//
	//
	TextView tv_tuto_memory_curve_text;
	ImageView iv_tuto_memory_wide_curve;
	ImageView iv_tuto_memory_curve_zoom_lens;
	CircleAnimationView bt_tuto_memory_quest_finish;
	////////////////////////////////
	// 보상 페이지 레이아웃
	//
	//
	TextView tv_tuto_memory_goal_setting_finish;
	ImageView iv_tuto_memory_head_icon;
	TextView tv_tuto_memory_reward_text;
	TextView tv_tuto_memory_finish_reward_after;
	CircleAnimationView bt_tuto_memory_quest_finish_reward;

	////////////////////////////////
	// 첫번째 페이지 애니메이션
	//
	//
	Animation settingTopInAnim;
	Animation goalTextInAnim;
	Animation learnpageInAnim;
	Animation btnInAnim;
	Animation learnpageOutAnim;
	Animation btnOutAnim;
	Animation settingTopOutAnim;
	Animation goalTextOutAnim;

	////////////////////////////////
	// 튜토리얼 페이지 애니메이션
	//
	//
	Animation tutorial_top_in;
	Animation tutorial_text_in;
	Animation listview_in;
	Animation hand_in;
	Animation arrow_in;
	Animation tutorial_text_fade_out;
	Animation tutorial_text_out;
	Animation tutorial_top_out;
	////////////////////////////////
	// 세번째 페이지 애니메이션
	//
	//
	Animation question_icon_in;
	Animation when_text_in;
	Animation question_icon_out;
	Animation when_text_out;
	Animation gray_gradient_in;
	Animation gray_gradient_out;
	////////////////////////////////
	// 네번째 페이지 애니메이션
	//
	//
	Animation card_3_hour_card_in;
	Animation card_7_hour_card_in;
	Animation card_3_hour_card_cover;
	Animation card_3_hour_text_in;
	Animation card_3_hour_card_out;
	Animation card_3_hour_text_out;
	Animation card_7_hour_text_in;
	Animation card_7_hour_card_cover;
	Animation card_7_hour_card_out;
	Animation card_7_hour_text_out;
	////////////////////////////////
	// 다섯번째 페이지 애니메이션
	//
	//
	Animation user_memory_text_in;
	Animation user_memory_profile_in;
	Animation user_memory_clock_in;
	Animation user_memory_text_out;
	Animation user_memory_profile_out;
	Animation user_memory_clock_out;
	////////////////////////////////
	// 여섯번째 페이지 애니메이션
	//
	//
	Animation user_memory_profile_move;
	Animation user_memory_wide_curve;
	Animation curve_text_in;
	Animation curve_btn_in;
	Animation zoom_lens_in;
	Animation yellow_gradient_in;
	Animation curve_text_out;
	Animation curve_out;
	////////////////////////////////
	// 보상 페이지 애니메이션
	//
	//
	Animation expand_memory_top;
	Animation finish_reward_text_in;
	Animation set_finish_text_in;
	Animation reward_text_anim;
	Animation head_icon_in;
	Animation get_reward_btn_in;
	////////////////////////////////
	// 단어장부분
	//
	//

	RelativeLayout rl_tuto_memroy_wordlist;
	LinearLayout ll_tutorial_notice;

	private ListAdapter adapter;
	private ListView listView;
	private PullToRefreshListView mPullToRefreshListView;

	static final int ANIMATION_DURATION = 400;
	private boolean isListAnimaion = true;
	TTS_Util tts_util;
	private boolean flag_set_swipe_mode = true;
	private DisplayMetrics metrics;
	private static ArrayList<Word> words;
	private ImageView ivTemp;
	static Vibrator vibe;
	private DBPool db;
	private SensorManager mSensorManager;
	private ShakeEventListener mSensorListener;
	private RelativeLayout relativeWord;
	TextView tv_tutorial_title;
	ImageView iv_navi2;
	ImageView iv_tutorial_right;
	ImageView iv_navi3;
	TextView tv_tuto_memory_finish_reward_wordlist;

	Animation fadeInAnimation;
	Animation fadeOutAnimation;
	long[] pattern = { 0, 100, 50, 100 };
	SharedPreferences mPreference;
	boolean isStep[] ={true, false, false, false, false, false};
	boolean isTopAnimStep[] ={true, false, false, false, false, false};

	Handler handler ;

	Animation anim[]= new Animation[9];
	ImageView iv_nav4;

	int ONCE_WORD_COUNT = Config.ONCE_WORD_COUNT;

	//log parameter
	Map<String, String> tutorial1Params = new HashMap<String, String>();
	Map<String, String> step0Params = new HashMap<String, String>();
	Map<String, String> step1Params = new HashMap<String, String>();
	Map<String, String> step2Params = new HashMap<String, String>();
	String step0start;
	String step1start;
	String step2start;

	String starttime;
	String startdate;
	Date date = new Date();
	Map<String, String> articleParams ;

	Animation anim_slideinTop;
	Animation anim_fadeOut;
	Animation anim_fadeOut2;

	ImageView iv_tuto_memory_kakao_pic;

	private AudioManager audio;

	Handler card7_cover_handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			tv_card_7_time.setText("0" + msg.what + ":00");
		}
	};
	Handler card3_cover_handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			tv_card_3_time.setText("0" + msg.what + ":00");
		}
	};



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
			setContentView(R.layout.activity_memory_tuto_small);
		}else{
			setContentView(R.layout.activity_memory_tuto);
		}

		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		handler = new Handler();

		tts_util = new TTS_Util(getApplicationContext());

		mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);

		rl_tuto_memroy_wordlist = (RelativeLayout) findViewById(R.id.rl_tuto_memroy_wordlist);

		iv_tuto_memory_kakao_pic = (ImageView) findViewById(R.id.iv_tuto_memory_kakao_pic);
		rl_tuto_memory_setting_top = (RelativeLayout) findViewById(R.id.rl_tuto_memory_setting_top);
		tv_tuto_memory_finish_reward = (TextView) findViewById(R.id.tv_tuto_memory_finish_reward);
		tv_tuto_memory_teach_text = (TextView) findViewById(R.id.tv_tuto_memory_teach_text);
		iv_tuto_memory_learnpage = (ImageView) findViewById(R.id.iv_tuto_memory_learnpage);
		bt_tuto_memory_learn = (CircleAnimationView) findViewById(R.id.bt_tuto_memory_learn);

		tv_tuto_memory_right_push_text = (TextView) findViewById(R.id.tv_tuto_memory_right_push_text);
		tv_tuto_memory_tts_text = (TextView) findViewById(R.id.tv_tuto_memory_tts_text);
		tv_tuto_memory_left_push_text = (TextView) findViewById(R.id.tv_tuto_memory_left_push_text);
//		lv_tuto_memory_tutorial = (ListView) findViewById(R.id.lv_tuto_memory_tutorial);
		iv_tuto_memory_mean_guide = (ImageView) findViewById(R.id.iv_tuto_memory_mean_guide);
		iv_tuto_memory_known_guide = (ImageView) findViewById(R.id.iv_tuto_memory_known_guide);
		iv_tuto_memory_mean_hand = (ImageView) findViewById(R.id.iv_tuto_memory_mean_hand);
		iv_tuto_memory_tts_hand = (ImageView) findViewById(R.id.iv_tuto_memory_tts_hand);
		iv_tuto_memory_known_hand = (ImageView) findViewById(R.id.iv_tuto_memory_known_hand);

		iv_tuto_memory_question_mark = (ImageView) findViewById(R.id.iv_tuto_memory_question_mark);
		tv_tuto_memory_when_text = (TextView) findViewById(R.id.tv_tuto_memory_when_text);
		bg_gray = (View) findViewById(R.id.bg_gray);

		tv_tuto_memory_3_hour_text = (TextView) findViewById(R.id.tv_tuto_memory_3_hour_text);
		tv_tuto_memory_7_hour_text = (TextView) findViewById(R.id.tv_tuto_memory_7_hour_text);
		ll_tuto_memory_card_3 = (LinearLayout) findViewById(R.id.ll_tuto_memory_card_3);
		iv_tuto_memory_3_cover = (ImageView) findViewById(R.id.iv_tuto_memory_3_cover);
		ll_tuto_memory_card_7 = (LinearLayout) findViewById(R.id.ll_tuto_memory_card_7);
		iv_tuto_memory_7_cover = (ImageView) findViewById(R.id.iv_tuto_memory_7_cover);
		tv_card_3_time = (TextView) findViewById(R.id.tv_card_3_time);
		tv_card_7_time = (TextView) findViewById(R.id.tv_card_7_time);

		tv_tuto_memory_user_memory_text = (TextView) findViewById(R.id.tv_tuto_memory_user_memory_text);
		rl_tuto_memory_kakao_profile = (RelativeLayout) findViewById(R.id.rl_tuto_memory_kakao_profile);
		ll_tuto_memory_question_clock = (LinearLayout) findViewById(R.id.ll_tuto_memory_question_clock);
		bg_yellow = (View) findViewById(R.id.bg_yellow);

		tv_tuto_memory_curve_text = (TextView) findViewById(R.id.tv_tuto_memory_curve_text);
		iv_tuto_memory_wide_curve = (ImageView) findViewById(R.id.iv_tuto_memory_wide_curve);
		iv_tuto_memory_curve_zoom_lens = (ImageView) findViewById(R.id.iv_tuto_memory_curve_zoom_lens);
		bt_tuto_memory_quest_finish = (CircleAnimationView) findViewById(R.id.bt_tuto_memory_quest_finish);

		tv_tuto_memory_goal_setting_finish = (TextView) findViewById(R.id.tv_tuto_memory_goal_setting_finish);
		iv_tuto_memory_head_icon = (ImageView) findViewById(R.id.iv_tuto_memory_head_icon);
		tv_tuto_memory_reward_text = (TextView) findViewById(R.id.tv_tuto_memory_reward_text);
		tv_tuto_memory_finish_reward_after = (TextView) findViewById(R.id.tv_tuto_memory_finish_reward_after);
		bt_tuto_memory_quest_finish_reward = (CircleAnimationView) findViewById(R.id.bt_tuto_memory_quest_finish_reward);



		settingTopInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_layout);
		goalTextInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.set_goal_text_in);
		learnpageInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.learnpage_in_anim);
		btnInAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_in);
		learnpageOutAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_3_out_anim);
		btnOutAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_4_out_anim);
		settingTopOutAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_layout_out);
		goalTextOutAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.set_memory_goal_text_out);

		tutorial_top_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_layout);
		tutorial_text_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.set_goal_text_in);
		listview_in = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
		hand_in = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
		arrow_in = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
		tutorial_text_fade_out = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
		tutorial_text_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.memory_quest_text_out);
		tutorial_top_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.memory_test_top_out);

		question_icon_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.question_icon_in);
		when_text_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.when_text_in);
		question_icon_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.question_icon_out);
		when_text_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.when_text_out);
		gray_gradient_in = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
		gray_gradient_out = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);

		card_3_hour_card_in	= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.card_3_hours_card_in);
		card_7_hour_card_in	= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.card_7_hours_card_in);
		card_3_hour_card_cover = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.card_3_hours_cover);
		card_3_hour_text_in	= AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
		card_3_hour_card_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.card_3_hours_card_out);
		card_3_hour_text_out = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
		card_7_hour_card_cover = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.card_7_hours_cover);
		card_7_hour_text_in	= AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
		card_7_hour_card_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.card_7_hours_card_out);
		card_7_hour_text_out = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);

		user_memory_text_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.user_memory_text);
		user_memory_profile_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.user_memory_profile_in);
		user_memory_clock_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.user_memory_clock_in);
		user_memory_text_out = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
		user_memory_profile_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.user_memory_profile_out);
		user_memory_clock_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.user_memory_clock_out);
		yellow_gradient_in = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);

		user_memory_profile_move = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.user_memory_profile_move);
		user_memory_wide_curve = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.user_memory_wide_curve_in);
		curve_text_in = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
		curve_btn_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_in);
		zoom_lens_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.curve_zoom_lens_in);
		curve_text_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.set_goal_text_out);

		expand_memory_top = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_layout_memory_expand);
		set_finish_text_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.set_goal_finish_text);
		head_icon_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.punch_icon_in);
		reward_text_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.do_not_forget_text_in);
		finish_reward_text_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.finish_reward_in);
		get_reward_btn_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_in);

		SpannableStringBuilder sb = new SpannableStringBuilder();
		String str = "지금 외운 이 단어를\n언제까지\n기억 할 수 있을까요";
		sb.append(str);
		sb.setSpan(new StyleSpan(Typeface.BOLD), 11, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv_tuto_memory_when_text.setText(sb);

		SpannableStringBuilder sb2 = new SpannableStringBuilder();
		String card_3_text = "한 학생은\n\'3시간\'을 기억했고";
		sb2.append(card_3_text);
		sb2.setSpan(new StyleSpan(Typeface.BOLD), 6, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		tv_tuto_memory_3_hour_text.setText(sb2);


		SpannableStringBuilder sb3 = new SpannableStringBuilder();
		String card_7_text = "어떤 학생은\n\'7시간\'을 기억했습니다";
		sb3.append(card_7_text);
		sb3.setSpan(new StyleSpan(Typeface.BOLD), 7, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		tv_tuto_memory_7_hour_text.setText(sb3);



		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		paint.setTextSize(WillTutoActivity.spTopx(getApplicationContext(), 12));
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.CENTER);

		bt_tuto_memory_learn.setText("단어 암기법 배우기");
		bt_tuto_memory_quest_finish.setText("확인");
		bt_tuto_memory_quest_finish_reward.setText("보상받기");

		bt_tuto_memory_learn.setColor(0xdd, 0xae, 0x3e);
		bt_tuto_memory_quest_finish.setColor(0xff, 0xff, 0xff);
		bt_tuto_memory_quest_finish_reward.setColor(0xff, 0xff, 0xff);

		bt_tuto_memory_quest_finish_reward.setAnimButton(paint, R.drawable.forgetqwest_confirm_btn, new CircleAnimationView.ClickCallbacks() {
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {
						bt_tuto_memory_quest_finish_reward.stopAnimation();

						Intent i = new Intent(getApplicationContext(), MemoryRewardActivity.class);
						i.putExtra("start", false);
						startActivityForResult(i, 13579);

						FlurryAgent.logEvent("MemoryTutoActivity:Click_Finish", Step3Params);
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

		bt_tuto_memory_quest_finish.setAnimButton(paint, R.drawable.forgetqwest_confirm_btn, new CircleAnimationView.ClickCallbacks() {
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {
						bt_tuto_memory_quest_finish.stopAnimation();

						rl_tuto_memory_setting_top.startAnimation(expand_memory_top);

						FlurryAgent.logEvent("MemoryTutoActivity:Click_WantKnow_Forgetting", Step2Params);
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

		bt_tuto_memory_learn.setAnimButton(paint, R.drawable.forgetqwest_confirm_btn, new CircleAnimationView.ClickCallbacks() {
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {
						bt_tuto_memory_learn.stopAnimation();
						bt_tuto_memory_learn.startAnimation(btnOutAnim);
						iv_tuto_memory_learnpage.startAnimation(learnpageOutAnim);
					}
				});
			}
		});

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent i = new Intent(getApplicationContext(), MemoryRewardActivity.class);
				i.putExtra("start", true);
				startActivityForResult(i, 13579);
			}
		}, 900);



		//rl_tuto_memory_setting_top.setVisibility(View.VISIBLE);
		//rl_tuto_memory_setting_top.startAnimation(settingTopInAnim);

		head_icon_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						bt_tuto_memory_quest_finish_reward.setVisibility(View.VISIBLE);
						bt_tuto_memory_quest_finish_reward.startAnimation(get_reward_btn_in);
					}
				}, 208);
			}
		});

		expand_memory_top.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				tv_tuto_memory_goal_setting_finish.setVisibility(View.VISIBLE);
				tv_tuto_memory_reward_text.setVisibility(View.VISIBLE);
				tv_tuto_memory_finish_reward_after.setVisibility(View.VISIBLE);
				iv_tuto_memory_head_icon.setVisibility(View.VISIBLE);

				set_finish_text_in.setStartOffset(1000);
				reward_text_anim.setStartOffset(1000);
				finish_reward_text_in.setStartOffset(1000);
				head_icon_in.setStartOffset(1042);

				tv_tuto_memory_goal_setting_finish.startAnimation(set_finish_text_in);
				tv_tuto_memory_reward_text.startAnimation(reward_text_anim);
				tv_tuto_memory_finish_reward_after.startAnimation(finish_reward_text_in);
				iv_tuto_memory_head_icon.startAnimation(head_icon_in);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				iv_tuto_memory_curve_zoom_lens.setVisibility(View.GONE);
				iv_tuto_memory_wide_curve.setVisibility(View.GONE);
				rl_tuto_memory_kakao_profile.setVisibility(View.GONE);
				tv_tuto_memory_curve_text.setVisibility(View.GONE);
				bt_tuto_memory_quest_finish.setVisibility(View.GONE);
				bg_yellow.setVisibility(View.GONE);

				rl_tuto_memory_setting_top.getLayoutParams().height = LayoutParams.MATCH_PARENT;


			}
		});

		user_memory_profile_move.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				bt_tuto_memory_quest_finish.setVisibility(View.VISIBLE);
				bt_tuto_memory_quest_finish.startAnimation(curve_btn_in);
				iv_tuto_memory_curve_zoom_lens.setVisibility(View.VISIBLE);
				iv_tuto_memory_curve_zoom_lens.startAnimation(zoom_lens_in);
			}
		});

		user_memory_profile_out.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				int margin = Utils.dpToPixels(6, getResources());
				RelativeLayout.LayoutParams picParams = (LayoutParams) iv_tuto_memory_kakao_pic.getLayoutParams();
				picParams.setMargins(margin, margin, margin, margin);

				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_tuto_memory_kakao_profile.getLayoutParams();//
				params.height = HitTutoActivity.getDpToPixel(getApplicationContext(), 48);
				params.width = HitTutoActivity.getDpToPixel(getApplicationContext(), 48);
				rl_tuto_memory_kakao_profile.setLayoutParams(params);

				rl_tuto_memory_kakao_profile.startAnimation(user_memory_profile_move);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						iv_tuto_memory_wide_curve.setVisibility(View.VISIBLE);
						iv_tuto_memory_wide_curve.startAnimation(user_memory_wide_curve);

						curve_text_in.setDuration(83);
						tv_tuto_memory_curve_text.setVisibility(View.VISIBLE);
						tv_tuto_memory_curve_text.startAnimation(curve_text_in);
					}
				}, 167);
			}
		});

		user_memory_text_out.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				tv_tuto_memory_user_memory_text.setVisibility(View.GONE);
			}
		});

		user_memory_clock_out.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				ll_tuto_memory_question_clock.setVisibility(View.GONE);
			}
		});

		user_memory_clock_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				user_memory_text_out.setStartOffset(1292);
				user_memory_text_out.setDuration(500);
				tv_tuto_memory_user_memory_text.startAnimation(user_memory_text_out);
				rl_tuto_memory_kakao_profile.startAnimation(user_memory_profile_out);
				ll_tuto_memory_question_clock.startAnimation(user_memory_clock_out);
			}
		});

		gray_gradient_out.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				bg_gray.setVisibility(View.GONE);
			}
		});

		user_memory_text_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				yellow_gradient_in.setDuration(667);
				yellow_gradient_in.setStartOffset(208);
				bg_yellow.setVisibility(View.VISIBLE);
				bg_yellow.startAnimation(yellow_gradient_in);
				gray_gradient_out.setDuration(667);
				bg_gray.startAnimation(gray_gradient_out);}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				rl_tuto_memory_kakao_profile.setVisibility(View.VISIBLE);
				rl_tuto_memory_kakao_profile.startAnimation(user_memory_profile_in);
				ll_tuto_memory_question_clock.setVisibility(View.VISIBLE);
				ll_tuto_memory_question_clock.startAnimation(user_memory_clock_in);
			}
		});

		card_7_hour_card_out.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				ll_tuto_memory_card_7.setVisibility(View.GONE);

				tv_tuto_memory_user_memory_text.setVisibility(View.VISIBLE);
				tv_tuto_memory_user_memory_text.startAnimation(user_memory_text_in);
			}
		});

		card_7_hour_text_out.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				tv_tuto_memory_7_hour_text.setVisibility(View.GONE);
			}
		});

		card_7_hour_text_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				card_7_hour_text_out.setStartOffset(625);
				card_7_hour_text_out.setDuration(417);
				tv_tuto_memory_7_hour_text.startAnimation(card_7_hour_text_out);
				ll_tuto_memory_card_7.startAnimation(card_7_hour_card_out);
			}
		});

		card_7_hour_card_cover.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				Thread thread = new Thread(new Runnable(){
					int rate = 0;
					@Override
					public void run() {
						while(true) {
							card7_cover_handler.sendEmptyMessage(rate);
							if ( rate == 7)
								break;
							rate++;
							SystemClock.sleep(114);
						}
					}
				});
				thread.start();
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				card_7_hour_text_in.setDuration(625);
				tv_tuto_memory_7_hour_text.setVisibility(View.VISIBLE);
				tv_tuto_memory_7_hour_text.startAnimation(card_7_hour_text_in);
			}
		});

		card_3_hour_card_out.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				ll_tuto_memory_card_3.setVisibility(View.GONE);

				iv_tuto_memory_7_cover.startAnimation(card_7_hour_card_cover);
			}
		});

		card_3_hour_text_out.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				tv_tuto_memory_3_hour_text.setVisibility(View.GONE);
			}
		});

		card_3_hour_text_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				card_3_hour_text_out.setStartOffset(625);
				card_3_hour_text_out.setDuration(417);
				tv_tuto_memory_3_hour_text.startAnimation(card_3_hour_text_out);
				ll_tuto_memory_card_3.startAnimation(card_3_hour_card_out);
			}
		});

		card_3_hour_card_cover.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Thread thread = new Thread(new Runnable() {
							int rate = 0;

							@Override
							public void run() {
								while (true) {
									card3_cover_handler.sendEmptyMessage(rate);
									if (rate == 3)
										break;
									rate++;
									SystemClock.sleep(266);
								}
							}
						});
						thread.start();
					}
				}, 1550);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				card_3_hour_text_in.setDuration(625);
				tv_tuto_memory_3_hour_text.setVisibility(View.VISIBLE);
				tv_tuto_memory_3_hour_text.startAnimation(card_3_hour_text_in);
			}
		});

		card_7_hour_card_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				iv_tuto_memory_3_cover.startAnimation(card_3_hour_card_cover);
			}
		});

		question_icon_out.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				iv_tuto_memory_question_mark.setVisibility(View.GONE);
				ll_tuto_memory_card_3.setVisibility(View.VISIBLE);
				ll_tuto_memory_card_3.startAnimation(card_3_hour_card_in);
				ll_tuto_memory_card_7.setVisibility(View.VISIBLE);
				ll_tuto_memory_card_7.startAnimation(card_7_hour_card_in);
			}
		});

		when_text_out.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				tv_tuto_memory_when_text.setVisibility(View.GONE);
			}
		});

		question_icon_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				iv_tuto_memory_question_mark.startAnimation(question_icon_out);
				tv_tuto_memory_when_text.startAnimation(when_text_out);
			}
		});

		when_text_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				gray_gradient_in.setDuration(542);
				bg_gray.setVisibility(View.VISIBLE);
				bg_gray.startAnimation(gray_gradient_in);}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				iv_tuto_memory_question_mark.setVisibility(View.VISIBLE);
				iv_tuto_memory_question_mark.startAnimation(question_icon_in);
			}
		});

//		tutorial_top_out.setAnimationListener(new AnimationListener() {
//			@Override
//			public void onAnimationStart(Animation animation) {}
//			@Override
//			public void onAnimationRepeat(Animation animation) {}
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				rl_tuto_memory_setting_top.setVisibility(View.GONE);
//				
//				tv_tuto_memory_when_text.setVisibility(View.VISIBLE);
//				tv_tuto_memory_when_text.startAnimation(when_text_in);
//			}
//		});


		hand_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						iv_tuto_memory_mean_guide.setVisibility(View.GONE);
						iv_tuto_memory_mean_hand.setVisibility(View.GONE);
					}
				}, 700);

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						rl_tuto_memory_setting_top.startAnimation(tutorial_top_out);
						tv_tuto_memory_right_push_text.startAnimation(tutorial_text_out);
						tv_tuto_memory_finish_reward.startAnimation(tutorial_text_out);
					}
				}, 825);
			}
		});

		arrow_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				hand_in.setStartOffset(42);
				hand_in.setDuration(250);
				iv_tuto_memory_mean_hand.setVisibility(View.VISIBLE);
				iv_tuto_memory_mean_hand.startAnimation(hand_in);
			}
		});

//		tutorial_top_in.setAnimationListener(new AnimationListener() {
//			@Override
//			public void onAnimationStart(Animation animation) {}
//			@Override
//			public void onAnimationRepeat(Animation animation) {}
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				arrow_in.setDuration(208);
//				arrow_in.setStartOffset(83);
//				iv_tuto_memory_mean_guide.setVisibility(View.VISIBLE);
//				iv_tuto_memory_mean_guide.startAnimation(arrow_in);
//			}
//		});

		settingTopOutAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				rl_tuto_memory_setting_top.setVisibility(View.GONE);

				rl_tuto_memory_setting_top.getLayoutParams().height = HitTutoActivity.getDpToPixel(getApplicationContext(), 176);

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						rl_tuto_memory_setting_top.setVisibility(View.VISIBLE);
						rl_tuto_memory_setting_top.startAnimation(tutorial_top_in);
						tv_tuto_memory_finish_reward.setVisibility(View.VISIBLE);
						tv_tuto_memory_finish_reward.startAnimation(tutorial_text_in);
						tv_tuto_memory_right_push_text.setVisibility(View.VISIBLE);
						tv_tuto_memory_right_push_text.startAnimation(tutorial_text_in);
					}
				}, 583);
			}
		});

		goalTextOutAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				tv_tuto_memory_finish_reward.setVisibility(View.GONE);
				tv_tuto_memory_teach_text.setVisibility(View.GONE);
			}
		});

		learnpageOutAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				iv_tuto_memory_learnpage.setVisibility(View.GONE);
				bt_tuto_memory_learn.setVisibility(View.GONE);

				rl_tuto_memory_setting_top.startAnimation(settingTopOutAnim);
				tv_tuto_memory_finish_reward.startAnimation(goalTextOutAnim);
				tv_tuto_memory_teach_text.startAnimation(goalTextOutAnim);
			}
		});

		btnOutAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				bt_tuto_memory_learn.setClickable(false);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {}
		});

		learnpageInAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				bt_tuto_memory_learn.setVisibility(View.VISIBLE);
				bt_tuto_memory_learn.startAnimation(btnInAnim);
				bt_tuto_memory_learn.setClickable(true);
			}
		});

		settingTopInAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				tv_tuto_memory_finish_reward.setVisibility(View.VISIBLE);
				tv_tuto_memory_finish_reward.startAnimation(goalTextInAnim);
				tv_tuto_memory_teach_text.setVisibility(View.VISIBLE);
				tv_tuto_memory_teach_text.startAnimation(goalTextInAnim);
				iv_tuto_memory_learnpage.setVisibility(View.VISIBLE);
				iv_tuto_memory_learnpage.startAnimation(learnpageInAnim);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {}
		});


		SpannableStringBuilder sb4 = new SpannableStringBuilder();
		String str2 = "그렇다면 가장 중요한\n" + mPreference.getString(MainValue.GpreName, "이름없음") + "님의 기억력은\n어떨까요?";
		sb4.append(str2);
		sb4.setSpan(new StyleSpan(Typeface.BOLD), 5, 18 + mPreference.getString(MainValue.GpreName, "이름없음").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		tv_tuto_memory_user_memory_text.setText(sb4);

		SpannableStringBuilder sb5 = new SpannableStringBuilder();
		String str3 = "밀당영단어의\n[망각곡선 자동 복습기]로\n" + mPreference.getString(MainValue.GpreName, "이름없음") + "님의 망각곡선(기억력)을\n측정해 드릴게요";
		sb5.append(str3);
		sb5.setSpan(new StyleSpan(Typeface.BOLD), 6, 20, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		tv_tuto_memory_curve_text.setText(sb5);

		tv_tuto_memory_reward_text.setText("기억력 측정은 " + mPreference.getString(MainValue.GpreName, "이름없음") + "님의\n학습 빅데이터를 기반으로 측정됩니다");

		readProfile();

	}

	private void setTestWordlist() {
		// TODO Auto-generated method stub

		ll_tutorial_notice = (LinearLayout)findViewById(R.id.ll_tutorial_notice);
		listView = (ListView)findViewById(R.id.lv_toturial);
		tv_tutorial_title = (TextView)findViewById(R.id.tv_tutorial_title);
		iv_navi2 = (ImageView)findViewById(R.id.iv_nav2);
		iv_tutorial_right = (ImageView)findViewById(R.id.iv_tutorial_right);
		iv_navi3 = (ImageView)findViewById(R.id.iv_navi3);
		tv_tuto_memory_finish_reward_wordlist = (TextView) findViewById(R.id.tv_tuto_memory_finish_reward_wordlist);

		anim_slideinTop = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.set_goal_text_in);
		anim_fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
		anim_fadeOut2 = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
		anim_slideinTop.setStartOffset(583);


		relativeWord = (RelativeLayout)findViewById(R.id.rl_tuto_memroy_wordlist);


		step0start = String.valueOf(System.currentTimeMillis());
		step1start= String.valueOf(System.currentTimeMillis());
		step2start= String.valueOf(System.currentTimeMillis());

			/*
			 * anim 총 모음
			 * 
			 */

		anim[0] = new AnimationUtils().loadAnimation(getApplicationContext(), R.anim.animation);
		anim[1] = new AnimationUtils().loadAnimation(getApplicationContext(), R.anim.animation11);
		anim[2] = new AnimationUtils().loadAnimation(getApplicationContext(), R.anim.animation12);
		anim[4] = new AnimationUtils().loadAnimation(getApplicationContext(), R.anim.animation14);
		anim[5] = new AnimationUtils().loadAnimation(getApplicationContext(), R.anim.zoom_out_fake);
		anim[6] = new AnimationUtils().loadAnimation(getApplicationContext(), R.anim.zoom_out_before1);
		anim[7] = new AnimationUtils().loadAnimation(getApplicationContext(), R.anim.zoom_out_after);
		anim[8] = new AnimationUtils().loadAnimation(getApplicationContext(), R.anim.zoom_out_after_3);

		fadeInAnimation = AnimationUtils.loadAnimation( getApplicationContext(), android.R.anim.fade_in );

		fadeOutAnimation = AnimationUtils.loadAnimation( getApplicationContext(), android.R.anim.fade_out );
		fadeInAnimation.setDuration(500);
		fadeOutAnimation.setDuration(500);

		Animation anim_topSlide = new AnimationUtils().loadAnimation(getApplicationContext(), R.anim.set_goal_text_in);
		anim_topSlide.setStartOffset(0);
		ll_tutorial_notice.startAnimation(tutorial_top_in);
		ll_tutorial_notice.setVisibility(View.VISIBLE);

		tutorial_top_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				ll_tutorial_notice.setVisibility(View.VISIBLE);
				tv_tuto_memory_finish_reward_wordlist.setVisibility(View.VISIBLE);
				tv_tutorial_title.setVisibility(View.VISIBLE);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {}
		});

		tutorial_text_out.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				tv_tutorial_title.setVisibility(View.INVISIBLE);
				tv_tuto_memory_finish_reward_wordlist.setVisibility(View.INVISIBLE);
			}
		});

		tutorial_top_out.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				ll_tutorial_notice.setVisibility(View.GONE);

				tv_tuto_memory_when_text.setVisibility(View.VISIBLE);
				tv_tuto_memory_when_text.startAnimation(when_text_in);
			}
		});

		anim_fadeOut.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub

				// TODO Auto-generated method stub
				Log.e("memorytuto_test", "onAnimationEnd");
				Log.e("memorytuto_test", ""+isStep[1]+ "  "+ isStep[2]+"  "+ isStep[3]);

				if(isTopAnimStep[1]){
					tv_tutorial_title.setVisibility(View.VISIBLE);
					tv_tutorial_title.startAnimation(tutorial_text_in);
					tv_tuto_memory_finish_reward_wordlist.startAnimation(tutorial_text_in);
					tv_tutorial_title.setText("오른쪽으로 살짝 밀어서\n단어의 뜻을 보고 외워주세요");
				}else if(isTopAnimStep[2]){
					tv_tutorial_title.setVisibility(View.VISIBLE);
					tv_tutorial_title.startAnimation(tutorial_text_in);
					tv_tuto_memory_finish_reward_wordlist.startAnimation(tutorial_text_in);
					tv_tutorial_title.setText("이제 단어를 외웠다면\n왼쪽으로 밀어서 없애주세요");
				}else if(isTopAnimStep[3]){
					tv_tutorial_title.setVisibility(View.VISIBLE);
					tv_tutorial_title.setText("기능 튜토리얼이 모두 끝이 났습니다.\n수고하셨습니다");
					tv_tuto_memory_finish_reward_wordlist.startAnimation(tutorial_text_in);
					rl_tuto_memroy_wordlist.startAnimation(new AnimationUtils().loadAnimation(getApplicationContext(), android.R.anim.fade_out));
					rl_tuto_memroy_wordlist.setVisibility(View.GONE);
					isTopAnimStep[0]= true;
					isTopAnimStep[3]= false;
					isStep[0]= true;
					isStep[3]= false;
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							SharedPreferences.Editor editor = mPreference.edit();
							//				 					editor.putString(MainActivitys.GpreTutorial, "1");
							//									editor.commit();
							//									Intent intent = new Intent(MemoryTutoActivity.this, Tutorial_Test_Activity.class);
							//									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							//									startActivity(intent);
							//									finish();
							//									overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);



//							rl_tuto_memory_setting_top.setVisibility(View.VISIBLE);
//							rl_tuto_memory_setting_top.startAnimation(tutorial_top_in);
//							tv_tuto_memory_finish_reward.setVisibility(View.VISIBLE);
//							tv_tuto_memory_finish_reward.startAnimation(tutorial_text_in);
//							tv_tuto_memory_right_push_text.setVisibility(View.VISIBLE);
//							tv_tuto_memory_right_push_text.startAnimation(tutorial_text_in);


//							rl_tuto_memory_setting_top.startAnimation(tutorial_top_out);
//							tv_tuto_memory_right_push_text.startAnimation(tutorial_text_out);
//							tv_tuto_memory_finish_reward.startAnimation(tutorial_text_out);
							mPullToRefreshListView.setVisibility(View.GONE);
							listView.setVisibility(View.GONE);
							ll_tutorial_notice.startAnimation(tutorial_top_out);
						}
					}, 333);

				}else if(isStep[4]){

				}


			}
		});


		anim[5].setAnimationListener(new AnimationListener() {

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
				if(isStep[2]){
					iv_navi3.invalidate();
					iv_navi3.clearAnimation();
					iv_navi3.startAnimation(anim[1]);
					anim[1].setAnimationListener(new AnimationListener() {

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
							if(isStep[2]){
								iv_navi3.startAnimation(anim[5]);
							}
						}
					});
				}
			}
		});

		anim[6].setAnimationListener(new AnimationListener() {

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
				if(isStep[1]){
					iv_navi3.invalidate();
					iv_navi3.clearAnimation();
					iv_navi3.startAnimation(anim[2]);
					anim[2].setAnimationListener(new AnimationListener() {

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

							iv_navi3.startAnimation(anim[6]);

						}
					});
				}
			}
		});

		anim[4].setAnimationListener(new AnimationListener() {

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
				if(isStep[3]){
					iv_navi2.invalidate();
					iv_navi2.clearAnimation();
					iv_navi2.startAnimation(anim[8]);
					anim[8].setAnimationListener(new AnimationListener() {

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
							if(isStep[3]){
								iv_navi2.startAnimation(anim[4]);
							}
						}
					});
				}
			}
		});

		db = DBPool.getInstance(MemoryTutoActivity.this);

		words= new ArrayList<Word>();
		metrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		vibe = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);



		mPullToRefreshListView = new PullToRefreshListView(getApplicationContext());
		mPullToRefreshListView.setOnLoadingNextClickListenet(new OnNextClickListener(){

			@Override
			public void onClick() {
				//				Log.d("kjw", "WordListFragment Click!!");
				Log.e("getword", "click refresh");
				if(isStep[4]){
					SharedPreferences.Editor editor = mPreference.edit();
					editor.putString(MainActivitys.GpreTutorial, "1");
					editor.commit();

//					Intent intent = new Intent(Tutorial_Activity.this, Tutorial_Test_Activity.class);
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					startActivity(intent);
//					finish();
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					isStep[4] = false;
					isStep[0] = true;

				}
			}
		});

		mPullToRefreshListView.setOnScrollListener(this);
		mPullToRefreshListView.setLayoutParams(listView.getLayoutParams());

		mPullToRefreshListView.setMode(Mode.PULL_FROM_END);
		View view = mPullToRefreshListView.get_footer();

		iv_nav4 = (ImageView)view.findViewById(R.id.iv_nav4);

		relativeWord.addView(mPullToRefreshListView);


		test_word_add(1);
	}

	public void test_word_add(int type){
		words.clear();

		if(words.isEmpty()){
			words.add(db.get_test_db(type).get(0));
		}

		if(type ==2){
			adapter.clear();
		}


		adapter = new ListAdapter(getApplicationContext(), R.layout.test_word_list_row, words);
		mPullToRefreshListView.setAdapter(adapter);
	}

	private void setViewHolder(View view) {

		final int NORMAL_KNOWN_COLOR = Color.rgb(0x00, 0xb5, 0x69);
		final int EXAM_KNOWN_COLOR = Color.rgb(0xe6, 0x44, 0x2e);

		WordItemViewHolder vh = new WordItemViewHolder();

		vh.linearForward = (LinearLayout)view.findViewById(R.id.linearForward);
		vh.linearKnown = (LinearLayout)view.findViewById(R.id.linearKnown);
		vh.linearUnknown = (LinearLayout)view.findViewById(R.id.linearUnknown);


		vh.ll_known_first_mean = (LinearLayout)view.findViewById(R.id.ll_known_first_mean);
		vh.ll_known_second_mean = (LinearLayout)view.findViewById(R.id.ll_known_second_mean);
		vh.ll_known_third_mean = (LinearLayout)view.findViewById(R.id.ll_known_third_mean);
		vh.ll_known_forth_mean = (LinearLayout)view.findViewById(R.id.ll_known_forth_mean);

		vh.ll_first_mean = (LinearLayout)view.findViewById(R.id.ll_first_mean);
		vh.ll_second_mean = (LinearLayout)view.findViewById(R.id.ll_second_mean);
		vh.ll_third_mean = (LinearLayout)view.findViewById(R.id.ll_third_mean);
		vh.ll_forth_mean = (LinearLayout)view.findViewById(R.id.ll_forth_mean);


		vh.ivKnown = (ImageView)view.findViewById(R.id.ivKnown);
		vh.iv_wc = (ImageView)view.findViewById(R.id.iv_wc);

		vh.linearForward.setVisibility(View.VISIBLE);
		vh.tvForward =(TextView)view.findViewById(R.id.tvForward);
		vh.tvKnownWord =(TextView)view.findViewById(R.id.tvKnownWord);
		vh.tvUnknownWord =(TextView)view.findViewById(R.id.tvUnknownWord);
		vh.tvUnknownCount =(ImageView)view.findViewById(R.id.tvUnknownCount);

		vh.tv_known_first_mean_title=(TextView)view.findViewById(R.id.tv_known_first_mean_title);
		vh.tv_known_second_mean_title=(TextView)view.findViewById(R.id.tv_known_second_mean_title);
		vh.tv_known_third_mean_title=(TextView)view.findViewById(R.id.tv_known_third_mean_title);
		vh.tv_known_forth_mean_title=(TextView)view.findViewById(R.id.tv_known_forth_mean_title);

		vh.tv_known_first_mean=(TextView)view.findViewById(R.id.tv_known_first_mean);
		vh.tv_known_second_mean=(TextView)view.findViewById(R.id.tv_known_second_mean);
		vh.tv_known_third_mean=(TextView)view.findViewById(R.id.tv_known_third_mean);
		vh.tv_known_forth_mean=(TextView)view.findViewById(R.id.tv_known_forth_mean);

		vh.tv_first_mean_title=(TextView)view.findViewById(R.id.tv_first_mean_title);
		vh.tv_second_mean_title=(TextView)view.findViewById(R.id.tv_second_mean_title);
		vh.tv_third_mean_title=(TextView)view.findViewById(R.id.tv_third_mean_title);
		vh.tv_forth_mean_title=(TextView)view.findViewById(R.id.tv_forth_mean_title);

		vh.tv_first_mean=(TextView)view.findViewById(R.id.tv_first_mean);
		vh.tv_second_mean=(TextView)view.findViewById(R.id.tv_second_mean);
		vh.tv_third_mean=(TextView)view.findViewById(R.id.tv_third_mean);
		vh.tv_forth_mean=(TextView)view.findViewById(R.id.tv_forth_mean);


		vh.linearKnown.setBackgroundColor(NORMAL_KNOWN_COLOR);

		vh.iv_flip_image = (FlipImage)view.findViewById(R.id.iv_flip_image);
		vh.tvUnknownCount1 = (ImageView)view.findViewById(R.id.tvUnknownCount1);



		vh.needInflate = false;
		view.setTag(vh);
	}


	ImageView iv_guide2;
	ImageView iv_guide3;
	ImageView iv_guide4;

	boolean flag_touch = false;


	private class ListAdapter extends ArrayAdapter<Word>{

		LayoutInflater vi;
		private ArrayList<Word> items;
		private boolean isWrongContinueShow;
		Context mContext;

		public ListAdapter(Context context, int resourceId, ArrayList<Word> items){
			super(context, resourceId, items);

			this.items = items;
			isWrongContinueShow = true;
			mContext= context;
			this.vi = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}
		public void removeItem(int position){
			items.remove(position);
			notifyDataSetChanged();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent){

			final WordItemViewHolder vh;
			final View view;

			if (convertView==null) {
				view = vi.inflate(R.layout.test_word_list_row, parent, false);
				setViewHolder(view);
			}
			else if (((WordItemViewHolder)convertView.getTag()).needInflate) {
				view = vi.inflate(R.layout.test_word_list_row, parent, false);
				setViewHolder(view);
			}
			else {
				view = convertView;
			}



			final Word word = items.get(position);

			word.setState(0);

			vh = (WordItemViewHolder)view.getTag();

			vh.tvForward.setText(word.getWord());
			vh.tvKnownWord.setText(word.getWord());
			vh.tvUnknownWord.setText(word.getWord());

			vh.iv_wc.setImageResource(R.drawable.wc_upperbar_a+position%4);

			//vh.tvUnknownMeaning.setTextSize(TypedValue.COMPLEX_UNIT_SP, position);

			vh.ivKnown.setVisibility(View.GONE);
			iv_navi3.setVisibility(View.GONE);
			if(position==0&&isStep[0]){
				step0start = String.valueOf(System.currentTimeMillis());
				step0Params.put("Start", date.get_currentTime());
				iv_navi3.bringToFront();
				iv_navi3.setVisibility(View.GONE);
				iv_navi3.startAnimation(anim[0]);
			}else if(position==0&&isStep[1]){
				step1start = String.valueOf(System.currentTimeMillis());
				step1Params.put("Start", date.get_currentTime());
				iv_navi3.bringToFront();
				iv_navi3.setVisibility(View.VISIBLE);
				iv_navi3.startAnimation(anim[6]);
			}else if(position==0&&isStep[2]){
				step2start = String.valueOf(System.currentTimeMillis());
				step2Params.put("Start", date.get_currentTime());
				iv_navi3.bringToFront();
				iv_navi3.setVisibility(View.VISIBLE);
				iv_navi3.startAnimation(anim[5]);
			}
			if(word.isShow()){
				vh.iv_wc.setVisibility(View.VISIBLE);
			}else{
				vh.iv_wc.setVisibility(View.INVISIBLE);
			}

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
					WordStateAnimation(vh, word,1000, 1000);

					if(!isStep[0]){	//listen the words
						if(isStep[1]){
							FlurryAgent.logEvent("MemoryTutoActivity:Fail_AnotherStep", true);
						}else if(isStep[2]){
							FlurryAgent.logEvent("MemoryTutoActivity:Fail_AnotherStep", true);
						}
						return;
					}

					Animation animation = AnimationUtils.loadAnimation(MemoryTutoActivity.this, R.anim.zoom_out);
					vh.tvForward.startAnimation(animation);
					AudioManager audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);

					step0Params.put("End", date.get_currentTime());
					step0Params.put("Duration", ""+(System.currentTimeMillis()-Long.valueOf(step0start))/1000);
					FlurryAgent.endTimedEvent("MemoryTutoActivity:ListenToWord_Start");
					FlurryAgent.logEvent("MemoryTutoActivity:ListenToWord_End", step0Params);

					switch(audioManager.getRingerMode()){
						case AudioManager.RINGER_MODE_VIBRATE:
							// 占쎈��占쎈�占쎌��
							new AlertDialog.Builder(MemoryTutoActivity.this)
									.setMessage("진동 모드를 해제하시면 발음을 들으실 수 있습니다.")
									.setPositiveButton("확인", new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub

											isStep[0]= false;
											isTopAnimStep[1]= true;
											vibe.vibrate(pattern, -1);
											handler.post(new Runnable() {

												@Override
												public void run() {
													// TODO Auto-generated method stub
													anim[0].cancel();
													//iv_tutorial_right.setVisibility(View.VISIBLE);

													tv_tutorial_title.startAnimation(anim_fadeOut);
													tv_tuto_memory_finish_reward_wordlist.startAnimation(anim_fadeOut2);
													iv_navi3.setVisibility(View.GONE);
													iv_navi3.clearAnimation();
													Timer timer = new Timer();
													timer.schedule(new TimerTask() {

														@Override
														public void run() {
															// TODO Auto-generated method stub
															handler.post(new Runnable() {

																@Override
																public void run() {
																	// TODO Auto-generated method stub


																	isStep[1] = true;

																	notifyDataSetChanged();
																	Log.e("test_count",""+isListAnimaion);
																	iv_guide3 = (ImageView)findViewById(R.id.iv_guide3);
																	iv_guide3.setVisibility(View.VISIBLE);
																	iv_guide3.bringToFront();
																}
															});
														}
													}, 583);
												}
											});

										}
									}).show();



							break;
						case AudioManager.RINGER_MODE_NORMAL:
							// 소리
							if(tts_util.tts_check()){
								tts_util.tts_reading(vh.tvForward.getText().toString());
								if(isStep[0]){
									anim[0].cancel();
									vibe.vibrate(pattern, -1);
									//iv_tutorial_right.setVisibility(View.VISIBLE);

									isStep[0]= false;
									isTopAnimStep[1]= true;
									iv_navi3.setVisibility(View.GONE);
									tv_tutorial_title.startAnimation(anim_fadeOut);
									tv_tuto_memory_finish_reward_wordlist.startAnimation(anim_fadeOut2);
									iv_navi3.clearAnimation();
									Timer timer = new Timer();
									timer.schedule(new TimerTask() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											handler.post(new Runnable() {

												@Override
												public void run() {
													// TODO Auto-generated method stub

													isStep[1] = true;
													notifyDataSetChanged();
													Log.e("test_count",""+isListAnimaion);
													iv_guide3 = (ImageView)findViewById(R.id.iv_guide3);
													iv_guide3.setVisibility(View.VISIBLE);
													iv_guide3.bringToFront();
												}
											});
										}
									}, 583);
								}
							}else{
								Toast.makeText(MemoryTutoActivity.this, "잠시후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
							}
							break;
						case AudioManager.RINGER_MODE_SILENT:
							// 무음
							new AlertDialog.Builder(MemoryTutoActivity.this)
									.setMessage("음소거 모드를 해제하시면 발음을 들으실 수 있습니다.")
									.setPositiveButton("확인", new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub

											isStep[0]= false;
											isTopAnimStep[1]= true;
											vibe.vibrate(pattern, -1);
											handler.post(new Runnable() {

												@Override
												public void run() {
													// TODO Auto-generated method stub
													anim[0].cancel();
													//iv_tutorial_right.setVisibility(View.VISIBLE);

													iv_navi3.setVisibility(View.GONE);
													iv_navi3.clearAnimation();
													tv_tutorial_title.startAnimation(anim_fadeOut);
													tv_tuto_memory_finish_reward_wordlist.startAnimation(anim_fadeOut2);


													Timer timer = new Timer();
													timer.schedule(new TimerTask() {

														@Override
														public void run() {
															// TODO Auto-generated method stub
															handler.post(new Runnable() {

																@Override
																public void run() {
																	// TODO Auto-generated method stub
																	isStep[1] = true;

																	notifyDataSetChanged();
																	Log.e("test_count",""+isListAnimaion);
																	iv_guide3 = (ImageView)findViewById(R.id.iv_guide3);
																	iv_guide3.setVisibility(View.VISIBLE);
																	iv_guide3.bringToFront();
																}
															});
														}
													}, 583);
												}
											});

										}
									}).show();
							break;
					}
					vh.iv_wc.setVisibility(View.INVISIBLE);
				}
			});


			vh.linearUnknown.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					vh.linearForward.setVisibility(View.VISIBLE);
					Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_left);
					anim.setDuration(150);
					vh.linearForward.startAnimation(anim);

				}
			});



			vh.linearForward.setOnTouchListener(new SwipeDismissTouchListener(
							vh.linearForward, vh.linearUnknown, vh.linearKnown, vh.iv_wc , flag_set_swipe_mode,
							null,
							new SwipeDismissTouchListener.DismissCallbacks() {

								@Override
								public boolean canDismiss(View v,Object token) {

//							if(flag_touch){
//								return false;
//							}else{
//								flag_touch = true;
//							}

									if(word.getMeanList().size()!=0){

										int count = word.getMeanList().size();

										Log.d("count", ""+count);
										vh.ll_first_mean.setVisibility(View.GONE);
										vh.ll_second_mean.setVisibility(View.GONE);
										vh.ll_third_mean.setVisibility(View.GONE);
										vh.ll_forth_mean.setVisibility(View.GONE);

										vh.ll_known_first_mean.setVisibility(View.GONE);
										vh.ll_known_second_mean.setVisibility(View.GONE);
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

										Mean mean ;

										String M_N="";
										String M_V="";
										String M_A="";
										String M_AD="";
										String M_CONJ="";
										Log.d("test_class", "1");
										for(int i=0; i <word.getMeanList().size(); i++){
											mean = word.getMean(i);
											int key = mean.getMClass();
											switch (key) {
												case Word.Class_N:
													M_N += mean.getMeaning()+", ";
													break;
												case Word.Class_V:
													M_V += mean.getMeaning()+", ";
													break;
												case Word.Class_Ad:
													M_AD += mean.getMeaning()+", ";
													break;
												case Word.Class_Conj:
													M_CONJ += mean.getMeaning()+", ";
													break;
												default:
													break;
											}
										}
										Log.d("test_class", "2");
										Log.d("test_class", "2  "+word.getP_class());

										switch (word.getP_class()) {
											case Word.Class_N:
												vh.ll_first_mean.setVisibility(View.VISIBLE);
												vh.tv_first_mean_title.setText("n");
												vh.tv_first_mean.setText(M_N.substring(0, M_N.length()-2));
												vh.ll_known_first_mean.setVisibility(View.VISIBLE);
												vh.tv_known_first_mean.setText(M_N.substring(0, M_N.length()-2));
												Log.d("test_class", "22222222222222");
												break;
											case Word.Class_V:
												vh.ll_first_mean.setVisibility(View.VISIBLE);
												vh.tv_first_mean_title.setText("v");
												vh.tv_first_mean.setText(M_V.substring(0, M_V.length()-2));
												vh.ll_known_first_mean.setVisibility(View.VISIBLE);
												vh.tv_known_first_mean.setText(M_V.substring(0, M_V.length()-2));
												break;
											case Word.Class_A:
												vh.ll_first_mean.setVisibility(View.VISIBLE);
												vh.tv_first_mean_title.setText("a");
												vh.tv_first_mean.setText(M_A.substring(0, M_A.length()-2));
												vh.ll_known_first_mean.setVisibility(View.VISIBLE);
												vh.tv_known_first_mean.setText(M_A.substring(0, M_A.length()-2));
												break;
											case Word.Class_Ad:
												vh.ll_first_mean.setVisibility(View.VISIBLE);
												vh.tv_first_mean_title.setText("ad");
												vh.tv_first_mean.setText(M_AD.substring(0, M_AD.length()-2));
												vh.ll_known_first_mean.setVisibility(View.VISIBLE);
												vh.tv_known_first_mean.setText(M_AD.substring(0, M_AD.length()-2));
												break;
											case Word.Class_Conj:
												vh.ll_first_mean.setVisibility(View.VISIBLE);
												vh.tv_first_mean_title.setText("conj");
												vh.tv_first_mean.setText(M_CONJ.substring(0, M_CONJ.length()-2));
												vh.ll_known_first_mean.setVisibility(View.VISIBLE);
												vh.tv_known_first_mean.setText(M_CONJ.substring(0, M_CONJ.length()-2));
												break;
										}
										Log.d("test_class", "3");

										HashMap<Integer, Boolean> hm = word.getmClassList();
										hm.remove(word.getP_class());
										Set<Integer> st = hm.keySet();
										Iterator<Integer> it= st.iterator();
										int line = 0;

										while(it.hasNext()){
											int key = it.next();
											String multi_mean = "";
											String mclass = "";
											switch(key){
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

											if(line ==0){
												vh.ll_second_mean.setVisibility(View.VISIBLE);
												vh.tv_second_mean_title.setText(mclass);
												vh.tv_second_mean.setText(multi_mean.substring(0,multi_mean.length()-2));
												vh.ll_known_second_mean.setVisibility(View.VISIBLE);
												vh.tv_known_second_mean.setText(multi_mean.substring(0,multi_mean.length()-2));
												mclass = "";
												multi_mean = "";
												line++;
											}else if(line ==1){
												vh.ll_third_mean.setVisibility(View.VISIBLE);
												vh.tv_third_mean_title.setText("conj");
												vh.tv_third_mean.setText(multi_mean.substring(0,multi_mean.length()-2));
												vh.ll_known_third_mean.setVisibility(View.VISIBLE);
												vh.tv_known_third_mean.setText(multi_mean.substring(0,multi_mean.length()-2));
												mclass = "";
												multi_mean = "";
											}
										}

										Log.d("test_class", "4");

										int text_sp = 0;

										if(word.getmClassList().size()>1){
											text_sp = 15;
										}else if(word.getmClassList().size()==1){
											if(vh.tv_first_mean.length()>15||vh.tv_second_mean.length()>15){
												//포문으로 글씨를 계속 줄여본다
												text_sp = 15;
											}else{
												text_sp = 17;
											}
										}else{
											text_sp = 17;
										}

										vh.tv_first_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
										vh.tv_second_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
										vh.tv_third_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
										vh.tv_forth_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);

										vh.tv_first_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
										vh.tv_second_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
										vh.tv_third_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
										vh.tv_forth_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);

										vh.tv_known_first_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
										vh.tv_known_second_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
										vh.tv_known_third_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
										vh.tv_known_forth_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);

										vh.tv_known_first_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
										vh.tv_known_second_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
										vh.tv_known_third_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
										vh.tv_known_forth_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);

										if(vh.tv_first_mean_title.getText().toString().equals("conj")){
											vh.tv_first_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
										}else if(vh.tv_second_mean_title.getText().toString().equals("conj")){
											vh.tv_second_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
										}else if(vh.tv_third_mean_title.getText().toString().equals("conj")){
											vh.tv_third_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
										}

										Log.d("test_class", "5");
									}else{
										vh.tv_known_first_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
										vh.tv_known_first_mean.setText("없음");
										vh.ll_known_second_mean.setVisibility(View.GONE);
										vh.ll_known_third_mean.setVisibility(View.GONE);
										vh.ll_known_forth_mean.setVisibility(View.GONE);

										vh.tv_first_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
										vh.tv_first_mean.setText("없음");
										vh.ll_second_mean.setVisibility(View.GONE);
										vh.ll_third_mean.setVisibility(View.GONE);
										vh.ll_forth_mean.setVisibility(View.GONE);
									}
									Log.d("test_class", "6");
									return true;
								}


								@Override
								public void onLeftDismiss(View v, Object token, boolean flag) {
									// TODO Auto-generated method stub
									flag_touch = false;

								}


								@Override
								public void onRightDismiss(View v, Object token, boolean flag) {
									// TODO Auto-generated method stub

									flag_touch = false;
									if(isStep[2]){

										step2Params.put("End", date.get_currentTime());
										step2Params.put("Duration", ""+(System.currentTimeMillis()-Long.valueOf(step2start))/1000);
										FlurryAgent.endTimedEvent("MemoryTutoActivity:KnownWord_Start");
										FlurryAgent.logEvent("MemoryTutoActivity:KnownWord_End", step2Params);

										isStep[2]=false;
										isTopAnimStep[3] = true;
										isTopAnimStep[2] = false;
										iv_guide4.setVisibility(View.GONE);
										iv_guide3.setVisibility(View.GONE);
										vibe.vibrate(pattern, -1);
										anim[5].cancel();
										anim[1].cancel();
										iv_navi3.clearAnimation();
										iv_navi3.setVisibility(View.GONE);

										final Word del_word = word;
										flag_touch = false;

										// onRightParams.put("word", ""+word.getWord());
										// onRightParams.put("time",
										// String.valueOf(System.currentTimeMillis()));
										// FlurryAgent.logEvent("튜토리얼2 onRightDismiss,Tutorial_Test_Activity,0",
										// onRightParams);

										vh.linearForward.setVisibility(View.GONE);

										// int ex_state = items.get(position).getState();
										final boolean isKnown = true;
										Log.e("swipe", "before   isWrong : " + String.valueOf(word.isWrong()) + "isRight : " + String.valueOf(word.isRight()));

										if (!del_word.isRight()) {

											del_word.setRight(true);
											del_word.setWrong(false);
											// userDb.deleteCurrentWord(word.get_id());

										}

										if (word.getState() > 0 && isWrongContinueShow) {
											// vh.tvUnknownCount.setText(items.get(position).getWrongCount()
											// + "");

											// TODO Auto-generated method stub
											vh.tvUnknownCount.setVisibility(View.INVISIBLE);
											vh.tvUnknownCount.setBackgroundResource(R.drawable.main_cell_forget);

										}

										handler.post(new Runnable() {

											@Override
											public void run() {
												// TODO Auto-generated method stub
												db.insertState0FlagTableElement(
														items.get(position), isKnown);
												// update forgetting curves by 'isknown' of word
												db.updateForgettingCurvesByNewInputs(word,
														Config.MAINWORDBOOK, isKnown);
												// insert review time to get value of memeory
												// coach ment
												db.insertReviewTimeToGetMemoryCoachMent(word);
												// db.updateWordInfo(word, isKnown);

												db.insertLevel(word, isKnown);
											}
										});

										deleteCell(view, position);
										Timer timer = new Timer();
										timer.schedule(new TimerTask() {

											@Override
											public void run() {
												// TODO Auto-generated method stub
												handler.post(new Runnable() {

													@Override
													public void run() {
														// TODO Auto-generated method stub

														if(isStep[2]){
															tv_tutorial_title.setText("기능 튜토리얼이 모두 끝이 났습니다.\n수고하셨습니다");
														}

														//iv_tutorial_right.setVisibility(View.VISIBLE);
														ll_tutorial_notice.startAnimation(tutorial_top_out);
														isStep[3] = true;

														Log.e("test_count",""+isListAnimaion);

													}
												});
											}
										}, 583);

										Date_Step2 = date.get_currentTime();
										Time_Step2 = String.valueOf(System.currentTimeMillis());
									}else if(isStep[0]){
										FlurryAgent.logEvent("MemoryTutoActivity:Fail_AnotherStep");
									}else if(isStep[1]){
										FlurryAgent.logEvent("MemoryTutoActivity:Fail_AnotherStep");
									}

								}


								@Override
								public void onLeftMovement(View v) {
									flag_touch = false;

									if(isStep[1]){
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
//							db.insertState0FlagTableElement(
//									items.get(position), isKnown);
										// update forgetting curves by 'isknown' of word
										db.updateForgettingCurvesByNewInputs(
												items.get(position), Config.MAINWORDBOOK,  isKnown);
										// insert review time to get value of memeory coach
										// ment
										db.insertReviewTimeToGetMemoryCoachMent(word);
										// db.updateWordInfo(items.get(position), isKnown);
										db.insertLevel(items.get(position), isKnown);

										step1Params.put("End", date.get_currentTime());
										step1Params.put("Duration", ""+(System.currentTimeMillis()-Long.valueOf(step1start))/1000);
										FlurryAgent.endTimedEvent("MemoryTutoActivity:UnknownWord_Start");
										FlurryAgent.logEvent("MemoryTutoActivity:UnknownWord_End", step1Params);

										vibe.vibrate(pattern, -1);

										isStep[1]=false;
										isTopAnimStep[2] = true;
										isTopAnimStep[1] = false;

										word.setState(-1);

										vh.iv_flip_image.setImage(word.getState());

										if (word.getWrongCount() != 0 && isWrongContinueShow) {
											vh.tvUnknownCount.setVisibility(View.INVISIBLE);

											vh.tvUnknownCount1.setVisibility(View.VISIBLE);
											vh.tvUnknownCount1.setBackgroundResource(R.drawable.img_state_forget_color);
										}
										WordForgetAnimation(vh, word, 500);


										tv_tutorial_title.startAnimation(anim_fadeOut);
										tv_tuto_memory_finish_reward_wordlist.startAnimation(anim_fadeOut2);
										//	notifyDataSetChanged();
										anim[6].cancel();
										anim[2].cancel();
										iv_navi3.clearAnimation();
										iv_navi3.setVisibility(View.GONE);
										isListAnimaion=false;
										iv_guide3.setVisibility(View.GONE);

										handler.postDelayed(new Runnable() {

											@Override
											public void run() {
												// TODO Auto-generated method stub

												//iv_tutorial_right.setVisibility(View.VISIBLE);
												handler.postDelayed(new Runnable() {

													@Override
													public void run() {
														// TODO Auto-generated method stub
														isStep[2] = true;
														if(isStep[2]){
															tv_tutorial_title.setText("이제 단어를 외웠다면\n왼쪽으로 밀어서 없애주세요");
														}
														//	notifyDataSetChanged();
														iv_guide4 = (ImageView)findViewById(R.id.iv_guide4);
														iv_guide4.setVisibility(View.VISIBLE);
														iv_guide4.bringToFront();
													}
												},1000);

											}
										},400);


										//								RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
										//										ViewGroup.LayoutParams.FILL_PARENT,
										//										ViewGroup.LayoutParams.FILL_PARENT);
										//								lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
										//								listView.setLayoutParams(lp);
									}else if(isStep[0]){
										FlurryAgent.logEvent("MemoryTutoActivity:Fail_AnotherStep");
									}else if(isStep[2]){
										FlurryAgent.logEvent("MemoryTutoActivity:Fail_AnotherStep");
									}
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
							})
			);
			if(position==1){
				Log.e("test_count",""+isListAnimaion);
			}

			if((isListAnimaion&&!isStep[1])||isStep[3])
			{

				if(position<8){
					Animation animation = null;
					animation = new AnimationUtils().loadAnimation(getApplicationContext(), android.R.anim.fade_in);
					animation.setStartOffset(583);
					animation.setAnimationListener(new AnimationListener() {

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
							iv_navi3.setVisibility(View.VISIBLE);
						}
					});
					//animation.setDuration((position * 20) + 800);  
					view.startAnimation(animation);

				}else{

					isListAnimaion = false;
				}
				if(isStep[3]){
					iv_guide4.setVisibility(View.GONE);
					iv_guide2 = (ImageView)findViewById(R.id.iv_guide2);
					iv_guide2.setVisibility(View.VISIBLE);
					iv_guide2.bringToFront();
				}
			}

			WordStateAnimation(vh, word,1000, 1500);


			return view;
		}
	}


	float moved_percent;


	void WordForgetAnimation(WordItemViewHolder viewholder, final Word word,long duration) {
		final WordItemViewHolder vh = viewholder;

		mForgetAnim = new ScaleAnimation(0.85f, 1f, 0.85f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mForgetAnim.setDuration(duration);
		//mForgetAnim.setInterpolator(new DecelerateInterpolator());

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
	void WordStateAnimation(WordItemViewHolder viewholder, Word word,long duration, long starttime) {
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



	Animation mStateAnim;
	Animation mForgetAnim;



	private void deleteCell(final View v, final int index) {

		AnimationListener al = new AnimationListener() {


			@Override
			public void onAnimationEnd(Animation arg0) {
				flag_touch =true;
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						words.remove(index);
						WordItemViewHolder vh = (WordItemViewHolder)v.getTag();
//						vh.needInflate = true;
//						isListAnimaion = true; 
//						test_word_add(2);
//						iv_navi2.setVisibility(View.VISIBLE);
//						iv_navi2.startAnimation(anim[4]);
//						iv_navi2.bringToFront();
					}
				});

				//				vh.linearForward.setEnabled(true);
			}

			@Override public void onAnimationRepeat(Animation animation) {}

			@Override public void onAnimationStart(Animation animation) {}

		};
		collapse(v, al);
	}
	private void collapse(final View v, AnimationListener al) {

		final int initialHeight = v.getHeight();
		//		Log.v("kjw", "real initialHeight = " + initialHeight);
		Animation anim = new Animation() {

			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				//				Log.v("kjw", "real interpolatedTime = " + interpolatedTime);
				if (interpolatedTime == 1) {
					v.setVisibility(View.GONE);
				}
				else {
					//					v.getLayoutParams().height = initialHeight;// - (int)(initialHeight * interpolatedTime);
					v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
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
		anim.setDuration(ANIMATION_DURATION);
		v.startAnimation(anim);
	}



	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}




	public void onStart() {

		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("MemoryTutoActivity", articleParams);

		// your code
	}

	public void onStop() {
		super.onStop();

		FlurryAgent.endTimedEvent("MemoryTutoActivity");
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

	Bitmap bitmap;
	private String thumbnailURL;

	public void readProfile() {

		thumbnailURL = mPreference.getString("image", "");
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
								iv_tuto_memory_kakao_pic.setImageBitmap(bitmap);
							}
						} catch (NullPointerException e) {
							// TODO: handle exception
							iv_tuto_memory_kakao_pic
									.setImageResource(R.drawable.profile);
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
		paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(source, rect, rect, paint);
		return output;
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
