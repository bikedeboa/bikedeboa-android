package com.bdb.bikedeboa.model.network.response;

import com.google.gson.annotations.SerializedName;

public class LocalLight {

	@SerializedName("id")
	public Integer id;
	@SerializedName("lat")
	public Double latitude;
	@SerializedName("lng")
	public Double longitude;
	@SerializedName("average")
	public Double averageScore;
}
