package com.zarkonnen.trigram;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FrequencyTable<T> {
	public HashMap<T, Integer> freqs = new HashMap<T, Integer>();
	public int total = 0;
	
	public void add(T t) {
		if (freqs.containsKey(t)) {
			freqs.put(t, freqs.get(t) + 1);
		} else {
			freqs.put(t, 1);
		}
		total++;
	}
	
	public T pick(Random r) {
		int i = r.nextInt(total);
		for (Map.Entry<T, Integer> f : freqs.entrySet()) {
			i -= f.getValue();
			if (i <= 0) {
				return f.getKey();
			}
		}
		return null;
	}
}
