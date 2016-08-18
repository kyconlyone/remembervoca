package com.ihateflyingbugs.hsmd.indicator;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

public class FriendFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
	
	List<FriendFragment> list;
	
	public FriendFragmentAdapter(FragmentManager fm, List<FriendFragment> list) {
		super(fm);
		this.list = list; 
	}

	@Override
	public Fragment getItem(int position) {
		return list.get(position % list.size());
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return ""+position;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public int getIconResId(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

}