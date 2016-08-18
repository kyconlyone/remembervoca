package com.ihateflyingbugs.hsmd;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.Word;
import com.ihateflyingbugs.hsmd.flipimageview.FlipImage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class Known_Activity extends Activity implements OnScrollListener,OnCheckedChangeListener {

	public static final int MODE_NORMAL_KNOWN_LIST = 1;

	public static int mode;
	private boolean flag_set_swipe_mode= true;
	private String query;
	static final int ANIMATION_DURATION = 400;
	private DisplayMetrics metrics;

	private DBPool db;
	private static ArrayList<Word> words;
	private ListAdapter adapter;
	private ListView listView;

	TTS_Util tts_util;

	Handler handler ;

	public Known_Activity(){}


	private boolean isListAnimaion = true;
	private boolean isStartAnimaion = true;
	static Vibrator vibe;
	// 시작은 0 이므로 즉시 실행되고 진동 200 milliseconds, 멈춤 500 milliseconds 된다
	long[] pattern = { 0, 100, 50, 100 };


	private ActionBar actionBar;
	private View actionBar_View;

	private LinearLayout ll_actionbar;
	private ImageView iv_back;
	private TextView tv_title;
	Context mContext;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setActionBar();

		handler = new Handler();
		setContentView(R.layout.test_fragment_word_list);
		mContext = getApplicationContext();
		metrics = new DisplayMetrics();
		db = DBPool.getInstance(this);
		vibe = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		words = new ArrayList<Word>();

		listView = (ListView)findViewById(R.id.test_listView1);
		listView.setDividerHeight(1);

		Log.d("kjw", "create refresh !!!!!!!");
		refresh();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);


		tts_util = new TTS_Util(getApplicationContext());
	}
	View view_topnavi;




	int start;
	int last;
	private void refresh()
	{
		words.clear();

		listView.setBackgroundResource(R.drawable.list_known_bg);
		words = db.wordsWithKnown();

		isListAnimaion = true;
		flag_set_swipe_mode = false;

		adapter = new ListAdapter(getApplicationContext(), R.layout.word_list_row, words);
		listView.setAdapter(adapter);
		printList();
	}
	private void printList()
	{

		isListAnimaion =true;
		adapter = new ListAdapter(getApplicationContext(), R.layout.word_list_row, words);
		listView.setAdapter(adapter);

	}



	private void setViewHolder(View view) {

		final int NORMAL_KNOWN_COLOR = Color.rgb(0x75, 0xc9, 0x6d);
		final int EXAM_KNOWN_COLOR = Color.rgb(0xee, 0xc0, 0x00);
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


	boolean flag_touch = false;
	float moved_percent;
	Animation mStateAnim;
	Animation mForgetAnim;

	private class ListAdapter extends ArrayAdapter<Word>{
		LayoutInflater vi;
		private ArrayList<Word> items;
		private boolean isWrongContinueShow;
		public ListAdapter(Context context, int resourceId, ArrayList<Word> items){
			super(context, resourceId, items);
			this.items = items;
			this.vi = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			isWrongContinueShow = false;
			mContext= context;
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

			vh.linearUnknown.setVisibility(View.GONE);
			vh.iv_flip_image.setVisibility(View.VISIBLE);
			vh.tvUnknownCount.setVisibility(View.INVISIBLE);

			String mStateImageString;

			if(word.getState() > 10)
				mStateImageString = "img_state_10_color";
			else
				mStateImageString = "img_state_" + word.getState() + "_color";

			int mStateImageResourceIdInt = getResources().getIdentifier(mStateImageString, "drawable", getPackageName());

			vh.tvUnknownCount1.setBackgroundResource(mStateImageResourceIdInt);

			//			if(word.getMeaning().length()>20){
			//				vh.tvUnknownMeaning.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			//				vh.tvUnknownMeaning.setLineSpacing(4, 1);
			//			}else{
			//				vh.tvUnknownMeaning.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
			//				vh.tvUnknownMeaning.setLineSpacing(2, 1);
			//			}



			//vh.tvUnknownMeaning.setText(Word_split(word.getMeaning()));



			//vh.tvUnknownMeaning.setTextSize(TypedValue.COMPLEX_UNIT_SP, position);


			vh.ivKnown.setVisibility(View.VISIBLE);

			vh.iv_flip_image.setImage(word.getState());
			//final Map<String, String> Time_map = new HashMap<String, String>();


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
					Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
					vh.tvForward.startAnimation(animation);
					AudioManager audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);

					//FlurryAgent.logEvent("아는단어장 발음듣기 클릭,Known_Activity,1", true);
					//Time_map.clear();
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
								Toast.makeText(getApplicationContext(), "재생에 문제가 있습니다. 잠시후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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

			vh.linearForward.setOnTouchListener(new SwipeDismissTouchListener(
							vh.linearForward, vh.linearUnknown, vh.linearKnown, vh.iv_wc , flag_set_swipe_mode,
							null,
							new SwipeDismissTouchListener.DismissCallbacks() {

								@Override
								public boolean canDismiss(View v, Object token) {

									//Time_map.put("Start", ""+System.currentTimeMillis());
									//FlurryAgent.logEvent("아는단어장 단어 열기 시작,Known_Activity,1", true);
									vh.ll_second_mean.setVisibility(View.GONE);
									vh.ll_third_mean.setVisibility(View.GONE);
									vh.ll_forth_mean.setVisibility(View.GONE);
									vh.ll_first_mean.setVisibility(View.VISIBLE);

									vh.tv_first_mean.setText("밀어서 단어 없애기");

									vh.tv_first_mean_title.setText(">");

									int text_sp = 17;
									vh.tv_first_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);
									vh.tv_first_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_sp);

									Log.d("test_class", "5");

									return true;
								}


								@Override
								public void onLeftDismiss(View v, Object token,
														  boolean flag) {
									// TODO Auto-generated method stub
									vh.iv_flip_image.setVisibility(View.GONE);
									vh.tvUnknownCount.setVisibility(View.GONE);
									vh.tvUnknownCount1.setVisibility(View.GONE);
									flag_touch = false;
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
									db.updateForgettingCurvesByNewInputs(word,  Config.MAINWORDBOOK, false);
									db.insertReviewTimeToGetMemoryCoachMent(word);
									db.insertLevel(word, false);



							/*
							 * 오류 수정 해야함 인덱스가 일치하질 않는다
							 * 동기화시켜주면 해결할수있는 부분인것 같다.
							 */


									Word word_for_write= db.getWord(word.get_id());


									word.setState(word_for_write.getState());
									if(!isWrongContinueShow)
									{
										//Time_map.put("End", ""+System.currentTimeMillis());
										//Time_map.put("Duration", ""+(Long.valueOf(Time_map.get("End"))-Long.valueOf(Time_map.get("Start"))));
										//FlurryAgent.logEvent("아는단어장 Leftdismiss,Known_Activity,1", Time_map);
										//Time_map.clear();
										deleteCell(view, position);
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

//							if (word.getWrongCount() != 0) {
//								vh.tvUnknownCount.setVisibility(View.INVISIBLE);
//								vh.tvUnknownCount.setBackgroundResource(R.drawable.img_state_forget);
//
//								vh.tvUnknownCount1.setVisibility(View.VISIBLE);
//								vh.tvUnknownCount1.setBackgroundResource(R.drawable.img_state_forget_color);
//							}

									//WordForgetAnimation(vh, word);
								}


								@Override
								public void onRightDismiss(View v, Object token,
														   boolean flag) {
									// TODO Auto-generated method stub

									flag_touch = false;
									//Time_map.put("End", ""+System.currentTimeMillis());
									//Time_map.put("Duration", ""+(Long.valueOf(Time_map.get("End"))-Long.valueOf(Time_map.get("Start"))));
									//FlurryAgent.logEvent("아는단어장 Rightdismiss,Known_Activity,1", Time_map);
									//Time_map.clear();
									return ;
								}


								@Override
								public void onLeftMovement(View v) {
									// TODO Auto-generated method stub
									flag_touch = false;
									flag_touch = false;
									//Time_map.put("End", ""+System.currentTimeMillis());
									//Time_map.put("Duration", ""+(Long.valueOf(Time_map.get("End"))-Long.valueOf(Time_map.get("Start"))));
									//FlurryAgent.logEvent("아는단어장 LeftMovement,Known_Activity,1", Time_map);
									//Time_map.clear();
									return ;


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

							/*if(!Direction){
								float new_percent = Math.abs(deltaX)/(view.getWidth()/8);
								moved_percent = new_percent;
								Log.e("new_percent", ""+moved_percent);

								if(mStateAnim != null) {
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
							}*/

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

	void WordForgetAnimation(ViewHolder viewholder ,final Word word) {
		final ViewHolder vh = viewholder;
/*
		mForgetAnim = new ScaleAnimation(0.75f, 1f, 0.75f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mForgetAnim.setDuration(duration);
		mForgetAnim.setInterpolator(new BounceInterpolator());
		//mForgetAnim.setInterpolator(new DecelerateInterpolator());
*/

		mForgetAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_forget);
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
				WordStateAnimation(vh, 1000, 200, word);
			}
		});
	}

	void WordStateAnimation(ViewHolder viewholder, long duration, long starttime, Word word) {
		final ViewHolder vh = viewholder;

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

	private void deleteCell(final View v, final int index) {
		AnimationListener al = new AnimationListener() {


			@Override
			public void onAnimationEnd(Animation arg0) {
				words.remove(index);
				ViewHolder vh = (ViewHolder)v.getTag();
				vh.needInflate = true;
				isListAnimaion = false;
				adapter.notifyDataSetChanged();
//				if(adapter.getCount()==0){
//					new AlertDialog.Builder(Known_Activity.this)
//					.setMessage("현재 단어장 내의 단어를 모두 학습을 끝냈습니다.\n다음 단어장으로 넘어갑니다.")
//					.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							// TODO Auto-generated method stub
//							adapter.clear();
//							Intent intent = new Intent(Known_Activity.this, com.ihateflyingbugs.hsmd.tutorial.MainActivity.class);
//							startActivity(intent);
//							finish();
//						}
//					}).show();
//				}
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


	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */

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
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {

		super.onPause();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub

	}

	String starttime;
	String startdate;
	Date date = new Date();

	Map<String, String> articleParams ;

	public void onStart()
	{

		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("KnownWordListActivity", articleParams);
		// your code
		//	MainActivity.writeLog("[아는단어장 시작,Known_Activity,1]\r\n");

	}

	public void onStop()
	{
		super.onStop();
		FlurryAgent.endTimedEvent("KnownWordListActivity");
		articleParams.put("End", String.valueOf(System.currentTimeMillis()));
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
		//articleParams.clear();
		// your code
		//	MainActivity.writeLog("[아는단어장 끝,Known_Activity,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");
	}


}