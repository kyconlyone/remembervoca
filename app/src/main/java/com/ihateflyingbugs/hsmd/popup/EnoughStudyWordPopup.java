package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.DBPool;

public class EnoughStudyWordPopup extends Activity {


	DBPool db;

	Handler handler;

	int word_code;
	Button bt_enoughstudy_cancel;
	Button bt_enoughstudy_ok;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.popup_enough_study_word);


		handler = new Handler();

		db= DBPool.getInstance(getApplicationContext());

		word_code = getIntent().getExtras().getInt("word_code");

		bt_enoughstudy_cancel =  (Button)findViewById(R.id.bt_enoughstudy_cancel);
		bt_enoughstudy_ok =  (Button)findViewById(R.id.bt_enoughstudy_ok);

		bt_enoughstudy_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		bt_enoughstudy_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				db.setWordState(word_code, 200);
				setResult(RESULT_OK);
				finish();
			}
		});


	}


	@Override
	public void onBackPressed() {

	}
}
