package com.icetraveller.android.apps.cometpark.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

	
	public CometParkDatabase(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
