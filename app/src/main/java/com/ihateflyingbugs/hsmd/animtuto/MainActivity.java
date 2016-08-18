package com.ihateflyingbugs.hsmd.animtuto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.VOCAconfig;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.model.UseAppInfoData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;

public class MainActivity extends Activity {
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("ACTIVITYRESULT", "" + requestCode);
		Log.d("ACTIVITYRESULT", "" + resultCode);
		if ( requestCode == 2569 && resultCode == 9652) {
			tv_tuto_will_forward.setText("설 정 완 료");
			tv_tuto_will_forward.setGravity(Gravity.CENTER_HORIZONTAL);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					willTopLayout.setVisibility(View.VISIBLE);
					willIconLayout.startAnimation(willIconAnim);

					ll_will_start_forward.setVisibility(View.GONE);
					ll_tuto_mainwill_first_card.setVisibility(View.GONE);
					ll_tuto_mainwill_second_card.setVisibility(View.GONE);

					ll_will_start_forward.setClickable(true);
				}
			}, 1500);

		}else if ( requestCode == 3864 && resultCode == 4683) {
			tv_tuto_memory_forward.setText("숙 지 완 료");
			tv_tuto_memory_forward.setGravity(Gravity.CENTER_HORIZONTAL);
			tv_tuto_memory_memorize_word.setText(db.getKnownCount() + "");
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {

					memoryTopLayout.setVisibility(View.VISIBLE);
					memoryIconLayout.startAnimation(memoryIconAnim);

					ll_memory_start_forward.setVisibility(View.GONE);
					ll_tuto_mainmemory_first_card.setVisibility(View.GONE);
					ll_tuto_mainmemory_second_card.setVisibility(View.GONE);
					ll_memory_start_forward.setClickable(true);
				}
			}, 1500);

		}else if ( requestCode == 7681 && resultCode == 1867) {
			tv_tuto_hit_forward.setText("설 정 완 료");
			tv_tuto_hit_forward.setGravity(Gravity.CENTER_HORIZONTAL);

			tv_tuto_memory_memorize_word.setText(db.getKnownCount() + "");
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


			LinearLayout.LayoutParams hit_progress_gauge_param;
			LinearLayout.LayoutParams hit_progress_backgroud_param;

			hit_progress_gauge_param = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);

			hit_progress_backgroud_param = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);

			hit_progress_gauge_param.weight = current_count;
			hit_progress_backgroud_param.weight = all_grade_count[currentLevel - 1][1]
					- current_count;

			hitBottomProgressLeft.setLayoutParams(hit_progress_gauge_param);
			hitBottomProgressRight.setLayoutParams(hit_progress_backgroud_param);


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
			tv_current_level.setText(currentLevelStr);
			tv_tuto_hit_bottom_current_memorize.setText(currentLevelCount);

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					hitTopLayout.setVisibility(View.VISIBLE);
					hitIconLayout.startAnimation(hitIconAnim);

					ll_hit_start_forward.setVisibility(View.GONE);
					ll_tuto_mainhit_first_card.setVisibility(View.GONE);
					ll_tuto_mainhit_second_card.setVisibility(View.GONE);

					ll_hit_start_forward.setClickable(true);
				}
			}, 1500);
		}
	}

	String TAG = "MainActivity";

	private Integer level_counting[][] = new Integer[6][2];

	Activity thisActivity = this;

	ScrollView sc_tuto_feedback;

	Animation willBottomAnim;
	Animation willIconAnim;
	Animation willTextLayoutAnim;
	Animation willTextAnim;
	Animation fadeInAnim;
	Animation barFadeInAnim;

	LinearLayout ll_will_start_forward;
	LinearLayout ll_tuto_will_redback;
	LinearLayout ll_will_start_back;

	LinearLayout ll_memory_start_forward;
	LinearLayout ll_tuto_memory_redback;
	LinearLayout ll_memory_start_back;
	RelativeLayout rl_memory_lock_card;
	ImageView iv_memory_lock;
	ImageView iv_tuto_memory_center_underline;

	LinearLayout ll_hit_start_forward;
	LinearLayout ll_tuto_hit_redback;
	LinearLayout ll_hit_start_back;
	RelativeLayout rl_hit_lock_card;
	ImageView iv_hit_lock;

	Animation lockCardAnim;
	Animation lockIconAnim;

	LinearLayout willTopLayout;
	LinearLayout willIconLayout;
	LinearLayout willTextLayout;
	LinearLayout willBottomLayout;
	LinearLayout willBottomTextLayout;
	TextView willTextView;
	TextView willBar;

	Animation memoryTextLayoutAnim;
	Animation memoryTextViewAnim;
	Animation memoryIconAnim;
	Animation memoryBottomAnim;
	Animation memoryBottomProgressAnim;
	Animation memoryBottomCenterAnim;
	Animation memoryBottomRightAnim;
	Animation memoryBottomLeftAnim;

	LinearLayout memoryTopLayout;
	LinearLayout memoryTextLayout;
	LinearLayout memoryIconLayout;
	LinearLayout memoryBottomLayout;
	LinearLayout memoryBottomProgressLayout;
	LinearLayout memoryBottomCenterLayout;
	LinearLayout memoryBottomRightLayout;
	LinearLayout memoryBottomLeftLayout;
	TextView memoryTextView;
	TextView memoryBar;

	LinearLayout hitTopLayout;
	LinearLayout hitTextLayout;
	LinearLayout hitIconLayout;
	LinearLayout hitBottomLayout;
	LinearLayout hitBottomTextLayout;
	LinearLayout hitBottomProgressLayout;
	TextView hitBottomProgressRight;
	TextView hitBottomProgressLeft;
	TextView hitBottomCurrentMomorize;
	TextView hitBottomLeftText;
	TextView hitTextView;
	TextView hitBar;

	Animation hitTextLayoutAnim;
	Animation hitTextViewAnim;
	Animation hitIconAnim;
	Animation hitBottomAnim;
	Animation hitBottomProgressAnim;
	Animation hitBottomTextAnim;
	Animation hitBottomCurrentMemorizeAnim;
	Animation hitBottomLeftTextAnim;

	ImageView image;
	Animation wave;


	/////////////////////////////////////////////////////////////
	//변경된 시작부분 레이아웃!
	LinearLayout ll_tuto_mainwill_first_card;
	TextView tv_tuto_mainwill_first_card_text;

	LinearLayout ll_tuto_mainwill_second_card;
	TextView tv_tuto_mainwill_second_card_left;
	ImageView iv_tuto_mainwill_second_card_center;
	TextView tv_tuto_mainwill_second_card_right;


	LinearLayout ll_tuto_mainmemory_first_card;
	TextView tv_tuto_mainmemory_first_card_text;

	LinearLayout ll_tuto_mainmemory_second_card;
	TextView tv_tuto_mainmemory_second_card_left;
	ImageView iv_tuto_mainmemory_second_card_center;
	TextView tv_tuto_mainmemory_second_card_right;


	LinearLayout ll_tuto_mainhit_first_card;
	TextView tv_tuto_mainhit_first_card_text;

	LinearLayout ll_tuto_mainhit_second_card;
	TextView tv_tuto_mainhit_second_card_left;
	ImageView iv_tuto_mainhit_second_card_center;
	TextView tv_tuto_mainhit_second_card_right;


	Animation will_first_card_in;
	Animation will_first_card_out;
	Animation will_first_card_text_in;
	Animation will_first_card_text_out;
	Animation will_second_card_icon_in;
	Animation will_second_card_left_in;
	Animation will_second_card_right_in;

	Animation hit_first_card_in;
	Animation hit_first_card_out;
	Animation hit_first_card_text_in;
	Animation hit_first_card_text_out;
	Animation hit_second_card_icon_in;
	Animation hit_second_card_left_in;
	Animation hit_second_card_right_in;

	Animation memory_first_card_in;
	Animation memory_first_card_out;
	Animation memory_first_card_text_in;
	Animation memory_first_card_text_out;
	Animation memory_second_card_icon_in;
	Animation memory_second_card_left_in;
	Animation memory_second_card_right_in;

	Animation will_third_card_in;
	Animation hit_third_card_in;
	Animation memory_third_card_in;

	TextView tv_tuto_will_forward;
	TextView tv_tuto_memory_forward;
	TextView tv_tuto_hit_forward;


	Animation ic_bounce;


	TextView tv_tuto_memory_memorize_word;
	TextView tv_current_level;
	TextView tv_tuto_hit_bottom_current_memorize;

	/////////////////////////////////////////////////////////////

	protected boolean willJelloIsBig =false;
	protected boolean memoryJelloIsBig =false;
	protected boolean hitJelloIsBig =false;

	SharedPreferences mpreferences;
	boolean tuto[] = new boolean[4];

	DBPool db;

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


	Handler handler;
	boolean isFinish= false;

	ProgressBar pb_splash_tuto;

