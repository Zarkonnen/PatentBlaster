package com.zarkonnen;

import com.zarkonnen.catengine.util.Clr;
import java.util.Random;

public enum Element {
	STEEL(new Clr(180, 180, 180), 1.4, 1.5, "STEEL"),
	ACID(new Clr(120, 200, 30), 1.2, 1.05, "ACID"),
	FIRE(new Clr(255, 80, 10), 1.0, 1.0, "FIRE"),
	ICE(new Clr(100, 100, 255), 0.85, 0.8, "ICE"),
	CURSED(new Clr(70, 150, 120), new Clr(70, 150, 120, 127), 0.5, 1.2, "CURSED"),
	BLESSED(new Clr(255, 255, 240), new Clr(255, 255, 240, 127), 1.0, 1.2, "BLESSED");
	
	public final Clr tint;
	public final Clr glowTint;
	public final double dmgMult;
	public final double speedMult;
	public final String shotSound;

	private Element(Clr tint, double dmgMult, double speedMult, String shotSound) {
		this.tint = tint;
		this.dmgMult = dmgMult;
		this.speedMult = speedMult;
		this.shotSound = shotSound;
		this.glowTint = null;
	}
	
	private Element(Clr tint, Clr glowTint, double dmgMult, double speedMult, String shotSound) {
		this.tint = tint;
		this.dmgMult = dmgMult;
		this.speedMult = speedMult;
		this.shotSound = shotSound;
		this.glowTint = glowTint;
	}
	
	public static Element pick(Random r) {
		return values()[r.nextInt(5)];
	}
}
