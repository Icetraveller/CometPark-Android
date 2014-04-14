package com.icetraveller.android.apps.cometpark.io;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.icetraveller.android.apps.cometpark.io.model.Lot;
import com.icetraveller.android.apps.cometpark.io.model.Lots;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.icetraveller.android.apps.cometpark.provider.CometParkContract;
import com.icetraveller.android.apps.cometpark.utils.Lists;

import static com.icetraveller.android.apps.cometpark.utils.LogUtils.*;

public class LotsHandler extends JSONHandler {
	private static final String TAG = makeLogTag(LotsHandler.class);

	public LotsHandler(Context context) {
		super(context);
	}

	@Override
	public ArrayList<ContentProviderOperation> parse(String json)
			throws IOException {
		final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
		Lots lotsJson = new Gson().fromJson(json, Lots.class);
		for (Lot lot : lotsJson.lots) {
			parseLot(lot, batch);
		}
		return batch;
	}

	private static void parseLot(Lot lot,
			ArrayList<ContentProviderOperation> batch) {
		ContentProviderOperation.Builder builder = ContentProviderOperation
				.newInsert(CometParkContract.Lots.CONTENT_URI);
		builder.withValue(CometParkContract.Lots.ID, lot.id);
		builder.withValue(CometParkContract.Lots.NAME, lot.name);
		builder.withValue(CometParkContract.Lots.MAP_TILE_FILE, lot.filename);
		builder.withValue(CometParkContract.Lots.MAP_TILE_URL, lot.url);
		builder.withValue(CometParkContract.Lots.LOCATION_TOP_LEFT,
				lot.getInfo(lot.topLeft));
		builder.withValue(CometParkContract.Lots.LOCATION_TOP_RIGHT,
				lot.getInfo(lot.topRight));
		builder.withValue(CometParkContract.Lots.LOCATION_BOTTOM_LEFT,
				lot.getInfo(lot.bottomLeft));
		builder.withValue(CometParkContract.Lots.LOCATION_BOTTOM_RIGHT,
				lot.getInfo(lot.bottomRight));
		builder.withValue(CometParkContract.Lots.STATUS, lot.status);
		batch.add(builder.build());
	}

}