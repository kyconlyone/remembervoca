package com.ihateflyingbugs.hsmd.OfflineLesson.OLFragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.ihateflyingbugs.hsmd.OfflineLesson.OLActivity.SampleTextPopup;
import com.ihateflyingbugs.hsmd.OfflineLesson.OLData.SampleText;
import com.ihateflyingbugs.hsmd.OfflineLesson.OLData.SampleTextAdapter;
import com.ihateflyingbugs.hsmd.OfflineLesson.OLData.WordTextView;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.manager.ManagerInfo;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public final class SampleTextFragment extends Fragment {
    String TAG = "SampleTextFragment";
    private static final String KEY_CONTENT = "TestFragment:Content";

    int j = 0;
    String[] str;
    List<WordTextView> list;

    List<SampleText> list_SampleTexts;

    static SampleTextFragment sampleTextFragment;

    public static SampleTextFragment newInstance() {
        if(sampleTextFragment==null){
            sampleTextFragment = new SampleTextFragment();
        }
        return sampleTextFragment;
    }


    private String mContent = "???";
    DBPool dbPool;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list_SampleTexts = new ArrayList<SampleText>();

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    Handler handler;

    LinearLayout ll_sample_text;
    RelativeLayout rl_fragment_sampletext_progress;
    ScrollView sv_fragment_sampletext;
    CircularProgressView progress_view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sampletext, container, false);
        ll_sample_text = (LinearLayout) view.findViewById(R.id.ll_sampletext_fragment);
        SampleTextAdapter sampleTextAdapter = new SampleTextAdapter(getActivity().getApplicationContext(),
                list_SampleTexts);
        dbPool = DBPool.getInstance(getActivity().getApplicationContext());

        rl_fragment_sampletext_progress = (RelativeLayout)view.findViewById(R.id.rl_fragment_sampletext_progress);
        sv_fragment_sampletext = (ScrollView)view.findViewById(R.id.sv_fragment_sampletext);
        progress_view = (CircularProgressView)view.findViewById(R.id.progress_view);
        rl_fragment_sampletext_progress.setVisibility(View.VISIBLE);
        sv_fragment_sampletext.setVisibility(View.GONE);
        progress_view.setColor(Color.parseColor("#00b569"));
        progress_view.startAnimation();

 //       Log.e(TAG, "studnet_id : "+ dbPool.getStudentId()+"  teacher_id : "+ManagerInfo.getInstance(getActivity().getApplicationContext()).getTeacher_id());

        Log.e("check_time", "retroGetSampleTextList Ready");
        new RetrofitService().getOfflineLessonService().retroGetSampleTextList(dbPool.getStudentId(),
                                                                                ""+ ManagerInfo.getInstance(getActivity().getApplicationContext()).getTeacher_id())
//        new RetrofitService().getOfflineLessonService().retroGetSampleTextList("1400001515",
//                                                                                "938852926222081")
                .enqueue(new Callback<List<SampleText>>() {
                    @Override
                    public void onResponse(final Response<List<SampleText>> response, Retrofit retrofit) {

                        Log.e("check_time", "retroGetSampleTextList Get");

                        if(response.isSuccess()){

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for(int i=0; i<response.body().size();i++){
                                        TextView rowTextView = new TextView(getActivity().getApplicationContext());
                                        rowTextView.setText(" "+response.body().get(i).getText());
                                        rowTextView =  setWordTextViewParam(rowTextView, response.body().get(i));
                                        rowTextView.setId((R.id.sample_text)+i);
                                        ll_sample_text.addView(rowTextView);
                                    }

                                    rl_fragment_sampletext_progress.setVisibility(View.GONE);
                                    sv_fragment_sampletext.setVisibility(View.VISIBLE);
                                    progress_view.stopAnimation();

                                    Log.e("check_time", "retroGetSampleTextList Finish");

                                }
                            });

                        }else{
                            Log.e(TAG, "retroGetSampleTextList notSuccess : not success");
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e(TAG, "retroGetSampleTextList onFailure : "+ t.toString());
                    }
                });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }

    public TextView setWordTextViewParam(final TextView wordTextView, final SampleText sampletext){
        wordTextView.setMovementMethod(LinkMovementMethod.getInstance());
        wordTextView.setHighlightColor(Color.TRANSPARENT);
        wordTextView.setPadding(70,70,70,70);
        wordTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        wordTextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f, getResources().getDisplayMetrics()), 1.0f);
        wordTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        wordTextView.setTextColor(Color.parseColor("#000000"));
        wordTextView.setBackgroundResource(R.drawable.feed_main_bg);
        wordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SampleText sampleText = new SampleText();
                sampleText = sampletext;


                Intent intent = new Intent(getActivity().getApplicationContext(), SampleTextPopup.class);
                intent.putExtra("sampletext", sampleText);
                startActivityForResult(intent,5);
            }
        });

        return wordTextView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK){

            rl_fragment_sampletext_progress.setVisibility(View.VISIBLE);
            sv_fragment_sampletext.setVisibility(View.GONE);
            progress_view.setColor(Color.parseColor("#00b569"));
            progress_view.startAnimation();
            ll_sample_text.removeAllViews();
            new RetrofitService().getOfflineLessonService().retroGetSampleTextList(dbPool.getStudentId(),
                    ""+ ManagerInfo.getInstance(getActivity().getApplicationContext()).getTeacher_id())
//        new RetrofitService().getOfflineLessonService().retroGetSampleTextList("1400001515",
//                                                                                "938852926222081")
                    .enqueue(new Callback<List<SampleText>>() {
                        @Override
                        public void onResponse(final Response<List<SampleText>> response, Retrofit retrofit) {

                            Log.e("check_time", "retroGetSampleTextList Get");

                            if(response.isSuccess()){

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for(int i=0; i<response.body().size();i++){
                                            TextView rowTextView = new TextView(getActivity().getApplicationContext());
                                            rowTextView.setText(" "+response.body().get(i).getText());
                                            rowTextView =  setWordTextViewParam(rowTextView, response.body().get(i));
                                            rowTextView.setId((R.id.sample_text)+i);
                                            ll_sample_text.addView(rowTextView);
                                        }

                                        rl_fragment_sampletext_progress.setVisibility(View.GONE);
                                        sv_fragment_sampletext.setVisibility(View.VISIBLE);
                                        progress_view.stopAnimation();

                                        Log.e("check_time", "retroGetSampleTextList Finish");

                                    }
                                });

                            }else{
                                Log.e(TAG, "retroGetSampleTextList notSuccess : response "+ response.body().toString());
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e(TAG, "retroGetSampleTextList onFailure : "+ t.toString());
                        }
                    });
        }

    }
}
