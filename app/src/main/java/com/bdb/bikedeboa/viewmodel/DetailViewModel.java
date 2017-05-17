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

import java.util.ArrayList;
import java.util.List;

public class DetailViewModel extends BaseObservable implements
		RackManager.SingleRackCallback {

	private final RackManager rackManager;
	private Resources res = null;
	private Rack rack;
	private GoogleMap googleMap;
	// Rating variables
	private int ratingInProgress = 0;
	private List<Integer> selectedChips;


	public DetailViewModel(int rackId, Context context) {
		this.res = context.getResources();
		this.rackManager = RackManager.getInstance();
		this.rackManager.setSingleRackCallback(this);
		this.rack = this.rackManager.getRack(rackId);
		this.selectedChips = new ArrayList<>();

		fetchRackDetails();
	}

	public void fetchRackDetails() {
		rackManager.fetchLocalFull(rack.getId());
	}

	@Override
	public void onRackUpdate(Rack rack) {
		notifyPropertyChanged(BR._all);
		setMap(null); // If googleMaps is already not null, it will work
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

	public Double getLatitude() {
		return rack.getLatitude();
	}

	public Double getLongitude() {
		return rack.getLongitude();
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

	public boolean hasAccessAndType() {
		return rack.isPublic() != null && !rack.isPublic().equals("null") &&
				rack.getStructureType() != null && !rack.getStructureType().equals("null");
	}

	public String getAccess() {
		return AssetHelper.getAccessString(rack.isPublic());
	}

	public String getStructureType() {
		return AssetHelper.getStructureTypeString(rack.getStructureType());
	}

	public Drawable getAccessImage() {
		return AssetHelper.getAccessImage(rack.isPublic());
	}

	public Drawable getStructureTypeImage() {
		return AssetHelper.getStructureTypeImage(rack.getStructureType());
	}

	public void setMap(GoogleMap googleMap) {
		// Setter
		if (this.googleMap == null && googleMap != null) {
			this.googleMap = googleMap;
		}
		// In case it's already set and we are just using
		if (this.googleMap != null) {
			LatLng pinPosition = new LatLng(rack.getLatitude(), rack.getLongitude());
			this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pinPosition, 18));
			this.googleMap.addMarker(new MarkerOptions()
					.position(pinPosition)
					.icon(AssetHelper.getCustomPin(rack.getAverageRating(), false)));
		}
	}

	public void submitRating() {
		rackManager.submitRating(rack.getId(), ratingInProgress, selectedChips);
	}

	public void setRatingInProgress(int ratingInProgress) {
		this.ratingInProgress = ratingInProgress;
		notifyPropertyChanged(BR._all); // Notify otherwise stars won't update on dialog
	}

	public int getRatingInProgress() {
		return ratingInProgress;
	}

	public void addChip(int id) {
		this.selectedChips.add(id);
	}

	public void removeChip(int id) {
		this.selectedChips.remove(Integer.valueOf(id));
	}

	public boolean isUserRated() {
		return rack.getUserRatingId() != -1;
	}

}
