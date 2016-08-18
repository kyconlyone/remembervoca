package com.ihateflyingbugs.hsmd.indicator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.viewpagerindicator.IconPagerAdapter;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.VOCAconfig;
import com.ihateflyingbugs.hsmd.data.MainValue;

class TestFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
	
    protected static final String[] CONTENT 
    = new String[] { String.valueOf(R.layout.item_tutorial1),
    	String.valueOf(R.layout.item_tutorial2),String.valueOf(R.layout.item_tutorial3),String.valueOf(R.layout.item_tutorial4)};
   
    protected static final String[] CONTENT_SMALL 
    = new String[] { String.valueOf(R.layout.item_tutorial1_small),
    	String.valueOf(R.layout.item_tutorial2_small),String.valueOf(R.layout.item_tutorial3_small),String.valueOf(R.layout.item_tutorial4_small)};
    

    private int mCount = CONTENT.length;

    public TestFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
    	if(MainValue.isLowDensity(VOCAconfig.context)){
            return TestFragment.newInstance(CONTENT_SMALL[position % CONTENT.length]);
		}else{
	        return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
		}
        
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return TestFragmentAdapter.CONTENT[position % CONTENT.length];
    }

    
    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }

	@Override
	public int getIconResId(int index) {
		// TODO Auto-generated method stub
		return 0;
	}
}