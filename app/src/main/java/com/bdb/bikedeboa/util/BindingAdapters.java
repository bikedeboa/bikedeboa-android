package com.bdb.bikedeboa.util;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.ImageView;

import com.bdb.bikedeboa.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class BindingAdapters {

	@BindingAdapter({"imageAddress"})
	public static void loadImage(ImageView imageView, String imageAddress) {
		// Works for both Uris and Urls
		Glide.with(imageView.getContext())
				.load(imageAddress)
				.thumbnail(Glide.with(imageView.getContext())
						.load(R.drawable.wheel_loading))
				.diskCacheStrategy(DiskCacheStrategy.RESULT)
				.into(imageView);
	}

	@BindingAdapter({"visible"})
	public static void getVisibility(View view, boolean visible) {
		if (visible) {
			view.setVisibility(View.VISIBLE);
		} else {
			view.setVisibility(View.GONE);
		}
	}
}
