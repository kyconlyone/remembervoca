package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;

public class TwoButtonPopUp extends Activity {

	Button bt_yes_popup;
	Button bt_no_popup;
	TextView tv_doc_title;
	TextView tv_doc_contents;

	private DBPool db;

	static SharedPreferences mPreference;
	SharedPreferences.Editor editor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_twobutton);

		db = DBPool.getInstance(this);

		mPreference = getSharedPreferences(Config.PREFS_NAME,
				MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
		editor = mPreference.edit();

		tv_doc_title = (TextView) findViewById(R.id.tv_two_title);
		tv_doc_contents = (TextView) findViewById(R.id.tv_two_contents);
		bt_yes_popup = (Button) findViewById(R.id.bt_yes_popup);
		bt_no_popup = (Button) findViewById(R.id.bt_no_popup);

		final int sort = getIntent().getIntExtra("title", 1);

		switch (sort) {

		/*
		 * 11~20 등급선택 여부
		 */

			case 0:
				tv_doc_contents.setText("현재 단어장을\n" + "등급 단어장으로\n변경하시겠습니까?");
				tv_doc_title.setText("");
				bt_yes_popup.setText("변경");
				bt_no_popup.setText("취소");
				break;
			case 1:

				tv_doc_contents.setText("로그아웃하시겠습니까?");
				tv_doc_title.setText("");
				bt_yes_popup.setText("확인");
				bt_no_popup.setText("취소");
				break;
			case 2:

				tv_doc_contents.setText("공부 좀 하세요!");
				tv_doc_title.setText("");
				bt_yes_popup.setText("공부하러 가기");
				bt_no_popup.setText("나중으로 미루기");
				break;
			case 3:

				tv_doc_contents.setText("좀만 공부해봐~");
				tv_doc_title.setText("");
				bt_yes_popup.setText("공부하러 가기");
				bt_no_popup.setText("나중으로 미루기");
				break;
			case 4:

				tv_doc_contents.setText("오늘의 학습량을\n채우지 않았습니다.\n목표 학습량을 채워주세요.");
				tv_doc_title.setText("");
				bt_yes_popup.setText("공부하러 가기");
				bt_no_popup.setText("나중으로 미루기");
				break;
			case 5:

				tv_doc_contents.setText("이번 " + "에\n출간된 " + "\n영단어가 추가되었습니다.");
				tv_doc_title.setText("");
				bt_yes_popup.setText("공부하러 가기");
				bt_no_popup.setText("나중으로 미루기");
				break;
			case 6:

				tv_doc_contents.setText("에서\n밀당영단어가 나온 단어가\n" + "%이상 출제되었습니다.");
				tv_doc_title.setText("");
				bt_yes_popup.setText("공부하러 가기");
				bt_no_popup.setText("나중으로 미루기");
				break;

			case 7:

				tv_doc_contents.setText("지금 외우면 까먹지 않을 단어\n" + "개가\n"
						+ "님을 기다리고 있습니다.");
				tv_doc_title.setText("");
				bt_yes_popup.setText("공부하러 가기");
				bt_no_popup.setText("나중으로 미루기");
				break;

			case 11:

				tv_doc_contents.setText("현재 단어장을\n1등급 단어장으로\n변경하시겠습니까?");
				tv_doc_title.setText("");
				bt_yes_popup.setText("변경");
				bt_no_popup.setText("취소");
				break;

			case 12:

				tv_doc_contents.setText("현재 단어장을\n2-3등급 단어장으로\n변경하시겠습니까?");
				tv_doc_title.setText("");
				bt_yes_popup.setText("변경");
				bt_no_popup.setText("취소");
				break;

			case 13:

				tv_doc_contents.setText("현재 단어장을\n4-5등급 단어장으로\n변경하시겠습니까?");
				tv_doc_title.setText("");
				bt_yes_popup.setText("변경");
				bt_no_popup.setText("취소");
				break;

			case 14:

				tv_doc_contents.setText("현재 단어장을\n6-7등급 단어장으로\n변경하시겠습니까?");
				tv_doc_title.setText("");
				bt_yes_popup.setText("변경");
				bt_no_popup.setText("취소");
				break;

			case 15:

				tv_doc_contents.setText("현재 단어장을\n8-9등급 단어장으로\n변경하시겠습니까?");
				tv_doc_title.setText("");
				bt_yes_popup.setText("변경");
				bt_no_popup.setText("취소");
				break;

			default:
				break;
		}

		bt_yes_popup.setOnClickListener(new View.OnClickListener() {

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
					case 11:
						db.insertWordLevel(5);

						db.insertLevelHistory(5);
						Log.d("level_change", "lv5");
						finish();
						break;
					case 12:
						db.insertWordLevel(4);

						db.insertLevelHistory(4);
						Log.d("level_change", "lv4");
						finish();
						break;
					case 13:
						db.insertWordLevel(3);

						db.insertLevelHistory(3);
						Log.d("level_change", "lv3");
						finish();
						break;
					case 14:
						db.insertWordLevel(2);

						db.insertLevelHistory(2);
						Log.d("level_change", "lv2");
						finish();
						break;
					case 15:
						db.insertWordLevel(1);

						db.insertLevelHistory(1);
						Log.d("level_change", "lv1");
						finish();
						break;
					default:
						finish();
						break;
				}
			}
		});

		bt_no_popup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

}
