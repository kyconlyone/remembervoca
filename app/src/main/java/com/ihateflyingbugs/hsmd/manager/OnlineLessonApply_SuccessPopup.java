package com.ihateflyingbugs.hsmd.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.model.UseAppInfoData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class OnlineLessonApply_SuccessPopup extends Activity {
	String TAG = "OnlineLessonApply_SuccessPopup";

	Context mContext;
	SharedPreferences mSharedPreferences;
	DBPool mDBPool;

	//
	Button mOKButton;

	String mName_String;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		Log.e(TAG, "   onCreate");
		setContentView(R.layout.popup_onlinelesson_success);

		mContext = getApplicationContext();
		mSharedPreferences = getSharedPreferences(Config.PREFS_NAME, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		mDBPool = DBPool.getInstance(mContext);

		// init
		mOKButton = (Button)findViewById(R.id.btn_onlinelesson_success_ok);
		mOKButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mName_String = mSharedPreferences.getString(MainValue.GpreName, "");

				//TODO

				new RetrofitService().getUseAppInfoService().getBetaServiceAdmin()
						.enqueue(new Callback<UseAppInfoData>() {
							@Override
							public void onResponse(Response<UseAppInfoData> response, Retrofit retrofit) {
								String mPhoneNumber_String = "01032263632";
								String mSMSMessage = mName_String + " 학생 온라인튜터링 신청";

								SmsManager smsManager = SmsManager.getDefault();
								smsManager.sendTextMessage(mPhoneNumber_String, null, mSMSMessage, null, null);

								Intent intent = new Intent();
								setResult(RESULT_OK, intent);

								OnlineLessonApply_SuccessPopup.this.finish();
							}

							@Override
							public void onFailure(Throwable t) {
								Log.e(TAG, "onFailure : "+ t.toString());
								Intent intent = new Intent();
								setResult(RESULT_CANCELED, intent);

								OnlineLessonApply_SuccessPopup.this.finish();
							}
						});

			}
		});
	}
}
