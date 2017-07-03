package com.bdb.bikedeboa.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;

import com.bdb.bikedeboa.R;
import com.bdb.bikedeboa.databinding.ActivityMapsBinding;
import com.bdb.bikedeboa.viewmodel.MapViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.bdb.bikedeboa.R.id.map;
import static com.bdb.bikedeboa.util.Constants.LOCATION_REQUEST_CODE;
import static com.bdb.bikedeboa.util.Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE;
import static com.bdb.bikedeboa.util.Constants.RACK_ID;
import static com.bdb.bikedeboa.util.Constants.SETTINGS_REQUEST_CODE;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
		GoogleMap.OnMarkerClickListener,
		GoogleMap.OnCameraMoveListener,
		NavigationView.OnNavigationItemSelectedListener,
		EasyPermissions.PermissionCallbacks,
		LocationListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	private static final String TAG = MapActivity.class.getSimpleName();
	private GoogleMap googleMap;
	private GoogleApiClient googleApiClient;
	private ActivityMapsBinding binding;
	private MapViewModel mapViewModel;

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = DataBindingUtil.setContentView(this, R.layout.activity_maps);

		// Set listeners
		binding.menuDrawer.menuNavigationView.setNavigationItemSelectedListener(this);
		binding.placeSearch.setOnClickListener(placeSearchListener);
		binding.drawerButton.setOnClickListener(menuDrawerToggleListener);
		binding.filterButton.setOnClickListener(filterDrawerToggleListener);
		binding.myLocation.setOnClickListener(myLocationListener);
		binding.addRack.setOnClickListener(addRackListener);
		binding.addRackView.setListener(addRackViewClickListener);

		// Create the location client to start receiving updates
		googleApiClient = new GoogleApiClient.Builder(getBaseContext())
				.addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();

		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(map);
		mapFragment.getMapAsync(this);
	}


	@Override
	public void onResume() {
		super.onResume();
		googleApiClient.connect();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (googleApiClient != null && googleApiClient.isConnected()) {
			LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
			googleApiClient.disconnect();
		}
	}

	// Map code
	@Override
	public void onMapReady(GoogleMap googleMap) {
		this.googleMap = googleMap;

		// Move camera to Porto Alegre
		LatLng portoAlegre = new LatLng(-30.039005, -51.224059);
		this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(portoAlegre, 14));
		customizeMap();
		customizeMapLocation();

		// Set map specific listeners
		googleMap.setOnMarkerClickListener(this);
		googleMap.setOnCameraMoveListener(this);

		mapViewModel = new MapViewModel(this.googleMap, this);
		binding.setViewModel(mapViewModel); // We don't use data binding anywhere though
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

	@SuppressWarnings("MissingPermission")
	private void customizeMapLocation() {
		if (EasyPermissions.hasPermissions(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
			this.googleMap.setMyLocationEnabled(true);
			this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO markers' hitboxes are not working properly, they are too big
		// If user has added a marker through place search, it's tag should be null
		if (marker.getTag() != null) {
			int rackId = (int) marker.getTag();
			launchDetailActivity(rackId);
		}
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

	// Drawer code
	View.OnClickListener menuDrawerToggleListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
				binding.drawerLayout.closeDrawer(GravityCompat.START);
			} else {
				binding.drawerLayout.openDrawer(GravityCompat.START);
			}
		}
	};

	View.OnClickListener filterDrawerToggleListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
				binding.drawerLayout.closeDrawer(GravityCompat.END);
			} else {
				binding.drawerLayout.openDrawer(GravityCompat.END);
			}
		}
	};

	@Override
	public void onBackPressed() {
		if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
			binding.drawerLayout.closeDrawer(GravityCompat.START);
		} else if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
			binding.drawerLayout.closeDrawer(GravityCompat.END);
		} else if (binding.addRackView.getVisibility() == View.VISIBLE) {
			binding.addRack.callOnClick();
		} else {
			super.onBackPressed();
		}
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.about) {

		} else if (id == R.id.faq) {

		} else if (id == R.id.login_collaborator) {

		}

		binding.drawerLayout.closeDrawer(GravityCompat.START);
		return true;
	}

	// Autocomplete code
	private View.OnClickListener placeSearchListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			try {
				// Launch autocomplete activity
				Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
						.setFilter(new AutocompleteFilter.Builder()
								.setCountry("BR")
								.build())
						.build(MapActivity.this);
				startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
			} catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
				// TODO: Handle the error.
			}
		}
	};

	private View.OnClickListener myLocationListener = new View.OnClickListener() {
		@SuppressWarnings("MissingPermission")
		@Override
		public void onClick(View v) {
			String permission = Manifest.permission.ACCESS_FINE_LOCATION;
			if (EasyPermissions.hasPermissions(getBaseContext(), permission)) {
				customizeMapLocation();
				mapViewModel.setLastLocation(LocationServices.FusedLocationApi.getLastLocation(googleApiClient));
				if (mapViewModel.getLastLocation() != null) {
					Location lastLocation = mapViewModel.getLastLocation();
					cameraUpdate(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
				}
			} else {
				EasyPermissions.requestPermissions(MapActivity.this,
						getString(R.string.location_rationale), LOCATION_REQUEST_CODE, permission);
			}
		}
	};

	private View.OnClickListener addRackListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (binding.addRackView.getVisibility() == View.VISIBLE) {
				binding.addRackView.setVisibility(View.GONE);
				binding.addRack.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary)));
				binding.addRack.setImageResource(R.drawable.ic_add_location_white_24dp);
			} else {
				binding.addRackView.setVisibility(View.VISIBLE);
				TranslateAnimation animation = new TranslateAnimation(0, 0, -1000, 0);
				animation.setDuration(500);
				binding.addRackView.startAnimation(animation);
				binding.addRack.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(), R.color.darkerGray)));
				binding.addRack.setImageResource(R.drawable.ic_clear_white_24dp);
			}
		}
	};

	private AddRackView.AddRackViewClickListener addRackViewClickListener = new AddRackView.AddRackViewClickListener() {
		@Override
		public void onClick(Point point) {
			googleMap.addMarker(new MarkerOptions()
					.position(googleMap.getProjection().fromScreenLocation(point))
					.title("Hello world"));
			binding.addRack.callOnClick();
		}
	};

	private void cameraUpdate(LatLng position) {
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 17);
		googleMap.animateCamera(cameraUpdate);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Move camera to that place and add normal marker
				Place place = PlaceAutocomplete.getPlace(this, data);
				this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 17), 1000, null);
				this.googleMap.addMarker(new MarkerOptions().position(place.getLatLng()));
				// Set text on "edit text"
				binding.placeSearch.setText(place.getName());
			} else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
				Status status = PlaceAutocomplete.getStatus(this, data);
				Log.w(TAG, status.getStatusMessage());
			} else if (resultCode == RESULT_CANCELED) {
				// The user canceled the operation -- clear text
				binding.placeSearch.setText("");
			}
		}
	}

	// Permissions code
	@Override
	public void onPermissionsGranted(int requestCode, List<String> permissions) {
		if (requestCode == LOCATION_REQUEST_CODE && permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
			binding.myLocation.callOnClick();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}

	@Override
	public void onPermissionsDenied(int requestCode, List<String> permissions) {
		if (EasyPermissions.somePermissionPermanentlyDenied(this, permissions)) {
			new AppSettingsDialog.Builder(this)
					.setRationale(R.string.rationale)
					.setTitle(R.string.title_rationale)
					.setPositiveButton(R.string.app_settings)
					.setNegativeButton(R.string.cancel)
					.setRequestCode(SETTINGS_REQUEST_CODE)
					.build()
					.show();
		}
	}

	@SuppressWarnings("MissingPermission")
	@Override
	public void onConnected(@Nullable Bundle bundle) {
		if (EasyPermissions.hasPermissions(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
			LocationRequest locationRequest = LocationRequest.create()
					.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
					.setInterval(10 * 1000) /* 10 secs */
					.setFastestInterval(2000); /* 2 secs */
			// Request location updates
			LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		/* At the current moment problems with the connection are not important */
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		/* At the current moment problems with the connection are not important */
	}

	@Override
	public void onLocationChanged(Location location) {
		/* Visual location changes are already dealt by google maps blue dot */
	}

	// Filter code
	public void onCheckboxClick(View v) {
		// Couldn't think of a better way
		List<Pair<Float, Float>> ratingFilter = new ArrayList<>();
		List<String> structureFilter = new ArrayList<>();
		String accessFilter = "";

		// Rating
		if (binding.filterDrawer.excellentFilter.isChecked()) {
			ratingFilter.add(new Pair<Float, Float>(3.5f, 5f));
		}
		if (binding.filterDrawer.mediumFilter.isChecked()) {
			ratingFilter.add(new Pair<Float, Float>(2.0001f, 3.4999f));
		}
		if (binding.filterDrawer.badFilter.isChecked()) {
			ratingFilter.add(new Pair<Float, Float>(1f, 2f));
		}
		if (binding.filterDrawer.unknownFilter.isChecked()) {
			ratingFilter.add(new Pair<Float, Float>(0f, .9999f));
		}

		// Access
		if (binding.filterDrawer.publicFilter.isChecked() &&
				binding.filterDrawer.restrictedFilter.isChecked()) {
			accessFilter = "";
		} else {
			if (binding.filterDrawer.publicFilter.isChecked()) {
				accessFilter = (String) binding.filterDrawer.publicFilter.getTag();
			}
			if (binding.filterDrawer.restrictedFilter.isChecked()) {
				accessFilter = (String) binding.filterDrawer.restrictedFilter.getTag();
			}
		}

		// Structure
		if (binding.filterDrawer.derodaFilter.isChecked()) {
			structureFilter.add((String) binding.filterDrawer.derodaFilter.getTag());
		}
		if (binding.filterDrawer.uinvetidoFilter.isChecked()) {
			structureFilter.add((String) binding.filterDrawer.uinvetidoFilter.getTag());
		}
		if (binding.filterDrawer.gradeFilter.isChecked()) {
			structureFilter.add((String) binding.filterDrawer.gradeFilter.getTag());
		}
		if (binding.filterDrawer.traveFilter.isChecked()) {
			structureFilter.add((String) binding.filterDrawer.traveFilter.getTag());
		}
		if (binding.filterDrawer.suspensoFilter.isChecked()) {
			structureFilter.add((String) binding.filterDrawer.suspensoFilter.getTag());
		}
		if (binding.filterDrawer.otherTypeFilter.isChecked()) {
			structureFilter.add((String) binding.filterDrawer.otherTypeFilter.getTag());
		}

		mapViewModel.updateFilters(ratingFilter, structureFilter, accessFilter);
	}


}
