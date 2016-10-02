package com.timbell.spaceinvaders.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.timbell.spaceinvaders.SpaceInvaders;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 640;
		config.height = 360;

//		config.WIDTH = 800;
//		config.HEIGHT = 360;

		// ipad 1, 2, and 3
//		config.WIDTH = 2048/4;
//		config.HEIGHT = 1536/4;

		// iphone 3gs, 4, 4s and lower android devices
//		config.WIDTH = 480;
//		config.HEIGHT = 320;

		// android, windows phone 7
//		config.WIDTH = 800;
//		config.HEIGHT = 480;

		// android and samsung tablets
//		config.WIDTH = 1024;
//		config.HEIGHT = 600;

		// iphone 5, android
//		config.WIDTH = 640;
//		config.HEIGHT = 360;

		config.resizable = false;
		new LwjglApplication(new SpaceInvaders(), config);
	}
}
