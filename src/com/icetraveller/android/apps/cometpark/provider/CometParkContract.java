package com.icetraveller.android.apps.cometpark.provider;

import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.text.TextUtils;

public final class CometParkContract {

	interface SpotColumns {
		String ID = "spot_id";
		String LOT = "spot_lot";
		String TYPE = "spot_type";
		String LAT = "spot_lat";
		String LNG = "spot_lng";
		String STATUS = "spot_status";
	}

	interface LotColumns {
		String ID = "lot_id";
		String NAME = "lot_name";
		String LOCATION_TOP_LEFT = "Lot_location_top_left";
		String LOCATION_TOP_RIGHT = "Lot_location_top_right";
		String LOCATION_BOTTOM_LEFT = "Lot_location_bottom_left";
		String LOCATION_BOTTOM_RIGHT = "Lot_location_bottom_right";
		String MAP_TILE_FILE = "lot_map_tile_file";
		String MAP_TILE_URL = "lot_map_tile_url";
		String STATUS = "lot_status";
	}

	// interface LocationColumns {
	// String ID = "location_id";
	// String LATITUDE = "latitude";
	// String LONGITUDE = "longitude";
	// }

	interface LotStatusColumns {
		String ID = "lot_id";
		String AVAILABLE_SPOTS_COUNT = "available_spots_count";
	}

	public static final String CONTENT_AUTHORITY = "com.icetraveller.android.apps.cometpark";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://"
			+ CONTENT_AUTHORITY);

	private static final String PATH_SPOTS = "spots";
	private static final String PATH_LOTS = "lots";
	private static final String PATH_OF = "of";
	private static final String PATH_LOT_STATUS = "lot_status";

	public static class Spots implements SpotColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_SPOTS).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.cometpark.spot";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.cometpark.spot";

		/** Default "ORDER BY" clause. */
		// TODO this order might not be good
		public static final String DEFAULT_SORT = SpotColumns.ID + " ASC";

		/** Build {@link Uri} for all spots */
		public static Uri buildUri() {
			return CONTENT_URI;
		}

		/** Build {@link Uri} for requested spot. BETA TODO */
		public static Uri buildSpotUri(String spotId) {
			return CONTENT_URI.buildUpon().appendPath(spotId).build();
		}

		public static Uri buildSpotsInLot(String lotId) {
			return CONTENT_URI.buildUpon().appendPath(PATH_OF)
					.appendPath(lotId).build();
		}

		/** Read {@link #SPOTS_ID} from {@link Spots} {@link Uri}. */
		public static String getSpotId(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		/** Read {@link #SPOTS_ID} from {@link Spots} {@link Uri}. */
		public static String getLotIdForSpots(Uri uri) {
			return uri.getPathSegments().get(2);
		}
	}

	public static class Lots implements LotColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_LOTS).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.cometpark.lot";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.cometpark.lot";

		/** Default "ORDER BY" clause. */
		public static final String DEFAULT_SORT = LotColumns.NAME + " ASC";

		/** Build {@link Uri} for all lots */
		public static Uri buildUri() {
			return CONTENT_URI;
		}

		/** Build {@link Uri} for requested spot. BETA TODO */
		public static Uri buildLotUri(String lotId) {
			return CONTENT_URI.buildUpon().appendPath(lotId).build();
		}

		/** Build {@link Uri} for requested spot. BETA TODO */
		public static Uri buildLotNameUri(String lotName) {
			return CONTENT_URI.buildUpon().appendPath(String.valueOf(lotName))
					.build();
		}

		/** Read {@link #LOTS_ID} from {@link Lots} {@link Uri}. */
		public static String getLotId(Uri uri) {
			return uri.getPathSegments().get(1);
		}
		
		public static Uri buildLotsLotStatus(){
			return CONTENT_URI.buildUpon().appendPath(PATH_LOT_STATUS).build();
		}
	}

	public static class LotStatus implements LotStatusColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
				.appendPath(PATH_LOT_STATUS).build();
		
		 public static final String CONTENT_TYPE =
		 "vnd.android.cursor.dir/vnd.cometpark.lot_status";
		 public static final String CONTENT_ITEM_TYPE =
		 "vnd.android.cursor.item/vnd.cometpark.lot_status";
		 
	}

	// public static class Locations implements LocationColumns, BaseColumns{
	// public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
	// .appendPath(PATH_LOCATIONS).build();
	//
	// public static final String CONTENT_TYPE =
	// "vnd.android.cursor.dir/vnd.cometpark.location";
	// public static final String CONTENT_ITEM_TYPE =
	// "vnd.android.cursor.item/vnd.cometpark.location";
	//
	// /** Default "ORDER BY" clause. */
	// public static final String DEFAULT_SORT = LocationColumns.ID + " ASC";
	//
	// /** Build {@link Uri} for requested spot. BETA TODO*/
	// public static Uri buildLocationUri(String locationId) {
	// return CONTENT_URI.buildUpon()
	// .appendPath(String.valueOf(locationId)).build();
	// }
	//
	// /** Read {@link #LOCATIONS_ID} from {@link Locations} {@link Uri}. */
	// public static String getLocationId(Uri uri) {
	// return uri.getPathSegments().get(1);
	// }
	// }

	private CometParkContract() {
	}
}
