package com.zarkonnen;

import com.zarkonnen.catengine.Draw;
import com.zarkonnen.catengine.util.Clr;
import java.awt.Rectangle;
import java.util.ArrayList;

public enum FurnitureStore {
	CRATE(4, Location.FLOOR, 80, 80, 0) {
		@Override
		public Wall build(Level l, int x, int y) {
			return new Crate(l, x, y);
		}
	},
	DRESSER(2, Location.FLOOR, 100, 90, 0),
	WARDROBE(2, Location.FLOOR, 70, 140, 0),
	FRIDGE(2, Location.FLOOR, 75, 120, 0) {
		@Override
		public Wall build(Level l, int x, int y) {
			return new Fridge(l, x, y, w, h);
		}
	},
	MINIFRIDGE(1, Location.FLOOR, 40, 50, 0) {
		@Override
		public Wall build(Level l, int x, int y) {
			return new Fridge(l, x, y, w, h);
		}
	},
	TABLE(2, Location.FLOOR, 180, 90, 0),
	CANDLE_TABLE(3, Location.FLOOR, 180, 130, 40) {
		@Override
		public void assemble(Level l, int x, int y) {
			TABLE.assemble(l, x, y + 40);
			l.walls.add(new Candle( x + 10 + l.r.nextInt(w - 20), y + 20, l.power));
		}
	},
	FOUNTAIN(5, Location.FLOOR, 120, 320, 0) {
		@Override
		public void assemble(Level l, int x, int y) {
			l.walls.add(new Wall(x, y + 260, 120, 60).floor());
			l.walls.add(new Fountainhead(x + 60, y + 200, l.power, Element.ICE, Clr.BLUE));
		}
	},
	HOLY_FOUNTAIN(3, Location.FLOOR, 120, 320, 0) {
		@Override
		public void assemble(Level l, int x, int y) {
			l.walls.add(new Wall(x, y + 260, 120, 60).floor());
			l.walls.add(new Fountainhead(x + 60, y + 200, l.power, Element.BLESSED, Element.BLESSED.tint));
		}
	},
	UNHOLY_FOUNTAIN(3, Location.FLOOR, 120, 320, 0) {
		@Override
		public void assemble(Level l, int x, int y) {
			l.walls.add(new Wall(x, y + 260, 120, 60).floor());
			l.walls.add(new Fountainhead(x + 60, y + 200, l.power, Element.CURSED, Element.CURSED.tint));
		}
	},
	SHELF(8, Location.WALL, 120, 25, 0),
	PLATFORM(8, Location.WALL, 400, 30, 0),
	BOOKSHELF(8, Location.WALL, 120, 105, 80) {
		@Override
		public void assemble(Level l, int x, int y) {
			SHELF.assemble(l, x, y + 80);
			int books = 2 + l.r.nextInt(5);
			int bookStart = 5 + l.r.nextInt(20);
			for (int i = 0; i < books; i++) {
				l.walls.add(new Book(x + bookStart + i * 15, y + 80, l));
			}
		}
	},
	EVIL_BOOKSHELF(3, Location.WALL, 120, 125, 100) {
		@Override
		public void assemble(Level l, int x, int y) {
			SHELF.assemble(l, x, y + 100);
			l.walls.add(new EvilBook(x + l.r.nextInt(80) + 10, y + 100, l));
		}
	},
	DOGSHELF(4, Location.WALL, 120, 105, 80) {
		@Override
		public void assemble(Level l, int x, int y) {
			SHELF.assemble(l, x, y + 80);
			int start = 5 + l.r.nextInt(30);
			while (start < 80) {
				l.walls.add(new ChinaDog(x + start, y + 80));
				start += 33 + l.r.nextInt(30);
			}
		}
	},
	BEESHELF(4, Location.WALL, 120, 150, 0) {
		@Override
		public void assemble(Level l, int x, int y) {
			SHELF.assemble(l, x, y + 125);
			l.walls.add(new BeeJar(x + l.r.nextInt(10) + 5, y + 125, l.power));
		}
	},
	ACIDSHELF(3, Location.WALL, 120, 75, 50) {
		@Override
		public void assemble(Level l, int x, int y) {
			SHELF.assemble(l, x, y + 50);
			l.walls.add(new Bottle(x + l.r.nextInt(90) + 5, y + 50, l.power, 0.2, null));
		}
	},
	MEDISHELF(3, Location.WALL, 120, 75, 50) {
		@Override
		public void assemble(Level l, int x, int y) {
			SHELF.assemble(l, x, y + 50);
			l.walls.add(new Bottle(x + l.r.nextInt(90) + 5, y + 50, l.power, -0.8, HEAL));
		}
	},
	TORCH(10, Location.WALL, 8, 24) {
		@Override
		public void assemble(Level l, int x, int y) {
			l.walls.add(new Torch(x, y, l.power));
		}
	},
	LEAK(4, Location.CEILING, 20, 5) {
		@Override
		public void assemble(Level l, int x, int y) {
			l.walls.add(new Leak(x, y, l.power, Element.ICE, 0));
		}
	},
	ACID_LEAK(5, Location.CEILING, 20, 5) {
		@Override
		public void assemble(Level l, int x, int y) {
			l.walls.add(new Leak(x, y, l.power, Element.ACID, 3));
		}
	};
		
