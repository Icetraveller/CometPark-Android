package com.icetraveller.android.apps.cometpark.provider;

import java.util.Arrays;

import com.icetraveller.android.apps.cometpark.provider.CometParkContract.Locations;
import com.icetraveller.android.apps.cometpark.provider.CometParkContract.Lots;
import com.icetraveller.android.apps.cometpark.provider.CometParkContract.Spots;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import static com.icetraveller.android.apps.cometpark.utils.LogUtils.*;

public class CometParkProvider extends ContentProvider {

	private static final String TAG = makeLogTag(CometParkProvider.class);

	private CometParkDatabase mOpenHelper;

	private static final UriMatcher sUriMatcher = buildUriMatcher();

	private static final int SPOTS = 100;
	private static final int SPOTS_ID = 101;
	private static final int SPOTS_ID_LOT = 102;
	private static final int SPOTS_ID_TYPE = 103;
	private static final int SPOTS_ID_LOCATION_ID = 104;
	private static final int SPOTS_ID_STATUS = 105;

	private static final int LOTS = 200;
	private static final int LOTS_ID = 201;
	private static final int LOTS_ID_NAME = 202;
	private static final int LOTS_ID_LOCATION_TOP_LEFT = 203;
	private static final int LOTS_ID_LOCATION_TOP_RIGHT = 204;
	private static final int LOTS_ID_LOCATION_BOTTOM_LEFT = 205;
	private static final int LOTS_ID_LOCATION_BOTTOM_RIGHT = 206;
	private static final int LOTS_ID_STATUS = 207;
	private static final int LOTS_ID_MAP_TILES_FILE = 208;
	private static final int LOTS_ID_MAP_TILES_URL = 209;

	private static final int LOCATIONS = 300;
	private static final int LOCATIONS_ID = 301;
	private static final int LOCATIONS_ID_LATITUDE = 302;
	private static final int LOCATIONS_ID_LONGITUDE = 303;

	/**
	 * Build and return a {@link UriMatcher} that catches all {@link Uri}
	 * variations supported by this {@link ContentProvider}.
	 */
	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = CometParkContract.CONTENT_AUTHORITY;
		// matcher.addURI(authority, "mapmarkers", MAPMARKERS);
		// matcher.addURI(authority, "mapmarkers/floor/*", MAPMARKERS_FLOOR);
		// matcher.addURI(authority, "mapmarkers/*", MAPMARKERS_ID);
		//
		// matcher.addURI(authority, "maptiles", MAPTILES);
		// matcher.addURI(authority, "maptiles/*", MAPTILES_FLOOR);

		matcher.addURI(authority, "spots", SPOTS);
		matcher.addURI(authority, "spots/*", SPOTS_ID);
		matcher.addURI(authority, "spots/*/lot", SPOTS_ID_LOT);
		matcher.addURI(authority, "spots/*/type", SPOTS_ID_TYPE);
		matcher.addURI(authority, "spots/*/location_id", SPOTS_ID_LOCATION_ID);
		matcher.addURI(authority, "spots/*/status", SPOTS_ID_STATUS);

		matcher.addURI(authority, "lots", LOTS);
		matcher.addURI(authority, "lots/*", LOTS_ID);
		matcher.addURI(authority, "lots/*/name", LOTS_ID_NAME);
		matcher.addURI(authority, "lots/*/location_top_left",
				LOTS_ID_LOCATION_TOP_LEFT);
		matcher.addURI(authority, "lots/*/location_top_right",
				LOTS_ID_LOCATION_TOP_RIGHT);
		matcher.addURI(authority, "lots/*/location_bottom_left",
				LOTS_ID_LOCATION_BOTTOM_LEFT);
		matcher.addURI(authority, "lots/*/location_bottom_right",
				LOTS_ID_LOCATION_BOTTOM_RIGHT);
		matcher.addURI(authority, "lots/*/map_tiles_file",
				LOTS_ID_MAP_TILES_FILE);
		matcher.addURI(authority, "lots/*/map_tiles_url", LOTS_ID_MAP_TILES_URL);
		matcher.addURI(authority, "lots/*/status", LOTS_ID_STATUS);

		matcher.addURI(authority, "locations", LOCATIONS);
		matcher.addURI(authority, "locations/*", LOCATIONS_ID);

		return matcher;
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new CometParkDatabase(getContext());
		return true;
	}

	private void deleteDatabase() {
		// TODO: wait for content provider operations to finish, then tear down
		mOpenHelper.close();
		Context context = getContext();
		CometParkDatabase.deleteDatabase(context);
		mOpenHelper = new CometParkDatabase(getContext());
	}

	@Override
	public String getType(Uri uri) {
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case SPOTS:
			return Spots.CONTENT_TYPE;
		case SPOTS_ID:
			return Spots.CONTENT_ITEM_TYPE;
		case LOTS:
			return Lots.CONTENT_TYPE;
		case LOTS_ID:
			return Lots.CONTENT_ITEM_TYPE;
		case LOCATIONS:
			return Locations.CONTENT_TYPE;
		case LOCATIONS_ID:
			return Locations.CONTENT_ITEM_TYPE;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		LOGV(TAG, "query(uri=" + uri + ", proj=" + Arrays.toString(projection)
				+ ")");
		final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		final int match = sUriMatcher.match(uri);
		switch (match) {
		
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
