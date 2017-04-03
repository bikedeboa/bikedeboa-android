package com.bdb.bikedeboa.util;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class BindingAdapters {

	@BindingAdapter({"imageAddress"})
	public static void loadImage(ImageView imageView, String imageAddress) {
		// Works for both Uris and Urls
		Glide.with(imageView.getContext())
				.load(imageAddress)
				.diskCacheStrategy(DiskCacheStrategy.RESULT)
				.into(imageView);
	}
}
