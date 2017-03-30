package com.bdb.bikedeboa.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.bdb.bikedeboa.R;
import com.bdb.bikedeboa.model.manager.RackManager;
import com.bdb.bikedeboa.model.model.Rack;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapViewModel extends BaseObservable implements RackManager.RackManagerCallback {

	private Context context;
	private GoogleMap googleMap;
	private RackManager rackManager;

	public MapViewModel(GoogleMap googleMap, Context context) {
		this.googleMap = googleMap;
		this.context = context;
		this.rackManager = RackManager.getInstance();

		rackManager.setRackManagerCallback(this);
		// Fetch racks currently stored at the local db
		placeMarkers(rackManager.getRackList());
	}

	private void placeMarkers(List<Rack> rackList) {

		for (Rack rack : rackList) {
			LatLng coords = new LatLng(rack.getLatitude(), rack.getLongitude());
			float rackScore = rack.getAverageScore();
			this.googleMap.addMarker(new MarkerOptions()
					.position(coords)
					.icon(getCustomPin(rackScore))
					.zIndex(rackScore))
					.setTag(rack.getId());
		}
	}

	private BitmapDescriptor getCustomPin(float rackScore) {

		Bitmap pinBitmap = null;
		float scale = 0;
		if (rackScore == 0) {
			pinBitmap = ((BitmapDrawable) context.getResources()
					.getDrawable(R.drawable.pin_gray)).getBitmap();
			scale = 0.8f;
		} else if (rackScore > 0 && rackScore <= 2) {
			pinBitmap = ((BitmapDrawable) context.getResources()
					.getDrawable(R.drawable.pin_red)).getBitmap();
		} else if (rackScore > 2 && rackScore < 3.5) {
			pinBitmap = ((BitmapDrawable) context.getResources()
					.getDrawable(R.drawable.pin_yellow)).getBitmap();
		} else if (rackScore >= 3.5) {
			pinBitmap = ((BitmapDrawable) context.getResources()
					.getDrawable(R.drawable.pin_green)).getBitmap();
		}

		if (scale == 0) {
			scale = 0.5f + (rackScore/10);
		}

		pinBitmap = Bitmap.createScaledBitmap(pinBitmap,
				Math.round(pinBitmap.getWidth() * scale),
				Math.round(pinBitmap.getHeight() * scale), false);

		return BitmapDescriptorFactory.fromBitmap(pinBitmap);

	}

	@Override
	public void onRackListUpdate(List<Rack> rackList) {
		placeMarkers(rackList);
	}

	@Override
	public void onRackUpdate(Rack rack) {
		// notifyPropertyChanged... when the detail fragment is implemented
	}

	public void onRackClicked(int rackId) {
		rackManager.fetchLocalFull(rackId);
	}
}
