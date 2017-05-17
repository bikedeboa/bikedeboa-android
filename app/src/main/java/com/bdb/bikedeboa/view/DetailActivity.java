package com.bdb.bikedeboa.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.adroitandroid.chipcloud.ChipListener;
import com.bdb.bikedeboa.R;
import com.bdb.bikedeboa.databinding.ActivityDetailBinding;
import com.bdb.bikedeboa.databinding.RatingDialogBinding;
import com.bdb.bikedeboa.util.TagMap;
import com.bdb.bikedeboa.viewmodel.DetailViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MapStyleOptions;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.bdb.bikedeboa.util.Constants.RACK_ID;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

	private static final String TAG = DetailActivity.class.getSimpleName();
	private int rackId;
	private ActivityDetailBinding binding;
	private RatingDialogBinding ratingDialogBinding;
	private DetailViewModel detailViewModel;
	private AlertDialog ratingDialog;

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			rackId = extras.getInt(RACK_ID);
			detailViewModel = new DetailViewModel(rackId, this);
			binding.setViewModel(detailViewModel);
			buildRatingDialog();
		} else {
			// Something's not right, finish this activity
			this.finish();
		}

		// Set click listeners
		binding.howToGetThere.setOnClickListener(getMeThere);
		binding.rackPhoto.setOnClickListener(expandImage);
		binding.rate.setOnClickListener(launchRatingDialog);

		// Fire up map lite
		binding.mapLite.onCreate(null);
		binding.mapLite.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {

		MapsInitializer.initialize(this);
		customizeMap(googleMap);
		detailViewModel.setMap(googleMap);
	}

	private void customizeMap(GoogleMap googleMap) {
		// Disable little map icons
		googleMap.getUiSettings().setMapToolbarEnabled(false);
		// Customise the styling of the base map using a JSON object defined in a raw resource file.
		try {
			boolean success = googleMap.setMapStyle(
					MapStyleOptions.loadRawResourceStyle(this, R.raw.styles_map));

			if (!success) {
				Log.e(TAG, "Style parsing failed.");
			}
		} catch (Resources.NotFoundException e) {
			Log.e(TAG, "Can't find style. Error: ", e);
		}
	}

	private void buildRatingDialog() {

		ratingDialogBinding = DataBindingUtil.inflate(getLayoutInflater(),
				R.layout.rating_dialog, null, false);
		ratingDialogBinding.setViewModel(detailViewModel);

		ratingDialogBinding.ratingChipCloud.addChips(TagMap.getTags());

		ratingDialogBinding.ratingChipCloud.setChipListener(new ChipListener() {
			@Override
			public void chipSelected(int i) { detailViewModel.addChip(TagMap.indexToId(i)); }

			@Override
			public void chipDeselected(int i) { detailViewModel.removeChip(TagMap.indexToId(i)); }
		});

		ratingDialog = new AlertDialog.Builder(this)
				.setView(ratingDialogBinding.getRoot())
				.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						detailViewModel.submitRating();
					}
				})
				.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) { }
				}).create();
	}

	private View.OnClickListener getMeThere = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// <?q=%f,%f> necessary for pin positioning
			String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f",
					detailViewModel.getLatitude(), detailViewModel.getLongitude(),
					detailViewModel.getLatitude(), detailViewModel.getLongitude());
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			startActivity(intent);
		}
	};

	private View.OnClickListener expandImage = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(DetailActivity.this, ExpandImageActivity.class);
			intent.putExtra(RACK_ID, rackId);
			startActivity(intent);
		}
	};

	private View.OnClickListener launchRatingDialog = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ratingDialog.show();
		}
	};

	public void onStarClick(View view) {
		int ratingInProgress = Integer.parseInt((String) view.getTag());
		detailViewModel.setRatingInProgress(ratingInProgress);
		ratingDialog.show();
	}
}