	public static final Clr HEAL = new Clr(200, 255, 200);
	public static final Clr DEFAULT = new Clr(70, 50, 20);
		
	public void assemble(Level l, int x, int y) {
		l.walls.add(build(l, x, y));
	}
	
	public Wall build(Level l, int x, int y) {
		Wall wall = new Wall(x, y, w, h)/* {
			@Override
			public void draw(Draw d, Level l, double scrollX, double scrollY) {
				super.draw(d, l, scrollX, scrollY);
				d.text(name(), PatentBlaster.FOUNT, x + scrollX, y + scrollY);
			}
		}*/.floor();
		wall.tint = DEFAULT;
		return wall;
	}
	
	public final int p;
	public final Location location;
	public final int w;
	public final int h;
	public final boolean hasPlatform;
	public final int platformY;

	private FurnitureStore(int p, Location location, int w, int h) {
		this.p = p;
		this.location = location;
		this.w = w;
		this.h = h;
		this.hasPlatform = false;
		this.platformY = 0;
	}
	
	private FurnitureStore(int p, Location location, int w, int h, int platformY) {
		this.p = p;
		this.location = location;
		this.w = w;
		this.h = h;
		this.hasPlatform = true;
		this.platformY = platformY;
	}
	
	static class PlacedFurniture {
		final FurnitureStore type;
		final int x;
		final int y;

		public PlacedFurniture(FurnitureStore type, int x, int y) {
			this.type = type;
			this.x = x;
			this.y = y;
		}
	}
	
	public static void furnish(Level l, int items, int platforms, int highPlatforms) {
		int placed = 0;
		ArrayList<PlacedFurniture> pf = new ArrayList<PlacedFurniture>();
		lp: while (placed < items) {
			boolean platformsOnly = placed > items - platforms;
			ArrayList<FurnitureStore> stores = new ArrayList<FurnitureStore>();
			for (FurnitureStore fs : FurnitureStore.values()) {
				if (!platformsOnly || (fs.location == Location.WALL && fs.hasPlatform)) {
					stores.add(fs);
				}
			}
			int rollRange = 0;
			for (FurnitureStore fs : stores) {
				rollRange += fs.p;
			}
			int roll = l.r.nextInt(rollRange);
			int ti = 0;
			while (roll > stores.get(ti).p) {
				roll -= stores.get(ti).p;
				ti++;
			}
			FurnitureStore type = stores.get(ti);
			int x = 0, y = 0;
			switch (type.location) {
				case FLOOR:
					y = Level.LVL_H * Level.GRID_SIZE - Level.GRID_SIZE - type.h;
					break;
				case CEILING:
					y = Level.GRID_SIZE;
					break;
				case WALL:
					y = Level.GRID_SIZE * 4 + l.r.nextInt((Level.LVL_H - 6) * Level.GRID_SIZE) - 30;
					break;
			}
			boolean acceptable = false;
			att: for (int attempt = 0; attempt < 50; attempt++) {
				x = Level.GRID_SIZE * 2 + l.r.nextInt((Level.LVL_W - 4) * Level.GRID_SIZE - type.w);
				if (!reachable(type, x, y, l, pf, placed > items - highPlatforms)) { continue att; }
				for (Wall other : l.walls) {
					if (
						other.x < x + type.w &&
						other.x + other.w > x &&
						other.y < y + type.h &&
						other.y + other.h > y)
					{
						continue att;
					}
				}
				acceptable = true;
				break;
			}
			if (acceptable) {
				type.assemble(l, x, y);
				pf.add(new PlacedFurniture(type, x, y));
				placed++;
			}
		}
	}
	
	public static enum Location {
		FLOOR, CEILING, WALL
	}
	
	public static final int JUMP_H = 180;
	public static final int JUMP_W = 120;
	
	static boolean reachable(FurnitureStore fs, int x, int y, Level l, ArrayList<PlacedFurniture> pf, boolean up) {
		if (!fs.hasPlatform) { return true; }
		// establish baseline for floor
		// could just generate a reachability rect
		// yeah!
		// france!
		// qqDPS once again being reallllllly naughty and using Rectangle
		ArrayList<Rectangle> accessible = new ArrayList<Rectangle>();
		if (!up) {
			accessible.add(new Rectangle(0, (Level.LVL_H - 1) * Level.GRID_SIZE - JUMP_H, Level.LVL_W * Level.GRID_SIZE, 5000));
		}
		for (PlacedFurniture f : pf) {
			if (f.type.hasPlatform) {
				accessible.add(new Rectangle(f.x - JUMP_W, f.y + f.type.platformY - JUMP_H, f.type.w + JUMP_W * 2, up ? (f.type.platformY + JUMP_H + 25) : 5000));
			}
		}
		Rectangle platform = new Rectangle(x, y + fs.platformY, fs.w, 10);
		for (Rectangle a : accessible) {
			if (a.intersects(platform)) {
				return true;
			}
		}
		return false;
	}
}
