package com.icetraveller.android.apps.cometpark.utils;

import java.util.Arrays;

import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.targets.ViewTarget;
import com.icetraveller.android.apps.cometpark.Config;
import com.icetraveller.android.apps.cometpark.R;
import com.icetraveller.android.apps.cometpark.ui.MapActivity;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

public class UIUtils {

	public static Class getMapActivityClass(Context context) {
		return MapActivity.class;
	}

	public static String emptyAvailbleCountString() {
		int[] array = new int[Config.PERMIT_TYPE_SUM + 1];
		String arrayStr = Arrays.toString(array);
		return arrayStr.substring(1, arrayStr.length() - 1);
	}
	
	 public static boolean isNetworkAvailable(Context context) {
	        boolean isAvailable;
	            isAvailable = isNetWorkAvailable(context);
	            if (!isAvailable) {
	                return false;
	            }
	        return true;
	    }
	 
	 static boolean isNetWorkAvailable(Context context) {

	        boolean ret = false;
	        if (context == null) {
	            return ret;
	        }
	        try {
	            ConnectivityManager connetManager = (ConnectivityManager) context
	                    .getSystemService(Context.CONNECTIVITY_SERVICE);
	            if (connetManager == null) {
	                return ret;
	            }
	            NetworkInfo[] infos = connetManager.getAllNetworkInfo();
	            if (infos == null) {
	                return ret;
	            }
	            for (int i = 0; i < infos.length && infos[i] != null; i++) {
	                if (infos[i].isConnected() && infos[i].isAvailable()) {
	                    ret = true;
	                    break;
	                }
	            }
	        } catch (Exception e) {

	            e.printStackTrace();
	        }
	        return ret;
	    }

}
