package com.bdb.bikedeboa.model.model;

import com.bdb.bikedeboa.model.network.response.LocalFull;
import com.bdb.bikedeboa.model.network.response.LocalLight;

import io.realm.RealmList;
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
//	private RealmList<Review> reviewList;
	private RealmList<Tag> tagList = new RealmList<>();;
	private int checkIns;
	private boolean isComplete = false;

	public Rack() {}

	public Rack(LocalLight localLight) {
		this.id = localLight.id;
		this.latitude = localLight.lat;
		this.longitude = localLight.lng;
		this.averageScore = localLight.average != null ? localLight.average : 0;
	}

	public void completeRack(LocalFull localFull) {
		this.text = localFull.text;
		this.structureType = localFull.structureType;
		this.isPublic = localFull.isPublic;
		this.photoUrl = localFull.photo;
		this.description = localFull.description;
		this.address = localFull.address;
		this.checkIns = localFull.checkIns;
		//this.reviewList
		this.isComplete = true; // TODO: Doesn't consider if reviews were fetched -- second request needed
		for (LocalFull.Tag tag : localFull.tags) {
			this.tagList.add(new Tag(tag.name, tag.count));
		}
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

	public boolean isComplete() {
		return isComplete;
	}
}