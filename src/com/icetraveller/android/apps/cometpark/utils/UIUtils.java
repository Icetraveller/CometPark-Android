package com.icetraveller.android.apps.cometpark.utils;

import java.util.Arrays;

import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.targets.ViewTarget;
import com.icetraveller.android.apps.cometpark.Config;
import com.icetraveller.android.apps.cometpark.R;
import com.icetraveller.android.apps.cometpark.ui.MapActivity;

import android.app.Activity;
import android.content.Context;
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

	public static void showTutorial(Context context, View v) {
		boolean showTutorial = PreferenceHelper.getFirstTimeUser(context);
		if (showTutorial) {
			showTurorial(context, v);
			PreferenceHelper.setFirstTimeUser(context, false);
		}
	}

	private static void showTurorial(Context context, View v) {
		ShowcaseView.ConfigOptions mOptions = new ShowcaseView.ConfigOptions();
		mOptions.centerText = true;
		ShowcaseView.insertShowcaseView(new ViewTarget(v), (Activity)context, "Tip",
				"Help", mOptions);
	}

}
