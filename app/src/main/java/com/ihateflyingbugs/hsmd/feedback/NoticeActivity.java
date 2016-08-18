package com.ihateflyingbugs.hsmd.feedback;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.ihateflyingbugs.hsmd.tutorial.Feed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class NoticeActivity extends Activity {
	String TAG = "NoticeActivity";

	Context mContext;
	ArrayList<Feed> list;
	NoticeAdapter adapter;
	Handler handler;


	//
	ActionBar mActionBar;
	View mCustomActionBarView;

	LinearLayout mBack_Layout;
	TextView mTitle_TextView;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	//


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_notice);

		setActionBar();

		mContext = getApplicationContext();
		list = new ArrayList<Feed>();

		//
		ListView lv = (ListView) findViewById(R.id.list_setting_notice);

		handler = new Handler();

		adapter = new NoticeAdapter(mContext, R.layout.itemlist_feed_notice, list);
		lv.setAdapter(adapter);
		adapter.addAll(list);
		lv.setClickable(false);


		new RetrofitService().getBoardService().retroGetNotice().enqueue(new Callback<List<Feed>>() {
			@Override
			public void onResponse(Response<List<Feed>> response, Retrofit retrofit) {
				final List<Feed> notice = response.body();
				handler.post(new Runnable() {

					@Override
					public void run() {
						adapter.addAll(notice);
						adapter.notifyDataSetChanged();

					}
				});
			}

			@Override
			public void onFailure(Throwable t) {

				Log.e("notice_log", "notice fail" + t.toString());
			}
		});
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
	}

	public class NoticeAdapter extends ArrayAdapter<Feed> {
		List<Feed> list_study;
		private LayoutInflater mInflater;

		public NoticeAdapter(Context context, int resourceId,
							 ArrayList<Feed> items) {
			super(context, resourceId, items);
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.list_study = items;
			mContext = context;
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			Feed feed = list_study.get(position);
			final ViewHoder holder = new ViewHoder();

			convertView = mInflater.inflate(R.layout.itemlist_feed_notice, null);

			holder.ll_feed_notice = (LinearLayout) convertView.findViewById(R.id.ll_feed_notice);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_feed_notice_title);
			holder.tv_contents = (TextView) convertView.findViewById(R.id.tv_feed_notice_contents);
			holder.tv_day = (TextView) convertView.findViewById(R.id.tv_feed_notice_day);

			holder.tv_title.setText("" + feed.getTitle());
			holder.tv_contents.setText("" + feed.getContents());
			holder.tv_day.setText("" + feed.getDate().replace("-", "/").substring(0, 10));

			holder.iv_direcrion = (ImageView) convertView.findViewById(R.id.direction);

			holder.ll_feed_notice.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (holder.tv_contents.getVisibility() == View.VISIBLE) {
						holder.tv_contents.setVisibility(View.GONE);
						holder.iv_direcrion.setBackground(getResources().getDrawable(R.drawable.icon_faqsettingpage_pressicon_normal));

						FlurryAgent.logEvent("NoticeActivity:Click_Close_Contents");
					} else {
						holder.tv_contents.setVisibility(View.VISIBLE);
						holder.iv_direcrion.setBackground(getResources().getDrawable(R.drawable.icon_faqsettingpage_pressicon_normal_down));

						FlurryAgent.logEvent("NoticeActivity:Click_Show_Contents");
					}
				}
			});
			convertView.setTag(holder);

			return convertView;
		}

	}

	class ViewHoder {
		LinearLayout ll_feed_notice;
		TextView tv_title;
		TextView tv_contents;
		TextView tv_day;
		ImageView iv_direcrion;
	}


	void setActionBar() {
		mActionBar = getActionBar();
		mCustomActionBarView = getLayoutInflater().inflate(R.layout.setting_action_bar, null);
		mActionBar.setCustomView(mCustomActionBarView, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		mBack_Layout = (LinearLayout) mCustomActionBarView.findViewById(R.id.layout_actionbar_back);
		mBack_Layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FlurryAgent.logEvent("NoticeActivity:Click_Close_BackButton");
				NoticeActivity.this.finish();
			}
		});

		mTitle_TextView = (TextView) mCustomActionBarView.findViewById(R.id.text_actionbar_title);
		mTitle_TextView.setText("공지사항");
	}


	String starttime;
	String startdate;
	Date date = new Date();
	Map<String, String> articleParams;

	public void onStart() {
		super.onStart();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("NoticeActivity", articleParams);
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
//		Action viewAction = Action.newAction(
//				Action.TYPE_VIEW, // TODO: choose an action type.
//				"Notice Page", // TODO: Define a title for the content shown.
//				// TODO: If you have web page content that matches this app activity's content,
//				// make sure this auto-generated web page URL is correct.
//				// Otherwise, set the URL to null.
//				Uri.parse("http://host/path"),
//				// TODO: Make sure this auto-generated app URL is correct.
//				Uri.parse("android-app://com.ihateflyingbugs.hsmd.feedback/http/host/path")
//		);
//		AppIndex.AppIndexApi.start(client, viewAction);4
	}

	public void onStop() {
		super.onStop();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
//		Action viewAction = Action.newAction(
//				Action.TYPE_VIEW, // TODO: choose an action type.
//				"Notice Page", // TODO: Define a title for the content shown.
//				// TODO: If you have web page content that matches this app activity's content,
//				// make sure this auto-generated web page URL is correct.
//				// Otherwise, set the URL to null.
//				Uri.parse("http://host/path"),
//				// TODO: Make sure this auto-generated app URL is correct.
//				Uri.parse("android-app://com.ihateflyingbugs.hsmd.feedback/http/host/path")
//		);
		FlurryAgent.endTimedEvent("NoticeActivity");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", "" + ((Long.valueOf(System.currentTimeMillis()) - Long.valueOf(starttime))) / 1000);
		FlurryAgent.onEndSession(this);
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
	}
}
