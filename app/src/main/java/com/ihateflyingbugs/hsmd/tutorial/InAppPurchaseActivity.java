package com.ihateflyingbugs.hsmd.tutorial;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.example.android.trivialdrivesample.util.IabHelper;
import com.example.android.trivialdrivesample.util.IabResult;
import com.example.android.trivialdrivesample.util.Inventory;
import com.example.android.trivialdrivesample.util.Purchase;
import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.CheckBoxCoupon;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.Crypter;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.indicator.CheckBoxCouponArrayAdapter;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.model.CouponData;
import com.ihateflyingbugs.hsmd.model.PaymentData;
import com.ihateflyingbugs.hsmd.popup.FailPaymentPopup;
import com.ihateflyingbugs.hsmd.popup.GiftCardPopup;
import com.ihateflyingbugs.hsmd.popup.PayBankBookPopup;
import com.ihateflyingbugs.hsmd.popup.SuccessCouponPopUp;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.ihateflyingbugs.hsmd.service.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class InAppPurchaseActivity extends Activity implements OnClickListener {

	//	public final int TYPE_ID = 0;
	//	public final int NOT_DC_PRICE = 1;
	//	public final int PRICE = 2;
	//	public final int PERIOD_MONTH = 3;
	//	public final int IS_SUBSCRIBED = 4;
	//
	//	int price_arr[][]; // 위의 5가지 정보를 저장하는 2차원 배열
	//	String google_product_id[]; // 각 인앱결제 아이템의 고유 코드를 저장하는 배열

	int current_price_idx[];

	ScrollView sc_inapp;

	LinearLayout ll_inapp_first;
	LinearLayout ll_inapp_second;
	LinearLayout ll_inapp_third;

	TextView tv_inapp_period[];

	TextView tv_inapp_auto_text[];

	TextView tv_inapp_sale_price[];

	TextView tv_inapp_expensive_price[];

	TextView tv_inapp_day_price;

	TextView tv_usable_coupon_count;

	Button bt_another_payment;
	Button bt_utilization_payment;






	ProgressBar pb_loading;

	ListView lv_inapp_coupon;

	List<CheckBoxCoupon> items;

	Handler handler;

	String DevPayload;

	final String TAG = "InAppPurchaseActivity";

	static final int RC_REQUEST = 50005;

	IInAppBillingService mService;

	IabHelper mHelper;

	DBPool db;

	int price_code = 1;

	int max_price_code = 1;

	ServiceConnection mServiceConn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IInAppBillingService.Stub.asInterface(service);
		}
	};
	private SharedPreferences mPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_inapp_purchase_2);

		Intent intent = getIntent();

		if (intent.hasExtra("DevPayload")) {
			DevPayload = intent.getStringExtra("DevPayload");
		}

		mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);

		if (DevPayload == null) {
			alert("결제 코드를 가져오지 못하였습니다. 다시 한번 결제를 선택해 주세요.");
			finish();
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		}

		current_price_idx = new int[3];

		handler = new Handler();

		db = DBPool.getInstance(getApplicationContext());

		tv_inapp_period = new TextView[3];
		tv_inapp_auto_text = new TextView[3];
		tv_inapp_expensive_price = new TextView[3];
		tv_inapp_sale_price = new TextView[3];

		sc_inapp = (ScrollView) findViewById(R.id.sc_inapp);

		ll_inapp_first = (LinearLayout) findViewById(R.id.ll_inapp_first);
		ll_inapp_second = (LinearLayout) findViewById(R.id.ll_inapp_second);
		ll_inapp_third = (LinearLayout) findViewById(R.id.ll_inapp_third);


		tv_usable_coupon_count = (TextView) findViewById(R.id.tv_usable_coupon_count);

		tv_inapp_period[0] = (TextView) findViewById(R.id.tv_inapp_first_period);
		tv_inapp_period[1] = (TextView) findViewById(R.id.tv_inapp_second_period);
		tv_inapp_period[2] = (TextView) findViewById(R.id.tv_inapp_third_period);

		tv_inapp_auto_text[0] = (TextView) findViewById(R.id.tv_inapp_auto_text_first);
		tv_inapp_auto_text[1] = (TextView) findViewById(R.id.tv_inapp_auto_text_second);
		tv_inapp_auto_text[2] = (TextView) findViewById(R.id.tv_inapp_auto_text_third);

		tv_inapp_expensive_price[0] = (TextView) findViewById(R.id.tv_inapp_first_expensive_price);
		tv_inapp_expensive_price[1] = (TextView) findViewById(R.id.tv_inapp_second_expensive_price);
		tv_inapp_expensive_price[2] = (TextView) findViewById(R.id.tv_inapp_third_expensive_price);

		tv_inapp_sale_price[0] = (TextView) findViewById(R.id.tv_inapp_first_sale_price);
		tv_inapp_sale_price[1] = (TextView) findViewById(R.id.tv_inapp_second_sale_price);
		tv_inapp_sale_price[2] = (TextView) findViewById(R.id.tv_inapp_third_sale_price);

		pb_loading = (ProgressBar) findViewById(R.id.pb_inapp_loading);
		pb_loading.setProgress(10);

		bt_another_payment = (Button) findViewById(R.id.bt_another_payment);
		bt_utilization_payment = (Button)findViewById(R.id.bt_utilization_payment);

		lv_inapp_coupon = (ListView) findViewById(R.id.lv_inapp_coupon);

		ll_inapp_first.setOnClickListener(this);
		ll_inapp_second.setOnClickListener(this);
		ll_inapp_third.setOnClickListener(this);
		bt_another_payment.setOnClickListener(this);
		bt_utilization_payment.setOnClickListener(this);

		//		google_product_id = new String[3];
		//		price_arr = new int[3][];
		//		for (int i = 0; i < price_arr.length; i++) {
		//			price_arr[i] = new int[6];
		//		}

		tv_inapp_day_price = (TextView) findViewById(R.id.tv_inapp_day_price);

		items = new ArrayList<CheckBoxCoupon>();


		new RetrofitService().getPaymentService().retroGetUsableCoupon(db.getStudentId())
				.enqueue(new Callback<CouponData>() {
					@Override
					public void onResponse(final Response<CouponData> response, Retrofit retrofit) {

						handler.post(new Runnable() {
							@Override
							public void run() {
									CouponData coupon_data = response.body();
									if( coupon_data.getCount()!=0){
										//수정필요
										List<CouponData.Coupon> couponList = coupon_data.getCoupon();
										for (int i = 0; i < couponList.size(); i++) {
											int prime_num = couponList.get(i).getCoupon_type_id();
											String name = couponList.get(i).getCoupon_type_name();
											String explain_coupon = couponList.get(i).getCoupon_type_info();
											String valid_date = couponList.get(i).getStudent_coupon_valid_date();
											Boolean duplication_flag = couponList.get(i).getIs_duplicated() == 0 ? true : false;

											max_price_code *= prime_num;

											final CheckBoxCoupon coupon = new CheckBoxCoupon(prime_num, name, duplication_flag, explain_coupon, valid_date);

											items.add(coupon);
										}

										CheckBoxCouponArrayAdapter adapter = new CheckBoxCouponArrayAdapter(getApplicationContext(), items);

										android.widget.LinearLayout.LayoutParams llparams = (android.widget.LinearLayout.LayoutParams)lv_inapp_coupon.getLayoutParams();
										llparams.height =  0 ;
										for (int i = 0; i < items.size(); i++) {
											llparams.height = llparams.height + Utils.dpToPixels(72, getResources());
										}
										tv_usable_coupon_count.setText("사용 가능한 쿠폰이 있습니다.");
										lv_inapp_coupon.setLayoutParams(llparams);

										lv_inapp_coupon.setAdapter(adapter);
									}else{
										tv_usable_coupon_count.setVisibility(View.GONE);
									}

							}
						});
					}

					@Override
					public void onFailure(Throwable t) {

					}
				});

		lv_inapp_coupon.setClickable(true);

		lv_inapp_coupon.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final CheckBoxCoupon temp = (CheckBoxCoupon) parent.getItemAtPosition(position);

				Context context = getApplicationContext();

				// TODO Auto-generated method stub
				CheckBox cb_coupon = (CheckBox) view.findViewById(R.id.cb_coupon);
				LinearLayout ll = (LinearLayout)view.findViewById(R.id.ll_item_coupon);
				cb_coupon.setChecked(!cb_coupon.isChecked());
				final boolean isChecked = cb_coupon.isChecked();

				if(((CheckBoxCouponArrayAdapter) lv_inapp_coupon.getAdapter()).isAllItemChecked()){
					tv_usable_coupon_count.setVisibility(View.GONE);
				} else {
					tv_usable_coupon_count.setVisibility(View.VISIBLE);
				}


				if (isChecked) {
					price_code *= temp.getPrimeNum();
					CheckBoxCoupon cbc = items.get(position);
					if(cbc.getCouponExplain().equals("")){
						ll.setBackgroundResource(R.drawable.bg_purchase_coupon_blue_selected);
					}else{
						ll.setBackgroundResource(R.drawable.bg_purchase_coupon_red_selected);
					}
				} else {
					price_code /= temp.getPrimeNum();
					CheckBoxCoupon cbc = items.get(position);
					if(cbc.getCouponExplain().equals("")){
						ll.setBackgroundResource(R.drawable.bg_purchase_coupon_blue_ready);
					}else{
						ll.setBackgroundResource(R.drawable.bg_purchase_coupon_red_ready);
					}
				}
				Log.d("CouponText", price_code+ "" );

				for (int i = 0; i < tv_inapp_auto_text.length; i++) {

					int period = 1;

					if (i == 0) {
						period = 1;
					} else if (i == 1) {
						period = 6;
					} else if (i == 2) {
						period = 12;
					}

					for (int j = 0; j < Config.usable_purchase[0].length; j++) {

						if (Config.usable_purchase[0][j][Config.PEROID_MONTH] != period
								|| Config.usable_purchase[0][j][Config.PRICE_CODE] != price_code) {
							continue;
						} else {
							if (Config.usable_purchase[0][j][Config.IS_SUBSCRIBED] != 0) {
								tv_inapp_auto_text[i].setVisibility(View.VISIBLE);
							} else {
								tv_inapp_auto_text[i].setVisibility(View.GONE);
							}

							if (Config.usable_purchase[0][j][Config.NOT_DC_PRICE] != 0) {
								tv_inapp_expensive_price[i].setVisibility(View.VISIBLE);
								if (Config.usable_purchase[0][j][Config.NOT_DC_PRICE] < 1000) {
									tv_inapp_expensive_price[i].setText(Config.usable_purchase[0][j][Config.NOT_DC_PRICE]);
								} else {
									tv_inapp_expensive_price[i].setText(Config.usable_purchase[0][j][Config.NOT_DC_PRICE]
											/ 1000
											+ ","
											+ (Config.usable_purchase[0][j][Config.NOT_DC_PRICE] + "")
											.substring((Config.usable_purchase[0][j][Config.NOT_DC_PRICE] + "").length() - 3));
								}
							} else {
								tv_inapp_expensive_price[i].setVisibility(View.GONE);
							}

							if (Config.usable_purchase[0][j][Config.PRICE] < 1000) {
								tv_inapp_sale_price[i].setText(Config.usable_purchase[0][j][Config.PRICE]);
							} else {
								tv_inapp_sale_price[i].setText(Config.usable_purchase[0][j][Config.PRICE]
										/ 1000
										+ ","
										+ (Config.usable_purchase[0][j][Config.PRICE] + "")
										.substring((Config.usable_purchase[0][j][Config.PRICE] + "").length() - 3));
							}
							current_price_idx[i] = j;
						}
					}
				}

			}
		});

		for (int i = 0; i < tv_inapp_auto_text.length; i++) {

			int period = 1;

			if (i == 0) {
				period = 1;
			} else if (i == 1) {
				period = 6;
			} else if (i == 2) {
				period = 12;
			}

			for (int j = 0; j < Config.usable_purchase[0].length; j++) {

				if (Config.usable_purchase[0][j][Config.PEROID_MONTH] != period || Config.usable_purchase[0][j][Config.PRICE_CODE] != price_code) {
					continue;
				} else {
					if (Config.usable_purchase[0][j][Config.IS_SUBSCRIBED] != 0) {
						tv_inapp_auto_text[i].setVisibility(View.VISIBLE);
					} else {
						tv_inapp_auto_text[i].setVisibility(View.GONE);
					}

					if (Config.usable_purchase[0][j][Config.NOT_DC_PRICE] != 0) {
						tv_inapp_expensive_price[i].setVisibility(View.VISIBLE);
						if (Config.usable_purchase[0][j][Config.NOT_DC_PRICE] < 1000) {
							tv_inapp_expensive_price[i].setText(Config.usable_purchase[0][j][Config.NOT_DC_PRICE]);
						} else {
							tv_inapp_expensive_price[i].setText(Config.usable_purchase[0][j][Config.NOT_DC_PRICE]
									/ 1000
									+ ","
									+ (Config.usable_purchase[0][j][Config.NOT_DC_PRICE] + "")
									.substring((Config.usable_purchase[0][j][Config.NOT_DC_PRICE] + "").length() - 3));
						}
					} else {
						tv_inapp_expensive_price[i].setVisibility(View.GONE);
					}

					if (Config.usable_purchase[0][j][Config.PRICE] < 1000) {
						tv_inapp_sale_price[i].setText(Config.usable_purchase[0][j][Config.PRICE]);
					} else {
						tv_inapp_sale_price[i].setText(Config.usable_purchase[0][j][Config.PRICE]
								/ 1000
								+ ","
								+ (Config.usable_purchase[0][j][Config.PRICE] + "").substring((Config.usable_purchase[0][j][Config.PRICE] + "")
								.length() - 3));
					}
					current_price_idx[i] = j;
				}
			}
		}

		// new Async_get_product_type(new VocaCallback() {
		// @Override
		// public void Resonponse(final JSONObject response) {
		// final JSONObject json[] = new JSONObject[3];
		// handler.post(new Runnable() {
		// @Override
		// public void run() {
		// try {
		// sc_inapp.setVisibility(View.VISIBLE);
		// pb_loading.setVisibility(View.GONE);
		// for (int i = 0; i < json.length; i++) {
		// json[i] = response.getJSONObject(i + "");
		// price_arr[i][TYPE_ID] = json[i].getInt("type_id");
		// price_arr[i][NOT_DC_PRICE] = json[i].getInt("not_dc_price");
		// price_arr[i][PRICE] = json[i].getInt("price");
		// price_arr[i][PERIOD_MONTH] = json[i].getInt("period_month");
		// price_arr[i][IS_SUBSCRIBED] = json[i].getInt("is_subscribed");
		//
		// if(json[i].getString("google_product_id") != null){
		// google_product_id[i] = json[i].getString("google_product_id");
		// }
		//
		// tv_inapp_period[i].setText(price_arr[i][PERIOD_MONTH] + "");
		//
		// if (price_arr[i][IS_SUBSCRIBED] != 0) {
		// tv_inapp_auto_text[i].setVisibility(View.VISIBLE);
		// } else {
		// tv_inapp_auto_text[i].setVisibility(View.GONE);
		// }
		//
		// if (price_arr[i][NOT_DC_PRICE] != 0) {
		// if (price_arr[i][NOT_DC_PRICE] < 1000) {
		// tv_inapp_expensive_price[i].setText(price_arr[i][NOT_DC_PRICE]);
		// } else {
		// tv_inapp_expensive_price[i].setText(price_arr[i][NOT_DC_PRICE] / 1000
		// + ","
		// + (price_arr[i][NOT_DC_PRICE] +
		// "").substring((price_arr[i][NOT_DC_PRICE] + "").length() - 3));
		// }
		// } else {
		// tv_inapp_expensive_price[i].setVisibility(View.GONE);
		// }
		//
		// if (price_arr[i][PRICE] < 1000) {
		// tv_inapp_sale_price[i].setText(price_arr[i][PRICE]);
		// } else {
		// tv_inapp_sale_price[i].setText(price_arr[i][PRICE] / 1000 + ","
		// + (price_arr[i][PRICE] + "").substring((price_arr[i][PRICE] +
		// "").length() - 3));
		// }
		// }
		//
		// SpannableStringBuilder spb = new SpannableStringBuilder();
		// String str[] = { "하루 ", (price_arr[0][PRICE] /
		// (price_arr[0][PERIOD_MONTH] * 30)) + "원", "으로 ", "수능 영단어 마스터", "하세요!"
		// };
		// SpannableString s[] = { new SpannableString(str[1]), new
		// SpannableString(str[3]) };
		//
		// s[0].setSpan(new ForegroundColorSpan(Color.rgb(0xe9, 0x4f, 0x37)), 0,
		// str[1].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// s[1].setSpan(new ForegroundColorSpan(Color.rgb(0x31, 0x6f, 0x9b)), 0,
		// str[3].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// s[1].setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
		// str[3].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		//
		// spb.append(str[0]);
		// spb.append(s[0]);
		// spb.append(str[2]);
		// spb.append(s[1]);
		// spb.append(str[4]);
		//
		// tv_inapp_day_price.setText(spb);
		//
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// }
		// });
		// }
		//
		// @Override
		// public void Exception() {
		// Toast.makeText(getApplicationContext(), "잠시후 다시 시도해 주세요",
		// Toast.LENGTH_LONG).show();
		// }
		// }).execute();

		handler = new Handler();

		Intent service_intent= new Intent().setAction("com.android.vending.billing.InAppBillingService.BIND");
		service_intent.setPackage("com.ihateflyingbugs.hsmd");
		bindService(service_intent, mServiceConn, Context.BIND_AUTO_CREATE);

		if (!(mPreference.getString("Word_Pass", "hi").length() < 5)) {
			try {

				Crypter crypter = new Crypter(mPreference.getString("Word_Pass", "hi"));
				String res = new String(crypter.decrypt(Config.base64EncodedPublicKey), "UTF-8" );
				mHelper = new IabHelper(this,res);
				mHelper.enableDebugLogging(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			alert("결제 코드를 불러올수 없습니다. 앱을 종료후 다시 시도해 주세요.");
			finish();
		}

		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

			@Override
			public void onIabSetupFinished(IabResult result) {
				// TODO Auto-generated method stub
				if (!result.isSuccess()) {
					// 구매오류처리 ( 토스트하나 띄우고 결제팝업 종료시키면 되겠습니다 )
					return;
				}

				if (mHelper == null) {
					return;
				}

				Log.d(TAG, "Setup successful. Querying inventory.");
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}

		});

	}

	@Override
	public void onClick(View v) {

		int period = 1;
		Intent intent;
		switch (v.getId()) {
			case R.id.ll_inapp_first:
				// price_code[0];

				FlurryAgent.logEvent(TAG+":Purchage"+String.valueOf(Config.usable_purchase[0][current_price_idx[0]][Config.TYPE_ID]));

				if (DevPayload == null) {
					alert("결제 코드를 가져오지 못하였습니다. 다시 한번 결제를 선택해 주세요.");
					finish();
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}

				final int First_Button = 0;

				Log.d("inappP", db.getStudentId() + ", " + DevPayload  + ", " + Config.usable_purchase[0][current_price_idx[First_Button]][Config.TYPE_ID]);

				new RetrofitService().getPaymentService().retroUpdatePaymentRequest(db.getStudentId(),
																					DevPayload,
																					""+Config.usable_purchase[0][current_price_idx[First_Button]][Config.TYPE_ID])
						.enqueue(new Callback<PaymentData>() {
							@Override
							public void onResponse(Response<PaymentData> response, Retrofit retrofit) {
								Log.d("inappP", Config.usable_google_product_id[0][current_price_idx[First_Button]] + ", " + String.valueOf(Config.usable_purchase[0][current_price_idx[First_Button]][Config.TYPE_ID]));
								mHelper.launchPurchaseFlow(InAppPurchaseActivity.this, Config.usable_google_product_id[0][current_price_idx[First_Button]], IabHelper.ITEM_TYPE_SUBS, RC_REQUEST,
										mPurchaseFinishedListener, DevPayload);
							}

							@Override
							public void onFailure(Throwable t) {
								alert("서버 연결상태가 좋지 않습니다. 다시 시도해 주시고 계속적으로 문제가 발생시 앱을 다시 실행시켜 주세요");
							}
						});

				break;

			case R.id.ll_inapp_second:
				// price_code[1];

				FlurryAgent.logEvent(TAG+":Purchage"+String.valueOf(Config.usable_purchase[0][current_price_idx[1]][Config.TYPE_ID]));
				if (DevPayload == null) {
					alert("결제 코드를 가져오지 못하였습니다. 다시 한번 결제를 선택해 주세요.");
					finish();
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}

				final int Second_Button = 1;

				Log.d("inappP", Config.usable_google_product_id[0][current_price_idx[Second_Button]] + ", " + String.valueOf(Config.usable_purchase[0][current_price_idx[Second_Button]][Config.TYPE_ID])  + ", " + current_price_idx[Second_Button]);


				new RetrofitService().getPaymentService().retroUpdatePaymentRequest(db.getStudentId(),
						DevPayload,
						String.valueOf(Config.usable_purchase[0][current_price_idx[Second_Button]][Config.TYPE_ID]))
						.enqueue(new Callback<PaymentData>() {
							@Override
							public void onResponse(Response<PaymentData> response, Retrofit retrofit) {
								mHelper.launchPurchaseFlow(InAppPurchaseActivity.this, Config.usable_google_product_id[0][current_price_idx[Second_Button]], RC_REQUEST, mPurchaseFinishedListener,
										DevPayload);
							}

							@Override
							public void onFailure(Throwable t) {
								alert("서버 연결상태가 좋지 않습니다. 다시 시도해 주시고 계속적으로 문제가 발생시 앱을 다시 실행시켜 주세요");
							}
						});

				break;

			case R.id.ll_inapp_third:
				// price_code[2];

				FlurryAgent.logEvent(TAG+":Purchage"+String.valueOf(Config.usable_purchase[0][current_price_idx[2]][Config.TYPE_ID]));
				if (DevPayload == null) {
					alert("결제 코드를 가져오지 못하였습니다. 다시 한번 결제를 선택해 주세요.");
					finish();
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}

				final int Third_Button = 2;

				Log.d("inappP", Config.usable_google_product_id[0][current_price_idx[Third_Button]] + ", " + String.valueOf(Config.usable_purchase[0][current_price_idx[Third_Button]][Config.TYPE_ID]) + ", " + current_price_idx[Third_Button]);

				new RetrofitService().getPaymentService().retroUpdatePaymentRequest(db.getStudentId(),
						DevPayload,
						String.valueOf(Config.usable_purchase[0][current_price_idx[Third_Button]][Config.TYPE_ID]))
						.enqueue(new Callback<PaymentData>() {
							@Override
							public void onResponse(Response<PaymentData> response, Retrofit retrofit) {
								mHelper.launchPurchaseFlow(InAppPurchaseActivity.this, Config.usable_google_product_id[0][current_price_idx[Third_Button]], RC_REQUEST, mPurchaseFinishedListener,
										DevPayload);
							}

							@Override
							public void onFailure(Throwable t) {
								alert("서버 연결상태가 좋지 않습니다. 다시 시도해 주시고 계속적으로 문제가 발생시 앱을 다시 실행시켜 주세요");
							}
						});

				break;

			case R.id.bt_another_payment:

				FlurryAgent.logEvent(TAG+":ClickOtherPayment");
				new RetrofitService().getPaymentService().retroInsertPaymentRequest(db.getStudentId(),"2")
						.enqueue(new Callback<PaymentData>() {
							@Override
							public void onResponse(Response<PaymentData> response, Retrofit retrofit) {
								String tempDevPayload = response.body().getRequest_id();
								Log.e(TAG, "" + tempDevPayload);

								Intent intent = new Intent(InAppPurchaseActivity.this, PayBankBookPopup.class);
								intent.putExtra("DevPayload", tempDevPayload);
								for (int i = 0; i < tv_inapp_auto_text.length; i++) {

									int period = 3;

									if (i == 0) {
										period = 3;
									} else if (i == 1) {
										period = 6;
									} else if (i == 2) {
										period = 12;
									}

									String priceArr[] = new String[4];
									// 인앱결제가 아닌 다른방법으로 결제시에 필요한 상품의 정보들을 저장해서 액티비티를 부른다.
									// 0: 기간
									// 1: 가격
									// 2: 블루페이 결제 코드
									// 3: 무통장 입금 결제 코드
									priceArr[0] = period + "";

									for (int j = 0; j < Config.usable_purchase[1].length; j++) {

										if (Config.usable_purchase[1][j][Config.PEROID_MONTH] != period
												|| Config.usable_purchase[1][j][Config.PRICE_CODE] != max_price_code) {
											continue;
										} else {
											if (Config.usable_purchase[1][j][Config.PRICE] < 1000) {
												priceArr[1] = Config.usable_purchase[1][j][Config.PRICE] + "";
												priceArr[2] = Config.usable_purchase[1][j][Config.TYPE_ID] + "";
												priceArr[3] = Config.usable_purchase[2][j][Config.TYPE_ID] + "";
												intent.putExtra(i + "", priceArr);
											} else {
												priceArr[1] = Config.usable_purchase[1][j][Config.PRICE]
														/ 1000
														+ ","
														+ (Config.usable_purchase[1][j][Config.PRICE] + "")
														.substring((Config.usable_purchase[1][j][Config.PRICE] + "").length() - 3) + "";
												priceArr[2] = Config.usable_purchase[1][j][Config.TYPE_ID] + "";
												priceArr[3] = Config.usable_purchase[2][j][Config.TYPE_ID] + "";
												intent.putExtra(i + "", priceArr);
											}
										}
									}
								}
								Log.e(TAG, "" + tempDevPayload);
								startActivity(intent);

							}

							@Override
							public void onFailure(Throwable t) {

							}
						});

				break;

			case R.id.bt_utilization_payment: {
				// TODO
				new RetrofitService().getPaymentService().retroInsertPaymentRequest(db.getStudentId(),"8")
						.enqueue(new Callback<PaymentData>() {
							@Override
							public void onResponse(Response<PaymentData> response, Retrofit retrofit) {
								FlurryAgent.logEvent(TAG+":ClickUtilization");
								Intent util_intent = new Intent(InAppPurchaseActivity.this, GiftCardPopup.class);
								util_intent.putExtra("payment_request_id", response.body().getRequest_id());
								startActivityForResult(util_intent, 44444);
							}

							@Override
							public void onFailure(Throwable t) {
								Log.e(TAG, "retroInsertPaymentRequest onFailure:  "+ t.toString());
							}
						});

			} break;

		}
	}

	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

			// mHelper 객체가 소거되었다면 종료
			if (mHelper == null)
				return;

			if (result.isFailure()) {
				Intent intent = new Intent(getApplicationContext(), FailPaymentPopup.class);

				for (int i = 0; i < tv_inapp_auto_text.length; i++) {

					int period = 3;

					if (i == 0) {
						period = 3;
					} else if (i == 1) {
						period = 6;
					} else if (i == 2) {
						period = 12;
					}

					String priceArr[] = new String[4];
					// 인앱결제가 아닌 다른방법으로 결제시에 필요한 상품의 정보들을 저장해서 액티비티를 부른다.
					// 0: 기간
					// 1: 가격
					// 2: 블루페이 결제 코드
					// 3: 무통장 입금 결제 코드
					priceArr[0] = period + "";

					for (int j = 0; j < Config.usable_purchase[1].length; j++) {

						if (Config.usable_purchase[1][j][Config.PEROID_MONTH] != period
								|| Config.usable_purchase[1][j][Config.PRICE_CODE] != max_price_code) {
							continue;
						} else {
							if (Config.usable_purchase[1][j][Config.PRICE] < 1000) {
								priceArr[1] = Config.usable_purchase[1][j][Config.PRICE] + "";
								priceArr[2] = Config.usable_purchase[1][j][Config.TYPE_ID] + "";
								priceArr[3] = Config.usable_purchase[2][j][Config.TYPE_ID] + "";
								intent.putExtra(i + "", priceArr);
							} else {
								priceArr[1] = Config.usable_purchase[1][j][Config.PRICE]
										/ 1000
										+ ","
										+ (Config.usable_purchase[1][j][Config.PRICE] + "")
										.substring((Config.usable_purchase[1][j][Config.PRICE] + "").length() - 3) + "";
								priceArr[2] = Config.usable_purchase[1][j][Config.TYPE_ID] + "";
								priceArr[3] = Config.usable_purchase[2][j][Config.TYPE_ID] + "";
								intent.putExtra(i + "", priceArr);
							}
						}
					}
				}
				startActivity(intent);
				setWaitScreen(false);
				return;
			}

			Log.d(TAG, "Purchase successful.");
			if (purchase.getItemType().equals(IabHelper.ITEM_TYPE_SUBS)) {
				Log.d(TAG, "verify_subs, and send async");
				verify_Subs_DeveloperPayload(purchase);
			} else if (purchase.getItemType().equals(IabHelper.ITEM_TYPE_INAPP)) {
				Log.d(TAG, "verify_inapp, and send async");
				verify_Nomal_DeveloperPayload(purchase);
			} else {
				Log.d(TAG, "verify_else, and send async  :" + purchase.getItemType());
				verify_Nomal_DeveloperPayload(purchase);
			}

		}
	};

	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
			Log.d(TAG, "Query inventory finished.");

			// mHelper가 소거되었다면 종료
			if (mHelper == null)
				return;

			// getPurchases()가 실패하였다면 종료
			if (result.isFailure()) {
				// complain("Failed to query inventory: " + result);
				return;
			}

			Log.d(TAG, "Query inventory was successful.");

			/*
			 * 유저가 보유중인 각각의 아이템을 체크합니다. 여기서 developerPayload가 정상인지 여부를 확인합니다.
			 * 자세한 사항은 verifyDeveloperPayload()를 참고하세요.
			 */

			// 월 자동 결제를 구독중입니까 ?

			Purchase month_Purchase = inventory.getPurchase(Config.usable_google_product_id[0][current_price_idx[0]]);
			Log.d("inappP", Config.usable_google_product_id[0][current_price_idx[0]] + ", " + current_price_idx[0]);
			if (month_Purchase != null) {
				verify_Subs_DeveloperPayload(month_Purchase);
			}

			// 이용권을 가지고 있는가 ? 가지고 있으면 날짜를 채워줍니다.
			Purchase gasPurchase = inventory.getPurchase(Config.usable_google_product_id[0][current_price_idx[1]]);
			Log.d("inappP", Config.usable_google_product_id[0][current_price_idx[1]] + ", " + current_price_idx[1]);
			if (gasPurchase != null) {
				Log.d(TAG, "We have item 1. Consuming it.");
				// 여기서 먼저 서버에다가 소비한다고 보낸다 성공하면 컨슘해준다 (실패하면 패스)
				verify_Nomal_DeveloperPayload(gasPurchase);
			}

			Purchase gasPurchase2 = inventory.getPurchase(Config.usable_google_product_id[0][current_price_idx[2]]);
			Log.d("inappP", Config.usable_google_product_id[0][current_price_idx[2]] + ", " + current_price_idx[2]);
			if (gasPurchase2 != null) {
				Log.d(TAG, "We have item 2. Consuming it.");
				verify_Nomal_DeveloperPayload(gasPurchase2);
				return;
			}

			// updateUi();
			setWaitScreen(false);
			Log.d(TAG, "Initial inventory query finished; enabling main UI.");
		}
	};

	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {

			// mHelper가 소거되었다면 종료
			if (mHelper == null)
				return;

			// 이 샘플에서는 "관리되지 않는 제품"은 "가스" 한가지뿐이므로 상품에 대한 체크를 하지 않습니다.
			// 하지만 다수의 제품이 있을 경우 상품 아이디를 비교하여 처리할 필요가 있습니다.
			if (result.isSuccess()) {
				String month = null;
				// 성공적으로 소진되었다면 상품의 효과를 게임상에 적용합니다. 여기서는 가스를 충전합니다.
				if (purchase.getSku().equals(Config.usable_google_product_id[0][current_price_idx[1]])) {
					Log.d(TAG, "Consumption successful. Provisioning. Month_pay1");

					// 여기서 6달을 늘려줌. 앱에서
					new RetrofitService().getPaymentService().retroInsertGoogleItemConsume(db.getStudentId(),  purchase.getDeveloperPayload());
				} else if (purchase.getSku().equals(Config.usable_google_product_id[0][current_price_idx[2]])) {
					Log.d(TAG, "Consumption successful. Provisioning. Month_pay2");
					// 여기선 12개월
					// 컨숨 완료 플레그 필요할듯

					new RetrofitService().getPaymentService().retroInsertGoogleItemConsume(db.getStudentId(),  purchase.getDeveloperPayload());
					finishAffinity();
					startActivity(new Intent(InAppPurchaseActivity.this, SplashActivity.class));
				}
				// 앱안에서의 데이터를 변경 ()
			} else {
			}
			// updateUi();
			setWaitScreen(false);
			Log.d(TAG, "End consumption flow.");
		}
	};

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		Log.d(TAG, "Showing alert dialog: " + message);
		bld.create().show();
	}


	/*
	 * TODO: 위의 그림에서 설명하였듯이 로컬 저장소 또는 원격지로부터 미리 저장해둔 developerPayload값을 꺼내 변조되지
	 * 않았는지 여부를 확인합니다.
	 *
	 * 이 payload의 값은 구매가 시작될 때 랜덤한 문자열을 생성하는것은 충분히 좋은 접근입니다. 하지만 두개의 디바이스를 가진
	 * 유저가 하나의 디바이스에서 결제를 하고 다른 디바이스에서 검증을 하는 경우가 발생할 수 있습니다. 이 경우 검증을 실패하게
	 * 될것입니다. 그러므로 개발시에 다음의 상황을 고려하여야 합니다.
	 * `
	 * 1. 두명의 유저가 같은 아이템을 구매할 때, payload는 같은 아이템일지라도 달라야 합니다. 두명의 유저간 구매가 이어져서는
	 * 안됩니다.
	 *
	 * 2. payload는 앱을 두대를 사용하는 유저의 경우에도 정상적으로 동작할 수 있어야 합니다. 이 payload값을 저장하고
	 * 검증할 수 있는 자체적인 서버를 구축하는것을 권장합니다.
	 */

	public void verify_Subs_DeveloperPayload(final Purchase p) {
		String payload = p.getDeveloperPayload();

		new RetrofitService().getPaymentService().retroInsertPaymentInfo(p.getDeveloperPayload(),
																	p.getSignature(),
																	p.getOrderId()).enqueue(new Callback<PaymentData>() {
			@Override
			public void onResponse(Response<PaymentData> response, Retrofit retrofit) {
				Log.d(TAG, "PayLoad is exist");

				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (p.getSku().equals(Config.usable_google_product_id[0][current_price_idx[0]])) {
							// "무한 가스"를 구독하였다면 바로 적용
							Log.d(TAG, "Infinite gas subscription purchased.");
							alert("월 결제를 구입해주셔서 감사합니다. 앱을 재시작하면 적용됩니다.");
							// 기간 늘려준다
							setWaitScreen(false);
							finishAffinity();
							startActivity(new Intent(InAppPurchaseActivity.this, SplashActivity.class));
						}
					}
				});
			}

			@Override
			public void onFailure(Throwable t) {
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Log.e(TAG, "PayLoad is not exist");
						setWaitScreen(false);
						finishAffinity();
						startActivity(new Intent(InAppPurchaseActivity.this, SplashActivity.class));
						return;
					}
				});
			}
		});

	}

	public void verify_Nomal_DeveloperPayload(final Purchase p) {

		String payload = p.getDeveloperPayload();

		new RetrofitService().getPaymentService().retroInsertPaymentInfo(p.getDeveloperPayload(),
				p.getSignature(),
				p.getOrderId()).enqueue(new Callback<PaymentData>() {
			@Override
			public void onResponse(Response<PaymentData> response, Retrofit retrofit) {
				Log.d(TAG, "PayLoad is exist");

				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (p.getSku().equals(Config.usable_google_product_id[0][current_price_idx[2]])) {
							// "프리미엄 업그레이드"를 구매하였다면 바로 적용
							Log.d(TAG, "먼스 페이2을 샀습니다 잘쓰세요");
							mHelper.consumeAsync(p, mConsumeFinishedListener);
							alert("구입해주셔서 감사합니다. 앱을 재시작하면 적용됩니다.");
							setWaitScreen(false);
						} else if (p.getSku().equals(Config.usable_google_product_id[0][current_price_idx[1]])) {
							// 가스탱크의 1/4을 채워주는 "가스" 상품을 구매하였다면 소진 진행
							Log.d(TAG, "먼스 페이1을 샀습니다 잘쓰세요");
							mHelper.consumeAsync(p, mConsumeFinishedListener);
							alert("구입해주셔서 감사합니다. 앱을 재시작하면 적용됩니다.");
							setWaitScreen(false);
						}
					}
				});
			}

			@Override
			public void onFailure(Throwable t) {
				Log.e(TAG, "PayLoad is not exist");

				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						setWaitScreen(false);
						finishAffinity();
						startActivity(new Intent(InAppPurchaseActivity.this, SplashActivity.class));
						return;
					}
				});
			}
		});

	}

	boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();

		/*
		 * TODO: 위의 그림에서 설명하였듯이 로컬 저장소 또는 원격지로부터 미리 저장해둔 developerPayload값을 꺼내
		 * 변조되지 않았는지 여부를 확인합니다.
		 *
		 * 이 payload의 값은 구매가 시작될 때 랜덤한 문자열을 생성하는것은 충분히 좋은 접근입니다. 하지만 두개의 디바이스를
		 * 가진 유저가 하나의 디바이스에서 결제를 하고 다른 디바이스에서 검증을 하는 경우가 발생할 수 있습니다. 이 경우 검증을
		 * 실패하게 될것입니다. 그러므로 개발시에 다음의 상황을 고려하여야 합니다.
		 *
		 * 1. 두명의 유저가 같은 아이템을 구매할 때, payload는 같은 아이템일지라도 달라야 합니다. 두명의 유저간 구매가
		 * 이어져서는 안됩니다.
		 *
		 * 2. payload는 앱을 두대를 사용하는 유저의 경우에도 정상적으로 동작할 수 있어야 합니다. 이 payload값을
		 * 저장하고 검증할 수 있는 자체적인 서버를 구축하는것을 권장합니다.
		 */

		// 서버에다가 보내자

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		System.out.println("requestCode : " + requestCode);

		System.out.println("resultCode : " + resultCode);

		if (mHelper == null)
			return;

		Log.e("activityresult_check", "requestCode  : "+requestCode);
		if(requestCode==44444&&resultCode==55555){
			Intent intent = new Intent(InAppPurchaseActivity.this, SuccessCouponPopUp.class);
			startActivityForResult(intent, 33333);
		}else if(requestCode==33333&&resultCode==55555){
			setResult(Activity.RESULT_OK);
			finish();
			Intent intent = new Intent(InAppPurchaseActivity.this, SplashActivity.class);
			intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK|intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);

		}

		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);

			// 여기서부터 아이템추가
			// 만약 서버로부터 영수증 체크후에 아이템 추가하게되면, 서버로 purchaseData , dataSignature
			// 2개를 보내면됨

		}else if(requestCode==55555&&resultCode==55555){
			finish();
		}else {
			// 구매취소 처리
			Log.d(TAG, "onActivityResult  handled by IABUtil");
		}

		// if(requestCode == 1001)
		//
		// if (resultCode == RESULT_OK) {
		//
		// if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
		//
		// super.onActivityResult(requestCode, resultCode, data);
		//
		// int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
		// String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
		// String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
		//
		// //서버에 등록된 정보와 일치하는지 확인해야함.
		//
		// Log.e("ihfb_pay", "purchaseData  : " +responseCode );
		// Log.e("ihfb_pay", "purchaseData  : " +purchaseData );
		// Log.e("ihfb_pay", "dataSignature : " +dataSignature );
		//
		// // 여기서부터 아이템추가
		// // 만약 서버로부터 영수증 체크후에 아이템 추가하게되면, 서버로 purchaseData , dataSignature 2개를
		// 보내면됨
		//
		//
		// } else {
		// // 구매취소 처리
		// alert("다시 시도해주세요.");
		// }
		// }else{
		// // 구매취소 처리
		// alert("결제가 실패하였습니다. 다시 시도해주세요.");
		// }
		// else{
		//
		// // 구매취소 처리
		//
		// }

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (mService != null) {
			unbindService(mServiceConn);
		}

	}


	void setWaitScreen(boolean set) {
		// findViewById(R.id.screen_main).setVisibility(set ? View.GONE :
		// View.VISIBLE);
		// findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE :
		// View.GONE);
	}



	String starttime;
	String startdate;
	com.ihateflyingbugs.hsmd.data.Date dates = new com.ihateflyingbugs.hsmd.data.Date();

	Map<String, String> articleParams ;

	public void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = dates.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent(TAG, articleParams);

	}

	@Override
	protected void onStop() {

		super.onStop();
		articleParams.put("Start", startdate);
		articleParams.put("End", dates.get_currentTime());
		Log.e("splash", startdate+"        "+dates.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 끝,FinishStudyFragment,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");

		/*
		 * com.android.systemui//홈버튼 길게 com.buzzpia.aqua.launcher
		 * com.pantech.launcher2.Launcher
		 * com.kakao.talk.activity.chat.ChatRoomActivity 초대시에는
		 * com.kakao.talk.activity.TaskRootActivity
		 */


		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;

		if (!componentInfo.getPackageName().equals("com.ihateflyingbugs.hsmd")) {
			for (int i = 0; i < taskInfo.size(); i++) {
				finish();
			}
		}
	}


}
