package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.model.PaymentData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.ihateflyingbugs.hsmd.tutorial.SplashActivity;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class PaymentAccountPopup extends Activity implements OnClickListener {

	String TAG = "PaymentAccountPopup";
	TextView tv_account_price;

	Spinner sp_account_payment;

	LinearLayout ll_account_payment_1;
	LinearLayout ll_account_payment_2;
	LinearLayout ll_spinner;

	Button bt_account_payment;
	Button bt_account_payment_close;

	String[] items;
	String[] receive_item;
	String[][] spinner_receive_item;

	String DevPayload;

	DBPool db;

	Handler handler;

	String type_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_payment_account);

		tv_account_price = (TextView) findViewById(R.id.tv_account_price);

		sp_account_payment = (Spinner) findViewById(R.id.sp_account_payment);

		ll_account_payment_1 = (LinearLayout) findViewById(R.id.ll_account_payment_1);
		ll_account_payment_2 = (LinearLayout) findViewById(R.id.ll_account_payment_2);
		ll_spinner = (LinearLayout) findViewById(R.id.ll_spinner);

		bt_account_payment = (Button) findViewById(R.id.bt_account_payment);
		bt_account_payment_close = (Button) findViewById(R.id.bt_account_payment_close);

		db = DBPool.getInstance(getApplicationContext());

		handler = new Handler();

		items = new String[3];

		Intent intent = getIntent();
		DevPayload = intent.getStringExtra("DevPayload");

		spinner_receive_item = new String[3][];

		if (intent.hasExtra("selected_item")) {
			ll_spinner.setVisibility(View.GONE);
			receive_item = intent.getStringArrayExtra("selected_item");
			tv_account_price.setText(receive_item[1]);
		} else {
			for (int i = 0; i < items.length; i++) {
				String[] temp = intent.getStringArrayExtra(i + "");
				spinner_receive_item[i] = temp;
				items[i] = temp[1] + "원 / " + temp[0] + "개월";
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp_account_payment.setAdapter(adapter);

			sp_account_payment.setSelection(2);

			sp_account_payment.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					int index = items[position].indexOf("원");
					tv_account_price.setText(items[position].substring(0, index + 1));
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub

				}
			});
		}

		bt_account_payment.setOnClickListener(this);
		bt_account_payment_close.setOnClickListener(this);
	}

	String phoneNum;
	int i;
	int count=0;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_account_payment:

				if (ll_spinner.getVisibility() == View.VISIBLE) {
					type_id = spinner_receive_item[sp_account_payment.getSelectedItemPosition()][3];
				} else {
					type_id = receive_item[3];
				}

				new RetrofitService().getPaymentService().retroUpdatePaymentRequest(db.getStudentId(),
						DevPayload,
						type_id)
						.enqueue(new Callback<PaymentData>() {
							@Override
							public void onResponse(Response<PaymentData> response, Retrofit retrofit) {
								new RetrofitService().getPaymentService().retroInsertPaymentInfo(DevPayload,
										null,
										null).enqueue(new Callback<PaymentData>() {
									@Override
									public void onResponse(Response<PaymentData> response, Retrofit retrofit) {
										handler.post(new Runnable() {
											@Override
											public void run() {

												ll_account_payment_1.setVisibility(View.GONE);
												ll_account_payment_2.setVisibility(View.VISIBLE);

												TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
												phoneNum = manager.getLine1Number();
												if (phoneNum == null) {

												}
												try {
													if (phoneNum.subSequence(0, 3).equals("+82")) {
														phoneNum = phoneNum.replace("+82", "0");
													}
												} catch (StringIndexOutOfBoundsException e) {
													// TODO: handle exception
													phoneNum = "01000000000";
												}

												final SmsManager mSmsManager = SmsManager.getDefault();

												final String smsMessage[] = {
														"[밀당영단어]\n무통장입금안내입니다.\n입금금액:" + tv_account_price.getText() + "\n입금계좌:우리은행\n1005002350713\n아이헤이트플라잉버그스",
														"*24시간내 입금후,\n입금자명과 사용학생 이름을 010-7101-7935로 보내주세요.\n예)입금자:박헌숙 학생:김형석",
														"블로그 : http://blog.naver.com/mildang_\n홈페이지 : www.lnslab.com\n담당자 : 010-7101-7935" };

												for (i = 0; i < smsMessage.length; i++) {
													Log.d("SMS SEND", smsMessage[i]);
													handler.postDelayed(new Runnable() {

														@Override
														public void run() {
															// TODO Auto-generated method stub
															if(count<3){
																mSmsManager.sendTextMessage(phoneNum, null, smsMessage[count], null, null);
																count++;
															}
														}
													}, 1000);

												}

											}
										});
									}

									@Override
									public void onFailure(Throwable t) {


										Log.e(TAG,"retroInsertPaymentInfo onFaile : "+t.toString());
									}
								});

							}

							@Override
							public void onFailure(final Throwable t) {
								handler.post(new Runnable() {
									@Override
									public void run() {
										Log.e(TAG,"retroUpdatePaymentRequest onFaile : "+t.toString());
										Toast.makeText(getApplicationContext(), "서버 연결상태가 좋지 않습니다. 다시 시도해 주시고 계속적으로 문제가 발생시 앱을 다시 실행시켜 주세요", Toast.LENGTH_LONG).show();
									}
								});
							}
						});
				break;
			case R.id.bt_account_payment_close:
				finishAffinity();
				startActivity(new Intent(PaymentAccountPopup.this, SplashActivity.class));
				break;

			default:
				break;
		}
	}
}
