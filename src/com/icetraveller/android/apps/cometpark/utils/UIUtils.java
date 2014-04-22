package com.icetraveller.android.apps.cometpark.utils;



import java.util.Arrays;

import com.icetraveller.android.apps.cometpark.Config;
import com.icetraveller.android.apps.cometpark.ui.MapActivity;

import android.content.Context;

public class UIUtils {
	
	public static Class getMapActivityClass(Context context) {
        return MapActivity.class;
    }
	
	public static String emptyAvailbleCountString(){
		int[] array = new int[Config.PERMIT_TYPE_SUM+1];
		String arrayStr =Arrays.toString(array);
		return arrayStr.substring(1, arrayStr.length()-1);
	}
	
}
