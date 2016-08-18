package com.ihateflyingbugs.hsmd;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MyQnA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QnAListFragment extends Fragment implements OnClickListener{


	private Activity thisActivity;

	private static Handler handler;
	static QnaAdapter adapter;

	Context mContext;
	Button bt_make_qna;
	static List<MyQnA> list_qna;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		Log.e("KARAM", "     QnAListFragment !!!!!!   KARAMKARAM");

		thisActivity = getActivity();
		mContext = getActivity().getApplicationContext();
		View view = inflater.inflate(R.layout.fragment_qna_list, container, false);
		ListView lv = (ListView)view.findViewById(R.id.qna_listView);
		bt_make_qna = (Button)view.findViewById(R.id.bt_make_qna);
		bt_make_qna.setOnClickListener(this);
		list_qna = new ArrayList<MyQnA>();
		adapter = new QnaAdapter(mContext, R.layout.itemlist_feed_faq, list_qna);
		lv.setAdapter(adapter);
		handler= new Handler();
		Log.e("success", "Async_get_indiv_anwer  start");

		DBPool db = DBPool.getInstance(getActivity().getApplicationContext());

//		new Async_get_indiv_anwer(db.getStudentId(), new VocaCallback() {
//
//			@Override
//			public void Resonponse(JSONObject response) {
//				// TODO Auto-generated method stub
//				Log.e("success", "Async_get_indiv_anwer   "+response.toString());
//				donwloadIndivQues(response);
//			}
//
//			@Override
//			public void Exception() {
//				// TODO Auto-generated method stub
//				Log.e("success", "Async_get_indiv_anwer : Exception");
//
//			}
//		}).execute();

		lv.setClickable(false);

		return view;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.bt_make_qna:
			/*FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
			fragmentTransaction.replace(R.id.linearFragment, new QnAFragment()).addToBackStack(null).commit();
			thisActivity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);*/
				break;

			default:
				break;
		}
	}

	public class QnaAdapter extends ArrayAdapter<MyQnA>{
		List<MyQnA> list_QnA;
		private LayoutInflater mInflater;

		public QnaAdapter(Context context, int resourceId, List<MyQnA> list_qna){
			super(context, resourceId, list_qna);
			mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.list_QnA = new ArrayList<MyQnA>();
			mContext= context;
		}


		/* (non-Javadoc)
		 * @see android.widget.ArrayAdapter#addAll(java.util.Collection)
		 */
		@Override
		public void addAll(Collection<? extends MyQnA> collection) {
			// TODO Auto-generated method stub
			list_QnA.addAll(collection);
			this.notifyDataSetChanged();
		}


		/* (non-Javadoc)
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			int type = getItemViewType(position);
			final MyQnA feed = list_QnA.get(position);
			final ViewHoder holder = new ViewHoder();



			convertView = mInflater.inflate(R.layout.itemlist_feed_faq, null);
			//holder.text_title= (TextView)convertView.findViewById(R.id.tv_feed_study_title);
			holder.ll_faq_question = (LinearLayout)convertView.findViewById(R.id.ll_faq_question);
			holder.ll_faq_answer = (LinearLayout)convertView.findViewById(R.id.ll_faq_answer);
			holder.tv_question = (TextView)convertView.findViewById(R.id.tv_feed_faq_title);
			holder.tv_answer = (TextView)convertView.findViewById(R.id.tv_feed_faq_contents);
			holder.iv_answer = (ImageView)convertView.findViewById(R.id.iv_faq_question);


			holder.tv_question.setText(feed.getQuestion());

			if(feed.getAflag().equals("y")){
				holder.tv_answer.setEllipsize(TruncateAt.END);
				holder.tv_answer.setMaxLines(2);
				holder.tv_answer.setVisibility(View.VISIBLE);
				holder.tv_answer.setText(feed.getAnswer());
			}else{
				holder.ll_faq_answer.setVisibility(View.GONE);
			}

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
					if(feed.getAflag().equals("y")){
						if(holder.tv_answer.getMaxLines()==2){
							FlurryAgent.logEvent("SideActivity_QnAListFragment:OpenQnA", true);
							holder.tv_answer.setEllipsize(null);
							holder.tv_answer.setMaxLines(100);
						}else{
							FlurryAgent.endTimedEvent("SideActivity_QnAListFragment:OpenQnA");
							FlurryAgent.logEvent("SideActivity_QnAListFragment:CloseQnA");
							holder.tv_answer.setEllipsize(TruncateAt.END);
							holder.tv_answer.setMaxLines(2);
						}
					}
				}
			});



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
	}


	public static void donwloadIndivQues(JSONObject json_IndivQues){
		list_qna.clear();
		Log.e("Download indivQues", "Download indivQues");
		try{
			Log.e("success", "Async_get_indiv_anwer  donwloadIndivQues");
			JSONArray num 		= json_IndivQues.getJSONArray("Num");
			JSONArray user_id	= json_IndivQues.getJSONArray("User_ID");
			JSONArray title		= json_IndivQues.getJSONArray("Title");
			JSONArray q_date 	= json_IndivQues.getJSONArray("Q_Date");
			JSONArray question 	= json_IndivQues.getJSONArray("Question");
			JSONArray a_flag	= json_IndivQues.getJSONArray("A_Flag");
			JSONArray a_date 	= json_IndivQues.getJSONArray("A_Date");
			JSONArray answer 	= json_IndivQues.getJSONArray("Answer");

			for(int i=num.length()-1; i>=0; i--){
				MyQnA mq = new MyQnA(num.optInt(i), user_id.optString(i),
						title.optString(i), q_date.optString(i), question.optString(i),a_flag.optString(i));

				if(a_flag.optString(i).equals("y")){	//answered
					Log.e("print test: A_Date", 	"A_Date : " + a_date.optString(i));
					Log.e("print test: Answer",		"Answer : " + answer.optString(i));
					mq.setAnswer(answer.optString(i),a_date.optString(i));
				}else{	//no answered
					Log.e("print test: Ans", "No Answer");
				}
				list_qna.add(mq);
			}
			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					adapter.addAll(list_qna);
				}
			});
		}catch(JSONException e){
			Log.e("success", "Async_get_indiv_anwer  donwloadIndivQues     exception");
			e.printStackTrace();
		}
	}


	String starttime;
	String startdate;
	Date date = new Date();
	Map<String, String> articleParams;

	public void onStart()
	{
		super.onStart();
		articleParams = new HashMap<String, String>();
		startdate = date.get_currentTime();
		starttime = String.valueOf(System.currentTimeMillis());
		FlurryAgent.logEvent("SideActivity_QnAListFragment", articleParams);
		// your code
		//	MainActivity.writeLog("[紐⑤뱺�⑥뼱 �붽린�꾨즺 �쒖옉,FinishStudyFragment,1]\r\n");

	}

	public void onStop()
	{
		super.onStop();
		//FlurryAgent.endTimedEvent("QnAListFragment:Start");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		Log.e("splash", startdate+"        "+date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		// your code
		//	MainActivity.writeLog("[紐⑤뱺�⑥뼱 �붽린�꾨즺 ��FinishStudyFragment,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");
	}

}
