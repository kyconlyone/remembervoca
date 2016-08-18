package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;

public class OneButtonPopUp extends Activity {

	Button bt_close_doc_popup;
	TextView tv_doc_title;
	TextView tv_doc_contents;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_onebutton);

		tv_doc_title = (TextView) findViewById(R.id.tv_doc_title);
		tv_doc_contents = (TextView) findViewById(R.id.tv_doc_contents);
		bt_close_doc_popup = (Button) findViewById(R.id.bt_close_doc_popup);

		final int sort = getIntent().getIntExtra("caller", 1);

		/*
		 * caller 0 ~ : 10 ~ :의지관련 푸시 20 ~ :기억관련 푸시 30 ~ :적중관련 푸시 40 ~ :
		 */

		switch (sort) {
			case 0:
				tv_doc_contents.setText("블라블라");
				tv_doc_title.setText("의지관련 푸쉬기능이란?");
				break;
			case 1:

				tv_doc_contents.setText("블라블라");
				tv_doc_title.setText("카톡차단 기능이란?");
				break;
			case 2:

				tv_doc_contents.setText("블라블라");
				tv_doc_title.setText("기억관련 푸쉬기능이란?");
				break;
			case 10:

				tv_doc_contents.setText("의지관련 푸시를 켭니다!");
				tv_doc_title.setText("의지 푸시 끔");
				break;
			case 11:

				tv_doc_contents.setText("의지관련 푸시를 끕니다!");
				tv_doc_title.setText("의지 푸시 켬");
				break;
			case 20:

				tv_doc_contents.setText("기억관련 푸시를 켭니다!");
				tv_doc_title.setText("기억 푸시 끔");
				break;
			case 21:

				tv_doc_contents.setText("기억관련 푸시를 끕니다!");
				tv_doc_title.setText("기억 푸시 켬");
				break;


			case 30:

				tv_doc_contents.setText("블라블라");
				tv_doc_title.setText("적중관련 푸쉬기능이란?");
				break;
			case 31:

				tv_doc_contents.setText("1등급 단어장은 블라블라");
				tv_doc_title.setText("");
				break;
			case 32:

				tv_doc_contents.setText("2-3등급 단어장은 블라블라");
				tv_doc_title.setText("");
				break;
			case 33:

				tv_doc_contents.setText("4-5등급 단어장은 블라블라");
				tv_doc_title.setText("");
				break;
			case 34:

				tv_doc_contents.setText("6-7등급 단어장은 블라블라");
				tv_doc_title.setText("");
				break;

			case 35:

				tv_doc_contents.setText("8-9등급 단어장은 블라블라");
				tv_doc_title.setText("");

				break;

			case 36:
				tv_doc_contents.setText("블라블라 기능");
				tv_doc_title.setText("OFF시 해당정보가\n제공되지 않습니다.");

				break;

			case 37:

				tv_doc_contents.setText("블라블라 기능");
				tv_doc_title.setText("ON시 해당정보가\n제공됩니다.");

			case 40:
				tv_doc_contents.setText("블라블라 기능");
				tv_doc_title.setText("OFF시 해당정보가\n제공되지 않습니다.");

				break;

			case 41:

				tv_doc_contents.setText("블라블라 기능");
				tv_doc_title.setText("ON시 해당정보가\n제공됩니다.");


				break;



			default:
				break;
		}

		bt_close_doc_popup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				switch (sort) {
					case 0:
						finish();
						break;
					case 1:
						finish();
						break;
					case 2:
						finish();
						break;
					case 3:
						finish();
						break;
					case 4:
						finish();
						break;
					case 5:
						finish();
						break;
					case 6:
						finish();
						break;
					default:
						finish();
						break;
				}
			}
		});

	}

}
