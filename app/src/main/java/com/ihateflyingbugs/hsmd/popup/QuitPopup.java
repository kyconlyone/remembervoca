package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ihateflyingbugs.hsmd.data.AsyncResultType;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.indicator.SampleCirclesSnap;
import com.ihateflyingbugs.hsmd.model.AuthorizationData;
import com.ihateflyingbugs.hsmd.model.CouponData;
import com.ihateflyingbugs.hsmd.model.PaymentData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class QuitPopup extends Activity implements OnClickListener, OnCheckedChangeListener {

	String TAG = "QuitPopup";
	DBPool db;

	LinearLayout ll_quit_1;
	LinearLayout ll_quit_2;
	LinearLayout ll_quit_3;
	LinearLayout ll_quit_4;
	RadioGroup rg_quit_reason;
	Button bt_quit_ok2;
	Button bt_quit_postpone;

	EditText et_quit_reason;
	TextView tv_quit_text;
	TextView tv_quit_confirm;

	int selectedReason = 0;

	String reason ="";
	final String reason1 = "가격 부담";
	final String reason2 = "학습 기능 불만족";
	final String reason3 = "교육 효과에 대한 신뢰 부족";
	final String reason4 = "유료앱 사용에 대한 거부감";
	final String reason5 = "부모님의 반대";
	final String reason6 = "기타 사항";

	final String[] type = { "저희 밀당영단어는 기억력 측정, 단어 업데이트, 학습 진도 관리등 "
			+ "종이 단어장을 10권 사는것보다도 더 큰 효과를 드릴"
			+ "것을 약속 드립니다.가격이 부담 되시더라도 무료사용"
			+ "기간을 드릴 테니 한 번사용해 보시고 결정해주시면안될까요?ㅠ", "저희가  제공하는  기능들이마음에  들지  않으셨나요? 원래  학습하시던  "
			+ "익숙한  방식이 아니라서  낯선  것에  대한 거부감이  생기실  수도  있어요. "
			+ "저희가  무료  사용  기간을  드릴테니  속는  셈  치고  밀당 영단어의 기능과  암기법을  좀  더  "
			+ "사용해보시면  안될까요? 절대 실망시키지  않을게요","저희가  약속한  효과들이믿어지지  않으셨나요? "
			+ "밀당영단어는  절대  과장된 효과들로  거짓을  말하지않을게요. "
			+ "무료  사용기간을  드릴  테니 저희를  믿고  조금만  더  사용해보시면  안될까요? "
			+ "절대 실망시키지  않을게요!", "저희  밀당영단어는  기억력  측정, 단어 업데이트, 학습  진도  관리  등 "
			+ " 종이 단어장으로  할  수  없는  일들을  가능하게 만들어  드리고  싶어서  앱으로  단어장을 만들게  되었답니다."
			+ "무료  사용  기간을  드릴  테니  거부감이 들더라도  조금은  더  사용해  보셨으면 해요 ㅠ "
			+ "가격이  부담  되시더라도  무료 사용기간을  드릴  테니  한  번  사용해보시고  결정해주시면  안될까요?", "부모님께서  밀당영단어  사용을 반대  하셨나요? "
			+ "저희가  많은  걸  해드릴  수는없지만  조금이라도  더  사용하실 수  있게  "
			+ "무료  사용기간을  드릴테니 더  사용해  보시고  정말  괜찮다고 생각이  드시면  다시  한번 "
			+ "부모님께  여쭤보는  건  어떠세요?", "저희  밀당영단어는  시간이  지나면 점점  가치를  잃는  다른  단어장과는 다르게 "
			+ "끊임  없는  분석과  업데이트로 사용하면  할수록  가치가  높아지는 암기  시스템입니다."
			+ "적지만  무료 사용기간을  드릴  테니 조금  불만족스러우셨던  부분들이 있더라도  조금  더  사용해  보시면 안될까요?"
			+ "절대  실망시키지않을게요 ㅠ"};

	final String type1 = "저희 밀당영단어는 기억력 측정, 단어 업데이트, 학습 진도 관리등 "
			+ "종이 단어장을 10권 사는것보다도 더 큰 효과를 드릴"
			+ "것을 약속 드립니다.가격이 부담 되시더라도 무료사용"
			+ "기간을 드릴 테니 한 번사용해 보시고 결정해주시면안될까요?ㅠ";

	final String type2="저희가  제공하는  기능들이마음에  들지  않으셨나요? 원래  학습하시던  "
			+ "익숙한  방식이 아니라서  낯선  것에  대한 거부감이  생기실  수도  있어요. "
			+ "저희가  무료  사용  기간을  드릴테니  속는  셈  치고  밀당 영단어의 기능과  암기법을  좀  더  "
			+ "사용해보시면  안될까요? 절대 실망시키지  않을게요";

	final String type3="저희가  약속한  효과들이믿어지지  않으셨나요? "
			+ "밀당영단어는  절대  과장된 효과들로  거짓을  말하지않을게요. "
			+ "무료  사용기간을  드릴  테니 저희를  믿고  조금만  더  사용해보시면  안될까요? "
			+ "절대 실망시키지  않을게요!";

	final String type4="저희  밀당영단어는  기억력  측정, 단어 업데이트, 학습  진도  관리  등 "
			+ " 종이 단어장으로  할  수  없는  일들을  가능하게 만들어  드리고  싶어서  앱으로  단어장을 만들게  되었답니다."
			+ "무료  사용  기간을  드릴  테니  거부감이 들더라도  조금은  더  사용해  보셨으면 해요 ㅠ "
			+ "가격이  부담  되시더라도  무료 사용기간을  드릴  테니  한  번  사용해보시고  결정해주시면  안될까요?";

	final String type5="부모님께서  밀당영단어  사용을 반대  하셨나요? "
			+ "저희가  많은  걸  해드릴  수는없지만  조금이라도  더  사용하실 수  있게  "
			+ "무료  사용기간을  드릴테니 더  사용해  보시고  정말  괜찮다고 생각이  드시면  다시  한번 "
			+ "부모님께  여쭤보는  건  어떠세요?";

	final String type6="저희  밀당영단어는  시간이  지나면 점점  가치를  잃는  다른  단어장과는 다르게 "
			+ "끊임  없는  분석과  업데이트로 사용하면  할수록  가치가  높아지는 암기  시스템입니다."
			+ "적지만  무료 사용기간을  드릴  테니 조금  불만족스러우셨던  부분들이 있더라도  조금  더  사용해  보시면 안될까요?"
			+ "절대  실망시키지않을게요 ㅠ";

	RadioButton rb_quit_etc;

	Handler handler;

	boolean isFreeState = false;
	boolean isAfter= false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.popup_quit);

		handler = new Handler();

		db= DBPool.getInstance(getApplicationContext());

		ll_quit_1 = (LinearLayout)findViewById(R.id.ll_quit_1);
		ll_quit_2 = (LinearLayout)findViewById(R.id.ll_quit_2);
		ll_quit_3 = (LinearLayout)findViewById(R.id.ll_quit_3);
		ll_quit_4 = (LinearLayout)findViewById(R.id.ll_quit_4);

		ll_quit_1.setVisibility(View.VISIBLE);
		ll_quit_2.setVisibility(View.GONE);
		ll_quit_3.setVisibility(View.GONE);
		ll_quit_4.setVisibility(View.GONE);

		Button bt_quit_ok  = (Button)findViewById(R.id.bt_quit_ok);
		Button bt_quit_cancel  = (Button)findViewById(R.id.bt_quit_cancel);
		Button bt_quit_ok3  = (Button)findViewById(R.id.bt_quit_ok3);
		bt_quit_ok2 = (Button)findViewById(R.id.bt_quit_ok2);
		bt_quit_postpone = (Button)findViewById(R.id.bt_quit_postpone);

		bt_quit_ok.setOnClickListener(this);
		bt_quit_cancel.setOnClickListener(this);
		bt_quit_ok2.setOnClickListener(this);
		bt_quit_ok3.setOnClickListener(this);

		et_quit_reason = (EditText)findViewById(R.id.et_quit_reason);
		tv_quit_text = (TextView)findViewById(R.id.tv_quit_text);
		tv_quit_confirm =  (TextView)findViewById(R.id.tv_quit_confirm);

		SpannableString contentsp = new SpannableString(tv_quit_confirm.getText());
		contentsp.setSpan(new UnderlineSpan(), 0, tv_quit_confirm.getText().length(), 0);

		tv_quit_confirm.setText(contentsp);
		tv_quit_confirm.setOnClickListener(this);
		bt_quit_postpone.setOnClickListener(this);

		rg_quit_reason = (RadioGroup)findViewById(R.id.rg_quit_reason);
		rb_quit_etc = (RadioButton)findViewById(R.id.rb_quit_reason_etc);

		rg_quit_reason.setOnCheckedChangeListener(this);

		new RetrofitService().getPaymentService().retroCheckGotFreeDay(db.getStudentId())
				.enqueue(new Callback<CouponData>() {
					@Override
					public void onResponse(Response<CouponData> response, Retrofit retrofit) {
						if(response.body().getResult()==3){
							isAfter = true;
						}else{
							isAfter = false;
						}
					}

					@Override
					public void onFailure(Throwable t) {
						isAfter= true;
						Log.e(TAG, "retroCheckGotFreeDay onFailure : "+t.toString());
					}
				});



	}
	int student_use_state= 3;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
			case R.id.bt_quit_ok:
				ll_quit_1.setVisibility(View.GONE);
				ll_quit_2.setVisibility(View.VISIBLE);
				bt_quit_ok2.setVisibility(View.INVISIBLE);
				break;
			case R.id.bt_quit_cancel:
				finish();
				break;

			case R.id.bt_quit_ok2:
				ll_quit_2.setVisibility(View.GONE);
				if(!isAfter){
					ll_quit_4.setVisibility(View.VISIBLE);
					tv_quit_text.setText(type[selectedReason-1]);
					bt_quit_postpone.setVisibility(View.VISIBLE);
				}else{
					sendReason(this.reason);
				}


				break;

			case R.id.bt_quit_ok3:
				try {
					reason = et_quit_reason.getText().toString();
				} catch (Exception e) {
					// TODO: handle exception
					reason = "입력하지 않음";
				}
				reason =  et_quit_reason.getText().toString();
				rb_quit_etc.setText(reason);
				ll_quit_3.setVisibility(View.GONE);
				ll_quit_2.setVisibility(View.VISIBLE);

				break;

			case R.id.bt_quit_postpone:
				Log.e("postpone", "1");

				new RetrofitService().getAuthorizationService().retroCheckavailability(db.getStudentId(), "14").enqueue(new Callback<AuthorizationData>() {
					@Override
					public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {

						int availability = -2;
						int student_use_state = 0;

						availability = response.body().getAvailability();
						student_use_state = response.body().getStudent_use_state();

						if(student_use_state==2){

							new RetrofitService().getPaymentService().retroInsertFinishDateAtLeft(db.getStudentId())
									.enqueue(new Callback<PaymentData>() {
										@Override
										public void onResponse(final Response<PaymentData> response, Retrofit retrofit) {
											Log.e("postpone", "3");
											handler.post(new Runnable() {

												@Override
												public void run() {
													// TODO Auto-generated method stub
													try {
														Log.e("postpone", "13");
														if(response.body().getResult()== AsyncResultType.RESULT_SUCCESS){
															Log.e("postpone", "10");
															Toast.makeText(getApplicationContext(), "무료 사용 기간이 연장되었습니다.", Toast.LENGTH_SHORT).show();
															finish();
														}else if(response.body().getResult()==AsyncResultType.RESULT_AREALDY_EXIST){
															Log.e("postpone", "11");
															Toast.makeText(getApplicationContext(), "무료사용을 하신지 아직 한달이 지나지 않았습니다.\n다음달부터 무료 사용받기가 가능합니다.:D", Toast.LENGTH_SHORT).show();
														}else{
															Log.e("postpone", "14");
														}
													} catch (Exception e) {
														// TODO Auto-generated catch block
														Log.e("postpone", "12");
														Toast.makeText(getApplicationContext(), "죄송합니다. 무료사용 받기 과정에서 문제가 발생했습니다."
																+ "계속해서 문제 발생시 cs@lnslab.com 으로 메일 보내시길 부탁드립니다. 감사합니다.", Toast.LENGTH_SHORT).show();
														e.printStackTrace();
													}
												}
											});
										}

										@Override
										public void onFailure(final Throwable t) {
											Log.e("postpone", "4");
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
						}else{
							Log.e("postpone", "5");
							sendReason(reason);
						}
					}

					@Override
					public void onFailure(Throwable t) {
						Log.e("postpone", "6");
						finish();
					}
				});


				break;
			case R.id.tv_quit_confirm:
				sendReason(this.reason);
				break;
			default:
				break;
		}

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub

		switch (group.getCheckedRadioButtonId()) {
			case R.id.rb_quit_reason1:
				bt_quit_ok2.setVisibility(View.VISIBLE);
				selectedReason = 1;
				reason = reason1;
				break;
			case R.id.rb_quit_reason2:
				bt_quit_ok2.setVisibility(View.VISIBLE);
				selectedReason = 2;
				reason = reason2;

				break;
			case R.id.rb_quit_reason3:
				bt_quit_ok2.setVisibility(View.VISIBLE);
				selectedReason = 3;
				reason = reason3;
				break;
			case R.id.rb_quit_reason4:
				bt_quit_ok2.setVisibility(View.VISIBLE);
				selectedReason = 4;
				reason = reason4;

				break;
			case R.id.rb_quit_reason5:
				bt_quit_ok2.setVisibility(View.VISIBLE);
				selectedReason = 5;
				reason = reason5;
				break;
			case R.id.rb_quit_reason_etc:
				bt_quit_ok2.setVisibility(View.VISIBLE);
				ll_quit_3.setVisibility(View.VISIBLE);
				ll_quit_2.setVisibility(View.GONE);
				selectedReason = 6;
				reason = reason6;

				break;
			default:
				break;
		}

	}


	public void sendReason(final String reason){
		UserManagement.requestUnlink(new UnLinkResponseCallback() {
			@Override
			public void onSessionClosed(ErrorResult errorResult) {
				new RetrofitService().getAuthorizationService().retroInsertLeaveInfo(db.getStudentId(),
						reason)
						.enqueue(new retrofit.Callback<AuthorizationData>() {
							@Override
							public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {
								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub

										db.initailizeInfo();
										Toast.makeText(QuitPopup.this, "탈퇴에 성공하였습니다.\n그동안 이용해 주셔서 감사합니다.", Toast.LENGTH_SHORT).show();
										setResult(Activity.RESULT_OK);
										finish();
										Intent intent = new Intent(QuitPopup.this, SampleCirclesSnap.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
										intent.putExtra("EXIT", true);
										startActivity(intent);
									}
								});
							}

							@Override
							public void onFailure(Throwable t) {
								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										Toast.makeText(QuitPopup.this, "탈퇴에 실패하였습니다. 다시 시도해 주세요..", Toast.LENGTH_SHORT).show();
										setResult(Activity.RESULT_CANCELED);
									}
								});
							}
						});
			}

			@Override
			public void onNotSignedUp() {
				new RetrofitService().getAuthorizationService().retroInsertLeaveInfo(db.getStudentId(),
						reason)
						.enqueue(new retrofit.Callback<AuthorizationData>() {
							@Override
							public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {
								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										db.initailizeInfo();
										Toast.makeText(QuitPopup.this, "탈퇴에 성공하였습니다.\n그동안 이용해 주셔서 감사합니다.", Toast.LENGTH_SHORT).show();
										setResult(Activity.RESULT_OK);
										finish();
										Intent intent = new Intent(QuitPopup.this, SampleCirclesSnap.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
										intent.putExtra("EXIT", true);
										startActivity(intent);

									}
								});
							}

							@Override
							public void onFailure(Throwable t) {
								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										Toast.makeText(QuitPopup.this, "전송 실패.", Toast.LENGTH_SHORT).show();
										setResult(Activity.RESULT_CANCELED);
									}
								});
							}
						});
			}

			@Override
			public void onSuccess(Long result) {
				new RetrofitService().getAuthorizationService().retroInsertLeaveInfo(db.getStudentId(),
						reason)
						.enqueue(new retrofit.Callback<AuthorizationData>() {
							@Override
							public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {
								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										db.initailizeInfo();
										Toast.makeText(QuitPopup.this, "탈퇴에 성공하였습니다.\n그동안 이용해 주셔서 감사합니다.", Toast.LENGTH_SHORT).show();
										setResult(Activity.RESULT_OK);
										finish();
										Intent intent = new Intent(QuitPopup.this, SampleCirclesSnap.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
										intent.putExtra("EXIT", true);
										startActivity(intent);

									}
								});
							}

							@Override
							public void onFailure(Throwable t) {
								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										Toast.makeText(QuitPopup.this, "전송 실패.", Toast.LENGTH_SHORT).show();
										setResult(Activity.RESULT_CANCELED);
									}
								});
							}
						});
			}
		});

	}




}
