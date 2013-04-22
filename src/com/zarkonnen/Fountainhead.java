package com.zarkonnen;

import com.zarkonnen.catengine.util.Clr;

public class Fountainhead extends EmitterWall {
	public Fountainhead(int x, int y, int power, Element el, Clr c) {
		super(x, y, 0, 0, power);
		tint = Clr.LIGHT_GREY;
		weapon.shotSize = 2;
		weapon.shotSpeed *= 0.15;
		weapon.shotLife *= 0.3;
		weapon.dmg = 0;
		weapon.element = el;
		weapon.tint = c;
		isCollidedWith = false;
	}
	
	@Override
	public void tick(Level l) {
		double dir = l.r.nextDouble() * Math.PI * 2;
		Shot s = shoot(l, x, y - 1, x + Math.cos(dir) * 100, y + Math.sin(dir) * 100 - 100);
		s.dx *= 0.65;
		s.dy *= 4.5 + l.r.nextDouble();
		s.lifeLeft = 300;
		s.gravityMult = 0.5;
		if (l.tick % 10 == 0) {
			s = shoot(l, x, y, 0, 0);
			s.dx = 0;
			s.dy = 0;
			s.lifeLeft = 10;
			s.gravityMult = 0;
			s.remains = true;
			s.x = x - 50;
			s.y = y + 57;
			s.w = 100;
			s.h = 2;
			s.glowTint = null;
			if (weapon.element == Element.ICE) {
				s.extinguishes = true;
			}
		}
	}
}
