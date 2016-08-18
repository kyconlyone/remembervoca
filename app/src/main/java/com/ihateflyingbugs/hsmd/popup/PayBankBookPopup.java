package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.model.PaymentData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class PayBankBookPopup extends Activity implements OnClickListener {

	Button bt_confirm_paybankbook;
	Button bt_confirm_parents;
	Button bt_qna_pay;
	Spinner dropdown;

	String product_type[][];
	String items[];

	int current_selected_type_id;

	String DevPayload;

	DBPool db;

	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_paybankbook);

		Intent receiveIntent = getIntent();

		items = new String[3];
		handler = new Handler();
		db = DBPool.getInstance(getApplicationContext());

		// 인앱결제가 아닌 다른방법으로 결제시에 필요한 상품의 정보
		product_type = new String[3][];
		//  첫번째 인덱스 0:3개월, 1:6개월, 2:12개월
		//  두번째 인덱스에 따른 값
		//  0: 기간
		//  1: 가격
		//  2: 블루페이 결제 코드
		//  3: 무통장 입금 결제 코드

		for (int i = 0; i < items.length; i++) {
			String[] temp = receiveIntent.getStringArrayExtra(i + "");
			product_type[i] = temp;
			Log.d("INTENTTTTTT ", temp[0] + ", " + temp[1] + "Receive");
			items[i] = temp[1] + "원 / " + temp[0] + "개월";
		}
		DevPayload = receiveIntent.getStringExtra("DevPayload");
		Log.d("adasdasd", DevPayload);

		dropdown = (Spinner) findViewById(R.id.sp_paymoneylist);

		bt_confirm_paybankbook = (Button) findViewById(R.id.bt_confirm_paybankbook);
		bt_confirm_parents = (Button) findViewById(R.id.bt_confirm_parents);
		bt_qna_pay = (Button) findViewById(R.id.bt_qna_pay);

		bt_confirm_paybankbook.setOnClickListener(this);
		bt_confirm_parents.setOnClickListener(this);
		bt_qna_pay.setOnClickListener(this);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dropdown.setAdapter(adapter);

		dropdown.setSelection(2);
		dropdown.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				current_selected_type_id = position;
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}

		});

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.bt_confirm_paybankbook:

				Log.d("inappP", db.getStudentId() + ", " + DevPayload  + ", " +product_type[current_selected_type_id][3]);
				new RetrofitService().getPaymentService().retroUpdatePaymentRequest(db.getStudentId(),
						DevPayload,
						product_type[current_selected_type_id][3])
						.enqueue(new Callback<PaymentData>() {
							@Override
							public void onResponse(Response<PaymentData> response, Retrofit retrofit) {
								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										Log.d("adasdasd", product_type[current_selected_type_id][3]);
										Intent intent = new Intent(getApplicationContext(), PaymentAccountPopup.class);
										intent.putExtra("DevPayload", DevPayload);
										intent.putExtra("selected_item", product_type[current_selected_type_id]);
										startActivity(intent);
									}
								});
							}

							@Override
							public void onFailure(Throwable t) {
								handler.post(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(getApplicationContext(), "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
									}
								});
							}
						});
				break;

			case R.id.bt_confirm_parents:
				Uri uri = Uri.parse("http://lnslab.com/payment/bluepay/pay.php?product_type="
						+ product_type[current_selected_type_id][2]
						+ "&request_id="+DevPayload);

				intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				break;

			case R.id.bt_qna_pay:
				intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:027438838"));
				startActivity(intent);
				break;

			default:

				break;
		}
	}

}