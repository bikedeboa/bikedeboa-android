package com.bdb.bikedeboa.viewmodel;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.android.databinding.library.baseAdapters.BR;
import com.bdb.bikedeboa.R;
import com.bdb.bikedeboa.model.manager.RackManager;
import com.bdb.bikedeboa.model.model.Rack;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailViewModel extends BaseObservable implements
		RackManager.SingleRackCallback {

	private final RackManager rackManager;
	private GoogleMap googleMap;
	private final Resources res;
	private Rack rack;

	public DetailViewModel(int rackId, GoogleMap googleMap, Context context) {
		this.googleMap = googleMap;
		this.res = context.getResources();
		this.rackManager = RackManager.getInstance();
		this.rackManager.setSingleRackCallback(this);
		this.rack = this.rackManager.getRack(rackId);

		fetchRackDetails();
		setUpMap();
	}

	public void fetchRackDetails() {
		rackManager.fetchLocalFull(rack.getId());
	}

	@Override
	public void onRackUpdate(Rack rack) {
		notifyPropertyChanged(BR._all);
	}

	public String getTitle() {
		return rack.getText();
	}

	public String getAddress() {
		return rack.getAddress();
	}

	public String getImage() {
		return rack.getPhotoUrl();
	}

	public boolean hasImage() {
		return rack.getPhotoUrl() != null && !rack.getPhotoUrl().equals("");
	}

	public BitmapDescriptor getCustomPin() {
		// Select correct resource
		Drawable drawable = null;
		float rackScore = rack.getAverageScore();
		if (rackScore == 0) {
			drawable = res.getDrawable(R.drawable.pin_gray);
		} else if (rackScore > 0 && rackScore <= 2) {
			drawable = res.getDrawable(R.drawable.pin_red);
		} else if (rackScore > 2 && rackScore < 3.5) {
			drawable = res.getDrawable(R.drawable.pin_yellow);
		} else if (rackScore >= 3.5) {
			drawable = res.getDrawable(R.drawable.pin_green);
		}

		Bitmap bitmap;
		try {
			// Svg was way too big, that's why I'm dividing by seven
			// (mostly because I don't want to change the svgs too)
			bitmap = Bitmap.createBitmap(
					drawable.getIntrinsicWidth() / 7,
					drawable.getIntrinsicHeight() / 7,
					Bitmap.Config.ARGB_4444);

			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			drawable.draw(canvas);
		} catch (OutOfMemoryError e) {
			return null;
		}
		return BitmapDescriptorFactory.fromBitmap(bitmap);
	}

	private void setUpMap() {

		LatLng pinPosition = new LatLng(rack.getLatitude(), rack.getLongitude());
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pinPosition, 18));
		googleMap.addMarker(new MarkerOptions()
				.position(pinPosition)
				.icon(getCustomPin()));
	}
}
