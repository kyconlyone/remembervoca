package com.ihateflyingbugs.hsmd.feedback;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.HitItem;
import com.ihateflyingbugs.hsmd.HitListAdapter;
import com.ihateflyingbugs.hsmd.HitListHeader;
import com.ihateflyingbugs.hsmd.HitListItem;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.Date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeforeHitActivity extends Activity {
	String TAG = "BeforeHitActivity";

	ListView mBeforeHit_ListView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_before_hit);

		setActionBar();

		//
		mBeforeHit_ListView = (ListView) findViewById(R.id.lv_list);

		//
		List<HitItem> items = new ArrayList<HitItem>();

		items.add(new HitListHeader());
		items.add(new HitListItem(null, null, null));

		HitListAdapter adapter = new HitListAdapter(this, items);
		mBeforeHit_ListView.setAdapter(adapter);
	}


	//
	ActionBar mActionBar;
	View mCustomActionBarView;

	LinearLayout mActionBar_Layout;
	LinearLayout mBack_Layout;
	TextView mTitle_TextView;
	//

	void setActionBar() {
		mActionBar = getActionBar();
		mCustomActionBarView = getLayoutInflater().inflate(R.layout.setting_action_bar, null);
		mActionBar.setCustomView(mCustomActionBarView, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		mActionBar_Layout = (LinearLayout)mCustomActionBarView.findViewById(R.id.layout_actionbar);
		mActionBar_Layout.setBackgroundColor(Color.parseColor("#2f6e9b"));

		mBack_Layout = (LinearLayout)mCustomActionBarView.findViewById(R.id.layout_actionbar_back);
		mBack_Layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BeforeHitActivity.this.finish();
			}
		});

		mTitle_TextView = (TextView)mCustomActionBarView.findViewById(R.id.text_actionbar_title);
		mTitle_TextView.setText("이전 시험 적중률");
	}


	String starttime;
	String startdate;
	Date date = new Date();
	Map<String, String> articleParams;

	public void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
		articleParams = new HashMap<String, String>();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("BeforeHitActivity", articleParams);
	}

	public void onStop()
	{
		super.onStop();
		FlurryAgent.endTimedEvent("BeforeHitActivity");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
	}
}
