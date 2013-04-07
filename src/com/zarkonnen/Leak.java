package com.zarkonnen;

public class Leak extends EmitterWall {
	public Leak(int x, int y, int power, Element el, double dmgMult) {
		super(x, y, 20, 5, power);
		weapon.element = el;
		weapon.dmg *= dmgMult;
		weapon.shotSize = 3;
		weapon.shotLife = 1000;
	}
	
	@Override
	public void tick(Level l) {
		if (l.r.nextInt(200) == 0) {
			Shot s = shoot(l, x + w / 2 - 1, y + h + 1, 100, 100);
			s.gravityMult = 1.0;
			s.dx = l.r.nextDouble() * 0.06 - 0.03;
			s.dy = 0;
			s.popOnWorldHit = false;
			s.sprayProbability = 0;
		}
	}
}
