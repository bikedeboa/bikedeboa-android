package com.bdb.bikedeboa.model.network.response;

import com.google.gson.annotations.SerializedName;

public class ReviewResponse {

	@SerializedName("id")
	private Integer reviewId;
	@SerializedName("description")
	private Object description;
	@SerializedName("rating")
	private Integer rating;
	@SerializedName("hour")
	private String hour;
	@SerializedName("date")
	private String date;
	@SerializedName("local_id")
	private Integer localId;
	@SerializedName("updatedAt")
	private String updatedAt;
	@SerializedName("createdAt")
	private String createdAt;

	public Integer getReviewId() {
		return reviewId;
	}

	public Integer getLocalId() {
		return localId;
	}
}
