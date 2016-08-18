package com.caldroidsample;

import com.ihateflyingbugs.hsmd.VOCAconfig;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

public class CaldroidSampleCustomFragment extends CaldroidFragment {

	@Override
	public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
		// TODO Auto-generated method stub


		return new CaldroidSampleCustomAdapter(VOCAconfig.context, month, year,
				getCaldroidData(), extraData);
	}
}