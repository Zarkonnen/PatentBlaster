package com.zarkonnen;

import com.zarkonnen.catengine.Draw;
import com.zarkonnen.catengine.util.Clr;

public enum FurnitureStore {
	CRATE(Location.FLOOR, 80, 80, 0) {
		@Override
		public Wall build(Level l, int x, int y) {
			return new Crate(l, x, y);
		}
	},
	DRESSER(Location.FLOOR, 100, 90, 0),
	WARDROBE(Location.FLOOR, 70, 140, 0),
	FRIDGE(Location.FLOOR, 75, 120, 0) {
		@Override
		public Wall build(Level l, int x, int y) {
			return new Fridge(l, x, y, w, h);
		}
	},
	MINIFRIDGE(Location.FLOOR, 40, 50, 0) {
		@Override
		public Wall build(Level l, int x, int y) {
			return new Fridge(l, x, y, w, h);
		}
	},
	TABLE(Location.FLOOR, 180, 90, 0),
	CANDLE_TABLE(Location.FLOOR, 180, 130, 40) {
		@Override
		public void assemble(Level l, int x, int y) {
			TABLE.assemble(l, x, y + 40);
			l.walls.add(new Candle( x + 10 + l.r.nextInt(w - 20), y + 20, l.power));
		}
	},
	FOUNTAIN(Location.FLOOR, 120, 320, 0) {
		@Override
		public void assemble(Level l, int x, int y) {
			l.walls.add(new Wall(x, y + 260, 120, 60).floor());
			l.walls.add(new Fountainhead(x + 60, y + 200, l.power, Element.ICE, Clr.BLUE));
		}
	},
	HOLY_FOUNTAIN(Location.FLOOR, 120, 320, 0) {
		@Override
		public void assemble(Level l, int x, int y) {
			l.walls.add(new Wall(x, y + 260, 120, 60).floor());
			l.walls.add(new Fountainhead(x + 60, y + 200, l.power, Element.BLESSED, Element.BLESSED.tint));
		}
	},
	UNHOLY_FOUNTAIN(Location.FLOOR, 120, 320, 0) {
		@Override
		public void assemble(Level l, int x, int y) {
			l.walls.add(new Wall(x, y + 260, 120, 60).floor());
			l.walls.add(new Fountainhead(x + 60, y + 200, l.power, Element.CURSED, Element.CURSED.tint));
		}
	},
	SHELF(Location.WALL, 120, 25, 0),
	PLATFORM(Location.WALL, 400, 30, 0),
	BOOKSHELF(Location.WALL, 120, 105, 80) {
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
	EVIL_BOOKSHELF(Location.WALL, 120, 125, 100) {
		@Override
		public void assemble(Level l, int x, int y) {
			SHELF.assemble(l, x, y + 100);
			l.walls.add(new EvilBook(x + l.r.nextInt(80) + 10, y + 100, l));
		}
	},
	DOGSHELF(Location.WALL, 120, 105, 80) {
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
	BEESHELF(Location.WALL, 120, 150, 0) {
		@Override
		public void assemble(Level l, int x, int y) {
			SHELF.assemble(l, x, y + 125);
			l.walls.add(new BeeJar(x + l.r.nextInt(10) + 5, y + 125, l.power));
		}
	},
	ACIDSHELF(Location.WALL, 120, 75, 50) {
		@Override
		public void assemble(Level l, int x, int y) {
			SHELF.assemble(l, x, y + 50);
			l.walls.add(new Bottle(x + l.r.nextInt(90) + 5, y + 50, l.power, 0.2, null));
		}
	},
	MEDISHELF(Location.WALL, 120, 75, 50) {
		@Override
		public void assemble(Level l, int x, int y) {
			SHELF.assemble(l, x, y + 30);
			l.walls.add(new Bottle(x + l.r.nextInt(90) + 5, y + 50, l.power, -0.8, HEAL));
		}
	},
	TORCH(Location.WALL, 8, 24) {
		@Override
		public void assemble(Level l, int x, int y) {
			l.walls.add(new Torch(x, y, l.power));
		}
	},
	LEAK(Location.CEILING, 20, 5) {
		@Override
		public void assemble(Level l, int x, int y) {
			l.walls.add(new Leak(x, y, l.power, Element.ICE, 0));
		}
	},
	ACID_LEAK(Location.CEILING, 20, 5) {
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
	
	public final Location location;
	public final int w;
	public final int h;
	public final boolean hasPlatform;
	public final int platformY;

	private FurnitureStore(Location location, int w, int h) {
		this.location = location;
		this.w = w;
		this.h = h;
		this.hasPlatform = false;
		this.platformY = 0;
	}
	
	private FurnitureStore(Location location, int w, int h, int platformY) {
		this.location = location;
		this.w = w;
		this.h = h;
		this.hasPlatform = true;
		this.platformY = platformY;
	}
	
	public static void furnish(Level l, int items) {
		int placed = 0;
		lp: while (placed < items) {
			FurnitureStore type = FurnitureStore.values()[l.r.nextInt(FurnitureStore.values().length)];
			int x = 0, y = 0;
			switch (type.location) {
				case FLOOR:
					x = Level.GRID_SIZE * 2 + l.r.nextInt((Level.LVL_W - 4) * Level.GRID_SIZE - type.w);
					y = Level.LVL_H * Level.GRID_SIZE - Level.GRID_SIZE - type.h;
					break;
				case CEILING:
					x = Level.GRID_SIZE * 2 + l.r.nextInt((Level.LVL_W - 4) * Level.GRID_SIZE - type.w);
					y = Level.GRID_SIZE;
					break;
				case WALL:
					x = Level.GRID_SIZE * 2 + l.r.nextInt((Level.LVL_W - 4) * Level.GRID_SIZE - type.w);
					y = Level.GRID_SIZE * 4 + l.r.nextInt((Level.LVL_H - 6) * Level.GRID_SIZE);
					break;
			}
			for (Wall other : l.walls) {
				if (
					other.x < x + type.w &&
					other.x + other.w > x &&
					other.y < y + type.h &&
					other.y + other.h > y)
				{
					continue lp;
				}
			}
			type.assemble(l, x, y);
			placed++;
		}
	}
	
	public static enum Location {
		FLOOR, CEILING, WALL
	}
}
