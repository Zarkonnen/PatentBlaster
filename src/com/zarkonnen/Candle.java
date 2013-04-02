package com.zarkonnen;

import com.zarkonnen.catengine.util.Clr;

public class Candle extends EmitterWall {
	public static final Clr GLOW = new Clr(255, 255, 127, 30);
	
	public Candle(int x, int y, int power) {
		super(x, y, 4, 20, power);
		tint = new Clr(150, 140, 120);
		weapon.element = Element.FIRE;
		weapon.tint = Element.FIRE.tint;
		weapon.dmg *= 0.001;
		weapon.shotSize = 2;
		destructible = true;
		weapon.shotSpeed *= 0.15;
		weapon.shotLife *= 0.3;
		isCollidedWith = false;
	}
	
	@Override
	public void tick(Level l) {
		if (l.r.nextInt(5) == 0) {
			double dir = l.r.nextDouble() * Math.PI * 2;
			Shot s = shoot(l, x + 1.25, y - 2.25, x + 4 + Math.cos(dir) * 100, y + Math.sin(dir) * 100 - 50);
			s.dx *= 0.12;
			s.dy *= 0.12;
			s.lifeLeft *= 0.6 + l.r.nextDouble();
			s.gravityMult = -0.12;
		}
		if (l.tick % 10 == 0) {
			Shot s = shoot(l, x + 1.25, y - 2.25, 0, 0);
			s.dx = 0;
			s.dy = 0;
			s.lifeLeft = 10;
			s.gravityMult = 0;
			s.remains = true;
			double sz = 8 + l.r.nextInt(10);
			s.tint = GLOW;
			s.x = x + 2 - sz / 2;
			s.y = y - 2.5 - sz / 2;
			s.w = sz;
			s.h = sz;
		}
	}
}
