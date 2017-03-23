package com.bdb.bikedeboa.model.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Rack extends RealmObject {

	// Light description
	@PrimaryKey
	private int id;
	private double latitude, longitude;
	private double averageScore;
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
}