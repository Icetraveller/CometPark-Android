package com.icetraveller.android.apps.cometpark.utils;



import com.icetraveller.android.apps.cometpark.ui.MapActivity;

import android.content.Context;

public class UIUtils {
	
	public static Class getMapActivityClass(Context context) {
//        if (UIUtils.isHoneycombTablet(context)) {
//            return MapMultiPaneActivity.class;
//        }

        return MapActivity.class;
    }
	
}
