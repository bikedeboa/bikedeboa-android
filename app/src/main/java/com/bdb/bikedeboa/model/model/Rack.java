package com.bdb.bikedeboa.model.model;

import com.bdb.bikedeboa.model.network.response.LocalLight;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Rack extends RealmObject {

	// Light description
	@PrimaryKey
	private int id;
	private double latitude, longitude;
	private float averageScore;
	// Complete description
	private String text;
	private String structureType;
	private boolean isPublic;
	private String photoUrl;
	private String description;
	private String address;
//	private List<Review> reviewList;
//	private List<Tags> TagList;
	private int checkIns;

	public Rack() {}

	public Rack(LocalLight localLight) {
		this.id = localLight.id;
		this.latitude = localLight.latitude;
		this.longitude = localLight.longitude;
		this.averageScore = localLight.averageScore != null ? localLight.averageScore : 0;
	}

	public int getId() {
		return id;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public float getAverageScore() {
		return averageScore;
	}

	public String getText() {
		return text;
	}

	public String getStructureType() {
		return structureType;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public String getDescription() {
		return description;
	}

	public String getAddress() {
		return address;
	}

	public int getCheckIns() {
		return checkIns;
	}
}