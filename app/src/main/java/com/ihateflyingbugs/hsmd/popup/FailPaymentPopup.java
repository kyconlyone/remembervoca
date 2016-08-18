package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.model.PaymentData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class FailPaymentPopup extends Activity {

	String TAG = "FailPaymentPopup";
	Intent receiveIntent;

	DBPool db;

	String[][] spinner_receive_item;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_failpayment);

		Button bt_failpayment_retry = (Button) findViewById(R.id.bt_failpayment_retry);
		Button bt_failpayment_bankbook = (Button) findViewById(R.id.bt_failpayment_bankbook);

		bt_failpayment_retry.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		db = DBPool.getInstance(getApplicationContext());

		receiveIntent = getIntent();

		bt_failpayment_bankbook.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				new RetrofitService().getPaymentService().retroInsertPaymentRequest(db.getStudentId(),"5")
						.enqueue(new Callback<PaymentData>() {
					@Override
					public void onResponse(Response<PaymentData> response, Retrofit retrofit) {
						String tempDevPayload = response.body().getRequest_id();
						Intent intent = new Intent(FailPaymentPopup.this, PaymentAccountPopup.class);

						for (int i = 0; i < 3; i++) {
							String[] temp = receiveIntent.getStringArrayExtra(i + "");
							intent.putExtra(i + "", temp);
						}
						intent.putExtra("DevPayload", tempDevPayload);
						startActivity(intent);
						finish();
					}

					@Override
					public void onFailure(Throwable t) {
						Log.e(TAG, "onFailure : "+t.toString());

					}
				});

			}
		});

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

}
