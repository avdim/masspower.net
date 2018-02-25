package com.riseofcat;

import com.badlogic.gdx.utils.Json;
import com.riseofcat.lib.com.rits.cloning.Cloner;

public class UtilsCore {
private static final Json json = new Json();
private static Cloner cloner = new Cloner();
public static <T> T copy(T value) {
	if(true) return cloner.deepClone(value);
	else {
		try {
			Class<T> clazz = (Class<T>) value.getClass();
			return json.fromJson(clazz, json.toJson(value));//todo better without json
		} catch(Throwable t) {
			t.printStackTrace();
		}
		return null;
	}
}
}
