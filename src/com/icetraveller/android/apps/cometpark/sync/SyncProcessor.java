package com.icetraveller.android.apps.cometpark.sync;

import java.io.IOException;

import com.icetraveller.android.apps.cometpark.io.LotStatusFetcher;

import android.content.Context;
import android.os.AsyncTask;

public class SyncProcessor extends AsyncTask<Integer, Void, String>{
	Context context;
	
	public SyncProcessor(Context context){
		super();
		this.context = context;
	}

	@Override
	protected String doInBackground(Integer... params) {
		SyncHelper syncHelper = new SyncHelper(context);
		try {
			syncHelper.performInitializationSync(params[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
}

