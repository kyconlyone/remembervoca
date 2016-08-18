package com.ihateflyingbugs.hsmd.popup;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.model.UseAppInfoData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

//import com.google.android.gms.internal.pb;


public class DocPopup extends Activity {

	String TAG = "DocPopup";


	final public static int REQUEST_POPUP_UTIL = 5001;
	final public static int REQUEST_POPUP_COLLECT = 5002;
	final public static int REQUEST_POPUP_KAKAO_BLOCK = 5003;
	final public static int REQUEST_POPUP_FORGET = 5004;
	final public static int REQUEST_POPUP_LOGOUT = 5005;
	final public static int REQUEST_POPUP_WILLPUSH = 5006;
	final public static int REQUEST_POPUP_MEMORYPUSH = 5007;
	final public static int REQUEST_POPUP_HIT = 5008;

	ProgressBar pb_two_contents;
	TextView tv_contents;

	int sort;
	Intent intent;

	Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.popup_twobutton);

		handler= new Handler();

		TextView tv_title = (TextView)findViewById(R.id.tv_two_title);
		tv_contents = (TextView)findViewById(R.id.tv_two_contents);
		Button bt_no_popup = (Button)findViewById(R.id.bt_no_popup);

		RelativeLayout rl_two_contents = (RelativeLayout)findViewById(R.id.rl_two_contents);
		TextView tv_two_kakaocontents = (TextView)findViewById(R.id.tv_two_kakaocontents);

		pb_two_contents = (ProgressBar)findViewById(R.id.pb_two_contents);

		ScrollView sv_two_contents = (ScrollView)findViewById(R.id.sv_two_contents);
		View vi_two_devide = (View)findViewById(R.id.vi_two_devide);
		TextView tv_check_contents = (TextView)findViewById(R.id.tv_check_contents);

		if(getIntent().getBooleanExtra("activity", false) ){

		}

		String title = null;
		String contents =null;

		intent = new Intent();
		sort = getIntent().getIntExtra("title",1);
		sort = REQUEST_POPUP_COLLECT;
		if(sort==REQUEST_POPUP_COLLECT){
			title = "개인정보수집약관";
			pb_two_contents.setVisibility(View.VISIBLE);

			new RetrofitService().getUseAppInfoService().retroGetPolicy()
					.enqueue(new Callback<UseAppInfoData>() {
						@Override
						public void onResponse(final Response<UseAppInfoData> response, Retrofit retrofit) {
							handler.post(new Runnable() {
								public void run() {
									pb_two_contents.setVisibility(View.GONE);
									tv_contents.setText(response.body().getType2());
								}
							});
						}

						@Override
						public void onFailure(Throwable t) {
							Log.e(TAG, "onFailure : "+ t.toString());
						}
					});

		}else if(sort == REQUEST_POPUP_UTIL){
			title = "이용약관";
			pb_two_contents.setVisibility(View.VISIBLE);

			new RetrofitService().getUseAppInfoService().retroGetPolicy()
					.enqueue(new Callback<UseAppInfoData>() {
						@Override
						public void onResponse(final Response<UseAppInfoData> response, Retrofit retrofit) {
							handler.post(new Runnable() {
								public void run() {
									pb_two_contents.setVisibility(View.GONE);
									tv_contents.setText(response.body().getType1());
								}
							});
						}

						@Override
						public void onFailure(Throwable t) {
							Log.e(TAG, "onFailure : "+ t.toString());
						}
					});
		}else if(sort == REQUEST_POPUP_KAKAO_BLOCK){
			title = "카카오톡 차단";
			contents = "카카오톡을 정말 차단하시겠습니까?";
			sv_two_contents.setVisibility(View.GONE);
			tv_check_contents.setVisibility(View.GONE);
			tv_two_kakaocontents.setVisibility(View.VISIBLE);
			tv_two_kakaocontents.setText(contents);
			tv_contents.setText(contents);
		}else if(sort == REQUEST_POPUP_WILLPUSH){
			if(getIntent().getExtras().getBoolean("state")){
				title = "의지푸시를 끄시겠습니까?";
			}else{
				title = "의지푸시를 켜시겠습니까?";
			}
			rl_two_contents.setVisibility(View.GONE);
			tv_check_contents.setVisibility(View.GONE);
			tv_title.setText(title);
		}else if(sort == REQUEST_POPUP_MEMORYPUSH){
			if(getIntent().getExtras().getBoolean("state")){
				title = "기억푸시를 끄시겠습니까?";
			}else{
				title = "기억푸시를 켜시겠습니까?";
			}
			rl_two_contents.setVisibility(View.GONE);
			tv_check_contents.setVisibility(View.GONE);
			tv_title.setText(title);

		}else if(sort == REQUEST_POPUP_HIT){
			if(getIntent().getExtras().getBoolean("state")){
				title = "적중푸시를 끄시겠습니까?";
			}else{
				title = "적중푸시를 켜시겠습니까?";
			}
			rl_two_contents.setVisibility(View.GONE);
			tv_check_contents.setVisibility(View.GONE);
			tv_title.setText(title);

		}else if(sort == REQUEST_POPUP_FORGET){
			DBPool db = DBPool.getInstance(getApplicationContext());
			SharedPreferences mpre = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
			title = "'"+mpre.getString(MainValue.GpreName, "이름없음")+"님이 외운 단어들 중\n"+db.getMforget()+"개가 머릿속에서\n잊혀지려 합니다.";
			rl_two_contents.setVisibility(View.GONE);
			tv_check_contents.setVisibility(View.GONE);
			tv_title.setText(title);

		}else if(sort == REQUEST_POPUP_LOGOUT){
			title = "로그아웃 하시겠습니까?";
			rl_two_contents.setVisibility(View.GONE);
			tv_check_contents.setVisibility(View.GONE);
			tv_title.setText(title);
		}

		tv_title.setText(title);

		bt_no_popup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(sort==REQUEST_POPUP_UTIL){
					intent.putExtra("isOk", false);
					setResult(3690, intent);
				}else if(sort == REQUEST_POPUP_COLLECT){
					intent.putExtra("isOk", false);
					setResult(2580, intent);
				}else{
					setResult(Activity.RESULT_CANCELED);
				}
				finish();
			}
		});
		Button bt_yes_popup = (Button)findViewById(R.id.bt_yes_popup);

		tv_title.setText(title);




		bt_yes_popup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(sort==REQUEST_POPUP_UTIL){
					intent.putExtra("isOk", true);
					setResult(3690, intent);
				}else if(sort == REQUEST_POPUP_COLLECT){
					intent.putExtra("isOk", true);
					setResult(2580, intent);
				}else{
					setResult(Activity.RESULT_OK);
				}
				finish();
			}
		});



	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}



}
