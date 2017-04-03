package com.bdb.bikedeboa;

import android.app.Application;

import com.bdb.bikedeboa.model.manager.NetworkManager;
import com.bdb.bikedeboa.model.manager.RackManager;
import com.bdb.bikedeboa.model.manager.UserManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BikeDeBoaApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		CalligraphyConfig.initDefault(
				new CalligraphyConfig.Builder()
						.setDefaultFontPath("Raleway-Regular.ttf")
						.setFontAttrId(R.attr.fontPath)
						.build());

		initDatabase();
		NetworkManager.init();
		UserManager.authenticate();
		RackManager.getInstance().fetchLocalLightList();
	}

	private void initDatabase() {

		Realm.init(BikeDeBoaApplication.this);
		RealmConfiguration configuration = new RealmConfiguration.Builder()
				.deleteRealmIfMigrationNeeded()
				.build();
		Realm.setDefaultConfiguration(configuration);
	}
}
