package com.ihateflyingbugs.hsmd.feedback;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.ihateflyingbugs.hsmd.QnAListActivity;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.FAQ;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class FAQActivity extends Activity{

	private Activity thisActivity;

	private Handler handler;

	Context mContext;
	DBPool db;
	List<FAQ> list_faq;
	FAQAdapter adapter;


	//
	ActionBar mActionBar;
	View mCustomActionBarView;

	LinearLayout mBack_Layout;
	TextView mTitle_TextView;
	//

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_faq_list);


		setActionBar();


		ListView lv = (ListView)findViewById(R.id.faq_listView);

		Button bt_call_qna = (Button)findViewById(R.id.bt_call_qna);
		Button bt_send_email  = (Button)findViewById(R.id.bt_send_email);

		bt_call_qna.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FAQActivity.this, QnAListActivity.class);
				startActivity(intent);
				finish();
				FlurryAgent.logEvent("FAQActivity:Click_Show_QnaList");
			}
		});

		bt_send_email.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:01071017935" ));
				startActivity(intent);
				FlurryAgent.logEvent("FAQActivity:Click_Show_QnaCall");
			}
		});

		mContext = getApplicationContext();

		handler = new Handler();

		list_faq = new ArrayList<FAQ>();

		adapter = new FAQAdapter(mContext, R.layout.itemlist_feed_faq, list_faq);
		lv.setAdapter(adapter);
		lv.setClickable(false);


		new RetrofitService().getBoardService().retroGetFAQBoard().enqueue(new Callback<List<FAQ>>() {
			@Override
			public void onResponse(Response<List<FAQ>> response, Retrofit retrofit) {
				final List<FAQ> faq = response.body();

				Log.e("faq_log", "faq :" + faq.get(0).getTitle());
				handler.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						list_faq.addAll(faq);
						adapter.notifyDataSetChanged();

					}
				});
			}

			@Override
			public void onFailure(Throwable t) {

				Log.e("faq_log", "faq fail" + t.toString());
			}
		});

	}







	public class FAQAdapter extends ArrayAdapter<FAQ>{
		List<FAQ> list_FAQ;
		private LayoutInflater mInflater;

		public FAQAdapter(Context context, int resourceId, List<FAQ> list_faq){
			super(context, resourceId, list_faq);
			mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.list_FAQ = list_faq;
			mContext= context;
		}



		/* (non-Javadoc)
		 * @see android.widget.ArrayAdapter#addAll(java.util.Collection)
		 */
		@Override
		public void addAll(Collection<? extends FAQ> collection) {
			// TODO Auto-generated method stub
			list_FAQ.addAll(collection);
			this.notifyDataSetChanged();
		}



		/* (non-Javadoc)
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			int type = getItemViewType(position);
			final FAQ faq = list_FAQ.get(position);
			final ViewHoder holder = new ViewHoder();
			System.out.println("getView " + position + " " + convertView + " type = " + type);


			convertView = mInflater.inflate(R.layout.itemlist_feed_faq, null);
			//holder.text_title= (TextView)convertView.findViewById(R.id.tv_feed_study_title);

			holder.ll_faq_question = (LinearLayout)convertView.findViewById(R.id.ll_faq_question);
			holder.ll_faq_answer = (LinearLayout)convertView.findViewById(R.id.ll_faq_answer);
			holder.tv_question = (TextView)convertView.findViewById(R.id.tv_feed_faq_title);
			holder.tv_answer = (TextView)convertView.findViewById(R.id.tv_feed_faq_contents);
			holder.iv_answer = (ImageView)convertView.findViewById(R.id.iv_faq_question);

			holder.tv_question.setText(faq.getQuestion());
			holder.iv_direction = (ImageView)convertView.findViewById(R.id.direction);
			holder.tv_answer.setText(faq.getAnswer());

			holder.tv_answer.setEllipsize(TruncateAt.END);
			holder.tv_answer.setMaxLines(2);

			holder.ll_faq_question.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});

			holder.ll_faq_answer.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(!holder.isOpen){
						FlurryAgent.logEvent("FAQActivity:Click_on_FAQItem", true);
						holder.isOpen = true;
						holder.tv_answer.setEllipsize(TruncateAt.MARQUEE);
						holder.tv_answer.setMaxLines(500);
						holder.iv_direction.setImageResource(R.drawable.icon_faqsettingpage_pressicon_normal_down);
					}else{
						FlurryAgent.endTimedEvent("FAQActivity:Click_Off_FAQItem");
						FlurryAgent.logEvent("SideActivity_FAQFragment:CloseFAQ", true);
						holder.tv_answer.setEllipsize(TruncateAt.END);
						holder.tv_answer.setMaxLines(2);
						holder.iv_direction.setImageResource(R.drawable.icon_faqsettingpage_pressicon_normal);
						holder.isOpen = false;
					}
				}
			});


			//			holder.tv_question.setText(""+faq.getQdate().substring(0, 4)+"."+faq.getQdate().substring(4, 6));
			//			holder.tv_answer.setText(""+faq.getAdate().substring(6, faq.getAdate().length()));

			convertView.setTag(holder);

			return convertView;
		}

	}

	class ViewHoder{
		LinearLayout ll_faq_question;
		LinearLayout ll_faq_answer;
		TextView tv_question;
		TextView tv_answer;
		ImageView iv_answer;
		ImageView iv_question;
		ImageView iv_direction;
		boolean isOpen = false;
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
				FlurryAgent.logEvent("FAQActivity:Click_Close_BackButton");
				FAQActivity.this.finish();
			}
		});

		mTitle_TextView = (TextView)mCustomActionBarView.findViewById(R.id.text_actionbar_title);
		mTitle_TextView.setText("FAQ");
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
		FlurryAgent.logEvent("FAQActivity", articleParams);
	}

	public void onStop()
	{
		super.onStop();
		FlurryAgent.endTimedEvent("FAQActivity");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
	}
}
