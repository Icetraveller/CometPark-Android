package com.icetraveller.android.apps.cometpark.ui;

import com.icetraveller.android.apps.cometpark.Config;
import com.icetraveller.android.apps.cometpark.R;
import com.icetraveller.android.apps.cometpark.provider.CometParkContract;
import com.icetraveller.android.apps.cometpark.utils.CharacterDrawable;
import com.icetraveller.android.apps.cometpark.utils.LogUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RankAdapter extends CursorAdapter {
	private static final String TAG = LogUtils.makeLogTag(RankAdapter.class);

	private Activity mActivity;
	private boolean mIsDropDown;
	private int userPermitType;


	public RankAdapter(FragmentActivity activity, boolean isDropDown) {
		super(activity, null, 0);
		mActivity = activity;
		mIsDropDown = isDropDown;

		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(mActivity);
		String permitTypeString = prefs.getString(
				SettingsActivity.PREF_KEY_PERMIT_TYPE, "2");
		userPermitType = Integer.parseInt(permitTypeString);
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup parent) {
		return mActivity.getLayoutInflater().inflate(R.layout.item_row, parent,
				false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String id = cursor.getString(LotsStatusQuery.ID);
		String[] statusStrings = cursor.getString(
				LotsStatusQuery.AVAILABLE_SPOTS_COUNT).split(",");
		int status = countAvailableSpots(statusStrings);
		int max = Integer.parseInt(statusStrings[Config.PERMIT_TYPE_SUM].trim());
		float level = 0;
		if(max !=0){
			level = (float)status / max;
		}
		Log.d(TAG, "status= "+status+" max= "+max + " level = "+level);
		
		TextView primaryTextView,secondryTextView,levelTextView;
		
		primaryTextView = (TextView) view.findViewById(R.id.primaryLine);
		primaryTextView.setText(id);
		
		secondryTextView = (TextView) view.findViewById(R.id.secondLine);
		secondryTextView.setText(""+status);
		int c = Color.WHITE;
		levelTextView = (TextView) view.findViewById(R.id.level);
		if(level > 0.5){
			c = mActivity.getResources().getColor(R.color.level_easy);
			levelTextView.setText("Easy");
		}else if(level > 0.25){
			c = mActivity.getResources().getColor(R.color.level_avg);
			levelTextView.setText("Average");
		}else{
			c = mActivity.getResources().getColor(R.color.level_hard);
			levelTextView.setText("Hard");
		}
		
		ImageView icon = (ImageView) view.findViewById(R.id.icon);
		CharacterDrawable drawable = new CharacterDrawable(id.toCharArray()[0], c);
		icon.setImageDrawable(drawable); 
	}
	
	private int countAvailableSpots(String[] ss){
		int i = 0;
		int sum = 0;
		while( i <= userPermitType){
			sum =sum + Integer.parseInt(ss[i].trim());
			i++;
		}
		return sum;
		
	}

	public interface LotsStatusQuery {
		int _TOKEN = 0x1;

		String[] PROJECTION = { BaseColumns._ID,CometParkContract.LotStatus.ID,
				CometParkContract.LotStatus.AVAILABLE_SPOTS_COUNT };

		int _ID = 0;
		int ID = 1;
		int AVAILABLE_SPOTS_COUNT = 2;
	}

}
