package com.bdb.bikedeboa.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.location.Location;
import android.util.Pair;

import com.bdb.bikedeboa.model.manager.RackManager;
import com.bdb.bikedeboa.model.model.Rack;
import com.bdb.bikedeboa.util.AssetHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends BaseObservable implements RackManager.RackListCallback {

	private GoogleMap googleMap;
	private RackManager rackManager;
	private List<Marker> markerList;
	private float cameraZoom;
	private Location lastLocation;

	public MapViewModel(GoogleMap googleMap, Context context) {
		this.googleMap = googleMap;
		this.rackManager = RackManager.getInstance();
		this.cameraZoom = googleMap.getCameraPosition().zoom;
		this.markerList = new ArrayList<>();

		// Set listeners
		rackManager.setRackListCallback(this);

		// Fetch racks currently stored at the local db
		placeMarkers(rackManager.getRackList());
	}

	private void placeMarkers(List<Rack> rackList) {
		// Clean previous markers
		for (Marker marker : markerList) {
			marker.remove();
		}
		markerList.clear();

		// Add new
		for (Rack rack : rackList) {
			LatLng coords = new LatLng(rack.getLatitude(), rack.getLongitude());
			float rackScore = rack.getAverageRating();
			Marker marker = this.googleMap.addMarker(new MarkerOptions()
					.position(coords)
					.icon(AssetHelper.getCustomPin(rackScore, cameraZoom < 13))
					.zIndex(rackScore)); // Order z by rack average review
			marker.setTag(rack.getId()); // Use tag to identify rack
			markerList.add(marker);
		}
	}

	private void updatePinIcons(boolean mini) {

		for (Marker marker : markerList) {
			// Z index is the review average value
			marker.setIcon(AssetHelper.getCustomPin(marker.getZIndex(), mini));
		}
	}

	@Override
	public void onRackListUpdate(List<Rack> rackList) {
		placeMarkers(rackList);
	}

	public void onCameraMove() {

		float currentZoom = googleMap.getCameraPosition().zoom;
		if (currentZoom > 13.0 && this.cameraZoom <= 13.0) {
			// Normal pins
			updatePinIcons(false);
		} else if (currentZoom < 13.0 && this.cameraZoom >= 13.0) {
			// Mini pins
			updatePinIcons(true);
		}
		this.cameraZoom = currentZoom;
	}

	public Location getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}

	public void updateFilters(List<Pair<Float, Float>> ratingFilter, List<String> structureFilter, String accessFilter) {
		rackManager.updateFilters(ratingFilter, structureFilter, accessFilter);
	}
}
