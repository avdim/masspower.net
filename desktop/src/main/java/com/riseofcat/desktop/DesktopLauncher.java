package com.riseofcat.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.riseofcat.Core;
import com.riseofcat.share.mass.GameConst;
import com.riseofcat.test.TestJson;

/**
 * Launches the desktop (LWJGL) application.
 */
public class DesktopLauncher {
public static void main(final String[] args) {
	// Initiating web sockets module:
	createApplication();
}

private static LwjglApplication createApplication() {
	return new LwjglApplication(new Core(), getDefaultConfiguration());
}

private static LwjglApplicationConfiguration getDefaultConfiguration() {
	final LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
	configuration.title = GameConst.INSTANCE.getTITLE();
	configuration.width = 400;
	configuration.height = 400;
//	for(int size : new int[]{128, 64, 32, 16}) {
//		configuration.addIcon("libgdx" + size + ".png", FileType.Internal);
//	}
	TestJson.Companion.testJson();
	return configuration;
}
}