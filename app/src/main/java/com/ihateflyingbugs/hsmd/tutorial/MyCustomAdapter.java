package com.ihateflyingbugs.hsmd.tutorial;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class MyCustomAdapter  extends BaseAdapter{

	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SEPARATOR = 1;
	private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

	private LayoutInflater mInflater;
	LayoutInflater vi;
	List<Feed> list_feed;
	Context mContext;
	Activity thisActivity;
	ListView lv;

	private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();

	public MyCustomAdapter(Context context,Activity thisActivity, ListView lv ){
		mContext= context;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		list_feed = new ArrayList<Feed>();
		this.thisActivity = thisActivity;
		this.lv = lv;
	}


	public void addItem(final Feed item) {
		list_feed.add(item);

		notifyDataSetChanged();
	}
	public void clear(){
		list_feed.clear();
		mSeparatorsSet.clear();
	}

	public void addSeparatorItem(final Feed item) {
		list_feed.add(item);
		// save separator position
		mSeparatorsSet.add(list_feed.size()-1);
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	public int getCount() {
		return list_feed.size();
	}

	public Feed getItem(int position) {
		return list_feed.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		int type = getItemViewType(position);
		Feed feed = list_feed.get(position);
		FeedViewHolder holder = null;
		View view = null;

		System.out.println("getView " + position + " " + convertView + " type = " + type);

		holder = new FeedViewHolder();
		switch (type) {
			case TYPE_ITEM:
				switch (feed.getType()) {

					case Feed.NOTICE:

						convertView = mInflater.inflate(R.layout.item_feed_notice, null);
						holder.text_title= (TextView)convertView.findViewById(R.id.tv_feed_notice_title);
						holder.text_contents = (TextView)convertView.findViewById(R.id.tv_feed_notice_contents);
						holder.iv_feed_notice= (ImageView)convertView.findViewById(R.id.iv_feed_notice);

						holder.text_title.setText(feed.getTitle());
						holder.text_contents.setText(feed.getContents());

						holder.iv_feed_notice.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
					/*	Intent intent_notice= new Intent(mContext, com.ihateflyingbugs.hsmd.SideActivity.class);
						intent_notice.putExtra("Fragment", SideActivity.FRAGMENT_BOARD);
						intent_notice.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						thisActivity.getApplication().startActivity(intent_notice);*/
								thisActivity.overridePendingTransition(R.anim.slide_in_left, 0);
								lv.startAnimation(new AnimationUtils().loadAnimation(mContext, R.anim.slide_out_left));
								lv.setVisibility(View.GONE);
							}
						});
						break;
					case Feed.LEVEL_UP:
						convertView = mInflater.inflate(R.layout.item_feed_levelup, null);
						holder.text_title= (TextView)convertView.findViewById(R.id.tv_feed_level_title);
						holder.text_contents = (TextView)convertView.findViewById(R.id.tv_feed_level_contents);
						holder.iv_feed_level= (ImageView)convertView.findViewById(R.id.iv_feed_level);

						holder.text_title.setText("레벨이 변경되었습니다.\n"+feed.getTitle());
						holder.text_contents.setText(feed.getContents());

						holder.iv_feed_level.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub

							}
						});
						break;
					case Feed.STUDY_FEEDBACK:

						convertView = mInflater.inflate(R.layout.item_feed_study, null);
						//holder.text_title= (TextView)convertView.findViewById(R.id.tv_feed_study_title);
						holder.tv_total_count = (TextView)convertView.findViewById(R.id.tv_feed_total_study);
						holder.tv_new_count = (TextView)convertView.findViewById(R.id.tv_feed_new);
						holder.iv_feed_study= (ImageView)convertView.findViewById(R.id.iv_feed_study);

						//holder.text_title.setText(feed.getTitle());
						holder.tv_total_count.setText(""+feed.getDo_review_count()+"/"+feed.getTotal_review_count());
						holder.tv_new_count.setText(""+feed.getNew_study_count());

						holder.iv_feed_study.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
						/*Intent intent_study= new Intent(mContext, com.ihateflyingbugs.hsmd.SideActivity.class);
						intent_study.putExtra("Fragment", SideActivity.FRAGMENT_Study);
						intent_study.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						thisActivity.getApplication().startActivity(intent_study);*/
								thisActivity.overridePendingTransition(R.anim.slide_in_left, 0);
								lv.startAnimation(new AnimationUtils().loadAnimation(mContext, R.anim.slide_out_left));
								lv.setVisibility(View.GONE);
							}
						});

						break;

				}
				break;
			case TYPE_SEPARATOR:
				convertView = mInflater.inflate(R.layout.item_header, null);
				holder.text_title = (TextView)convertView.findViewById(R.id.tv_section_date);
				String year = feed.getDate().substring(0, 4);
				String month= feed.getDate().substring(4, 6);
				String day= feed.getDate().substring(6, 8);

				holder.text_title.setText(year+"."+month+"."+day);
				break;
		}
		convertView.setTag(holder);


		return convertView;
	}



}

