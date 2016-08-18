package com.ihateflyingbugs.hsmd.feedback;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.SwipeDismissTouchListener;
import com.ihateflyingbugs.hsmd.TTS_Util;
import com.ihateflyingbugs.hsmd.ViewHolder;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.Word;
import com.ihateflyingbugs.hsmd.flipimageview.FlipImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Known_Activity extends Activity{
	String TAG = "Known_Activity";

	Context mContext;
	Handler handler;
	DBPool db;
	DisplayMetrics metrics;
	static Vibrator vibe;


	ListView listView;
	static ArrayList<Word> words;
	ListAdapter adapter;

	TTS_Util tts_util;

	boolean flag_set_swipe_mode= true;
	boolean isListAnimaion = true;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_fragment_word_list);

		setActionBar();


		mContext = getApplicationContext();
		handler = new Handler();
		db = DBPool.getInstance(this);
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		vibe = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		tts_util = new TTS_Util(mContext);
		words = new ArrayList<Word>();

		listView = (ListView)findViewById(R.id.test_listView1);
		listView.setDividerHeight(1);


		refresh();
	}


	private void refresh()
	{
		words.clear();
		words = db.wordsWithKnown();

		isListAnimaion = true;
		flag_set_swipe_mode = false;

		adapter = new ListAdapter(mContext, R.layout.word_list_row, words);
		listView.setAdapter(adapter);
	}


	float moved_percent;
	Animation mStateAnim;
	Animation mForgetAnim;



	private void setViewHolder(View view) {
		final int NORMAL_KNOWN_COLOR = Color.rgb(0x75, 0xc9, 0x6d);

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

		vh.linearForward.setVisibility(View.VISIBLE);
		vh.tvForward =(TextView)view.findViewById(R.id.tvForward);
		vh.tvKnownWord =(TextView)view.findViewById(R.id.tvKnownWord);
		vh.tvUnknownWord =(TextView)view.findViewById(R.id.tvUnknownWord);
		vh.tvUnknownCount =(ImageView)view.findViewById(R.id.tvUnknownCount);
		vh.iv_flip_image = (FlipImage)view.findViewById(R.id.iv_flip_image);
		vh.tvUnknownCount1 = (ImageView)view.findViewById(R.id.tvUnknownCount1);
		vh.iv_enough_study=(ImageView)view.findViewById(R.id.iv_enough_study);

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

		vh.tv_forth_mean=(TextView)view.findViewById(R.id.tv_forth_mean);


		vh.linearKnown.setBackgroundColor(NORMAL_KNOWN_COLOR);

		vh.needInflate = false;
		view.setTag(vh);
	}
	private class ListAdapter extends ArrayAdapter<Word>{
		LayoutInflater vi;
		private ArrayList<Word> items;

		public ListAdapter(Context context, int resourceId, ArrayList<Word> items){
			super(context, resourceId, items);
			this.items = items;
			this.vi = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			vh.tvUnknownWord.setText(word.getWord());

			vh.linearUnknown.setVisibility(View.GONE);
			vh.iv_flip_image.setVisibility(View.VISIBLE);
			vh.tvUnknownCount.setVisibility(View.INVISIBLE);



			vh.ivKnown.setVisibility(View.VISIBLE);
			vh.iv_flip_image.setImage(word.getState());
			vh.linearForward.setVisibility(View.VISIBLE);
			vh.linearForward.setBackgroundColor(Color.WHITE);
			vh.tvUnknownCount.setVisibility(View.INVISIBLE);


			vh.linearForward.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
					vh.tvForward.startAnimation(animation);
					AudioManager audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);

					switch(audioManager.getRingerMode()){
						case AudioManager.RINGER_MODE_VIBRATE: // 진동
							Toast.makeText(getApplicationContext(), "소리 모드로 전화후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
							break;
						case AudioManager.RINGER_MODE_NORMAL: // 소리
							if(tts_util.tts_check()){
								tts_util.tts_reading(vh.tvForward.getText().toString());
							}
							else{
								Toast.makeText(getApplicationContext(), "재생에 문제가 있습니다. 잠시후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
							}
							break;
						case AudioManager.RINGER_MODE_SILENT: // 무음
							Toast.makeText(getApplicationContext(), "소리 모드로 전화후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
							break;
					}
				}
			});

			vh.linearForward.setOnTouchListener(new SwipeDismissTouchListener(
					vh.linearForward, vh.linearUnknown, vh.linearKnown, vh.iv_wc , flag_set_swipe_mode,
					null,
					new SwipeDismissTouchListener.DismissCallbacks() {

						@Override
						public boolean canDismiss(View v, Object token) {
							vh.ll_first_mean.setVisibility(View.VISIBLE);
							vh.ll_second_mean.setVisibility(View.GONE);
							vh.ll_third_mean.setVisibility(View.GONE);
							vh.ll_forth_mean.setVisibility(View.GONE);

							vh.tv_first_mean_title.setText(">");
							vh.tv_first_mean.setText("밀어서 단어 없애기");

							int text_sp = 17;
							vh.tv_first_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
							vh.tv_first_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
							return true;
						}

						@Override
						public void onLeftDismiss(View v, Object token, boolean flag) {
							int ex_state = word.getState();
							boolean isKnown =false;
							vh.linearForward.setVisibility(View.GONE);

							Map<String, String> wordmap = new HashMap<String, String>();
							wordmap.put("word", word.getWord());
							wordmap.put("State", String.valueOf(ex_state));
							wordmap.put("Score", String.valueOf(word.getScore()));
							FlurryAgent.logEvent("KnownWordListActivity:RemoveKnownWord",wordmap);

							word.increaseWrongCount();

							if(!word.isWrong())
							{
								word.setWrong(true);
								word.setRight(false);
								db.updateRightWrong(false, word.get_id());
							}

							db.insertState0FlagTableElement(word, false);
							db.updateForgettingCurvesByNewInputs(word, Config.MAINWORDBOOK,  false);
							db.insertReviewTimeToGetMemoryCoachMent(word);
							db.insertLevel(word, false);

							Word word_for_write= db.getWord(word.get_id());


							word.setState(word_for_write.getState());

							deleteCell(view, position);

							if (word.getWrongCount() != 0) {
								vh.tvUnknownCount.setVisibility(View.INVISIBLE);
								vh.tvUnknownCount.setBackgroundResource(R.drawable.img_state_forget);

								vh.tvUnknownCount1.setVisibility(View.VISIBLE);
								vh.tvUnknownCount1.setBackgroundResource(R.drawable.img_state_forget_color);
							}
						}


						@Override
						public void onRightDismiss(View v, Object token, boolean flag) {
							return ;
						}

						@Override
						public void onLeftMovement(View v) {
							return ;
						}

						@Override
						public void onRightMovement() {
							return ;
						}

						@Override
						public void showFlipAnimation(boolean Direction, float deltaX) {

						}
					})
			);

			if(isListAnimaion)
			{
				if(position<8){
					Animation animation = null;
					animation = new TranslateAnimation(metrics.widthPixels/2, 0, 0, 0);
					animation.setDuration((position * 20) + 800);
					view.startAnimation(animation);
				}
				else{
					isListAnimaion = false;
				}
			}
			return view;
		}
	}

	private void deleteCell(final View v, final int index) {
		AnimationListener al = new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation arg0) {
				words.remove(index);
				ViewHolder vh = (ViewHolder)v.getTag();
				vh.needInflate = true;
				isListAnimaion = false;
				adapter.notifyDataSetChanged();
			}

			@Override public void onAnimationRepeat(Animation animation) {
			}

			@Override public void onAnimationStart(Animation animation) {
			}

		};
		collapse(v, al);
	}

	private void collapse(final View v, AnimationListener al) {
		final int initialHeight = v.getHeight();
		Animation anim = new Animation() {

			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if (interpolatedTime == 1) {
					v.setVisibility(View.GONE);
				}
				else {
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

		anim.setDuration(400);
		v.startAnimation(anim);
	}

	//
	ActionBar mActionBar;
	View mCustomActionBarView;

	LinearLayout mActionBar_Layout;
	LinearLayout mBack_Layout;
	TextView mTitle_TextView;
	//

	void setActionBar() {
		mActionBar = getActionBar();
		mCustomActionBarView = getLayoutInflater().inflate(R.layout.setting_action_bar, null);
		mActionBar.setCustomView(mCustomActionBarView, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		mActionBar_Layout = (LinearLayout)mCustomActionBarView.findViewById(R.id.layout_actionbar);
		mActionBar_Layout.setBackgroundColor(Color.parseColor("#8cb74b"));

		mBack_Layout = (LinearLayout)mCustomActionBarView.findViewById(R.id.layout_actionbar_back);
		mBack_Layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Known_Activity.this.finish();
			}
		});

		mTitle_TextView = (TextView)mCustomActionBarView.findViewById(R.id.text_actionbar_title);
		mTitle_TextView.setText("외운 단어장");
	}


	String starttime;
	String startdate;
	Date date = new Date();
	Map<String, String> articleParams;

	public void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("Known_Activity", articleParams);
	}

	public void onStop()
	{
		super.onStop();
		FlurryAgent.endTimedEvent("Known_Activity");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
	}
}