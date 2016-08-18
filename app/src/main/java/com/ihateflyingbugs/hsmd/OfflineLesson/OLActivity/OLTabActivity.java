package com.ihateflyingbugs.hsmd.OfflineLesson.OLActivity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.OfflineLesson.OLFragment.OriginWordFragment;
import com.ihateflyingbugs.hsmd.OfflineLesson.OLFragment.SampleTextFragment;
import com.ihateflyingbugs.hsmd.OfflineLesson.OLFragment.YoutubeFragment;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.WordListFragment;
import com.viewpagerindicator.TabPageIndicator;

public class OLTabActivity extends FragmentActivity {
    private static final String[] CONTENT = new String[] { /*"의미나무", "어 원",*/ "기출지문"/*, "예문", "시험결과"*/};
    ScreenSlidePagerAdapter adapter;
    private ActionBar mActionBar;
    View mCustomActionBarView;

    LinearLayout mActionBar_Layout;
    LinearLayout mBack_Layout;
    TextView mTitle_TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ol_main);

        adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

        final ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);
        setActionBar();


        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }

    class GoogleMusicAdapter extends FragmentPagerAdapter {
        public GoogleMusicAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position){
                case 0:
                    fragment=  new YoutubeFragment();
                case 2:
                    fragment= SampleTextFragment.newInstance();
                default:
                    fragment= SampleTextFragment.newInstance();
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            Fragment fragment;
            switch (position){
                case 0:
                    fragment=  new SampleTextFragment();
                    break;
                case 1:
                    fragment = new OriginWordFragment();
                    break;
                case 2:
                    fragment= new SampleTextFragment();
                    break;
                default:
                    fragment= new SampleTextFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
    }

    void setActionBar() {
        mActionBar = getActionBar();
        mCustomActionBarView = getLayoutInflater().inflate(R.layout.setting_action_bar, null);
        mActionBar.setCustomView(mCustomActionBarView, new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        mActionBar_Layout = (LinearLayout)mCustomActionBarView.findViewById(R.id.layout_actionbar);

        mBack_Layout = (LinearLayout)mCustomActionBarView.findViewById(R.id.layout_actionbar_back);
        mBack_Layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FlurryAgent.logEvent("OLTabActivity:Click_Close_BackButton");
                finish();
            }
        });

        mTitle_TextView = (TextView)mCustomActionBarView.findViewById(R.id.text_actionbar_title);
        mTitle_TextView.setText("Offline Lesson");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WordListFragment.clearWordList();

    }
}
