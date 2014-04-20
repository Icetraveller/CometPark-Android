package com.icetraveller.android.apps.cometpark.provider;

import com.icetraveller.android.apps.cometpark.provider.CometParkContract.LotColumns;
import com.icetraveller.android.apps.cometpark.provider.CometParkContract.LotStatus;
import com.icetraveller.android.apps.cometpark.provider.CometParkContract.LotStatusColumns;
import com.icetraveller.android.apps.cometpark.provider.CometParkContract.Lots;
import com.icetraveller.android.apps.cometpark.provider.CometParkContract.SpotColumns;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import static com.icetraveller.android.apps.cometpark.utils.LogUtils.*;

public class CometParkDatabase extends SQLiteOpenHelper {

	private static final String TAG = makeLogTag(CometParkDatabase.class);

	private static final String DATABASE_NAME = "cometpark.db";

	private static final int VER_2014_LAUNCH = 101; // 1.0
	private static final int VER_2014_DEV = 100; // 1.1
	private static final int DATABASE_VERSION = VER_2014_DEV;
	private final Context mContext;

	/**
	 * Interface
	 */
	interface Tables {
		String SPOTS = "spots";
		// String LOCATIONS = "locations";
		String LOTS = "lots";
		String LOT_STATUS = "lot_status";

		String LOT_JOIN_LOT_STATUS = LOTS + " LEFT OUTER JOIN " + LOT_STATUS
				+ " ON " + LOTS + "." + Lots.ID + "=" + LOT_STATUS + "."
				+ LotStatus.ID;
	}

	private interface References {
		// String LOCATION_ID = "REFERENCES " + Tables.LOCATIONS + "(" +
		// Locations.ID + ")";
	}

	public CometParkDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Tables.SPOTS + " (" + BaseColumns._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + SpotColumns.ID
				+ " TEXT NOT NULL," + SpotColumns.LOT + " INTEGER NOT NULL,"
				+ SpotColumns.TYPE + " INTEGER NOT NULL," + SpotColumns.LAT
				+ " DOUBLE NOT NULL," + SpotColumns.LNG + " DOUBLE NOT NULL,"
				+ SpotColumns.STATUS + " INTEGER NOT NULL," + "UNIQUE ("
				+ SpotColumns.ID + ") ON CONFLICT REPLACE)");
		db.execSQL("CREATE TABLE " + Tables.LOTS + " (" + BaseColumns._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + LotColumns.ID
				+ " TEXT NOT NULL," + LotColumns.NAME + " TEXT NOT NULL,"
				+ LotColumns.STATUS + " INTEGER NOT NULL,"
				+ LotColumns.MAP_TILE_FILE + " TEXT NOT NULL,"
				+ LotColumns.MAP_TILE_URL + " TEXT NOT NULL,"
				+ LotColumns.LOCATION_TOP_LEFT + " TEXT NOT NULL, "
				+ LotColumns.LOCATION_TOP_RIGHT + " TEXT NOT NULL, "
				+ LotColumns.LOCATION_BOTTOM_LEFT + " TEXT NOT NULL, "
				+ LotColumns.LOCATION_BOTTOM_RIGHT + " TEXT NOT NULL, "
				+ "UNIQUE (" + LotColumns.ID + ") ON CONFLICT REPLACE)");
		db.execSQL("CREATE TABLE " + Tables.LOT_STATUS + " (" + BaseColumns._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + LotStatusColumns.ID
				+ " TEXT NOT NULL," + LotStatusColumns.AVAILABLE_SPOTS_COUNT
				+ " TEXT NOT NULL," + "UNIQUE (" + LotStatusColumns.ID
				+ ") ON CONFLICT REPLACE)");
		// db.execSQL("CREATE TABLE "+Tables.LOCATIONS+ " ("
		// + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
		// + LocationColumns.ID + " TEXT NOT NULL,"
		// +LocationColumns.LATITUDE + " DOUBLE NOT NULL,"
		// +LocationColumns.LONGITUDE + " DOUBLE NOT NULL,"
		// + "UNIQUE (" + LocationColumns.ID+ ") ON CONFLICT REPLACE)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		LOGD(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);

		// TODO cancel all updates or sync

		int version = oldVersion;
		// TODO will come back later
	}

	public static void deleteDatabase(Context context) {
		context.deleteDatabase(DATABASE_NAME);
	}

}
