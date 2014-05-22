package com.icetraveller.android.apps.cometpark.io.model;

public class LotStatus {
	private String lotId;
	private int[] available_spots_count;
	
	public int[] getAvailableSpotsCount() {
		return available_spots_count;
	}
	public void setAvailableSpotsCount(int[] availableSpotsCount) {
		this.available_spots_count = availableSpotsCount;
	}
	public String getLotId() {
		return lotId;
	}
	public void setLotId(String lotId) {
		this.lotId = lotId;
	}
}