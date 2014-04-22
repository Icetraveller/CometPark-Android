package com.icetraveller.android.apps.cometpark.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceHelper {

	public static final String PREF_KEY_FIRST_TIME_USER = "pref_key_first_time_user";
	public static final String PREF_KEY_PERMIT_TYPE = "pref_key_permit_type";
	public static final String PREF_KEY_DATA_LOADED = "pref_key_data_loaded";
	
	public static void setFirstTimeUser(Context context, boolean isFirstTimeUser){
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putBoolean(PREF_KEY_FIRST_TIME_USER, isFirstTimeUser);
		editor.apply();
	}
	
	public static boolean getFirstTimeUser(Context context){
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_FIRST_TIME_USER, false);
	}
	
	public static void setDataLoaded(Context context, boolean isDataLoaded){
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putBoolean(PREF_KEY_DATA_LOADED, isDataLoaded);
		editor.apply();
	}
	
	public static boolean getDataLoaded(Context context){
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_DATA_LOADED, false);
	}
}
