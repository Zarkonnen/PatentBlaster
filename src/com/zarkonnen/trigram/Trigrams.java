package com.zarkonnen.trigram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
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
	
	static void writeNumber(ObjectOutputStream s, int n) throws IOException {
		while (n / 128 > 0) {
			s.writeByte(-(n % 128));
			n /= 128;
		}
		s.writeByte(n % 128);
	}
	
	static int readNumber(ObjectInputStream s) throws IOException {
		int n = 0;
		int mult = 1;
		byte b = s.readByte();
		while (b < 0) {
			n -= b * mult;
			mult *= 128;
		}
		n += b * mult;
		return n;
	}
	
	public void exportTo(ObjectOutputStream o) throws IOException {
		FrequencyTable<String> words = new FrequencyTable<String>();
		for (LinkedList<String> l : beginnings) {
			for (String s : l) {
				words.add(s);
			}
		}
		for (Map.Entry<String, FrequencyTable<String>> e : trigrams.entrySet()) {
			for (String s : e.getKey().split(" ")) {
				words.add(s);
			}
			for (String s : e.getValue().freqs.keySet()) {
				words.add(s);
			}
		}
		
		ArrayList<Map.Entry<String, Integer>> wordList = new ArrayList<Map.Entry<String, Integer>>(words.freqs.entrySet());
		Collections.sort(wordList, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> t, Entry<String, Integer> t1) {
				return t1.getValue() - t.getValue();
			}
		});
		
		HashMap<String, Integer> dict = new HashMap<String, Integer>();
		int i = 0;
		for (Map.Entry<String, Integer> e : wordList) {
			dict.put(e.getKey(), i++);
			o.writeUTF(e.getKey());
		}
		
		writeNumber(o, beginnings.size());
		for (LinkedList<String> l : beginnings) {
			for (String s : l) {
				writeNumber(o, dict.get(s));
			}
		}
		writeNumber(o, trigrams.size());
		for (Map.Entry<String, FrequencyTable<String>> e : trigrams.entrySet()) {
			for (String s : e.getKey().split(" ")) {
				writeNumber(o, dict.get(s));
			}
			writeNumber(o, e.getValue().freqs.size());
			for (Map.Entry<String, Integer> f : e.getValue().freqs.entrySet()) {
				writeNumber(o, dict.get(f.getKey()));
				writeNumber(o, f.getValue());
			}
		}
	}
	
	public static void main(String[] argh) {
		Trigrams ts = new Trigrams();
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(Trigrams.class.getResourceAsStream("trigrams.txt"), "UTF-8"));
			ts.importFrom(r);
			r.close();
		} catch (Exception e) {
			e.printStackTrace();
			LinkedList<String> ll = new LinkedList<String>();
			ll.add(""); ll.add("");
			ts.beginnings.add(ll);
			ts.trigrams.put(" ", new FrequencyTable<String>());
			ts.trigrams.get(" ").freqs.put("", 1);
			ts.trigrams.get(" ").total = 1;
		}
		for (int i = 0; i < 100; i++) {
			System.out.println(ts.generate(200, new Random()));
			System.out.println();
		}
	}
	
	public static void main2(String[] args) throws Exception {
		File f = new File("/Users/zar/Desktop/trigrams");
		FileOutputStream fos = new FileOutputStream(f);
		ObjectOutputStream o = new ObjectOutputStream(fos);
		TRIGRAMS.exportTo(o);
		o.flush();
		o.close();
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
	
	public static void main3(String[] args) throws Exception {
		Trigrams t = new Trigrams();
		t.addFromDir(args[0]);
		//System.out.println(t.generate(100, new Random()) + "...");
		PrintWriter w = new PrintWriter(new File(args[1]));
		t.exportTo(w);
		w.flush();
		w.close();
	}
	
	public static Trigrams TRIGRAMS;
	
	static {
		Thread t = new Thread("Trigram Loader") {
			@Override
			public void run() {
				Trigrams ts = new Trigrams();
				try {
					BufferedReader r = new BufferedReader(new InputStreamReader(Trigrams.class.getResourceAsStream("trigrams.txt"), "UTF-8"));
					ts.importFrom(r);
					r.close();
				} catch (Exception e) {
					e.printStackTrace();
					LinkedList<String> ll = new LinkedList<String>();
					ll.add(""); ll.add("");
					ts.beginnings.add(ll);
					ts.trigrams.put(" ", new FrequencyTable<String>());
					ts.trigrams.get(" ").freqs.put("", 1);
					ts.trigrams.get(" ").total = 1;
				}
				TRIGRAMS = ts;
			}
		};
		t.setDaemon(true);
		t.start();
	}
}
