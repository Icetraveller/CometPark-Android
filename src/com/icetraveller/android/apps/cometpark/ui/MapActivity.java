package com.icetraveller.android.apps.cometpark.ui;

import com.icetraveller.android.apps.cometpark.utils.MapUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;


public class MapActivity extends SimpleSinglePaneActivity implements
		MapFragment.Callbacks {
	
	
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
}
