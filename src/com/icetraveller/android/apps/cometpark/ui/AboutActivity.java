package com.icetraveller.android.apps.cometpark.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import com.icetraveller.android.apps.cometpark.R;
import com.icetraveller.android.apps.cometpark.utils.LogUtils;

public class AboutActivity extends BaseActivity {

	private static final String TAG = LogUtils.makeLogTag(AboutActivity.class);

	private Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		setActionBarTrackIcon(1);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


}
