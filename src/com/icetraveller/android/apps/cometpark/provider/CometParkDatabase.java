package com.icetraveller.android.apps.cometpark.provider;

import com.icetraveller.android.apps.cometpark.provider.CometParkContract.LocationColumns;
import com.icetraveller.android.apps.cometpark.provider.CometParkContract.Locations;
import com.icetraveller.android.apps.cometpark.provider.CometParkContract.LotColumns;
import com.icetraveller.android.apps.cometpark.provider.CometParkContract.SpotColumns;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import static com.icetraveller.android.apps.cometpark.utils.LogUtils.*;

public class CometParkDatabase extends SQLiteOpenHelper{
	
	private static final String TAG = makeLogTag(CometParkDatabase.class);

    private static final String DATABASE_NAME = "cometpark.db";
    
    private static final int VER_2014_LAUNCH = 101;  // 1.0
    private static final int VER_2014_DEV = 100;  // 1.1
    private static final int DATABASE_VERSION = VER_2014_DEV;
    private final Context mContext;
	
	/**
	 * Interface
	 */
	interface Tables{
		String SPOTS = "spots";
		String LOCATIONS = "locations";
		String LOTS = "lots";
	}
	
	private interface References {
		String LOCATION_ID = "REFERENCES " + Tables.LOCATIONS + "(" + Locations.ID + ")";
	}

	
	public CometParkDatabase(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE "+Tables.SPOTS+ " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ SpotColumns.ID + " INTEGER NOT NULL,"
				+ SpotColumns.LOT + " INTEGER NOT NULL,"
				+ SpotColumns.TYPE + " INTEGER NOT NULL,"
				+ SpotColumns.LOCATIONID + " INTEGER NOT NULL,"
				+ SpotColumns.STATUS + " INTEGER NOT NULL,"
				+ "UNIQUE (" + SpotColumns.ID+ ") ON CONFLICT REPLACE)");
		db.execSQL("CREATE TABLE "+Tables.LOTS+ " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ LotColumns.ID + " INTEGER NOT NULL,"
				+ LotColumns.NAME + " TEXT NOT NULL,"
				+ LotColumns.STATUS + " INTEGER NOT NULL,"
				+ LotColumns.MAP_TILE_FILE + " TEXT NOT NULL,"
				+ LotColumns.MAP_TILE_URL + " TEXT NOT NULL,"
				+ LotColumns.LOCATION_TOP_LEFT + " INTEGER " + References.LOCATION_ID +","
				+ LotColumns.LOCATION_TOP_RIGHT + " INTEGER " + References.LOCATION_ID +","
				+ LotColumns.LOCATION_BOTTOM_LEFT + " INTEGER " + References.LOCATION_ID +","
				+ LotColumns.LOCATION_BOTTOM_RIGHT + " INTEGER " + References.LOCATION_ID +","
				+ "UNIQUE (" + LotColumns.ID+ ") ON CONFLICT REPLACE)");
		db.execSQL("CREATE TABLE "+Tables.LOCATIONS+ " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ LocationColumns.ID + " INTEGER NOT NULL,"
				+LocationColumns.LATITUDE + " DOUBLE NOT NULL,"
				+LocationColumns.LONGITUDE + " DOUBLE NOT NULL,"
				+ "UNIQUE (" + LocationColumns.ID+ ") ON CONFLICT REPLACE)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 LOGD(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
		 
		 //TODO cancel all updates or sync
		 
		 int version = oldVersion;
		 //TODO will come back later
	}
	
	public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

}
