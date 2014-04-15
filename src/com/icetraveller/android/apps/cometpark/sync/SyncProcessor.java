package com.icetraveller.android.apps.cometpark.sync;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;

public class SyncProcessor extends AsyncTask<Context, Void, String>{
	
	public interface CallBacks{
		public void done();
	}
	
	CallBacks cb;
	
	Object mContext;
	
	public SyncProcessor(Object context){
		super();
		mContext = context;
		cb = (CallBacks) mContext;
	}
	public SyncProcessor(){
		super();
	}

	@Override
	protected String doInBackground(Context... params) {
		SyncHelper syncHelper = new SyncHelper(params[0]);
		try {
			syncHelper.performSync(SyncHelper.FLAG_SYNC_LOCAL | SyncHelper.FLAG_SYNC_REMOTE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
    protected void onPostExecute(String result) {
		if(cb!=null)
			cb.done();
    }
	
}

