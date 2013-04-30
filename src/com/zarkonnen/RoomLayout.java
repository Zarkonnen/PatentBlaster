package com.zarkonnen;

import com.zarkonnen.catengine.util.Pt;
import com.zarkonnen.catengine.util.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class RoomLayout {
	public int background;
	public boolean[] window = new boolean[Level.LVL_W * Level.GRID_SIZE / 512 + 2];
	public ArrayList<Utils.Pair<WallDecoType, Pt>> decos = new ArrayList<Utils.Pair<WallDecoType, Pt>>();
	public ArrayList<Utils.Pair<FurnitureStore, Pt>> furniture = new ArrayList<Utils.Pair<FurnitureStore, Pt>>();
	public ArrayList<Utils.Pair<Barrel.Type, Pt>> barrels = new ArrayList<Utils.Pair<Barrel.Type, Pt>>();
	
	RoomLayout ln(PrintWriter pw, String ln) {
		pw.println(ln);
		return this;
	}
	
	RoomLayout ln(PrintWriter pw, int ln) {
		pw.println(ln);
		return this;
	}
	
	RoomLayout ln(PrintWriter pw, double ln) {
		pw.println(ln);
		return this;
	}
	
	RoomLayout ln(PrintWriter pw, boolean ln) {
		pw.println(ln);
		return this;
	}
	
	public void write(PrintWriter pw) {
		ln(pw, "RoomLayout").ln(pw, 1).ln(pw, background);
		ln(pw, window.length);
		for (boolean w : window) { ln(pw, w); }
		for (Utils.Pair<WallDecoType, Pt> d : decos) {
			ln(pw, d.a.name()).ln(pw, d.b.x).ln(pw, d.b.y);
		}
		for (Utils.Pair<FurnitureStore, Pt> f : furniture) {
			ln(pw, f.a.name()).ln(pw, f.b.x).ln(pw, f.b.y);
		}
		for (Utils.Pair<Barrel.Type, Pt> b : barrels) {
			ln(pw, b.a.name()).ln(pw, b.b.x).ln(pw, b.b.y);
		}
	}
	
	public static RoomLayout read(BufferedReader r) throws Exception {
		if (!r.readLine().equals("RoomLayout")) {
			throw new Exception("Not a RoomLayout");
		}
		if (i(r) > 1) {
			throw new Exception("Version too new");
		}
		int nWins = i(r);
		RoomLayout rl = new RoomLayout();
		rl.window = new boolean[nWins];
		for (int i = 0; i < nWins; i++) {
			rl.window[i] = b(r);
		}
		String type;
		while ((type = r.readLine()) != null) {
			boolean success = false;
			try {
				WallDecoType wdt = WallDecoType.valueOf(type);
				rl.decos.add(new Utils.Pair<WallDecoType, Pt>(wdt, new Pt(d(r), d(r))));
				success = true;
			} catch (Exception e) {}
			try {
				FurnitureStore fs = FurnitureStore.valueOf(type);
				rl.furniture.add(new Utils.Pair<FurnitureStore, Pt>(fs, new Pt(d(r), d(r))));
				success = true;
			} catch (Exception e) {}
			try {
				Barrel.Type bt = Barrel.Type.valueOf(type);
				rl.barrels.add(new Utils.Pair<Barrel.Type, Pt>(bt, new Pt(d(r), d(r))));
				success = true;
			} catch (Exception e) {}
			if (!success) {
				throw new Exception("Unknown type: " + type);
			}
		}
		return rl;
	}
	
	public static int i(BufferedReader r) throws IOException {
		return Integer.parseInt(r.readLine());
	}
	
	public static boolean b(BufferedReader r) throws IOException {
		return Boolean.parseBoolean(r.readLine());
	}
	
	public static double d(BufferedReader r) throws IOException {
		return Double.parseDouble(r.readLine());
	}
	
	public RoomLayout() {}
}
