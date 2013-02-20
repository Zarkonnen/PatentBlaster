package com.zarkonnen.trigram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

public class Trigrams {
	public ArrayList<LinkedList<String>> beginnings = new ArrayList<LinkedList<String>>();
	public HashMap<String, FrequencyTable<String>> trigrams = new HashMap<String, FrequencyTable<String>>();

	public void add(String text) {
		String[] words = text.trim().split("[ \\n\\t]+");
		if (words.length < 3) { return; }
		LinkedList<String> beg = new LinkedList<String>();
		beg.add(words[0]);
		beg.add(words[1]);
		beginnings.add(beg);
		int i = 0;
		while (i < words.length - 2) {
			String oneTwo = words[i] + " " + words[i + 1];
			String three = words[i + 2];
			if (!trigrams.containsKey(oneTwo)) {
				trigrams.put(oneTwo, new FrequencyTable<String>());
			}
			trigrams.get(oneTwo).add(three);
			i++;
		}
	}
	
	public String generate(int numWords, Random r) {
		ArrayList<String> words = new ArrayList<String>();
		words.addAll(beginnings.get(r.nextInt(beginnings.size())));
		while (words.size() < numWords) {
			String oneTwo = words.get(words.size() - 2) + " " + words.get(words.size() - 1);
			if (!trigrams.containsKey(oneTwo)) {
				break; // Oops.
			}
			words.add(trigrams.get(oneTwo).pick(r));
		}
		StringBuilder sb = new StringBuilder();
		for (String w : words) {
			sb.append(w).append(" ");
		}
		return sb.toString();
	}
	
	public void exportTo(PrintWriter w) {
		w.println(beginnings.size());
		for (LinkedList<String> beg : beginnings) {
			for (String s : beg) {
				w.println(s);
			}
		}
		w.println(trigrams.size());
		for (Map.Entry<String, FrequencyTable<String>> e : trigrams.entrySet()) {
			w.println(e.getKey());
			FrequencyTable<String> t = e.getValue();
			w.println(t.total);
			w.println(t.freqs.size());
			for (Map.Entry<String, Integer> fe : t.freqs.entrySet()) {
				w.println(fe.getKey());
				w.println(fe.getValue());
			}
		}
	}
	
	static int readInt(BufferedReader r) throws IOException {
		return Integer.parseInt(r.readLine());
	}
	
	public void importFrom(BufferedReader r) throws IOException {
		int bSize = readInt(r);
		for (int i = 0; i < bSize; i++) {
			LinkedList<String> beg = new LinkedList<String>();
			beg.add(r.readLine());
			beg.add(r.readLine());
			beginnings.add(beg);
		}
		int fSize = readInt(r);
		for (int i = 0; i < fSize; i++) {
			String key = r.readLine();
			FrequencyTable<String> t = new FrequencyTable<String>();
			trigrams.put(key, t);
			t.total = readInt(r);
			int tSize = readInt(r);
			for (int j = 0; j < tSize; j++) {
				t.freqs.put(r.readLine(), readInt(r));
			}
		}
	}
	
	public void addFromDir(String dir) throws FileNotFoundException, IOException {
		File dirF = new File(dir);
		if (!dirF.exists() || !dirF.isDirectory()) {
			System.err.println(dir + " is not a directory!");
			return;
		}
		for (File f : dirF.listFiles()) {
			if (f.getName().endsWith(".txt")) {
				StringBuilder tb = new StringBuilder();
				BufferedReader r = new BufferedReader(new FileReader(f));
				String l;
				while ((l = r.readLine()) != null) { tb.append(l).append(" "); }
				r.close();
				add(tb.toString());
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		Trigrams t = new Trigrams();
		t.addFromDir(args[0]);
		//System.out.println(t.generate(100, new Random()) + "...");
		PrintWriter w = new PrintWriter(new File(args[1]));
		t.exportTo(w);
		w.flush();
		w.close();
	}
	
	public static final Trigrams TRIGRAMS = new Trigrams();
	static {
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(Trigrams.class.getResourceAsStream("trigrams.txt"), "UTF-8"));
			TRIGRAMS.importFrom(r);
			r.close();
		} catch (Exception e) {
			e.printStackTrace();
			LinkedList<String> ll = new LinkedList<String>();
			ll.add(""); ll.add("");
			TRIGRAMS.beginnings.add(ll);
			TRIGRAMS.trigrams.put(" ", new FrequencyTable<String>());
			TRIGRAMS.trigrams.get(" ").freqs.put("", 1);
			TRIGRAMS.trigrams.get(" ").total = 1;
		}
	}
}
