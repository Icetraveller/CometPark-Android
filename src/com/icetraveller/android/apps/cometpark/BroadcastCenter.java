package com.icetraveller.android.apps.cometpark;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import static com.icetraveller.android.apps.cometpark.Config.*;

public class BroadcastCenter {
	public static void displayMessage(Context context, String message, int type) {
    	Log.d("Config", "received");
    	Intent intent = null;
    	switch(type){
    	case BROADCAST_SPOTS_STATUS_UPDATE:
    		intent = new Intent(UPDATE_SPOTS_ACTION);
            intent.putExtra(EXTRA_MESSAGE, message);
    		break;
    	case BROADCAST_VIEW_LOT:
    		intent = new Intent(VIEW_LOT_ACTION);
            intent.putExtra(EXTRA_MESSAGE, message);
    		break;
//    	case BROADCAST_UPDATE_PERMIT_PREF:
//    		intent = new Intent(ACTION_UPDATE_PERMIT_PREF);
//            intent.putExtra(EXTRA_MESSAGE, message);
//    		break;
    	}
    		
        if(intent != null){
        	context.sendBroadcast(intent);
        }
    }
}
