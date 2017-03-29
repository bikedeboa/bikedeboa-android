package com.bdb.bikedeboa.view;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.bdb.bikedeboa.R;
import com.bdb.bikedeboa.viewmodel.MapViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

	private GoogleMap googleMap;
	private MapViewModel mapViewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_maps);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		this.googleMap = googleMap;
		this.googleMap.setOnMarkerClickListener(this);

		mapViewModel = new MapViewModel(this.googleMap);
		// Move camera to Porto Alegre
		this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-30.0346, -51.2177)));
	}

	@Override
	public boolean onMarkerClick(Marker marker) {

		int rackId = (int) marker.getTag();
		mapViewModel.onRackClicked(rackId);
		// Return false if we want the camera to move to the marker and an info window to appear
		return true;
	}
}
