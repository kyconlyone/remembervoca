package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;

public class StudyRoomUsePopup extends Activity{
	static final String TAG = "StudyRoomUsePopup";

	Handler handler;

	TextView mContentTextView;
	Button mUseButton;

	String mStudyRoomNameString;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_studyroom_use);

		handler = new Handler();


		mContentTextView = (TextView)findViewById(R.id.text_studyroomuse_content);
		mUseButton = (Button)findViewById(R.id.bt_studyroomuse_use);


		Intent intent = getIntent();

		mStudyRoomNameString = intent.getExtras().getString("Name");
		if(mStudyRoomNameString == null)
			mStudyRoomNameString = "가맹";

		SuccessContentSetting(mStudyRoomNameString);


		mUseButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	void SuccessContentSetting(String studyroom_name) {
		String mString1 = "세계최초 기억력 측정 영단어 어플,\n";
		String mString2 = "밀당영단어";
		String mString3 = "를 " + studyroom_name + " 독서실에서 \n무료로 이용하세요!";

		float mDptoPixel = convertDpToPixel(16, getApplicationContext());
		int m16Pixel = (int)mDptoPixel;

		SpannableStringBuilder mStringBuilder = new SpannableStringBuilder();

		SpannableString mSpanString1 = new SpannableString(mString1);
		mSpanString1.setSpan(new AbsoluteSizeSpan(m16Pixel), 0, mString1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mSpanString1.setSpan(new ForegroundColorSpan(Color.parseColor("#757575")), 0, mString1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mStringBuilder.append(mSpanString1);

		SpannableString mSpanString2 = new SpannableString(mString2);
		mSpanString2.setSpan(new AbsoluteSizeSpan(m16Pixel), 0, mString2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mSpanString2.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, mString2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mSpanString2.setSpan(new ForegroundColorSpan(Color.parseColor("#E94F37")), 0, mString2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mStringBuilder.append(mSpanString2);

		SpannableString mSpanString3 = new SpannableString(mString3);
		mSpanString3.setSpan(new AbsoluteSizeSpan(m16Pixel), 0, mString3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mSpanString3.setSpan(new ForegroundColorSpan(Color.parseColor("#757575")), 0, mString3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mStringBuilder.append(mSpanString3);

		mContentTextView.setText(mStringBuilder);
	}

	public static float convertDpToPixel(float dp, Context context){
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

}
