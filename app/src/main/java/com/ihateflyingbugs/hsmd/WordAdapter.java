package com.ihateflyingbugs.hsmd;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Mean;
import com.ihateflyingbugs.hsmd.data.Word;

import java.util.ArrayList;
import java.util.Calendar;


public class WordAdapter extends ArrayAdapter<Word>{

	public static final int MODE_NORMAL_SCORE_LIST = 0;
	public static final int MODE_NORMAL_KNOWN_LIST = 1;
	public static final int MODE_EXAM_KNOWN_LIST = 2;
	public static final int MODE_EXAM_SCORE_LIST = 3;

	public static int mode;

	private boolean flag_set_swipe_mode;

	private String query;

	static final int ANIMATION_DURATION = 400;
	private int right, wrong, none = Config.ONCE_WORD_COUNT;
	TTS_Util tts_util;
	private DisplayMetrics metrics;
	LogDataFile log_file;


	LayoutInflater vi;
	private ArrayList<Word> items;
	private boolean isWrongContinueShow;
	Context mContext;

	private DBPool db;

	int start;
	int last;

	private RelativeLayout relativeWord;
	private PullToRefreshListView mPullToRefreshListView;
	private ImageView ivTemp;
	private boolean isListAnimaion = true;


	public WordAdapter(Context context, int resourceId, ArrayList<Word> items){
		super(context, resourceId, items);
		this.items = items;
		this.vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


		if(mode == MODE_NORMAL_SCORE_LIST || mode == MODE_EXAM_SCORE_LIST)
			isWrongContinueShow = true;
		else
			isWrongContinueShow = false;

		mContext= context;
	}

	//		@Override
	//		public boolean isEnabled(int position)
	//		{
	//			return false;
	//		}


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

		vh.iv_wc.setImageResource(R.drawable.wc_upperbar_a+position%4);

		//vh.tvUnknownMeaning.setTextSize(TypedValue.COMPLEX_UNIT_SP, position);

		if(mode == MODE_NORMAL_KNOWN_LIST || mode == MODE_EXAM_KNOWN_LIST)
		{
			vh.ivKnown.setVisibility(View.VISIBLE);
		}
		else
		{
			vh.ivKnown.setVisibility(View.GONE);
		}

		if(word.isShow()){
			vh.iv_wc.setVisibility(View.VISIBLE);
		}else{

			vh.iv_wc.setVisibility(View.INVISIBLE);
		}

		vh.linearForward.setVisibility(View.VISIBLE);

		if(word.getState()>0 && isWrongContinueShow)
		{
			//				vh.linearForward.setBackgroundColor(Color.rgb(230, 230, 230));
			//vh.linearForward.setBackgroundResource(R.drawable.liawefaweo;fijaweo;ijfwae;if;oawiejf;oaisdfjl;asdjkfldaalifjoiawejflawfj_again_line);
			//vh.tvUnknownCount.setText(word.getWrongCount() + "");
			//vh.tvUnknownCount.setVisibility(View.VISIBLE);

			//if(items.get(position).getWrongCount()!=0){
			//vh.tvUnknownCount.setText(items.get(position).getWrongCount() + "");
			//vh.tvUnknownCount.setBackgroundResource(R.drawable.list_right_icon);
			vh.tvUnknownCount.setVisibility(View.VISIBLE);
			//}

			//				vh.linearForward.setOnTouchListener(null);
		}else if(word.getState()<0 && isWrongContinueShow){
			//	vh.tvUnknownCount.setBackgroundResource(R.drawable.unknown_questionmark);
			vh.tvUnknownCount.setVisibility(View.VISIBLE);

		}
		else
		{
			vh.linearForward.setBackgroundColor(Color.WHITE);
			vh.tvUnknownCount.setVisibility(View.INVISIBLE);
		}

		//			vh.linearForward.setOnLongClickListener(new View.OnLongClickListener() {
		//
		//				@Override
		//				public boolean onLongClick(View v) {
		//					// TODO Auto-generated method stub
		//					switch (vh.iv_wc.getVisibility()) {
		//					case View.VISIBLE:
		//						word.setShow(false);
		//						vh.iv_wc.setVisibility(View.INVISIBLE);
		//						break;
		//					case View.INVISIBLE:
		//						word.setShow(true);
		//						vh.iv_wc.setVisibility(View.VISIBLE);	
		//						break;
		//					default:
		//						word.setShow(false);
		//						vh.iv_wc.setVisibility(View.INVISIBLE);	
		//						break;
		//					}
		//					return true;
		//				}
		//			});

