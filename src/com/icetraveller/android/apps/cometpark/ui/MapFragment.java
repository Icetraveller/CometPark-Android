package com.icetraveller.android.apps.cometpark.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.espian.showcaseview.targets.ActionItemTarget;
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
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.gson.Gson;
import com.icetraveller.android.apps.cometpark.Config;
import com.icetraveller.android.apps.cometpark.R;
import com.icetraveller.android.apps.cometpark.io.model.Spot;
import com.icetraveller.android.apps.cometpark.io.model.Spots;
import com.icetraveller.android.apps.cometpark.provider.CometParkContract;
import com.icetraveller.android.apps.cometpark.sync.SyncProcessor;
import com.icetraveller.android.apps.cometpark.ui.RankAdapter.LotsStatusQuery;
import com.icetraveller.android.apps.cometpark.utils.MapUtils;
import com.icetraveller.android.apps.cometpark.utils.PreferenceHelper;
import com.icetraveller.android.apps.cometpark.utils.SVGTileProvider;
import com.icetraveller.android.apps.cometpark.utils.UIUtils;

import static com.icetraveller.android.apps.cometpark.utils.LogUtils.*;

public class MapFragment extends SupportMapFragment implements
		GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener,
		GoogleMap.OnCameraChangeListener, LoaderCallbacks<Cursor> {
	// Initial camera position
	private static final LatLng CAMERA_UTDALLAS = new LatLng(32.987156,
			-96.750735);
	private static final LatLng TEST_SPOT = new LatLng(32.985990, -96.749650);
	private static final LatLng TEST_SPOT2 = new LatLng(32.985960, -96.749650);
	private static final float CAMERA_ZOOM = 15f;

	public static final String SHOW_ALL = "show_all";

	private MapInfoWindowAdapter mInfoAdapter;

	// currently displayed lot
	private String INITIAL_LOT = "0";
	private String mLot = "";

	private static final String TAG = makeLogTag(MapFragment.class);

	// Tile Providers
	private HashMap<String, TileProvider> mTileProviders;
	private HashMap<String, TileOverlay> mTileOverlays;

	// Markers stored by id
	private HashMap<String, MarkerModel> mMarkers = null;

	// Markers stored by floor
	private ArrayList<Marker> mMarkersLot = null;

	private boolean mLotsLoaded = false;
	private boolean mMarkersLoaded = false;
	private boolean wholeCampusMode = false;

	// Show markers at default zoom level
	private boolean mShow = true;

	// Cached size of view
	private int mWidth, mHeight;

	// Padding for #centerMap
	private int mShiftRight = 0;
	private int mShiftTop = 0;

	private int userPermitType = -1;

	private LatLng dadd = null;
	private LatLng sadd = null;
	// Screen DPI
	private float mDPI = 0;

	private GoogleMap mMap;
	
	private LocationManager locationManager = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// read mLots
		mLot = getArguments().getString(MapUtils.SHOW_LOT);
		if (mLot.equals(SHOW_ALL)) {
			// enter whole campus mode
			wholeCampusMode = true;
		}
		LOGD(TAG, "Map onCreate");

		clearMap();
		// get DPI
		mDPI = getActivity().getResources().getDisplayMetrics().densityDpi / 160f;
		setHasOptionsMenu(true);

		getActivity().registerReceiver(mHandleMessageReceiver,
				new IntentFilter(Config.UPDATE_SPOTS_ACTION));
		getActivity().registerReceiver(mHandleLotViewrReceiver,
				new IntentFilter(Config.VIEW_LOT_ACTION));

		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		String permitTypeString = prefs.getString(
				PreferenceHelper.PREF_KEY_PERMIT_TYPE, "2");
		userPermitType = Integer.parseInt(permitTypeString);

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
		if (!wholeCampusMode) {
			lm.initLoader(LotsQuery._TOKEN, null, this);
			lm.initLoader(SpotsQuery._TOKEN, null, this);
			locationManager = (LocationManager) getActivity()
					.getSystemService(Context.LOCATION_SERVICE);
			// Register the listener with the Location Manager to receive
						// location
						// updates
						locationManager.requestLocationUpdates(
								LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		} else {
			lm.initLoader(LotsStatusQuery._TOKEN, null, this);
		}

		return v;
	}

	public void onDestroy() {
		getActivity().unregisterReceiver(mHandleMessageReceiver);
		getActivity().unregisterReceiver(mHandleLotViewrReceiver);
		super.onDestroy();
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if (!wholeCampusMode) {
			getActivity().getMenuInflater().inflate(R.menu.map, menu);
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_navigate:
			if (dadd != null) {
				if(sadd == null){
					String locationProvider = LocationManager.NETWORK_PROVIDER;
					Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
					addCurrentAddr(new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude()));
				}
				String uri = "http://maps.google.com/maps?saddr="
						+ sadd.latitude + "," + sadd.longitude + "&daddr="
						+ dadd.latitude + "," + dadd.longitude;
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse(uri));
				intent.setClassName("com.google.android.apps.maps",
						"com.google.android.maps.MapsActivity");
				startActivity(intent);
				// Remove the listener you previously added
				locationManager.removeUpdates(locationListener);
			}
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	private void setupMap(boolean resetCamera) {
		mInfoAdapter = new MapInfoWindowAdapter(getLayoutInflater(null),
				getResources(), mMarkers);

		mMap = getMap();
		mMap.setOnMarkerClickListener(this);
		mMap.setOnInfoWindowClickListener(this);
		mMap.setOnCameraChangeListener(this);
		mMap.setMyLocationEnabled(true);
		mMap.setInfoWindowAdapter(mInfoAdapter);
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
		mTileProviders = new HashMap<String, TileProvider>();
		mTileOverlays = new HashMap<String, TileOverlay>();

		mMarkers = new HashMap<String, MarkerModel>();
		mMarkersLot = new ArrayList<Marker>();
	}

	private void enableLot() {
		if (mLotsLoaded && mMarkersLoaded && mShow)
			showLot(mLot);
	}

	private void showLot(String lot) {
		// hide old overlay
		mTileOverlays.get(lot).setVisible(false);
		for (Marker m : mMarkersLot) {
			m.setVisible(false);
		}
		mLot = lot;
		// show the lot overlay
		if (mTileOverlays.get(lot) != null) {
			mTileOverlays.get(lot).setVisible(true);
		}

		// show all markers
		showMarker();
	}

	private void disableLot() {
		// hide the lot overlay
		if (mTileOverlays.get(mLot) != null) {
			mTileOverlays.get(mLot).setVisible(false);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		if (TextUtils.isEmpty(mLot)) {
			mLot = INITIAL_LOT;
		}
		switch (id) {
		case SpotsQuery._TOKEN: {
			Uri uri = CometParkContract.Spots.buildSpotsInLot(mLot);
			return new CursorLoader(getActivity(), uri, SpotsQuery.PROJECTION,
					null, null, null);
		}
		case LotsQuery._TOKEN: {
			Uri uri = CometParkContract.Lots.buildLotUri(mLot);// TODO
																// currently
			// I support
			// only one
			return new CursorLoader(getActivity(), uri, LotsQuery.PROJECTION,
					null, null, null);
		}
		case LotsStatusQuery._TOKEN: {
			Uri uri = CometParkContract.Lots.buildLotsLotStatus();
			return new CursorLoader(getActivity(), uri,
					LotsStatusQuery.PROJECTION, null, null, null);
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
		case LotsStatusQuery._TOKEN:
			onLotsStatusLoadComplete(cursor);
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {

	}

	@Override
	public void onCameraChange(CameraPosition cameraPosition) {
		// if (TextUtils.isEmpty(mLot)) {
		// return;
		// }
		// mShow = cameraPosition.zoom >= 18;
		// if (mShow) {
		// enableLot();
		//
		// } else {
		// showMarker();
		// disableLot();
		// }
	}

	private void showMarker() {
		if (!mShow) {
			return;
		}

		if (wholeCampusMode) {
			for (Marker m : mMarkersLot) {
				m.setVisible(true);
			}
		} else {
			Log.d(TAG, "userPermitType= " + userPermitType);
			boolean permitFlag = true;
			for (Marker m : mMarkersLot) {
				String spotId = m.getTitle();
				MarkerModel model = mMarkers.get(spotId);
				permitFlag = userPermitType >= model.type;
				if ((model.status == Config.STATUS_AVAILABLE) && permitFlag) {
					m.setVisible(true);
				} else {
					m.setVisible(false);
				}
			}
		}
	}

	private void clearMarker() {
		if (!mShow || !mMarkersLoaded) {
			return;
		}
		for (Marker m : mMarkersLot) {
			m.setVisible(false);
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		Log.d(TAG, "onMarkerClicked");
		if (!wholeCampusMode) {
			return true;
		}
		MarkerModel model = mMarkers.get(marker.getSnippet());
		mInfoAdapter.setLotData(marker, marker.getTitle(), model.contentString);
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Log.d(TAG, "onInfoWindowClicked");
		if (!wholeCampusMode) {
			return;
		}
		String lotId = marker.getSnippet();
		Intent intent = new Intent(getActivity(),
				UIUtils.getMapActivityClass(getActivity()));
		intent.putExtra(MapUtils.SHOW_LOT, lotId);
		startActivity(intent);

	}

	private interface SpotsQuery {
		int _TOKEN = 0x1;

		String[] PROJECTION = { CometParkContract.Spots.ID,
				CometParkContract.Spots.LOT, CometParkContract.Spots.STATUS,
				CometParkContract.Spots.TYPE, CometParkContract.Spots.LAT,
				CometParkContract.Spots.LNG };

		int SPOT_ID = 0;
		int SPOT_LOT = 1;
		int SPOT_STATUS = 2;
		int SPOT_TYPE = 3;
		int SPOT_LAT = 4;
		int SPOT_LNG = 5;
	}

	private interface LotsQuery {
		int _TOKEN = 0x2;
		String[] PROJECTION = { CometParkContract.Lots.ID,
				CometParkContract.Lots.NAME,
				CometParkContract.Lots.MAP_TILE_FILE,
				CometParkContract.Lots.STATUS,
				CometParkContract.Lots.LOCATION_TOP_LEFT,
				CometParkContract.Lots.LOCATION_TOP_RIGHT,
				CometParkContract.Lots.LOCATION_BOTTOM_LEFT,
				CometParkContract.Lots.LOCATION_BOTTOM_RIGHT };
		int LOT_ID = 0;
		int LOT_NAME = 1;
		int LOT_MAP_TILE_FILE = 2;
		int LOT_STATUS = 3;
		int LOT_LOCATION_TOP_LEFT = 4;
		int LOT_LOCATION_TOP_RIGHT = 5;
		int LOT_LOCATION_BOTTOM_LEFT = 6;
		int LOT_LOCATION_BOTTOM_RIGHT = 7;
	}

	public interface LotsStatusQuery {
		int _TOKEN = 0x3;

		String[] PROJECTION = { BaseColumns._ID, CometParkContract.Lots.ID,
				CometParkContract.LotStatus.AVAILABLE_SPOTS_COUNT,
				CometParkContract.Lots.NAME,
				CometParkContract.Lots.LOCATION_TOP_LEFT,
				CometParkContract.Lots.LOCATION_BOTTOM_LEFT,
				CometParkContract.Lots.LOCATION_TOP_RIGHT,
				CometParkContract.Lots.LOCATION_BOTTOM_RIGHT,
				CometParkContract.Lots.STATUS };
		int _ID = 0;
		int ID = 1;
		int AVAILABLE_SPOTS_COUNT = 2;
		int NAME = 3;
		int LOCATION_TOP_LEFT = 4;
		int LOCATION_BOTTOM_LEFT = 5;
		int LOCATION_TOP_RIGHT = 6;
		int LOCATION_BOTTOM_RIGHT = 7;
		int STATUS = 8;
	}

	// loaders
	private void onSpotsLoadComplete(Cursor cursor) {
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				// get data
				String spotId = cursor.getString(SpotsQuery.SPOT_ID);
				int status = cursor.getInt(SpotsQuery.SPOT_STATUS);
				Log.d(TAG, "spot:" + spotId + " status: " + status);
				int permitType = cursor.getInt(SpotsQuery.SPOT_TYPE);
				double lat = cursor.getDouble(SpotsQuery.SPOT_LAT);
				double lng = cursor.getDouble(SpotsQuery.SPOT_LNG);
				BitmapDescriptor icon = null;
				switch (permitType) {
				case Config.PERMIT_TYPE_EXTENDED:
					icon = BitmapDescriptorFactory
							.fromResource(R.drawable.marker_extended);
					break;
				case Config.PERMIT_TYPE_GREEN:
					icon = BitmapDescriptorFactory
							.fromResource(R.drawable.marker_green);
					break;
				case Config.PERMIT_TYPE_GOLD:
					icon = BitmapDescriptorFactory
							.fromResource(R.drawable.marker_gold);
					break;
				case Config.PERMIT_TYPE_ORANGE:
					icon = BitmapDescriptorFactory
							.fromResource(R.drawable.marker_orange);
					break;
				case Config.PERMIT_TYPE_PURPLE:
					icon = BitmapDescriptorFactory
							.fromResource(R.drawable.marker_purple);
					break;
				default:
					icon = BitmapDescriptorFactory
							.fromResource(R.drawable.marker_test);
				}

				// add marker to map
				if (icon != null) {
					Marker m = mMap.addMarker(new MarkerOptions()
							.position(new LatLng(lat, lng)).title(spotId)
							.snippet("" + status).icon(icon).visible(false));
					MarkerModel model = new MarkerModel(spotId, status,
							permitType, m);
					mMarkersLot.add(m);
					mMarkers.put(spotId, model);
				}
				cursor.moveToNext();
			}
			mMarkersLoaded = true;
			enableLot();
		}
	}

	private void onLotsLoadComplete(Cursor cursor) {
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				// get data
				String lotId = cursor.getString(LotsQuery.LOT_ID);
				String file = cursor.getString(LotsQuery.LOT_MAP_TILE_FILE);
				int status = cursor.getInt(LotsQuery.LOT_STATUS);
				if (status == Config.STATUS_OCCUPIED) {
					// the lot is reserved
				} else {
					String[] topLeft = cursor.getString(
							LotsQuery.LOT_LOCATION_TOP_LEFT).split(",");
					String[] topRight = cursor.getString(
							LotsQuery.LOT_LOCATION_TOP_RIGHT).split(",");
					String[] bottomLeft = cursor.getString(
							LotsQuery.LOT_LOCATION_BOTTOM_LEFT).split(",");
					String[] bottomRight = cursor.getString(
							LotsQuery.LOT_LOCATION_BOTTOM_RIGHT).split(",");

					File f = MapUtils.getTileFile(getActivity()
							.getApplicationContext(), file);
					if (f != null) {
						double[] coordinates = MapUtils.stringsToProjections(
								topLeft, topRight, bottomLeft, bottomRight);
						LatLng ll = MapUtils.findCenter(topLeft, topRight,
								bottomLeft, bottomRight);
						moveCamera(ll);
						dadd = ll;
						Log.d(TAG, "file:" + file);
						addTileProvider(lotId, f, coordinates);
					}
				}
				cursor.moveToNext();
			}
			mLotsLoaded = true;
			enableLot();
		}

	}

	private void addCurrentAddr(LatLng ll) {
		sadd = ll;
	}

	private void onLotsStatusLoadComplete(Cursor cursor) {
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				// get data
				String lotId = cursor.getString(LotsStatusQuery.ID);
				String name = cursor.getString(LotsStatusQuery.NAME);
				// String file = cursor.getString(LotsQuery.LOT_MAP_TILE_FILE);
				int status = cursor.getInt(LotsStatusQuery.STATUS);
				if (status == Config.STATUS_OCCUPIED) {
					// the lot is reserved
				} else {
					String[] topLeft = cursor.getString(
							LotsStatusQuery.LOCATION_TOP_LEFT).split(",");
					String[] topRight = cursor.getString(
							LotsStatusQuery.LOCATION_TOP_RIGHT).split(",");
					String[] bottomLeft = cursor.getString(
							LotsStatusQuery.LOCATION_BOTTOM_LEFT).split(",");
					String[] bottomRight = cursor.getString(
							LotsStatusQuery.LOCATION_BOTTOM_RIGHT).split(",");
					LatLng ll = MapUtils.findCenter(topLeft, topRight,
							bottomLeft, bottomRight);

					BitmapDescriptor icon = null;
					String[] statusStrings = cursor.getString(
							LotsStatusQuery.AVAILABLE_SPOTS_COUNT).split(",");
					int availableCounts = countAvailableSpots(statusStrings);
					int max = Integer
							.parseInt(statusStrings[Config.PERMIT_TYPE_SUM]
									.trim());
					float level = 0;
					if (max != 0) {
						level = (float) availableCounts / max;
					}
					if (level > 0.5) {
						icon = BitmapDescriptorFactory
								.fromResource(R.drawable.marker_easy);
					} else if (level > 0.25) {
						icon = BitmapDescriptorFactory
								.fromResource(R.drawable.marker_avg);
					} else {
						icon = BitmapDescriptorFactory
								.fromResource(R.drawable.marker_hard);
					}
					if (icon != null) {
						Marker m = mMap.addMarker(new MarkerOptions()
								.position(ll).title("Lot " + name)
								.snippet(lotId).icon(icon).visible(false));
						MarkerModel model = new MarkerModel(level * 100
								+ "% Chance to find a spot", status, 99, m);
						mMarkersLot.add(m);
						mMarkers.put(lotId, model);
					}
				}
				cursor.moveToNext();
			}
			mLotsLoaded = true;
			mMarkersLoaded = true;
			// enableLot();
			showMarker();
		}
	}

	private int countAvailableSpots(String[] ss) {
		if (ss.length <= userPermitType)
			return 0;
		int i = 0;
		int sum = 0;
		while (i <= userPermitType) {
			try {
				sum = sum + Integer.parseInt(ss[i].trim());
			} catch (NumberFormatException e) {
				// ignore
			}
			i++;
		}
		return sum;
	}

	private void moveCamera(LatLng ll) {
		CameraPosition cameraPosition = new CameraPosition.Builder()
	    .target(ll)      // Sets the center of the map to Mountain View
	    .zoom(18)                   // Sets the zoom
	    .tilt(45)                   // Sets the tilt of the camera to 30 degrees
	    .build();                   // Creates a CameraPosition from the builder
		mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	void addTileProvider(String lot, File f, double[] coordinates) {
		if (!f.exists()) {
			return;
		}
		TileProvider provider;
		try {
			provider = new SVGTileProvider(f, mDPI, coordinates);
		} catch (IOException e) {
			LOGD(TAG, "Could not create Tile Provider.");
			e.printStackTrace();
			return;
		}
		TileOverlayOptions tileOverlay = new TileOverlayOptions().tileProvider(
				provider).visible(false);
		mTileProviders.put(lot, provider);
		mTileOverlays.put(lot, mMap.addTileOverlay(tileOverlay));
	}

	/**
	 * A structure to store information about a Marker.
	 */
	public static class MarkerModel {
		String contentString;
		int status;
		int type;
		Marker marker;

		public MarkerModel(String contentString, int status, int type,
				Marker marker) {
			this.contentString = contentString;
			this.type = type;
			this.status = status;
			this.marker = marker;
		}
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (wholeCampusMode) {
				return;
			}

			String newMessage = intent.getExtras().getString(
					Config.EXTRA_MESSAGE);
			Spots spotsJson = new Gson().fromJson(newMessage, Spots.class);

			for (Spot spot : spotsJson.spots) {
				MarkerModel model = mMarkers.get(spot.id);
				model.status = spot.status;
				Marker m = model.marker;
				m.setSnippet("" + model.status);
				m.setVisible(false);
			}
			showMarker();
			Log.d(TAG, newMessage);
		}
	};

	private final BroadcastReceiver mHandleLotViewrReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!wholeCampusMode) {
				return;
			}

			String lotId = intent.getExtras().getString(Config.EXTRA_MESSAGE);

			MarkerModel model = mMarkers.get(lotId);
			Marker marker = model.marker;

			LatLng ll = model.marker.getPosition();
			moveCamera(ll);

			mInfoAdapter.setLotData(marker, marker.getTitle(),
					model.contentString);
			model.marker.showInfoWindow();
		}
	};

	// Define a listener that responds to location updates
	private LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			// Called when a new location is found by the network location
			// provider.
			addCurrentAddr(new LatLng(location.getLatitude(),
					location.getLongitude()));
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}
	};

}
