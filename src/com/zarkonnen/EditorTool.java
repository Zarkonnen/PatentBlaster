package com.zarkonnen;

import com.zarkonnen.catengine.util.Pt;
import com.zarkonnen.catengine.util.Utils;
import java.util.ArrayList;

public abstract class EditorTool {
	public String name;
	public int w;
	public int h;
	public abstract void run(int x, int y, RoomLayout rl);

	public EditorTool(String name, int w, int h) {
		this.name = name.replace(" ", "_");
		this.w = w;
		this.h = h;
	}
	
	public static final ArrayList<EditorTool> TOOLS = new ArrayList<EditorTool>();
	
	static {
		TOOLS.add(new EditorTool("Cycle Background", 0, 0) {
			@Override
			public void run(int x, int y, RoomLayout rl) {
				rl.background = (rl.background + 1) % Level.NUM_BACKGROUNDS;
			}
		});
		TOOLS.add(new EditorTool("Toggle Window", 0, 0) {
			@Override
			public void run(int x, int y, RoomLayout rl) {
				int windex = x / 512;
				if (windex > 0 && windex < rl.window.length) {
					rl.window[windex] = !rl.window[windex];
				}
			}
		});
		TOOLS.add(new EditorTool("Delete", 0, 0) {
			@Override
			public void run(int x, int y, RoomLayout rl) {
				for (Utils.Pair<FurnitureStore, Pt> f : rl.furniture) {
					if (x >= f.b.x && y >= f.b.y && x <= f.b.x + f.a.w && y <= f.b.y + f.a.h) {
						rl.furniture.remove(f);
						return;
					}
				}
				for (Utils.Pair<Barrel.Type, Pt> b : rl.barrels) {
					if (x >= b.b.x && y >= b.b.y && x <= b.b.x + 42 && y <= b.b.y + 60) {
						rl.barrels.remove(b);
						return;
					}
				}
				for (Utils.Pair<NonsensePatent, Pt> p : rl.patents) {
					if (x >= p.b.x && y >= p.b.y && x <= p.b.x + WallDecoType.PATENT.w && y <= p.b.y + WallDecoType.PATENT.h) {
						rl.patents.remove(p);
						return;
					}
				}
				for (Utils.Pair<WallDecoType, Pt> p : rl.decos) {
					if (x >= p.b.x && y >= p.b.y && x <= p.b.x + p.a.w && y <= p.b.y + p.a.h) {
						rl.decos.remove(p);
						return;
					}
				}
			}
		});
		// Backgrounds
		for (final WallDecoType wdt : WallDecoType.values()) {
			if (wdt == WallDecoType.PATENT) { continue; }
			TOOLS.add(new EditorTool(wdt.name(), wdt.w, wdt.h) {
				@Override
				public void run(int x, int y, RoomLayout rl) {
					rl.decos.add(new Utils.Pair<WallDecoType, Pt>(wdt, new Pt(x * 1.0, y * 1.0)));
				}
			});
		}
		// Barrels
		for (final Barrel.Type wdt : Barrel.Type.values()) {
			TOOLS.add(new EditorTool(wdt.name(), 42, 60) {
				@Override
				public void run(int x, int y, RoomLayout rl) {
					rl.barrels.add(new Utils.Pair<Barrel.Type, Pt>(wdt, new Pt(x, y)));
				}
			});
		}
		// Furns
		for (final FurnitureStore fs : FurnitureStore.values()) {
			TOOLS.add(new EditorTool(fs.name(), fs.w, fs.h) {
				@Override
				public void run(int x, int y, RoomLayout rl) {
					rl.furniture.add(new Utils.Pair<FurnitureStore, Pt>(fs, new Pt(x, y)));
				}
			});
		}
		// Patents
		for (final NonsensePatent np : NonsensePatent.values()) {
			TOOLS.add(new EditorTool(np.name(), WallDecoType.PATENT.w, WallDecoType.PATENT.h) {
				@Override
				public void run(int x, int y, RoomLayout rl) {
					rl.patents.add(new Utils.Pair<NonsensePatent, Pt>(np, new Pt(x, y)));
				}
			});
		}
	}
}
