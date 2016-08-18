package com.caldroidsample.day;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ihateflyingbugs.hsmd.R;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.roomorama.caldroid.CalendarHelper;

import java.util.Date;

import hirondelle.date4j.DateTime;

public class CaldroidDayFragment extends Fragment {

	private DayFragmentAdapter mAdapter;
	private ViewPager mPager;

	private CaldroidFragment caldroidFragment;

	public int currentPage = 1;

	private ProgressDialog mProgress;

	boolean loadingFinished = true;

	boolean isStart = false;

	private Date date = new Date();

	private boolean isFinish;

	public void setCaldoridFragment(CaldroidFragment caldroid) {
		caldroidFragment = caldroid;
		Log.d("Fragment", "Create");
	}

	public DayFragmentAdapter getAdapter() {
		return mAdapter;
	}

	public DateTime getCurrentDay() {
		DayFragmentAdapter tempAdapter = (DayFragmentAdapter) mPager.getAdapter();
		return tempAdapter.getDateTime();
	}

	public void setDays(DateTime dateTime) {
		DayFragmentAdapter tempAdapter = (DayFragmentAdapter) mPager.getAdapter();
		tempAdapter.setDays(dateTime);
		Log.d("Date", "SetDays " + dateTime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isFinish = false;

		mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			DayFragmentAdapter tempAdapter = (DayFragmentAdapter) mPager.getAdapter();
			CaldroidGridAdapter adapter = caldroidFragment.getDatePagerAdapters().get(0);
			DateTime currentDay;

			@Override
			public void onPageSelected(int arg0) {

				// currentPage = 1;

				try {
					switch (arg0) {
						case 0:
							handler.postDelayed(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									mPager.setCurrentItem(1, false);
								}
							}, 200);
							currentDay = adapter.getCurrentDay().minusDays(1);
							Log.d("Date", "123123 prev" + adapter.getCurrentDay() + ", " + currentDay);
							Date prevDate = CalendarHelper.convertDateTimeToDate(currentDay);
							Log.d("Date", prevDate.toString());
							caldroidFragment.getCaldroidListener().onSelectDate(prevDate, getView(), 2);
							caldroidFragment.refreshView();
							tempAdapter.refreshAdapter(adapter.getCurrentDay());
							break;
						case 1:
							break;
						case 2:
							handler.postDelayed(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									mPager.setCurrentItem(1, false);
								}
							}, 200);
							Log.d("Date", "123123 next" + adapter.getCurrentDay() + ", " + currentDay);
							currentDay = adapter.getCurrentDay().plusDays(1);
							Date nextDate = CalendarHelper.convertDateTimeToDate(currentDay);
							caldroidFragment.getCaldroidListener().onSelectDate(nextDate, getView(), 2);
							caldroidFragment.refreshView();
							tempAdapter.refreshAdapter(adapter.getCurrentDay());
							break;
						default:
							break;
					}
				} catch (NumberFormatException e) {
					// TODO: handle exception
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				mPager.getParent().requestDisallowInterceptTouchEvent(true);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	Handler handler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.day_fragment, container, false);

		mPager = (ViewPager) view.findViewById(R.id.infinite_pager);
		mAdapter = new DayFragmentAdapter(getChildFragmentManager());
		mPager.setAdapter(mAdapter);
		// activity = this;
		// mContext = getApplicationContext();


		mPager.setCurrentItem(1);

		handler = new Handler();

		return view;
	}

	private void initialize() {
		InitializationRunnable init = new InitializationRunnable();
		new Thread(init).start();
	}

	class InitializationRunnable implements Runnable {
		public void run() {

		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();
	}

	public void onPageFinished() {

		if (loadingFinished) {
			if (null != mProgress) {
				if (mProgress.isShowing()) {
					mProgress.dismiss();
				}
			}
		}
	}

	public ViewPager getMPager() {
		// TODO Auto-generated method stub
		return mPager;
	}

	public void setCaldroidListener(OnPageChangeListener caldroidDayListener) {
		mPager.setOnPageChangeListener(caldroidDayListener);
	}

}