package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DBupdatePopUp extends Activity {

	final static int VALUE_WORDS = 1;
	final static int VALUE_MEANS = 2;
	final static int VALUE_WrongRate = 3;


	Button bt_close_doc_popup;
	TextView tv_doc_title;
	TextView tv_doc_contents;

	DBPool db;
	ProgressBar pb;
	TextView tv;
	Animation anim2;

	Handler handler;

	boolean isStudyroom;
	String name_studyroom;

	Animation anim;

	boolean isMeanUpdate = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_dbupdate);
		db = DBPool.getInstance(getApplicationContext());

		Intent intent =  getIntent();
		try {
			isMeanUpdate = getIntent().getExtras().getBoolean("isUpdate", false);
		} catch (Exception e) {
			// TODO: handle exception

		}


		try {

			isStudyroom = getIntent().getExtras().getBoolean("isStudyroom");
		} catch (NullPointerException e) {
			// TODO: handle exception
			isStudyroom = false;
		}

		try {

			name_studyroom = getIntent().getExtras().getString("Name");
		} catch (NullPointerException e) {
			// TODO: handle exception
			name_studyroom = "이름없음";
		}

		handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1){
					pb.startAnimation(anim);
				}
			}

		};





		tv  = (TextView)findViewById(R.id.tv_dbupdate);
		pb  = (ProgressBar)findViewById(R.id.pb_dbupdate);


		anim2 = new AnimationUtils().loadAnimation(getApplicationContext(), android.R.anim.fade_out);

		anim2.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				tv.setVisibility(View.VISIBLE);
				tv.setText("업데이트가 완료되었습니다.");
				tv.startAnimation(new AnimationUtils().loadAnimation(getApplicationContext(), android.R.anim.fade_in));
				tv.setVisibility(View.VISIBLE);

				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						finish();
						if(isStudyroom){
							Intent intent = new Intent(DBupdatePopUp.this, StudyRoomUsePopup.class);
							intent.putExtra("Name", name_studyroom);
							startActivity(intent);
						}
					}
				},1500);




			}
		});


		anim = new AnimationUtils().loadAnimation(getApplicationContext(), android.R.anim.fade_out);

		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

				tv.startAnimation(anim2);

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				pb.setVisibility(View.GONE);
				tv.setVisibility(View.GONE);
				tv.setText("업데이트가 완료되었습니다.");

			}
		});


		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				thread.start();
			}
		}, 1500);




	}

	Thread thread = new Thread(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!isMeanUpdate){
				List<String[]> cvsword = readCsv(getApplicationContext(), "addwords.csv", VALUE_WORDS);
			}

			List<String[]> cvsmean = readCsv(getApplicationContext(), "addmeans.csv", VALUE_MEANS);
			List<String[]> cvsWrongrate = readCsv(getApplicationContext(), "360.csv", VALUE_WrongRate);

			Message message = new Message().obtain();
			message.what = 1;
			handler.sendMessage(message);
		}
	});



	public final List<String[]> readCsv(Context context, String CVSname, int sort) {

		if(sort==VALUE_WrongRate){
			db.maketbl_Wrongrate();
		}

		String DB_FILE_PATH = Config.DB_FILE_DIR + CVSname;


		File file = new File(Config.DB_FILE_DIR);
		if (!file.exists() && !file.mkdirs())
			return null;

		file = new File(DB_FILE_PATH);
		if (!file.exists()) {
			// file copy
			try {
				InputStream is = context.getAssets().open(CVSname);
				OutputStream os = new FileOutputStream(DB_FILE_PATH);

				byte[] buffer = new byte[1024];
				int len;
				while ((len = is.read(buffer)) > 0)
					os.write(buffer, 0, len);

				os.flush();
				os.close();
				is.close();
			} catch (IOException e) {
				return null;
			}
		}

		final List<String[]> questionList = new ArrayList<String[]>();
		AssetManager assetManager = context.getAssets();

		try {
			InputStream csvStream = assetManager.open(CVSname);
			InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
			CSVReader csvReader = new CSVReader(csvStreamReader);

			questionList.clear();
			String[] line;
			int count = 0;
			int fail = 0;

			while ((line = csvReader.readNext()) != null) {
				boolean isOk= false;
				if(sort == VALUE_WORDS){
					isOk = db.updateword(line);
				}else if(sort == VALUE_MEANS){
					isOk = db.updateMean(line);

				}else if(sort == VALUE_WrongRate){
					isOk = db.update_wrongrate(line);
				}
				if(isOk){
					count++;
				}else{
					fail++;
					//questionList.add(line);
				}

			}
			db.deleteWord(19097);

			Log.v("cvsread", "count : "+count +"  fail : "+fail);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return questionList;
	}



}
