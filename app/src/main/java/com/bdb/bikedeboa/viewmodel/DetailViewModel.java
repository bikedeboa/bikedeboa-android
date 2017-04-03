package com.bdb.bikedeboa.viewmodel;

import android.databinding.BaseObservable;

import com.android.databinding.library.baseAdapters.BR;
import com.bdb.bikedeboa.model.manager.RackManager;
import com.bdb.bikedeboa.model.model.Rack;

public class DetailViewModel extends BaseObservable implements
		RackManager.SingleRackCallback {

	private final RackManager rackManager;
	private Rack rack;

	public DetailViewModel(int rackId) {
		this.rackManager = RackManager.getInstance();
		this.rackManager.setSingleRackCallback(this);
		this.rack = this.rackManager.getRack(rackId);

		fetchRackDetails();
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
}
