package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.model.UseAppInfoData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class KakaostoryPopup extends Activity implements OnClickListener {

	String TAG = "KakaostoryPopup";

	ProgressDialog mProgressDialog;

	LinearLayout ll_kakaostory_1;
	LinearLayout ll_kakaostory_2;
	LinearLayout ll_kakaostory_3;

	Spinner sp_kakaostory;

	Button bt_kakaostory_1;
	Button bt_kakaostory_2;
	Button bt_kakaostory_3;

	ImageView img_kakaostory_2;

	int count;

	TextView tv_kakaostory_2;

	String[] items = { "1등급", "2등급", "3등급", "4,5등급", "6,7등급", "8,9등급" };

	TextView tv_kakao_story_3;

	DBPool db;

	int selectedItemIdx = 0;

	Handler handler;

	SharedPreferences mPreferences;

	String[][] word_value = { { "leak", "8.76", "27.27" }, { "transmit", "13.99", "18.18" }, { "precise", "40.15", "9.09" },
			{ "executive", "23.59", "18.18" }, { "specific", "61.16", "20.83" }, { "constant", "55.31", "9.09" } };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_kakaostory_push);

		Config.isKakaoStory = false;

		mPreferences = getSharedPreferences(Config.PREFS_NAME, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE | MODE_MULTI_PROCESS);

		ll_kakaostory_1 = (LinearLayout) findViewById(R.id.ll_kakaostory_1);
		ll_kakaostory_2 = (LinearLayout) findViewById(R.id.ll_kakaostory_2);
		ll_kakaostory_3 = (LinearLayout) findViewById(R.id.ll_kakaostory_3);

		sp_kakaostory = (Spinner) findViewById(R.id.sp_kakaostory);

		tv_kakaostory_2 = (TextView) findViewById(R.id.tv_kakaostory_2);
		tv_kakao_story_3 = (TextView) findViewById(R.id.tv_kakao_story_3);
		img_kakaostory_2 = (ImageView) findViewById(R.id.img_kakaostory_2);

		bt_kakaostory_1 = (Button) findViewById(R.id.bt_kakaostory_1);
		bt_kakaostory_2 = (Button) findViewById(R.id.bt_kakaostory_2);
		bt_kakaostory_3 = (Button) findViewById(R.id.bt_kakaostory_3);

		bt_kakaostory_1.setOnClickListener(this);
		bt_kakaostory_2.setOnClickListener(this);
		bt_kakaostory_3.setOnClickListener(this);

		db = DBPool.getInstance(getApplicationContext());

		db.setPushTable(2, 0);

		handler = new Handler();

		new RetrofitService().getUseAppInfoService().retroGetUseCount()
				.enqueue(new Callback<UseAppInfoData>() {
					@Override
					public void onResponse(Response<UseAppInfoData> response, Retrofit retrofit) {
						count = response.body().getUser_count();
					}

					@Override
					public void onFailure(Throwable t) {
						count = 6452;
						Log.e(TAG, "onFailure : "+t.toString());
					}
				});

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_grade_spinner, items);
		adapter.setDropDownViewResource(R.layout.item_grade_spinner);
		sp_kakaostory.setAdapter(adapter);

		sp_kakaostory.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedItemIdx = position;

				double percent = 100 - Double.valueOf(word_value[selectedItemIdx][2]);
				tv_kakaostory_2.setText("밀당 영단어의 학생 " + count + "명의 학습 데이터를 분석해보니 " + word_value[selectedItemIdx][0] + "를 외운 적 있던 학생 중 " + percent
						+ "%는 하루만에 망각하였습니다. ");
				switch (selectedItemIdx) {
					case 0:
						img_kakaostory_2.setImageResource(R.drawable.img_kkostry_wordchart_1);
						break;
					case 1:
						img_kakaostory_2.setImageResource(R.drawable.img_kkostry_wordchart_2);
						break;
					case 2:
						img_kakaostory_2.setImageResource(R.drawable.img_kkostry_wordchart_3);
						break;
					case 3:
						img_kakaostory_2.setImageResource(R.drawable.img_kkostry_wordchart_45);
						break;
					case 4:
						img_kakaostory_2.setImageResource(R.drawable.img_kkostry_wordchart_67);
						break;
					case 5:
						img_kakaostory_2.setImageResource(R.drawable.img_kkostry_wordchart_89);
						break;

				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_kakaostory_1:
				ll_kakaostory_1.setVisibility(View.GONE);
				ll_kakaostory_2.setVisibility(View.VISIBLE);
				break;
			case R.id.bt_kakaostory_2:
				ll_kakaostory_2.setVisibility(View.GONE);
				handler.post(new Runnable() {

					@Override
					public void run() {
						mProgressDialog = ProgressDialog.show(KakaostoryPopup.this, "공유중", "잠시만 기다려주세요.");
					}
				});
//
//				new Async_send_kakaostory(db.getStudentId(), "" + sp_kakaostory.getSelectedItemPosition(), Session.getCurrentSession().getAccessToken(),
//						new VocaCallback() {
//
//							@Override
//							public void Resonponse(JSONObject response) {
//								// TODO Auto-generated method stub
//								handler.post(new Runnable() {
//
//									@Override
//									public void run() {
//										mProgressDialog.dismiss();
//										ll_kakaostory_3.setVisibility(View.VISIBLE);
//										tv_kakao_story_3.setText(mPreferences.getString(MainValue.GpreName, "이름없음")
//												+ "님의 카카오 스토리 담벼락에서 BEST 10단어를 확인해 주세요.");
//									}
//								});
//							}
//
//							@Override
//							public void Exception() {
//								// TODO Auto-generated method stub
//								handler.post(new Runnable() {
//
//									@Override
//									public void run() {
//										Toast.makeText(getApplicationContext(), "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
//										mProgressDialog.dismiss();
//										ll_kakaostory_2.setVisibility(View.VISIBLE);
//									}
//								});
//							}
//						}).execute();
				break;
			case R.id.bt_kakaostory_3:
				finish();
				break;
			default:
				break;
		}
	}
}
