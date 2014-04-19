package com.icetraveller.android.apps.cometpark.sync;

import static com.icetraveller.android.apps.cometpark.utils.LogUtils.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.icetraveller.android.apps.cometpark.Config;
import com.icetraveller.android.apps.cometpark.R;
import com.icetraveller.android.apps.cometpark.io.JSONHandler;
import com.icetraveller.android.apps.cometpark.io.LotStatusFetcher;
import com.icetraveller.android.apps.cometpark.io.LotsFetcher;
import com.icetraveller.android.apps.cometpark.io.LotsHandler;
import com.icetraveller.android.apps.cometpark.io.SpotsFetcher;
import com.icetraveller.android.apps.cometpark.io.SpotsHandler;
import com.icetraveller.android.apps.cometpark.provider.CometParkContract;
import com.icetraveller.android.apps.cometpark.utils.Lists;
import com.icetraveller.android.apps.cometpark.utils.MapUtils;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParseException;
import com.larvalabs.svgandroid.SVGParser;
import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.HttpResponse;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;

/**
 * Please notice this sync does not do a account sync, it just access server
 * using http method
 * 
 * @author yue
 * 
 */
public class SyncHelper {
	public static final int FLAG_SYNC_LOCAL = 0x1; // sync with the resources in
													// apk
	public static final int FLAG_SYNC_REMOTE = 0x2; // sync with server resources
	public static final int FLAG_SIMPLE_SYNC = 0x4; //update spots only

	private static final int LOCAL_VERSION_CURRENT = 1;

	private static final String TAG = makeLogTag(SyncHelper.class);

	public static final String PREF_KEY_LOCAL_DATA_VERSION = "local_data_version";

	private Context mContext;

	public SyncHelper(Context context) {
		mContext = context;
	}

	/**
	 * TODO this is used in home screen
	 * This sync should sync whole lot status data.
	 * The data is suppose to be sync when rank list resume
	 */
	public static void requestManualSync() {
	}

	/**
	 * This sync is to create lots spots data
	 * @param flags
	 *            can be FLAG_SYNC_LOCAL, FLAG_SYNC_REMOTE or both
	 */
	public void performInitializationSync(int flags) throws IOException {
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		final int localVersion = prefs.getInt(PREF_KEY_LOCAL_DATA_VERSION, 0);

		// Bulk of sync work, performed by executing several fetches from
		// local and online sources.

		final ContentResolver resolver = mContext.getContentResolver();
		ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
		
		if ((flags & FLAG_SYNC_LOCAL) != 0) {// perform a local sync
			LOGI(TAG, "Performing  local sync");
			final long startLocal = System.currentTimeMillis();
			final boolean localParse = localVersion < LOCAL_VERSION_CURRENT;
			LOGD(TAG, "found localVersion=" + localVersion
					+ " and LOCAL_VERSION_CURRENT=" + LOCAL_VERSION_CURRENT);
			if (localParse) {
				LOGI(TAG, "Local syncing lots");
				LotsHandler lotsHandler = new LotsHandler(mContext);
				batch.addAll(lotsHandler.parse(JSONHandler.parseResource(
						mContext, R.raw.lots)));
				LOGI(TAG, "Local syncing spots");
				batch.addAll(new SpotsHandler(mContext).parse(JSONHandler
						.parseResource(mContext, R.raw.spots)));
				syncMapTiles(lotsHandler.getTileMap());

				prefs.edit()
						.putInt(PREF_KEY_LOCAL_DATA_VERSION,
								LOCAL_VERSION_CURRENT).commit();
			}
			LOGD(TAG, "Local sync took "
					+ (System.currentTimeMillis() - startLocal) + "ms");

			try {
				// Apply all queued up batch operations for local data.
				resolver.applyBatch(CometParkContract.CONTENT_AUTHORITY, batch);
			} catch (RemoteException e) {
				throw new RuntimeException("Problem applying batch operation",
						e);
			} catch (OperationApplicationException e) {
				throw new RuntimeException("Problem applying batch operation",
						e);
			}

			// refresh batch
			batch = new ArrayList<ContentProviderOperation>();
		}
		
		/**
		 * Should query remote data version before sync
		 */
		 if ((flags & FLAG_SYNC_REMOTE) != 0 && isOnline()) {
			 LOGI(TAG, "Performing  remote sync");
			 
			 //TODO should query server for data version before any sync
			 batch.addAll(new LotsFetcher(mContext).fetchAndParse());
			 batch.addAll(new LotStatusFetcher().fetchAndParse());
			 batch.addAll(new SpotsFetcher(mContext).fetchAndParse(Config.TYPE_REQUEST_SPOTS_INFO));
			 
			 //lots
		 }
		 
		 // Apply all queued up remaining batch operations (only remote content at this point).
         try {
			resolver.applyBatch(CometParkContract.CONTENT_AUTHORITY, batch);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

	private void syncMapTiles(HashMap<String, String> tileMap)
			throws IOException, SVGParseException {
		// keep track of used files, unused files are removed
		ArrayList<String> usedTiles = Lists.newArrayList();
		Iterator<String> iterator = tileMap.keySet().iterator();
		while (iterator.hasNext()) {
			String filename = iterator.next();
			String url = tileMap.get(filename);

			usedTiles.add(filename);
			if (!MapUtils.hasTile(mContext, filename)) {
				// copy or download the tile if it is not stored yet
				if (MapUtils.hasTileAsset(mContext, filename)) {
					// file already exists as an asset, copy it
					MapUtils.copyTileAsset(mContext, filename);
				} else {
					// download the file
					File tileFile = MapUtils.getTileFile(mContext, filename);
					BasicHttpClient httpClient = new BasicHttpClient();
					// httpClient.setRequestLogger(mQuietLogger);
					HttpResponse httpResponse = httpClient.get(url, null);
					writeFile(httpResponse.getBody(), tileFile);

					// ensure the file is valid SVG
					InputStream is = new FileInputStream(tileFile);
					SVGParser.getSVGFromInputStream(is);
					is.close();
				}
			}
		}
		MapUtils.removeUnusedTiles(mContext, usedTiles);
	}

	/**
	 * Write the byte array directly to a file.
	 * 
	 * @throws IOException
	 */
	private void writeFile(byte[] data, File file) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(file, false));
		bos.write(data);
		bos.close();
	}

}
