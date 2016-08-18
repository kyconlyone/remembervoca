package com.ihateflyingbugs.hsmd.data;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.SwipeDismissTouchListener;
import com.ihateflyingbugs.hsmd.TTS_Util;
import com.ihateflyingbugs.hsmd.flipimageview.FlipImage;
import com.ihateflyingbugs.hsmd.service.DBService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class WordListArraySet {
	String TAG = "WordListArraySet";

	Activity mActivity;
	Context mContext;

	View view;
	private LayoutInflater mInflater;

	private ArrayList<Word> words;
	WordItemViewHolder mViewHolder = null;

	TTS_Util tts_util;
	DBPool mDBPool;
	Handler handler;
	DisplayMetrics mMetrics;
	SharedPreferences settings;

	String mLocalClassName;

	boolean flag_touch = false;
	boolean flag_animation = true;


	WordListCallback mCallback;

	LinearLayout mWordLayout;

	public WordListArraySet(Activity activity, Context context, ArrayList<Word> items, DisplayMetrics metrics, LinearLayout layout, WordListCallback vc) {

		words = items;

		mInflater = LayoutInflater.from(context);

		mActivity = activity;
		mContext = context;
		mCallback = vc;

		mDBPool = DBPool.getInstance(mContext);
		tts_util = new TTS_Util(mContext);
		handler = new Handler();
		mMetrics = metrics;

		settings = mActivity.getSharedPreferences(Config.PREFS_NAME, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);

		mWordLayout = layout;

		mLocalClassName = mActivity.getLocalClassName();
	}



	public void setInit() {

		LayoutInflater inflater = LayoutInflater.from(mActivity);

		for(int i = 0; i<words.size(); i++) {
			View view = inflater.inflate(R.layout.itemlist_main_word, mWordLayout, false);
			initWordViewRow(view);

			GetView(view, i);
			mWordLayout.addView(view);
		}
	}

	private void initWordViewRow(View view) {
		final int NORMAL_KNOWN_COLOR = Color.rgb(0x00, 0xb5, 0x69);

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

		vh.linearForward.setVisibility(LinearLayout.VISIBLE);
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



		vh.tvUnknownCount1 = (ImageView) view.findViewById(R.id.tvUnknownCount1);
		vh.iv_flip_image = (FlipImage)view.findViewById(R.id.iv_flip_image);

		vh.needInflate = false;
		view.setTag(vh);
	}

	private boolean isWrongContinueShow = true;
	int position;
	View mView;
	String mSpecialRate;

	View GetView(View view, int pos) {
		final WordItemViewHolder vh;
		position = pos;
		mView = view;
		final Word word = words.get(position);

		vh = (WordItemViewHolder) mView.getTag();

		mSpecialRate = word.getmSpecialRate();

		if(mSpecialRate != null) {
			vh.tvForward.setBackgroundResource(R.drawable.img_sell_special);
			vh.tvForward.setTextColor(Color.parseColor("#FFFFFF"));
		}
		else {
			vh.tvForward.setBackground(null);
			vh.tvForward.setTextColor(Color.parseColor("#8a000000"));
		}

		vh.tvForward.setText(word.getWord());
		vh.tvKnownWord.setText(word.getWord());
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

			int mStateImageResourceIdInt = mActivity.getResources().getIdentifier(mStateImageString, "drawable", mActivity.getPackageName());

			vh.tvUnknownCount.setVisibility(View.INVISIBLE);

			if(word.getState() > 10)
				mStateImageString = "img_state_10_color";
			else
				mStateImageString = "img_state_" + word.getState() + "_color";

			mStateImageResourceIdInt = mActivity.getResources().getIdentifier(mStateImageString, "drawable", mActivity.getPackageName());

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

		WordStateAnimation(vh, word, 1000, 1500);


		vh.linearForward.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WordStateAnimation(vh, word, 1000, 1000);

				Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.zoom_out);
				vh.tvForward.startAnimation(animation);

				AudioManager audioManager = (AudioManager)mActivity.getSystemService(Context.AUDIO_SERVICE);

				if(!(audioManager.isWiredHeadsetOn()||audioManager.isBluetoothA2dpOn())){
					switch (audioManager.getRingerMode()) {
						case AudioManager.RINGER_MODE_VIBRATE:	// 진동
							Toast.makeText(mActivity,
									"소리 모드로 전환후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
							break;
						case AudioManager.RINGER_MODE_NORMAL:	// 소리
							if (tts_util.tts_check()) {
								tts_util.tts_reading(vh.tvForward.getText().toString());
							}
							else {
								Toast.makeText(mActivity,
										"잠시후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
							}
							break;
						case AudioManager.RINGER_MODE_SILENT:	// 무음
							Toast.makeText(mActivity,
									"소리 모드로 전환후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
							break;
					}
				}
				else{
					tts_util.tts_reading(vh.tvForward.getText().toString());
				}
			}
		});



		vh.linearForward.setOnTouchListener(new SwipeDismissTouchListener(
				vh.linearForward, vh.linearUnknown, vh.linearKnown, vh.iv_wc, true, null,
				new SwipeDismissTouchListener.DismissCallbacks() {
					@Override
					public boolean canDismiss(View v, Object token) {


						if(word.getMeanList().size()==0){
							word.setMeanList(mDBPool.getMean(word.get_id()));
						}

						if (word.getMeanList().size() != 0) {

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

							Mean mean;

							String M_N = "";
							String M_V = "";
							String M_A = "";
							String M_AD = "";
							String M_CONJ = "";

							for (int i = 0; i < word.getMeanList().size(); i++) {
								mean = word.getMean(i);
								int key = mean.getMClass();

								switch (key) {
									case Word.Class_N: // noun : 1
										M_N += mean.getMeaning() + ", ";
										break;
									case Word.Class_V: // verb : 2
										M_V += mean.getMeaning() + ", ";
										break;
									case Word.Class_A: // adjective : 3
										M_A += mean.getMeaning() + ", ";
										break;
									case Word.Class_Ad: // adverb : 4
										M_AD += mean.getMeaning() + ", ";
										break;
									case Word.Class_Conj: // conjunction : 5
										M_CONJ += mean.getMeaning() + ", ";
										break;
								}
							}

							switch (word.getP_class()) {
								case Word.Class_N:
									vh.ll_first_mean.setVisibility(View.VISIBLE);
									vh.tv_first_mean_title.setText("n");
									vh.tv_first_mean.setText(M_N.substring(0, M_N.length() - 2));
									vh.ll_known_first_mean.setVisibility(View.VISIBLE);
									vh.tv_known_first_mean_title.setText("n");
									vh.tv_known_first_mean.setText(M_N.substring(0, M_N.length() - 2));
									break;
								case Word.Class_V:
									vh.ll_first_mean.setVisibility(View.VISIBLE);
									vh.tv_first_mean_title.setText("v");
									vh.tv_first_mean.setText(M_V.substring(0, M_V.length() - 2));
									vh.ll_known_first_mean.setVisibility(View.VISIBLE);
									vh.tv_known_first_mean_title.setText("v");
									vh.tv_known_first_mean.setText(M_V.substring(0, M_V.length() - 2));
									break;
								case Word.Class_A:
									vh.ll_first_mean.setVisibility(View.VISIBLE);
									vh.tv_first_mean_title.setText("a");
									vh.tv_first_mean.setText(M_A.substring(0, M_A.length() - 2));
									vh.ll_known_first_mean.setVisibility(View.VISIBLE);
									vh.tv_known_first_mean_title.setText("a");
									vh.tv_known_first_mean.setText(M_A.substring(0, M_A.length() - 2));
									break;
								case Word.Class_Ad:
									vh.ll_first_mean.setVisibility(View.VISIBLE);
									vh.tv_first_mean_title.setText("ad");
									vh.tv_first_mean.setText(M_AD.substring(0, M_AD.length() - 2));
									vh.ll_known_first_mean.setVisibility(View.VISIBLE);
									vh.tv_known_first_mean_title.setText("ad");
									vh.tv_known_first_mean.setText(M_AD.substring(0, M_AD.length() - 2));
									break;
								case Word.Class_Conj:
									vh.ll_first_mean.setVisibility(View.VISIBLE);
									vh.tv_first_mean_title.setText("conj");
									vh.tv_first_mean.setText(M_CONJ.substring(0, M_CONJ.length() - 2));
									vh.ll_known_first_mean.setVisibility(View.VISIBLE);
									vh.tv_known_first_mean_title.setText("conj");
									vh.tv_known_first_mean.setText(M_CONJ.substring(0, M_CONJ.length() - 2));
									break;
							}

							HashMap<Integer, Boolean> hm = word.getmClassList();
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
									vh.ll_second_mean.setVisibility(View.VISIBLE);
									vh.tv_second_mean_title.setText(mclass);
									vh.tv_second_mean.setText(multi_mean.substring(0, multi_mean.length() - 2));
									vh.ll_known_second_mean.setVisibility(View.VISIBLE);
									vh.tv_known_second_mean_title.setText(mclass);
									vh.tv_known_second_mean.setText(multi_mean.substring(0, multi_mean.length() - 2));

									mclass = "";
									multi_mean = "";
									line++;
								} else if (line == 1) {
									vh.ll_third_mean.setVisibility(View.VISIBLE);
									vh.tv_third_mean_title.setText(mclass);
									vh.tv_third_mean.setText(multi_mean.substring(0, multi_mean.length() - 2));
									vh.ll_known_third_mean.setVisibility(View.VISIBLE);
									vh.tv_known_third_mean_title.setText(mclass);
									vh.tv_known_third_mean.setText(multi_mean.substring(0, multi_mean.length() - 2));

									mclass = "";
									multi_mean = "";
								}
							}

							int text_sp = 0;

							if (word.getmClassList().size() > 1) {
								text_sp = 15;
							} else if (word.getmClassList().size() == 1) {
								if (vh.tv_first_mean.length() > 15 || vh.tv_second_mean.length() > 15) {
									// 포문으로 글씨를 계속 줄여본다
									text_sp = 15;
								} else {
									text_sp = 17;
								}
							} else {
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

							if (vh.tv_first_mean_title.getText().toString().equals("conj")
									|| vh.tv_first_mean_title.getText().toString().equals("conj")) {
								vh.tv_first_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
								vh.tv_known_first_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
							} else if (vh.tv_second_mean_title.getText().toString().equals("conj")
									|| vh.tv_second_mean_title.getText().toString().equals("conj")) {
								vh.tv_second_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
								vh.tv_known_second_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
							} else if (vh.tv_third_mean_title.getText().toString().equals("conj")
									|| vh.tv_third_mean_title.getText().toString().equals("conj")) {
								vh.tv_third_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
								vh.tv_known_third_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
							}
						} else {	// size 0
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

						return true;
					}

					@Override
					public void onLeftDismiss(View v, Object token,boolean flag) {
					}

					//TODO Right
					@Override
					public void onRightDismiss(final View v, Object token, boolean flag) {
						FlurryAgent.logEvent(TAG + " : onRightDismiss", true);

						vh.linearForward.setVisibility(View.GONE);


						Animation anim = new AnimationUtils().loadAnimation(mActivity.getApplicationContext(), android.R.anim.fade_out);
						anim.setDuration(1500);

						boolean isKnown = true;
						int ex_state;
						try {
							ex_state = word.getState();

							if (!word.isRight()) {
								word.setRight(true);
								word.setWrong(false);
								mDBPool.updateRightWrong(true, word.get_id());
							}

							// insert upper forgetting_curves value
							mDBPool.isMaxState(word.getState());
							mDBPool.insertState0FlagTableElement(word, isKnown);
							mDBPool.updateForgettingCurvesByNewInputs(word,  Config.MAINWORDBOOK, isKnown);
							mDBPool.insertReviewTimeToGetMemoryCoachMent(word);
							mDBPool.insertLevel(word, isKnown);

							Word word_for_write = mDBPool.getWord(word.get_id());
							word.setState(word_for_write.getState());

							int Index = words.indexOf(word);
							Log.e("KARAM", "Index : " + Index + ", Word : " + word.getWord());
							deleteCell(mWordLayout.getChildAt(Index), word, ex_state);

						} catch (IndexOutOfBoundsException e) {
							Toast.makeText(mActivity.getApplicationContext(), "잠시후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
						}
					}

					// TODO Left
					@Override
					public void onLeftMovement(View v) {
						FlurryAgent.logEvent(TAG + " : onLeftMovement", true);

						int ex_state = word.getState();
						word.increaseWrongCount();
						boolean isKnown = false;

						if (!word.isWrong()) {
							word.setWrong(true);
							word.setRight(false);
							mDBPool.updateRightWrong(false, word.get_id());
						}

						// insert state0 flag table element
						mDBPool.insertState0FlagTableElement(word, isKnown);
						// update forgetting curves by 'isknown' of word
						mDBPool.updateForgettingCurvesByNewInputs(word,   Config.MAINWORDBOOK, isKnown);
						// insert review time to get value of memeory coach ment
						mDBPool.insertReviewTimeToGetMemoryCoachMent(word);
						// db.updateWordInfo(items.get(position), isKnown);
						mDBPool.insertLevel(word, isKnown);

						if (ex_state > 0) {
							settings.edit().putInt(MainValue.GpreCheckCurve, settings.getInt(MainValue.GpreCheckCurve, 0) + 1).commit();
						}

						Word word_for_write = mDBPool.getWord(word.get_id());
						word.setState(word_for_write.getState());

						vh.iv_flip_image.setImage(word.getState());
						if (word.getWrongCount() != 0 && isWrongContinueShow) {
							vh.tvUnknownCount.setVisibility(View.INVISIBLE);

							vh.tvUnknownCount1.setVisibility(View.VISIBLE);
							vh.tvUnknownCount1.setBackgroundResource(R.drawable.img_state_forget_color);
						}

						WordForgetAnimation(vh, word);
					}

					@Override
					public void onRightMovement() {
						Log.e(TAG, "Main TouchListener  -> RightMovement");
						vh.iv_flip_image.flip(moved_percent);
					}


					@Override
					public void showFlipAnimation(boolean Direction, float deltaX) {
						if(!Direction){
							float new_percent = Math.abs(deltaX)/(mView.getWidth()/8);
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

		return view;


	}

	float moved_percent;

	Animation mStateAnim;
	Animation mForgetAnim;

	void WordForgetAnimation(WordItemViewHolder viewholder, final Word word) {
		final WordItemViewHolder vh = viewholder;

		mForgetAnim = AnimationUtils.loadAnimation(mActivity, R.anim.anim_forget);
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
		Word mWord = word;

		mStateAnim = new AlphaAnimation(1.0f, 0.0f);

		mStateAnim.setDuration(duration);
		mStateAnim.setStartOffset(starttime);

		final int mWordstate = mWord.getState();
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


	private synchronized void deleteCell(final View v, final Word word,	final int exState) {
		AnimationListener al = new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation arg0) {
				Log.e(TAG, "onAnimation End");

				// exState문제가 왜 일어나는지 도데체가 알수가없다. 확인필요 ->
				// exState가 복습단어에서도 0으로 나오는 문제 해결
				switch (mDBPool.getExState(word.get_id())) {
					case -1: // 몰랐다가 알게된 경우    ? -> 0
						//새로학습한단어
						mDBPool.insertStudyWord(mDBPool.getStudyWord() + 1);
						Config.unknw_to_knw++;
						break;
					case 0: // 처음 보는 단어
						switch (exState) {
							case -1:
								// 처음 본 모르는 단어를 외웠을 때 X -> ? -> 0
								//새로학습한단어
								mDBPool.insertStudyWord(mDBPool.getStudyWord() + 1);
								Config.new_to_unknw_to_knw++;
								break;
							default: // case 0
								// 원래 아는 던어 X -> 0
								//새로학습한단어
								mDBPool.insertStudyWord(mDBPool.getStudyWord() + 1);
								Config.new_to_knw++;
								break;
						}
						break;
					default: // 아는 단어
						switch (exState) {
							case -1:
								// 외웠다가 까먹은 단어 ! -> ? -> 0
								//새로학습한단어
								mDBPool.insertStudyWord(mDBPool.getStudyWord() + 1);
								Config.knw_to_unknw_to_knw++;
								break;
							default:
								// 까먹지 않은 단어 ! -> 0
								// 기억이 연장된단어
								mDBPool.insertReviewWord(mDBPool.getReviewWord() + 1);
								Config.knw_to_knw++;
								break;
						}
						break;
				}

				MildangDate md = new MildangDate();
				mDBPool.putCalendarData(md.get_today());
				mDBPool.deleteCurrentWord(word.get_id());


				WordItemViewHolder vh = (WordItemViewHolder) v.getTag();
				vh.needInflate = true;

				int Index = mWordLayout.indexOfChild(v);
				mWordLayout.removeViewAt(Index);

				words.remove(word);


				//	Log.e(TAG, "End Animation RemoveView Call  And Size  -> " + mWordLayout.getChildCount());
				// 단어체크될때마다 실시간으로 확인함.

				if (words.size() == 0) {
					handler.post(new Runnable() {

						@Override
						public void run() {
							mDBPool.deleteAllCurrentWord();

							mCallback.refreshWordList();

							Intent intent = new Intent(mActivity,
									DBService.class);
							PendingIntent pintent = PendingIntent.getService(
									mActivity, 0, intent, 0);
							try {
								pintent.send();
							} catch (CanceledException e) {
								e.printStackTrace();
							}
						}
					});
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationStart(Animation animation) {
			}
		};
		collapse(v, al);
	}

	private void collapse(final View v, AnimationListener al) {
		Log.e(TAG, "Collapse  ");

		final int initialHeight = v.getHeight();
		Animation anim = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if (interpolatedTime == 1) {
					v.setVisibility(View.GONE);
				} else {
					v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
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

		anim.setDuration(400);
		v.startAnimation(anim);

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
}
