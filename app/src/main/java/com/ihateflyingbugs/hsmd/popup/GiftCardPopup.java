package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.model.PaymentData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class GiftCardPopup extends Activity implements OnClickListener {

    EditText et_utilization_number;

    DBPool db;
    Handler handler;
    String request_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup_payment_utilization);
        try {
            request_id = getIntent().getExtras().getString("payment_request_id", "");
        } catch (Exception e) {
            finish();
        }


        db = DBPool.getInstance(getApplicationContext());

        et_utilization_number = (EditText) findViewById(R.id.et_utilization_number);
        Button bt_use_et_utilization = (Button) findViewById(R.id.bt_confirm_utilization);

        bt_use_et_utilization.setOnClickListener(this);

        handler = new Handler();

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        switch (v.getId()) {
            case R.id.bt_confirm_utilization:
                String number = et_utilization_number.getText().toString();
                new RetrofitService().getPaymentService().retroGiftCard(db.getStudentId(),
                        request_id,
                        et_utilization_number.getText().toString())
                        .enqueue(new Callback<PaymentData>() {
                            @Override
                            public void onResponse(final Response<PaymentData> response, Retrofit retrofit) {
                                if (response.isSuccess()) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            switch (response.body().getResult()) {
                                                case 1:
                                                    new AlertDialog.Builder(GiftCardPopup.this).setMessage("쿠폰 등록에 성공하였습니다. 앞으로 많은 이용 부탁드릴꼐요.")
                                                            .setPositiveButton("확인", null).show();
                                                    setResult(55555);
                                                    finish();
                                                    break;
                                                case 3:
                                                    Toast.makeText(GiftCardPopup.this, "죄송합니다 이미 사용된 쿠폰입니다.\n문제가 있으시다면 고객센터에 문의 바랍니다.", Toast.LENGTH_SHORT).show();
                                                    break;
                                                default:
                                                    Toast.makeText(GiftCardPopup.this, "죄송합니다 등록에 실패하였습니다.\n잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                                    break;

                                            }

                                        }
                                    });
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(GiftCardPopup.this, "죄송합니다 등록에 실패하였습니다.\n쿠폰 번호를 확인하고 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }


                            }

                            @Override
                            public void onFailure(Throwable t) {

                            }
                        });
//				new Async_use_utilization(db.getStudentId(), number, new VocaCallback() {
//
//					@Override
//					public void Resonponse(final JSONObject response) {
//						// TODO Auto-generated method stub
//
//
//						// TODO Auto-generated method stub
//						int result=0;
//						try {
//							result = response.getInt(AsyncResultType.TAG_RESULT);
//							Log.v("kyc", "response : "+response.toString());
//						} catch (JSONException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//						switch (result) {
//							case -1:
//								//userid 입력실패
//								handler.post(new Runnable() {
//
//									@Override
//									public void run() {
//										// TODO Auto-generated method stub
//
//										Toast.makeText(GiftCardPopup.this, "죄송합니다 등록에 실패하였습니다.\n잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
//									}
//								});
//								break;
//							case -2:
//								handler.post(new Runnable() {
//
//									@Override
//									public void run() {
//										// TODO Auto-generated method stub
//
//										Toast.makeText(GiftCardPopup.this, "죄송합니다 이미 사용된 쿠폰입니다.\n문제가 있으시다면 고객센터에 문의 바랍니다.", Toast.LENGTH_SHORT).show();
//									}
//								});
//								//이미 사용된 쿠폰
//
//								break;
//							case -3:
//								handler.post(new Runnable() {
//
//									@Override
//									public void run() {
//										// TODO Auto-generated method stub
//
//										Toast.makeText(GiftCardPopup.this, "죄송합니다 등록되지않은 쿠폰입니다.\n다시 확인해 주세요.", Toast.LENGTH_SHORT).show();
//									}
//								});
//								//사용할수없는번호
//								break;
//							case 0:
//								handler.post(new Runnable() {
//
//									@Override
//									public void run() {
//										// TODO Auto-generated method stub
//
//										Toast.makeText(GiftCardPopup.this, "죄송합니다 등록에 실패하였습니다.\n잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
//									}
//								});
//								//paymentinfo에 넣는거 실패했을때
//
//								break;
//							case 1:
//								handler.post(new Runnable() {
//
//									@Override
//									public void run() {
//										// TODO Auto-generated method stub
//										new AlertDialog.Builder(GiftCardPopup.this).setMessage("쿠폰 등록에 성공하였습니다. 앞으로 많은 이용 부탁드릴꼐요.")
//												.setPositiveButton("확인",null).show();
//										setResult(55555);
//										finish();
//									}
//								});
//								//성공
//
//								break;
//
//						}
//
//					}
//
//					@Override
//					public void Exception() {
//						// TODO Auto-generated method stub
//						handler.post(new Runnable() {
//							@Override
//							public void run() {
//								// TODO Auto-generated method stub
//								Toast.makeText(GiftCardPopup.this, "등록에 실패하였습니다. 잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
//							}
//						});
//					}
//				}).execute();
                break;

            default:
                break;
        }

    }


}
