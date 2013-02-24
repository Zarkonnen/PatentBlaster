package com.zarkonnen;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.prefs.Preferences;

public class Names {
	public static final String BEFORE = "class=\"mw-changeslist-title\">";
	public static final String AFTER = "</a>";
	
	public static final ArrayList<String> names = new ArrayList<String>();
	public static final String[] DEFAULT_NAMES = { "My Medium-Sized Pony" };
	
	static {
		final Preferences pn = Preferences.userNodeForPackage(Names.class);
		try {
			if (pn.getLong("namesLastReceived", 0) != 0) {
				long lastReceived = pn.getLong("namesLastReceived", 0);
				long daysSinceLastReceived = (System.currentTimeMillis() - lastReceived)
						/ 1000 // ms -> s
						/ 3600 // s -> h
						/ 24; // h -> d
				daysSinceLastReceived = 10;
				if (daysSinceLastReceived < 3) {
					synchronized (names) {
						for (int page = 0; page < 10; page++) {
							for (String n : pn.get("names_" + page, "").split("___")) {
								if (n.length() > 0) {
									names.add(n);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (names.isEmpty()) {
			Runnable r = new Runnable() {
				@Override
				public void run() {
					try {
						URL url = new URL("http://en.wikipedia.org/w/index.php?title=Special:RecentChanges&limit=500&namespace=0");
						InputStream is = (InputStream) url.getContent();
						BufferedReader br = new BufferedReader(new InputStreamReader(is));
						String l = null;
						synchronized (names) {
							while ((l = br.readLine()) != null) {
								if (l.contains(BEFORE)) {
									String txt = l.split(BEFORE)[1].split(AFTER)[0];
									if (txt.length() > 3 && txt.length() < 24 && txt.matches("[a-zA-Z0-9 .,-/()!?_'\"@$]+")) {
										names.add(txt);
									}
								}
							}
							if (names.isEmpty()) {
								names.addAll(Arrays.asList(DEFAULT_NAMES));
							} else {
								try {
									pn.putLong("namesLastReceived", System.currentTimeMillis());
									int nIndex = 0;
									for (int page = 0; page < 10; page++) {
										StringBuilder namesB = new StringBuilder();
										for (int i = 0; i < 50 && nIndex < names.size(); i++) {
											namesB.append(names.get(nIndex++)).append("___");
										}
										pn.put("names_" + page, namesB.toString());
									}
									pn.flush();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							names.notifyAll();
						}
						br.close();
					} catch (Exception e) {
						synchronized (names) {
							names.addAll(Arrays.asList(DEFAULT_NAMES));
							names.notifyAll();
						}
						e.printStackTrace();
					}
				}
			};
			Thread t = new Thread(r);
			t.setDaemon(true);
			t.setName("Wikipedia Name Fetcher");
			t.start();
		}
	}
	
	public static boolean namesLoaded() {
		return !names.isEmpty();
	}
	
	public static String pick(Random r) {
		try {
			synchronized (names) {
				while (names.isEmpty()) {
					names.wait(1000);
				}
				return names.get(r.nextInt(names.size()));
			}
		} catch (InterruptedException e) {
			return "Squirrel";
		}
	}
}
