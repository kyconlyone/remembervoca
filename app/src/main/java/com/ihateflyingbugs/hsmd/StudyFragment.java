package com.ihateflyingbugs.hsmd;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.tutorial.Feed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudyFragment extends Fragment implements OnClickListener{

	private static Activity thisActivity;

	private static Handler handler;

	Context mContext;
	DBPool db;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		thisActivity = getActivity();
		mContext = getActivity().getApplicationContext();
		View view = inflater.inflate(R.layout.fragment_study_list, container, false);

		handler= new Handler();
		db = DBPool.getInstance(mContext);

		ListView lv = (ListView)view.findViewById(R.id.study_listView);


		ArrayList<Feed> list = new ArrayList<Feed>();
		list.clear();
		list.addAll(db.getStudyList());

		StudyAdapter adapter = new StudyAdapter(mContext, R.layout.itemlist_feed_study, list);
		lv.setAdapter(adapter);

		return view;
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		}
	}
	public class StudyAdapter extends ArrayAdapter<Feed>{
		List<Feed> list_study;
		private LayoutInflater mInflater;

		public StudyAdapter(Context context, int resourceId, ArrayList<Feed> items){
			super(context, resourceId, items);
			mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.list_study = items;
			mContext= context;
		}

		/* (non-Javadoc)
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			int type = getItemViewType(position);
			Feed feed = list_study.get(position);
			ViewHoder holder = null;
			System.out.println("getView " + position + " " + convertView + " type = " + type);

			holder = new ViewHoder();

			convertView = mInflater.inflate(R.layout.itemlist_feed_study, null);
			//holder.text_title= (TextView)convertView.findViewById(R.id.tv_feed_study_title);
			holder.tv_total_count = (TextView)convertView.findViewById(R.id.tv_study_feed_total_study);
			holder.tv_new_count = (TextView)convertView.findViewById(R.id.tv_study_feed_new);
			holder.tv_review = (TextView)convertView.findViewById(R.id.tv_study_feed_review);
			holder.tv_month = (TextView)convertView.findViewById(R.id.tv_study_feed_month);
			holder.tv_day = (TextView)convertView.findViewById(R.id.tv_study_feed_day);

			int New_Study = feed.getNew_study_count();
			int Review_Study=feed.getDo_review_count();
			int Origin_Reiview = feed.getTotal_review_count();
			int Total_Study =New_Study+ Review_Study ;
			holder.tv_month.setText(""+feed.getDate().substring(0, 4)+"."+feed.getDate().substring(4, 6));
			holder.tv_day.setText(""+feed.getDate().substring(6, feed.getDate().length()));
			holder.tv_day = (TextView)convertView.findViewById(R.id.tv_study_feed_day);
			holder.tv_total_count.setText(""+Total_Study);
			holder.tv_new_count.setText(""+feed.getNew_study_count());
			holder.tv_review.setText(""+Review_Study+"/"+Origin_Reiview);

			convertView.setTag(holder);

			return convertView;
		}

	}

	class ViewHoder{
		TextView tv_month;
		TextView tv_day;
		TextView tv_total_count;
		TextView tv_new_count;
		TextView tv_review;
	}


	String starttime;
	String startdate;
	Date date = new Date();
	Map<String, String> articleParams ;

	public void onStart()
	{

		super.onStart();
		articleParams = new HashMap<String, String>();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		//FlurryAgent.logEvent("StudyConditionFragment", true);
		FlurryAgent.logEvent("SideActivity_StudyConditionFragment", articleParams);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 시작,FinishStudyFragment,1]\r\n");

	}

	public void onStop()
	{
		super.onStop();
		//FlurryAgent.endTimedEvent("StudyConditionFragment:Start");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		Log.e("splash", startdate+"        "+date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 끝,FinishStudyFragment,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");
	}
}
