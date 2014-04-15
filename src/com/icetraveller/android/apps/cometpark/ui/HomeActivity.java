package com.icetraveller.android.apps.cometpark.ui;

import java.io.IOException;
import java.util.ArrayList;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.OperationApplicationException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.icetraveller.android.apps.cometpark.R;
import com.icetraveller.android.apps.cometpark.gcm.ServerUtilities;
import com.icetraveller.android.apps.cometpark.io.JSONHandler;
import com.icetraveller.android.apps.cometpark.io.LotsHandler;
import com.icetraveller.android.apps.cometpark.io.SpotsHandler;
import com.icetraveller.android.apps.cometpark.provider.CometParkContract;
import com.icetraveller.android.apps.cometpark.sync.SyncHelper;
import com.icetraveller.android.apps.cometpark.sync.SyncProcessor;
import com.icetraveller.android.apps.cometpark.utils.App;

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
	public static final String TAB_LOTS = "parking_lots";
	public static final String EXTRA_DEFAULT_TAB = "com.icetraveller.apps.cometpark.extra.DEFAULT_TAB";
	AsyncTask<Void, Void, Void> mRegisterTask;

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

		} else {
			// wowow
			homeScreenLabel = "ooh";
		}
		getSupportActionBar().setHomeButtonEnabled(false);
		LOGD("Tracker", homeScreenLabel);

		// Sync data on load
		if (savedInstanceState == null) {
			registerGCMClient();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		SyncProcessor process = new SyncProcessor(this);
		process.execute(SyncHelper.FLAG_SYNC_LOCAL|SyncHelper.FLAG_SYNC_REMOTE);
	}

	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		GCMRegistrar.onDestroy(this);
		super.onDestroy();
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
			startActivity(new Intent(this, SettingsActivity.class));
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	private void registerGCMClient() {
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		Log.d(TAG, "regId:" + regId);
		if (regId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(this, App.SENDER_ID);
		} else {
			// Device is already registered on GCM, check server.
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						ServerUtilities.register(context, regId);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}
	}


}
