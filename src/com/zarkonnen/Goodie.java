package com.zarkonnen;

import com.zarkonnen.catengine.Draw;
import com.zarkonnen.catengine.util.Clr;

public class Goodie extends Entity {
	public static final int GOODIE_SIZE = 30;
	
	public Item item;
	public Weapon weapon;
	public int age = 0;
	
	@Override
	public void tick(Level l) {
		age++;
	}
	
	private Goodie(Creature from) {
		x = from.x + from.w / 2 - GOODIE_SIZE / 2;
		y = from.y + from.h / 2 - GOODIE_SIZE / 2;
		w = GOODIE_SIZE;
		h = GOODIE_SIZE;
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
