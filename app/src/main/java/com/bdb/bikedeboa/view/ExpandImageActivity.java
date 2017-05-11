package com.bdb.bikedeboa.view;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.bdb.bikedeboa.R;
import com.bdb.bikedeboa.util.BlurTransformation;
import com.bdb.bikedeboa.viewmodel.DetailViewModel;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;

import static com.bdb.bikedeboa.util.Constants.RACK_ID;

public class ExpandImageActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expand_image);

		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();

		Bundle extras = getIntent().getExtras();
		int rackId = 0;
		if (extras != null) {
			rackId = extras.getInt(RACK_ID);
		} else {
			// Something's not right, finish this activity
			this.finish();
		}

		DetailViewModel detailViewModel = new DetailViewModel(rackId, this);
		PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);

		// Request cached thumbnail
		DrawableRequestBuilder<String> thumbnailRequest = Glide
				.with(this)
				.load(detailViewModel.getImage().replace("images/", "images/thumbs/"))
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
				.transform(new BlurTransformation(this));

		Glide.with(this)
				.load(detailViewModel.getImage())
				.thumbnail(thumbnailRequest)
				.crossFade()
				.diskCacheStrategy(DiskCacheStrategy.SOURCE)
				.priority(Priority.HIGH)
				.into(photoView);

	}
}
