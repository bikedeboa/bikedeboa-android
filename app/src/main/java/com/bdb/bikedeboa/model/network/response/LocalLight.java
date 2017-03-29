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
}
