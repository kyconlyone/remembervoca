package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.model.StudyInfoData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class CompleteStudyTimePopup extends Activity implements OnClickListener {


	DBPool db;

	Handler handler;
	ImageView iv_complete_studytime_pic;

	LinearLayout ll_complete_studytime_1;
	LinearLayout ll_complete_studytime_2;
	SharedPreferences mPreference;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.popup_complete_studytime);

		handler = new Handler();


		db= DBPool.getInstance(getApplicationContext());


		readProfile();

		Date date = new Date();
		http://lnslab.com/mildang/upload_user_coupon.php
		ll_complete_studytime_1 = (LinearLayout)findViewById(R.id.ll_complete_studytime_1);
		ll_complete_studytime_2 = (LinearLayout)findViewById(R.id.ll_complete_studytime_2);
		ll_complete_studytime_1.setVisibility(View.VISIBLE);
		ll_complete_studytime_2.setVisibility(View.GONE);

		TextView tv_complete_studytime_title = (TextView)findViewById(R.id.tv_complete_studytime_title);
		TextView tv_complete_studytime1 = (TextView)findViewById(R.id.tv_complete_studytime1);
		iv_complete_studytime_pic = (ImageView)findViewById(R.id.iv_complete_studytime_pic);
		final TextView tv_complete_studytime_totalcount = (TextView)findViewById(R.id.tv_complete_studytime_totalcount);

		Button bt_complete_studytime1 = (Button)findViewById(R.id.bt_complete_studytime1);


		final TextView tv_complete_studytime_discounttitle = (TextView)findViewById(R.id.tv_complete_studytime_discounttitle);
		final TextView tv_complete_studytime_discount = (TextView)findViewById(R.id.tv_complete_studytime_discount);
		final TextView tv_complete_studytime2 = (TextView)findViewById(R.id.tv_complete_studytime2);

		Button bt_complete_studytime2 = (Button)findViewById(R.id.bt_complete_studytime2);


		new RetrofitService().getStudyInfoService().retroInsertDailyAchievement()
				.enqueue(new Callback<StudyInfoData>() {
					@Override
					public void onResponse(final Response<StudyInfoData> response, Retrofit retrofit) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
									if(response.body().getCount()!=0){
										tv_complete_studytime_totalcount.setText("전체 1232명의 학생 중 3일 안에 \n목표를 달성하는 학생은 "+response.body().getCount()+"명입니다");
									}else{
										tv_complete_studytime_totalcount.setText("전체 1232명의 학생 중 3일 안에 \n목표를 달성하는 학생은 2332명 입니다");
									}

							}
						});
					}

					@Override
					public void onFailure(Throwable t) {

					}
				});


		tv_complete_studytime_title.setText(getMySharedPreferences(MainValue.GpreName)+"님의\n첫 학습목표 달성을 축하드립니다.");
		tv_complete_studytime1.setText("처음에 약속했던 학습목표를 달성해 주셔서\n감사합니다. 저희는 불타는 의지를 가진\n"+getMySharedPreferences(MainValue.GpreName)+"님 같은 학생 덕분에 가장 큰 보람을\n느끼고 있습니다.");




		bt_complete_studytime1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ll_complete_studytime_1.setVisibility(View.GONE);
				ll_complete_studytime_2.setVisibility(View.VISIBLE);
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						tv_complete_studytime_discount.setText("목표 달성 쿠폰");
						tv_complete_studytime_discount.setText("20% 추가 할인");

						tv_complete_studytime2.setText("감사의 의미로 "+getMySharedPreferences(MainValue.GpreName)+"님께 밀당영단어 "+"20%"+
								"\n할인 혜택을 드립니다. 작심삼일로 끝나지\n않고 앞으로도 열심히 학습하셔서 원하는\n대학에 합격하시길 바랄게요 ^^");

					}
				});


			}
		});

		bt_complete_studytime2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(Activity.RESULT_OK);
				startActivity(new Intent(CompleteStudyTimePopup.this, ShowCard.class));
				finish();

			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub



	}

	Bitmap bitmap;

	private String thumbnailURL;

	public void readProfile() {

		thumbnailURL = getMySharedPreferences("image");
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
				// conn.connect();// url연결
				// bis = new BufferedInputStream(
				// conn.getInputStream()); // 접속한 url로부터 데이터값을 얻어온다
				// bm = BitmapFactory.decodeStream(bis);// 얻어온 데이터 Bitmap 에저장
				// bm = roundBitmap(bm);
				// bis.close();// 사용을 다한 BufferedInputStream 종료

				handler.post(new Runnable() {
					@Override
					public void run() {
						try {
							iv_complete_studytime_pic.setImageBitmap(bitmap);
						} catch (NullPointerException e) {
							// TODO: handle exception
							iv_complete_studytime_pic
									.setImageResource(R.drawable.my_btn_photo_default);
							Log.e("profile", e.toString());
						}
					}
				});
			}
		});
		thread.start();
		// load image
	}

	private Bitmap round(Bitmap source) {
		// TODO Auto-generated method stub
		Bitmap output = Bitmap.createBitmap(source.getWidth(),
				source.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawCircle(source.getWidth() / 2, source.getHeight() / 2,
				source.getWidth() / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(source, rect, rect, paint);
		return output;
	}

	public String getMySharedPreferences(String _key) {
		if (mPreference == null) {
			mPreference = getSharedPreferences(Config.PREFS_NAME,
					MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE
							| MODE_MULTI_PROCESS);
		}
		return mPreference.getString(_key, "");
	}


}
