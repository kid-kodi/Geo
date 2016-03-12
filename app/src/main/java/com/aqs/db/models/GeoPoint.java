package com.aqs.db.models;

public class GeoPoint {

	int id;
	int pacerelle_id;
	double longitude;
	double latitude;

	// constructors
	public GeoPoint() {

	}

	public GeoPoint(int pacerelle_id, double longitude, double latitude) {
		this.pacerelle_id = pacerelle_id;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public GeoPoint(int id, int pacerelle_id, double longitude, double latitude) {
		this.id = id;
		this.pacerelle_id = pacerelle_id;
		this.longitude = longitude;
		this.latitude = latitude;
	}

	// setter
	public void setId(int id) {
		this.id = id;
	}

	public void setPacerelle_id(int pacerelle_id) {
		this.pacerelle_id = pacerelle_id;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	// getter
	public int getId() {
		return this.id;
	}

	public int getPacerelle_id() {
		return this.pacerelle_id;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public double getLatitude() {
		return this.latitude;
	}
}
