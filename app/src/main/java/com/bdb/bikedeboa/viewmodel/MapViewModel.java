package com.bdb.bikedeboa.viewmodel;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.bdb.bikedeboa.R;
import com.bdb.bikedeboa.model.manager.RackManager;
import com.bdb.bikedeboa.model.model.Rack;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends BaseObservable implements RackManager.RackManagerCallback,
		GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener {

	private Context context;
	private Resources res;
	private GoogleMap googleMap;
	private RackManager rackManager;
	private List<Marker> markerList;
	private float cameraZoom;

	public MapViewModel(GoogleMap googleMap, Context context) {
		this.googleMap = googleMap;
		this.context = context;
		this.rackManager = RackManager.getInstance();
		this.cameraZoom = googleMap.getCameraPosition().zoom;
		this.markerList = new ArrayList<>();
		this.res = context.getResources();

		// Set listeners
		rackManager.setRackManagerCallback(this);
		googleMap.setOnMarkerClickListener(this);
		googleMap.setOnCameraMoveListener(this);

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
			float rackScore = rack.getAverageScore();
			Marker marker = this.googleMap.addMarker(new MarkerOptions()
					.position(coords)
					.icon(getCustomPin(rackScore, cameraZoom < 13))
					.zIndex(rackScore)); // Order z by rack average review
			marker.setTag(rack.getId()); // Use tag to identify rack
			markerList.add(marker);
		}
	}

	private void updatePinIcons(boolean mini) {

		for (Marker marker : markerList) {
			// Z index is the review average value
			marker.setIcon(getCustomPin(marker.getZIndex(), mini));
		}
	}

	private BitmapDescriptor getCustomPin(float rackScore, boolean mini) {

		// Select correct resource
		Drawable drawable = null;
		if (rackScore == 0) {
			if (mini) {
				drawable = res.getDrawable(R.drawable.pin_gray_mini);
			} else {
				drawable = res.getDrawable(R.drawable.pin_gray);
			}
		} else if (rackScore > 0 && rackScore <= 2) {
			if (mini) {
				drawable =	res.getDrawable(R.drawable.pin_red_mini);
			} else {
				drawable =	res.getDrawable(R.drawable.pin_red);
			}
		} else if (rackScore > 2 && rackScore < 3.5) {
			if (mini) {
				drawable = res.getDrawable(R.drawable.pin_yellow_mini);
			} else {
				drawable = res.getDrawable(R.drawable.pin_yellow);
			}
		} else if (rackScore >= 3.5) {
			if (mini) {
				drawable = res.getDrawable(R.drawable.pin_green_mini);
			} else {
				drawable = res.getDrawable(R.drawable.pin_green);
			}
		}

		float scale;
		int alpha;
		if (mini) {
			scale = rackScore == 0 ? 0.4f : 0.1f + (rackScore/10);
			alpha = (int) (255*0.7);
		} else {
			scale = rackScore == 0 ? 0.8f : 0.6f + (rackScore/10);
			alpha = (int) (255*0.9);
		}

		Bitmap bitmap;
		try {
			// Svg was way too big, that's why I'm dividing by seven
			// (mostly because I don't want to change the svgs too)
			bitmap = Bitmap.createBitmap(
					(int) (scale * drawable.getIntrinsicWidth()/7),
					(int) (scale * drawable.getIntrinsicHeight()/7),
					Bitmap.Config.ARGB_4444);

			Canvas canvas = new Canvas(bitmap);
			drawable.setAlpha(alpha);
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			drawable.draw(canvas);
		} catch (OutOfMemoryError e) {
			return null;
		}
		return BitmapDescriptorFactory.fromBitmap(bitmap);
	}

	@Override
	public void onRackListUpdate(List<Rack> rackList) {
		placeMarkers(rackList);
	}

	@Override
	public void onRackUpdate(Rack rack) {
		// notifyPropertyChanged... when the detail fragment is implemented
	}

	@Override
	public boolean onMarkerClick(Marker marker) {

		int rackId = (int) marker.getTag();
		rackManager.fetchLocalFull(rackId);
		// Return false if we want the camera to move to the marker and an info window to appear
		return true;
	}

	@Override
	public void onCameraMove() {

		float currentZoom = googleMap.getCameraPosition().zoom;
		if(currentZoom > 13.0 && this.cameraZoom <= 13.0) {
			// Normal pins
			updatePinIcons(false);
		} else if (currentZoom < 13.0 && this.cameraZoom >= 13.0) {
			// Mini pins
			updatePinIcons(true);
		}
		this.cameraZoom = currentZoom;
	}
}
