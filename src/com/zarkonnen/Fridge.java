package com.zarkonnen;

import com.zarkonnen.catengine.util.Clr;
import com.zarkonnen.catengine.util.Pt;

public class Fridge extends EmitterWall {
	public Fridge(Level l, int x, int y, int w, int h) {
		super(x, y, w, h, l.power);
		tint = new Clr(74, 74, 77);
		img = PatentBlaster.FURN_IMGS.get("fridge");
		weapon.element = Element.ICE;
		weapon.tint = Element.ICE.tint;
		weapon.dmg *= 0.001;
		weapon.shotSize = 2;
		destructible = true;
		weapon.shotSpeed *= 0.15;
		weapon.shotLife *= 0.3;
		floor();
	}
	
	@Override
	public void tick(Level l) {
		if (l.r.nextInt(20) == 0) {
			shoot(l);
		}
	}
	
	@Override
	public void takeDamage(Level l, Shot s) {
		if (s.shooter == meatSource) { return; }
		super.takeDamage(l, s);
		if (hp <= 0) {
			killMe = true;
			for (int i = 0; i < 50; i++) {
				shoot(l);
			}
			l.soundRequests.add(new SoundRequest("shatter", x + w / 2, y + h / 2, 1.0));
			for (int gy = 5; gy < h - 5; gy += 5) {
				if (gy % 4 == 0) { continue; }
				for (int gx = 5; gx < w - 5; gx += 5) {
					bleed(l, x + gx, y + gy, Barrel.MEAT_TINT, 5, 2);
				}
			}
			smash(l);
		}
	}

	private void shoot(Level l) {
		Pt ep = edgePt(l, 3);
		Shot s = shoot(l, ep.x, ep.y, x + (ep.x - x - w / 2) * 100, y + (ep.y - y - h / 2) * 100);
		s.gravityMult = 0.8;
		s.bounciness = 0.5;
		s.popOnWorldHit = false;
	}
}
