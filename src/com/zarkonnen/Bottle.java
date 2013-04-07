package com.zarkonnen;

import com.zarkonnen.catengine.Draw;
import com.zarkonnen.catengine.util.Clr;

public class Bottle extends EmitterWall {
	public Bottle(int x, int baseline, int power, double dmgMult, Clr specialTint) {
		super(x, baseline - 32, 22, 32, power);
		weapon.element = Element.ACID;
		weapon.dmg *= dmgMult;
		weapon.shotSize = 3;
		this.specialTint = specialTint;
		destructible = true;
	}
	
	Clr specialTint;
	
	@Override
	public void draw(Draw d, Level l, double scrollX, double scrollY) {
		d.rect(BeeJar.GLASS, x + scrollX, y + scrollY, w, h);
		Clr c = specialTint;
		if (c == null) { c = Element.ACID.tint; }
		d.rect(c, x + scrollX + 2, y + scrollY + 5, w - 4, h - 7);
	}
	
	@Override
	public void takeDamage(Level l, Shot s) {
		super.takeDamage(l, s);
		if (hp <= 0) {
			killMe = true;
			l.soundRequests.add(new SoundRequest("shatter", x + w / 2, y + h / 2, 0.6));
			l.soundRequests.add(new SoundRequest("squelch", x + w / 2, y + h / 2, 0.6));
			for (int y2 = 0; y2 < 25; y2 += 3) { for (int x2 = 0; x2 < 18; x2++) {
				Shot shot = shoot(l, x + x2 + 2, y + y2 + 5, 100, 100);
				double dir = 2 * Math.PI * l.r.nextDouble();
				shot.popOnWorldHit = false;
				shot.dx = Math.cos(dir);
				shot.dy = Math.sin(dir);
				if (specialTint != null) {
					shot.tint = specialTint;
				}
				shot.lifeLeft *= 50 + l.r.nextInt(30);
				shot.gravityMult = 1.0;
				shot.freeAgent = true;
				shot.sprayProbability = 0;
			}}
			smash(l);
		}
	}
}
