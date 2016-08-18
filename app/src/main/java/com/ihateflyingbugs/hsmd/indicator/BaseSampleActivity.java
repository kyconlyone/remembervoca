package com.ihateflyingbugs.hsmd.indicator;

import java.util.Random;

import com.viewpagerindicator.PageIndicator;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Config;

public abstract class BaseSampleActivity extends FragmentActivity {

    TestFragmentAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;

	
}
