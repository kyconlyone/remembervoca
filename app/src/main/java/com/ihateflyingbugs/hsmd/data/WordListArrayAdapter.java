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
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.SwipeDismissTouchListener;
import com.ihateflyingbugs.hsmd.TTS_Util;
import com.ihateflyingbugs.hsmd.flipimageview.FlipImage;
import com.ihateflyingbugs.hsmd.model.StudyInfoData;
import com.ihateflyingbugs.hsmd.model.WordData;
import com.ihateflyingbugs.hsmd.popup.DetailWordPopup;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.ihateflyingbugs.hsmd.service.DBService;
import com.ihateflyingbugs.hsmd.tutorial.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class WordListArrayAdapter extends ArrayAdapter<Word> {
    String TAG = "WordListArrayAdapter";

    Activity mActivity;
    Context mContext;

    View view;
    private LayoutInflater mInflater;

    private ArrayList<Word> mWordArrayList;
    WordItemViewHolder mViewHolder = null;

    TTS_Util tts_util;
    DBPool mDBPool;
    Handler handler;
    DisplayMetrics mMetrics;
    SharedPreferences settings;

    Word mWord;

    String mLocalClassName;

    boolean flag_touch = false;
    boolean flag_animation = true;
    boolean bSpecialWord = false;
    String mSpecialRate;

    WordListCallback mCallback;

    public enum RowType {
        LIST_ITEM, HEADER_ITEM
    }

    public WordListArrayAdapter(Activity activity, Context context, ArrayList<Word> items, DisplayMetrics metrics, WordListCallback vc) {
        super(context, 0, items);

        mWordArrayList = items;

        mInflater = LayoutInflater.from(context);

        mActivity = activity;
        mContext = context;
        mCallback = vc;

        mDBPool = DBPool.getInstance(mContext);
        tts_util = new TTS_Util(mContext);
        handler = new Handler();
        mMetrics = metrics;

        settings = mActivity.getSharedPreferences(Config.PREFS_NAME, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);

        mLocalClassName = mActivity.getLocalClassName();
    }

    public void setTouchMode(boolean touch) {
        flag_touch = touch;
    }

    public void setAnimationFlag(boolean anim) {
        flag_animation = anim;
    }

    LinearLayout mWordLayout;

    public void setLayout(LinearLayout layout) {
        mWordLayout = layout;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            view = getItem(position).getView(mInflater, convertView);
            setViewHolder(view);
        } else if (((WordItemViewHolder) convertView.getTag()).needInflate) {
            view = getItem(position).getView(mInflater, convertView);
            setViewHolder(view);
        } else {
            view = convertView;
        }


        mViewHolder = (WordItemViewHolder) view.getTag();
        mViewHolder.linearForward.setId(position);

        mWord = getItem(position);

        mSpecialRate = mWord.getmSpecialRate();

        if (mSpecialRate != null) {
            mViewHolder.tvForward.setBackgroundResource(R.drawable.img_sell_special);
            mViewHolder.tvForward.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            mViewHolder.tvForward.setBackground(null);
            mViewHolder.tvForward.setTextColor(Color.parseColor("#8a000000"));
        }


        mViewHolder.tvForward.setText(mWord.getWord());
        mViewHolder.linearForward.setVisibility(View.VISIBLE);
        mViewHolder.iv_flip_image.setImage(mWord.getState());

        int mWordstate = mWord.getState();
        if (mWordstate > 0) {
            mViewHolder.iv_flip_image.setVisibility(View.VISIBLE);
            mViewHolder.tvUnknownCount.setVisibility(View.INVISIBLE);

            String mStateImageString;

            if (mWordstate > 10)
                mStateImageString = "img_state_10_color";
            else
                mStateImageString = "img_state_" + mWordstate + "_color";

            int mStateImageResourceIdInt = mActivity.getResources().getIdentifier(mStateImageString, "drawable", mActivity.getPackageName());

            mViewHolder.tvUnknownCount1.setBackgroundResource(mStateImageResourceIdInt);
        } else if (mWordstate < 0) {
            mViewHolder.iv_flip_image.setVisibility(View.VISIBLE);
            mViewHolder.tvUnknownCount.setVisibility(View.INVISIBLE);
            mViewHolder.tvUnknownCount1.setBackgroundResource(R.drawable.img_state_forget_color);
        } else {
            mViewHolder.iv_flip_image.setVisibility(View.VISIBLE);
            mViewHolder.linearForward.setBackgroundColor(Color.WHITE);
            mViewHolder.tvUnknownCount.setVisibility(View.INVISIBLE);
            mViewHolder.tvUnknownCount1.setVisibility(View.INVISIBLE);
        }


        mViewHolder.linearForward.setOnClickListener(clicklistener);
        mViewHolder.linearForward.setOnLongClickListener(longclicklistener);


        setOnTouch(view, position);

        return view;
    }


    // TODO Click Listener
    public View.OnClickListener clicklistener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            WordItemViewHolder vh = (WordItemViewHolder) v.getTag();
            WordStateAnimation(vh, 1000, 1000);

            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.zoom_out);
            vh.tvForward.startAnimation(animation);

            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

            if (!(audioManager.isWiredHeadsetOn() || audioManager.isBluetoothA2dpOn())) {
                switch (audioManager.getRingerMode()) {
                    case AudioManager.RINGER_MODE_VIBRATE:
                    case AudioManager.RINGER_MODE_SILENT:
                        Toast.makeText(mContext, "소리 모드로 전환후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        break;
                    case AudioManager.RINGER_MODE_NORMAL:
                        if (tts_util.tts_check()) {
                            tts_util.tts_reading(vh.tvForward.getText().toString());
                        } else {
                            Toast.makeText(mContext, "잠시후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            } else {
                tts_util.tts_reading(vh.tvForward.getText().toString());
            }
        }
    };


    // TODO Long Click Listener
    public View.OnLongClickListener longclicklistener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            Log.e("KARAM", "LongClick");

            final WordItemViewHolder vh = (WordItemViewHolder) v.getTag();
            mWord = getItem(vh.linearForward.getId());

            Log.e("Async", "Async_get_word_detail_question_list : ready");

            new RetrofitService().getWordService().retroGetWorkbookListByWord("" + mWord.get_id())
                    .enqueue(new Callback<WordData>() {
                        @Override
                        public void onResponse(Response<WordData> response, Retrofit retrofit) {
                            Log.e("KARAM", "LongClick CallActivity()   " + mWord.get_id());
                            //KARAM

                            Config.setting_rate = response.body().getSetting_rate();
                            Config.word = mWord.getWord();
                            int count = 0;
                            try {
                                count = response.body().getWorkbook_list().size();
                            } catch (Exception e) {
                                count = 0;
                            }
                            if (count == 0) {
                                handler.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub


                                        Toast.makeText(mContext, "잠시후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }

                            Config.category = new int[count];
                            Config.cat1 = new String[count];
                            Config.cat2 = new String[count];
                            Config.cat2_sub = new String[count];
                            Config.sum = new int[count];
                            List<StudyInfoData.WorkBook> workBookList = response.body().getWorkbook_list();
                            for (int i = 0; i < count; i++) {
                                Config.category[i] = workBookList.get(i).getCategory();
                                Config.cat1[i] = workBookList.get(i).getCat1();
                                Config.cat2[i] = workBookList.get(i).getCat2();
                                Config.cat2_sub[i] = workBookList.get(i).getCat2_sub();
                                Config.sum[i] = workBookList.get(i).getSum();
                            }

                            Log.e("KARAM", "Category Length " + count);
                            Intent i = new Intent(mActivity, DetailWordPopup.class);
                            i.putExtra("wordcode", mWord.get_id());

                            mActivity.startActivity(i);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e(TAG, "onFailure :  "+ t.toString());
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    Toast.makeText(mContext, "잠시후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });


            return true;
        }
    };


    // TODO Touch Listener
    public void setOnTouch(final View view, int position) {
        final WordItemViewHolder vh = (WordItemViewHolder) view.getTag();
        mWord = getItem(vh.linearForward.getId());

        vh.linearForward.setOnTouchListener(new SwipeDismissTouchListener(vh.linearForward, vh.linearUnknown, vh.linearKnown,
                vh.iv_wc, true, null, new SwipeDismissTouchListener.DismissCallbacks() {


            @Override
            public void onRightDismiss(View v, Object token, boolean flag) {
                FlurryAgent.logEvent(TAG + " : onRightDismiss", true);

                flag_touch = false;

                mWord = getItem(v.getId());

                int ex_state;

                try {
                    ex_state = mWord.getState();

                    if (!mWord.isRight()) {
                        mWord.setRight(true);
                        mWord.setWrong(false);
                        mDBPool.updateRightWrong(true, mWord.get_id());
                    }

                    mDBPool.isMaxState(mWord.getState());
                    mDBPool.insertState0FlagTableElement(mWord, true);
                    mDBPool.updateForgettingCurvesByNewInputs(mWord, Config.MAINWORDBOOK, true);
                    mDBPool.insertReviewTimeToGetMemoryCoachMent(mWord);
                    mDBPool.insertLevel(mWord, true);

                    Word word_for_write = mDBPool.getWord(mWord.get_id());
                    mWord.setState(word_for_write.getState());

                    if (ex_state > 0) {
                        settings.edit().putInt(MainValue.GpreCheckCurve,  settings.getInt(MainValue.GpreCheckCurve, 0) + 1).commit();
                    }

                    MainActivity.setActionBar(true);


                    vh.linearForward.setVisibility(View.GONE);

                    deleteCell(view, mWord, ex_state);
                } catch (IndexOutOfBoundsException e) {
                    Toast.makeText(mContext, "잠시후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }

            }

            @Override
            public void onLeftMovement(View v) {
                FlurryAgent.logEvent(TAG + " : onLeftMovement", true);

                flag_touch = false;

                mWord = getItem(v.getId());
                Log.e(TAG, "   onLeftMovement " + mWord.getWord() + "  " + vh.tvForward.getText().toString());

                Config.unknow_count++;

                int ex_state = mWord.getState();
                mWord.increaseWrongCount();

                if (!mWord.isWrong()) {
                    mWord.setWrong(true);
                    mWord.setRight(false);
                    mDBPool.updateRightWrong(false, mWord.get_id());
                }

                mDBPool.insertState0FlagTableElement(mWord, false);
                mDBPool.updateForgettingCurvesByNewInputs(mWord, Config.MAINWORDBOOK,  false);
                mDBPool.insertReviewTimeToGetMemoryCoachMent(mWord);
                mDBPool.insertLevel(mWord, false);


                Word word_for_write = mDBPool.getWord(mWord.get_id());
                mWord.setState(word_for_write.getState());


                if (ex_state > 0) {
                    settings.edit().putInt(MainValue.GpreCheckCurve, settings.getInt(MainValue.GpreCheckCurve, 0) + 1).commit();
                }


                vh.iv_flip_image.setImage(mWord.getState());

                if (mWord.getWrongCount() != 0) {
                    vh.tvUnknownCount.setVisibility(View.INVISIBLE);
                    vh.tvUnknownCount.setBackgroundResource(R.drawable.img_state_forget);

                    vh.tvUnknownCount1.setVisibility(View.VISIBLE);
                    vh.tvUnknownCount1.setBackgroundResource(R.drawable.img_state_forget_color);
                }

                WordForgetAnimation(vh);

                MainActivity.setActionBar(true);
            }

            @Override
            public void onLeftDismiss(View v, Object token, boolean flag) {
                flag_touch = false;
            }

            @Override
            public boolean canDismiss(View v, Object token) {
                if (flag_touch) {
                    flag_touch = false;
                    return false;
                }

                //	Log.e("KARAM", TAG + "  Candismiss  ID : " + v.getId());
                mWord = getItem(v.getId());
                if (mWord == null) {
                    Log.e("KARAM", TAG + " Word Null");
                }

                if (mWord.getMeanList().size() == 0) {
                    mWord.setMeanList(mDBPool.getMean(mWord.get_id()));
                }

                if (mWord.getMeanList().size() != 0) {
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

                    String Mean_N = "";
                    String Mean_V = "";
                    String Mean_A = "";
                    String Mean_AD = "";
                    String Mean_CONJ = "";

                    // 뜻 저장
                    for (int i = 0; i < mWord.getMeanList().size(); i++) {
                        mean = mWord.getMean(i);
                        int key = mean.getMClass();

                        switch (key) {
                            case Word.Class_N: // noun : 1
                                Mean_N += mean.getMeaning() + ", ";
                                break;
                            case Word.Class_V: // verb : 2
                                Mean_V += mean.getMeaning() + ", ";
                                break;
                            case Word.Class_A: // adjective : 3
                                Mean_A += mean.getMeaning() + ", ";
                                break;
                            case Word.Class_Ad: // adverb : 4
                                Mean_AD += mean.getMeaning() + ", ";
                                break;

                            case Word.Class_Conj: // conjunction : 5
                                Mean_CONJ += mean.getMeaning() + ", ";
                                break;
                        }
                    }

                    switch (mWord.getP_class()) {
                        case Word.Class_N:
                            vh.ll_first_mean.setVisibility(View.VISIBLE);
                            vh.tv_first_mean_title.setText("n");
                            vh.tv_first_mean.setText(Mean_N.substring(0, Mean_N.length() - 2));

                            vh.ll_known_first_mean.setVisibility(View.VISIBLE);
                            vh.tv_known_first_mean_title.setText("n");
                            vh.tv_known_first_mean.setText(Mean_N.substring(0, Mean_N.length() - 2));
                            break;
                        case Word.Class_V:
                            vh.ll_first_mean.setVisibility(View.VISIBLE);
                            vh.tv_first_mean_title.setText("v");
                            vh.tv_first_mean.setText(Mean_V.substring(0, Mean_V.length() - 2));

                            vh.ll_known_first_mean.setVisibility(View.VISIBLE);
                            vh.tv_known_first_mean_title.setText("v");
                            vh.tv_known_first_mean.setText(Mean_V.substring(0, Mean_V.length() - 2));
                            break;
                        case Word.Class_A:
                            vh.ll_first_mean.setVisibility(View.VISIBLE);
                            vh.tv_first_mean_title.setText("a");
                            vh.tv_first_mean.setText(Mean_A.substring(0, Mean_A.length() - 2));

                            vh.ll_known_first_mean.setVisibility(View.VISIBLE);
                            vh.tv_known_first_mean_title.setText("a");
                            vh.tv_known_first_mean.setText(Mean_A.substring(0, Mean_A.length() - 2));
                            break;
                        case Word.Class_Ad:
                            vh.ll_first_mean.setVisibility(View.VISIBLE);
                            vh.tv_first_mean_title.setText("ad");
                            vh.tv_first_mean.setText(Mean_AD.substring(0, Mean_AD.length() - 2));

                            vh.ll_known_first_mean.setVisibility(View.VISIBLE);
                            vh.tv_known_first_mean_title.setText("ad");
                            vh.tv_known_first_mean.setText(Mean_AD.substring(0, Mean_AD.length() - 2));
                            break;
                        case Word.Class_Conj:
                            vh.ll_first_mean.setVisibility(View.VISIBLE);
                            vh.tv_first_mean_title.setText("conj");
                            vh.tv_first_mean.setText(Mean_CONJ.substring(0, Mean_CONJ.length() - 2));

                            vh.ll_known_first_mean.setVisibility(View.VISIBLE);
                            vh.tv_known_first_mean_title.setText("conj");
                            vh.tv_known_first_mean.setText(Mean_CONJ.substring(0, Mean_CONJ.length() - 2));
                            break;
                    }

                    // 두번째, 세번째 단어 뜻 셋팅
                    HashMap<Integer, Boolean> hm = mWord.getmClassList();
                    hm.remove(mWord.getP_class());

                    Iterator<Integer> iterator = hm.keySet().iterator();
                    int line = 0;

                    while (iterator.hasNext()) {
                        int key = iterator.next();
                        String multi_mean = "";
                        String mclass = "";

                        switch (key) {
                            case Word.Class_A:
                                mclass = "a";
                                multi_mean = Mean_A;
                                break;
                            case Word.Class_Ad:
                                mclass = "ad";
                                multi_mean = Mean_AD;
                                break;
                            case Word.Class_N:
                                mclass = "n";
                                multi_mean = Mean_N;
                                break;
                            case Word.Class_V:
                                mclass = "v";
                                multi_mean = Mean_V;
                                break;
                            case Word.Class_Conj:
                                mclass = "conj";
                                multi_mean = Mean_CONJ;
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

                    if (mWord.getmClassList().size() > 1) {
                        text_sp = 15;
                    } else if (mWord.getmClassList().size() == 1) {
                        if (vh.tv_first_mean.length() > 15 || vh.tv_second_mean.length() > 15) {
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


                    if (vh.tv_first_mean_title.getText().toString().equals("conj")) {
                        vh.tv_first_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                        vh.tv_known_first_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                    } else if (vh.tv_second_mean_title.getText().toString().equals("conj")) {
                        vh.tv_second_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                        vh.tv_known_second_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                    } else if (vh.tv_third_mean_title.getText().toString().equals("conj")) {
                        vh.tv_third_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                        vh.tv_known_third_mean_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                    }
                } else {    // size 0
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

                flag_touch = true;
                return true;
            }

            @Override
            public void showFlipAnimation(boolean Direction, float deltaX) {
                if (!Direction) {
                    float new_percent = Math.abs(deltaX) / (view.getWidth() / 8);
                    moved_percent = new_percent;

                    if (mStateAnim != null) {
                        vh.tvUnknownCount1.clearAnimation();
                        vh.tvUnknownCount1.animate().cancel();
                        vh.tvUnknownCount1.setVisibility(View.GONE);
                        vh.iv_flip_image.setVisibility(View.VISIBLE);
                        mStateAnim = null;
                    }

                    if (mForgetAnim != null) {
                        vh.tvUnknownCount1.clearAnimation();
                        vh.tvUnknownCount1.animate().cancel();
                        vh.tvUnknownCount1.setVisibility(View.GONE);
                        vh.iv_flip_image.setVisibility(View.VISIBLE);
                        mForgetAnim = null;
                    }

                    if (new_percent < 2) {
                        vh.iv_flip_image.flip_ani(new_percent);
                    } else {
                        vh.iv_flip_image.flip_ani(2);
                    }
                }
            }

            @Override
            public void onRightMovement() {
                flag_touch = false;
                vh.iv_flip_image.flip(moved_percent);

            }


        }));


        if (flag_animation) {
            if (position < 8) {
                Animation animation = null;
                animation = new TranslateAnimation(mMetrics.widthPixels / 2, 0, 0, 0);
                animation.setDuration((position * 20) + 800);
                view.startAnimation(animation);

                WordStateAnimation(vh, 1000, 1500);
            }
        }

    }

    Animation mStateAnim;
    Animation mForgetAnim;

    void WordForgetAnimation(WordItemViewHolder viewholder) {
        final WordItemViewHolder vh = viewholder;
/*
        mForgetAnim = new ScaleAnimation(0.75f, 1f, 0.75f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		mForgetAnim.setDuration(duration);
		mForgetAnim.setInterpolator(new BounceInterpolator());
		//mForgetAnim.setInterpolator(new DecelerateInterpolator());
*/

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
                WordStateAnimation(vh, 1000, 200);
            }
        });
    }

    void WordStateAnimation(WordItemViewHolder viewholder, long duration, long starttime) {
        final WordItemViewHolder vh = viewholder;

        mStateAnim = new AlphaAnimation(1.0f, 0.0f);

        mStateAnim.setDuration(duration);
        mStateAnim.setStartOffset(starttime);

        final int mWordstate = mWord.getState();
        if (mWordstate != 0)
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

    float moved_percent;


    private synchronized void deleteCell(final View v, final Word word, final int exState) {
        //	Intent intent = new Intent(mActivity, FriendRecommend_SelectDialogActivity.class);
        //	mActivity.startActivity(intent);


        AnimationListener al = new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {
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


                MildangDate date = new MildangDate();
                mDBPool.deleteCurrentWord(word.get_id());
                mDBPool.putCalendarData(date.get_today());


                WordItemViewHolder vh = (WordItemViewHolder) v.getTag();
                vh.needInflate = true;

                remove(word);
                flag_animation = false;


                flag_touch = false;
                notifyDataSetChanged();

                if (getCount() == 0) {
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            flag_animation = true;

//                            new AlertDialog.Builder(mActivity)
//                                    .setMessage("현재 단어장 내의 단어를 모두 학습을 끝냈습니다.\n다음 단어장으로 넘어갑니다.")
//                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                        }
//                                    }).show();

                            mDBPool.deleteAllCurrentWord();
                            MainActivity.isShowCard = false;

                            MainActivity.sendWorkEachCount(mDBPool);

                            mCallback.refreshWordList();


                            Intent intent = new Intent(mActivity, DBService.class);
                            PendingIntent pintent = PendingIntent.getService(mActivity, 0, intent, 0);
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
        final int initialHeight = v.getHeight();

        Animation anim = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime,
                                               Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    //	Log.e("KARAM", "Height : " + initialHeight + "  " + v.getLayoutParams().height);
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


    private void setViewHolder(View view) {
        WordItemViewHolder vh = new WordItemViewHolder();
        vh.linearForward = (LinearLayout) view.findViewById(R.id.linearForward);
        vh.linearKnown = (LinearLayout) view.findViewById(R.id.linearKnown);
        vh.linearUnknown = (LinearLayout) view.findViewById(R.id.linearUnknown);

        vh.ll_known_first_mean = (LinearLayout) view.findViewById(R.id.ll_known_first_mean);
        vh.ll_known_second_mean = (LinearLayout) view.findViewById(R.id.ll_known_second_mean);
        vh.ll_known_third_mean = (LinearLayout) view.findViewById(R.id.ll_known_third_mean);
        vh.ll_known_forth_mean = (LinearLayout) view.findViewById(R.id.ll_known_forth_mean);

        vh.ll_first_mean = (LinearLayout) view.findViewById(R.id.ll_first_mean);
        vh.ll_second_mean = (LinearLayout) view.findViewById(R.id.ll_second_mean);
        vh.ll_third_mean = (LinearLayout) view.findViewById(R.id.ll_third_mean);
        vh.ll_forth_mean = (LinearLayout) view.findViewById(R.id.ll_forth_mean);

        vh.tvForward = (TextView) view.findViewById(R.id.tvForward);
        vh.tvKnownWord = (TextView) view.findViewById(R.id.tvKnownWord);
        vh.tvUnknownWord = (TextView) view.findViewById(R.id.tvUnknownWord);
        vh.tvUnknownCount = (ImageView) view.findViewById(R.id.tvUnknownCount);
        vh.tvUnknownCount1 = (ImageView) view.findViewById(R.id.tvUnknownCount1);

        vh.tv_known_first_mean_title = (TextView) view.findViewById(R.id.tv_known_first_mean_title);
        vh.tv_known_second_mean_title = (TextView) view.findViewById(R.id.tv_known_second_mean_title);
        vh.tv_known_third_mean_title = (TextView) view.findViewById(R.id.tv_known_third_mean_title);
        vh.tv_known_forth_mean_title = (TextView) view.findViewById(R.id.tv_known_forth_mean_title);

        vh.tv_known_first_mean = (TextView) view.findViewById(R.id.tv_known_first_mean);
        vh.tv_known_second_mean = (TextView) view.findViewById(R.id.tv_known_second_mean);
        vh.tv_known_third_mean = (TextView) view.findViewById(R.id.tv_known_third_mean);
        vh.tv_known_forth_mean = (TextView) view.findViewById(R.id.tv_known_forth_mean);

        vh.tv_first_mean_title = (TextView) view.findViewById(R.id.tv_first_mean_title);
        vh.tv_second_mean_title = (TextView) view.findViewById(R.id.tv_second_mean_title);
        vh.tv_third_mean_title = (TextView) view.findViewById(R.id.tv_third_mean_title);
        vh.tv_forth_mean_title = (TextView) view.findViewById(R.id.tv_forth_mean_title);

        vh.tv_first_mean = (TextView) view.findViewById(R.id.tv_first_mean);
        vh.tv_second_mean = (TextView) view.findViewById(R.id.tv_second_mean);
        vh.tv_third_mean = (TextView) view.findViewById(R.id.tv_third_mean);
        vh.tv_forth_mean = (TextView) view.findViewById(R.id.tv_forth_mean);

        vh.iv_back_del = (ImageView) view.findViewById(R.id.iv_back_del);
        vh.iv_back_del.setVisibility(View.INVISIBLE);

        vh.mLineView = (View) view.findViewById(R.id.view_wordlist_line);
        vh.mLineView.setVisibility(View.GONE);

        vh.iv_flip_image = (FlipImage) view.findViewById(R.id.iv_flip_image);

        vh.needInflate = false;

        vh.linearForward.setTag(vh);
        view.setTag(vh);
    }
}
