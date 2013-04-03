package com.zarkonnen;

import com.zarkonnen.catengine.util.Pt;

public class EvilBook extends Book {
	public static final String[] EVIL_TITLES = { "[000000ff88]N\nE\nC\nR\nO", "[000000ff88]U\nN\nA\nU\nS", "[000000ff88]L\nE\nN\nG", "[000000ff88]F\nU\nL\nV\nA" }; 
	
	public EvilBook(int x, int baseline, Level l) {
		super(x, baseline, l);
		w = 30;
		y -= h;
		h *= 2;
		title = EVIL_TITLES[l.r.nextInt(EVIL_TITLES.length)];
		weapon.dmg *= 200;
		weapon.element = Element.CURSED;
		weapon.shotSize = 3;
		weapon.shotSpeed *= 0.3;
	}
	
	@Override
	public void tick(Level l) {
		if (l.r.nextInt(20) == 0) {
			shoot(l);
		}
	}
	
	@Override
	public void takeDamage(Level l, Shot s) {
		boolean prevKM = killMe;
		super.takeDamage(l, s);
		if (hp <= 0 && !prevKM) {
			killMe = true;
			l.soundRequests.add(new SoundRequest("explode", x + w / 2, y + h / 2, 1.0));
			for (int i = 0; i < 50; i++) {
				shoot(l);
			}
			for (int i = 0; i < 50; i++) {
				Shot shot = shoot(l);
				shot.dx *= l.r.nextDouble() * 8;
				shot.dy *= l.r.nextDouble() * 8;
			}
		}
	}
	
	private Shot shoot(Level l) {
		Pt ep = edgePt(l, 3);
		Shot s = shoot(l, ep.x, ep.y, x + (ep.x - x - w / 2) * 100, y + (ep.y - y - h / 2) * 100);
		s.gravityMult = -0.2;
		return s;
	}
}
