package com.bdb.bikedeboa.model.manager;

import com.bdb.bikedeboa.model.network.Service;
import com.bdb.bikedeboa.model.network.response.LocalLight;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class NetworkManager {

	private static Retrofit retrofit;
	private static Service service;

	private NetworkManager() {
	}

	public static void init() {
		retrofit = new Retrofit.Builder()
				.baseUrl("https://bikedeboa-api.herokuapp.com/")
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		service = retrofit.create(Service.class);
		Call<List<LocalLight>> localListCall = service.getLocalsLight();
		localListCall.enqueue(new Callback<List<LocalLight>>() {
			@Override
			public void onResponse(Call<List<LocalLight>> call, Response<List<LocalLight>> response) {
				response.body();
			}

			@Override
			public void onFailure(Call<List<LocalLight>> call, Throwable t) {
				t.getCause();
			}
		});
	}
}
