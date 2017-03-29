package com.bdb.bikedeboa.model.manager;

import android.util.Log;

import com.bdb.bikedeboa.model.network.response.Token;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class UserManager {

	private static final String TAG = UserManager.class.getSimpleName();
	private static String authKey;

	public static void authenticate() {
		// I'll leave them hardcoded for now, like on the web version,
		// because I'm not sure about how the "Login Colaborador" works
		NetworkManager.getAuthToken(tokenCallback, "client", "deboanalagoa");
	}

	public static boolean isAuthenticated() {
		// Doesn't think about expired keys (yet) -- might not have to, tests needed
		return authKey != null;
	}

	public static String getAuthKey() {
		return authKey;
	}

	private static Callback<Token> tokenCallback = new Callback<Token>() {
		@Override
		public void onResponse(Call<Token> call, Response<Token> response) {
			if (response != null) {
				UserManager.authKey = response.body().token;
			}
		}

		@Override
		public void onFailure(Call<Token> call, Throwable t) {
			Log.w(TAG, "onFailure: Unable to fetch auth token.");
		}
	};
}
