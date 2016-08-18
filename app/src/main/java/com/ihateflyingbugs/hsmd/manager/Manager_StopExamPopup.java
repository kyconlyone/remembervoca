package com.ihateflyingbugs.hsmd.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.model.ManagerData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Manager_StopExamPopup extends Activity {
	String TAG = "Manager_StopExamPopup";

	Context mContext;
	SharedPreferences mSharedPreferences;
	SharedPreferences.Editor mShared_Editor;

	//
	Button mCancel_Button;
	Button mStopExam_Button;

	int test_id;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			test_id = getIntent().getExtras().getInt("test_id");
		} catch (Exception e) {
			// TODO: handle exception
		}

		Log.e(TAG, ""+test_id);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		Log.i(TAG, "   onCreate");
		setContentView(R.layout.popup_manager_stopexam);

		mContext = getApplicationContext();

		// init
		mCancel_Button = (Button)findViewById(R.id.btn_stopexam_close);
		mStopExam_Button = (Button)findViewById(R.id.btn_stopexam_ok);


		mCancel_Button.setOnClickListener(clickListener);
		mStopExam_Button.setOnClickListener(clickListener);
	}

	OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()) {
				case R.id.btn_stopexam_close: {
					finish();
				} break;

				case R.id.btn_stopexam_ok: {
					new RetrofitService().getManagerService().retroUpdateStopTest(""+test_id,
																					"1")
							.enqueue(new Callback<ManagerData>() {
								@Override
								public void onResponse(Response<ManagerData> response, Retrofit retrofit) {
									setResult(111);
									finish();
								}

								@Override
								public void onFailure(Throwable t) {
									runOnUiThread(new Runnable() {
										public void run() {
											Toast.makeText(Manager_StopExamPopup.this, "왜 안돼지 ", Toast.LENGTH_SHORT).show();
										}
									});
								}
							});
				} break;
			}
		}
	};

}
