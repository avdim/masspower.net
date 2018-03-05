package com.riseofcat.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.czyzby.websocket.CommonWebSockets;
import com.riseofcat.share.Params;
import com.riseofcat.App;
import com.riseofcat.Core;
import com.riseofcat.test.TestJson;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Launches the desktop (LWJGL) application.
 */
public class DesktopLauncher {
public static void main(final String[] args) {
	// Initiating web sockets module:
	CommonWebSockets.initiate();
	createApplication();
}

private static LwjglApplication createApplication() {
	TestJson.Companion.testJson();
	return new LwjglApplication(new Core(), getDefaultConfiguration());
}

private static LwjglApplicationConfiguration getDefaultConfiguration() {
	final LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
	configuration.title = Params.INSTANCE.getTITLE();
	configuration.width = 400;
	configuration.height = 400;
//	for(int size : new int[]{128, 64, 32, 16}) {
//		configuration.addIcon("libgdx" + size + ".png", FileType.Internal);
//	}
	return configuration;
}
}