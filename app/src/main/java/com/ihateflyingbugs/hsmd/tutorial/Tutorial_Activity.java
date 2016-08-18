package com.ihateflyingbugs.hsmd.tutorial;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.ihateflyingbugs.hsmd.ViewHolder;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.Mean;
import com.ihateflyingbugs.hsmd.data.Word;
import com.ihateflyingbugs.hsmd.login.MainActivitys;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class Tutorial_Activity extends Activity implements OnScrollListener {

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
	TextView tv_tutorial_title_num;
	ImageView iv_navi2;
	ImageView iv_tutorial_right;
	ImageView iv_navi3;

	Animation fadeInAnimation;
	Animation fadeOutAnimation;
	long[] pattern = { 0, 100, 50, 100 };
	SharedPreferences mPreference;
	boolean isStep[] ={true, false, false, false, false, false};

	Handler handler ;

	Animation anim[]= new Animation[9];
	ImageView iv_nav4;


	//log parameter
	Map<String, String> tutorial1Params = new HashMap<String, String>();
	Map<String, String> step0Params = new HashMap<String, String>();
	Map<String, String> step1Params = new HashMap<String, String>();
	Map<String, String> step2Params = new HashMap<String, String>();
	String step0start;
	String step1start;
	String step2start;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);


		handler = new Handler();
		setContentView(R.layout.fragment_tutorial_word_list);
		listView = (ListView)findViewById(R.id.lv_toturial);
		tv_tutorial_title = (TextView)findViewById(R.id.tv_tutorial_title);
		tv_tutorial_title_num= (TextView)findViewById(R.id.tv_tutorial_title_num);
		iv_navi2 = (ImageView)findViewById(R.id.iv_nav2);
		iv_tutorial_right = (ImageView)findViewById(R.id.iv_tutorial_right);
		iv_navi3 = (ImageView)findViewById(R.id.iv_navi3);

		mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);


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

		fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {

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
				iv_tutorial_right.startAnimation( fadeOutAnimation );
				iv_tutorial_right.setVisibility(View.GONE);

				if(isStep[1]){
					tv_tutorial_title_num.setText("2");
					tv_tutorial_title.setText("모르는 단어는 오른쪽으로 살짝 밀면 뜻이 보입니다 ");
				}else if(isStep[2]){
					tv_tutorial_title_num.setText("3");
					tv_tutorial_title.setText("아는 단어는 왼쪽으로 끝까지 밀어주세요");
				}else if(isStep[3]){

					tv_tutorial_title_num.setText("4");
					tv_tutorial_title.setText("기능 튜토리얼이 모두 끝이 났습니다.\n수고하셨습니다");

					isStep[0]= true;
					isStep[3]= false;
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							SharedPreferences.Editor editor = mPreference.edit();
							editor.putString(MainActivitys.GpreTutorial, "1");
							editor.commit();
							Intent intent = new Intent(Tutorial_Activity.this, Tutorial_Test_Activity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
							finish();
							overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						}
					}, 1000);


//					tv_tutorial_title_num.setText("4");
//					tv_tutorial_title.setText("이제 배운것들을 통해 단어장에서 테스트 해보겠습니다 \n화면을 위로 당겨주세요");
//					iv_navi3.setVisibility(View.GONE);
//					mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>(){
//
//						@Override
//						public void onPullDownToRefresh(
//								PullToRefreshBase<ListView> refreshView) {
//						}
//						@Override
//						public void onPullUpToRefresh(
//								PullToRefreshBase<ListView> refreshView) {
//							Log.e("ssrefresh", "onPullUpToRefresh");
//							if(isStep[3]){
//								new Handler().postDelayed(new Runnable(){
//									@Override
//									public void run() {
//										/*
//										 * java.lang.NullPointerException: Attempt to invoke virtual method
//										 * 'void android.widget.ImageView.setVisibility(int)'
//										 * on a null object reference
//
//										 * 오류 로보여짐 널포인터
//										 */
//										iv_guide2.setVisibility(View.GONE);
//										tv_tutorial_title_num.setText("5");
//										iv_navi2.clearAnimation();
//										mPullToRefreshListView.onRefreshComplete(0, 0, 0);
//										tv_tutorial_title.setText("단어장 불러오기를 눌러주세요");
//										iv_navi2.setVisibility(View.GONE);
//
//										iv_nav4.setVisibility(View.VISIBLE);
//										handler.postDelayed(new Runnable() {
//
//											@Override
//											public void run() {
//												// TODO Auto-generated method stub
//												isStep[3]= false;
//												isStep[4]= true;
//												iv_nav4.startAnimation(anim[0]);
//											}
//										}, 1000);
//									}
//								}, 500);
//							}
//						}
//					});
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









		db = DBPool.getInstance(Tutorial_Activity.this);

		words= new ArrayList<Word>();
		metrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

		vibe = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		relativeWord = (RelativeLayout)findViewById(R.id.rl_test_view);



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

					Intent intent = new Intent(Tutorial_Activity.this, Tutorial_Test_Activity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					finish();
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					isStep[4] = false;
					isStep[0] = true;

				}
			}
		});

		mPullToRefreshListView.setOnScrollListener(Tutorial_Activity.this);
		mPullToRefreshListView.setLayoutParams(listView.getLayoutParams());

		mPullToRefreshListView.setMode(Mode.PULL_FROM_END);
		View view = mPullToRefreshListView.get_footer();

		iv_nav4 = (ImageView)view.findViewById(R.id.iv_nav4);

		mSensorManager = (SensorManager)getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
