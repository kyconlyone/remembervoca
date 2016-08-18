package com.ihateflyingbugs.hsmd.tutorial;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.MyQnA;
import com.ihateflyingbugs.hsmd.model.CouponData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * Created by 영철 on 2016-05-18.
 */
public class RetroTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain);


        new RetrofitService().getBoardService().retroGetQnABoard("1400031280").enqueue(new Callback<List<MyQnA>>() {
            @Override
            public void onResponse(Response<List<MyQnA>> response, Retrofit retrofit) {
                try {
                    final List<MyQnA> QnAList = response.body();
                    for (int i =0; i<QnAList.size();i++){
                        Log.e("qna_log", "QnA title" + QnAList.get(i).getQuestion());
                    }
                }catch (Exception e){
                    Log.e("qna_log", "qna_error : "+ e.toString());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("qna_log", "QnA fail" + t.toString());

            }
        });


        int i =0;

        new RetrofitService().getPaymentService().retroGetUsableCoupon("1400031280").enqueue(new Callback<CouponData>() {
            @Override
            public void onResponse(Response<CouponData> response, Retrofit retrofit) {
                try {
                    Log.e("coupon_log", "coupon : " + response.body().getStatus());
                } catch (Exception e) {
                    Log.e("coupon_log", "coupon : " + e.toString());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("coupon_log", "coupon fail : "+ t.toString());

            }
        });





//        new RetrofitService().getBoardService().retroGetNotice().enqueue(new Callback<List<Feed>>() {
//            @Override
//            public void onResponse(Response<List<Feed>> response, Retrofit retrofit) {
//                List<Feed> notice = response.body();
//                for (int i=0; i<notice.size();i++)
//                    Log.e("notice_log", ""+notice.get(i).getTitle());
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//
//                Log.e("notice_log", "notice fail"+t.toString());
//            }
//        });
    }
}
