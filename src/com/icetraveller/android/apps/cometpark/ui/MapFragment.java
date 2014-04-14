package com.icetraveller.android.apps.cometpark.ui;

import java.util.HashMap;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileProvider;
import com.icetraveller.android.apps.cometpark.R;

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

	// Tile Providers
	private TileProvider[] mTileProviders;
	private TileOverlay[] mTileOverlays;

	private static final int NUM_LOTS = 1;

	// Markers stored by id
	// private HashMap<String, MarkerModel> mMarkers = null;

	private boolean mOverlaysLoaded = false;
	private boolean mMarkersLoaded = false;

	// Cached size of view
	private int mWidth, mHeight;

	// Padding for #centerMap
	private int mShiftRight = 0;
	private int mShiftTop = 0;

	// Screen DPI
	private float mDPI = 0;

	private GoogleMap mMap;

	interface Callbacks {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LOGD(TAG, "Map onCreate");

		clearMap();

		// get DPI
		mDPI = getActivity().getResources().getDisplayMetrics().densityDpi / 160f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mapView = super.onCreateView(inflater, container,
				savedInstanceState);

		View v = inflater.inflate(R.layout.fragment_map, container, false);
		FrameLayout layout = (FrameLayout) v.findViewById(R.id.map_container);

		layout.addView(mapView, 0);
		
		
		return v;
	}

	private void clearMap() {
		if (mMap != null) {
			mMap.clear();
		}

		// setup tile provider arrays
		mTileProviders = new TileProvider[NUM_LOTS];
		mTileOverlays = new TileOverlay[NUM_LOTS];
	}

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
