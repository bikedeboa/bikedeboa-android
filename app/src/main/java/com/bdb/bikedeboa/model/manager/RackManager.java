package com.bdb.bikedeboa.model.manager;

import android.util.Log;

import com.bdb.bikedeboa.model.model.Rack;
import com.bdb.bikedeboa.model.network.response.LocalFull;
import com.bdb.bikedeboa.model.network.response.LocalLight;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RackManager {

	private static final String TAG = RackManager.class.getSimpleName();
	private static RackManager instance;
	private RackListCallback rackListCallback;
	private SingleRackCallback singleRackCallback;
	private Realm realm;
	private List<Rack> rackList;

	private RackManager() {

		rackList = new ArrayList<>();
		realm = Realm.getDefaultInstance();
		// Update rack list to match db on instantiation
		updateRackList();
	}

	public static RackManager getInstance() {

		if (instance == null) {
			instance = new RackManager();
		}
		return instance;
	}

	public void setRackListCallback(RackListCallback rackListCallback) {
		this.rackListCallback = rackListCallback;
	}

	public void setSingleRackCallback(SingleRackCallback singleRackCallback) {
		this.singleRackCallback = singleRackCallback;
	}

	public List<Rack> getRackList() {
		return rackList;
	}

	public Rack getRack(int rackId) {
		return realm.where(Rack.class)
				.equalTo("id", rackId)
				.findFirst();
	}

	private void updateRackList() {

		rackList.clear();
		rackList.addAll(realm.where(Rack.class).findAll());
		if (rackListCallback != null) {
			rackListCallback.onRackListUpdate(rackList);
		}
	}

	public void fetchLocalLightList() {
		NetworkManager.getLocalLightList(localLightCallback);
	}

	private Callback<List<LocalLight>> localLightCallback = new Callback<List<LocalLight>>() {
		@Override
		public void onResponse(Call<List<LocalLight>> call, Response<List<LocalLight>> response) {

			List<LocalLight> localLightList = response.body();
			if (localLightList != null) {
				realm.beginTransaction();
				for (LocalLight localLight : localLightList) {
					realm.copyToRealmOrUpdate(new Rack(localLight));
				}
				realm.commitTransaction();
				updateRackList();
			}
		}

		@Override
		public void onFailure(Call<List<LocalLight>> call, Throwable t) {
			Log.w(TAG, "Failure fetching /local/light.");
		}
	};

	public void fetchLocalFull(int rackId) {

		if (UserManager.isAuthenticated()) {
			NetworkManager.getLocalFull(localFullCallback, rackId, UserManager.getAuthKey());
		}
	}

	private Callback<LocalFull> localFullCallback = new Callback<LocalFull>() {
		@Override
		public void onResponse(Call<LocalFull> call, Response<LocalFull> response) {
			// Complete or update data of an already existing rack
			if (response != null) {

				LocalFull localFull = response.body();
				Rack rack = realm.where(Rack.class)
						.equalTo("id", localFull.id)
						.findFirst();

				realm.beginTransaction();
				rack.completeRack(localFull);
				realm.commitTransaction();

				if (singleRackCallback != null) {
					RackManager.this.singleRackCallback.onRackUpdate(rack);
				}
			}
		}

		@Override
		public void onFailure(Call<LocalFull> call, Throwable t) {
			Log.w(TAG, "Failure fetching /local/:id.");
		}
	};

	public interface RackListCallback {
		void onRackListUpdate(List<Rack> rackList);
	}

	public interface SingleRackCallback {
		void onRackUpdate(Rack rack);
	}
}
