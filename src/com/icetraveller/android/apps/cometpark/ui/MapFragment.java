package com.icetraveller.android.apps.cometpark.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;

import static com.icetraveller.android.apps.cometpark.utils.LogUtils.*;

public class MapFragment extends SupportMapFragment implements
		GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener,
		GoogleMap.OnCameraChangeListener, LoaderCallbacks<Cursor> {

	// Initial camera position
	private static final float CAMERA_ZOOM = 19f;
	
	private static final int SHOW_ALL = -1;

	// currently displayed lot
	private int mLot = SHOW_ALL;
	
	private static final String TAG = makeLogTag(MapFragment.class);

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCameraChange(CameraPosition position) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		// TODO Auto-generated method stub

	}
}
