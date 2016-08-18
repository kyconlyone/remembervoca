package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.ihateflyingbugs.hsmd.data.AsyncResultType;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.model.PaymentData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.ihateflyingbugs.hsmd.tutorial.MainActivity;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class GetFreeDayPopup extends Activity implements OnClickListener {


String TAG = "GetFreeDayPopup";
	DBPool db;

	int selectedItemIdx = 0;

	Handler handler;

	SharedPreferences mPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_freeday);

		handler = new Handler();
		mPreferences = getSharedPreferences(Config.PREFS_NAME, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE | MODE_MULTI_PROCESS);

		db = DBPool.getInstance(getApplicationContext());

		MainActivity.is1FreeDay = false;

		Button bt_freeday_ok = (Button)findViewById(R.id.bt_freeday_ok);
		Button bt_freeday_cancel = (Button)findViewById(R.id.bt_freeday_cancel);

		bt_freeday_cancel.setOnClickListener(this);
		bt_freeday_ok.setOnClickListener(this);


	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_freeday_cancel:
				finish();
				setResult(55555);
				break;

			case R.id.bt_freeday_ok:

				new RetrofitService().getPaymentService().retroInsertFinishDateAtLeft(db.getStudentId())
						.enqueue(new Callback<PaymentData>() {
							@Override
							public void onResponse(final Response<PaymentData> response, Retrofit retrofit) {
								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										try {
											Log.e(TAG, "onResponse : "+response.body().getResult());
											if(response.body().getResult()==AsyncResultType.RESULT_SUCCESS){
												Toast.makeText(getApplicationContext(), "무료 사용 기간이 연장되었습니다.", Toast.LENGTH_SHORT).show();
												finish();
											}else if(response.body().getResult()==AsyncResultType.RESULT_AREALDY_EXIST){
												Toast.makeText(getApplicationContext(), "무료사용을 하신지 아직 한달이 지나지 않았습니다.\n다음달부터 무료 사용받기가 가능합니다.:D", Toast.LENGTH_SHORT).show();
											}
										} catch (Exception e) {
											// TODO Auto-generated catch block
											Toast.makeText(getApplicationContext(), "죄송합니다. 무료사용 받기 과정에서 문제가 발생했습니다."
													+ "계속해서 문제 발생시 cs@lnslab.com 으로 메일 보내시길 부탁드립니다. 감사합니다.", Toast.LENGTH_SHORT).show();
											e.printStackTrace();
										}
									}
								});
							}

							@Override
							public void onFailure(final Throwable t) {
								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub

										Log.e(TAG, "onFailure : "+t.toString());
										Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
									}
								});
							}
						});

				break;
			default:
				break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		setResult(55555);
	}


}
