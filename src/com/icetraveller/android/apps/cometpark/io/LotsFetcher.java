package com.icetraveller.android.apps.cometpark.io;

import java.io.IOException;
import java.util.ArrayList;

import com.icetraveller.android.apps.cometpark.Config;
import com.icetraveller.android.apps.cometpark.utils.Lists;
import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;

import android.content.ContentProviderOperation;
import android.content.Context;

/**
 * This class for sync remotely
 * @author yue
 *
 */
public class LotsFetcher extends LotsHandler {
	
	private Context mContext;
	
	public LotsFetcher(Context context) {
		super(context);
		this.mContext = context;
	}
	
	public ArrayList<ContentProviderOperation> fetchAndParse()
			throws IOException {
		String url = Config.SERVER_URL + "/request";
		BasicHttpClient httpClient = new BasicHttpClient();
		ParameterMap params = null;
		params = httpClient.newParams().add("" + Config.TYPE,
				"" + Config.TYPE_REQUEST_LOTS_INFO);
		HttpResponse httpResponse = httpClient.post(url, params);
		String jsonMessage = httpResponse.getBodyAsString();
		return parse(jsonMessage);
	}

}
