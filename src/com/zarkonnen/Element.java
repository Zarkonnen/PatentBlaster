package com.zarkonnen;

import com.zarkonnen.catengine.util.Clr;

public enum Element {
	STEEL(new Clr(180, 180, 180), 1.25, 1.8, "STEEL"),
	ACID(new Clr(120, 200, 30), 1.0, 1.25, "ACID"),
	FIRE(new Clr(255, 80, 10), 1.0, 1.0, "FIRE"),
	ICE(new Clr(100, 100, 255), 1.0, 1.0, "ICE");
	
	public final Clr tint;
	public final double dmgMult;
	public final double speedMult;
	public final String shotSound;

	private Element(Clr tint, double dmgMult, double speedMult, String shotSound) {
		this.tint = tint;
		this.dmgMult = dmgMult;
		this.speedMult = speedMult;
		this.shotSound = shotSound;
	}
}
