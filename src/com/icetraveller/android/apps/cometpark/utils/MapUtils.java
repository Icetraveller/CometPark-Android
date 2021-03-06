package com.icetraveller.android.apps.cometpark.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

public class MapUtils {

	private static final String TILE_PATH = "lotstiles";
	private static final String PREF_MYLOCATION_ENABLED = "map_mylocation_enabled";
	public static final String SHOW_LOT = "show_lot";
	private static final String TAG = LogUtils.makeLogTag(MapUtils.class);

	private static float mDPI = -1.0f;
	private static int mLabelPadding;
	private static final float LABEL_OUTLINEWIDTH = 3.5f;
	private static final int LABEL_PADDING = 2;
	private static final int LABEL_TEXTSIZE = 14;
	private static Paint mLabelOutlinePaint = null;
	private static Paint mLabelTextPaint;

	private static void setupLabels() {
		float strokeWidth = LABEL_OUTLINEWIDTH * mDPI;

		mLabelTextPaint = new Paint();
		mLabelTextPaint.setTextSize(LABEL_TEXTSIZE * mDPI);
		mLabelTextPaint.setColor(Color.WHITE);
		mLabelTextPaint.setAntiAlias(true);

		mLabelOutlinePaint = new Paint(mLabelTextPaint);
		mLabelOutlinePaint.setStyle(Paint.Style.STROKE);
		mLabelOutlinePaint.setColor(0x99000000);
		mLabelOutlinePaint.setStrokeWidth(strokeWidth);
		mLabelOutlinePaint.setXfermode(new PorterDuffXfermode(
				PorterDuff.Mode.SRC));
		mLabelOutlinePaint.setStrokeJoin(Paint.Join.ROUND);
		mLabelOutlinePaint.setStrokeCap(Paint.Cap.ROUND);

		mLabelPadding = (int) Math.ceil(LABEL_PADDING * mDPI + strokeWidth);
	}

	public static Bitmap createTextLabel(String label, float dpi) {
		if (dpi != mDPI) {
			mDPI = dpi;
			setupLabels();
		}

		// calculate size
		Rect bounds = new Rect();
		mLabelTextPaint.getTextBounds(label, 0, label.length(), bounds);

		// calculate text size
		int bitmapH = Math.abs(bounds.top) + 2 * mLabelPadding;
		int bitmapW = bounds.right + 2 * mLabelPadding;

		// Create bitmap and draw text
		Bitmap b = Bitmap.createBitmap(bitmapW, bitmapH,
				Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);

		c.drawText(label, mLabelPadding, bitmapH - mLabelPadding,
				mLabelOutlinePaint);
		c.drawText(label, mLabelPadding, bitmapH - mLabelPadding,
				mLabelTextPaint);

		return b;
	}

	private static String[] mapTileAssets;

	/**
	 * Returns true if the given tile file exists as a local asset.
	 */
	public static boolean hasTileAsset(Context context, String filename) {

		// cache the list of available files
		if (mapTileAssets == null) {
			try {
				mapTileAssets = context.getAssets().list("lotstiles");
			} catch (IOException e) {
				// no assets
				mapTileAssets = new String[0];
			}
		}

		// search for given filename
		for (String s : mapTileAssets) {
			if (s.equals(filename)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Copy the file from the assets to the map tiles directory if it was
	 * shipped with the APK.
	 */
	public static boolean copyTileAsset(Context context, String filename) {
		if (!hasTileAsset(context, filename)) {
			// file does not exist as asset
			return false;
		}

		// copy file from asset to internal storage
		try {
			InputStream is = context.getAssets().open(
					TILE_PATH + File.separator + filename);
			File f = getTileFile(context, filename);
			FileOutputStream os = new FileOutputStream(f);

			byte[] buffer = new byte[1024];
			int dataSize;
			while ((dataSize = is.read(buffer)) > 0) {
				os.write(buffer, 0, dataSize);
			}
			os.close();
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	/**
	 * Return a {@link File} pointing to the storage location for map tiles.
	 */
	public static File getTileFile(Context context, String filename) {
		File folder = new File(context.getFilesDir(), TILE_PATH);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		return new File(folder, filename);
	}

	public static void removeUnusedTiles(Context mContext,
			final ArrayList<String> usedTiles) {
		// remove all files are stored in the tile path but are not used
		File folder = new File(mContext.getFilesDir(), TILE_PATH);
		File[] unused = folder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {
				return !usedTiles.contains(filename);
			}
		});

		for (File f : unused) {
			f.delete();
		}
	}

	public static boolean hasTile(Context mContext, String filename) {
		return getTileFile(mContext, filename).exists();
	}

	public static boolean getMyLocationEnabled(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(PREF_MYLOCATION_ENABLED, false);
	}

	public static void setMyLocationEnabled(final Context context,
			final boolean enableMyLocation) {
		LogUtils.LOGD(TAG, "Set my location enabled: " + enableMyLocation);
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(PREF_MYLOCATION_ENABLED, enableMyLocation)
				.commit();
	}

	/**
	 * 
	 * @param lat in deg
	 * @param lng in deg
	 * @return projection lng:lat
	 */
	public static double[] convertToProjection(double lat, double lng) {
		if (lng > 180) {
			lng -= 360;
		}
		lng /= 360;
		lng += 0.5;
		lat = 0.5 - ((Math.log(Math.tan((Math.PI / 4)
				+ (Math.toRadians(0.5 * lat)))) / Math.PI) / 2.0);

		// System.out.println(lng*256 + ", " + lat*256);
		return new double[] { lng * 256, lat * 256 };
	}

	public static double[] stringsToDoubles(String[]... args){
		if(args.length != 4){
			//wrong
		}
		double[] result = new double[8];
		int i = 0;
		
		for(String[] strings: args){
			result[i] = Double.parseDouble(strings[0]);
			result[i+1] = Double.parseDouble(strings[1]);
			i+=2;
		}
		return result;
	}
	public static double[] stringsToProjections(String[]... args){
		if(args.length != 4){
			//wrong
		}
		double[] result = new double[8];
		int i = 0;
		
		for(String[] strings: args){
			double[] projections = convertToProjection(Double.parseDouble(strings[0]),Double.parseDouble(strings[1]));
			result[i] = projections[0];
			result[i+1] = projections[1];
			i+=2;
		}
		return result;
	}
	
	public static LatLng findCenter(String[]... args){
	    int total = args.length;

	    double X = 0;
	    double Y = 0;
	    double Z = 0;

	    for(String[] s : args)
	    {
	    	double i1 = Double.parseDouble(s[0]);
	    	double i2 = Double.parseDouble(s[1]);
	        double lat = i1 * Math.PI / 180;
	        double lon = i2 * Math.PI / 180;

	        double x = Math.cos(lat) * Math.cos(lon);
	        double y = Math.cos(lat) * Math.sin(lon);
	        double z = Math.sin(lat);

	        X += x;
	        Y += y;
	        Z += z;
	    }

	    X = X / total;
	    Y = Y / total;
	    Z = Z / total;

	    double Lon = Math.atan2(Y, X);
	    double Hyp = Math.sqrt(X * X + Y * Y);
	    double Lat = Math.atan2(Z, Hyp);
	    return new LatLng(Lat * 180 / Math.PI , Lon * 180 / Math.PI);
	}
	
}
