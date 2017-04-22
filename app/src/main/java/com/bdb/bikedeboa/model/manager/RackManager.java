package com.bdb.bikedeboa.model.manager;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.bdb.bikedeboa.model.model.Rack;
import com.bdb.bikedeboa.model.network.response.LocalFull;
import com.bdb.bikedeboa.model.network.response.LocalLight;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RackManager {

	private static final String TAG = RackManager.class.getSimpleName();
	private static RackManager instance;
	private RackListCallback rackListCallback;
	private SingleRackCallback singleRackCallback;
	private Realm realm;
	private Context context;
	private List<Rack> rackList;
	// Filters
	List<Pair<Float, Float>> ratingRangeFilter;
	String accessFilter = "";
	List<String> structureTypeFilter;

	private RackManager(Context context) {

		this.context = context;
		rackList = new ArrayList<>();
		ratingRangeFilter = new ArrayList<>();
		structureTypeFilter = new ArrayList<>();
		// Just for testing
//		accessFilter = "true";
//		structureTypeFilter.add("uinvertido");
//		ratingRangeFilter.add(new Pair<Float, Float>(3.5f, 5f));

		realm = Realm.getDefaultInstance();
		// Update rack list to match db on instantiation
		updateRackList();
	}

	public static RackManager init(Context context) {

		if (instance == null) {
			instance = new RackManager(context);
		}
		return instance;
	}

	public static RackManager getInstance() {
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

		// Build lazy query
		RealmQuery<Rack> query = realm.where(Rack.class);

		if (!accessFilter.equals("")) {
			// isPublic values can be "true", "false", or "" (info not available)
			// Default behaviour will be always bring "" if filtering -- we can change it later
			query.beginGroup()
					.equalTo("isPublic", accessFilter)
					.or()
					.equalTo("isPublic", "")
					.endGroup();
		}

		if (!structureTypeFilter.isEmpty()) {
			query.in("structureType", structureTypeFilter.toArray(new String[structureTypeFilter.size()]));
		}

		if (!ratingRangeFilter.isEmpty()) {
			query.beginGroup();
			for (int i = 0; i < ratingRangeFilter.size(); ++i) {
				Pair<Float, Float> range = ratingRangeFilter.get(i);
				query.between("averageRating", range.first, range.second);
				if (i + 1 < ratingRangeFilter.size()) {
					query.or();
				}
			}
			query.endGroup();
		}

		rackList.addAll(query.findAll());

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
					Rack rack = realm.where(Rack.class)
							.equalTo("id", localLight.id)
							.findFirst();

					// Add to realm if rack is new and avoid overriding complete racks
					if (rack == null || !rack.isComplete()) {
						realm.copyToRealmOrUpdate(new Rack(localLight));
					}
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

				if (rack != null) {
					realm.beginTransaction();
					rack.completeRack(localFull, context);
					realm.commitTransaction();

					if (singleRackCallback != null) {
						RackManager.this.singleRackCallback.onRackUpdate(rack);
					}
				}
			}
		}

		@Override
		public void onFailure(Call<LocalFull> call, Throwable t) {
			Log.w(TAG, "Failure fetching /local/:id.");
		}
	};

	public void updateFilters(List<Pair<Float, Float>> ratingFilter, List<String> structureFilter, String accessFilter) {
		this.ratingRangeFilter = ratingFilter;
		this.structureTypeFilter = structureFilter;
		this.accessFilter = accessFilter;
		updateRackList();
	}

	public interface RackListCallback {
		void onRackListUpdate(List<Rack> rackList);
	}

	public interface SingleRackCallback {
		void onRackUpdate(Rack rack);
	}
}
