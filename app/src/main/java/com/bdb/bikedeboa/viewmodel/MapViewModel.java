package com.bdb.bikedeboa.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.bdb.bikedeboa.model.manager.RackManager;
import com.bdb.bikedeboa.model.model.Rack;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapViewModel extends BaseObservable implements RackManager.RackManagerCallback {

	private GoogleMap googleMap;
	private RackManager rackManager;

	@Bindable
	public boolean updateRacks;

	public MapViewModel(GoogleMap googleMap) {
		this.googleMap = googleMap;
		this.rackManager = RackManager.getInstance();

		rackManager.setRackManagerCallback(this);
		// Fetch racks currently stored at the local db
		placeMarkers(rackManager.getRackList());
	}

	private void placeMarkers(List<Rack> rackList) {

		for (Rack rack : rackList) {
			LatLng coords = new LatLng(rack.getLatitude(), rack.getLongitude());
			this.googleMap.addMarker(new MarkerOptions().position(coords));
		}
	}

	@Override
	public void onRackListUpdate(List<Rack> rackList) {
		placeMarkers(rackList);
	}

	@Override
	public void onRackUpdate(Rack rack) {

	}
}
