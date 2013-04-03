package com.zarkonnen;

import com.zarkonnen.catengine.util.Clr;

public class ChinaDog extends Wall {
	public ChinaDog(int x, int baseline) {
		super(x, baseline - 40, 30, 40);
		hp = 1;
		destructible = true;
		tint = Clr.LIGHT_GREY;
	}
	
	@Override
	public void takeDamage(Level l, Shot s) {
		super.takeDamage(l, s);
		if (hp <= 0 && !killMe) {
			killMe = true;
			l.soundRequests.add(new SoundRequest("shatter", x + w / 2, y + h / 2, 0.5));
			smash(l);
		}
	}
}
