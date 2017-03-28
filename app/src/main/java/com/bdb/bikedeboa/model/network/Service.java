package com.bdb.bikedeboa.model.network;

import com.bdb.bikedeboa.model.network.response.LocalLight;
import com.bdb.bikedeboa.model.network.response.Token;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Service {

	@GET("local/light")
	Call<List<LocalLight>> getLocalsLight();

	@FormUrlEncoded
	@POST("token")
	Call<Token> getAuthToken(@Field("username") String username,
							 @Field("password") String password);
}
