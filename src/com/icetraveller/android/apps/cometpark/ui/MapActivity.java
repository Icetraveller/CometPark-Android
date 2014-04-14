package com.icetraveller.android.apps.cometpark.ui;

import android.support.v4.app.Fragment;


public class MapActivity extends SimpleSinglePaneActivity implements
		MapFragment.Callbacks {
	@Override
	protected Fragment onCreatePane() {
		return new MapFragment();
	}
}
