package com.icetraveller.android.apps.cometpark.ui;

import java.io.IOException;
import java.util.ArrayList;

import android.app.SearchManager;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
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
import android.widget.SearchView;

import com.icetraveller.android.apps.cometpark.R;
import com.icetraveller.android.apps.cometpark.io.JSONHandler;
import com.icetraveller.android.apps.cometpark.io.LotsHandler;
import com.icetraveller.android.apps.cometpark.io.SpotsHandler;

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

		setContentView(R.layout.activity_home);
		FragmentManager fm = getSupportFragmentManager();

		mViewPager = (ViewPager) findViewById(R.id.pager);

		String homeScreenLabel;
		if (mViewPager != null) {
			// Phone setup
			mViewPager.setAdapter(new HomePagerAdapter(
					getSupportFragmentManager()));
			mViewPager.setOnPageChangeListener(this);
			 mViewPager.setPageMarginDrawable(R.drawable.grey_border_inset_lr);
			 mViewPager.setPageMargin(getResources().getDimensionPixelSize(
			 R.dimen.page_margin_width));

			final ActionBar actionBar = getSupportActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			
			actionBar.addTab(actionBar.newTab().setText(R.string.title_rank)
					.setTabListener(this));
			
			setHasTabs();

			if (getIntent() != null
					&& TAB_LOTS.equals(getIntent().getStringExtra(
							EXTRA_DEFAULT_TAB)) && savedInstanceState == null) {
				mViewPager.setCurrentItem(1);
			}
			
			

			homeScreenLabel = getString(R.string.title_rank);

			getSupportActionBar().setHomeButtonEnabled(false);


		}else{
			//wowow
			homeScreenLabel = "ooh";
		}
		getSupportActionBar().setHomeButtonEnabled(false);
		LOGD("Tracker", homeScreenLabel);
		
		// Sync data on load
        if (savedInstanceState == null) {
//            registerGCMClient();TODO
        }
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
//		final ContentResolver resolver = getContentResolver();
//        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
//        try {
////        	SpotsHandler spotsHandler = new SpotsHandler(this); 
////        	String s = JSONHandler.parseResource(this, R.raw.request_spots_example);
////        	ArrayList sa =spotsHandler.parse(s);
////			batch.addAll(sa);
//        	batch.addAll(new LotsHandler(this).parse(
//                    JSONHandler.parseResource(this, R.raw.request_lots_exmaple)));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mGCMRegisterTask != null) {
//            mGCMRegisterTask.cancel(true);
//        }
//
//        try {
//            GCMRegistrar.onDestroy(this);
//        } catch (Exception e) {
//            LOGW(TAG, "C2DM unregistration error", e);
//        }
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

	private class HomePagerAdapter extends FragmentPagerAdapter {
		public HomePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				 return new RankFragment();
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
		getMenuInflater().inflate(R.menu.home, menu);
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
