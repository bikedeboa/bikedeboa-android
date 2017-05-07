package com.bdb.bikedeboa.viewmodel;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.location.Location;
import android.util.Pair;

import com.android.databinding.library.baseAdapters.BR;
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

	private Resources res;
	private GoogleMap googleMap;
	private RackManager rackManager;
	private List<Marker> markerList;
	private float cameraZoom;
	private Location lastLocation;
	private Rack rack;
	private DetailViewModel detailViewModel;

	public MapViewModel(GoogleMap googleMap, Context context) {
		this.googleMap = googleMap;
		this.res = context.getResources();
		this.rackManager = RackManager.getInstance();
		this.cameraZoom = googleMap.getCameraPosition().zoom;
		this.markerList = new ArrayList<>();

		// Set listeners
		rackManager.setRackListCallback(this);

		// Fetch racks currently stored at the local db
		placeMarkers(rackManager.getRackList());
	}

//	public MapViewModel(int rackId, GoogleMap googleMap, Context context) {
//		this.googleMap = googleMap;
//		this.res = context.getResources();
//		this.rackManager = RackManager.getInstance();
//		this.cameraZoom = googleMap.getCameraPosition().zoom;
//		this.markerList = new ArrayList<>();
//		this.rack = this.rackManager.getRack(rackId);
//
//		// Set listeners
//		this.rackManager.setRackListCallback(this);
//		this.rackManager.setSingleRackCallback(this);
//
//		// Fetch racks currently stored at the local db
//		placeMarkers(rackManager.getRackList());
//
//		notifyPropertyChanged(BR._all);
//
//		fetchRackDetails();
//		// setUpMap();
//	}

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

	public void setDetailViewModel(DetailViewModel detailViewModel) {
		this.detailViewModel = detailViewModel;
		notifyPropertyChanged(BR._all);
	}

	public DetailViewModel getDetailViewModel() {
		return detailViewModel;
	}

	//	// DETAILS
//
//	public void fetchRackDetails() {
//		rackManager.fetchLocalFull(rack.getId());
//	}
//
//	@Override
//	public void onRackUpdate(Rack rack) {
//		notifyPropertyChanged(BR._all);
//	}
//
//	public String getTitle() {
//		return (rack != null) ? rack.getText() : null;
//	}
//
//	public String getAddress() {
//		return (rack != null) ? rack.getAddress() : null;
//	}
//
//	public String getImage() {
//		return (rack != null) ? rack.getPhotoUrl() : null;
//	}
//
//	public boolean hasImage() {
//		return rack.getPhotoUrl() != null && !rack.getPhotoUrl().equals("");
//	}
//
//	public List<Tag> getTagList() {
//		return (rack != null) ? rack.getTagList() : null;
//	}
//
//	public float getAverageRating() {
//		return (rack != null) ? rack.getAverageRating() : 0.0f;
//	}
//
//	public Double getLatitude() {
//		return (rack != null) ? rack.getLatitude() : 0.0;
//	}
//
//	public Double getLongitude() {
//		return (rack != null) ? rack.getLongitude() : null;
//	}
//
//	public String getAverageRatingString() {
//		// Avoid 4.0
//		if (rack != null) {
//			if (rack.getAverageRating() % 1 == 0) {
//				return String.format("%.0f", rack.getAverageRating());
//			} else {
//				return String.format("%.1f", rack.getAverageRating());
//			}
//		} else {
//			return null;
//		}
//	}
//
//	public boolean hasDescription() {
//		return rack != null && rack.getDescription() != null && !rack.getDescription().equals("");
//	}
//
//	public String getDescription() {
//		return (rack != null) ? rack.getDescription() : null;
//	}
//
//	public String getReviewNumberString() {
//		return (rack != null) ? res.getQuantityString(R.plurals.n_ratings, rack.getReviewNumber(), rack.getReviewNumber()) : null;
//	}
//
//	public boolean hasAccessAndType() {
//		return rack != null && rack.isPublic() != null && !rack.isPublic().equals("null") && rack.getStructureType() != null && !rack.getStructureType().equals("null");
//	}
//
//	public String getAccess() {
//		return (rack != null) ? AssetHelper.getAccessString(rack.isPublic()) : null;
//	}
//
//	public String getStructureType() {
//		return (rack != null) ? AssetHelper.getStructureTypeString(rack.getStructureType()) : null;
//	}
//
//	public Drawable getAccessImage() {
//		return (rack != null) ? AssetHelper.getAccessImage(rack.isPublic()) : null;
//	}
//
//	public Drawable getStructureTypeImage() {
//		return (rack != null) ? AssetHelper.getStructureTypeImage(rack.getStructureType()) : null;
//	}
//
//	private void setUpMap() {
//		if (rack != null) {
//			LatLng pinPosition = new LatLng(rack.getLatitude(), rack.getLongitude());
//			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pinPosition, 18));
//			googleMap.addMarker(new MarkerOptions()
//					.position(pinPosition)
//					.icon(AssetHelper.getCustomPin(rack.getAverageRating(), false)));
//		}
//	}
}
