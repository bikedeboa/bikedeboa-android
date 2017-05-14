package com.bdb.bikedeboa.model.network.response;

import com.google.gson.annotations.SerializedName;

public class TagEntry {
	@SerializedName("id")
	public Integer id;
	@SerializedName("name")
	public String name;
	@SerializedName("createdAt")
	public String createdAt;
	@SerializedName("updatedAt")
	public String updatedAt;
}
