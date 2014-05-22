package com.icetraveller.android.apps.cometpark.ui;

import java.util.HashMap;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.icetraveller.android.apps.cometpark.R;
import com.icetraveller.android.apps.cometpark.ui.MapFragment.MarkerModel;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class MapInfoWindowAdapter implements InfoWindowAdapter {
	// Common parameters
	private String lotName;
	private Marker mMarker;
	private String contentString;
	private View mView = null;
	private LayoutInflater mInflater;
	private Resources mResources;

	private HashMap<String, MarkerModel> mMarkers;

	public MapInfoWindowAdapter(LayoutInflater inflater, Resources resources,
			HashMap<String, MarkerModel> markers) {
		this.mInflater = inflater;
		this.mResources = resources;
		mMarkers = markers;
	}

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		 if (mMarker != null && mMarker.getTitle().equals(marker.getTitle())) {
			 return renderLot(marker);
		 }
		return null;
	}

	private View renderLot(Marker marker) {
		if (mView == null) {
			mView = mInflater.inflate(R.layout.map_info_lot, null);
		}
		TextView lotNameTextView = (TextView) mView
				.findViewById(R.id.map_info_lottitle);
		lotNameTextView.setText(lotName);

		TextView first = (TextView) mView
				.findViewById(R.id.map_info_session_now);
		first.setText("Possibility:");
		TextView second = (TextView) mView
				.findViewById(R.id.map_info_session_next);
		second.setText(contentString);
		View spacer = mView.findViewById(R.id.map_info_session_spacer);

		// default visibility
		first.setVisibility(View.VISIBLE);
		second.setVisibility(View.VISIBLE);
		spacer.setVisibility(View.VISIBLE);
//		first.setVisibility(View.GONE);
//		second.setVisibility(View.GONE);
//		spacer.setVisibility(View.GONE);

		return mView;
	}
	
	public void clearData() {
        this.lotName = null;
        this.mMarker = null;
    }
	
	public void setLotData(Marker marker, String lotName, String contentString){
		this.lotName = lotName;
		mMarker = marker;
		this.contentString = contentString;
	}

}
