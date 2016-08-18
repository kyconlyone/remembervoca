package com.ihateflyingbugs.hsmd.OfflineLesson.OLFragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.OfflineLesson.OLActivity.OriginWordDetailActivity;
import com.ihateflyingbugs.hsmd.OfflineLesson.OLData.OriginWord;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.DBPool;


public class OriginWordFragment extends Fragment {

    static OriginWordFragment originWordFragment;

    public static OriginWordFragment newInstance() {
        if(originWordFragment==null){
            originWordFragment = new OriginWordFragment();
        }
        return originWordFragment;
    }

    final String TAG = "OriginWordFragment";

    DBPool dbPool;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_originword, container, false);
        final LinearLayout ll_originword_fragment = (LinearLayout) view.findViewById(R.id.ll_originword_fragment);
        dbPool = DBPool.getInstance(getActivity().getApplicationContext());

//        new RetrofitService().getOriginWordService().retroGetOriginWordList(dbPool.getStudentId())
//                .enqueue(new Callback<OriginWordData>() {
//                    @Override
//                    public void onResponse(final Response<OriginWordData> response, Retrofit retrofit) {
//                        if(response.isSuccess()){
//                            ll_originword_fragment.removeAllViews();
//                            final List<OriginWord> list_originword = response.body().getOriginwordlist();
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    for(int i=0; i<list_originword.size();i++){
//                                        TextView rowTextView = new TextView(getActivity().getApplicationContext());
//                                        rowTextView.setText(list_originword.get(i).getOrigin_word());
//                                        rowTextView.setTextColor(Color.parseColor("#328dc5"));
//                                        SpannableString spannableString = new SpannableString("\n"+list_originword.get(i).getOrigin_word_mean());
//                                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#545454")), 0, spannableString.length(),0);
//                                        rowTextView.append(spannableString);
//                                        rowTextView =  setOriginWordParam(rowTextView, list_originword.get(i), response.body().getImg_address());
//                                        rowTextView.setId((R.id.origin_word)+i);
//                                        rowTextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f, getResources().getDisplayMetrics()), 1.0f);
//                                        ll_originword_fragment.addView(rowTextView);
//
//                                    }
//                                }
//                            });
//
//                        }else{
//                            Log.e(TAG, "retroGetOriginWordList notSuccess : response "+ response.body().toString());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Throwable t) {
//                        Log.e(TAG, "retroGetOriginWordList onFailure : "+t.toString());
//
//                    }
//                });

        return view;
    }

    public TextView setOriginWordParam(final TextView wordTextView, final OriginWord originWord, final String address){
        wordTextView.setMovementMethod(LinkMovementMethod.getInstance());
        wordTextView.setHighlightColor(Color.TRANSPARENT);
        wordTextView.setPadding(70,70,70,70);
        wordTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        wordTextView.setTextColor(Color.parseColor("#000000"));
        wordTextView.setBackgroundResource(R.drawable.feed_main_bg);
        wordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OriginWord originWord1 = new OriginWord();
                originWord1 = originWord;


                Intent intent = new Intent(getActivity().getApplicationContext(), OriginWordDetailActivity.class);
                intent.putExtra("originword", originWord1);
                intent.putExtra("ImageUrl", address);
                startActivityForResult(intent,5);
            }
        });

        return wordTextView;
    }


}
