package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.kakao.auth.Session;

public class WordExamResultPopup extends Activity implements OnClickListener {


	DBPool db;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_word_exam);

		Config.isKakaoStory = false;


		db = DBPool.getInstance(getApplicationContext());

		db.deletePushTable();

		TextView tv_wordexam_title = (TextView)findViewById(R.id.tv_wordexam_title);
		Button bt_wordexam_confirm = (Button)findViewById(R.id.bt_wordexam_confirm);
		Button bt_wordexam_cancel = (Button)findViewById(R.id.bt_wordexam_cancel);

		SharedPreferences mPreferences = getSharedPreferences(Config.PREFS_NAME,
				MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE
						| MODE_MULTI_PROCESS);

		tv_wordexam_title.setText(mPreferences.getString(MainValue.GpreName, "이름없음").toString()+"님, 현재 전국 단어"+"모의고사가 진행되고 있습니다." );

		bt_wordexam_cancel.setOnClickListener(this);
		bt_wordexam_confirm.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_wordexam_confirm:
				Intent intent;
				String url = "http://lnslab.com/sat/loading.php";
				Uri uri = Uri.parse(url);

				Log.e("accesstoken", "카카오 : "+ Session.getCurrentSession().getAccessToken());
				Log.e("accesstoken", "아아디 : "+!db.getStudentId().equals(""));

				try {
					if(Session.getCurrentSession().isOpened()&&!db.getStudentId().equals("")){
						url +="?sat_user_id="+db.getStudentId()+"&"+"access_token="+Session.getCurrentSession().getAccessToken();
					}
				} catch (IllegalStateException e) {
					// TODO: handle exception
				}

				Uri sat_uri = Uri.parse(url);
				Log.e("accesstoken", "url : "+url);
				intent = new Intent(Intent.ACTION_VIEW, sat_uri);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(intent);

				break;

			case R.id.bt_wordexam_cancel:
				finish();
				break;

			default:
				break;
		}
	}
}
