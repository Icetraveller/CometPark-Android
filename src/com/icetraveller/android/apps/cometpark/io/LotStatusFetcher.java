package com.icetraveller.android.apps.cometpark.io;

import static com.icetraveller.android.apps.cometpark.utils.LogUtils.makeLogTag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.ContentProviderOperation;
import android.util.Log;

import com.google.gson.Gson;
import com.icetraveller.android.apps.cometpark.Config;
import com.icetraveller.android.apps.cometpark.io.model.LotStatus;
import com.icetraveller.android.apps.cometpark.io.model.LotStatuss;
import com.icetraveller.android.apps.cometpark.io.model.Spot;
import com.icetraveller.android.apps.cometpark.io.model.Spots;
import com.icetraveller.android.apps.cometpark.provider.CometParkContract;
import com.icetraveller.android.apps.cometpark.utils.Lists;
import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;

public class LotStatusFetcher {
	private static final String TAG = makeLogTag(LotStatusFetcher.class);
	public ArrayList<ContentProviderOperation> fetchAndParse()
			throws IOException {
		String url = Config.SERVER_URL + "/request";
		BasicHttpClient httpClient = new BasicHttpClient();
		ParameterMap params = null;
		params = httpClient.newParams().add("" + Config.TYPE,
				"" + Config.TYPE_REQUEST_LOTS_STATUS);
		HttpResponse httpResponse = httpClient.post(url, params);
		String jsonMessage = httpResponse.getBodyAsString();
		
		final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
		LotStatuss lotStatussJson = new Gson().fromJson(jsonMessage, LotStatuss.class);
		for (LotStatus lotStatus : lotStatussJson.lot_status_info) {
			updateLotStatus(lotStatus, batch);
		}
		return batch;
	}

	private void updateLotStatus(LotStatus lotStatus,
			ArrayList<ContentProviderOperation> batch) {
		ContentProviderOperation.Builder builder = ContentProviderOperation
				.newUpdate(CometParkContract.LotStatus.CONTENT_URI);
		String array =Arrays.toString(lotStatus.getAvailableSpotsCount());
		array = array.substring(1, array.length()-1);
		String id = lotStatus.getLotId();
		Log.d(TAG, "array:" + array + " id: " + id);
		builder.withValue(CometParkContract.LotStatus.AVAILABLE_SPOTS_COUNT, array)
		.withSelection(CometParkContract.LotStatus.ID +"=?", new String[] {id});
		batch.add(builder.build());
		
	}
}
