package com.zarkonnen;

import com.zarkonnen.catengine.util.Clr;

public class Torch extends EmitterWall {
	public static final Clr GLOW = new Clr(255, 255, 127, 30);
	
	public Torch(int x, int y, int power) {
		super(x, y, 8, 24, power);
		tint = FurnitureStore.DEFAULT;
		weapon.element = Element.FIRE;
		weapon.tint = Element.FIRE.tint;
		weapon.dmg *= 0.01;
		weapon.shotSize = 3;
		destructible = true;
		weapon.shotSpeed *= 0.15;
		weapon.shotLife *= 0.3;
		isCollidedWith = false;
	}
	
	@Override
	public void tick(Level l) {
		if (l.r.nextInt(2) == 0) {
			double dir = l.r.nextDouble() * Math.PI * 2;
			Shot s = shoot(l, x + 1.75, y - 3, x + 4 + Math.cos(dir) * 100, y + Math.sin(dir) * 100 - 50);
			s.dx *= 0.2;
			s.dy *= 0.3;
			s.lifeLeft *= 0.6 + l.r.nextDouble() * 1.8;
			s.gravityMult = -0.12;
		}
		if (l.tick % 8 == 0) {
			Shot s = shoot(l, x + 2.25, y - 2.25, 0, 0);
			s.dx = 0;
			s.dy = 0;
			s.lifeLeft = 8;
			s.gravityMult = 0;
			s.remains = true;
			double sz = 12 + l.r.nextInt(20);
			s.tint = GLOW;
			s.x = x + 2 - sz / 2;
			s.y = y - 2.5 - sz / 2;
			s.w = sz;
			s.h = sz;
		}
	}
}
