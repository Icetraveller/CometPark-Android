package com.icetraveller.android.apps.cometpark.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.view.Menu;
import android.view.MenuItem;

import com.icetraveller.android.apps.cometpark.R;
import static com.icetraveller.android.apps.cometpark.utils.LogUtils.*;

/**
 * The description will be rework later.
 * 
 * TODO Shall check if it's user first time entering. Check preference. - if it
 * is. Then go to setup activity. - else shall be the rank list of parking lot.
 * witch is load fragments of Map and rank list.
 * 
 * @author yue
 * 
 */
public class HomeActivity extends BaseActivity implements
		ActionBar.TabListener, ViewPager.OnPageChangeListener {
	private static final String TAG = makeLogTag(HomeActivity.class);
	private ViewPager mViewPager;
	private Menu mOptionsMenu;
	public static final String TAB_LOTS = "parking_lots";
	public static final String EXTRA_DEFAULT_TAB = "com.icetraveller.apps.cometpark.extra.DEFAULT_TAB";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isFinishing()) {
			return;
		}

		setContentView(R.layout.activity_main);

		FragmentManager fm = getSupportFragmentManager();
		mViewPager = (ViewPager) findViewById(R.id.pager);
		String mainScreenLabel;

		if (mViewPager != null) {
			// Phone setup
			mViewPager.setAdapter(new MainPagerAdapter(
					getSupportFragmentManager()));
			mViewPager.setOnPageChangeListener(this);
			// mViewPager.setPageMarginDrawable(R.drawable.grey_border_inset_lr);
			// mViewPager.setPageMargin(getResources().getDimensionPixelSize(
			// R.dimen.page_margin_width));

			final ActionBar actionBar = getSupportActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			actionBar.addTab(actionBar.newTab().setText(R.string.title_rank)
					.setTabListener(this));

			if (getIntent() != null
					&& TAB_LOTS.equals(getIntent().getStringExtra(
							EXTRA_DEFAULT_TAB)) && savedInstanceState == null) {
				mViewPager.setCurrentItem(1);
			}

			mainScreenLabel = getString(R.string.title_rank);

			getSupportActionBar().setHomeButtonEnabled(false);

			// Sync data on load
			if (savedInstanceState == null) {
				// TODO
				// registerGCMClient();
			}

		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {
		getSupportActionBar().setSelectedNavigationItem(position);

		int titleId = -1;
		switch (position) {
		case 0:
			titleId = R.string.title_rank;
			break;
		}
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		mViewPager.setCurrentItem(tab.getPosition());

	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
	}

	private class MainPagerAdapter extends FragmentPagerAdapter {
		public MainPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
//				return new RankFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			return 1;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		mOptionsMenu = menu;
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh: 
			// TODO triggerRefresh();
			return true;

		case R.id.menu_about:
			// TODO HelpUtils.showAbout(this);
			return true;

		case R.id.menu_settings:
			// TODO startActivity(new Intent(this, SettingsActivity.class));
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

}
