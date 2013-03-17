package com.zarkonnen;

import com.zarkonnen.catengine.Draw;
import com.zarkonnen.catengine.util.Clr;

public class Goodie extends Entity {
	public static final int GOODIE_SIZE = 32;
	
	public Item item;
	public Weapon weapon;
	public int age = 0;
	public int timeSpentTaken = 0;
	public double srcX, srcY, targX, targY;
	
	@Override
	public void tick(Level l) {
		age++;
	}
	
	private Goodie(Creature from) {
		x = from.x + from.w / 2 - GOODIE_SIZE / 2;
		y = from.h < GOODIE_SIZE - 1 ? (from.y - GOODIE_SIZE + from.h - 0.001) : (from.y + from.h / 2 - GOODIE_SIZE / 2);
		w = GOODIE_SIZE;
		h = GOODIE_SIZE;
		dy = -4;
		dx = from.dy / 10;
		gravityMult = 1;
	}

	public Goodie(Creature from, Item item) {
		this(from);
		this.item = item;
		img = item.img;
		tint = item.tint;
	}

	public Goodie(Creature from, Weapon weapon) {
		this(from);
		this.weapon = weapon;
		img = weapon.img;
		tint = weapon.tint;
	}
	
	@Override
	public void draw(Draw d, Level l, double scrollX, double scrollY) {
		d.blit(img, (l.tick / 20) % 2 == 0 ? Clr.WHITE : tint, x + scrollX, y + scrollY, w, h);
	}
}
