package com.icetraveller.android.apps.cometpark.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class ComeParkContract {

	interface SpotColumns {
		String ID = "spot_id";
		String LOT = "spot_lot";
		String TYPE = "spot_type";
		String LOCATIONID = "spot_location_id";
		String STATUS = "spot_status";
	}

	interface LotColumns {
		String ID = "lot_id";
		String LOCATION_TOP_LEFT = "Lot_location_top_left";
		String LOCATION_TOP_RIGHT = "Lot_location_top_right";
		String LOCATION_BOTTOM_LEFT = "Lot_location_bottom_left";
		String LOCATION_BOTTOM_RIGHT = "Lot_location_bottom_right";
		String MAP_TILE_FILE = "lot_map_tile_file";
		String MAP_TILE_URL = "lot_map_tile_url";
		String STATUS = "lot_status";
	}

	interface LocationColumns {
		String ID = "location_id";
		String LATITUDE = "latitude";
		String LONGITUDE = "longitude";
	}

	public static final String CONTENT_AUTHORITY = "com.icetraveller.android.apps.cometpark";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://"
			+ CONTENT_AUTHORITY);

	private static final String PATH_SPOTS = "spots";
	private static final String PATH_LOTS = "lots";
	private static final String PATH_LOCATIONS = "locations";
	
	public static class Spots implements SpotColumns, BaseColumns{
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_SPOTS).build();
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.cometpark.spot";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.cometpark.spot";
        
        /** Default "ORDER BY" clause. */ //TODO this order might not be good
        public static final String DEFAULT_SORT = SpotColumns.ID + " ASC";
        
        /** Build {@link Uri} for all spots */
        public static Uri buildUri() {
            return CONTENT_URI;
        }
        
        /** Build {@link Uri} for requested spot. BETA TODO*/
        public static Uri buildSpotUri(int spotId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(spotId)).build();
        }
	}
	
	public static class Lots implements LotColumns, BaseColumns{
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_LOTS).build();
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.cometpark.lot";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.cometpark.lot";
        
        /** Default "ORDER BY" clause. */ 
        public static final String DEFAULT_SORT = LotColumns.ID + " ASC";
        
        /** Build {@link Uri} for all lots */
        public static Uri buildUri() {
            return CONTENT_URI;
        }
        
        /** Build {@link Uri} for requested spot. BETA TODO*/
        public static Uri buildLotUri(int lotId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(lotId)).build();
        }
	}
	
	public static class Locations implements LocationColumns, BaseColumns{
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_LOCATIONS).build();
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.cometpark.location";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.cometpark.location";
        
        /** Default "ORDER BY" clause. */ 
        public static final String DEFAULT_SORT = LocationColumns.ID + " ASC";
        
        /** Build {@link Uri} for requested spot. BETA TODO*/
        public static Uri buildLocationUri(int locationId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(locationId)).build();
        }
		
	}
}
