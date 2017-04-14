package com.bdb.bikedeboa.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bdb.bikedeboa.R;
import com.bdb.bikedeboa.viewmodel.MapViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.bdb.bikedeboa.R.id.map;
import static com.bdb.bikedeboa.util.Constants.RACK_ID;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
		GoogleMap.OnMarkerClickListener,
		GoogleMap.OnCameraMoveListener,
		PlaceSelectionListener,
		NavigationView.OnNavigationItemSelectedListener {

	private static final String TAG = MapActivity.class.getSimpleName();
	private GoogleMap googleMap;
	private MapViewModel mapViewModel;
	private DrawerLayout drawer;

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_maps);
		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(map);
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		this.googleMap = googleMap;
		// Move camera to Porto Alegre
		LatLng portoAlegre = new LatLng(-30.039005, -51.224059);
		this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(portoAlegre, 14));
		customizeMap();
		setUpAutoComplete();
		setUpDrawer();

		// Set listeners
		googleMap.setOnMarkerClickListener(this);
		googleMap.setOnCameraMoveListener(this);

		mapViewModel = new MapViewModel(this.googleMap, this);
	}

	private void customizeMap() {
		// Customise the styling of the base map using a JSON object defined in a raw resource file.
		this.googleMap.getUiSettings().setRotateGesturesEnabled(false);
		try {
			boolean success = this.googleMap
					.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.styles_map));

			if (!success) {
				Log.e(TAG, "Style parsing failed.");
			}
		} catch (Resources.NotFoundException e) {
			Log.e(TAG, "Can't find style. Error: ", e);
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {

		int rackId = (int) marker.getTag();
		launchDetailActivity(rackId);
		// Return false if we want the camera to move to the marker and an info window to appear
		return true;
	}

	@Override
	public void onCameraMove() {
		mapViewModel.onCameraMove();
	}

	public void launchDetailActivity(int rackId) {

		Intent intent = new Intent(this, DetailActivity.class);
		intent.putExtra(RACK_ID, rackId);
		startActivity(intent);
	}

	// Autocomplete code
	private void setUpAutoComplete() {

		SupportPlaceAutocompleteFragment autocompleteFragment =
				(SupportPlaceAutocompleteFragment) getSupportFragmentManager()
						.findFragmentById(R.id.autocomplete_fragment);

		AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
				.setCountry("BR")
				.build();

		autocompleteFragment.setFilter(typeFilter);
		// Hint font is way too big -- let's just use the default hint for now
		// In the future we can build a custom one
		// autocompleteFragment.setHint(getResources().getString(R.string.autocomplete_hint));
		autocompleteFragment.setOnPlaceSelectedListener(this);
	}

	@Override
	public void onPlaceSelected(Place place) {
		// Move camera to that place and add normal marker
		this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 17), 1000, null);
		this.googleMap.addMarker(new MarkerOptions().position(place.getLatLng()));
	}

	@Override
	public void onError(Status status) {
		Log.w(TAG, "Selecting place: An error occurred: " + status);
	}

	// Drawer code

	View.OnClickListener drawerListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (drawer.isDrawerOpen(GravityCompat.START)) {
				drawer.closeDrawer(GravityCompat.START);
			} else {
				drawer.openDrawer(GravityCompat.START);
			}
		}
	};

	private void setUpDrawer() {

		ImageView drawerButton = (ImageView) findViewById(R.id.drawer_button);
		drawerButton.setOnClickListener(drawerListener);
	}

	@Override
	public void onBackPressed() {
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.about) {
			// Handle the camera action
		} else if (id == R.id.faq) {

		} else if (id == R.id.login_collaborator) {

		}

		drawer.closeDrawer(GravityCompat.START);
		return true;
	}
}
