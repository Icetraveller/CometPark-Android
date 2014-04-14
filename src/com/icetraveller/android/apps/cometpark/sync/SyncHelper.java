package com.icetraveller.android.apps.cometpark.sync;

import static com.icetraveller.android.apps.cometpark.utils.LogUtils.*;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

public class SyncHelper {
	private static final String TAG = makeLogTag(SyncHelper.class);

	private Context mContext;

	public SyncHelper(Context context) {
		mContext = context;
	}

	/**
	 * TODO this is used in home screen
	 */
	public static void requestManualSync() {
	}
}
