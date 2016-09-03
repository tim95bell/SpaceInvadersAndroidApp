package com.timbell.spaceinvaders.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.timbell.spaceinvaders.SpaceInvaders;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 640;
		config.height = 360;

//		config.width = 800;
//		config.height = 360;

		// ipad 1, 2, and 3
//		config.width = 2048/4;
//		config.height = 1536/4;

		// iphone 3gs, 4, 4s and lower android devices
//		config.width = 480;
//		config.height = 320;

		// android, windows phone 7
//		config.width = 800;
//		config.height = 480;

		// android and samsung tablets
//		config.width = 1024;
//		config.height = 600;

		// iphone 5, android
//		config.width = 640;
//		config.height = 360;

		config.resizable = false;
		new LwjglApplication(new SpaceInvaders(), config);
	}
}
