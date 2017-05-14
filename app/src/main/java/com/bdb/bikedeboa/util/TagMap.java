package com.bdb.bikedeboa.util;

import java.util.Hashtable;
import java.util.Map;

public final class TagMap<K, V> {

	private static Map<String, Integer> map = new Hashtable<>();

	public static synchronized void add(String value, int key) {
		map.put(value, key);
	}

	// Chip library is shit -- this is bad but works
	public static synchronized String[] getTags() {
		return map.keySet().toArray(new String[map.size()]);
	}

	public static synchronized String getTag(int index) {
		return (map.keySet().toArray(new String[map.size()]))[index];
	}

	public static synchronized int indexToId(int index) {
		return map.values().toArray(new Integer[map.size()])[index];
	}
}