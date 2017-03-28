package com.bdb.bikedeboa.model.manager;

import android.util.Log;

import com.bdb.bikedeboa.model.model.Rack;
import com.bdb.bikedeboa.model.network.response.LocalLight;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RackManager {

	private final String TAG = "RackManager";
	private static RackManager instance;
	private RackManagerCallback rackManagerCallback;
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

	public void setRackManagerCallback(RackManagerCallback rackManagerCallback) {
		this.rackManagerCallback = rackManagerCallback;
	}

	public List<Rack> getRackList() {
		return rackList;
	}

	private void updateRackList() {

		rackList.clear();
		rackList.addAll(realm.where(Rack.class).findAll());
		if (rackManagerCallback != null) {
			rackManagerCallback.onRackListUpdate(rackList);
		}
	}

	// Network requests
	public void fetchLocalLightList() {
		NetworkManager.getLocalLightList(localLightCallback);
	}

	// Network requests callbacks
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

	public interface RackManagerCallback {

		void onRackListUpdate(List<Rack> rackList);

		void onRackUpdate(Rack rack);
	}
}
