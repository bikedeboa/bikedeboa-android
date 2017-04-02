package com.bdb.bikedeboa.model.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// Response of /local/:id call
public class LocalFull {

	@SerializedName("id")
	public Integer id;
	@SerializedName("lat")
	public Double lat;
	@SerializedName("lng")
	public Double lng;
	@SerializedName("structureType")
	public String structureType;
	@SerializedName("isPublic")
	public Boolean isPublic;
	@SerializedName("text")
	public String text;
	@SerializedName("photo")
	public String photo;
	@SerializedName("description")
	public String description;
	@SerializedName("address")
	public String address;
	@SerializedName("createdAt")
	public String createdAt;
	@SerializedName("reviews")
	public String reviews;
	@SerializedName("checkins")
	public int checkIns;
	@SerializedName("tags")
	public List<Tag> tags = null;

	public class Tag {

		@SerializedName("name")
		public String name;
		@SerializedName("count")
		public int count;
	}
}