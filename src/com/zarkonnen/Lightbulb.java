package com.zarkonnen;

import com.zarkonnen.catengine.Draw;
import com.zarkonnen.catengine.util.Clr;

public class Lightbulb extends Wall {
	public Lightbulb(int x, int y) {
		super(x, y, FurnitureStore.LIGHTBULB.w, FurnitureStore.LIGHTBULB.h);
		isCollidedWith = false;
	}
	
	public static final Clr GLOW = new Clr(255, 255, 161, 30);
	public static final Clr ON = new Clr(255, 255, 161);
	public static final Clr OFF = Clr.DARK_GREY;
	
	boolean on = true;
	
	@Override
	public void draw(Draw d, Level l, double scrollX, double scrollY) {
		if (on = l.r.nextInt(200) != 0) {
			d.rect(ON, x + scrollX, y + scrollY + 20, 30, 30);
		} else {
			d.rect(OFF, x + scrollX, y + scrollY + 20, 30, 30);
		}
	}
	
	@Override
	public void drawGlow(Draw d, Level l, double scrollX, double scrollY) {
		if (on) {
			d.rect(GLOW, x - 100 + scrollX, y + scrollY, 230, 150);
		}
	}
}
