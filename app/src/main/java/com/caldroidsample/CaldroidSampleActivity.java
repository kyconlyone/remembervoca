package com.caldroidsample;

import hirondelle.date4j.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.caldroidsample.day.CaldroidDayFragment;
import com.ihateflyingbugs.hsmd.R;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.roomorama.caldroid.CaldroidListener;


@SuppressLint("SimpleDateFormat")
public class CaldroidSampleActivity extends FragmentActivity {
	private boolean undo = false;
	private CaldroidFragment caldroidFragment;
	private CaldroidDayFragment caldroidDayFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_feedback);

		final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");

		ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();

		caldroidFragment = new CaldroidFragment();
		caldroidDayFragment = new CaldroidDayFragment();
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction t = fragmentManager.beginTransaction();
		t.replace(R.id.calendar1, caldroidFragment);
		t.replace(R.id.day1, caldroidDayFragment);
		t.commit();
		fragmentManager.executePendingTransactions();

		caldroidDayFragment.setCaldoridFragment(caldroidFragment);

		final CaldroidListener caldroidListener = new CaldroidListener() {
			public CaldroidGridAdapter adapter;

			protected void getAdapter() {
				adapter = caldroidFragment.getDatePagerAdapters().get(0);
			}

			@Override
			public void onSelectDate(Date date, View view, int type) {

				Log.d("month", "123123 date" + date);

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);

				DateTime currentDay = adapter.getCurrentDay();

				int currentTimeMillis = (currentDay.getYear() * 12)
						+ currentDay.getMonth();
				int nextTimeMillis = calendar.get(Calendar.YEAR) * 12
						+ calendar.get(Calendar.MONTH) + 1;
				Log.d("timezone", "current " + currentTimeMillis);
				Log.d("timezone", "next " + nextTimeMillis);

				if (currentTimeMillis == nextTimeMillis) {
					adapter.setCurrentDay(date);
				} else if (currentTimeMillis > nextTimeMillis) {
					adapter.setCurrentDay(date);
					if (type == 0 || type == 2) {
						caldroidFragment.prevMonth();
					}
				} else {
					adapter.setCurrentDay(date);
					if (type == 0 || type == 2) {
						caldroidFragment.nextMonth();
					}
				}
				caldroidDayFragment.setDays(adapter.getCurrentDay());
				caldroidFragment.refreshView();
			}

			@Override
			public void onChangeMonth(int month) {
				Log.d("month", "123123 month" + month);

			}

			@Override
			public void onLongClickDate(Date date, View view) {
				Log.d("month", "123123 longclick" + date);
			}

			@Override
			public void onCaldroidViewCreated() {
				Log.d("month", "123123 created");
				getAdapter();
			}

		};

		caldroidFragment.setCaldroidListener(caldroidListener);

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	/**
	 * Save current states of the Caldroid here
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);

		if (caldroidFragment != null) {
			caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
		}
	}

}