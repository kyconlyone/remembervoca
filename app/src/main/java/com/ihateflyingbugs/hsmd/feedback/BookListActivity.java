package com.ihateflyingbugs.hsmd.feedback;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.BookAdapter;
import com.ihateflyingbugs.hsmd.BookItem;
import com.ihateflyingbugs.hsmd.BookListHeader;
import com.ihateflyingbugs.hsmd.BookListItem;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.model.StudyInfoData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class BookListActivity extends Activity {
	String TAG = "BookListActivity";

	InputStream is;

	//
	ListView mBook_ListView;


	String[] lista;

	String[] cat1 = new String[3];
	String[] cat2 = new String[3];
	String[] cat2_sub = new String[3];
	String[] dataName = { "cat1", "cat2", "cat2_sub" };
	BookAdapter adapter;
	Handler handler;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_list);

		setActionBar();
		handler = new Handler();

		//
		mBook_ListView = (ListView) findViewById(R.id.book_list_view);

		//
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		new RetrofitService().getStudyInfoService().retroGetWorkbookList()
				.enqueue(new Callback<StudyInfoData>() {
					@Override
					public void onResponse(Response<StudyInfoData> response, Retrofit retrofit) {
						List<StudyInfoData.WorkBook> workBookList = response.body().getWorkbook_list();

						String year = "";
						final List<BookItem> items = new ArrayList<BookItem>();

						for (int i = workBookList.size() - 1; 0 <= i; i--) {
							if (year.equals(workBookList.get(i).getCat1())) {
							} else {
								year = workBookList.get(i).getCat1();
								int years = Integer.parseInt(year.split("\\(")[0]) + 1;
								items.add(new BookListHeader(years + "학년도"));
							}
							items.add(new BookListItem(workBookList.get(i).getCat2() + " "
									+ workBookList.get(i).getCat2_sub(), workBookList.get(i).getWord_count(),
									workBookList.get(i).getPublished_date().replace("-", ".")));
						}
						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								adapter = new BookAdapter(getApplicationContext(), items);
								mBook_ListView.setAdapter(adapter);
							}
						});
					}

					@Override
					public void onFailure(Throwable t) {
						Log.e(TAG, "onFailure"+ t.toString());
					}
				});

		//		String data = getList();


	}


	public String getList() {
		String result = null;

		try {
			StringBuilder sBuilder = new StringBuilder();
			URL url = new URL(Config.Server_Url+"DBWord/DBWordInput/get_exam_list.php");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			String line = null;
			if (conn != null) {
				conn.setConnectTimeout(2000);
			}
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStreamReader is = new InputStreamReader(conn.getInputStream());
				BufferedReader br = new BufferedReader(is);
				while (true) {
					line = br.readLine();
					if (line == null) {
						break;
					}
					result = line;
				}
				br.close();
				conn.disconnect();
			}

		} catch (Exception e) {
		}
		return result;
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
				BookListActivity.this.finish();
			}
		});

		mTitle_TextView = (TextView)mCustomActionBarView.findViewById(R.id.text_actionbar_title);
		mTitle_TextView.setText("출제 문제집 리스트");
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
		FlurryAgent.logEvent("BookListActivity", articleParams);
	}

	public void onStop()
	{
		super.onStop();
		FlurryAgent.endTimedEvent("BookListActivity");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		FlurryAgent.onEndSession(this);
	}
}
