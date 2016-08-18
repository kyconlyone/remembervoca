package com.ihateflyingbugs.hsmd;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.internal.LoadingLayout.OnNextClickListener;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.data.MildangDate;
import com.ihateflyingbugs.hsmd.data.Word;
import com.ihateflyingbugs.hsmd.data.WordListArrayAdapter;
import com.ihateflyingbugs.hsmd.data.WordListCallback;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.model.AuthorizationData;
import com.ihateflyingbugs.hsmd.model.StudyInfoData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.ihateflyingbugs.hsmd.service.DBService;
import com.ihateflyingbugs.hsmd.tutorial.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class WordListFragment extends Fragment implements OnScrollListener {
	String TAG = "WordListFragment";

	private String query;
	static final int ANIMATION_DURATION = 400;



	private DisplayMetrics metrics;
	LogDataFile log_file;
	private DBPool mDBPool;

	private static ArrayList<Word> words;

	private int right, wrong, ONCE_WORD_COUNT = Config.ONCE_WORD_COUNT;


	// Word Shuffle
	private SensorManager mSensorManager;
	private ShakeEventListener mSensorListener;

	TTS_Util tts_util;


	private static SharedPreferences settings;

	static Context mContext;
	static Activity mActivity;

	static int Log_count = 0;


	int del_count = 0;
	boolean flag_touch = false;
	boolean flag_scroll = true;
	boolean ShowIndicator = false;


	int mStartPosition;
	int mLastPosition;
	static int level;



	boolean isTutoStart = true;


	public static boolean isInvitedFriend= false;

	static WordListArrayAdapter mWordListAdapter;



	public WordListFragment() {
	}



	private RelativeLayout relativeWord;
	private static PullToRefreshListView mPullToRefreshListView;
	RelativeLayout.LayoutParams mListViewLayoutParams;


	static Vibrator vibe;
	// 시작은 0 이므로 즉시 실행되고 진동 200 milliseconds, 멈춤 500 milliseconds 된다
	long[] pattern = { 0, 100, 50, 100 };

	private Handler handler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_word_list, container, false);

		MainActivity.isShowCard = true;

		settings = getActivity().getSharedPreferences(MainValue.preName, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
		mContext = getActivity().getApplicationContext();

		handler = new Handler();
		metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

		mDBPool = DBPool.getInstance(getActivity().getApplicationContext());
		mDBPool.setActivity(getActivity());
		log_file = new LogDataFile(getActivity().getApplicationContext());

		vibe = (Vibrator) getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

		relativeWord = (RelativeLayout) view.findViewById(R.id.relativeWord);

		mListViewLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		mPullToRefreshListView = new PullToRefreshListView(getActivity());
		mPullToRefreshListView.setOnScrollListener(WordListFragment.this);
		mPullToRefreshListView.setLayoutParams(mListViewLayoutParams);
		mPullToRefreshListView.setMode(Mode.PULL_FROM_END);

		relativeWord.addView(mPullToRefreshListView);


		mPullToRefreshListView.setOnLoadingNextClickListenet(new OnNextClickListener() {

			@Override
			public void onClick() {
				Intent intent = new Intent(getActivity(), DBService.class);
				PendingIntent pintent = PendingIntent.getService(getActivity(), 0, intent, 0);
				if(!isMyServiceRunning(DBService.class)){

					try {
						pintent.send();
					} catch (CanceledException e) {
						e.printStackTrace();
					}
				}
				//del_count = 0;

				sendWordlistLogInfo();

				mDBPool.deleteAllCurrentWord();


				ONCE_WORD_COUNT = Config.ONCE_WORD_COUNT;
				flag_touch = false;
				MainActivity.isShowCard =false;

				MainActivity.sendWorkEachCount(mDBPool);

				refresh();
			}
		});


		mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				mPullToRefreshListView.setShowIndicator(false);

				if (flag_touch) {
					flag_touch = false;
				}

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						FlurryAgent.logEvent("BaseActivity_WordListFragment:PulltoRefresh");
						mPullToRefreshListView.onRefreshComplete(right, wrong, ONCE_WORD_COUNT);
					}
				}, 500);
			}
		});


		mSensorManager = (SensorManager) getActivity().getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
		mSensorListener = new ShakeEventListener();
		mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
			public void onShake() {
				if (!words.isEmpty() && mWordListAdapter != null) {
					vibe.cancel();

					Collections.shuffle(words);

					mWordListAdapter.notifyDataSetChanged();
					mWordListAdapter.setAnimationFlag(true);
					// repeat은 -1 무반복
					vibe.vibrate(pattern, -1);
				} else {
					Toast.makeText(getActivity(), "준비 안됨", Toast.LENGTH_SHORT).show();
				}
			}
		});

		tts_util = new TTS_Util(getActivity().getApplicationContext());

		//refresh();


		return view;
	}


	@Override
	public void onResume() {
		super.onResume();

		mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);

		Log.e("KARAM", TAG + "   onResume");


		try {
			if(mWordListAdapter.getCount()!=0&&mDBPool.getCurrentWords().size()==mWordListAdapter.getCount()){
				Log.e("wordcount", "db : "+ mDBPool.getCurrentWords().size() + "   word : "+words.size());

			}else{
				refresh();
			}
		} catch (NullPointerException e) {
			// TODO: handle exception
			refresh();
		}

	}






	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.v("wordcount", "db : "+ mDBPool.getCurrentWords().size() + "   word : "+words.size());
		words.clear();
		mWordListAdapter.clear();
	}


	public void refresh() {
		if(!isTutoStart){
			isTutoStart = true;
		}

		if(!Config.isNetworkAvailable(VOCAconfig.context)){
			AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
			alertDialog.setTitle("인터넷을 연결할 수 없습니다.");
			alertDialog.setMessage("연결 상태를 확인한 후 다시 시도해 주세요.");
			alertDialog.setIcon(R.drawable.launcher_icon);
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			alertDialog.show();
			return;
		}
		else {
			if (ShowIndicator) {
				mPullToRefreshListView.setShowIndicator(false);
			}

			words = mDBPool.WordArrayOneSet(getActivity());

			if (words.size() == 0) {
				new AlertDialog.Builder(getActivity())
						.setMessage("단어장의 단어를 모두 외우셨습니다.")
						.setPositiveButton("확인", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
								fragmentTransaction.replace(R.id.linearFragment, new FinishStudyFragment()).addToBackStack(null).commit();
							}														// K : 새로운 레이아웃 필요!! 지금은 예전느낌~
						}).show();
			}
			else {
				printList();
			}
		}
	}




	private void printList() {
		for(int i=0; i<words.size(); i++) {
			String mSpecialRate = words.get(i).getmSpecialRate();

			if(mSpecialRate == null) {
				words.get(i).setmSpecialRate(mDBPool.getSpecialRate(words.get(i).get_id()));
			}
		}

		ONCE_WORD_COUNT = Config.ONCE_WORD_COUNT - right - wrong;
		mWordListAdapter = new WordListArrayAdapter(getActivity(), getActivity().getApplicationContext(), words, metrics, new WordListCallback() {

			@Override
			public void refreshWordList() {
				Log.e("KARAM", TAG + "  Callback Refresh()");
				refresh();
			}
		});

		mPullToRefreshListView.setAdapter(mWordListAdapter);
		mWordListAdapter.setAnimationFlag(true);


		String firstStart = settings.getString("firststart","0");
		Log.e("uploadClickNextWordSet", "first_start  " + firstStart);
		if (firstStart.equals("1")) {

			new RetrofitService().getStudyInfoService().retroInsertClickNextWordSet(mDBPool.getStudentId())
					.enqueue(new Callback<StudyInfoData>() {
						@Override
						public void onResponse(Response<StudyInfoData> response, Retrofit retrofit) {

						}

						@Override
						public void onFailure(Throwable t) {

						}
					});

			new RetrofitService().getAuthorizationService().retroCheckavailability(mDBPool.getStudentId(), "14").enqueue(new Callback<AuthorizationData>() {
				@Override
				public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {

					int availability = -2;
					int student_use_state = 0;

					availability = response.body().getAvailability();
					isInvitedFriend = (response.body().getIs_invite_friend()==1?true:false);
					student_use_state = response.body().getStudent_use_state();

					if (availability == 0) {
						final int _availability = availability;
						final int _student_use_state = student_use_state;
						if (isTutoStart) {
							handler.post(new Runnable() {
								@Override
								public void run() {
									MainActivity.startPaymentCard(_student_use_state, _availability, isInvitedFriend);
									isTutoStart = false;
								}
							});
						}
					}
				}

				@Override
				public void onFailure(Throwable t) {
					Log.e("availability", "Exception");
				}
			});

		}
	}


	@Override
	public void onDestroy() {
		tts_util.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		mStartPosition = firstVisibleItem;
		mLastPosition = visibleItemCount;

		return;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
//		if (flag_touch) {
//			flag_touch = false;
//		}
//
//		mWordListAdapter.setTouchMode(flag_touch);
		mWordListAdapter.setAnimationFlag(false);
		return;
	}





	@Override
	public void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(mSensorListener);

		ActivityManager am = (ActivityManager)getActivity().getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> Info = am.getRunningTasks(1);
		ComponentName topActivity = Info.get(0).topActivity;
		String topactivityname = topActivity.getPackageName();

		Log.e("KARAM", TAG + " onPause   " + topactivityname);
	}


	String starttime;
	String startdate;
	MildangDate date = new MildangDate();

	public void onStart() {
		super.onStart();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
	}

	@Override
	public void onStop() {
		super.onStop();
		Map<String, String> articleParams = new HashMap<String, String>();
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", "" + ((Long.valueOf(System.currentTimeMillis()) - Long.valueOf(starttime))) / 1000);

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


	public void sendWordlistLogInfo() {
		Map<String, String> map = new HashMap<String, String>();

		int reviewcount = mDBPool.getMforget();
		if (reviewcount > 0) { // review word
			MainActivity.setActionBar(true);
			String value = settings.getString(MainActivitys.GpreReviewTutorial,
					"0");
			if (value.equals("0")) {

				if (settings.getBoolean(MainValue.GpreReviewFlag, true) == true) {
					startActivity(new Intent(getActivity(),
							ReviewTutorialActivity.class));
				}
			}
			map.put("nxt_set_rvw_wrd", "" + reviewcount); // log : review word
		} else { // new word
			MainActivity.setActionBar(false);
			map.put("nxt_set_rvw_wrd", "0"); // log : new word
		}

		map.put("level", "" + Config.Difficulty); // log : current level
		map.put("!", "[{?->! : " + Config.unknw_to_knw + "}" + // 몰랐다가 외운 단어 ?
				// -> 0
				", {X->?->! : " + Config.new_to_unknw_to_knw + "}" + // 처음 본 모르는
				// 단어를
				// 외웠을 때
				// X ->
				// ? ->
				// 0
				", {X->! : " + Config.new_to_knw + "}" + // 원래 아는 던어 X -> 0
				", {!->?->! : " + Config.knw_to_unknw_to_knw + "}" + // 외웠다가 까먹은
				// 단어 !
				// -> ?
				// -> 0
				", {!->! : " + Config.knw_to_knw + "}]"); // 까먹지 않은 단어 ! -> 0

		Config.wordSetCount++;
		map.put("WordSetCount", "" + (Config.wordSetCount - 1));

		//	if (adapter.getCount() > 0) {
		if(mWordListAdapter.getCount() > 0 ) {
			for (int i = 0; i < mWordListAdapter.getCount(); i++) {
				switch (mWordListAdapter.getItem(i).getState()) {
					case -1: // unknown word
						switch (mWordListAdapter.getItem(i).getExState()) {
							case -1: // ?->?
								Config.unknw_to_unknw++;
								break;
							case 0: // X->?
								Config.new_to_unknw++;
								break;
							default: // !->?
								Config.knw_to_unknw++;
								break;
						}
						break;
					default:// case 0: //new word, X
						Config.new_to_new++;
						break;

				}
			}
		}

		map.put("?", "[{?->? : " + Config.unknw_to_unknw + "}" + ", {X->? : "
				+ Config.new_to_unknw + "}" + ", {!->? : "
				+ Config.knw_to_unknw + "}]");

		map.put("X", "[{X : " + Config.new_to_new + "}]");
		// Log.e("log test","?->!")
		FlurryAgent.logEvent("BaseActivity_WordListFragment:NextWordSet", map);
		Config.unknw_to_knw = 0;
		Config.new_to_unknw_to_knw = 0;
		Config.new_to_knw = 0;
		Config.knw_to_unknw_to_knw = 0;
		Config.knw_to_knw = 0;
		Config.unknw_to_unknw = 0;
		Config.new_to_unknw = 0;
		Config.knw_to_unknw = 0;

		Config.new_to_new = 0;
	}


	public static void clearWordList() {
		mWordListAdapter.clear();
		ArrayList<Word> wordss = new ArrayList<Word>();
		DBPool dbpool = DBPool.getInstance(mContext);
		dbpool.deleteAllCurrentWord();
		wordss = dbpool.wordsWithScore();
		wordss.addAll(dbpool.wordsWithUnknown());
		level = Config.Difficulty;

		int wordsCount = wordss.size();
		int restCount = Config.ONCE_WORD_COUNT - wordsCount;
		LinkedHashSet<Word> hs = new LinkedHashSet<Word>();
		hs.addAll(wordss);

		wordss.clear();
		wordss.addAll(hs);

		if (restCount > 0) {
			// 여기서 레벨 을 체크하면된다 기획이 조금 필요하긴하지용~ㅎㅎㅎ
			int p80Count = restCount;
			ArrayList<Word> temp;
			int tempCount;
			do {
				temp = dbpool.wordsWithLevel(level, p80Count);

				tempCount = temp.size();
				wordss.addAll(temp);
				p80Count = p80Count - tempCount;

				if (p80Count > 0) {
					if (level < Config.MAX_DIFFICULTY) {
						Log.e("leveltest", "현재 토픽     :" + Config.WORD_TOPIC
								+ "    현재 레벨    : " + level
								+ "     현재 MAX      :" + Config.MAX_DIFFICULTY);
						// 다음 등급 제시 어떻게 ?
						new AlertDialog.Builder(mContext)
								.setMessage(
										"해당 등급의 단어를 모두 외우셨습니다. 다음 등급으로 넘어감니다")
								.setPositiveButton("확인", null).show();

						dbpool.insertTrueCount(level);
						level++;
						Config.Difficulty = level;
						dbpool.insertWordLevel(Config.Difficulty);
					} else {
						break;
					}
				} else
					break;
			} while (p80Count > 0);

			// }
		}

		Log.d("getword", "words = " + wordss.size());
		while (wordss.size() > Config.ONCE_WORD_COUNT) {

			wordss.remove(wordss.size() - 1);
		}
		// words = db.wordsWithScore();
		int i = 1;

		for (Word word : wordss) {
			dbpool.insertCurrentWord(word, i);
			wordss.get(i - 1).setExState(wordss.get(i - 1).getState());
			i++;
		}
		mWordListAdapter.addAll(wordss);
	}

	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				Log.d("SERVISESEES", service.service.getClassName() + ", running");
				return true;
			}
		}
		return false;
	}
}