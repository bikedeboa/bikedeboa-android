package com.bdb.bikedeboa;

import android.app.Application;

import com.bdb.bikedeboa.model.manager.NetworkManager;
import com.bdb.bikedeboa.model.manager.RackManager;
import com.bdb.bikedeboa.model.manager.UserManager;
import com.bdb.bikedeboa.util.AssetHelper;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class BikeDeBoaApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		AssetHelper.init(this.getResources());
		initDatabase();
		NetworkManager.init(this.getResources());
		UserManager.authenticate();
		RackManager.init(this).fetchLocalLightList();
	}

	private void initDatabase() {

		Realm.init(BikeDeBoaApplication.this);
		RealmConfiguration configuration = new RealmConfiguration.Builder()
				.deleteRealmIfMigrationNeeded()
				.build();
		Realm.setDefaultConfiguration(configuration);
	}
}
