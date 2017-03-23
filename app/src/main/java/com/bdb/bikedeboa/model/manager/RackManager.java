package com.bdb.bikedeboa.model.manager;

import com.bdb.bikedeboa.model.model.Rack;

import java.util.ArrayList;
import java.util.List;

public class RackManager {

	private static RackManager instance;
	private List<Rack> rackList;

	private RackManager() {
		rackList = new ArrayList<>();
	}

	RackManager getInstance() {

		if (instance == null) {
			instance = new RackManager();
		}
		return instance;
	}
}
