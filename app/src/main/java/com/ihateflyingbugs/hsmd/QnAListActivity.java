package com.ihateflyingbugs.hsmd;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MyQnA;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class QnAListActivity extends Activity implements OnClickListener {

	private Activity thisActivity;

	private static Handler handler;
	static QnaAdapter adapter;

	Context mContext;
	Button bt_make_qna;
	static List<MyQnA> list_qna;

	private ActionBar actionBar;
	private View actionBar_View;

	private ImageView iv_back;
	private TextView tv_title;


	//
	ActionBar mActionBar;
	View mCustomActionBarView;

	LinearLayout mBack_Layout;
	TextView mTitle_TextView;
	List<MyQnA> QnAList;
	DBPool db;
	//

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_qna_list);

		setActionBar();


		mContext = getApplicationContext();
		ListView lv = (ListView) findViewById(R.id.qna_listView);
		bt_make_qna = (Button) findViewById(R.id.bt_make_qna);
		bt_make_qna.setOnClickListener(this);
		list_qna = new ArrayList<MyQnA>();
		adapter = new QnaAdapter(mContext, R.layout.itemlist_feed_faq, list_qna);
		lv.setAdapter(adapter);
		handler = new Handler();
		Log.e("success", "Async_get_indiv_anwer  start");
		db = DBPool.getInstance(getApplicationContext());



		lv.setClickable(false);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */

	@Override
	protected void onResume() {
		super.onResume();
		new RetrofitService().getBoardService().retroGetQnABoard(db.getStudentId()).enqueue(new Callback<List<MyQnA>>() {
			@Override
			public void onResponse(Response<List<MyQnA>> response, Retrofit retrofit) {
				list_qna = response.body();

				Log.e("qna_log", "QnA onResponse" +  response.body().size());
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							adapter.clear();
							adapter.addAll(list_qna);
							adapter.notifyDataSetChanged();
						}catch (Exception e){
							Log.e("qna_log", "QnA Exception" +  e.toString());
						}
					}
				});
			}

			@Override
			public void onFailure(Throwable t) {

				Log.e("qna_log", "QnA fail" + t.toString());
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.bt_make_qna:
				// FragmentTransaction fragmentTransaction =
				// getFragmentManager().beginTransaction();
				// fragmentTransaction.replace(R.id.linearFragment, new
				// QnAFragment()).addToBackStack(null).commit();
				// thisActivity.overridePendingTransition(R.anim.slide_in_left,
				// R.anim.slide_out_left);
				Intent intent = new Intent(this, QnAActivity.class);
				FlurryAgent.logEvent("QnaListActivity:Click_Write_Qna");
				startActivity(intent);
				break;

			default:
				break;
		}
	}

	public class QnaAdapter extends ArrayAdapter<MyQnA> {
		List<MyQnA> list_QnA;
		private LayoutInflater mInflater;
		ViewHoder holder;

		public QnaAdapter(Context context, int resourceId, List<MyQnA> list_qna) {
			super(context, resourceId, list_qna);
			mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.list_QnA = list_qna;
			mContext = context;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#addAll(java.util.Collection)
		 */
		@Override
		public void addAll(Collection<? extends MyQnA> collection) {
			// TODO Auto-generated method stub
			list_QnA.addAll(collection);
			Log.e("seokangseok", "addAll "+Integer.toString(collection.size()));
			this.notifyDataSetChanged();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final MyQnA feed = list_QnA.get(position);

			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.itemlist_feed_faq2, null);
			}

			if(convertView.getTag()==null){
				holder = new ViewHoder();
			}else{
				holder = (ViewHoder)convertView.getTag();
			}
			// holder.text_title=
			// (TextView)convertView.findViewById(R.id.tv_feed_study_title);
			holder.ll_faq_question = (LinearLayout) convertView
					.findViewById(R.id.ll_faq_question);
			holder.ll_faq_answer = (LinearLayout) convertView
					.findViewById(R.id.ll_faq_answer);
			holder.tv_question = (TextView) convertView
					.findViewById(R.id.tv_feed_faq_title);
			holder.tv_answer = (TextView) convertView
					.findViewById(R.id.tv_feed_faq_contents);
			holder.iv_answer = (ImageView) convertView
					.findViewById(R.id.iv_faq_question);
			holder.iv_direction = (ImageView) convertView.findViewById(R.id.direction);

			holder.tv_feed_faq_date = (TextView) convertView.findViewById(R.id.tv_feed_faq_date);

			holder.tv_question.setEllipsize(TruncateAt.END);
			holder.tv_question.setMaxLines(2);
			holder.tv_question.setText(feed.getQuestion());

			String qDate = feed.getQdate();

			holder.tv_feed_faq_date.setText(qDate.substring(0, 4) + "/" + qDate.substring(5, 7) + "/" + qDate.substring(8, 10));

			if (feed.getAflag().equals("y")) {
				holder.ll_faq_answer.setVisibility(View.GONE);
				holder.tv_answer.setText(feed.getAnswer());
			} else {
				holder.ll_faq_answer.setVisibility(View.GONE);
			}


			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (holder.tv_question.getMaxLines() == 2) {
						holder.tv_question.setEllipsize(null);
						holder.tv_question.setMaxLines(100);
						holder.iv_direction.setImageResource(R.drawable.icon_faqsettingpage_pressicon_normal_down);
						FlurryAgent.logEvent("QnaListActivity:Click_Show_QnaItem");
					} else {
						holder.tv_question.setEllipsize(TruncateAt.END);
						holder.tv_question.setMaxLines(2);
						holder.iv_direction.setImageResource(R.drawable.icon_faqsettingpage_pressicon_normal);
						FlurryAgent.logEvent("QnaListActivity:Click_Close_QnaItem");
					}

					if (feed.getAflag().equals("y")) {
						if (holder.ll_faq_answer.getVisibility() == View.GONE) {
							holder.ll_faq_answer.setVisibility(View.VISIBLE);
						}
						else {
							holder.ll_faq_answer.setVisibility(View.GONE);
						}
					}
				}
			});

