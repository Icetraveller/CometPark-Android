package com.icetraveller.android.apps.cometpark.provider;

import java.util.ArrayList;
import java.util.Arrays;

import com.icetraveller.android.apps.cometpark.provider.CometParkContract.Lots;
import com.icetraveller.android.apps.cometpark.provider.CometParkContract.Spots;
import com.icetraveller.android.apps.cometpark.provider.CometParkDatabase.Tables;
import com.icetraveller.android.apps.cometpark.utils.SelectionBuilder;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
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
//	private static final int SPOTS_ID_LOCATION_ID = 104;
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
//		case LOCATIONS:
//			return Locations.CONTENT_TYPE;
//		case LOCATIONS_ID:
//			return Locations.CONTENT_ITEM_TYPE;
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
		default:
			final SelectionBuilder builder = buildExpandedSelection(uri, match);
			Cursor cursor = builder.where(selection, selectionArgs).query(db,
					projection, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		LOGV(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		// in our case, insert always from sync helper
		switch (match) {
		case SPOTS: {
			db.insertOrThrow(Tables.SPOTS, null, values);
			notifyChange(uri);
			return Spots.buildSpotUri(values.getAsString(Spots.ID));
		}
		case LOTS: {
			db.insert(Tables.LOTS, null, values);
			notifyChange(uri);
			return Lots.buildLotUri(values.getAsString(Lots.ID));
		}
//		case LOCATIONS: {
//			db.insert(Tables.LOCATIONS, null, values);
//			notifyChange(uri);
//			return Locations.buildLocationUri(values.getAsString(Locations.ID));
//		}
		default: {
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		LOGV(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		// if (match == SEARCH_INDEX) {
		// // update the search index
		// // ScheduleDatabase.updateSessionSearchIndex(db);
		// return 1;
		// }
		final SelectionBuilder builder = buildSimpleSelection(uri);
		int retVal = builder.where(selection, selectionArgs).update(db, values);
		notifyChange(uri);
		return retVal;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		LOGV(TAG, "delete(uri=" + uri + ")");
		// if (uri == ScheduleContract.BASE_CONTENT_URI) {
		// // Handle whole database deletes (e.g. when signing out)
		// deleteDatabase();
		// notifyChange(uri, false);
		// return 1;
		// }

		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final SelectionBuilder builder = buildSimpleSelection(uri);
		int retVal = builder.where(selection, selectionArgs).delete(db);
		notifyChange(uri);
		return retVal;
	}

	/**
	 * Apply the given set of {@link ContentProviderOperation}, executing inside
	 * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
	 * any single one fails.
	 */
	@Override
	public ContentProviderResult[] applyBatch(
			ArrayList<ContentProviderOperation> operations)
			throws OperationApplicationException {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			final int numOperations = operations.size();
			final ContentProviderResult[] results = new ContentProviderResult[numOperations];
			for (int i = 0; i < numOperations; i++) {
				results[i] = operations.get(i).apply(this, results, i);
			}
			db.setTransactionSuccessful();
			return results;
		} finally {
			db.endTransaction();
		}
	}

	private SelectionBuilder buildSimpleSelection(Uri uri) {
		final SelectionBuilder builder = new SelectionBuilder();
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case SPOTS: {
			return builder.table(Tables.SPOTS);
		}
		case SPOTS_ID: {
			final String spotId = Spots.getSpotId(uri);
			return builder.table(Tables.SPOTS).where(Spots.ID + "=?", spotId);
		}
		case LOTS: {
			return builder.table(Tables.LOTS);
		}
		case LOTS_ID: {
			final String lotId = Lots.getLotId(uri);
			return builder.table(Tables.LOTS).where(Lots.ID + "=?", lotId);
		}
//		case LOCATIONS: {
//			return builder.table(Tables.LOCATIONS);
//		}
//		case LOCATIONS_ID: {
//			final String locationId = Locations.getLocationId(uri);
//			return builder.table(Tables.LOCATIONS).where(Locations.ID + "=?",
//					locationId);
//		}
		default: {
			throw new UnsupportedOperationException("Unknown uri for " + match
					+ ": " + uri);
		}
		}
	}
	

    /**
     * Build an advanced {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually only used by {@link #query}, since it
     * performs table joins useful for {@link Cursor} data.
     */
    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        switch (match) {
        case SPOTS: {
        	return builder.table(Tables.SPOTS);
        }
        case SPOTS_ID: {
        	final String spotId = Spots.getSpotId(uri);
			return builder.table(Tables.SPOTS).where(Spots.ID + "=?", spotId);
        }
        case LOTS: {
			return builder.table(Tables.LOTS);
		}
		case LOTS_ID: {
			final String lotId = Lots.getLotId(uri);
			return builder.table(Tables.LOTS).where(Lots.ID + "=?", lotId);
		}
//		case LOCATIONS: {
//			return builder.table(Tables.LOCATIONS);
//		}
//		case LOCATIONS_ID: {
//			final String locationId = Locations.getLocationId(uri);
//			return builder.table(Tables.LOCATIONS).where(Locations.ID + "=?",
//					locationId);
//		}
        default: {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        }
    }

	private void notifyChange(Uri uri) {
		Context context = getContext();
		context.getContentResolver().notifyChange(uri, null);

		// Widgets can't register content observers so we refresh widgets
		// separately.
	}
}
