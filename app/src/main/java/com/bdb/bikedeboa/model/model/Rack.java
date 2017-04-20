package com.bdb.bikedeboa.model.model;

import android.content.Context;

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
	private float averageRating;
	private String structureType;
	private String isPublic; // Public, private or unknown (i.e., null)
	// Complete description
	private String text;
	private String photoUrl;
	private String description;
	private String address;
	private int checkInNumber;
	private int reviewNumber;
	//	private RealmList<Review> reviewList;
	private RealmList<Tag> tagList = new RealmList<>();
	private boolean isComplete = false;

	public Rack() {
	}

	public Rack(LocalLight localLight) {
		this.id = localLight.id;
		this.latitude = localLight.lat;
		this.longitude = localLight.lng;
		this.averageRating = localLight.average != null ? localLight.average : 0;
		this.structureType = localLight.structureType;
		this.isPublic = localLight.isPublic;
	}

	public void completeRack(LocalFull localFull, Context context) {

		this.text = localFull.text;
		this.photoUrl = localFull.photo;
		this.description = localFull.description;
		this.address = localFull.address;
		this.checkInNumber = localFull.checkIns;
		this.reviewNumber = localFull.reviews;

		this.tagList.clear();
		for (LocalFull.Tag tag : localFull.tags) {
			this.tagList.add(new Tag(tag.name, tag.count));
		}

		//this.reviewList -- needs extra request
		this.isComplete = true; // Doesn't consider if reviews were fetched -- second request needed
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

	public float getAverageRating() {
		return averageRating;
	}

	public String getText() {
		return text;
	}

	public String getStructureType() {
		return structureType;
	}

	public String isPublic() {
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
		return checkInNumber;
	}

	public int getReviewNumber() {
		return reviewNumber;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public RealmList<Tag> getTagList() {
		return tagList;
	}
}