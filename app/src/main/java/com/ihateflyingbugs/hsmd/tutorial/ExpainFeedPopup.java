package com.ihateflyingbugs.hsmd.tutorial;


import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;

import java.util.Timer;
import java.util.TimerTask;


public class ExpainFeedPopup extends Activity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.popup_tutorial);

		String title =null;
		String contents =null;

		int sort = getIntent().getIntExtra("title",1);

		if(sort == 10){
			title = "복습할 단어";

			contents = "뒤질랜드?";
		}else if(sort == 11){

			title = "현재 외운 단어";

			contents = "완전히 외운 단어";
		}else if(sort == 12){

			title = "완전히 외운 단어";

			contents = "뒤질랜드?";
		}else if(sort == 13){

			title = "모르는 단어";

			contents = "뒤질랜드?";
		}else if(sort == 14){
			title = "학습 대기중인 단어";
			contents = "뒤질랜드?";
		}else if(sort == 15){
			title = "총 학습 단어";
			contents = "뒤질랜드?";
		}

		TextView tv_tupop_word = (TextView)findViewById(R.id.tv_tupop_word);
		TextView tv_title = (TextView)findViewById(R.id.tv_tupop_title);
		TextView tv_contents = (TextView)findViewById(R.id.tv_tupop_contents);
		tv_tupop_word.setText("'"+title+"'");
		tv_title.setText(contents);


		tv_contents.setText(contents);

		Timer timer= new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		}, 2000);


	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}


}
