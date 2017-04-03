package com.bdb.bikedeboa.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bdb.bikedeboa.R;
import com.bdb.bikedeboa.databinding.ActivityDetailBinding;
import com.bdb.bikedeboa.viewmodel.DetailViewModel;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.bdb.bikedeboa.util.Constants.RACK_ID;

public class DetailActivity extends AppCompatActivity {

	private int rackId;
	private ActivityDetailBinding binding;

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
		} else {
			// Something's not right, finish this activity
			this.finish();
		}

		DetailViewModel detailViewModel = new DetailViewModel(rackId);
		binding.setViewModel(detailViewModel);
		detailViewModel.fetchRackDetails();
	}
}
