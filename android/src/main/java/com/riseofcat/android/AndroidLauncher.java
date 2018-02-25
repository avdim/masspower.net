package com.riseofcat.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.github.czyzby.websocket.CommonWebSockets;
import com.riseofcat.App;
import com.riseofcat.Core;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initiating web sockets module:
        CommonWebSockets.initiate();
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        initialize(new Core(new App.Context() {
            public <T> List<T> createConcurrentList() {
                return new CopyOnWriteArrayList<T>();
            }
            public <K, V> Map<K, V> createConcurrentHashMap() {
                return new ConcurrentHashMap<K, V>();
            }
        }), configuration);
    }
}