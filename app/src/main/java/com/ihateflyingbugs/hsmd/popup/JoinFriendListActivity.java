package com.ihateflyingbugs.hsmd.popup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.FriendCard;
import com.ihateflyingbugs.hsmd.indicator.BaseSampleActivity;
import com.ihateflyingbugs.hsmd.indicator.FriendFragment;
import com.ihateflyingbugs.hsmd.indicator.FriendFragmentAdapter;
import com.ihateflyingbugs.hsmd.login.TimeClass;
import com.ihateflyingbugs.hsmd.model.AuthorizationData;
import com.ihateflyingbugs.hsmd.data.Friend;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.ihateflyingbugs.hsmd.service.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class JoinFriendListActivity extends BaseSampleActivity implements OnClickListener {


	DBPool db;
	SharedPreferences mPreference;
	final String TAG = "JoinFriendListActivity";

	FriendFragmentAdapter mAdapter;
	ViewPager mPager;

	List<FriendFragment> fragment_list;

	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.popup_join_friendlist);

		Log.d("ActiviityTest", "RequestPaymentActivity Create");

		db = DBPool.getInstance(getApplicationContext());

		db.setPushTable(1, 0);

		handler = new Handler();

		final ProgressBar pb_join_friendlist = (ProgressBar) findViewById(R.id.pb_join_friendlist);

		mPager = (ViewPager) findViewById(R.id.pager_join_friendlist);

		fragment_list = new ArrayList<FriendFragment>();

		new RetrofitService().getAuthorizationService().retroGetFriendList(db.getStudentId())
				.enqueue(new Callback<AuthorizationData>() {
					@Override
					public void onResponse(final Response<AuthorizationData> response, Retrofit retrofit) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
									List<Friend> friend_list = response.body().getFriend_list();
									if(friend_list.size()!=0){
										for (int i = 0; i < friend_list.size(); i++) {
											String name = friend_list.get(i).getStudent_name();
											double use_days = friend_list.get(i).getUse_days();
											String first_goal_time = friend_list.get(i).getFirst_goal_time();
											String profile_image_url = friend_list.get(i).getProfile_image_url();

											String school;
											if (friend_list.get(i).getSchool_name().equals("null")) {
												school = "고등학교";
											} else {
												school = friend_list.get(i).getSchool_name();
											}

											String grade = "재학생";

											String birth = friend_list.get(i).getBirth();
											if (!birth.equals("null")) {

												int sum = 0;

												if (Integer.valueOf((String) birth.subSequence(0, 2)) > 50) {
													sum = 1900;
													int years = Integer.valueOf((String) birth.subSequence(0, 2)) - 1;
													sum += years;
												} else {
													sum = 2000;
													int years = Integer.valueOf((String) birth.subSequence(0, 2)) - 1;
													sum += years;
												}

												if (birth.subSequence(2, 4).equals("01") || birth.subSequence(2, 4).equals("02")) {
													sum--;
												}

												int age = Integer.valueOf(TimeClass.getYear()) - sum;

												if (age == 17 || age == 14) {
													grade = "1학년";
												} else if (age == 18 || age == 15) {
													grade = "2학년";
												} else if (age == 19 || age == 16) {
													grade = "3학년";
												} else if (age > 19) {
													grade = "졸업생";
												}

											}

											FriendCard friendCard = new FriendCard(name, school, grade, use_days, first_goal_time, profile_image_url);
											fragment_list.add(new FriendFragment().newInstance(friendCard));
										}

										pb_join_friendlist.setVisibility(View.GONE);
										mAdapter = new FriendFragmentAdapter(getSupportFragmentManager(), fragment_list);
										mPager.setClipToPadding(false);
										mPager.setPadding(Utils.dpToPixels(48, getResources()), 0, Utils.dpToPixels(48, getResources()), 0);

										mPager.setAdapter(mAdapter);
										mPager.setVisibility(View.VISIBLE);
										pb_join_friendlist.setVisibility(View.GONE);

									}else{
										finish();
									}
							}
						});
					}

					@Override
					public void onFailure(Throwable t) {
						Log.e(TAG, "onFailure : "+t.toString());
					}
				});

		Button bt_join_friendlist = (Button)findViewById(R.id.bt_join_friendlist);

		bt_join_friendlist.setOnClickListener(this);

	}


	boolean isSend = false;
	long Time_send = 0;

	@Override
	public void onClick(View v) {
		int type = 0;
		switch (v.getId()) {
			case R.id.bt_join_friendlist:
				finish();
				break;
		}
	}



	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		// android.os.Process.killProcess(android.os.Process.myPid());
		setResult(55555);
		finish();
		return;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isSend = false;
	}

	String Current_Activity = "aaaa";

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		//		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		//		ComponentName componentInfo = taskInfo.get(0).topActivity;
		//
		//		if (isSend) {
		//			if (Current_Activity.equals("com.kakao.talk.activity.TaskRootActivity")
		//					|| Current_Activity.equals("com.kakao.talk.activity.friend.picker.ConnectBroadcastFriendsPickerActivity")
		//					|| Current_Activity.equals("com.kakao.talk.activity.main.MainTabFragmentActivity")) {
		//				if (componentInfo.getClassName().equals("com.kakao.talk.activity.chat.ChatRoomActivity")) {
		//					new Async_send_click_request().execute();
		//					Log.i("otherapp", "보냄");
		//				} else {
		//					Log.i("otherapp", "애매함");
		//				}
		//			}
		//			Current_Activity = componentInfo.getClassName().toString();
		//		} else {
		//			Current_Activity = "aaaa";
		//		}
		//
		//		Log.e("popAct", "" + Current_Activity);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		System.out.println("requestCode : " + requestCode);

		System.out.println("resultCode : " + resultCode);

		if (resultCode == 55555) {
			setResult(55555);
			finish();
		}

	}
}
