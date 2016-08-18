package com.ihateflyingbugs.hsmd.tutorial;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;


public class FeedbackPopup extends Activity  implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.popup_tutorial);

		String word =null;
		String title = null;
		String contents =null;

		int sort = getIntent().getIntExtra("title",1);

		TextView tv_tupop_word = (TextView)findViewById(R.id.tv_tupop_word);
		TextView tv_title = (TextView)findViewById(R.id.tv_tupop_title);
		TextView tv_contents = (TextView)findViewById(R.id.tv_tupop_contents);
		ImageView iv_popup_mark = (ImageView)findViewById(R.id.iv_popup_mark);
		Button bt_close_tutopopup = (Button)findViewById(R.id.bt_close_tutopopup);

		bt_close_tutopopup.setOnClickListener(this);

		if(sort==1){
			tv_tupop_word.setVisibility(View.VISIBLE);
			iv_popup_mark.setVisibility(View.GONE);

			word = getIntent().getStringExtra("word");
			title = "이미 아시는 단어시군요 !";
			// Convert the buffer into a string.
			contents = "알고있던 단어로 분류하겠습니다. ";
		}else if(sort == 2){
			tv_tupop_word.setVisibility(View.VISIBLE);
			iv_popup_mark.setVisibility(View.GONE);

			word = getIntent().getStringExtra("word");

			title = "외우기 어렵지 않은 단어군요!";

			contents = "아니면 외웠던건데 가물가물하셨나요?";
		}else if(sort == 3){
			tv_tupop_word.setVisibility(View.VISIBLE);
			iv_popup_mark.setVisibility(View.GONE);

			title = "외우기 조금 어려우셨군요";
			word = getIntent().getStringExtra("word");

			contents = "머신러닝이 당신이 힘들게 외운 단어를 잊지 않도록 관리해드릴게요!";
		}else if(sort == 4){
			tv_tupop_word.setVisibility(View.VISIBLE);
			iv_popup_mark.setVisibility(View.GONE);

			word = getIntent().getStringExtra("word");

			title = "정말 외우기 어려우셨죠?";

			contents = "외우기 어려운 단어는 잊기도 쉽습니다\n완벽하게 외우실 때까지 자주 보이게 될거에요 ";
		}else if(sort == 5){
			tv_tupop_word.setVisibility(View.VISIBLE);
			iv_popup_mark.setVisibility(View.GONE);

			word = getIntent().getStringExtra("word");

			title = "아직 모든 단어를 확인하지 않으셨어요~";

			contents = "뒤질랜드?";
		}else if(sort == 6){
			tv_tupop_word.setVisibility(View.GONE);
			iv_popup_mark.setVisibility(View.VISIBLE);
			iv_popup_mark.setImageResource(R.drawable.tuto_popup_ques);

			word = getIntent().getStringExtra("word");

			title = "아직 못 외운 단어입니다";

			contents = "뜻을 열어봤지만 외웠다고 표시하지 않은 단어에요";
		}else if(sort == 7){
			tv_tupop_word.setVisibility(View.GONE);
			iv_popup_mark.setVisibility(View.VISIBLE);
			iv_popup_mark.setImageResource(R.drawable.tuto_popup_check);

			word = getIntent().getStringExtra("word");

			title = "복습이 필요한 단어입니다";

			contents = "이미 외우셨지만 지금 복습하지 않으면 잊을 수도 있는 단어에요";
		}else if(sort == 8){
			tv_tupop_word.setVisibility(View.GONE);
			iv_popup_mark.setVisibility(View.VISIBLE);
			iv_popup_mark.setImageResource(R.drawable.tuto_popup_check);

			word = getIntent().getStringExtra("word");

			title = "아무것도 없을땐";

			contents = "새로 학습할 단어입니다";
		}


		tv_tupop_word.setText("'"+word+"'");
		tv_title.setText(title);


		tv_contents.setText(contents);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.bt_close_tutopopup:
				setResult(Activity.RESULT_OK);
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				break;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		FlurryAgent.onEndSession(this);
	}


}