//		mSensorListener = new ShakeEventListener();
//		mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
//			public void onShake() {
//				Log.e("test_class", "gaksf");
//				if(isStep[3]){
//					if(!words.isEmpty()&&adapter!=null){
//						//어느 순간에 취소하고 싶다면 아래 코드 cancel() 함수를 실행하면 된다.
//						iv_tuto_shake.setVisibility(View.GONE);
//						tv_tutorial_title.setText("끝");
//						isStep[4] = true;
//						isStep[3]=false;
//						iv_check[3].setBackgroundResource(R.drawable.tuto_progress_done);
//
//						vibe.cancel();
//						Collections.shuffle(words);
//						listView.setSelection(0);
//						adapter.notifyDataSetChanged();
//						isListAnimaion = true;
//						//repeat은 -1 무반복
//						vibe.vibrate(pattern, -1);
//						iv_tutorial_right.setVisibility(View.VISIBLE);
//						iv_tutorial_right.startAnimation(fadeInAnimation);
//						test_word_add(2);
//
//
//						iv_navi2.setVisibility(View.VISIBLE);
//						iv_navi2.startAnimation(anim[4]);
//						iv_navi2.bringToFront();
//
//						//						tv_tutorial_title.setAnimation(new AnimationUtils().loadAnimation(getApplicationContext(),
//						//								R.anim.slide_out_to_top));
//
//						//asdfasdfasdfasdfasdfasdfasdf
//						//asdfasdfasdfasdfasdfasd
//
//					}else{
//						Toast.makeText(Tutorial_Activity.this, "준비 안됨", Toast.LENGTH_SHORT).show();
//					}
//				}else{
//					Toast.makeText(Tutorial_Activity.this, "해당스텝을 먼저 진행해 주세요", Toast.LENGTH_SHORT).show();
//				}
//			}
//		});

		relativeWord.addView(mPullToRefreshListView);

		tts_util = new TTS_Util(getApplicationContext());

		test_word_add(1);

	}


	private boolean isFinish;

	@Override
	public void onResume() {
		super.onResume();
		mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_UI);
		isFinish = false;
	}

	@Override
	public void onPause() {
		mSensorManager.unregisterListener(mSensorListener);
		super.onPause();
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

		ViewHolder vh = new ViewHolder();

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

			final ViewHolder vh;
			final View view;

			if (convertView==null) {
				view = vi.inflate(R.layout.test_word_list_row, parent, false);
				setViewHolder(view);
			}
			else if (((ViewHolder)convertView.getTag()).needInflate) {
				view = vi.inflate(R.layout.test_word_list_row, parent, false);
				setViewHolder(view);
			}
			else {
				view = convertView;
			}

			final Word word = items.get(position);

			vh = (ViewHolder)view.getTag();

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
				iv_navi3.setVisibility(View.VISIBLE);
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
			if(word.getState()>0 && isWrongContinueShow)
			{

				vh.tvUnknownCount.setBackgroundResource(R.drawable.main_cell_know);
				vh.tvUnknownCount.setVisibility(View.VISIBLE);

			}else if(word.getState()<0 && isWrongContinueShow){
				vh.tvUnknownCount.setBackgroundResource(R.drawable.main_cell_forget);
				vh.tvUnknownCount.setVisibility(View.VISIBLE);
			}
			else
			{
				vh.linearForward.setBackgroundColor(Color.WHITE);
				vh.tvUnknownCount.setVisibility(View.INVISIBLE);
			}

			vh.linearForward.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!isStep[0]){	//listen the words
						if(isStep[1]){
							FlurryAgent.logEvent("BasicTutorialActivity:Fail_AnotherStep", true);
						}else if(isStep[2]){
							FlurryAgent.logEvent("BasicTutorialActivity:Fail_AnotherStep", true);
						}
						return;
					}
					Animation animation = AnimationUtils.loadAnimation(Tutorial_Activity.this, R.anim.zoom_out);
					vh.tvForward.startAnimation(animation);
					AudioManager audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);

					step0Params.put("End", date.get_currentTime());
					step0Params.put("Duration", ""+(System.currentTimeMillis()-Long.valueOf(step0start))/1000);
					FlurryAgent.endTimedEvent("BasicTutorialActivity:ListenToWord_Start");
					FlurryAgent.logEvent("BasicTutorialActivity:ListenToWord_End", step0Params);

					switch(audioManager.getRingerMode()){
						case AudioManager.RINGER_MODE_VIBRATE:
							// 진동
							new AlertDialog.Builder(Tutorial_Activity.this)
									.setMessage("진동 모드를 해제하시면 발음을 들으실 수 있습니다.")
									.setPositiveButton("확인", new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub

											isStep[0]= false;
											vibe.vibrate(pattern, -1);
											handler.post(new Runnable() {

												@Override
												public void run() {
													// TODO Auto-generated method stub
													anim[0].cancel();
													iv_tutorial_right.setVisibility(View.VISIBLE);
													iv_tutorial_right.startAnimation(fadeInAnimation);

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
																	if(isStep[1]){
																		tv_tutorial_title_num.setText("2");
																		tv_tutorial_title.setText("모르는 단어는 오른쪽으로 살짝 밀면 뜻이 보입니다 ");
																	}
																	notifyDataSetChanged();
																	Log.e("test_count",""+isListAnimaion);
																	iv_guide3 = (ImageView)findViewById(R.id.iv_guide3);
																	iv_guide3.setVisibility(View.VISIBLE);
																	iv_guide3.bringToFront();
																}
															});
														}
													}, 1000);
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
									iv_tutorial_right.setVisibility(View.VISIBLE);
									iv_tutorial_right.startAnimation(fadeInAnimation);

									isStep[0]= false;
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
													if(isStep[1]){
														tv_tutorial_title_num.setText("2");
														tv_tutorial_title.setText("모르는 단어는 오른쪽으로 살짝 밀면 뜻이 보입니다 ");
													}
													notifyDataSetChanged();
													Log.e("test_count",""+isListAnimaion);
													iv_guide3 = (ImageView)findViewById(R.id.iv_guide3);
													iv_guide3.setVisibility(View.VISIBLE);
													iv_guide3.bringToFront();
												}
											});
										}
									}, 1000);
								}
							}else{
								Toast.makeText(Tutorial_Activity.this, "잠시후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
							}
							break;
						case AudioManager.RINGER_MODE_SILENT:
							// 무음
							new AlertDialog.Builder(Tutorial_Activity.this)
									.setMessage("음소거 모드를 해제하시면 발음을 들으실 수 있습니다.")
									.setPositiveButton("확인", new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub

											isStep[0]= false;
											vibe.vibrate(pattern, -1);
											handler.post(new Runnable() {

												@Override
												public void run() {
													// TODO Auto-generated method stub
													anim[0].cancel();
													iv_tutorial_right.setVisibility(View.VISIBLE);
													iv_tutorial_right.startAnimation(fadeInAnimation);

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
																	if(isStep[1]){
																		tv_tutorial_title_num.setText("2");
																		tv_tutorial_title.setText("모르는 단어는 오른쪽으로 살짝 밀면 뜻이 보입니다 ");
																	}
																	notifyDataSetChanged();
																	Log.e("test_count",""+isListAnimaion);
																	iv_guide3 = (ImageView)findViewById(R.id.iv_guide3);
																	iv_guide3.setVisibility(View.VISIBLE);
																	iv_guide3.bringToFront();
																}
															});
														}
													}, 1000);
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
								public boolean canDismiss(View v, Object token) {

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
								public void onLeftDismiss(View v, Object token,
														  boolean flag) {
									// TODO Auto-generated method stub
									flag_touch = false;

								}


								@Override
								public void onRightDismiss(View v, Object token,
														   boolean flag) {
									// TODO Auto-generated method stub

									flag_touch = false;
									if(isStep[2]){

										step2Params.put("End", date.get_currentTime());
										step2Params.put("Duration", ""+(System.currentTimeMillis()-Long.valueOf(step2start))/1000);
										FlurryAgent.endTimedEvent("BasicTutorialActivity:KnownWord_Start");
										FlurryAgent.logEvent("BasicTutorialActivity:KnownWord_End", step2Params);

										isStep[2]=false;
										iv_guide4.setVisibility(View.GONE);
										iv_guide3.setVisibility(View.GONE);
										vibe.vibrate(pattern, -1);
										anim[5].cancel();
										anim[1].cancel();
										iv_navi3.clearAnimation();
										iv_navi3.setVisibility(View.GONE);
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
															tv_tutorial_title_num.setText("4");
															tv_tutorial_title.setText("기능 튜토리얼이 모두 끝이 났습니다.\n수고하셨습니다");
														}
														iv_tutorial_right.setVisibility(View.VISIBLE);
														iv_tutorial_right.startAnimation(fadeInAnimation);
														isStep[3] = true;

														Log.e("test_count",""+isListAnimaion);



													}
												});
											}
										}, 1000);
									}else if(isStep[0]){
										FlurryAgent.logEvent("BasicTutorialActivity:Fail_AnotherStep");
									}else if(isStep[1]){
										FlurryAgent.logEvent("BasicTutorialActivity:Fail_AnotherStep");
									}

								}


								@Override
								public void onLeftMovement(View v) {
									// TODO Auto-generated method stub

									flag_touch = false;

									if(isStep[1]){

										step1Params.put("End", date.get_currentTime());
										step1Params.put("Duration", ""+(System.currentTimeMillis()-Long.valueOf(step1start))/1000);
										FlurryAgent.endTimedEvent("BasicTutorialActivity:UnknownWord_Start");
										FlurryAgent.logEvent("BasicTutorialActivity:UnknownWord_End", step1Params);

										vibe.vibrate(pattern, -1);

										isStep[1]=false;

										notifyDataSetChanged();
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

												iv_tutorial_right.setVisibility(View.VISIBLE);
												iv_tutorial_right.startAnimation(fadeInAnimation);
												handler.postDelayed(new Runnable() {

													@Override
													public void run() {
														// TODO Auto-generated method stub
														isStep[2] = true;
														if(isStep[2]){
															tv_tutorial_title_num.setText("3");
															tv_tutorial_title.setText("아는 단어는 왼쪽으로 끝까지 밀어주세요");
														}
														notifyDataSetChanged();
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
										FlurryAgent.logEvent("BasicTutorialActivity:Fail_AnotherStep");
									}else if(isStep[2]){
										FlurryAgent.logEvent("BasicTutorialActivity:Fail_AnotherStep");
									}
								}


								@Override
								public void onRightMovement() {
									// TODO Auto-generated method stub
									flag_touch = false;

								}



								@Override
								public void showFlipAnimation(boolean Direction,
															  float deltaX) {
									// TODO Auto-generated method stub

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
					animation = new TranslateAnimation(metrics.widthPixels/2, 0, 0, 0);
					animation.setDuration((position * 20) + 800);
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

			return view;
		}
	}

	private void deleteCell(final View v, final int index) {

		AnimationListener al = new AnimationListener() {


			@Override
			public void onAnimationEnd(Animation arg0) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						words.remove(index);
						ViewHolder vh = (ViewHolder)v.getTag();
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
	public void onScroll(AbsListView view, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	String starttime;
	String startdate;
	Date date = new Date();
	Map<String, String> articleParams ;

	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.setUserId(db.getStudentId());
		articleParams = new HashMap<String, String>();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("BasicTutorialActivity", articleParams);

//		MainActivity.writeLog("[튜토리얼1 시작,Tutorial_Activity,1]\r\n");
	}

	@Override
	protected void onStop()
	{
		super.onStop();

		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			// Do whatever
			articleParams.put("WIFI", "On");
		}else{
			articleParams.put("WIFI", "Off");
		}

		FlurryAgent.endTimedEvent("BasicTutorialActivity:Start");

		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);

		FlurryAgent.onEndSession(this);

//		MainActivity.writeLog("[튜토리얼1 끝,Tutorial_Activity, 1,{Start:"+tutorial1Params.get("Start")+",End:"+tutorial1Params.get("End")+"}]\r\n");

	}
	private long mLastBackPressedTime = 0;
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Calendar now = Calendar.getInstance();
		long nowTime = now.getTimeInMillis();

		Toast.makeText(getApplicationContext(), "뒤로버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
		if(nowTime - mLastBackPressedTime <= 2000){
			isFinish = true;
//			moveTaskToBack(true);
//			handler.postDelayed(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					if(isFinish){
			finish();
//					}
//				}
//			},15000);
		}
		mLastBackPressedTime = nowTime;
	}
}
