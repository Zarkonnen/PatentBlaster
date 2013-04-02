package com.zarkonnen;

public class Crate extends Wall {
	Item item;
	Weapon weapon;
	
	public Crate(Level l, int x, int y) {
		super(x, y, FurnitureStore.CRATE.w, FurnitureStore.CRATE.h);
		tint = FurnitureStore.DEFAULT;
		destructible = true;
		
		int roll = l.r.nextInt(10);
		if (roll <= 1) {
			item = Item.make(l.r.nextInt(), l.power, l.player, null);
		}
		if (roll == 2) {
			weapon = Weapon.make(l.r.nextInt(), l.power, true);
		}
	}
	
	@Override
	public void tick(Level l) {
		if (!l.player.hasSeenCrate && Math.abs(l.player.x + l.player.w / 2 - x - w / 2) < 512) {
			l.player.hasSeenCrate = true;
			floatText(l, "Seconds Until Crate: " + PatentBlaster.round(PatentBlaster.gameTicks * 1.0 / PatentBlaster.FPS, 2) + "!");
		}
	}
	
	@Override
	public void takeDamage(Level l, Shot s) {
		super.takeDamage(l, s);
		if (hp <= 0) {
			killMe = true;
			l.soundRequests.add(new SoundRequest("shatter", x + w / 2, y + h / 2, 1.0));
			smash(l);
			Goodie g = null;
			if (weapon != null) {
				g = new Goodie(this, weapon);
			}
			if (item != null) {
				g = new Goodie(this, item);
			}
			if (g != null) {
				l.goodies.add(g);
				l.soundRequests.add(new SoundRequest("drop", x + w / 2, y + h / 2, 1.0));
			}
		}
	}
}
