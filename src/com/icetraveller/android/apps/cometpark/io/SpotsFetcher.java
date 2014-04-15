package com.icetraveller.android.apps.cometpark.io;

import java.io.IOException;
import java.util.ArrayList;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;
import com.icetraveller.android.apps.cometpark.io.model.Spot;
import com.icetraveller.android.apps.cometpark.io.model.Spots;
import com.icetraveller.android.apps.cometpark.provider.CometParkContract;
import com.icetraveller.android.apps.cometpark.utils.App;
import com.icetraveller.android.apps.cometpark.utils.Lists;
import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.util.Log;

import static com.icetraveller.android.apps.cometpark.utils.LogUtils.*;

public class SpotsFetcher {

	private static final String TAG = makeLogTag(SpotsFetcher.class);

	private Context mContext;
	private String lotId = "0";

	public SpotsFetcher(Context context, String lotId) {
		mContext = context;
		this.lotId = lotId;
	}

	public ArrayList<ContentProviderOperation> fetchAndParse()
			throws IOException {
		final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
		String url = App.SERVER_URL + "request";

		BasicHttpClient httpClient = new BasicHttpClient();
		ParameterMap params = httpClient.newParams()
				.add("" + App._TYPE, "" + App.TYPE_REQUEST_SPOTS_IN_LOT)
				.add(App.JSON_KEY_LOT, lotId);
		HttpResponse httpResponse = httpClient.post(url, params);
		String jsonMessage = httpResponse.getBodyAsString();
		return parse(jsonMessage);
	}

	public ArrayList<ContentProviderOperation> parse(String json)
			throws IOException {
		final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
		Spots spotsJson = new Gson().fromJson(json, Spots.class);
		for (Spot spot : spotsJson.spots) {
			updateSpot(spot, batch);
		}
		return batch;
	}
	
	public void updateSpot(Spot spot,
			ArrayList<ContentProviderOperation> batch) {
		ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(CometParkContract.Spots.CONTENT_URI);
		builder.withValue(CometParkContract.Spots.STATUS, spot.status).withSelection(CometParkContract.Spots.ID+"=?", new String[]{spot.id});
		Log.d(TAG, "spot:"+spot.id+" status: "+spot.status);
		batch.add(builder.build());
	}

}
