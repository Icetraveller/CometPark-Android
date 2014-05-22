package com.icetraveller.android.apps.cometpark.io.model;

import com.icetraveller.android.apps.cometpark.utils.MapUtils;

public class Lot {
	public String id;
	public String name;
	public String filename;
	public String url;
	public int status;
	public Location topLeft = new Location();
	public Location topRight = new Location();
	public Location bottomLeft = new Location();
	public Location bottomRight = new Location();

//	public String getInfo(Location l) {
//		return l.locationInfo();
//	}
	
	public String getInfo(Location l){
		return l.convertToString();
	}

}

class Location {
	double lat;
	double lng;

	public String locationInfo() {
		double[] result = MapUtils.convertToProjection(lat, lng);
		return result[0] + "," + result[1];
	}
	
	public String convertToString(){
		return lat+","+lng;
	}
}
