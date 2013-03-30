package com.zarkonnen;

import com.zarkonnen.catengine.util.Clr;

public class Wall extends Entity {
	public Wall(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.tint = Clr.GREY;
		gravityMult = 0;
		collides = false;
		ignoresWalls = true;
	}
	
	public boolean destructible;
	public int initialHP;
	public int hp;
	
	public void doDamage(Level l, Shot s) {
		if (!destructible) { return; }
		if (s.weapon == null) { return; }
		hp -= s.weapon.dmg * s.dmgMultiplier;
	}
}
