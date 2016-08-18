package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.ihateflyingbugs.hsmd.R;

public class FriendRecommend_SelectDialogActivity extends Activity{
	String TAG = " FriendRecommend_SelectDialogActivity";

	TextView mContentTextView;
	Button mSelectButton;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String period;
		try {

			period = getIntent().getExtras().getString("period");
		} catch (Exception e) {
			// TODO: handle exception
			period = "30";
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.popup_friendrecommend);

		mContentTextView = (TextView)findViewById(R.id.text_friendrecommend_content);
		mSelectButton = (Button)findViewById(R.id.btn_friednrecommend_select);

		mContentTextView.setText("\"빨리 가려면 혼자 가고 \n멀리 가려면 같이 가라\"\n\n함께 단어 공부하고 싶은\n친구 5명을 선택해 주세요.\n"+period+"일 무료 사용권을 모두 드릴게요^^");
		mContentTextView.setTextColor(Color.parseColor("#8A000000"));

		mSelectButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.e(TAG, "SelectButton Click");
				finish();
			}
		});


	}


}
