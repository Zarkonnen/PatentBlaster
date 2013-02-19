package com.zarkonnen;

import com.zarkonnen.catengine.util.Clr;
import com.zarkonnen.catengine.util.Utils.Pair;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Colors {
	public static ArrayList<Pair<Clr, String>> mapping = new ArrayList<Pair<Clr, String>>();
	public static HashMap<Clr, String> names = new HashMap<Clr, String>();
	static {
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(PatentBlaster.class.getResourceAsStream("colors.txt"), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String l = null;
			while ((l = r.readLine()) != null) {
				String[] bits = l.split(" ", 5);
				mapping.add(new Pair<Clr, String>(new Clr(Integer.parseInt(bits[0]), Integer.parseInt(bits[1]), Integer.parseInt(bits[2])), bits[4]));
			}
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public static String getName(Clr c) {
		if (!names.containsKey(c)) {
			int bestDist = 255 * 255 * 3 + 1;
			String bestName = "Purplish";
			for (Pair<Clr, String> m : mapping) {
				int dist = (c.r - m.a.r) * (c.r - m.a.r) + (c.g - m.a.g) * (c.g - m.a.g) + (c.b - m.a.b) * (c.b - m.a.b);
				if (dist < bestDist) {
					bestDist = dist;
					bestName = m.b;
				}
				if (dist == 0) { break; }
			}
			String[] bnb = bestName.split(" ");
			names.put(c, (bnb.length > 1 && bnb[bnb.length - 1].length() < 8 ? bnb[bnb.length - 2] + " " : "") + bnb[bnb.length - 1]);
			return bestName;
		}
		return names.get(c);
	}
}
