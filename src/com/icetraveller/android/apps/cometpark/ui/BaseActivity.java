package com.icetraveller.android.apps.cometpark.ui;

import com.icetraveller.android.apps.cometpark.R;
import com.icetraveller.android.apps.cometpark.utils.PlayServicesUtils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import static com.icetraveller.android.apps.cometpark.utils.LogUtils.*;

/**
 * A base activity that handles common functionality in the app.
 */
public class BaseActivity extends ActionBarActivity {
	private static final String TAG = makeLogTag(BaseActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if(!){
//			finish();
//		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Verifies the proper version of Google Play Services exists on the
		// device.
//		PlayServicesUtils.checkGooglePlaySevices(this);
	}

	protected void setHasTabs() {
		// currently support phone only
	}

	/**
	 * Sets the icon.
	 */
	protected void setActionBarTrackIcon(int id) {
		if(id == 0){
			getSupportActionBar().setIcon(R.drawable.actionbar_icon);
		}else{
			getSupportActionBar().setIcon(R.drawable.ic_launcher);
		}
		
		getSupportActionBar().setTitle(R.string.app_name);
	}

	/**
	 * Converts an intent into a {@link Bundle} suitable for use as fragment
	 * arguments.
	 */
	protected static Bundle intentToFragmentArguments(Intent intent) {
		Bundle arguments = new Bundle();
		if (intent == null) {
			return arguments;
		}

		final Uri data = intent.getData();
		if (data != null) {
			arguments.putParcelable("_uri", data);
		}

		final Bundle extras = intent.getExtras();
		if (extras != null) {
			arguments.putAll(intent.getExtras());
		}

		return arguments;
	}

	/**
	 * Converts a fragment arguments bundle into an intent.
	 */
	public static Intent fragmentArgumentsToIntent(Bundle arguments) {
		Intent intent = new Intent();
		if (arguments == null) {
			return intent;
		}

		final Uri data = arguments.getParcelable("_uri");
		if (data != null) {
			intent.setData(data);
		}

		intent.putExtras(arguments);
		intent.removeExtra("_uri");
		return intent;
	}

}
