package com.bdb.bikedeboa.model.network;

import com.bdb.bikedeboa.model.network.response.LocalLight;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Service {

	@GET("local/light")
	Call<List<LocalLight>> getLocalsLight();
}
