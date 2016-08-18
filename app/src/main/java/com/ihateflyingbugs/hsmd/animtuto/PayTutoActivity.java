package com.ihateflyingbugs.hsmd.animtuto;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.popup.SetLockscreenPopUp;

public class PayTutoActivity extends Activity implements OnClickListener {

	RelativeLayout rl_tuto_pay_bg;
	LinearLayout ll_tuto_pay1;
	LinearLayout ll_tuto_pay2;
	LinearLayout ll_tuto_pay3;

	TextView tv_tuto_pay1;
	TextView tv_tuto_pay2;

	Button bt_tuto_pay1;
	Button bt_tuto_pay2;
	Button bt_tuto_pay3;

	SharedPreferences mPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Log.d("ActiviityTest", "PayTutoActivity Create");

		setContentView(R.layout.activity_pay_tutorial);

		mPreference = getSharedPreferences(MainValue.preName, MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);

		rl_tuto_pay_bg = (RelativeLayout) findViewById(R.id.rl_tuto_pay_bg);

		ll_tuto_pay1 = (LinearLayout) findViewById(R.id.ll_tuto_pay1);
		ll_tuto_pay2 = (LinearLayout) findViewById(R.id.ll_tuto_pay2);
		ll_tuto_pay3 = (LinearLayout) findViewById(R.id.ll_tuto_pay3);

		tv_tuto_pay1 = (TextView) findViewById(R.id.tv_tuto_pay1);
		tv_tuto_pay2 = (TextView) findViewById(R.id.tv_tuto_pay2);

		bt_tuto_pay1 = (Button) findViewById(R.id.bt_tuto_pay1);
		bt_tuto_pay2 = (Button) findViewById(R.id.bt_tuto_pay2);
		bt_tuto_pay3 = (Button) findViewById(R.id.bt_tuto_pay3);



		int length = mPreference.getString(MainValue.GpreName, "안알랴줌").length();

		SpannableStringBuilder sb1 = new SpannableStringBuilder();
		String pay_1_text = "이곳이 " + mPreference.getString(MainValue.GpreName, "안알랴줌") + "님이\n앞으로 수능까지 단어를 암기할\n'단어장 페이지' 입니다.";
		sb1.append(pay_1_text);
		sb1.setSpan(new StyleSpan(Typeface.BOLD), 24+length, 33+length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		tv_tuto_pay1.setText(sb1);

		SpannableStringBuilder sb2 = new SpannableStringBuilder();
		String pay_2_text = "위 버튼을 누르면\n" + mPreference.getString(MainValue.GpreName, "안알랴줌") + "님의\n'학습 관리 페이지'를 볼 수 있습니다.";
		sb2.append(pay_2_text);
		sb2.setSpan(new StyleSpan(Typeface.BOLD), 14+length, 24+length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		tv_tuto_pay2.setText(sb2);


		bt_tuto_pay1.setOnClickListener(this);
		bt_tuto_pay2.setOnClickListener(this);
		bt_tuto_pay3.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_tuto_pay1:
				ll_tuto_pay1.setVisibility(View.GONE);
				ll_tuto_pay2.setVisibility(View.VISIBLE);
				rl_tuto_pay_bg.setBackgroundResource(R.drawable.bg_tuto_this_is_studyfeedback);
				break;
			case R.id.bt_tuto_pay2:
				ll_tuto_pay2.setVisibility(View.GONE);
				ll_tuto_pay3.setVisibility(View.VISIBLE);
				rl_tuto_pay_bg.setBackgroundResource(R.drawable.bg_tuto_this_is_longtouch);
				break;
			case R.id.bt_tuto_pay3:
				mPreference.edit().putString("firststart", ""+1).commit();
				startActivity(new Intent(PayTutoActivity.this, SetLockscreenPopUp.class));
				finish();
				break;
			default:
				break;
		}
	}


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		setResult(55555);
		finish();
		startActivity(new Intent(PayTutoActivity.this, SetLockscreenPopUp.class));
		return;
	}
}
