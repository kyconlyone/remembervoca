package com.ihateflyingbugs.hsmd.tutorial;

import android.app.ActionBar;
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
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.Get_my_uuid;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class Tutorial_Test_Activity extends Activity implements OnScrollListener {
	public static final int MODE_NORMAL_SCORE_LIST = 0;
	public static final int MODE_NORMAL_KNOWN_LIST = 1;
	public static final int MODE_EXAM_KNOWN_LIST = 2;
	public static final int MODE_EXAM_SCORE_LIST = 3;

	public static int mode;
	private boolean flag_set_swipe_mode= true;
	private String query;
	static final int ANIMATION_DURATION = 400;
	private DisplayMetrics metrics;

	private DBPool db;
	private static ArrayList<Word> words;
	private ListAdapter adapter;
	private ListView listView;
	private SensorManager mSensorManager;
	private ShakeEventListener mSensorListener;

	TextView tv_tuto_startpopup;
	TTS_Util tts_util;

	Handler handler ;

	public Tutorial_Test_Activity(){}


	private RelativeLayout relativeWord;
	//private PullToRefreshListView mPullToRefreshListView;
	private boolean isListAnimaion = true;
	private boolean isStartAnimaion = true;
	static Vibrator vibe;
	// 시작은 0 이므로 즉시 실행되고 진동 200 milliseconds, 멈춤 500 milliseconds 된다
	long[] pattern = { 0, 100, 50, 100 };
	private Animation slide_out_top;
	private Animation slide_in_top;


	private ActionBar bar;
	private View actionbar_main;

	ProgressBar pg_tuto;
	private SharedPreferences mPreference;

	private TextView tv_test_feedback;
	RelativeLayout ll_test_fb_notice;
	ScrollView sv_test_fb;
	ImageView iv_test_fb_right;
	Button bt_finish_test;

	boolean finish_test;

	String str_Feedback = "";
	List<String> List_Feedback;
	boolean isAnim= false;


	Map<String, String> tutorial2Params = new HashMap<String, String>();
	//Map<String, String> listenParams = new HashMap<String, String>();
	//Map<String, String> onRightParams = new HashMap<String, String>();
	//Map<String, String> onLeftParams = new HashMap<String, String>();

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);


		handler = new Handler();
		setContentView(R.layout.fragment_tutorial_study_word_list);
		metrics = new DisplayMetrics();
		db = DBPool.getInstance(this);
		vibe = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		words = new ArrayList<Word>();

		sv_test_fb=  (ScrollView)findViewById(R.id.sv_test_fb);

		relativeWord = (RelativeLayout)findViewById(R.id.rl_test_view);
		ll_test_fb_notice = (RelativeLayout)findViewById(R.id.ll_test_fb_notice);
		listView = (ListView)findViewById(R.id.lv_test_toturial);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnScrollListener(Tutorial_Test_Activity.this);
		tv_tuto_startpopup = (TextView)findViewById(R.id.tv_tuto_study_title);
		tv_test_feedback = (TextView)findViewById(R.id.tv_test_feedback);

		iv_test_fb_right = (ImageView)findViewById(R.id.iv_test_fb_right);
		List_Feedback = new ArrayList<String>();
		slide_out_top = new AnimationUtils().loadAnimation(getApplicationContext(), R.anim.slide_out_to_top);
		slide_in_top = new AnimationUtils().loadAnimation(getApplicationContext(), R.anim.slide_in_to_top);
		pg_tuto = (ProgressBar)findViewById(R.id.pg_tuto);
		bt_finish_test = (Button)findViewById(R.id.bt_finish_test);
		mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);

		new AlertDialog.Builder(Tutorial_Test_Activity.this)
				.setMessage("지금까지 배운 밀당 영단어의\n기능을 이용해 5개의 단어를\n모두 외워주세요!")
				.setPositiveButton("확인", null).show();
		bt_finish_test.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String text = null;
				if(words.size()==0){
					new AlertDialog.Builder(Tutorial_Test_Activity.this)
							.setMessage("이제 밀당 영단어의 모든 기능을 마스터하셨습니다")
							.setPositiveButton("확인", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									adapter.clear();
									pg_tuto.setVisibility(View.VISIBLE);
									listView.setVisibility(View.GONE);
									pg_tuto.bringToFront();
									handler.postDelayed(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub

											FlurryAgent.logEvent("LevelTestTutorialActivityActivity:Complete");

											Intent intent = new Intent(Tutorial_Test_Activity.this, MainActivity.class);
											intent.putExtra("tutorial", true);
											startActivity(intent);
											finish();

											overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
										}
									}, 2000);

								}
							}).show();
				}else{
					new AlertDialog.Builder(Tutorial_Test_Activity.this)
							.setMessage("모든 단어를 외워서 왼쪽으로 밀어 없애면 테스트가 종료됩니다.")
							.setPositiveButton("강제 종료", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									adapter.clear();
									pg_tuto.setVisibility(View.VISIBLE);
									listView.setVisibility(View.GONE);
									pg_tuto.bringToFront();
									handler.postDelayed(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub

											FlurryAgent.logEvent("LevelTestTutorialActivity:Skip");

											Intent intent = new Intent(Tutorial_Test_Activity.this, MainActivity.class);
											intent.putExtra("tutorial", true);
											startActivity(intent);
											finish();

											overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
										}
									}, 2000);

								}
							}).setNegativeButton("계속하기", null).show();
				}

			}
		});

		mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);



		slide_in_top.setAnimationListener(new AnimationListener() {

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
				slide_out_top.cancel();
				addTextToTextView(str_Feedback);
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub


						iv_test_fb_right.clearAnimation();
						//iv_test_fb_right.startAnimation(slide_out_top);
						//iv_test_fb_right.setVisibility(View.GONE);

					}
				},1500);
			}
		});
		slide_out_top.setAnimationListener(new AnimationListener() {

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
				isAnim = false;
			}
		});

		int topic = Integer.parseInt(mPreference.getString(MainActivitys.GpreTopic, "1"));

		switch (topic) {
			case 1:
				tv_tuto_startpopup.setText("단어를 외워서 왼쪽으로 밀어 없앨 때마다\n"+mPreference.getString(MainActivitys.GpreName, "이름없음")+"님이 외운 단어가 어떻게 분류되는지 \n이곳에 보여드립니다");
				break;
			case 2:
				tv_tuto_startpopup.setText("단어를 외워서 왼쪽으로 밀어 없앨 때마다\n"+mPreference.getString(MainActivitys.GpreName, "이름없음")+"님이 외운 단어가 어떻게 분류되는지 \n이곳에 보여드립니다");
				break;
			case 3:
				tv_tuto_startpopup.setText("단어를 외워서 왼쪽으로 밀어 없앨 때마다\n"+mPreference.getString(MainActivitys.GpreName, "이름없음")+"님이 외운 단어가 어떻게 분류되는지 \n이곳에 보여드립니다");
				break;
			default:
				break;
		}


		Log.e("getword", "uuid = " + Get_my_uuid.get_Device_id(getApplicationContext()));
		//		mPullToRefreshListView = new PullToRefreshListView(this);
		//		mPullToRefreshListView.setOnLoadingNextClickListenet(new OnNextClickListener(){
		//
		//			@Override
		//			public void onClick() {
		//				//				Log.d("kjw", "WordListFragment Click!!");
		//				Log.e("getword", "click refresh");
		//				pg_tuto.setVisibility(View.VISIBLE);
		//				listView.setVisibility(View.GONE);
		//				pg_tuto.bringToFront();
		//				handler.postDelayed(new Runnable() {
		//
		//					@Override
		//					public void run() {
		//						// TODO Auto-generated method stub
		//						Intent intent = new Intent(Tutorial_Test_Activity.this, Tutorial_Graph_Activity.class);
		//						startActivity(intent);
		//						finish();
		//						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		//					}
		//				}, 2000);
		//
		//				//refresh();
		//			}
		//		});
		//
		//		mPullToRefreshListView.setOnScrollListener(Tutorial_Test_Activity.this);
		//		mPullToRefreshListView.setLayoutParams(listView.getLayoutParams());
		//		mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>(){
		//
		//			@Override
		//			public void onPullDownToRefresh(
		//					PullToRefreshBase<ListView> refreshView) {
		//			}
		//			@Override
		//			public void onPullUpToRefresh(
		//					PullToRefreshBase<ListView> refreshView) {
		//				Log.e("ssrefresh", "onPullUpToRefresh");
		//				new Handler().postDelayed(new Runnable(){
		//					@Override
		//					public void run() {
		//						mPullToRefreshListView.onRefreshComplete(0, 0, 0);
		//					}
		//				}, 500);
		//			}
		//		});
		//		mPullToRefreshListView.setMode(mPullToRefreshListView.getMode() == Mode.BOTH ? Mode.PULL_FROM_START: Mode.BOTH);

		//		mPullToRefreshListView.setMode(Mode.PULL_FROM_END);

		Log.d("kjw", "create refresh !!!!!!!");
		refresh();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		//relativeWord.addView(listView);

		mSensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
		mSensorListener = new ShakeEventListener();
		mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
			public void onShake() {
				if(!words.isEmpty()&&adapter!=null&&!flag_shake){
					//어느 순간에 취소하고 싶다면 아래 코드 cancel() 함수를 실행하면 된다.
					vibe.cancel();
					Collections.shuffle(words);
					listView.setSelection(0);
					adapter.notifyDataSetChanged();
					isListAnimaion = true;
					//repeat은 -1 무반복
					vibe.vibrate(pattern, -1);
				}else{
					Toast.makeText(Tutorial_Test_Activity.this, "준비 안됨", Toast.LENGTH_SHORT).show();
				}
			}
		});
		tts_util = new TTS_Util(getApplicationContext());
	}

	int start;
	int last;
	private void refresh()
	{
		words.clear();

		if(words.isEmpty()){
			words = db.get_test_db(2);
		}

		for(int i=words.size()-1; i>4;i--){
			words.remove(i);
		}
		isListAnimaion = true;
		printList();
	}

	private void printList()
	{

		isListAnimaion =true;
		adapter = new ListAdapter(getApplicationContext(), R.layout.word_list_row, words);
		listView.setAdapter(adapter);

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

		vh.iv_back_del =(ImageView)view.findViewById(R.id.iv_back_del);

		if(mode == MODE_NORMAL_SCORE_LIST || mode == MODE_NORMAL_KNOWN_LIST)
			vh.linearKnown.setBackgroundColor(NORMAL_KNOWN_COLOR);
		else
			vh.linearKnown.setBackgroundColor(EXAM_KNOWN_COLOR);
		vh.needInflate = false;
		view.setTag(vh);
	}

	Integer del_count = 0;
	List<Word> list_del;

	Queue<Word> que;
	Queue<View> list_view;

	boolean flag_touch = false;
	boolean flag_shake = false;

	private class ListAdapter extends ArrayAdapter<Word>{
		LayoutInflater vi;
		private ArrayList<Word> items;
		private boolean isWrongContinueShow;
		Context mContext;
		public ListAdapter(Context context, int resourceId, ArrayList<Word> items){
			super(context, resourceId, items);
			this.items = items;
			this.vi = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if(mode == MODE_NORMAL_SCORE_LIST || mode == MODE_EXAM_SCORE_LIST)
				isWrongContinueShow = true;
			else
				isWrongContinueShow = false;
			mContext= context;
			list_del = new ArrayList<Word>();

			que = new LinkedList<Word>();
			list_view = new LinkedList<View>();
		}


		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			final ViewHolder vh;
			final View view;
			if (convertView==null) {
				view = vi.inflate(R.layout.word_list_row, parent, false);
				setViewHolder(view);
			}
			else if (((ViewHolder)convertView.getTag()).needInflate) {
				view = vi.inflate(R.layout.word_list_row, parent, false);
				setViewHolder(view);
			}
			else {
				view = convertView;
			}
			final Word word = items.get(position);
			vh = (ViewHolder)view.getTag();
			vh.tvForward.setText(word.getWord());
			vh.tvKnownWord.setText(word.getWord());
			//vh.tvKnownMeaning.setText(word.getMeaning());
			vh.tvUnknownWord.setText(word.getWord());

			//			if(word.getMeaning().length()>20){
			//				vh.tvUnknownMeaning.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			//				vh.tvUnknownMeaning.setLineSpacing(4, 1);
			//			}else{
			//				vh.tvUnknownMeaning.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
			//				vh.tvUnknownMeaning.setLineSpacing(2, 1);
			//			}



			//vh.tvUnknownMeaning.setText(Word_split(word.getMeaning()));


			//vh.tvUnknownMeaning.setTextSize(TypedValue.COMPLEX_UNIT_SP, position);
			if(mode == MODE_NORMAL_KNOWN_LIST || mode == MODE_EXAM_KNOWN_LIST)
			{
				vh.ivKnown.setVisibility(View.VISIBLE);
			}
			else
			{
				vh.ivKnown.setVisibility(View.GONE);
			}





			vh.linearForward.setVisibility(View.VISIBLE);
			word.setState(0);
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
					Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
					vh.tvForward.startAnimation(animation);
					AudioManager audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);

					//listenParams.put("word", ""+word.getWord());
					//listenParams.put("time", String.valueOf(System.currentTimeMillis()));
					//FlurryAgent.logEvent("튜토리얼2 단어 듣기,Tutorial_Test_Activity,0", listenParams, true);



					switch(audioManager.getRingerMode()){
						case AudioManager.RINGER_MODE_VIBRATE:
							// 진동
							Toast.makeText(getApplicationContext(), "소리 모드로 전화후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
							break;
						case AudioManager.RINGER_MODE_NORMAL:
							// 소리
							if(tts_util.tts_check()){
								tts_util.tts_reading(vh.tvForward.getText().toString());
							}else{
								Toast.makeText(getApplicationContext(), "잠시후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
							}
							break;
						case AudioManager.RINGER_MODE_SILENT:
							// 무음
							Toast.makeText(getApplicationContext(), "소리 모드로 전화후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
							break;
					}

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

			vh.iv_back_del.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					word.timer.cancel();
					synchronized (del_count) {
						if(flag_set_swipe_mode){
							if(del_count>0){
								del_count--;
								list_del.remove(word);
								que.remove(word);
							}else{
								flag_shake = false;
							}
							word.timer = new Timer();
						}
					}

					Log.e("click_k", "취소되었습니다.");

					vh.linearForward.setVisibility(View.VISIBLE);
					Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_right);
					anim.setDuration(150);
					vh.linearForward.startAnimation(anim);

					int ex_state = items.get(position).getState();
					items.get(position).increaseWrongCount();

					boolean isKnown= false;
					if(!word.isWrong())
					{

						word.setWrong(true);
						word.setRight(false);

					}
					db.updateWordInfo(items.get(position), isKnown);
					db.insertLevel(items.get(position), isKnown);

					//Config.Difficulty = db.calcLevel(10);

					Toast.makeText(mContext, "취소되었습니다.", Toast.LENGTH_SHORT).show();
				}
			});

			vh.linearForward.setOnTouchListener(new SwipeDismissTouchListener(
					vh.linearForward, vh.linearUnknown, vh.linearKnown, vh.iv_wc , flag_set_swipe_mode,
					null,
					new SwipeDismissTouchListener.DismissCallbacks() {
						@Override
						public boolean canDismiss(View v, Object token) {
							if(tv_tuto_startpopup.getVisibility()==View.VISIBLE){
								tv_tuto_startpopup.bringToFront();
								//view_topnavi.bringToFront();
							}

							if(flag_touch){
								return false;
							}else{
								flag_touch = true;
							}

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
											//										vh.ll_first_mean.setVisibility(View.VISIBLE);
											//										vh.tv_first_mean.setText(vh.tv_first_mean.getText()+word.getMean(i).getMeaning()+", ");
											//										vh.ll_known_first_mean.setVisibility(View.VISIBLE);
											//										vh.tv_known_first_mean.setText(vh.tv_known_first_mean.getText()+word.getMean(i).getMeaning()+", ");
											break;
										case Word.Class_V:
											M_V += mean.getMeaning()+", ";
											//										vh.ll_second_mean.setVisibility(View.VISIBLE);
											//										vh.tv_second_mean.setText(vh.tv_second_mean.getText()+word.getMean(i).getMeaning()+", ");
											//										vh.ll_known_second_mean.setVisibility(View.VISIBLE);
											//										vh.tv_known_second_mean.setText(vh.tv_known_second_mean.getText()+word.getMean(i).getMeaning()+", ");
											break;
										case Word.Class_A:
											M_A += mean.getMeaning()+", ";
											//										vh.ll_third_mean.setVisibility(View.VISIBLE);
											//										vh.tv_third_mean.setText(vh.tv_third_mean.getText()+word.getMean(i).getMeaning()+", ");
											//										vh.ll_known_third_mean.setVisibility(View.VISIBLE);
											//										vh.tv_known_third_mean.setText(vh.tv_known_third_mean.getText()+word.getMean(i).getMeaning()+", ");
											break;
										case Word.Class_Ad:
											M_AD += mean.getMeaning()+", ";
											//										vh.ll_third_mean.setVisibility(View.VISIBLE);
											//										vh.tv_third_mean.setText(vh.tv_third_mean.getText()+word.getMean(i).getMeaning()+", ");
											//										vh.ll_known_third_mean.setVisibility(View.VISIBLE);
											//										vh.tv_known_third_mean.setText(vh.tv_known_third_mean.getText()+word.getMean(i).getMeaning()+", ");
											break;
										case Word.Class_Conj:
											M_CONJ += mean.getMeaning()+", ";
											//										vh.ll_forth_mean.setVisibility(View.VISIBLE);
											//										vh.tv_forth_mean.setText(vh.tv_forth_mean.getText()+word.getMean(i).getMeaning()+", ");
											//										vh.ll_known_forth_mean.setVisibility(View.VISIBLE);
											//										vh.tv_known_forth_mean.setText(vh.tv_known_forth_mean.getText()+word.getMean(i).getMeaning()+", ");
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
										vh.tv_known_first_mean_title.setText("n");
										vh.tv_known_first_mean.setText(M_N.substring(0, M_N.length()-2));
										Log.d("test_class", "22222222222222");
										break;
									case Word.Class_V:
										vh.ll_first_mean.setVisibility(View.VISIBLE);
										vh.tv_first_mean_title.setText("v");
										vh.tv_first_mean.setText(M_V.substring(0, M_V.length()-2));
										vh.ll_known_first_mean.setVisibility(View.VISIBLE);
										vh.tv_known_first_mean_title.setText("v");
										vh.tv_known_first_mean.setText(M_V.substring(0, M_V.length()-2));
										break;
									case Word.Class_A:
										vh.ll_first_mean.setVisibility(View.VISIBLE);
										vh.tv_first_mean_title.setText("a");
										vh.tv_first_mean.setText(M_A.substring(0, M_A.length()-2));
										vh.ll_known_first_mean.setVisibility(View.VISIBLE);
										vh.tv_known_first_mean_title.setText("a");
										vh.tv_known_first_mean.setText(M_A.substring(0, M_A.length()-2));
										break;
									case Word.Class_Ad:
										vh.ll_first_mean.setVisibility(View.VISIBLE);
										vh.tv_first_mean_title.setText("ad");
										vh.tv_first_mean.setText(M_AD.substring(0, M_AD.length()-2));
										vh.ll_known_first_mean.setVisibility(View.VISIBLE);
										vh.tv_known_first_mean_title.setText("ad");
										vh.tv_known_first_mean.setText(M_AD.substring(0, M_AD.length()-2));
										break;
									case Word.Class_Conj:
										vh.ll_first_mean.setVisibility(View.VISIBLE);
										vh.tv_first_mean_title.setText("conj");
										vh.tv_first_mean.setText(M_CONJ.substring(0, M_CONJ.length()-2));
										vh.ll_known_first_mean.setVisibility(View.VISIBLE);
										vh.tv_known_first_mean_title.setText("conj");
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
										vh.tv_known_second_mean_title.setText(mclass);
										vh.tv_known_second_mean.setText(multi_mean.substring(0,multi_mean.length()-2));
										mclass = "";
										multi_mean = "";
										line++;
									}else if(line ==1){
										vh.ll_third_mean.setVisibility(View.VISIBLE);
										vh.tv_third_mean_title.setText(mclass);
										vh.tv_third_mean.setText(multi_mean.substring(0,multi_mean.length()-2));
										vh.ll_known_third_mean.setVisibility(View.VISIBLE);
										vh.tv_known_third_mean_title.setText(mclass);
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
							flag_touch = true;
							return true;
						}


						@Override
						public void onLeftDismiss(View v, Object token,
												  boolean flag) {
							// TODO Auto-generated method stub
							int ex_state = items.get(position).getState();
							boolean isKnown =false;

							flag_touch= false;
							vh.linearForward.setVisibility(View.GONE);
							if(!isWrongContinueShow)
							{
								deleteCell(view, word);
								//									words.remove(position);
							}
							word.increaseWrongCount();

							if(!word.isWrong())
							{

								word.setWrong(true);
								word.setRight(false);
								db.updateRightWrong(false, word.get_id());
							}

							db.updateWordInfo(word, isKnown);

							//Config.Difficulty = db.calcLevel(10);
							Word word_for_write= db.getWord(items.get(position).get_id());


							word.setState(word_for_write.getState());


							if(word.getWrongCount()!=0&& isWrongContinueShow){
								//vh.tvUnknownCount.setText(items.get(position).getWrongCount() + "");
								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										vh.tvUnknownCount.setVisibility(View.VISIBLE);
										vh.tvUnknownCount.setBackgroundResource(R.drawable.main_cell_forget);
									}
								});
							}
						}


						@Override
						public void onRightDismiss(final View v, Object token,
												   boolean flag) {
							// TODO Auto-generated method stub

							//사라질때 뭔가 다른방법으로 사라져야됨
							//예를들면 스택에 싸아놨다가 하나씩 없애는 방법으로
							final Word del_word = word;
							flag_touch= false;

							//onRightParams.put("word", ""+word.getWord());
							//onRightParams.put("time", String.valueOf(System.currentTimeMillis()));
							//FlurryAgent.logEvent("튜토리얼2 onRightDismiss,Tutorial_Test_Activity,0", onRightParams);


							vh.linearForward.setVisibility(View.GONE);

							que.offer(word);
							list_view.offer(view);

							synchronized (del_count) {
								if(flag_set_swipe_mode){
									del_count++;
									flag_shake = true;
								}
							}
							del_word.timer = new Timer();

							del_word.timer.schedule(new TimerTask() {

								@Override
								public void run() {


									//							mPullToRefreshListView.setClickable(false);
									handler.post(new Runnable() {

										@Override
										public void run() {
											synchronized (que) {

												if(tv_tuto_startpopup.getVisibility()==View.VISIBLE){
													tv_tuto_startpopup.bringToFront();
													//view_topnavi.bringToFront();
													handler.post(new Runnable() {

														@Override
														public void run() {
															// TODO Auto-generated method stub
															tv_tuto_startpopup.startAnimation(new AnimationUtils().loadAnimation(getApplicationContext(), R.anim.slide_out_to_top));
															tv_tuto_startpopup.setVisibility(View.GONE);
															ll_test_fb_notice.setVisibility(View.VISIBLE);
														}
													});
												}
												//int ex_state = items.get(position).getState();
												boolean isKnown= true;
												Word del_word = que.poll();
												View del_view = list_view.poll();
												//'13.12.24 - 아는단어로 셀삭제시 셀 깜빡거리는거 없앰

												if(del_word.getWrongCount()==0){
													List_Feedback.add("이미 알고 있는 단어_"+del_word.getWord());
													str_Feedback = str_Feedback+"\n이미 알고 있는 단어_"+del_word.getWord();
													Log.e("asdf", ""+del_word.getWord());
													iv_test_fb_right.setImageResource(R.drawable.tuto_test_know);

													iv_test_fb_right.setVisibility(View.VISIBLE);
													if(!isAnim){
														iv_test_fb_right.startAnimation(slide_in_top);
													}
													isAnim = true;
												}else if(del_word.getWrongCount()==1){
													List_Feedback.add("쉽게 외운 단어_"+del_word.getWord());
													iv_test_fb_right.setImageResource(R.drawable.tuto_test_easy);
													str_Feedback = str_Feedback+"\n쉽게 외운 단어_"+del_word.getWord();

													iv_test_fb_right.setVisibility(View.VISIBLE);
													iv_test_fb_right.startAnimation(slide_in_top);
													if(!isAnim){
														iv_test_fb_right.startAnimation(slide_in_top);
													}
													isAnim = true;
												}
												//									else if(word.getWrongCount()>1&&word.getWrongCount()<=4){
												//										iv_test_fb_right.setImageResource(R.drawable.tuto_test_know);
												//										str_Feedback ="외우기 어려워하는 단어_"+word.getWord();
												//										iv_test_fb_right.setVisibility(View.VISIBLE);
												//										iv_test_fb_right.startAnimation(slide_in_top);
												//									}
												else if(del_word.getWrongCount()>1){
													List_Feedback.add("외우기 매우 어려워 하는 단어_"+del_word.getWord());
													iv_test_fb_right.setImageResource(R.drawable.tuto_test_hard);
													str_Feedback = str_Feedback+"\n외우기 매우 어려워 하는 단어_" + del_word.getWord();
													iv_test_fb_right.setVisibility(View.VISIBLE);

													iv_test_fb_right.startAnimation(slide_in_top);
													if(!isAnim){
														iv_test_fb_right.startAnimation(slide_in_top);
													}
													isAnim = true;
												}
												str_Feedback = "";
												for(int i =0; i < List_Feedback.size(); i++){
													if(i  ==List_Feedback.size()-1 ){
														str_Feedback +=List_Feedback.get(i);
													}else{
														str_Feedback +=List_Feedback.get(i)+"\n";
													}
												}
												addTextToTextView(str_Feedback);
												//							vh.linearForward.setEnabled(false);

												Log.e("swipe", "before   isWrong : "+String.valueOf(word.isWrong()) + "isRight : "+String.valueOf(word.isRight()));

												if(!del_word.isRight())
												{

													del_word.setRight(true);
													del_word.setWrong(false);
													//								userDb.deleteCurrentWord(word.get_id());

												}


												if(word.getState()>0&& isWrongContinueShow){
													//vh.tvUnknownCount.setText(items.get(position).getWrongCount() + "");

													// TODO Auto-generated method stub
													vh.tvUnknownCount.setVisibility(View.VISIBLE);
													vh.tvUnknownCount.setBackgroundResource(R.drawable.main_cell_forget);


												}

												deleteCell(view, del_word);
											}
										}
									});
								}
							},100);
						}


						@Override
						public void onLeftMovement(View v) {
							// TODO Auto-generated method stub

							//onLeftParams.put("word", ""+word.getWord());
							//onLeftParams.put("time", String.valueOf(System.currentTimeMillis()));
							//FlurryAgent.logEvent("튜토리얼2 onLeftMovement,Tutorial_Test_Activity,0", onLeftParams);

							if(mode == MODE_EXAM_KNOWN_LIST||mode == MODE_NORMAL_KNOWN_LIST){
								return ;
							}

							flag_touch= false;
							int ex_state = items.get(position).getState();
							items.get(position).increaseWrongCount();
							boolean isKnown= false;
							if(!word.isWrong())
							{
								word.setWrong(true);
								word.setRight(false);
							}

							if(word.getWrongCount()!=0&& isWrongContinueShow){
								//vh.tvUnknownCount.setText(items.get(position).getWrongCount() + "");

								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										vh.tvUnknownCount.setVisibility(View.VISIBLE);
										vh.tvUnknownCount.setBackgroundResource(R.drawable.main_cell_forget);
									}
								});
							}
						}


						@Override
						public void onRightMovement() {
							// TODO Auto-generated method stub
							flag_touch= false;
						}



						@Override
						public void showFlipAnimation(boolean Direction,
													  float deltaX) {
							// TODO Auto-generated method stub

						}
					})
			);
			Log.e("getcount", String.valueOf(start)+ "   "+String.valueOf(last) + "  "+String.valueOf(position));
			if(isListAnimaion)
			{
				if(position<8){
					Animation animation = null;
					animation = new TranslateAnimation(metrics.widthPixels/2, 0, 0, 0);
					animation.setDuration((position * 20) + 800);
					view.startAnimation(animation);
				}else{
					isListAnimaion = false;
				}
			}
			return view;
		}


	}

	public int get_position(String line1, String line2, String line3){

		int position = 0;
		int min=line1.length();
		String[] line = {line1,line2,line3};
		for(int i=0 ; i<line.length; i++){
			if(line[i].length() < min){
				min=line[i].length();
				position = i;
			}
		}

		return position;
	}
	public int get_position(String line1, String line2){
		int position = 0;
		int min=line1.length();
		String[] line = {line1,line2};
		for(int i=0 ; i<line.length; i++){
			if(line[i].length() < min){
				min=line[i].length();
				position = i;
			}
		}
		return position;
	}
	public String Word_split(String means){

		//결과값
		String result_mean = "";

		//단어 쪼갠것
		String[] mean = means.split(", ");

		//각 라인
		String[] line = {"","",""};

		// 단어의 총길이.

		int length=means.length();
		for(int i=0;i<mean.length-1;i++){
			for(int j=0; j< mean.length-1-i;j++){
				if(mean[j].length()>mean[j+1].length()){
					String tmp = mean[j];
					mean[j] = mean[j+1];
					mean[j+1] = tmp;
				}
			}
		}

		if(mean.length==1){
			result_mean = means;
		}else if(length<=10){
			result_mean = means;
		}else if(length>10&&length<=20){
			for(int i=mean.length-1; i>-1;i--){
				line[get_position(line[0], line[1])] += mean[i]+", ";
			}

			result_mean =  line[0].substring(0, line[0].length()-2)+"\n"+line[1].substring(0, line[1].length()-2);
		}else if(length>20){
			for(int i=mean.length-1; i>-1;i--){
				line[get_position(line[0], line[1], line[2])] += mean[i]+", ";
			}

			result_mean =  line[0].substring(0, line[0].length()-2)+"\n"+line[1].substring(0, line[1].length()-2)
					+"\n"+line[2].substring(0, line[2].length()-2);
		}

		return result_mean;
	}

	public static String get_date(){
		String result = "";
		Calendar cal = Calendar.getInstance();

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);

		return result = year + "-" + month + "-" + day + " " + hour + ":" + minute;
	}
	List<Word> del_word = new ArrayList<Word>();
	synchronized private void deleteCell(final View v, final Word word) {
		AnimationListener al = new AnimationListener() {


			@Override
			public void onAnimationEnd(Animation arg0) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(del_count>0){
							synchronized (del_count) {
								del_count--;
							}
						}

						del_word.add(word);
						ViewHolder vh = (ViewHolder)v.getTag();

						vh.needInflate = true;
						isListAnimaion = false;

						synchronized (del_count) {
							if(del_count<=0){
								for(int i=del_word.size()-1; i>=0;i--){
									words.remove(del_word.get(i));
									del_word.remove(i);
								}

								flag_shake = false;
								flag_touch = false;

								if(iv_test_fb_right.getVisibility()==View.VISIBLE){
									handler.postDelayed(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											iv_test_fb_right.setVisibility(View.GONE);
											iv_test_fb_right.startAnimation(slide_out_top);
										}
									}, 1000);

								}
								adapter.notifyDataSetChanged();
							}
						}

						if(adapter.getCount()==0){
							new AlertDialog.Builder(Tutorial_Test_Activity.this)
									.setMessage("이제 밀당 영단어의 모든 기능을 마스터하셨습니다")
									.setPositiveButton("확인", new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											adapter.clear();
											pg_tuto.setVisibility(View.VISIBLE);
											listView.setVisibility(View.GONE);
											pg_tuto.bringToFront();
											handler.postDelayed(new Runnable() {

												@Override
												public void run() {
													// TODO Auto-generated method stub
													FlurryAgent.logEvent("LevelTestTutorialActivity:Complete");
													Intent intent = new Intent(Tutorial_Test_Activity.this, MainActivity.class);
													intent.putExtra("tutorial", true);
													startActivity(intent);
													finish();

													overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
												}
											}, 2000);

										}
									}).show();
						}					}
				});

				//				vh.linearForward.setEnabled(true);
			}

			@Override public void onAnimationRepeat(Animation animation) {}

			@Override public void onAnimationStart(Animation animation) {}

		};
		collapse(v, al);
	}

	synchronized private void collapse(final View v, AnimationListener al) {
		final int initialHeight = v.getHeight();
		//		Log.v("kjw", "real initialHeight = " + initialHeight);
		final Animation anim = new Animation() {

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
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				anim.setDuration(ANIMATION_DURATION);
				v.startAnimation(anim);
			}
		});

	}


	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */


	private void calcLevel()
	{
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

		start = firstVisibleItem;
		last = visibleItemCount;


	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if(flag_touch){
			flag_touch = false;
		}
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

	public void addTextToTextView(String text) {
		tv_test_feedback.setText(text);

		sv_test_fb.post(new Runnable() {
			public void run() {
				sv_test_fb.fullScroll(View.FOCUS_DOWN);
			}
		});
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
		FlurryAgent.logEvent("LevelTestTutorialActivity", articleParams);

//		MainActivity.writeLog("[튜토리얼2 시작,Tutorial_Test_Activity,1]\r\n");
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

		FlurryAgent.endTimedEvent("LevelTestTutorialActivity:Start");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);


		FlurryAgent.onEndSession(this);

//		MainActivity.writeLog("[튜토리얼2 끝,Tutorial_Test_Activity,1,{Start:"+tutorial2Params.get("Start")+",End:"+tutorial2Params.get("End")+"}]\r\n");

	}
	private long mLastBackPressedTime = 0;
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Calendar now = Calendar.getInstance();
		long nowTime = now.getTimeInMillis();

		Toast.makeText(getApplicationContext(), "뒤로버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
		if(nowTime - mLastBackPressedTime <= 2000){
			moveTaskToBack(true);
			isFinish = true;
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