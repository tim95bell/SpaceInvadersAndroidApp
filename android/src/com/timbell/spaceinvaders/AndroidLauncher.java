package com.timbell.spaceinvaders;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.timbell.spaceinvaders.SpaceInvaders;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		// is this needed??
		config.useAccelerometer = true;

		initialize(new SpaceInvaders(), config);
	}
}
