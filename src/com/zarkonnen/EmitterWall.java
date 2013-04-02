package com.zarkonnen;

import com.zarkonnen.catengine.util.Clr;
import com.zarkonnen.catengine.util.Pt;

public class EmitterWall extends Wall {
	public Creature meatSource;
	public Weapon weapon;

	public EmitterWall(int x, int y, int w, int h, int power) {
		super(x, y, w, h);
		meatSource = new Creature();
		meatSource.maxHP = (int) (Const.BASE_HP * Const.powerLvl(power)) + 1;
		meatSource.x = x;
		meatSource.y = y;
		meatSource.w = 2;
		meatSource.h = 2;
		
		weapon = new Weapon();
		weapon.dmg = Const.BASE_DMG * Const.powerLvl(power);
		weapon.shotSize = 4;
		weapon.shotLife = 75;
		weapon.shotSpeed = 6;
		weapon.reload = 100;
		
		hp = (int) (Const.BASE_BARREL_HP * Const.powerLvl(power)) + 1;
		initialHP = hp;
	}
	
	Shot shoot(Level l, double srcX, double srcY, double tX, double tY) {
		meatSource.x = srcX - 1;
		meatSource.y = srcY - 1;
		Shot s = new Shot(l, weapon, meatSource, tX, tY);
		s.x = srcX;
		s.y = srcY;
		s.freeAgent = true;
		s.sprayProbability /= 4;
		l.shotsToAdd.add(s);
		return s;
	}
	
	Shot bleed(Level l, double srcX, double srcY, Clr tint, double chunkSize, double speed) {
		Shot s = new Shot(tint, chunkSize, chunkSize, false, 3000 + l.r.nextInt(1000), srcX, srcY, l.r.nextDouble() * speed * 2 - speed, l.r.nextDouble() * speed * 2 - speed, 1.0, meatSource, false, false, false, srcX, srcY, 0);
		l.shotsToAdd.add(s);
		return s;
	}
	
	Pt edgePt(Level l, double dist) {
		switch (l.r.nextInt(4)) {
			case 0: // top
				return new Pt(x + w * l.r.nextDouble(), y - dist);
			case 1: // bottom
				return new Pt(x + w * l.r.nextDouble(), y + h + dist);
			case 2: // left
				return new Pt(x - dist, y + l.r.nextDouble() * h);
			case 3: // right
				return new Pt(x + w + dist, y + l.r.nextDouble() * h);
		}
		return new Pt(0, 0);
	}
}
