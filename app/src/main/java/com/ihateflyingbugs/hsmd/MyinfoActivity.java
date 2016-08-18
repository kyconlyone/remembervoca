package com.ihateflyingbugs.hsmd;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.model.AuthorizationData;
import com.ihateflyingbugs.hsmd.popup.QuitPopup;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.ihateflyingbugs.hsmd.tutorial.InstallActivity;
import com.ihateflyingbugs.hsmd.tutorial.MainActivity;
import com.ihateflyingbugs.hsmd.tutorial.SearchSchoolActivity;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MyinfoActivity extends Activity implements OnClickListener {
	String TAG = "MyinfoActivity";

	private Context mContext;
	SharedPreferences mPreference;
	SharedPreferences.Editor mShared_Editor;
	DBPool db;


	ImageView iv_myinfo_picture;

	EditText et_my_name;
	EditText et_my_birth;
	EditText et_my_school;
	EditText et_my_email;
	TextView tv_my_grade;

	Button bt_change;
	Button bt_edit_logout;
	Button bt_secession;


	protected Bitmap bitmap;
	protected Handler handler;

	public static String profileUrl;

	public static Bitmap bm;



	//
	ActionBar mActionBar;
	View mCustomActionBarView;

	LinearLayout mBack_Layout;
	TextView mTitle_TextView;
	//


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myinfo);

		setActionBar();


		mContext = getApplicationContext();
		mPreference = mContext.getSharedPreferences(MainActivitys.preName, mContext.MODE_WORLD_READABLE | mContext.MODE_WORLD_WRITEABLE);
		mShared_Editor = mPreference.edit();
		db = DBPool.getInstance(mContext);
		handler = new Handler();


		iv_myinfo_picture = (ImageView) findViewById(R.id.iv_myinfo_picture);
		et_my_name = (EditText) findViewById(R.id.et_my_name);
		et_my_birth = (EditText) findViewById(R.id.et_my_birth);
		et_my_school = (EditText) findViewById(R.id.et_my_school);
		et_my_email = (EditText) findViewById(R.id.et_my_email);
		tv_my_grade = (TextView) findViewById(R.id.tv_my_grade);

		bt_change = (Button) findViewById(R.id.bt_change);
		bt_edit_logout = (Button) findViewById(R.id.bt_edit_logout);
		bt_secession = (Button) findViewById(R.id.bt_secession);

		InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromInputMethod(et_my_name.getWindowToken(), 0);



		getPicture();
		et_my_name.setText(mPreference.getString(MainValue.GpreName, "이름없음"));
		et_my_birth.setText(mPreference.getString(MainValue.GpreBirth, "000000"));
		et_my_school.setText(mPreference.getString(MainValue.GpreSchoolName, "없음"));
		et_my_email.setText(mPreference.getString(MainValue.GpreEmail, "a@a.com"));
		tv_my_grade.setText(Integer.toString(mPreference.getInt(MainValue.GpreSatGrade, 5)));

		tv_my_grade.setOnClickListener(this);

		bt_change.setOnClickListener(this);
		bt_edit_logout.setOnClickListener(this);
		bt_secession.setOnClickListener(this);
		et_my_school.setFocusable(false);
		et_my_school.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_change:
				String name1;
				String birth1;
				String school1;
				String email1;
				String grade1;
				String level11;

				try {
					name1 = et_my_name.getText().toString();
					birth1 = et_my_birth.getText().toString();
					email1 = et_my_email.getText().toString();
					grade1 = tv_my_grade.getText().toString();
					level11 = String.valueOf(db.getWordLevel());

					if (name1.length() < 2 || name1.length() > 15) {
						Toast.makeText(MyinfoActivity.this, "이름은 2~15자 까지만 가능합니다", Toast.LENGTH_LONG).show();
						return;

					}

					if (school_id==0) {
						Toast.makeText(MyinfoActivity.this, "학교를 입력해 주세요.", Toast.LENGTH_LONG).show();
						return;

					}
					Pattern pattern = Pattern.compile(InstallActivity.EMAIL_PATTERN);

					if (!pattern.matcher(email1).matches()) {
						Toast.makeText(MyinfoActivity.this, "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_LONG).show();
						return;
					} else if (email1.length() > 40 || email1.length() < 8) {
						Toast.makeText(MyinfoActivity.this, "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_LONG).show();
						return;
					}

					if (birth1.length() != 6) {
						Toast.makeText(MyinfoActivity.this, "생년월일은 년월일(ex. 880502)", Toast.LENGTH_LONG).show();
						return;
					}

				} catch (NullPointerException e) {
					Toast.makeText(getApplicationContext(), "빈값을 입력해주세요 !", Toast.LENGTH_SHORT);
					return;
				}

				final String name =name1;
				final String birth = birth1;
				final String school =""+school_id;
				final String email = email1;
				final String grade = grade1;
				final String level = level11;
				final String profile_url=  mPreference.getString(MainValue.GpreProfileImage, "");

				final int level1 = db.getWordLevel();

				if (Integer.valueOf((String) birth.subSequence(0, 2)) > 50) {
					int years = Integer.valueOf((String) birth.subSequence(0, 2)) - 1;
				} else {
					int years = Integer.valueOf((String) birth.subSequence(0, 2)) - 1;
				}

				new RetrofitService().getAuthorizationService().retroUpdateUserInfo(db.getStudentId(),
																					name,
																					birth,
																					school,
																					email,
																					profile_url,
																					grade,
																					level)
						.enqueue(new Callback<AuthorizationData>() {
							@Override
							public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {
								handler.post(new Runnable() {

									@Override
									public void run() {
										Toast.makeText(getApplicationContext(), "정보를 수정하였습니다.", Toast.LENGTH_SHORT).show();

										mPreference.edit().putString(MainValue.GpreName, name).commit();
										mPreference.edit().putString(MainValue.GpreBirth, birth).commit();
										mPreference.edit().putString(MainValue.GpreSchoolId, ""+school_id).commit();
										mPreference.edit().putString(MainValue.GpreSchoolName, ""+school_name).commit();
										mPreference.edit().putString(MainValue.GpreEmail, email).commit();
										mPreference.edit().putInt(MainValue.GpreSatGrade, Integer.valueOf(grade)).commit();
									}
								});
							}

							@Override
							public void onFailure(final Throwable t) {
								handler.post(new Runnable() {

									@Override
									public void run() {
										Log.e(TAG, "onFailure : " + t.toString());
										Toast.makeText(getApplicationContext(), "정보 수정에 실패하였습니다. 잠시후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
									}
								});
							}
						});

				break;
			case R.id.bt_edit_logout:
				new AlertDialog.Builder(MyinfoActivity.this)
						.setMessage("로그아웃 하시겠습니까?")
						.setPositiveButton("확인", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								db.insertLoginId("000000");
								onClickLogout();
								FlurryAgent.logEvent("SideActivity_EditUserInfoFragment:ClickLogout", true);
							}
						}).setNegativeButton("취소", null).show();

				break;
			case R.id.bt_secession:
				Intent intent = new Intent(MyinfoActivity.this, QuitPopup.class);
				startActivityForResult(intent, Activity.RESULT_OK);
				break;
			case R.id.tv_my_grade:
				show(Integer.parseInt(tv_my_grade.getText().toString()));
				break;

			case R.id.et_my_school:
				startActivityForResult(new Intent(MyinfoActivity.this, SearchSchoolActivity.class), 6666);
				break;

			default:
				break;
		}
	}

	private void onClickLogout() {
		try {

			UserManagement.requestLogout(new LogoutResponseCallback() {
				@Override
				public void onCompleteLogout() {
					redirectLoginActivity();
				}
			});
		} catch (IllegalStateException e) {
			// TODO: handle exception
			redirectLoginActivity();
		}

	}

	protected void redirectLoginActivity() {
		// TODO Auto-generated method stub

		finish();
		MainActivity.finish_logout();
	}

	private void deleteMySharedPreferences(String _key) {
		if (mPreference == null) {
			mPreference = mContext.getSharedPreferences(MainActivitys.preName,
					mContext.MODE_WORLD_READABLE
							| mContext.MODE_WORLD_WRITEABLE);
		}
		SharedPreferences.Editor editor = mPreference.edit();
		editor.remove(_key);
		editor.commit();
	}


	/*
	 * 카톡로그인 달리면 주석 풀것!
	 */
	public void readProfile() {


		//
		//		KakaoTalkService
		//				.requestProfile(new MyTalkHttpResponseHandler<KakaoTalkProfile>() {
		//
		//					@Override
		//					protected void onHttpSuccess(KakaoTalkProfile talkProfile) {
		//						// TODO Auto-generated method stub
		//						String nickName = talkProfile.getNickName();
		//						String profileImageURL = talkProfile
		//								.getProfileImageURL();
		//						String thumbnailURL = talkProfile.getThumbnailURL();
		//						String countryISO = talkProfile.getCountryISO();
		//
		//						MyinfoActivity.profileUrl = profileImageURL;
		//						MyinfoActivity.bm = getImageforKakao(profileImageURL);
		//
		//						if (profileImageURL != "") {
		//							iv_myinfo_picture.setImageBitmap(roundBitmap(bm));
		//						}
		//
		//					}
		//
		//				});
	}


	public void getPicture() {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				String path = Config.DB_FILE_DIR;

				File directory = new File(path);

				File file = new File(directory, Config.PROFILE_NAME); // or any
				// other
				// format
				// supported

				FileInputStream streamIn;

				try {
					streamIn = new FileInputStream(file);
					bitmap = BitmapFactory.decodeStream(streamIn); // This gets
					// the image
					streamIn.close();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (bitmap != null) {
					bitmap = round(bitmap);
				}

				// url = new URL(thumbnailURL);
				// URLConnection conn = url.openConnection();
				// conn.connect();// url?�곌�?
				// bis = new BufferedInputStream(
				// conn.getInputStream()); // ?��?��?�� url濡��??�� ?��?��?�곌��� ?��?��?��?��
				// bm = BitmapFactory.decodeStream(bis);// ?��?��?�� ?��?��?�� Bitmap
				// ?��???��
				// bm = roundBitmap(bm);
				// bis.close();// ?��?��?�� ?��?�� BufferedInputStream 醫�猷�

				handler.post(new Runnable() {
					@Override
					public void run() {
						try {
							if (!bitmap.equals(null)) {
								iv_myinfo_picture.setImageBitmap(bitmap);
							}
						} catch (NullPointerException e) {
							// TODO: handle exception
							iv_myinfo_picture
									.setImageResource(R.drawable.profile);
						}
					}
				});
			}
		});
		thread.start();
	}

	private Bitmap round(Bitmap source) {
		// TODO Auto-generated method stub
		Bitmap output = Bitmap.createBitmap(source.getWidth(),
				source.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, source.getWidth(),
				source.getHeight());

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawCircle(source.getWidth() / 2,
				source.getHeight() / 2, source.getWidth() / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(source, rect, rect, paint);
		return output;
	}

	public void show(int grade) {

		final Dialog d = new Dialog(MyinfoActivity.this);
		d.setTitle("등급을 선택해 주세요");
		d.setContentView(R.layout.numberpickerdialog);
		Button b1 = (Button) d.findViewById(R.id.button1);
		Button b2 = (Button) d.findViewById(R.id.button2);
		final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
		np.setMaxValue(9); // max value 100
		np.setMinValue(1); // min value 0
		np.setValue(grade);
		np.setWrapSelectorWheel(true);
		np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				// TODO Auto-generated method stub
				Log.i("value is", "" + newVal);
			}
		});

		b1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tv_my_grade.setText(String.valueOf(np.getValue())); // set
				// the
				// value
				// to
				// textview
				d.dismiss();
			}
		});

		b2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				d.dismiss(); // dismiss the dialog
			}
		});
		d.show();

	}



	void setActionBar() {
		mActionBar = getActionBar();
		mCustomActionBarView = getLayoutInflater().inflate(R.layout.setting_action_bar, null);
		mActionBar.setCustomView(mCustomActionBarView, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		mBack_Layout = (LinearLayout)mCustomActionBarView.findViewById(R.id.layout_actionbar_back);
		mBack_Layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MyinfoActivity.this.finish();
			}
		});

		mTitle_TextView = (TextView)mCustomActionBarView.findViewById(R.id.text_actionbar_title);
		mTitle_TextView.setText("내 정보");
	}

	String starttime;
	String startdate;
	Date date = new Date();
	Map<String, String> articleParams;

	private String school_name;

	private int school_id=0;

	public void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("MyinfoActivity", articleParams);
	}

	public void onStop()
	{
		super.onStop();
		FlurryAgent.endTimedEvent("MyinfoActivity");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			setResult(55555);
			finish();
		}else if(resultCode == 6666){
			et_my_school.setText(data.getExtras().getString("school_name"));
			school_name= data.getExtras().getString("school_name");
			school_id = data.getExtras().getInt("school_id");
		}
	}
}
