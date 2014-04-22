package com.icetraveller.android.apps.cometpark.sync;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

public class DataLoaderTask extends AsyncTask<Void, Void, Boolean> {

	public interface CallBacks{
		void onDataLoadComplete(Bundle savedInstanceState);
	}
	
	Context context;
	CallBacks cb;
	Bundle savedInstanceState;

	public DataLoaderTask(Context context,Bundle savedInstanceState) {
		super();
		this.context = context;
		cb = (CallBacks) context;
		this.savedInstanceState = savedInstanceState;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		SyncHelper syncHelper = new SyncHelper(context);
		try {
			syncHelper.performInitializationSync(SyncHelper.FLAG_SYNC_LOCAL|SyncHelper.FLAG_SYNC_REMOTE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		cb.onDataLoadComplete(savedInstanceState);
	}


}
