package com.ihateflyingbugs.hsmd.OfflineLesson.OLActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bluejamesbond.text.DocumentView;
import com.bluejamesbond.text.style.TextAlignment;
import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.ihateflyingbugs.hsmd.OfflineLesson.OLData.SampleText;
import com.ihateflyingbugs.hsmd.OfflineLesson.OLData.Sentence;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.data.Mean;
import com.ihateflyingbugs.hsmd.data.Word;
import com.ihateflyingbugs.hsmd.manager.ManagerInfo;
import com.ihateflyingbugs.hsmd.model.ManagerData;
import com.ihateflyingbugs.hsmd.model.StudyInfoData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by 영철 on 2016-06-24.
 */
public class SampleTextPopup extends Activity implements View.OnClickListener {
    String TAG = "SampleTextPopup";
    List<Sentence> list;
    String[] str;
    DBPool dbPool;

    ActionBar mActionBar;
    View mCustomActionBarView;

    LinearLayout mBack_Layout;
    TextView mTitle_TextView;
    SampleText sampleText;
    ManagerInfo managerInfo;

    List<LocationTextview> list_postion;
    HashMap<Integer, Integer> isCheckWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_sampletext);
        setActionBar();
        dbPool = DBPool.getInstance(getApplicationContext());

        isCheckWord = new HashMap<>();

        sampleText = (SampleText) getIntent().getExtras().getSerializable("sampletext");

        managerInfo = ManagerInfo.getInstance(getApplicationContext());


        list = sampleText.getSentences();



        final ScrollView sv_popup_sampletext_basic = (ScrollView) findViewById(R.id.sv_popup_sampletext_basic);
        final LinearLayout ll_popup_sampletext = (LinearLayout) findViewById(R.id.ll_popup_sampletext);
        final LinearLayout ll_popup_sampletext_wordlist = (LinearLayout) findViewById(R.id.ll_popup_sampletext_wordlist);

        final DocumentView documentView = new DocumentView(this, DocumentView.PLAIN_TEXT);  // Support plain text
        documentView.getDocumentLayoutParams().setTextAlignment(TextAlignment.JUSTIFIED);


        Button bt_popup_sampletext_confirm = (Button) findViewById(R.id.bt_popup_sampletext_confirm);

        bt_popup_sampletext_confirm.setOnClickListener(this);


        Log.e(TAG, "" + list.size());
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                sv_popup_sampletext_basic.setVisibility(View.GONE);
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {

                final TextView rowTextView = new TextView(getApplicationContext());

                list_postion = new ArrayList<LocationTextview>();

                final TextView[] myTextViews = new TextView[list.size()]; // create an empty array;
                for (int i = 0; i < list.size(); i++) {
                    // create a new textview

                    // set some properties of rowTextView or something
                    for (int j = 0; j < list.get(i).getSentence_split().size(); j++) {
                        final int word_index = j;
                        final int sentence_index = i;

                        LocationTextview lt = new LocationTextview( list.get(sentence_index).getSentence_split().get(word_index).getWord_id(),
                                sentence_index, word_index, list.get(i).getSentence_split().get(j).getWord());


                        final Word word = dbPool.getWord(list.get(sentence_index).getSentence_split().get(word_index).getWord_id());
                        SpannableString ss = new SpannableString(list.get(i).getSentence_split().get(j).getWord());
                        list_postion.add(lt);


                        try {
                            if (!word.equals("")) {
                                if(word.getState()>-1){
                                    lt.setSelect(false);
                                    list.get(list_postion.get(i).getSentence_index()).getSentence_split().get(list_postion.get(i).getWord_index()).setWordSelected(false);
                                }else{
                                    lt.setSelect(true);
                                    list.get(list_postion.get(i).getSentence_index()).getSentence_split().get(list_postion.get(i).getWord_index()).setWordSelected(true);
                                }

                                ClickableSpan clickableSpan = new ClickableSpan() {
                                    @Override
                                    public void onClick(View textView) {

                                        Log.e(TAG, "onClick : "+ll_popup_sampletext.getChildCount()+"   "+list_postion.size());
                                        //get location and length in sentence
                                        int start = 0;
                                        int word_id =list.get(sentence_index).getSentence_split().get(word_index).getWord_id();
                                        boolean isCheck =list.get(sentence_index).getSentence_split().get(word_index).getWordSelected();
                                        Log.e(TAG, "word_id : "+word_id);

//                                        for (int x = 0; x <= sentence_index; x++) {
//                                            for (int y = 0; y < list.get(x).getSentence_split().size(); y++) {
//                                                if (x == sentence_index && y == word_index) {
//                                                    int end = start + list.get(sentence_index).getSentence_split().get(word_index).getWord().length();
//                                                    break;
//                                                }
//                                                start += list.get(x).getSentence_split().get(y).getWord().length();
//                                                start++;
//
//                                            }
//                                        }
                                        for(int i=0; i<list_postion.size();i++){
                                            if(word_id == list_postion.get(i).getWord_id()){
                                                if(isCheck){
                                                    Spannable s = (Spannable) ((TextView)ll_popup_sampletext.getChildAt(0)).getText();
                                                    s.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), start, start+list_postion.get(i).getWord().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    list.get(list_postion.get(i).getSentence_index()).getSentence_split().get(list_postion.get(i).getWord_index()).setWordSelected(false);
                                                    list_postion.get(i).setSelect(false);
                                                }else{
                                                    Log.e(TAG, "false");
                                                    Spannable s = (Spannable) ((TextView)ll_popup_sampletext.getChildAt(0)).getText();
                                                    s.setSpan(new BackgroundColorSpan(0xFFFFD74C), start, start+list_postion.get(i).getWord().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                    list.get(list_postion.get(i).getSentence_index()).getSentence_split().get(list_postion.get(i).getWord_index()).setSelected(true);
                                                    list_postion.get(i).setSelect(true);
                                                }

                                            }
                                            start += list_postion.get(i).getWord().length();
                                            start++;
                                        }

                                        isExistWord(ll_popup_sampletext_wordlist, list.get(sentence_index).getSentence_split().get(word_index).getWord_id(), isCheck);


                                    }

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        super.updateDrawState(ds);
                                        ds.setUnderlineText(false); // set to false to remove underline

                                        ds.setColor(Color.parseColor("#328dc5"));
                                    }
                                };
                                ss.setSpan(clickableSpan, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            }
                        } catch (NullPointerException e) {
                            list.get(i).getSentence_split().get(j).setWord_id(0);
                            lt.setSelect(false);
                            list.get(lt.getSentence_index()).getSentence_split().get(lt.getWord_index()).setWordSelected(false);
                        }
                        lt.setText(ss);


                        if(lt.getSelect()){
                            ss.setSpan(new BackgroundColorSpan(0xFFFFD74C), 0,
                                    lt.getWord().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            isCheckWord.put(lt.getWord_id(),lt.getWord_id());
                        }else{
                            ss.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), 0,
                                    lt.getWord().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

                        rowTextView.append(ss);
                        rowTextView.append(" ");

                        rowTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        rowTextView.setMovementMethod(LinkMovementMethod.getInstance());
                        rowTextView.setHighlightColor(Color.TRANSPARENT);
                        rowTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        rowTextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f, getResources().getDisplayMetrics()), 1.0f);
                        //rowTextView.setMinHeight((Utils.dpToPixels(30, getResources()))*rowTextView.getLineCount());
                        rowTextView.setTextColor(Color.parseColor("#000000"));
                    }
                    // add the textview to the linearlayout


                    // save a reference to the textview for later
                    myTextViews[i] = rowTextView;

                    Log.e("check_time", "i : " + i);
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Iterator<Integer> keys = isCheckWord.keySet().iterator();
                        while( keys.hasNext() ){
                            Integer key = keys.next();
                           addUnknownWord(isCheckWord.get(key), ll_popup_sampletext_wordlist);
                        }
                        ll_popup_sampletext.addView(rowTextView);
                        sv_popup_sampletext_basic.setVisibility(View.VISIBLE);

                    }
                });

            }
        }).start();


        Log.e("check_time", "add finish");


    }


    public class LocationTextview{
        int start_point;
        int lenth;
        int word_id;
        TextView textView;
        int sentence_index;
        int word_index;
        String word;
        SpannableString text;
        boolean isSelect = false;


        public LocationTextview(int word_id, int sentence_index, int word_index, String word){
            this.textView = textView;
            this.word_id = word_id;
            this.sentence_index = sentence_index;
            this.word_index = word_index;
            this.word = word;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public int getWord_id() {
            return word_id;
        }

        public void setWord_id(int word_id) {
            this.word_id = word_id;
        }

        public int getSentence_index() {
            return sentence_index;
        }

        public void setSentence_index(int sentence_index) {
            this.sentence_index = sentence_index;
        }

        public int getWord_index() {
            return word_index;
        }

        public void setWord_index(int word_index) {
            this.word_index = word_index;
        }



        public TextView getTextView() {
            return textView;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }

        public int getStart_point() {
            return start_point;
        }

        public void setStart_point(int start_point) {
            this.start_point = start_point;
        }

        public int getLenth() {
            return lenth;
        }

        public void setLenth(int lenth) {
            this.lenth = lenth;
        }

        public void setText(SpannableString text) {
            this.text = text;
        }

        public SpannableString getText() {
            return text;
        }

        public void setSelect(boolean select) {
            this.isSelect = select;
        }

        public boolean getSelect(){
            return isSelect;
        }
    }


    public void isExistWord(LinearLayout ll_list, int word_id, boolean needEdit) {

        Log.e("check_time", "isExistWord");
        boolean isExistWord = false;
        int position = 0;

        for (int i = 0; i < ll_list.getChildCount(); i++) {

            LinearLayout existTextview = (LinearLayout) ll_list.getChildAt(i);
            TextView tv_unknown_sample_word = (TextView) existTextview.findViewById(R.id.tv_unknown_sample_word);
            Log.e(TAG, "tv_unknown_sample_word : " + tv_unknown_sample_word.getText().toString());

            if (dbPool.getWord(word_id).getWord().equals(tv_unknown_sample_word.getText().toString())) {
                if (needEdit) {
                    //need remove textview
                    Log.e(TAG, "delete word");
                    ll_list.removeView(existTextview);
                } else {
                    //need add textview
                    position = i;
                }
            }
        }

        if ((!needEdit) && position == 0) {
            Word word = addUnknownWord(word_id, ll_list);
            setUnknownWord(word);
            isExistWord = true;
        }

    }

    public Word addUnknownWord(int word_id, LinearLayout ll_list){
        Word word = dbPool.getWord(word_id);
        word.setMeanList(dbPool.getMean(word_id));
        int textsize = 14;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout ll_unknown_word = (LinearLayout) inflater.inflate(R.layout.item_unknown_word_in_sampletext, ll_list, false);
        TextView tv_unknown_sample_word = (TextView) ll_unknown_word.findViewById(R.id.tv_unknown_sample_word);
        TextView tv_unknown_sample_mean = (TextView) ll_unknown_word.findViewById(R.id.tv_unknown_sample_mean);
        tv_unknown_sample_word.setText(word.getWord());
        tv_unknown_sample_word.setTextColor(Color.parseColor("#328dc5"));
        tv_unknown_sample_word.setTextSize(TypedValue.COMPLEX_UNIT_SP, textsize);
        tv_unknown_sample_word.setTypeface(null, Typeface.BOLD);
        while (tv_unknown_sample_word.getLineCount() > 1) {
            tv_unknown_sample_word.setTextSize(TypedValue.COMPLEX_UNIT_SP, textsize--);
        }
        textsize = 14;
        tv_unknown_sample_mean.setText(getMeans(word));
        tv_unknown_sample_mean.setTextColor(Color.parseColor("#545454"));
        tv_unknown_sample_mean.setTextSize(TypedValue.COMPLEX_UNIT_SP, textsize);
        tv_unknown_sample_mean.setTypeface(null, Typeface.BOLD);
        ll_list.addView(ll_unknown_word);
        return word;
    }

    public String getMeans(Word word) {
        Log.e("check_time", "getMeans");
        String means = "";
        List<Mean> list_mean = word.getMeanList();
        for (int i = 0; i < list_mean.size(); i++) {
            means += list_mean.get(i).getMeaning() + ", ";
        }
        means = means.substring(0, means.length() - 2);
        return means;
    }

    public void setUnknownWord(Word word) {
        Log.e("check_time", "setUnknownWord");
        Config.unknow_count++;

        int ex_state = word.getState();
        word.increaseWrongCount();

        if (!word.isWrong()) {
            word.setWrong(true);
            word.setRight(false);
            dbPool.updateRightWrong(false, word.get_id());
        }

        dbPool.insertState0FlagTableElement(word, false);
        dbPool.updateForgettingCurvesByNewInputs(word, Config.OFFLINELESSON, false);
        dbPool.insertReviewTimeToGetMemoryCoachMent(word);
        dbPool.insertLevel(word, false);


        Word word_for_write = dbPool.getWord(word.get_id());
        word.setState(word_for_write.getState());


        if (ex_state > 0) {
            SharedPreferences settings = getSharedPreferences(Config.PREFS_NAME, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
            settings.edit().putInt(MainValue.GpreCheckCurve, settings.getInt(MainValue.GpreCheckCurve, 0) + 1).commit();
        }
    }

    @Override
    public void onBackPressed() {
        finishSampleText();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_popup_sampletext_confirm:
                finishSampleText();
                break;
            default:
                break;
        }

    }

    public void finishSampleText() {
        new AlertDialog.Builder(SampleTextPopup.this)
                .setMessage("모든 문장을 해석하셨나요? 현재 저장된 내용으로 단어장에 반영합니다.")
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                Log.e("check_time", "finishSampleText");
                                dialog.dismiss();



                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setKnownWord();
                                        ArrayList<DBPool.word_log> list = dbPool.get_word_log();
                                        Gson gson = new GsonBuilder().create();
                                        JsonArray myCustomArray = gson.toJsonTree(list).getAsJsonArray();
                                        String word_log = myCustomArray.toString();

                                        new RetrofitService().getOfflineLessonService().retroSubmitStudentText(dbPool.getStudentId(),
                                                managerInfo.getTeacher_id(),
                                                ""+sampleText.getText_id())
                                                .enqueue(new Callback<ManagerData>() {
                                                    @Override
                                                    public void onResponse(Response<ManagerData> response, Retrofit retrofit) {
                                                        if(response.isSuccess()){
                                                            setResult(Activity.RESULT_OK);
                                                            finish();
                                                        }else{
                                                            Log.e(TAG, "retroSubmitStudentText is fail");
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Throwable t) {
                                                        Log.e(TAG, "retroSubmitStudentText onFailure : " + t.toString());
                                                    }
                                                });
                                        new RetrofitService().getStudyInfoService().retroInsertWordLog(dbPool.getStudentId(),
                                                word_log
                                        ).enqueue(new Callback<StudyInfoData>() {
                                            @Override
                                            public void onResponse(Response<StudyInfoData> response, Retrofit retrofit) {

                                            }

                                            @Override
                                            public void onFailure(Throwable t) {
                                                Log.e(TAG, "   Exception retroInsertWordLog : " + t.toString());
                                            }
                                        });
                                        Log.e("check_time", "finish");
                                    }
                                });

                                thread.start();


                            }
                        })
                .setNegativeButton("취소", null).show();
    }

    public void setKnownWord() {
        Log.e("check_time", "setKnownWord");
        for(int i =0; i<list_postion.size(); i++){
            for(int j  =list_postion.size()-1; i<j; j--){
               if(list_postion.get(i).getWord_id() == list_postion.get(j).getWord_id()){
                    list_postion.remove(j);
                   Log.e(TAG, "remove");
                }
            }
        }
        if(list_postion.size()>0){
            dbPool.insertKnownWord(list_postion, getApplicationContext());
        }
    }

    void setActionBar() {
        mActionBar = getActionBar();
        mCustomActionBarView = getLayoutInflater().inflate(R.layout.setting_action_bar, null);
        mActionBar.setCustomView(mCustomActionBarView, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        mBack_Layout = (LinearLayout) mCustomActionBarView.findViewById(R.id.layout_actionbar_back);
        mBack_Layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FlurryAgent.logEvent("FAQActivity:Click_Close_BackButton");
                finish();
            }
        });

        mTitle_TextView = (TextView) mCustomActionBarView.findViewById(R.id.text_actionbar_title);
        mTitle_TextView.setText("지문 학습");
    }
}

