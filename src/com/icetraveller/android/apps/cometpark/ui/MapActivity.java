package com.icetraveller.android.apps.cometpark.ui;

import com.icetraveller.android.apps.cometpark.utils.MapUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;


public class MapActivity extends SimpleSinglePaneActivity {
	
	@Override
	protected Fragment onCreatePane() {
		Intent intent = getIntent();
		String lotId = intent.getStringExtra(MapUtils.SHOW_LOT);
		MapFragment mapFragment = new MapFragment();
		Bundle b = new Bundle();
		b.putString(MapUtils.SHOW_LOT, lotId);
		mapFragment.setArguments(b);
		return mapFragment;
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
