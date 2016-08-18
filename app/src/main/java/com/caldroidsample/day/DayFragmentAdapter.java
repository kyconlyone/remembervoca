package com.caldroidsample.day;

import android.support.v4.app.FragmentPagerAdapter;

import com.ihateflyingbugs.hsmd.data.MildangDate;
import com.roomorama.caldroid.CalendarHelper;

import java.util.Date;

import hirondelle.date4j.DateTime;

public class DayFragmentAdapter extends FragmentPagerAdapter {

	private DayFragment[] contents;

	public DayFragmentAdapter(android.support.v4.app.FragmentManager fm) {
		super(fm);
		// Calendar cal = Calendar.getInstance();
		Date date = new Date();
		DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
		contents = new DayFragment[3];

		DateTime days[] = new DateTime[3];

		MildangDate mildangDate = new MildangDate();

		days[0] = dateTime.minusDays(1);
		days[1] = dateTime;
		days[2] = dateTime.plusDays(1);

		for (int i = 0; i < contents.length; i++) {
			contents[i] = new DayFragment(days[i]);
		}
		notifyDataSetChanged();

	}


	public void refreshAdapter(DateTime dateTime){
		DateTime days[] = new DateTime[3];
		days[0] = dateTime.minusDays(1);
		days[1] = dateTime;
		days[2] = dateTime.plusDays(1);

		for (int i = 0; i < contents.length; i++) {
			contents[i].setDays(days[i]);
		}
	}

	public void setDays(DateTime dateTime){
		DateTime tempDateTime[] = new DateTime[3];

		tempDateTime[1] = dateTime;
		tempDateTime[0] = dateTime.minusDays(1);
		tempDateTime[2] = dateTime.plusDays(1);

		for (int i = 0; i < contents.length; i++) {
			contents[i].setDays(tempDateTime[i]);
		}

		notifyDataSetChanged();
	}

	public DateTime getDateTime(){
		return contents[1].getDateTime();
	}

	@Override
	public DayFragment getItem(int position) {
		return contents[position];
	}

	@Override
	public int getCount() {
		return contents.length;
	}

}