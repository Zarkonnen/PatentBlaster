package com.zarkonnen;

import com.zarkonnen.catengine.Draw;
import com.zarkonnen.catengine.util.Clr;
import java.util.ArrayList;
import java.util.Random;

public class Barrel extends Entity {
	public static final Clr GLUE_TINT = new Clr(220, 220, 190);
	public static enum Type {
		GLUE(4, "squelch", true) {
			@Override
			public Shot makeShot(Level l, Barrel b, double x, double y) {
				Shot s = new Shot(GLUE_TINT, chunkSize, chunkSize, false, 10000 + l.r.nextInt(1000), x, y, l.r.nextDouble() * 10 - 5, l.r.nextDouble() * 10 - 8, 1.0, null, false, false, false, x, y, 0);
				s.stickiness = Const.powerLvl(l.power);
				s.friction = 0.9;
				return s;
			}
		},
		LIQUID_NITROGEN(3, "explode", true) {
			@Override
			public Shot makeShot(Level l, Barrel b, double x, double y) {
				double dir = l.r.nextDouble() * 2 * Math.PI;
				Shot s = new Shot(l, b.weapon, b.meatSource, x + Math.cos(dir) * 100, y + Math.sin(dir) * 100);
				s.dx *= 0.5 + l.r.nextDouble();
				s.dy *= 0.5 + l.r.nextDouble();
				s.gravityMult = 0.3;
				s.x = x;
				s.y = y;
				s.freeAgent = true;
				s.lifeLeft = s.lifeLeft / 2 + l.r.nextInt(s.lifeLeft / 2);
				s.sprayProbability /= 4;
				return s;
			}
		},
		HORSE_MEAT(9, "squelch", true) {
			final Clr TINT = new Clr(167, 82, 66);
			@Override
			public Shot makeShot(Level l, Barrel b, double x, double y) {
				Shot s = new Shot(TINT, chunkSize, chunkSize, false, 3000 + l.r.nextInt(1000), x, y, l.r.nextDouble() * 4 - 2, l.r.nextDouble() * 4 - 2, 1.0, b.meatSource, false, false, false, x, y, 0);
				s.friction = 0.985;
				return s;
			}
		},
		;
		
		public static Type[] available() {
			if (PatentBlaster.DEMO) {
				ArrayList<Barrel.Type> ts = new ArrayList<Barrel.Type>();
				for (Type t : values()) {
					if (t.inDemo) {
						ts.add(t);
					}
				}
				return ts.toArray(new Type[ts.size()]);
			} else {
				return values();
			}
		}
		
		public final int chunkSize;
		public final String breakSound;
		public final boolean inDemo;
		public abstract Shot makeShot(Level l, Barrel b, double x, double y);

		private Type(int chunkSize, String breakSound, boolean inDemo) {
			this.breakSound = breakSound;
			this.chunkSize = chunkSize;
			this.inDemo = inDemo;
		}
	}
	
	public int initialHP;
	public int hp;
	public Type t;
	public Weapon weapon;
	public Creature meatSource;
	public int textShift;
	
	@Override
	public void draw(Draw d, Level l, double scrollX, double scrollY) {
		super.draw(d, l, scrollX, scrollY);
		d.text("[bg=dddd88][BLACK]" + t.name().substring(textShift, Math.min(t.name().length(), 6) + textShift).replace("_", " "), PatentBlaster.SMOUNT, x + scrollX + 1, y + scrollY + 20);
	}
	
	public Barrel(Type t, long seed, int power, double x, double y, Random r) {
		if (t.name().length() > 6) {
			textShift = r.nextInt(t.name().length() - 5);
		}
		this.t = t;
		this.x = x;
		this.y = y;
		hp = (int) (Const.BASE_BARREL_HP * Const.powerLvl(power)) + 1;
		initialHP = hp;
		this.w = 42;
		this.h = 60;
		tint = Clr.GREY;
		meatSource = new Creature();
		meatSource.maxHP = (int) (Const.BASE_HP * Const.powerLvl(power)) + 1;
		meatSource.x = x;
		meatSource.y = y;
		meatSource.w = w;
		meatSource.h = h;
		
		weapon = new Weapon();
		weapon.dmg = (int) (0.4 * Const.BASE_DMG * Const.powerLvl(power)) + 1;
		weapon.shotSize = 4;
		weapon.shotLife = 75;
		weapon.shotSpeed = 6;
		weapon.reload = 100;
		
		switch (t) {
			case LIQUID_NITROGEN:
				weapon.element = Element.ICE;	
				weapon.tint = Element.ICE.tint;	
				break;
		}
	}
	
	public void doDamage(Level l, Shot s) {
		if (s.weapon == null) { return; }
		hp -= s.weapon.dmg * s.dmgMultiplier;
		if (hp < 0) {
			explode(l);
			killMe = true;
		} else {
			if (s.weapon.dmg * s.dmgMultiplier > initialHP / 7) {
				l.soundRequests.add(new SoundRequest("splt", x + w / 2, y + h / 2, 1.0));
			}
		}
	}
	
	private void explode(Level l) {
		l.soundRequests.add(new SoundRequest("shatter", x + w / 2, y + h / 2, 1.0));
		l.soundRequests.add(new SoundRequest(t.breakSound, x + w / 2, y + h / 2, 1.0));
		for (int gy = 0; gy < 56 / t.chunkSize; gy++) {
			for (int gx = 0; gx < 38 / t.chunkSize; gx++) {
				l.shotsToAdd.add(t.makeShot(l, this, x + gx * t.chunkSize, y + gy * t.chunkSize));
			}
		}
		for (int gy = 0; gy < 6; gy++) {
			for (int gx = 0; gx < 4; gx++) {
				Shot s = new Shot(tint, 10, 10, false, 30 + l.r.nextInt(60), x + gx * 10 - 6, y + gy * 10 - 6, l.r.nextDouble() - 0.5, l.r.nextDouble() - 0.5, 1.0, null, false, false, false, x + gx * 10 - 6, y + gy * 10 - 6, 0);
				l.shotsToAdd.add(s);
			}
		}
	}
}