//	Tracker mTracker;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		VOCAconfig application = (VOCAconfig) getApplication();
//		mTracker = application.getDefaultTracker();

		if(MainValue.isLowDensity(getApplicationContext())){
			setContentView(R.layout.activity_tutorialmain_small);
		}else{
			setContentView(R.layout.activity_tutorialmain);
		}

		mpreferences = getSharedPreferences(MainValue.preName,
				MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);


		Date_Step1 = date.get_currentTime();
		Time_Step1 = String.valueOf(System.currentTimeMillis());

		db = DBPool.getInstance(MainActivity.this);

		handler = new Handler();
		/*
		 * tutorial setting
		 */
		tuto[0] = db.getTutorial();
		tuto[1] = db.getTutorialWill();
		tuto[2] = db.getTutorialMemory();
		tuto[3] = db.getTutorialHit();

		sc_tuto_feedback = (ScrollView) findViewById(R.id.sc_tuto_feedback);

		ll_will_start_forward = (LinearLayout) findViewById(R.id.ll_will_start_forward);

		ll_memory_start_forward = (LinearLayout) findViewById(R.id.ll_memory_start_forward);
		//		ll_tuto_memory_redback = (LinearLayout) findViewById(R.id.ll_tuto_memory_redback);

		ll_hit_start_forward = (LinearLayout) findViewById(R.id.ll_hit_start_forward);
		//		ll_tuto_hit_redback = (LinearLayout) findViewById(R.id.ll_tuto_hit_redback);

		willTopLayout = (LinearLayout) findViewById(R.id.ll_tuto_will_top);
		willIconLayout = (LinearLayout) findViewById(R.id.ll_tuto_will_icon);
		willTextLayout = (LinearLayout) findViewById(R.id.ll_tuto_will_text);
		willTextView = (TextView) findViewById(R.id.tv_tuto_will);
		willBar = (TextView) findViewById(R.id.tv_tuto_will_bar);
		willBottomLayout = (LinearLayout) findViewById(R.id.ll_tuto_will_bottom);
		willBottomTextLayout = (LinearLayout) findViewById(R.id.ll_tuto_will_bottom_text);

		memoryTopLayout = (LinearLayout) findViewById(R.id.ll_tuto_memory_top);
		memoryTextLayout = (LinearLayout) findViewById(R.id.ll_tuto_memory_text);
		memoryIconLayout = (LinearLayout) findViewById(R.id.ll_tuto_memory_icon);
		memoryBottomLayout = (LinearLayout) findViewById(R.id.ll_tuto_memory_bottom);
		memoryBottomProgressLayout = (LinearLayout) findViewById(R.id.ll_tuto_memory_bottom_progress);
		memoryBottomCenterLayout = (LinearLayout) findViewById(R.id.ll_tuto_memory_bottom_center);
		memoryBottomRightLayout = (LinearLayout) findViewById(R.id.ll_tuto_memory_bottom_right);
		memoryBottomLeftLayout = (LinearLayout) findViewById(R.id.ll_tuto_memory_bottom_left);
		memoryTextView = (TextView) findViewById(R.id.tv_tuto_memory);
		memoryBar = (TextView) findViewById(R.id.tv_tuto_memory_bar);
		iv_tuto_memory_center_underline = (ImageView) findViewById(R.id.iv_tuto_memory_center_underline);

		hitTopLayout = (LinearLayout) findViewById(R.id.ll_tuto_hit_top);
		hitTextLayout = (LinearLayout) findViewById(R.id.ll_tuto_hit_text);
		hitIconLayout = (LinearLayout) findViewById(R.id.ll_tuto_hit_icon);
		hitBottomLayout = (LinearLayout) findViewById(R.id.ll_tuto_hit_bottom);
		hitBottomTextLayout = (LinearLayout) findViewById(R.id.ll_tuto_hit_current_level);
		hitBottomProgressLayout = (LinearLayout) findViewById(R.id.ll_tuto_hit_progrss_layout);
		hitBottomProgressRight = (TextView) findViewById(R.id.hit_progress_background);
		hitBottomProgressLeft = (TextView) findViewById(R.id.hit_progress_gauge);
		hitBottomCurrentMomorize = (TextView) findViewById(R.id.tv_tuto_hit_bottom_current_memorize);
		hitBottomLeftText = (TextView) findViewById(R.id.tv_tuto_hit_bottom_text);
		hitTextView = (TextView) findViewById(R.id.tv_tuto_hit);
		hitBar = (TextView) findViewById(R.id.tv_tuto_hit_bar);

		fadeInAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
		barFadeInAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
		willBottomAnim = AnimationUtils.loadAnimation(this, R.anim.main_bottom);
		willIconAnim = AnimationUtils.loadAnimation(this, R.anim.main_icon);
		willTextAnim = AnimationUtils.loadAnimation(this, R.anim.main_text);
		willTextLayoutAnim = AnimationUtils.loadAnimation(this, R.anim.main_text_layout);

		memoryTextLayoutAnim = AnimationUtils.loadAnimation(this, R.anim.main_text_layout);
		memoryTextViewAnim = AnimationUtils.loadAnimation(this, R.anim.main_text);
		memoryIconAnim = AnimationUtils.loadAnimation(this, R.anim.main_icon);
		memoryBottomAnim = AnimationUtils.loadAnimation(this, R.anim.main_bottom);
		memoryBottomCenterAnim = AnimationUtils.loadAnimation(this, R.anim.memory_bottom_center);
		memoryBottomProgressAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
		memoryBottomRightAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
		memoryBottomLeftAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);

		hitTextLayoutAnim = AnimationUtils.loadAnimation(this, R.anim.main_text_layout);
		hitTextViewAnim = AnimationUtils.loadAnimation(this, R.anim.main_text);
		hitIconAnim = AnimationUtils.loadAnimation(this, R.anim.main_icon);
		hitBottomAnim = AnimationUtils.loadAnimation(this, R.anim.main_bottom);
		hitBottomProgressAnim = AnimationUtils.loadAnimation(this, R.anim.hit_progress);
		hitBottomTextAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
		hitBottomCurrentMemorizeAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
		hitBottomLeftTextAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);

		rl_memory_lock_card = (RelativeLayout) findViewById(R.id.rl_memory_lock_card);
		rl_hit_lock_card = (RelativeLayout) findViewById(R.id.rl_hit_lock_card);

		iv_memory_lock = (ImageView) findViewById(R.id.iv_memory_lock);
		iv_hit_lock = (ImageView) findViewById(R.id.iv_hit_lock);

		lockCardAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
		lockCardAnim.setStartOffset(83);
		lockCardAnim.setDuration(300);
		lockIconAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
		lockIconAnim.setStartOffset(83);
		lockIconAnim.setDuration(300);

		/////////////////////////////////////////////////////////////
		//변경된 시작부분 레이아웃

		will_first_card_in = AnimationUtils.loadAnimation(this, R.anim.first_card_in);
		will_first_card_out = AnimationUtils.loadAnimation(this, R.anim.first_card_out);
		will_first_card_text_in = AnimationUtils.loadAnimation(this, R.anim.first_card_text_in);
		will_first_card_text_out = AnimationUtils.loadAnimation(this, R.anim.first_card_text_out);
		will_second_card_icon_in = AnimationUtils.loadAnimation(this, R.anim.second_card_icon_in);
		will_second_card_left_in = AnimationUtils.loadAnimation(this, R.anim.second_card_text_in);
		will_second_card_right_in = AnimationUtils.loadAnimation(this, R.anim.second_card_text_in);
		will_second_card_left_in.setStartOffset(333);
		will_second_card_right_in.setStartOffset(250);

		hit_first_card_in = AnimationUtils.loadAnimation(this, R.anim.first_card_in);
		hit_first_card_out = AnimationUtils.loadAnimation(this, R.anim.first_card_out);
		hit_first_card_text_in = AnimationUtils.loadAnimation(this, R.anim.first_card_text_in);
		hit_first_card_text_out = AnimationUtils.loadAnimation(this, R.anim.first_card_text_out);
		hit_second_card_icon_in = AnimationUtils.loadAnimation(this, R.anim.second_card_icon_in);
		hit_second_card_left_in = AnimationUtils.loadAnimation(this, R.anim.second_card_text_in);
		hit_second_card_right_in = AnimationUtils.loadAnimation(this, R.anim.second_card_text_in);
		hit_second_card_left_in.setStartOffset(333);
		hit_second_card_right_in.setStartOffset(250);

		memory_first_card_in = AnimationUtils.loadAnimation(this, R.anim.first_card_in);
		memory_first_card_out = AnimationUtils.loadAnimation(this, R.anim.first_card_out);
		memory_first_card_text_in = AnimationUtils.loadAnimation(this, R.anim.first_card_text_in);
		memory_first_card_text_out = AnimationUtils.loadAnimation(this, R.anim.first_card_text_out);
		memory_second_card_icon_in = AnimationUtils.loadAnimation(this, R.anim.second_card_icon_in);
		memory_second_card_left_in = AnimationUtils.loadAnimation(this, R.anim.second_card_text_in);
		memory_second_card_right_in = AnimationUtils.loadAnimation(this, R.anim.second_card_text_in);
		memory_second_card_left_in.setStartOffset(333);
		memory_second_card_right_in.setStartOffset(250);

		ll_tuto_mainwill_first_card = (LinearLayout) findViewById(R.id.ll_tuto_mainwill_first_card);
		tv_tuto_mainwill_first_card_text = (TextView) findViewById(R.id.tv_tuto_mainwill_first_card_text);

		ll_tuto_mainwill_second_card = (LinearLayout) findViewById(R.id.ll_tuto_mainwill_second_card);
		tv_tuto_mainwill_second_card_left = (TextView) findViewById(R.id.tv_tuto_mainwill_second_card_left);
		iv_tuto_mainwill_second_card_center = (ImageView) findViewById(R.id.iv_tuto_mainwill_second_card_center);
		tv_tuto_mainwill_second_card_right = (TextView) findViewById(R.id.tv_tuto_mainwill_second_card_right);


		ll_tuto_mainmemory_first_card = (LinearLayout) findViewById(R.id.ll_tuto_mainmemory_first_card);
		tv_tuto_mainmemory_first_card_text = (TextView) findViewById(R.id.tv_tuto_mainmemory_first_card_text);

		ll_tuto_mainmemory_second_card = (LinearLayout) findViewById(R.id.ll_tuto_mainmemory_second_card);
		tv_tuto_mainmemory_second_card_left = (TextView) findViewById(R.id.tv_tuto_mainmemory_second_card_left);
		iv_tuto_mainmemory_second_card_center = (ImageView) findViewById(R.id.iv_tuto_mainmemory_second_card_center);
		tv_tuto_mainmemory_second_card_right = (TextView) findViewById(R.id.tv_tuto_mainmemory_second_card_right);


		ll_tuto_mainhit_first_card = (LinearLayout) findViewById(R.id.ll_tuto_mainhit_first_card);
		tv_tuto_mainhit_first_card_text = (TextView) findViewById(R.id.tv_tuto_mainhit_first_card_text);

		ll_tuto_mainhit_second_card = (LinearLayout) findViewById(R.id.ll_tuto_mainhit_second_card);
		tv_tuto_mainhit_second_card_left = (TextView) findViewById(R.id.tv_tuto_mainhit_second_card_left);
		iv_tuto_mainhit_second_card_center = (ImageView) findViewById(R.id.iv_tuto_mainhit_second_card_center);
		tv_tuto_mainhit_second_card_right = (TextView) findViewById(R.id.tv_tuto_mainhit_second_card_right);

		will_third_card_in = AnimationUtils.loadAnimation(this, R.anim.third_card_slide_in);
		hit_third_card_in = AnimationUtils.loadAnimation(this, R.anim.third_card_slide_in);
		memory_third_card_in = AnimationUtils.loadAnimation(this, R.anim.third_card_slide_in);

		ic_bounce = new AnimationUtils().loadAnimation(getApplicationContext(), R.anim.zoom);

		tv_tuto_memory_memorize_word = (TextView) findViewById(R.id.tv_tuto_memory_memorize_word);
		tv_current_level = (TextView) findViewById(R.id.tv_current_level);
		tv_tuto_hit_bottom_current_memorize = (TextView) findViewById(R.id.tv_tuto_hit_bottom_current_memorize);

		pb_splash_tuto = (ProgressBar) findViewById(R.id.pb_splash_tuto);



		final ImageView iv_tuto_will_icon = (ImageView)findViewById(R.id.iv_tuto_will_foward);
		final ImageView iv_tuto_will_icon_back = (ImageView)findViewById(R.id.iv_tuto_will_backward);
		final ImageView iv_tuto_will_bounce = (ImageView)findViewById(R.id.iv_tuto_will_bounce);
		tv_tuto_will_forward = (TextView)findViewById(R.id.tv_will_forward_sidetv);

		//		final ImageView iv_tuto_hit_whiteback = (ImageView)findViewById(R.id.iv_tuto_hit_whiteback);
		final ImageView iv_tuto_hit_icon = (ImageView)findViewById(R.id.iv_tuto_hit_foward);
		final ImageView iv_tuto_hit_icon_back = (ImageView)findViewById(R.id.iv_tuto_hit_backward);
		final ImageView iv_tuto_hit_bounce = (ImageView)findViewById(R.id.iv_tuto_hit_bounce);
		tv_tuto_hit_forward = (TextView)findViewById(R.id.tv_hit_forward_sidetv);
		//		final ImageView iv_tuto_memory_whiteback = (ImageView)findViewById(R.id.iv_tuto_memory_whiteback);
		final ImageView iv_tuto_memory_icon = (ImageView)findViewById(R.id.iv_tuto_memory_foward);
		final ImageView iv_tuto_memory_icon_back = (ImageView)findViewById(R.id.iv_tuto_memory_backward);
		final ImageView iv_tuto_memory_bounce = (ImageView)findViewById(R.id.iv_tuto_memory_bounce);
		tv_tuto_memory_forward = (TextView)findViewById(R.id.tv_memory_forward_sidetv);
		//		final LinearLayout tv_tuto_memory_backward = (LinearLayout)findViewById(R.id.ll_memory_back_sidetv);

		final Animation anim_bounce  = new AnimationUtils().loadAnimation(getApplicationContext(), R.anim.will_icon_bounce);
		anim_bounce.setDuration(600);
		//		final RelativeLayout rl_tuto_memory_lock = (RelativeLayout)findViewById(R.id.rl_tuto_memory_lock);
		//		final RelativeLayout rl_tuto_hit_lock = (RelativeLayout)findViewById(R.id.rl_tuto_hit_lock);
		//		final TextView tv_willBtn = (TextView)findViewById(R.id.tv_willBtn);

		//		Log.d("START", tv_tuto_will_backward.getX() + ", " + tv_tuto_will_backward.getY() + " before");




		if(db.getTutorial()){
//			Intent intent = new Intent(MainActivity.this, MainTutoLastPopup.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);

//			Intent intent = new Intent(MainActivity.this, MainTutoLastPopup.class);
//			//						overridePendingTransition(R.anim.slide_up_bottom, R.anim.slide_up_top);
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);

			pb_splash_tuto.setVisibility(View.VISIBLE);
			Log.d("firstStart",  "LastPopup " + mpreferences.getString("firststart", "0"));

			new RetrofitService().getUseAppInfoService().retroUpdateTutorialFinished(db.getStudentId())
					.enqueue(new Callback<UseAppInfoData>() {
						@Override
						public void onResponse(Response<UseAppInfoData> response, Retrofit retrofit) {
							db.insertTutorial(true);
							Log.e("check_activity",  "LastPopup " + mpreferences.getString("firststart", "3"));
//					overridePendingTransition(R.anim.tutorial_activity_in, R.anim.tutorial_activity_in);
							Intent intent = new Intent(MainActivity.this, com.ihateflyingbugs.hsmd.tutorial.MainActivity.class);
//					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);

							handler.post(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									pb_splash_tuto.setVisibility(View.GONE);
									finish();
								}
							});
						}

						@Override
						public void onFailure(Throwable t) {
							Log.e(TAG, "check_activity : "+t.toString());
							db.insertTutorial(true);
							// overridePendingTransition(R.anim.tutorial_activity_in,
							// R.anim.tutorial_activity_in);
							Log.e("check_activity",  "LastPopup " + mpreferences.getString("firststart", "1"));
							Intent intent = new Intent(MainActivity.this, com.ihateflyingbugs.hsmd.tutorial.MainActivity.class);
							// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							isFinish = false;
							finish();
						}
					});

		}


		if(db.getTutorialWill()){
			ll_will_start_forward.setVisibility(View.GONE);
			ll_tuto_mainwill_first_card.setVisibility(View.GONE);
			ll_tuto_mainwill_second_card.setVisibility(View.GONE);
			willTopLayout.setVisibility(View.VISIBLE);
			willIconLayout.setVisibility(View.VISIBLE);
			willTextLayout.setVisibility(View.VISIBLE);
			willTextView.setVisibility(View.VISIBLE);
			willTopLayout.setBackgroundResource(R.drawable.qwest_card_crop_top);
			willTopLayout.setPadding(getDpToPixel(thisActivity, 14), getDpToPixel(thisActivity, 20), getDpToPixel(thisActivity, 14), getDpToPixel(thisActivity, 20));
			willBottomLayout.setVisibility(View.VISIBLE);
			willBar.setVisibility(View.VISIBLE);
			willBottomTextLayout.setVisibility(View.VISIBLE);



			ll_memory_start_forward.setVisibility(View.VISIBLE);
			ll_tuto_mainmemory_second_card.setVisibility(View.VISIBLE);
			ll_tuto_mainhit_second_card.setVisibility(View.VISIBLE);
			iv_tuto_mainhit_second_card_center.setVisibility(View.VISIBLE);
			tv_tuto_mainhit_second_card_left.setVisibility(View.VISIBLE);
			tv_tuto_mainhit_second_card_right.setVisibility(View.VISIBLE);
			rl_hit_lock_card.setVisibility(View.VISIBLE);
			tv_tuto_mainhit_second_card_left.setAlpha(0.20f);
			tv_tuto_mainhit_second_card_right.setAlpha(0.20f);
			iv_tuto_mainhit_second_card_center.setAlpha(0.20f);

			iv_tuto_memory_bounce.startAnimation(ic_bounce);
			iv_tuto_memory_bounce.bringToFront();
			iv_tuto_memory_bounce.setVisibility(View.VISIBLE);

			Date_Step2 = date.get_currentTime();
			Time_Step2 = String.valueOf(System.currentTimeMillis());

			if(db.getTutorialMemory()){

				ll_memory_start_forward.setVisibility(View.GONE);
				ll_tuto_mainmemory_first_card.setVisibility(View.GONE);
				ll_tuto_mainmemory_second_card.setVisibility(View.GONE);
				memoryTopLayout.setVisibility(View.VISIBLE);
				memoryIconLayout.setVisibility(View.VISIBLE);
				memoryTextLayout.setVisibility(View.VISIBLE);
				memoryTextView.setVisibility(View.VISIBLE);
				memoryTopLayout.setBackgroundResource(R.drawable.qwest_card_crop_top);
				memoryTopLayout.setPadding(getDpToPixel(thisActivity, 14), getDpToPixel(thisActivity, 20), getDpToPixel(thisActivity, 14), getDpToPixel(thisActivity, 20));
				memoryBottomLayout.setVisibility(View.VISIBLE);
				memoryBottomCenterLayout.setVisibility(View.VISIBLE);
				memoryBottomProgressLayout.setVisibility(View.VISIBLE);
				iv_tuto_memory_center_underline.setVisibility(View.VISIBLE);
				memoryBottomLeftLayout.setVisibility(View.VISIBLE);
				memoryBottomRightLayout.setVisibility(View.VISIBLE);
				memoryBar.setVisibility(View.VISIBLE);

				rl_hit_lock_card.setVisibility(View.GONE);

				tv_tuto_mainhit_second_card_left.setAlpha(1.0f);
				tv_tuto_mainhit_second_card_right.setAlpha(1.0f);
				iv_tuto_mainhit_second_card_center.setAlpha(1.0f);

				ll_hit_start_forward.setVisibility(View.VISIBLE);

				iv_tuto_hit_bounce.startAnimation(ic_bounce);
				iv_tuto_hit_bounce.bringToFront();
				iv_tuto_hit_bounce.setVisibility(View.VISIBLE);

				Date_Step3 = date.get_currentTime();
				Time_Step3 = String.valueOf(System.currentTimeMillis());

			}
		} else {
			hit_first_card_in.setStartOffset(250);
			will_first_card_in.setStartOffset(250);
			memory_first_card_in.setStartOffset(250);

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					ll_tuto_mainwill_first_card.setVisibility(View.VISIBLE);
					ll_tuto_mainwill_first_card.startAnimation(will_first_card_in);
				}
			}, 500);
		}

		sc_tuto_feedback.fullScroll(View.FOCUS_DOWN);

		ll_will_start_forward.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {

				Intent intent = new Intent(getApplicationContext(), WillTutoActivity.class);
				startActivityForResult(intent, 2569);
				overridePendingTransition(R.anim.slide_up_bottom, android.R.anim.fade_out);

				iv_tuto_will_bounce.setAnimation(null);
				iv_tuto_will_bounce.setVisibility(View.GONE);
				ll_will_start_forward.setClickable(false);

				FlurryAgent.logEvent("TutorialBaseActivity:Click_Start_WillTuto", Step1Params);
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

		ll_memory_start_forward.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {

				Intent intent = new Intent(getApplicationContext(), MemoryTutoActivity.class);
				startActivityForResult(intent, 3864);
				overridePendingTransition(R.anim.slide_up_bottom, android.R.anim.fade_out);

				iv_tuto_memory_bounce.setAnimation(null);
				iv_tuto_memory_bounce.setVisibility(View.GONE);
				ll_memory_start_forward.setClickable(false);

				FlurryAgent.logEvent("TutorialBaseActivity:Click_Start_MemoryTuto", Step2Params);
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

		ll_hit_start_forward.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {

				Intent intent = new Intent(getApplicationContext(), HitTutoActivity.class);
				startActivityForResult(intent, 7681);
				overridePendingTransition(R.anim.slide_up_bottom, android.R.anim.fade_out);

				iv_tuto_hit_bounce.setAnimation(null);
				iv_tuto_hit_bounce.setVisibility(View.GONE);
				ll_hit_start_forward.setClickable(false);

				FlurryAgent.logEvent("TutorialBaseActivity:Click_Start_HitTuto", Step3Params);
				Step3Params.put("Start", Date_Step3);
				Step3Params.put("End", date.get_currentTime());
				Step3Params.put(
						"Duration",
						""
								+ ((Long.valueOf(System.currentTimeMillis()) - Long
								.valueOf(Time_Step3))) / 1000);


			}
		});


		willIconAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				//
				willIconLayout.setVisibility(View.VISIBLE);
				willTextLayout.setVisibility(View.VISIBLE);
				willTextLayout.startAnimation(willTextLayoutAnim);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {}
		});

		willTextLayoutAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				willTextView.setVisibility(View.VISIBLE);
				willTextView.startAnimation(willTextAnim);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				willTopLayout.setBackgroundResource(R.drawable.qwest_card_crop_top);
				willTopLayout.setPadding(getDpToPixel(thisActivity, 14), getDpToPixel(thisActivity, 20), getDpToPixel(thisActivity, 14), getDpToPixel(thisActivity, 20));

				willBottomLayout.setVisibility(View.VISIBLE);
				willBottomLayout.startAnimation(willBottomAnim);
			}
		});

		willTextAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				barFadeInAnim.setDuration(83);
				barFadeInAnim.setStartOffset(83);

				fadeInAnim.setDuration(167);
				fadeInAnim.setStartOffset(375);

				willBar.setVisibility(View.VISIBLE);
				willBar.startAnimation(barFadeInAnim);
				willBottomTextLayout.setVisibility(View.VISIBLE);
				willBottomTextLayout.startAnimation(fadeInAnim);


			}
		});

		willBottomAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				rl_memory_lock_card.setVisibility(View.GONE);

				tv_tuto_mainmemory_second_card_left.setAlpha(1.0f);
				tv_tuto_mainmemory_second_card_right.setAlpha(1.0f);
				iv_tuto_mainmemory_second_card_center.setAlpha(1.0f);

				ll_memory_start_forward.setVisibility(View.VISIBLE);
				memory_third_card_in.setStartOffset(1000);
				ll_memory_start_forward.startAnimation(memory_third_card_in);
			}
		});

		memoryIconAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				memoryIconLayout.setVisibility(View.VISIBLE);
				memoryTextLayout.setVisibility(View.VISIBLE);
				memoryTextLayout.startAnimation(memoryTextLayoutAnim);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {}
		});

		memoryBottomAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				sc_tuto_feedback.fullScroll(View.FOCUS_DOWN);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {}
		});

		memoryTextLayoutAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				memoryTextView.setVisibility(View.VISIBLE);
				memoryTextView.startAnimation(memoryTextViewAnim);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				memoryTopLayout.setBackgroundResource(R.drawable.qwest_card_crop_top);
				memoryTopLayout.setPadding(getDpToPixel(thisActivity, 14), getDpToPixel(thisActivity, 20), getDpToPixel(thisActivity, 14), getDpToPixel(thisActivity, 20));

				memoryBottomLayout.setVisibility(View.VISIBLE);
				memoryBottomLayout.startAnimation(memoryBottomAnim);
			}
		});

		memoryTextViewAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				barFadeInAnim.setDuration(42);
				barFadeInAnim.setStartOffset(125);

				memoryBottomProgressAnim.setDuration(83);
				memoryBottomProgressAnim.setStartOffset(542);

				memoryBottomLeftAnim.setDuration(250);
				memoryBottomLeftAnim.setStartOffset(833);

				memoryBottomRightAnim.setDuration(250);
				memoryBottomRightAnim.setStartOffset(1042);

				memoryBottomCenterLayout.setVisibility(View.VISIBLE);
				memoryBottomCenterLayout.startAnimation(memoryBottomCenterAnim);

				memoryBottomProgressLayout.setVisibility(View.VISIBLE);
				memoryBottomProgressLayout.startAnimation(memoryBottomProgressAnim);

				iv_tuto_memory_center_underline.setVisibility(View.VISIBLE);
				iv_tuto_memory_center_underline.startAnimation(memoryBottomProgressAnim);

				memoryBottomLeftLayout.setVisibility(View.VISIBLE);
				memoryBottomLeftLayout.startAnimation(memoryBottomLeftAnim);

				memoryBottomRightLayout.setVisibility(View.VISIBLE);
				memoryBottomRightLayout.startAnimation(memoryBottomRightAnim);

				memoryBar.setVisibility(View.VISIBLE);
				memoryBar.startAnimation(barFadeInAnim);
			}
		});

		memoryBottomRightAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				rl_hit_lock_card.setVisibility(View.GONE);

				tv_tuto_mainhit_second_card_left.setAlpha(1.0f);
				tv_tuto_mainhit_second_card_right.setAlpha(1.0f);
				iv_tuto_mainhit_second_card_center.setAlpha(1.0f);

				ll_hit_start_forward.setVisibility(View.VISIBLE);
				hit_third_card_in.setStartOffset(1000);
				ll_hit_start_forward.startAnimation(hit_third_card_in);
			}
		});

		hitIconAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				hitIconLayout.setVisibility(View.VISIBLE);
				hitTextLayout.setVisibility(View.VISIBLE);
				hitTextLayout.setAnimation(hitTextLayoutAnim);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {}
		});


		hitTextLayoutAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				hitTextView.setVisibility(View.VISIBLE);
				hitTextView.setAnimation(hitTextViewAnim);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				hitTopLayout.setBackgroundResource(R.drawable.qwest_card_crop_top);
				hitTopLayout.setPadding(getDpToPixel(thisActivity, 14), getDpToPixel(thisActivity, 20), getDpToPixel(thisActivity, 14), getDpToPixel(thisActivity, 20));

				hitBottomLayout.setVisibility(View.VISIBLE);
				hitBottomLayout.setAnimation(hitBottomAnim);
			}
		});

		hitBottomAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				sc_tuto_feedback.fullScroll(View.FOCUS_DOWN);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
