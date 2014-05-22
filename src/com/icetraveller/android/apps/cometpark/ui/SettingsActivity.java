package com.icetraveller.android.apps.cometpark.ui;

import com.icetraveller.android.apps.cometpark.R;
import com.icetraveller.android.apps.cometpark.utils.PreferenceHelper;
import com.icetraveller.android.apps.cometpark.utils.UIUtils;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

	
	private static final int DEFAULT_VALUE = 1;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		final String[] stringArray = this.getResources().getStringArray(
				R.array.pref_permit_type_entry);

		ListPreference listPreference = (ListPreference) findPreference(PreferenceHelper.PREF_KEY_PERMIT_TYPE);
		if (listPreference.getValue() == null) {
			// to ensure we don't get a null value
			// set first value by default
			listPreference.setValueIndex(DEFAULT_VALUE);
		}
		int index = parse(listPreference.getValue().toString());

		listPreference.setSummary(stringArray[index]);
		listPreference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						preference.setSummary(stringArray[parse(newValue.toString())]);
						return true;
					}
				});
		
	}
	
	private int parse(String i){
		try{
			return Integer.parseInt(i);
		}catch(NumberFormatException e){
			return DEFAULT_VALUE;
		}
	}
}