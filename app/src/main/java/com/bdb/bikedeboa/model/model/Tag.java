package com.bdb.bikedeboa.model.model;

import io.realm.RealmObject;

public class Tag extends RealmObject {

	private String name;
	private int count;

	public Tag() {
	}

	public Tag(String name, int count) {
		this.name = name;
		this.count = count;
	}

	public String getName() {
		return name;
	}

	public int getCount() {
		return count;
	}
}
