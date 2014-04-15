package com.icetraveller.android.apps.cometpark.io;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.icetraveller.android.apps.cometpark.io.model.Spot;
import com.icetraveller.android.apps.cometpark.io.model.Spots;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.icetraveller.android.apps.cometpark.provider.CometParkContract;
import com.icetraveller.android.apps.cometpark.utils.Lists;

import static com.icetraveller.android.apps.cometpark.utils.LogUtils.*;

public class SpotsHandler extends JSONHandler {
	private static final String TAG = makeLogTag(SpotsHandler.class);
	
	public static final int CREATE = 1;
	public static final int UPDATE = 2;

	public SpotsHandler(Context context) {
		super(context);
	}

	@Override
	public ArrayList<ContentProviderOperation> parse(String json)
			throws IOException {
		final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
		Spots spotsJson = new Gson().fromJson(json, Spots.class);
		for (Spot spot : spotsJson.spots) {
			parseSpot(spot, batch);
		}
		return batch;
	}
	
	private void parseSpot(Spot spot,
			ArrayList<ContentProviderOperation> batch) {
		ContentProviderOperation.Builder builder = ContentProviderOperation
				.newInsert(CometParkContract.Spots.buildUri());
		builder.withValue(CometParkContract.Spots.ID, spot.id);
		builder.withValue(CometParkContract.Spots.LOT, spot.lot);
		builder.withValue(CometParkContract.Spots.TYPE, spot.permit_type);
		builder.withValue(CometParkContract.Spots.STATUS, spot.status);
		builder.withValue(CometParkContract.Spots.LAT, spot.lat);
		builder.withValue(CometParkContract.Spots.LNG, spot.lng);
		batch.add(builder.build());
	}
	
}
