package com.zarkonnen;

import com.zarkonnen.catengine.util.Clr;
import java.util.ArrayList;

public class BeeJar extends EmitterWall {
	public static final Clr GLASS = new Clr(220, 220, 200, 90);
	
	public ArrayList<Shot> bees = new ArrayList<Shot>();
	
	public BeeJar(int x, int baseline, int power) {
		super(x, baseline - 100, 100, 100, power);
		weapon.element = Element.FIRE;
		weapon.dmg *= 0.45;
		weapon.shotSize = 3;
		tint = GLASS;
		destructible = true;
		weapon.shotLife = 20;
	}
	
	@Override
	public void tick(Level l) {
		if (bees.isEmpty()) {
			for (int i = 0; i < 20; i++) {
				Shot s = shoot(l, x + 10 + l.r.nextInt(80), y + 10 + l.r.nextInt(80), 10, 10);
				double dir = l.r.nextDouble() * Math.PI * 2;
				s.dx = Math.cos(dir) * 2;
				s.dy = Math.sin(dir) * 2;
				s.ignoresWalls = true;
				s.freeAgent = true;
				bees.add(s);
			}
		}
		for (Shot b : bees) {
			b.lifeLeft = 500;
			if (b.x > x + 90) {
				b.dx *= -1;
				b.x = x + 90;
			}
			if (b.x < x + 10) {
				b.dx *= -1;
				b.x = x + 10;
			}
			if (b.y > y + 90) {
				b.dy *= -1;
				b.y = y + 90;
			}
			if (b.y < y + 10) {
				b.dy *= -1;
				b.y = y + 10;
			}
			if (l.r.nextInt(50) == 0) {
				double dir = l.r.nextDouble() * Math.PI * 2;
				b.dx = Math.cos(dir) * 2;
				b.dy = Math.sin(dir) * 2;
			}
		}
	}
	
	@Override
	public void takeDamage(Level l, Shot s) {
		if (s.shooter == meatSource) { return; }
		super.takeDamage(l, s);
		if (hp <= 0) {
			killMe = true;
			l.soundRequests.add(new SoundRequest("shatter", x + w / 2, y + h / 2, 1.0));
			smash(l);
			weapon.homing = true;
		}
	}
}
