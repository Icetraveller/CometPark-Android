package com.icetraveller.android.apps.cometpark.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileProvider;
import com.icetraveller.android.apps.cometpark.R;
import com.icetraveller.android.apps.cometpark.provider.CometParkContract;
import com.icetraveller.android.apps.cometpark.utils.MapUtils;

import static com.icetraveller.android.apps.cometpark.utils.LogUtils.*;

public class MapFragment extends SupportMapFragment implements
		GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener,
		GoogleMap.OnCameraChangeListener, LoaderCallbacks<Cursor> {
	// Initial camera position
	private static final LatLng CAMERA_UTDALLAS = new LatLng(32.986097,
			-96.749417);
	private static final LatLng TEST_SPOT = new LatLng(32.985990, -96.749650);
	private static final LatLng TEST_SPOT2 = new LatLng(32.985960, -96.749650);
	private static final float CAMERA_ZOOM = 19f;

	private static final String SHOW_ALL = "show_all";

	// currently displayed lot
	private String mLot = "0";
	private String[] mLots;

	private static final String TAG = makeLogTag(MapFragment.class);

	// Tile Providers
	private TileProvider[] mTileProviders;
	private TileOverlay[] mTileOverlays;

	private int numberOfLots = 1;

	// Markers stored by id
	private HashMap<String, MarkerModel> mMarkers = null;
	
	// Markers stored by floor
    private HashMap<String,ArrayList<Marker>> mMarkersLot = null;

	private boolean mOverlaysLoaded = false;
	private boolean mMarkersLoaded = false;
	
	// Show markers at default zoom level
    private boolean mShowMarkers = true;

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

		// read mLots
		mLots = getArguments().getString(MapUtils.SHOW_LOT).split(",");
		numberOfLots = mLots.length;

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

		// get the height and width of the view
		mapView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@SuppressWarnings("deprecation")
					@SuppressLint("NewApi")
					@Override
					public void onGlobalLayout() {
						final View v = getView();
						mHeight = v.getHeight();
						mWidth = v.getWidth();

						// also requires width and height
						// enableFloors(); TODO
						if (v.getViewTreeObserver().isAlive()) {
							// remove this layout listener
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
								v.getViewTreeObserver()
										.removeOnGlobalLayoutListener(this);
							} else {
								v.getViewTreeObserver()
										.removeGlobalOnLayoutListener(this);
							}
						}
					}
				});

		if (mMap == null) {
			setupMap(true);
		}

		LoaderManager lm = getActivity().getSupportLoaderManager();
		lm.initLoader(SpotsQuery._TOKEN, null, this);
		lm.initLoader(LotsQuery._TOKEN, null, this);

		return v;
	}

	private void setupMap(boolean resetCamera) {
		mMap = getMap();
		mMap.setOnMarkerClickListener(this);
		mMap.setOnCameraChangeListener(this);
		if (resetCamera) {
			mMap.moveCamera(CameraUpdateFactory
					.newCameraPosition(CameraPosition.fromLatLngZoom(
							CAMERA_UTDALLAS, CAMERA_ZOOM)));
		}
		mMap.setIndoorEnabled(false);
		mMap.getUiSettings().setZoomControlsEnabled(false);
		LOGD(TAG, "Map setup complete.");

	}

	private void clearMap() {
		if (mMap != null) {
			mMap.clear();
		}

		// setup tile provider arrays
		mTileProviders = new TileProvider[numberOfLots];
		mTileOverlays = new TileOverlay[numberOfLots];

		mMarkers = new HashMap<String, MarkerModel>();
		mMarkersLot = new HashMap<String,ArrayList<Marker>>();
		// initialise floor marker lists
        for(String s: mLots){
        	mMarkersLot.put(s, new ArrayList<Marker>());
        }
	}
	
	private void enableLot(){
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		switch (id) {
		case SpotsQuery._TOKEN: {
			Uri uri = CometParkContract.Spots.buildUri();
			return new CursorLoader(getActivity(), uri, SpotsQuery.PROJECTION,
					null, null, null);
		}
		case LotsQuery._TOKEN: {
			Uri uri = CometParkContract.Lots.buildLotUri(mLot);//TODO currently I support only one
			return new CursorLoader(getActivity(), uri, LotsQuery.PROJECTION, null, null, null);
		}
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (getActivity() == null) {
			return;
		}
		switch (loader.getId()) {
		case SpotsQuery._TOKEN:
			onSpotsLoadComplete(cursor);
			break;
		case LotsQuery._TOKEN:
			onLotsLoadComplete(cursor);
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {

	}

	@Override
	public void onCameraChange(CameraPosition cameraPosition) {
		mShowMarkers = cameraPosition.zoom >= 18;
		for (Marker m : mMarkersLot.get(mLot)) {
            m.setVisible(mShowMarkers);
        }
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

	private interface SpotsQuery {
		int _TOKEN = 0x1;

		String[] PROJECTION = { CometParkContract.Spots.ID,CometParkContract.Spots.LOT,
				CometParkContract.Spots.STATUS, CometParkContract.Spots.TYPE,
				CometParkContract.Spots.LAT, CometParkContract.Spots.LNG };

		int SPOT_ID = 0;
		int SPOT_LOT = 1;
		int SPOT_STATUS = 2;
		int SPOT_TYPE = 3;
		int SPOT_LAT = 4;
		int SPOT_LNG = 5;
	}
	
	private interface LotsQuery {
		int _TOKEN = 0x2;
		String[] PROJECTION = { 
				CometParkContract.Lots.ID,
				CometParkContract.Lots.NAME,
				CometParkContract.Lots.MAP_TILE_FILE,
				CometParkContract.Lots.STATUS,
				CometParkContract.Lots.LOCATION_TOP_LEFT,
				CometParkContract.Lots.LOCATION_TOP_RIGHT,
				CometParkContract.Lots.LOCATION_BOTTOM_LEFT,
				CometParkContract.Lots.LOCATION_BOTTOM_RIGHT
		};
		int LOT_ID = 0;
		int LOT_NAME = 1;
		int LOT_MAP_TILE_FILE = 2;
		int LOT_STATUS = 3;
		int LOT_LOCATION_TOP_LEFT = 4;
		int LOT_LOCATION_TOP_RIGHT = 5;
		int LOT_LOCATION_BOTTOM_LEFT = 6;
		int LOT_LOCATION_BOTTOM_RIGHT = 7;
		
	}

	// loaders
	private void onSpotsLoadComplete(Cursor cursor) {
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				// get data
				String spotId = cursor.getString(SpotsQuery.SPOT_ID);
				String lotId = cursor.getString(SpotsQuery.SPOT_LOT);
				int status = cursor.getInt(SpotsQuery.SPOT_STATUS);
				int permitType = cursor.getInt(SpotsQuery.SPOT_TYPE);
				double lat = cursor.getDouble(SpotsQuery.SPOT_LAT);
				double lng = cursor.getDouble(SpotsQuery.SPOT_LNG);
				if (status == 1) {
					// the spot is occupied, don't display anything
					// but may be display depends on user settings
				} else {
					BitmapDescriptor icon = null;
					switch (permitType) {
					default:
						icon = BitmapDescriptorFactory
								.fromResource(R.drawable.marker_test);
					}
					
					// add marker to map
					if (icon != null) {
						Marker m = mMap.addMarker(new MarkerOptions()
								.position(new LatLng(lat, lng)).title(spotId)
								.snippet("" + permitType).icon(icon)
								.visible(false));
						MarkerModel model = new MarkerModel(spotId, permitType,
								permitType, m);
						 mMarkersLot.get(lotId).add(m);
						 mMarkers.put(spotId, model);
					}
				}
				cursor.moveToNext();
			}
			mMarkersLoaded = true;
			enableLot();
		}

	}

	/**
	 * A structure to store information about a Marker.
	 */
	public static class MarkerModel {
		String spoId;
		int status;
		int type;
		Marker marker;

		public MarkerModel(String spoId, int status, int type, Marker marker) {
			this.spoId = spoId;
			this.type = type;
			this.status = status;
			this.marker = marker;
		}
	}
	

}
