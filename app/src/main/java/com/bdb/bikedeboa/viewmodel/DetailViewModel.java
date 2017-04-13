package com.bdb.bikedeboa.viewmodel;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.graphics.drawable.Drawable;

import com.android.databinding.library.baseAdapters.BR;
import com.bdb.bikedeboa.R;
import com.bdb.bikedeboa.model.manager.RackManager;
import com.bdb.bikedeboa.model.model.Rack;
import com.bdb.bikedeboa.model.model.Tag;
import com.bdb.bikedeboa.util.AssetHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

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

	public List<Tag> getTagList() {
		return rack.getTagList();
	}

	public float getAverageRating() {
		return rack.getAverageRating();
	}

	public String getAverageRatingString() {
		// Avoid 4.0
		if (rack.getAverageRating() % 1 == 0) {
			return String.format("%.0f", rack.getAverageRating());
		} else {
			return String.format("%.1f", rack.getAverageRating());
		}
	}

	public boolean hasDescription() {
		return rack.getDescription() != null && !rack.getDescription().equals("");
	}

	public String getDescription() {
		return rack.getDescription();
	}

	public String getReviewNumberString() {
		return res.getQuantityString(R.plurals.n_ratings, rack.getReviewNumber(), rack.getReviewNumber());
	}

	public boolean hasOwnershipAndType() {
		return rack.isPublic() != null && !rack.isPublic().equals("null") &&
				rack.getStructureType() != null && !rack.getStructureType().equals("null");
	}

	public String getOwnership() {
		return AssetHelper.getOwnershipString(rack.isPublic());
	}

	public String getStructureType() {
		return AssetHelper.getStructureTypeString(rack.getStructureType());
	}

	public Drawable getOwnershipImage() {
		return AssetHelper.getOwnershipImage(rack.isPublic());
	}

	public Drawable getStructureTypeImage() {
		return AssetHelper.getStructureTypeImage(rack.getStructureType());
	}

	private void setUpMap() {

		LatLng pinPosition = new LatLng(rack.getLatitude(), rack.getLongitude());
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pinPosition, 18));
		googleMap.addMarker(new MarkerOptions()
				.position(pinPosition)
				.icon(AssetHelper.getCustomPin(rack.getAverageRating(), false)));
	}
}