		vh.linearForward.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Animation animation = AnimationUtils.loadAnimation(mContext , R.anim.zoom_out);
				vh.tvForward.startAnimation(animation);
				AudioManager audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);

				switch(audioManager.getRingerMode()){
					case AudioManager.RINGER_MODE_VIBRATE:
						// 진동
						Toast.makeText(mContext, "소리 모드로 전화후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
						break;
					case AudioManager.RINGER_MODE_NORMAL:
						// 소리
						if(tts_util.tts_check()){
							tts_util.tts_reading(vh.tvForward.getText().toString());
						}else{
							Toast.makeText(mContext, "재생에 문제가 있습니다. 잠시후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
						}
						break;
					case AudioManager.RINGER_MODE_SILENT:
						// 무음
						Toast.makeText(mContext, "소리 모드로 전화후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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
				//ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(mContext, R.anim.flipping);
				//anim.setTarget(vh.linearForward);
				//anim.setDuration(500);
				//anim.start();

			}
		});

		vh.linearForward.setOnTouchListener(new SwipeDismissTouchListener(
				vh.linearForward, vh.linearUnknown, vh.linearKnown, vh.iv_wc , flag_set_swipe_mode,
				null,
				new SwipeDismissTouchListener.DismissCallbacks() {
					@Override
					public boolean canDismiss(View v, Object token) {

						if(word.getMeanList().size()!=0){
							vh.ll_first_mean.setVisibility(View.GONE);
							vh.ll_second_mean.setVisibility(View.GONE);
							vh.ll_third_mean.setVisibility(View.GONE);
							vh.ll_forth_mean.setVisibility(View.GONE);
							Mean mean ;
							for(int i=0; i <word.getMeanList().size(); i++){
								mean = word.getMean(i);
								int key = mean.getMClass();
								switch (key) {
									case Word.Class_N:
										vh.ll_first_mean.setVisibility(View.VISIBLE);
										vh.tv_first_mean.setText(vh.tv_first_mean.getText()+mean.getMeaning()+", ");
										vh.ll_known_first_mean.setVisibility(View.VISIBLE);
										vh.tv_known_first_mean.setText(vh.tv_first_mean.getText()+mean.getMeaning()+", ");
										break;
									case Word.Class_V:
										vh.ll_second_mean.setVisibility(View.VISIBLE);
										vh.tv_second_mean.setText(vh.tv_second_mean.getText()+mean.getMeaning()+", ");
										vh.ll_second_mean.setVisibility(View.VISIBLE);
										vh.tv_known_second_mean.setText(vh.tv_second_mean.getText()+mean.getMeaning()+", ");
										break;
									case Word.Class_Ad:
										vh.ll_third_mean.setVisibility(View.VISIBLE);
										vh.tv_third_mean.setText(vh.tv_third_mean.getText()+mean.getMeaning()+", ");
										vh.ll_third_mean.setVisibility(View.VISIBLE);
										vh.tv_known_third_mean.setText(vh.tv_third_mean.getText()+mean.getMeaning()+", ");
										break;
									case Word.Class_Conj:
										vh.ll_forth_mean.setVisibility(View.VISIBLE);
										vh.tv_forth_mean.setText(vh.tv_forth_mean.getText()+mean.getMeaning()+", ");
										vh.ll_known_forth_mean.setVisibility(View.VISIBLE);
										vh.tv_known_forth_mean.setText(vh.tv_forth_mean.getText()+mean.getMeaning()+", ");
										break;
									default:
										break;
								}
							}
							if(word.getmClassList().size()>2){
								vh.tv_first_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
								vh.tv_second_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
								vh.tv_third_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
								vh.tv_forth_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

								vh.tv_first_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
								vh.tv_second_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
								vh.tv_third_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
								vh.tv_forth_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

								vh.tv_known_first_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
								vh.tv_known_second_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
								vh.tv_known_third_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
								vh.tv_known_forth_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

								vh.tv_known_first_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
								vh.tv_known_second_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
								vh.tv_known_third_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
								vh.tv_known_forth_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

							}else{

								vh.tv_first_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
								vh.tv_second_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
								vh.tv_third_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
								vh.tv_forth_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);

								vh.tv_first_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
								vh.tv_second_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
								vh.tv_third_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
								vh.tv_forth_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);

								vh.tv_known_first_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
								vh.tv_known_second_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
								vh.tv_known_third_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
								vh.tv_known_forth_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);

								vh.tv_known_first_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
								vh.tv_known_second_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
								vh.tv_known_third_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
								vh.tv_known_forth_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
							}

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
						return true;
					}


					@Override
					public void onLeftDismiss(View v, Object token,
											  boolean flag) {
						// TODO Auto-generated method stub

						int ex_state = items.get(position).getState();

						boolean isKnown =false;
						vh.linearForward.setVisibility(View.GONE);

						if(!isWrongContinueShow)
						{
							deleteCell(view, position);
							//									words.remove(position);
						}
						word.increaseWrongCount();
						Log.e("swipe", "before   Right : "+String.valueOf(right)+"    wrong : "+String.valueOf(wrong)+"None : "+String.valueOf(none));
						Log.e("swipe", "before   isWrong : "+String.valueOf(word.isWrong()) + "isRight : "+String.valueOf(word.isRight()));
						if(!word.isWrong())
						{

							wrong++;
							none--;
							word.setWrong(true);
							word.setRight(false);

							db.updateRightWrong(false, word.get_id());
						}
						Log.e("swipe", "after    Right : "+String.valueOf(right)+"    wrong : "+String.valueOf(wrong)+"None : "+String.valueOf(none));

						db.updateWordInfo(word, isKnown);
						db.insertLevel(word, isKnown);



						Word word_for_write= db.getWord(items.get(position).get_id());


						word.setState(word_for_write.getState());

						String data = word_for_write.getWord()+"@"+String.valueOf(word_for_write.getScore())+"@"+String.valueOf(ex_state)+"@"
								+String.valueOf(word_for_write.getState())+"@"+get_date()+"\r\n";

						log_file.input_LogData_in_file(data);

						mPullToRefreshListView.setWordCount(right, wrong, none);



						if(word.getWrongCount()!=0&& isWrongContinueShow){
							//vh.tvUnknownCount.setText(items.get(position).getWrongCount() + "");

							vh.tvUnknownCount.setVisibility(View.VISIBLE);
							//	vh.tvUnknownCount.setBackgroundResource(R.drawable.unknown_questionmark);

						}
					}


					@Override
					public void onRightDismiss(View v, Object token,
											   boolean flag) {
						// TODO Auto-generated method stub

						int ex_state = items.get(position).getState();

						boolean isKnown= true;
						v.setOnTouchListener(null);
						ivTemp.setVisibility(View.VISIBLE);

						//								words.remove(position);
						//								vh.linearForward.setVisibility(View.GONE);
						//								vh.linearUnknown.setVisibility(View.GONE);
						//								vh.linearKnown.setVisibility(View.GONE);

						//'13.12.24 - 아는단어로 셀삭제시 셀 깜빡거리는거 없앰
						vh.linearForward.setVisibility(View.GONE);

						//							mPullToRefreshListView.setClickable(false);

						deleteCell(view, position);

						//							vh.linearForward.setEnabled(false);
						Log.e("swipe", "before   Right : "+String.valueOf(right)+"    wrong : "+String.valueOf(wrong)+"None : "+String.valueOf(none));
						Log.e("swipe", "before   isWrong : "+String.valueOf(word.isWrong()) + "isRight : "+String.valueOf(word.isRight()));

						if(!word.isRight())
						{
							//								if(mode == MODE_NORMAL_SCORE_LIST || mode == MODE_EXAM_SCORE_LIST)
							//								{
							//
							//								}

							right++;
							none--;
							word.setRight(true);
							word.setWrong(false);
							//								userDb.deleteCurrentWord(word.get_id());

							db.updateRightWrong(true, word.get_id());
						}

						Log.e("swipe", "after   Right : "+String.valueOf(right)+"    wrong : "+String.valueOf(wrong)+"None : "+String.valueOf(none));

						db.updateWordInfo(word, isKnown);
						db.insertLevel(word, isKnown);



						Word word_for_write= db.getWord(items.get(position).get_id());

						word.setState(word_for_write.getState());

						String data = word_for_write.getWord()+"@"+String.valueOf(word_for_write.getScore())+"@"+String.valueOf(ex_state)+"@"
								+String.valueOf(word_for_write.getState())+"@"+get_date()+"\r\n";

						log_file.input_LogData_in_file(data);

						mPullToRefreshListView.setWordCount(right, wrong, none);

						if(word.getState()>0&& isWrongContinueShow){
							//vh.tvUnknownCount.setText(items.get(position).getWrongCount() + "");
							vh.tvUnknownCount.setVisibility(View.VISIBLE);
							//vh.tvUnknownCount.setBackgroundResource(R.drawable.list_right_icon);
						}
					}

					@Override
					public void onLeftMovement(View v) {
						// TODO Auto-generated method stub

						if(mode == MODE_EXAM_KNOWN_LIST||mode == MODE_NORMAL_KNOWN_LIST){
							return ;
						}

						int ex_state = items.get(position).getState();

						items.get(position).increaseWrongCount();

						boolean isKnown= false;

						if(!word.isWrong())
						{
							wrong++;
							none--;
							word.setWrong(true);
							word.setRight(false);

							db.updateRightWrong(false, word.get_id());
						}

						db.updateWordInfo(items.get(position), isKnown);
						db.insertLevel(items.get(position), isKnown);




						Word word_for_write= db.getWord(items.get(position).get_id());
						word.setState(word_for_write.getState());

						String data = word_for_write.getWord()+"@"+String.valueOf(word_for_write.getScore())+"@"+String.valueOf(ex_state)+"@"
								+String.valueOf(word_for_write.getState())+"@"+get_date()+"\r\n";

						log_file.input_LogData_in_file(data);




						if(word.getWrongCount()!=0&& isWrongContinueShow){
							//vh.tvUnknownCount.setText(items.get(position).getWrongCount() + "");

							vh.tvUnknownCount.setVisibility(View.VISIBLE);
							//	vh.tvUnknownCount.setBackgroundResource(R.drawable.unknown_questionmark);

						}
						mPullToRefreshListView.setWordCount(right, wrong, none);
					}


					@Override
					public void onRightMovement() {
						// TODO Auto-generated method stub

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

	private void deleteCell(final View v, final int index) {
		AnimationListener al = new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation arg0) {
				items.remove(index);

				ViewHolder vh = (ViewHolder)v.getTag();

				vh.needInflate = true;

				isListAnimaion = false;
//				adapter.notifyDataSetChanged();
//
//				ivTemp.setVisibility(View.GONE);
//				if(adapter.getCount()==0){
//					new AlertDialog.Builder(getActivity())
//					.setMessage("현재 단어장 내의 단어를 모두 학습을 끝냈습니다.\n다음 단어장으로 넘어갑니다.")
//					.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							// TODO Auto-generated method stub
//							db.deleteAllCurrentWord();
//							adapter.clear();
//							none = Config.ONCE_WORD_COUNT;
//							refresh();
//
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

	private void setViewHolder(View view) {

		final int NORMAL_KNOWN_COLOR = Color.rgb(0x75, 0xc9, 0x6d);
		final int EXAM_KNOWN_COLOR = Color.rgb(0xee, 0xc0, 0x00);

		ViewHolder vh = new ViewHolder();

		vh.linearForward = (LinearLayout)view.findViewById(R.id.linearForward);
		vh.linearKnown = (LinearLayout)view.findViewById(R.id.linearKnown);
		vh.linearUnknown = (LinearLayout)view.findViewById(R.id.linearUnknown);


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

		vh.tv_first_mean=(TextView)view.findViewById(R.id.tv_known_first_mean);
		vh.tv_second_mean=(TextView)view.findViewById(R.id.tv_known_second_mean);
		vh.tv_third_mean=(TextView)view.findViewById(R.id.tv_known_third_mean);
		vh.tv_forth_mean=(TextView)view.findViewById(R.id.tv_known_forth_mean);

		vh.tv_first_mean_title=(TextView)view.findViewById(R.id.tv_first_mean_title);
		vh.tv_second_mean_title=(TextView)view.findViewById(R.id.tv_second_mean_title);
		vh.tv_third_mean_title=(TextView)view.findViewById(R.id.tv_third_mean_title);
		vh.tv_forth_mean_title=(TextView)view.findViewById(R.id.tv_forth_mean_title);

		vh.tv_first_mean=(TextView)view.findViewById(R.id.tv_first_mean);
		vh.tv_second_mean=(TextView)view.findViewById(R.id.tv_second_mean);
		vh.tv_third_mean=(TextView)view.findViewById(R.id.tv_third_mean);
		vh.tv_forth_mean=(TextView)view.findViewById(R.id.tv_forth_mean);



		//		if(mode == MODE_NORMAL_SCORE_LIST || mode == MODE_NORMAL_KNOWN_LIST)
		//			vh.linearKnown.setBackgroundColor(NORMAL_KNOWN_COLOR);
		//		else
		//			vh.linearKnown.setBackgroundColor(EXAM_KNOWN_COLOR);
		//
		//		vh.needInflate = false;
		//		view.setTag(vh);
	}



}





