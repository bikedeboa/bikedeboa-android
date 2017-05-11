package com.bdb.bikedeboa.model.network.response;

import com.google.gson.annotations.SerializedName;

public class LocalLight {

	@SerializedName("id")
	public Integer id;
	@SerializedName("lat")
	public Double lat;
	@SerializedName("lng")
	public Double lng;
	@SerializedName("average")
	public Float average;
	@SerializedName("structureType")
	public String structureType;
	@SerializedName("isPublic")
	public String isPublic;
	@SerializedName("photo")
	public String photoUrl;
	@SerializedName("text")
	public String text;

}
