package com.bdb.bikedeboa.model.manager;

import com.bdb.bikedeboa.model.network.Service;
import com.bdb.bikedeboa.model.network.response.LocalLight;
import com.bdb.bikedeboa.model.network.response.Token;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class NetworkManager {

	private static Service service;

	private NetworkManager() {
	}

	public static void init() {

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://bdb-api.herokuapp.com/")
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		service = retrofit.create(Service.class);
	}

	public static void getLocalLightList(Callback<List<LocalLight>> callback) {

		Call<List<LocalLight>> localListCall = service.getLocalsLight();
		localListCall.enqueue(callback);
	}

	public static void getAuthToken(Callback<Token> callback, String username, String password) {

		Call<Token> tokenCall = service.getAuthToken(username, password);
		tokenCall.enqueue(callback);
	}

}
