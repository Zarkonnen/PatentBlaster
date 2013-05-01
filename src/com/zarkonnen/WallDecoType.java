package com.zarkonnen;

import com.zarkonnen.catengine.Draw;
import com.zarkonnen.catengine.Img;
import com.zarkonnen.catengine.util.Clr;

public enum WallDecoType {
	SOCKET(6, new Img("socket"), 18, 14, Level.GRID_SIZE * (Level.LVL_H - 4), Level.GRID_SIZE * (Level.LVL_H - 2)),
	GRILLE(7, new Img("grille"), 60, 40),
	FAN(3, new Img("fan"), 55, 55),
	TEAR1(1, new Img("tear1"), 24, 59),
	TEAR2(1, new Img("tear2"), 20, 22),
	TEAR3(1, new Img("tear3"), 65, 84),
	TEAR4(1, new Img("tear4"), 89, 39),
	TEAR5(1, new Img("tear5"), 61, 165),
	TEAR6(1, new Img("tear6"), 49, 46),
	TEAR7(1, new Img("tear7"), 91, 73),
	TEAR8(1, new Img("tear8"), 29, 29),
	TEAR9(1, new Img("tear9"), 26, 41),
	TEAR10(1, new Img("tear10"), 20, 42),
	TEAR11(1, new Img("tear11"), 80, 72),
	BLOOD(1, new Img("blood"), 160, 169),
	BLOOD2(1, new Img("blood2"), 59, 123),
	BLOOD3(1, new Img("blood3"), 47, 61),
	BLOOD4(1, new Img("blood4"), 143, 157),
	STAIN(1, new Img("stain"), 150, 174, Level.GRID_SIZE, Level.GRID_SIZE),
	STAIN1(1, new Img("stain1"), 18, 51, Level.GRID_SIZE, Level.GRID_SIZE),
	STAIN2(1, new Img("stain2"), 118, 74, Level.GRID_SIZE, Level.GRID_SIZE),
	STAIN3(1, new Img("stain3"), 168, 115, Level.GRID_SIZE, Level.GRID_SIZE),
	STAIN4(1, new Img("stain4"), 109, 25, Level.GRID_SIZE, Level.GRID_SIZE),
	STAIN5(1, new Img("stain5"), 122, 38, Level.GRID_SIZE, Level.GRID_SIZE),
	STAIN6(1, new Img("stain6"), 51, 45, Level.GRID_SIZE, Level.GRID_SIZE),
	OIL(1, new Img("ink"), 154, 167),
	OIL2(1, new Img("ink2"), 174, 184),
	MOLD1(1, new Img("mold1"), 126, 131),
	MOLD2(1, new Img("mold2"), 199, 198),
	MOLD3(1, new Img("mold3"), 118, 178),
	SWITCH(6, new Img("switch"), 14, 18, Level.GRID_SIZE * (Level.LVL_H - 6), Level.GRID_SIZE * (Level.LVL_H - 4)),
	WIRE(4, null, 3, Level.GRID_SIZE * (Level.LVL_H - 2), Level.GRID_SIZE, Level.GRID_SIZE) {
		@Override
		public void draw(Draw d, WallDeco wd, int scrollX, int scrollY, Level l) {
			d.rect(WIRE_C, wd.x + scrollX, wd.y + scrollY, w, h);
		}
	},
	PORTRAIT(5, new Img("portrait"), 200, 207) {
		@Override
		public void draw(Draw d, WallDeco wd, int scrollX, int scrollY, Level l) {
			super.draw(d, wd, scrollX, scrollY, l);
			if (l.boss != null) {
				d.blit(l.boss.img, l.boss.tint, 0.6, wd.x + scrollX + 40, wd.y + scrollY + 40, 120, 120, 0);
			}
		}
	},
	PATENT(5, null, 43, 70) {
		@Override
		public void draw(Draw d, WallDeco wd, int scrollX, int scrollY, Level l) {
			//d.rect(PAPER_C, scrollX + wd.x, scrollY + wd.y, w, h);
			d.blit(PAPER, PAPER_C, scrollX + wd.x, scrollY + wd.y);
			if (wd.img != null) {
				d.blit(wd.img, PAPER_C, scrollX + wd.x + 5, scrollY + wd.y + 17, w - 10, w - 10);
			}
			//d.text(wd.text, PatentBlaster.SMOUNT, scrollX + wd.x + 5, scrollY + wd.y + 5, w - 10);
			
			d.rect(PRINT_C, scrollX + wd.x + 5, scrollY + wd.y + 5, w - 10, 2);
			
			for (int i = 0; i < 3; i++) {
				d.rect(PRINT_C, scrollX + wd.x + 5, scrollY + wd.y + w + 15 + i * 3, w - 10, 1);
			}
		}
	},
;
	
	public static final Clr PRINT_C = new Clr(60, 60, 55);
	public static final Clr PAPER_C = new Clr(120, 120, 110);
	public static final Clr WIRE_C = new Clr(20, 20, 20, 80);
	public static final Img PAPER = PatentBlaster.NONPAT_IMGS.get("paper");
	
	public final int p;
	public final Img img;
	public final int w;
	public final int h;
	public final int minY;
	public final int maxY;

	private WallDecoType(int p, Img img, int w, int h) {
		this.p = p;
		this.img = img;
		this.w = w;
		this.h = h;
		minY = Level.GRID_SIZE * 4;
		maxY = Level.GRID_SIZE * (Level.LVL_H - 2) - h;
	}

	private WallDecoType(int p, Img img, int w, int h, int minY, int maxY) {
		this.p = p;
		this.img = img;
		this.w = w;
		this.h = h;
		this.minY = minY;
		this.maxY = maxY;
	}
	
	public void draw(Draw d, WallDeco wd, int scrollX, int scrollY, Level l) {
		d.blit(img, wd.x + scrollX, wd.y + scrollY);
	}
}
