package com.ihateflyingbugs.hsmd;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.data.Date;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UseFragment extends Fragment{

	Context mContext;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_use, container, false);
		mContext = getActivity().getApplicationContext();
		String ues_contents;
		String uesrinfo_contents;
		try {
			InputStream is = getActivity().getAssets().open("private.txt");

			// We guarantee that the available method returns the total
			// size of the asset...  of course, this does mean that a single
			// asset can't be more than 2 gigs.
			int size = is.available();

			// Read the entire asset into a local byte buffer.
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();

			// Convert the buffer into a string.
			ues_contents = new String(buffer);

		} catch (IOException e) {
			// Should never happen!
			throw new RuntimeException(e);
		}

		try {
			InputStream is = getActivity().getAssets().open("use.txt");

			// We guarantee that the available method returns the total
			// size of the asset...  of course, this does mean that a single
			// asset can't be more than 2 gigs.
			int size = is.available();

			// Read the entire asset into a local byte buffer.
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();

			// Convert the buffer into a string.
			uesrinfo_contents = new String(buffer);

		} catch (IOException e) {
			// Should never happen!
			throw new RuntimeException(e);
		}

		TextView tv_use_law = (TextView)view.findViewById(R.id.tv_use_law);
		TextView tv_userinfo_law = (TextView)view.findViewById(R.id.tv_userinfo_law);

		tv_use_law.setText(ues_contents);


		tv_userinfo_law.setText(uesrinfo_contents);

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
		FlurryAgent.logEvent("SideActivity_UserAgreementFragment", articleParams);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 시작,FinishStudyFragment,1]\r\n");

	}

	public void onStop()
	{
		super.onStop();
		//FlurryAgent.endTimedEvent("UserAgreementFragment:Start");
		articleParams.put("Start", startdate);
		articleParams.put("End", date.get_currentTime());
		Log.e("splash", startdate+"        "+date.get_currentTime());
		articleParams.put("Duration", ""+((Long.valueOf(System.currentTimeMillis())-Long.valueOf(starttime)))/1000);
		// your code
		//	MainActivity.writeLog("[모든단어 암기완료 끝,FinishStudyFragment,1,{Start:"+articleParams.get("Start")+",End:"+articleParams.get("End")+"}]\r\n");
	}
}
