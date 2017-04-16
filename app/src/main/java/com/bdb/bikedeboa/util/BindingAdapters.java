package com.bdb.bikedeboa.util;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adroitandroid.chipcloud.Chip;
import com.adroitandroid.chipcloud.ChipCloud;
import com.bdb.bikedeboa.R;
import com.bdb.bikedeboa.model.model.Tag;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class BindingAdapters {

	@BindingAdapter({"imageAddress"})
	public static void loadImage(ImageView imageView, String imageAddress) {
		// Works for both Uris and Urls
		Glide.with(imageView.getContext())
				.load(imageAddress)
				.thumbnail(Glide.with(imageView.getContext())
						.load(R.drawable.wheel_loading))
				.crossFade()
				.diskCacheStrategy(DiskCacheStrategy.SOURCE)
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

	@BindingAdapter({"tags"})
	public static void setChips(ChipCloud chipCloud, List<Tag> tagList) {

		if (tagList != null && tagList.size() > 0) {
			// The library doens't offer a removeChips method, so we are removing old chip views manually
			int childCount = chipCloud.getChildCount();
			for (int i = 0; i < childCount; i++) {
				Chip chip = (Chip) chipCloud.getChildAt(0);
				chipCloud.removeView(chip);
			}
			// Add new chips
			for (Tag tag : tagList) {
				chipCloud.addChip(tag.getName());
			}
		}
	}

	@BindingAdapter({"stars"})
	public static void setStars(LinearLayout linearLayout, float rating) {

		Context context = linearLayout.getContext();
		int color = AssetHelper.getColorFromScore(rating);

		// Child 0 is the textView with the written score
		Drawable roundedBackground = ContextCompat.getDrawable(context, R.drawable.rounded_edges);
		roundedBackground.setColorFilter(color, PorterDuff.Mode.ADD);
		linearLayout.getChildAt(0).setBackground(roundedBackground);

		int nStars = Math.round(rating);
		for (int i = 1; i <= nStars; ++i) {
			ImageView imageView = (ImageView) linearLayout.getChildAt(i);
			imageView.setColorFilter(color);
		}
	}
}
