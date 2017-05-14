package com.bdb.bikedeboa.model.manager;

import android.content.res.Resources;

import com.bdb.bikedeboa.R;
import com.bdb.bikedeboa.model.network.Service;
import com.bdb.bikedeboa.model.network.response.LocalFull;
import com.bdb.bikedeboa.model.network.response.LocalLight;
import com.bdb.bikedeboa.model.network.response.ReviewResponse;
import com.bdb.bikedeboa.model.network.response.TagEntry;
import com.bdb.bikedeboa.model.network.response.Token;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class NetworkManager {

	private static Service service;

	private NetworkManager() {
	}

	public static void init(Resources resources) {

		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		// set your desired log level
		logging.setLevel(HttpLoggingInterceptor.Level.BODY);

		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		// add your other interceptors …

		// add logging as last interceptor
		httpClient.addInterceptor(logging);  // <-- this is the important line!

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(resources.getString(R.string.api_address))
				.addConverterFactory(GsonConverterFactory.create())
				.client(httpClient.build())
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

	public static void submitRating(int rackId, int nStars, List<Integer> tagIds, String authKey, Callback<ReviewResponse> callback) {

		Collections.sort(tagIds);
		// Not good -- API isn't retrofit friendly
		Map<String, Integer> tags = new LinkedHashMap<>();
		for (int i = 0; i < tagIds.size(); ++i) {
			tags.put("tags[" + i + "][id]", tagIds.get(i));
		}

		Call<ReviewResponse> call = service.postReview(rackId, nStars, tags, authKey);
		call.enqueue(callback);
	}

	public static void getTagDict(Callback<List<TagEntry>> callback, String authKey) {

		Call<List<TagEntry>> call = service.getTagDitc(authKey);
		call.enqueue(callback);
	}
}