//						Intent intent = new Intent(MainActivity.this, MainTutoLastPopup.class);
//						//						overridePendingTransition(R.anim.slide_up_bottom, R.anim.slide_up_top);
//						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//						startActivity(intent);

						pb_splash_tuto.setVisibility(View.VISIBLE);
						Log.d("firstStart",  "LastPopup " + mpreferences.getString("firststart", "0"));

						new RetrofitService().getUseAppInfoService().retroUpdateTutorialFinished(db.getStudentId())
								.enqueue(new Callback<UseAppInfoData>() {
									@Override
									public void onResponse(Response<UseAppInfoData> response, Retrofit retrofit) {
										Log.e("check_activity", "MainTutoLastPopup Response");
										// TODO Auto-generated method stub
										db.insertTutorial(true);
//								overridePendingTransition(R.anim.tutorial_activity_in, R.anim.tutorial_activity_in);
										Intent intent = new Intent(MainActivity.this, com.ihateflyingbugs.hsmd.tutorial.MainActivity.class);
//								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(intent);

										handler.post(new Runnable() {

											@Override
											public void run() {
												// TODO Auto-generated method stub
												pb_splash_tuto.setVisibility(View.GONE);
												finish();
											}
										});
									}

									@Override
									public void onFailure(Throwable t) {
										Log.e(TAG, "onFailure : "+t.toString());
										handler.post(new Runnable() {

											@Override
											public void run() {
												// TODO Auto-generated method stub
												pb_splash_tuto.setVisibility(View.GONE);
											}
										});
										if(isFinish){
											Toast.makeText(getApplicationContext(), "다시 한번 확인버튼을 눌러주세요.", Toast.LENGTH_SHORT).show();
											isFinish = true;
										}else{
											Log.e("check_activity", "MainTutoLastPopup Exception Else");
											db.insertTutorial(true);
//									overridePendingTransition(R.anim.tutorial_activity_in, R.anim.tutorial_activity_in);
											Intent intent = new Intent(MainActivity.this, com.ihateflyingbugs.hsmd.tutorial.MainActivity.class);
//									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											startActivity(intent);
											isFinish = false;

											finish();
										}
									}
								});




						finish();
					}
				}, 1500);

			}
		});

		hitTextViewAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				barFadeInAnim.setDuration(42);
				barFadeInAnim.setStartOffset(83);

				hitBottomTextAnim.setDuration(42);
				hitBottomTextAnim.setStartOffset(167);

				hitBottomLeftTextAnim.setDuration(125);
				hitBottomLeftTextAnim.setStartOffset(417);

				hitBottomCurrentMemorizeAnim.setDuration(125);
				hitBottomCurrentMemorizeAnim.setStartOffset(625);

				hitBar.setVisibility(View.VISIBLE);
				hitBar.startAnimation(barFadeInAnim);

				hitBottomProgressLayout.setVisibility(View.VISIBLE);
				hitBottomProgressLayout.startAnimation(hitBottomProgressAnim);

				hitBottomLeftText.setVisibility(View.VISIBLE);
				hitBottomLeftText.startAnimation(hitBottomLeftTextAnim);

				hitBottomCurrentMomorize.setVisibility(View.VISIBLE);
				hitBottomCurrentMomorize.startAnimation(hitBottomCurrentMemorizeAnim);

				hitBottomTextLayout.setVisibility(View.VISIBLE);
				hitBottomTextLayout.startAnimation(hitBottomTextAnim);
			}
		});




		hit_third_card_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				iv_tuto_hit_bounce.startAnimation(ic_bounce);
				iv_tuto_hit_bounce.bringToFront();
				iv_tuto_hit_bounce.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {}
		});

		memory_third_card_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				iv_tuto_memory_bounce.startAnimation(ic_bounce);
				iv_tuto_memory_bounce.bringToFront();
				iv_tuto_memory_bounce.setVisibility(View.VISIBLE);

			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {}
		});

		will_third_card_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				rl_memory_lock_card.startAnimation(lockCardAnim);
				rl_hit_lock_card.startAnimation(lockCardAnim);
				iv_memory_lock.startAnimation(lockIconAnim);
				iv_hit_lock.startAnimation(lockIconAnim);


				iv_tuto_will_bounce.startAnimation(ic_bounce);
				iv_tuto_will_bounce.bringToFront();
				iv_tuto_will_bounce.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				ll_tuto_mainwill_first_card.setVisibility(View.GONE);
				//				ll_tuto_mainwill_second_card.setVisibility(View.GONE);

				rl_memory_lock_card.setVisibility(View.VISIBLE);
				rl_hit_lock_card.setVisibility(View.VISIBLE);

				tv_tuto_mainmemory_second_card_left.setAlpha(0.20f);
				tv_tuto_mainmemory_second_card_right.setAlpha(0.20f);
				iv_tuto_mainmemory_second_card_center.setAlpha(0.20f);
				tv_tuto_mainhit_second_card_left.setAlpha(0.20f);
				tv_tuto_mainhit_second_card_right.setAlpha(0.20f);
				iv_tuto_mainhit_second_card_center.setAlpha(0.20f);
			}
		});

		hit_second_card_right_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				ll_will_start_forward.setVisibility(View.VISIBLE);
				will_third_card_in.setStartOffset(1000);
				ll_will_start_forward.startAnimation(will_third_card_in);
			}
		});

		hit_second_card_left_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				tv_tuto_mainhit_second_card_right.setVisibility(View.VISIBLE);
				tv_tuto_mainhit_second_card_right.startAnimation(hit_second_card_right_in);
			}
		});

		hit_second_card_icon_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				tv_tuto_mainhit_second_card_left.setVisibility(View.VISIBLE);
				tv_tuto_mainhit_second_card_left.startAnimation(hit_second_card_left_in);
			}
		});

		hit_first_card_text_out.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				ll_tuto_mainhit_first_card.setVisibility(View.GONE);
				tv_tuto_mainhit_first_card_text.setVisibility(View.GONE);
				iv_tuto_mainhit_second_card_center.setVisibility(View.VISIBLE);
				iv_tuto_mainhit_second_card_center.startAnimation(hit_second_card_icon_in);
			}
		});

		hit_first_card_text_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				ll_tuto_mainmemory_second_card.setVisibility(View.VISIBLE);
				ll_tuto_mainhit_second_card.setVisibility(View.VISIBLE);
				ll_tuto_mainhit_first_card.startAnimation(hit_first_card_out);
				tv_tuto_mainhit_first_card_text.startAnimation(hit_first_card_text_out);
			}
		});
		hit_first_card_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				tv_tuto_mainhit_first_card_text.setVisibility(View.VISIBLE);
				tv_tuto_mainhit_first_card_text.startAnimation(hit_first_card_text_in);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {}
		});

		memory_second_card_right_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				ll_tuto_mainhit_first_card.setVisibility(View.VISIBLE);
				ll_tuto_mainhit_first_card.startAnimation(hit_first_card_in);
			}
		});

		memory_second_card_left_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				tv_tuto_mainmemory_second_card_right.setVisibility(View.VISIBLE);
				tv_tuto_mainmemory_second_card_right.startAnimation(memory_second_card_right_in);
			}
		});

		memory_second_card_icon_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				tv_tuto_mainmemory_second_card_left.setVisibility(View.VISIBLE);
				tv_tuto_mainmemory_second_card_left.startAnimation(memory_second_card_left_in);
			}
		});

		memory_first_card_text_out.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				ll_tuto_mainmemory_first_card.setVisibility(View.GONE);
				tv_tuto_mainmemory_first_card_text.setVisibility(View.GONE);
				iv_tuto_mainmemory_second_card_center.setVisibility(View.VISIBLE);
				iv_tuto_mainmemory_second_card_center.startAnimation(memory_second_card_icon_in);
			}
		});

		memory_first_card_text_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				ll_tuto_mainmemory_second_card.setVisibility(View.VISIBLE);
				ll_tuto_mainmemory_first_card.startAnimation(memory_first_card_out);
				tv_tuto_mainmemory_first_card_text.startAnimation(memory_first_card_text_out);
			}
		});
		memory_first_card_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				tv_tuto_mainmemory_first_card_text.setVisibility(View.VISIBLE);
				tv_tuto_mainmemory_first_card_text.startAnimation(memory_first_card_text_in);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {}
		});

		will_second_card_right_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				ll_tuto_mainmemory_first_card.setVisibility(View.VISIBLE);
				ll_tuto_mainmemory_first_card.startAnimation(memory_first_card_in);
			}
		});

		will_second_card_left_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				tv_tuto_mainwill_second_card_right.setVisibility(View.VISIBLE);
				tv_tuto_mainwill_second_card_right.startAnimation(will_second_card_right_in);
			}
		});

		will_second_card_icon_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				tv_tuto_mainwill_second_card_left.setVisibility(View.VISIBLE);
				tv_tuto_mainwill_second_card_left.startAnimation(will_second_card_left_in);
			}
		});

		will_first_card_text_out.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				ll_tuto_mainwill_first_card.setVisibility(View.GONE);
				tv_tuto_mainwill_first_card_text.setVisibility(View.GONE);
				iv_tuto_mainwill_second_card_center.setVisibility(View.VISIBLE);
				iv_tuto_mainwill_second_card_center.startAnimation(will_second_card_icon_in);
			}
		});

		will_first_card_text_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				ll_tuto_mainwill_second_card.setVisibility(View.VISIBLE);
				ll_tuto_mainwill_first_card.startAnimation(will_first_card_out);
				tv_tuto_mainwill_first_card_text.startAnimation(will_first_card_text_out);
			}
		});

		will_first_card_in.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				tv_tuto_mainwill_first_card_text.setVisibility(View.VISIBLE);
				tv_tuto_mainwill_first_card_text.startAnimation(will_first_card_text_in);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});

		Log.e(TAG, "onCreate");



	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		mTracker.setScreenName("TutoMainActivity");
//		mTracker.send(new HitBuilders.ScreenViewBuilder().build());

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
		FlurryAgent.logEvent("TutorialBaseActivity", articleParams);

		// your code
	}

	public void onStop() {
		super.onStop();

		FlurryAgent.endTimedEvent("TutorialBaseActivity");
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


	public static int getDpToPixel(Context context, int DP) {
		float px = 0;
		try {
			px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP, context.getResources().getDisplayMetrics());
		} catch (Exception e) {

		}
		return (int) px;
	}
}
