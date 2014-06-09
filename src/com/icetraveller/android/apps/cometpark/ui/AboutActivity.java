package com.icetraveller.android.apps.cometpark.ui;

import com.android.inapppurchase.util.IabHelper;
import com.android.inapppurchase.util.IabResult;
import com.android.inapppurchase.util.Inventory;
import com.android.inapppurchase.util.Purchase;
import com.icetraveller.android.apps.cometpark.R;
import com.icetraveller.android.apps.cometpark.utils.BillUtil;
import com.icetraveller.android.apps.cometpark.utils.LogUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AboutActivity extends Activity {

	private static final String TAG = LogUtils.makeLogTag(AboutActivity.class);

	private Button donateButton;
	private IabHelper billHelper;
	private Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		findUI();
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

	private void findUI() {
		donateButton = (Button) findViewById(R.id.donate);
		donateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startIAB();
			}
		});
	}
	
	private void startIAB() {
		Log.i(TAG, "create IAB Helper");
		billHelper = new IabHelper(this, BillUtil.base64EncodedPublicKey);
		// TODO disable this
		billHelper.enableDebugLogging(true, TAG);

		Log.d(TAG, "Starting setup");
		billHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			@Override
			public void onIabSetupFinished(IabResult result) {
				Log.d(TAG, "Setup finished");
				if (!result.isSuccess()) {
					BillUtil.complain(context, TAG,
							"Problem setting up in-app billing: " + result);
					return;
				}

				if (billHelper == null) {
					return;
				}

				Log.d(TAG, "Setup successfully, querying inventory");
				billHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});
	}

	

	// query items that we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		@Override
		public void onQueryInventoryFinished(IabResult result, Inventory inv) {
			Log.d(TAG, "Query inventory finished.");

			if (billHelper == null) {
				return;
			}

			if (!result.isSuccess()) {
				BillUtil.complain(context, TAG, "Failed to query inventory: "
						+ result);
			}

			Log.d(TAG, "Query Inventory was successful.");
			Purchase oneDollarPurchase = inv
					.getPurchase(BillUtil.SKU_DONATION_ONE);

		}
	};

}
