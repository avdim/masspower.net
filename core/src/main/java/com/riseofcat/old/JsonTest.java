package com.riseofcat.old;

import com.badlogic.gdx.utils.Json;
import com.n8cats.lib_gwt.ILog;
import com.n8cats.share.old.ForJsonTest;
import com.n8cats.share.Logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public class JsonTest {
public static final Json json = new Json();
public static void main(String[] args) {
	test(null);
}
@Deprecated
public static void test(ILog log) {
	ForJsonTest test = new ForJsonTest();
	test.testStr = "testStrValue";
	test.mapStrStr = new HashMap<>();
	test.mapStrStr.put("key1", "value1");
	test.mapIntStr = new HashMap<>();
	test.mapIntStr.put(1, "One");
	test.mapIntStr.put(2, "Two");
	test.treeMapIntInt = new TreeMap<>();
	test.treeMapIntInt.put(1, 10);
	test.treeMapIntInt.put(3, 20);
	test.treeMapIntInt.put(2, 30);
	test.list = new ArrayList<>();
	test.list.add("One");
	test.list.add("Two");
	test.set = new HashSet<>();
	test.set.add("One");
	test.set.add("Two");
	test.set.add("One");
	test.mapIdStr = new HashMap<>();
	test.mapIdStr.put(new Logic.Player.Id(1), "PlayerOne");
	test.mapIdStr.put(new Logic.Player.Id(2), "PlayerTwo");
	test.mapIdId = new HashMap<>();
	Logic.Player.Id id1 = new Logic.Player.Id(1);
	test.mapIdId.put(id1, id1);

	String s = json.toJson(test);
	ForJsonTest result = json.fromJson(ForJsonTest.class, s);
	Set<Integer> integers = result.mapIntStr.keySet();
	try {
		Integer next = integers.iterator().next();
	} catch(Exception e) {

	}

	String s1 = result.mapIdStr.get("1");
	String s2 = result.mapIdStr.get(new Logic.Player.Id(2));
	if(log != null) {
		log.debug(s1);
		log.debug(s2);
	}
	if(!json.toJson(result).equals(s)) {
		if(log != null) {
			log.error("json not equals");
		}
	}
	if(log != null) {
		log.debug(json.toJson(result));
	} else {
		System.out.println(json.toJson(result));
	}
}

}