//			holder.ll_faq_answer.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if (feed.getAflag().equals("y")) {
//						if (holder.tv_answer.getMaxLines() == 2) {
//							FlurryAgent.logEvent(
//									"SideActivity_QnAListFragment:OpenQnA",
//									true);
//							holder.tv_answer.setEllipsize(null);
//							holder.tv_answer.setMaxLines(100);
//						} else {
//							FlurryAgent
//									.endTimedEvent("SideActivity_QnAListFragment:OpenQnA");
//							FlurryAgent
//									.logEvent("SideActivity_QnAListFragment:CloseQnA");
//							holder.tv_answer.setEllipsize(TruncateAt.END);
//							holder.tv_answer.setMaxLines(2);
//						}
//					}
//				}
//			});

			convertView.setTag(holder);

			return convertView;
		}
	}



	class ViewHoder {
		LinearLayout ll_faq_question;
		LinearLayout ll_faq_answer;
		TextView tv_question;
		TextView tv_answer;
		ImageView iv_answer;
		ImageView iv_question;
		TextView tv_feed_faq_date;
		ImageView iv_direction;
	}


	void setActionBar() {
		mActionBar = getActionBar();
		mCustomActionBarView = getLayoutInflater().inflate(R.layout.setting_action_bar, null);
		mActionBar.setCustomView(mCustomActionBarView, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		mBack_Layout = (LinearLayout)mCustomActionBarView.findViewById(R.id.layout_actionbar_back);
		mBack_Layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FlurryAgent.logEvent("QnaListActivity:Click_Close_BackButton");
				QnAListActivity.this.finish();
			}
		});

		mTitle_TextView = (TextView)mCustomActionBarView.findViewById(R.id.text_actionbar_title);
		mTitle_TextView.setText("1:1 문의");
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
		FlurryAgent.logEvent("QnAListActivity", articleParams);
	}

	public void onStop()
	{
		super.onStop();
		FlurryAgent.endTimedEvent("QnAListActivity");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
	}
}
