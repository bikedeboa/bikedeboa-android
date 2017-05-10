package com.bdb.bikedeboa.model.manager;

import android.content.res.Resources;

import com.bdb.bikedeboa.R;
import com.bdb.bikedeboa.model.network.Service;
import com.bdb.bikedeboa.model.network.response.LocalFull;
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

	public static void init(Resources resources) {

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(resources.getString(R.string.api_address))
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		service = retrofit.create(Service.class);
	}

	public static void getLocalLightList(Callback<List<LocalLight>> callback) {

		Call<List<LocalLight>> call = service.getLocalsLight();
		call.enqueue(callback);
	}

	public static void getAuthToken(Callback<Token> callback, String username, String password) {

		Call<Token> call = service.getAuthToken(username, password);
		call.enqueue(callback);
	}

	public static void getLocalFull(Callback<LocalFull> callback, int rackId, String authKey) {

		Call<LocalFull> call = service.getLocalFull(rackId, authKey);
		call.enqueue(callback);
	}

}
