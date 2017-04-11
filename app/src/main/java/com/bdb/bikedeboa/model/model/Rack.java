package com.bdb.bikedeboa.model.model;

import android.content.Context;
import android.net.Uri;

import com.bdb.bikedeboa.R;
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
	// Complete description
	private String text;
	private String structureType;
	private String structureTypeImage;
	private String ownership; // Public, private or unknown (i.e., null)
	private String ownershipImage;
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
	}

	public void completeRack(LocalFull localFull, Context context) {

		this.text = localFull.text;
		this.photoUrl = localFull.photo;
		this.description = localFull.description;
		this.address = localFull.address;
		this.checkInNumber = localFull.checkIns;
		this.reviewNumber = localFull.reviews;

		if (localFull.structureType != null) {
			switch (localFull.structureType) {
				case "uinvertido":
					this.structureType = context.getString(R.string.uinvertido);
					this.structureTypeImage = Uri.parse("android.resource://com.bdb.bikedeboa/drawable/tipo_uinvertido").toString();
					break;
				case "deroda":
					this.structureType = context.getString(R.string.deroda);
					this.structureTypeImage = Uri.parse("android.resource://com.bdb.bikedeboa/drawable/tipo_deroda").toString();
					break;
				case "trave":
					this.structureType = context.getString(R.string.trave);
					this.structureTypeImage = Uri.parse("android.resource://com.bdb.bikedeboa/drawable/tipo_trave").toString();
					break;
				case "suspenso":
					this.structureType = context.getString(R.string.suspenso);
					this.structureTypeImage = Uri.parse("android.resource://com.bdb.bikedeboa/drawable/tipo_suspenso").toString();
					break;
				case "grade":
					this.structureType = context.getString(R.string.grade);
					this.structureTypeImage = Uri.parse("android.resource://com.bdb.bikedeboa/drawable/tipo_grade").toString();
					break;
				case "other":
					this.structureType = context.getString(R.string.outro);
					this.structureTypeImage = Uri.parse("android.resource://com.bdb.bikedeboa/drawable/tipo_other").toString();
					break;
				default:
					this.structureType = null;
					this.structureTypeImage = null;
					break;
			}
		}

		if (localFull.isPublic != null) {
			switch (localFull.isPublic) {
				case "true":
					this.ownership = context.getString(R.string.public_rack);
					this.ownershipImage = Uri.parse("android.resource://com.bdb.bikedeboa/drawable/icon_public").toString();
					break;
				case "false":
					this.ownership = context.getString(R.string.private_rack);
					this.ownershipImage = Uri.parse("android.resource://com.bdb.bikedeboa/drawable/icon_private").toString();
					break;
				default:
					this.ownership = null;
					this.ownershipImage = null;
					break;
			}
		}

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

	public String getStructureTypeImage() {
		return structureTypeImage;
	}

	public String getOwnership() {
		return ownership;
	}

	public String getOwnershipImage() {
		return ownershipImage;
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