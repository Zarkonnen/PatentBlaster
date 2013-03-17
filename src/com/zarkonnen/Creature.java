package com.zarkonnen;

import com.zarkonnen.catengine.Draw;
import com.zarkonnen.catengine.Fount;
import com.zarkonnen.catengine.util.Clr;
import com.zarkonnen.catengine.util.Pt;
import com.zarkonnen.catengine.util.Utils.Pair;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import static com.zarkonnen.PatentBlaster.round;
import static com.zarkonnen.Const.*;
import com.zarkonnen.catengine.Img;
import java.util.Collections;
import java.util.LinkedList;

public class Creature extends Entity implements HasDesc {
	public static final double MAX_SPEED = 5;
	public static final double HOP_BONUS = 1.25;
	public static final int AIR_STEERING = 25;
	public static final int ABOVE_PREF = Level.GRID_SIZE * 5 / 2;
	public static final Clr JAR_CLR = new Clr(110, 90, 85);
	public static final Clr FROZEN_CLR = new Clr(100, 110, 200);;
	public static final Clr FROZEN_CUBE_CLR = new Clr(100, 110, 200, 120);
	
	public int imgIndex;
	public Img bImg, flippedBImg;
	public Img flippedImg;
	public ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	public ArrayList<Item> items = new ArrayList<Item>();
	public Element resistance;
	public Weapon weapon;
	public int hp;
	public int maxHP;
	public double speed;
	public double hpRegen;
	public boolean charges;
	public boolean explodes;
	public boolean dodges;
	public boolean massive;
	public boolean trackingAim;
	public boolean randomShootDelay;
	public boolean resurrects;
	public double eating;
	public Creature finalForm;
	public boolean splitsIntoFour;
	public boolean alwaysOnFire;
	public boolean flees;
	public boolean reproduces;
	public boolean reviens;
	public boolean jar;
	public boolean thief;
	public boolean absorber;
	public MoveMode moveMode;
	
	public boolean hovered = false;
	public boolean flown = false;
	
	public boolean unsplorted = false;
	
	public double heat;
	
	public int animCycle;
	public int animCycleLength;
	public double angle = 0;
	
	public int onFire = 0;
	public int frozen = 0;
	public Weapon fireWeapon;
	public Creature fireShooter;
	
	public boolean playerControlled;
	public boolean flipped;
	
	public boolean canSeeStats = false; // qqDPS Update with items being given!
	
	public double accumulatedRegen = 0;
	public boolean spotted = false;
	public int knockedBack = 0;
	public int ticksSinceHit = 1000;
	public boolean charging = false;
	
	public boolean dropWeapon = false;
	public boolean dropItem = false;
	
	public HasDesc newThing = null;
	public int newThingTimer = 0;
	
	public int shootDelay = 0;
	public int todaysShootDelay = 0;
	
	public boolean hoverPowerOff = false;
	public int voice = -1;
	public int voiceTimer = 0;
	
	public int fireSoundTimer = 0;
	public boolean fleeing = false;
	public boolean changedGun;
	public int flyHeightBonus;
	
	public ArrayList<Creature> babies = new ArrayList<Creature>();
	public int reproCooldown = PatentBlaster.FPS * 4;
	
	public boolean fuse = false;
	public int fuseTimer = 0;
	
	public Item stolenItem = null;
	public Weapon stolenWeapon = null;
	public int showingWeaponSwitchInfo;
	public boolean isZombie;
	public Creature lastShooter;
	
	public long seed;
	
	public int evading;
	public boolean evadingLeft;
	
	public int eatSoundTimer = 0;
	
	public int absorbTimer = 0;
	public boolean noTargetInRangeWarning;
	
	public MoveMode playerMoveModeSelection = MoveMode.SLIDE;
	public int ticksSinceGainingHover = 0;
	public int ticksSinceGainingFlight = 0;
	
	public int flyerHysteresis = 0;
	
	public double size;
	public double normalW;
	public double normalH;
	public int jumpElongate;
	
	public int slipperiness = 0;
	
	public Shot lastShot;
	public int flamethrowerTicks = 61;
	
	public LinkedList<Shot> stuckShots = new LinkedList<Shot>();
	
	public String encounterMessage = null;
	
	public double targetX, targetY;
	int ticksInView = 0;
	
	public int xDir = 0;
	public int ticksSinceDirChange = 1000;
	
	public static final int MIN_DIR_CHANGE_WAIT = 20;
	
	public Pt targetIntersect(Level l, double fromX, double fromY, double shotSpeed, int shotLife) {
		Creature sim = new Creature();
		sim.x = x;
		sim.y = y;
		sim.w = w;
		sim.h = h;
		sim.dx = dx;
		sim.dy = dy;
		sim.gravityMult = gravityMult;
		if (realMoveMode() == MoveMode.HOVER) { sim.gravityMult = 0; sim.dy = 0; }
		
		int tick = 0;
		while (tick <= shotLife) {
			l.physics(sim);
			tick++;
			double xDelta = fromX - sim.x - sim.w / 2;
			double yDelta = fromY - sim.y - sim.h / 2;
			double distToShooter = Math.sqrt(xDelta * xDelta + yDelta * yDelta);
			double distTravelled = tick * shotSpeed;
			if (Math.abs(distToShooter - distTravelled) < sim.w / 4 + sim.h / 4) {
				return new Pt(sim.x + sim.w / 2, sim.y + sim.h / 2);
			}
		}
		
		return null;
	}
	
	public double squish() {
		if (resistance != null) {
			switch (resistance) {
				case ACID:
					return 0.18;
				case STEEL:
					return 0;
			}
		}
		return 0.08;
	}
	
	public boolean fireproof() {
		return resistance(Element.FIRE) >= 0.5 || jar;
	}
	
	public boolean iceproof() {
		return resistance(Element.ICE) >= 0.5 || jar;
	}
	
	public boolean doesResurrect() {
		if (resurrects) { return true; }
		for (Item it : items) {
			if (it.resurrect) { return true; }
		}
		return false;
	}
		
	public double totalSpeed() {
		double s = speed;
		for (Item it : items) { s *= it.speedMult; }
		if (s > MAX_SPEED) { s = MAX_SPEED; }
		if (getStickiness() > totalMaxHP() / 4) {
			s = Math.max(0, (s - 1) / 3);
		}
		return s * (charging ? 2 : 1);
	}
	
	public int totalMaxHP() {
		int m = maxHP;
		for (Item it : items) { m += it.hpBonus; }
		return m;
	}
	
	public double baseHPRegen() {
		double reg = hpRegen;
		for (Item it : items) { reg += it.hpRegen; }
		return reg;
	}
	
	public double totalHPRegen() {
		return baseHPRegen() * (ticksSinceHit < 5 ? 0 : ticksSinceHit > PatentBlaster.FPS * 5 ? 100 : 1);
	}
	
	public MoveMode realMoveMode() {
		if (playerControlled) {
			return playerMoveModeSelection;
		}
		return canFly() ? MoveMode.FLY : canHover() ? MoveMode.HOVER : moveMode;
	}
	
	public int shieldAmt() {
		int a = 0;
		for (Item it : items) {
			if (it.shield) {
				if (it.shieldReload > it.shieldReloadTime - 10) {
					return 120;
				}
				if (it.shieldReload == 0) {
					a = 30;
				}
			}
		}
		return a;
	}
	
	public double mouthX() {
		if (flipped) {
			return x + w * (1 - PatentBlaster.IMG_MOUTH_X[imgIndex]);
		} else {
			return x + w * PatentBlaster.IMG_MOUTH_X[imgIndex];
		}
	}
	
	public double mouthY() {
		return y + h * PatentBlaster.IMG_MOUTH_Y[imgIndex];
	}
	
	public double gunX() {
		if (flipped) {
			return x + w * (1 - PatentBlaster.IMG_SHOOT_X[imgIndex]);
		} else {
			return x + w * PatentBlaster.IMG_SHOOT_X[imgIndex];
		}
	}
	
	public double gunY() {
		return y + h * PatentBlaster.IMG_SHOOT_Y[imgIndex];
	}
	
	public void jump() {
		dy = -totalSpeed() * 0.2 - HOP_BONUS * (getStickiness() > totalMaxHP() / 4 ? 0.1 : 1);
		jumpElongate = maxPress;
	}
	
	@Override
	public void draw(Draw d, Level l, double scrollX, double scrollY) {
		if (hp <= 0) { return; }
		int shield = shieldAmt();
		
		if (fuse && frozen == 0 && (l.tick / 2) % 2 == 0) {
			scrollY -= 3;
		}
		
		Clr t = tint;
		
		if (jar) {
			t = JAR_CLR;
		}

		if (onFire > 0 && (l.tick / 8) % 2 == 0) {
			t = Element.FIRE.tint;
		}
		if (PatentBlaster.lowGraphics) {
			if (frozen > 0) {
				t = FROZEN_CLR;
			}
			if (shield > 0) {
				int amt = 100 + ((l.tick / 5) % 2) * 35 + shield;
				t = new Clr(amt, amt, amt);
			}
		}
		
		double imgW = w / PatentBlaster.IMG_W[imgIndex];
		double imgH = h / PatentBlaster.IMG_H[imgIndex];
		double imgX = x + scrollX - imgW / 2 + w / 2;
		double imgY = y + scrollY - imgH + h;
		
		Img theImg = flipped ? flippedImg : img;
		if (!jar && PatentBlaster.ANIM[imgIndex] && (l.tick % PatentBlaster.ANIM_LENGTH[imgIndex] < PatentBlaster.ANIM_B_LENGTH[imgIndex])) {
			if (bImg == null) {
				bImg = PatentBlaster.CREATURE_IMGS.get(PatentBlaster.IMG_NAMES[imgIndex] + "_b");
				flippedBImg = bImg.flip();
			}
			theImg = flipped ? flippedBImg : bImg;
		}
		
		d.blit(theImg, t, imgX, imgY, imgW, imgH, angle);
		
		for (Shot s : stuckShots) {
			double delta = flipped ? w - s.x * 2 - s.w : 0;
			s.draw(d, l, scrollX + x + delta, scrollY + y);
		}
		
		if (!PatentBlaster.lowGraphics) {
			if (frozen > 0) {
				d.rect(FROZEN_CUBE_CLR, scrollX + x - w / 10, scrollY + y - h / 10, w + w / 5, h + h / 5, angle);
			}
			if (shield > 0) {
				d.rect(new Clr(255, 255, 255, shield), scrollX + x - w / 10, scrollY + y - h / 10, w + w / 5, h + h / 5, angle);
			}
		}
	}
	
	public void drawBars(Draw d, Level l, double scrollX, double scrollY) {
		if (hp <= 0) { return; }
		int tmh = totalMaxHP();
		if ((l.player.canSeeStats || this == l.player) && (hp < tmh * 0.9 || heat > tmh / 16 || -heat > tmh / 16)) {
			d.rect(weapon.reloadLeft == 0 ? Clr.WHITE : Clr.LIGHT_GREY, x + scrollX, y + scrollY + h - 10, w, 8);
			Clr c = Clr.GREEN;
			int adjHP = Math.min(hp, tmh);
			if (adjHP < tmh / 2) {
				c = Clr.YELLOW;
				if (adjHP < tmh / 4) {
					c = new Clr(255, 127, 0);
					if (adjHP < tmh / 8) {
						c = Clr.RED;
					}
				}
			}
			d.rect(c, x + scrollX + 2, y + scrollY + h - 8, (w - 4) * adjHP / tmh, 4);
			if (heat > tmh / 16 && heat < tmh / 4 && !fireproof() && onFire == 0) {
				d.rect(weapon.reloadLeft == 0 ? Clr.WHITE : Clr.LIGHT_GREY, x + scrollX, y + scrollY + h - 15, w, 6);
				d.rect(Element.FIRE.tint, x + scrollX + 2, y + scrollY + h - 14, (w - 4) * (heat) * 4 / tmh, 4);
			}
			if (heat < -tmh / 16 && heat > -tmh / 4 && !iceproof() && frozen == 0) {
				d.rect(weapon.reloadLeft == 0 ? Clr.WHITE : Clr.LIGHT_GREY, x + scrollX, y + scrollY + h - 15, w, 6);
				d.rect(Element.ICE.tint, x + scrollX + 1, y + scrollY + h - 14, (w - 4) * (- heat) * 4 / tmh, 4);
			}
		}
	}
	
	public Clr bloodClr() {
		if (jar) { return JAR_CLR; }
		if (resistance != null) { return resistance.tint; }
		return Clr.RED;
	}
	
	public ArrayList<Shot> explode(Level l) {
		hp = -1;
		sound(frozen > 0 || jar ? "shatter" : "squelch", l);
		if (playerControlled && lastShooter != null) {
			l.texts.add(new FloatingText("KILLED BY " + lastShooter.name().toUpperCase(), x + w / 2, y));
		}
		if (explodes) {
			sound("explode", l);
		}
		if (explodes) {
			for (int i = 0; i < 20; i++) {
				double dir = l.r.nextDouble() * 2 * Math.PI;
				Shot s = shoot(x + w / 2 + Math.cos(dir) * 100, y + h / 2 + Math.sin(dir) * 100, l);
				s.dx *= 0.5 + l.r.nextDouble();
				s.dy *= 0.5 + l.r.nextDouble();
				s.gravityMult = 0.3;
			}
		}
		
		double gw = w / PatentBlaster.IMG_W[imgIndex];
		double gh = h / PatentBlaster.IMG_H[imgIndex];
		double gx = x - gw / 2 + w / 2;
		double gy = y - gh + h;
		
		double targetGw = w / PatentBlaster.IMG_W[imgIndex];
		double targetGh = h / PatentBlaster.IMG_H[imgIndex];
		double targetGx = x - gw / 2 + w / 2;
		double targetGy = y - gh + h;
		
		Clr blood = bloodClr();
		boolean[][] grid = grid();
		boolean[][] reformGrid = grid;
		int skipAmt = finalForm != null || doesResurrect() || reviens ? 1 : PatentBlaster.shotDivider();
		ArrayList<Shot> bloodShots = new ArrayList<Shot>();
		ArrayList<Pair<Integer, Integer>> reformPoints = new ArrayList<Pair<Integer, Integer>>();
		if (finalForm != null && !doesResurrect()) {
			reformGrid = finalForm.grid();
			targetGw = finalForm.w / PatentBlaster.IMG_W[finalForm.imgIndex];
			targetGh = finalForm.h / PatentBlaster.IMG_H[finalForm.imgIndex];
			targetGx = gx + gw / 2 - targetGw / 2;
			targetGy = gy + gh / 2 - targetGh / 2;
		}
		for (int by = 0; by < Grids.GRID_SZ; by++) {
			for (int bx = 0; bx < Grids.GRID_SZ; bx++) {
				if (reformGrid[by][bx]) {
					reformPoints.add(new Pair<Integer, Integer>(bx, by));
				}
			}
		}
		
		int reformGridIndex = 0;
		
		double gibsSpeedMult = explodes ? 5 : 1;
		
		for (int by = 0; by < Grids.GRID_SZ; by += skipAmt) {
			for (int bx = 0; bx < Grids.GRID_SZ; bx += skipAmt) {
				if (grid[by][bx]) {
					if (reformGridIndex < reformPoints.size()) {
						double sx = gx + bx * gw / Grids.GRID_SZ;
						double sy = gy + by * gh / Grids.GRID_SZ;
						double tx = targetGx + reformPoints.get(reformGridIndex).a * targetGw / Grids.GRID_SZ;
						double ty = targetGy + reformPoints.get(reformGridIndex).b * targetGh / Grids.GRID_SZ;
						bloodShots.add(new Shot(blood, gw / Grids.GRID_SZ, gh / Grids.GRID_SZ, false, 120 + l.r.nextInt(80), sx, sy, (l.r.nextDouble() - 0.5) * gibsSpeedMult, (l.r.nextDouble() - 0.5) * gibsSpeedMult, 1.0, this, doesResurrect(), reviens, finalForm != null, tx, ty, frozen > 0 ? l.r.nextInt(40) + 10 : 0));
						reformGridIndex += skipAmt;
					}
				}
			}
		}
		
		while (reformGridIndex < reformPoints.size()) {
			double sx = gx + gw / 2;
			double sy = gy + gh / 2;
			double tx = targetGx + reformPoints.get(reformGridIndex).a * targetGw / Grids.GRID_SZ;
			double ty = targetGy + reformPoints.get(reformGridIndex).b * targetGh / Grids.GRID_SZ;
			bloodShots.add(new Shot(blood, gw / Grids.GRID_SZ, gh / Grids.GRID_SZ, false, 120 + l.r.nextInt(80), sx, sy, (l.r.nextDouble() - 0.5) * gibsSpeedMult, (l.r.nextDouble() - 0.5) * gibsSpeedMult, 1.0, this, doesResurrect(), reviens, finalForm != null, tx, ty, frozen > 0 ? l.r.nextInt(40) + 10 : 0));
			reformGridIndex += skipAmt;
		}
		
		l.shotsToAdd.addAll(bloodShots);
		
		if (dropItem && !doesResurrect() && !reviens && !items.isEmpty() && items.get(0).type.useful(l.player)) {
			l.goodies.add(new Goodie(this, items.get(0)));
			sound("drop", l);
		}
		if (stolenItem != null) {
			items.remove(stolenItem);
			l.goodies.add(new Goodie(this, stolenItem));
			stolenItem = null;
			sound("drop", l);
		}
		if (dropWeapon && !doesResurrect() && !reviens && l.player.isUseful(weapon)) {
			boolean drop = true;
			for (Goodie g : l.goodies) {
				if (g.weapon != null && g.weapon.equals(weapon)) {
					drop = false;
					break;
				}
			}
			if (drop) {
				l.goodies.add(new Goodie(this, weapon));
				sound("drop", l);
			}
		}
		if (stolenWeapon != null) {
			weapons.remove(stolenWeapon);
			weapon = weapons.get(0);
			l.goodies.add(new Goodie(this, stolenWeapon));
			stolenWeapon = null;
			sound("drop", l);
		}
				
		if (splitsIntoFour) {
			for (int i = 0; i < 4; i++) {
				Creature tiny = makeTinyVersion(l);
				tiny.x = x + w * (i % 2) * 0.5 + w * 0.25;
				tiny.y = y + h * (i / 2) * 0.5 + h * 0.25;
				tiny.dx = dx + l.r.nextDouble() * 4 - 2;
				tiny.dy = dy - l.r.nextDouble() * 4;
				tiny.heal();
				l.monstersToAdd.add(tiny);
				for (Shot s : l.shots) {
					if (s != null && s.weapon != null) {
						if (s.immune == null) { s.immune = new ArrayList<Creature>(); }
						s.immune.add(tiny);
					}
				}
			}
		}
		if (jar) {
			for (int i = 0; i < 6; i++) {
				Creature tiny = makeTinyVersion(l);
				tiny.x = x + l.r.nextDouble() * w * 0.4 + w * 0.05;
				tiny.y = y + l.r.nextDouble() * h * 0.3 + h * 0.05;
				tiny.dx = l.r.nextDouble() * 8 - 4;
				tiny.dy = l.r.nextDouble() * 8 - 6;
				tiny.heal();
				tiny.knockedBack = 120;
				tiny.weapon.reloadLeft = (int) (tiny.weapon.reload * l.r.nextDouble());
				l.monstersToAdd.add(tiny);
				for (Shot s : l.shots) {
					if (s != null && s.weapon != null) {
						if (s.immune == null) { s.immune = new ArrayList<Creature>(); }
						s.immune.add(tiny);
					}
				}
			}
		}
		
		return bloodShots;
	}
	
	public boolean isUseful(Weapon w) {
		if (weapons.contains(w)) { return false; }
		for (Weapon w2 : weapons) {
			if (w2.element == w.element && (w2.dps() > w.dps()) && w.homing == w2.homing) {
				return false;
			}
			if (w2.dps() > w.dps() * 3) {
				return false;
			}
		}
		return true;
	}
	
	public Creature makeTinyVersion(Level l) {
		Creature t = new Creature();
		t.img = img;
		t.flippedImg = flippedImg;
		t.imgIndex = imgIndex;
		t.tint = tint;
		t.canSeeStats = canSeeStats;
		t.charges = charges;
		t.dodges = dodges;
		t.eating = eating;
		t.explodes = explodes;
		t.flipped = flipped;
		t.maxHP = maxHP / 4;
		t.randomShootDelay = randomShootDelay;
		t.moveMode = moveMode;
		if (!items.isEmpty()) {
			t.items.add(items.get(0).makeTinyVersion());
		}
		t.reviens = reviens;
		t.hpRegen = hpRegen;
		t.resistance = resistance;
		t.w = w / 2;
		t.h = h / 2;
		t.speed = speed * 1.5;
		t.weapon = weapon.makeTinyVersion();
		t.weapons.add(t.weapon);
		t.animCycleLength = animCycleLength / 2 + 2;
		t.alwaysOnFire = alwaysOnFire;
		t.thief = thief;
		t.flyHeightBonus = l.r.nextInt(Level.GRID_SIZE);
		t.size = size / 2;
		t.normalW = normalW / 2;
		t.normalH = normalH / 2;
		return t;
	}
	
	public Creature makeGiantVersion(Level l) {
		Creature t = new Creature();
		t.img = img;
		t.flippedImg = flippedImg;
		t.imgIndex = imgIndex;
		t.tint = tint;
		t.canSeeStats = canSeeStats;
		t.charges = charges;
		t.dodges = dodges;
		t.eating = eating;
		t.explodes = explodes;
		t.flipped = flipped;
		t.maxHP = maxHP * 4;
		t.randomShootDelay = randomShootDelay;
		t.moveMode = moveMode;
		if (!items.isEmpty()) {
			t.items.add(items.get(0).makeGiantVersion());
		}
		t.reviens = reviens;
		t.hpRegen = hpRegen;
		t.resistance = resistance;
		t.w = w * 2;
		t.h = h * 2;
		t.speed = speed * 0.7;
		t.weapon = weapon.makeGiantVersion();
		t.weapons.add(t.weapon);
		t.animCycleLength = animCycleLength * 2;
		t.alwaysOnFire = alwaysOnFire;
		t.thief = thief;
		t.flyHeightBonus = l.r.nextInt(Level.GRID_SIZE);
		t.size = size * 2;
		t.normalW = normalW * 2;
		t.normalH = normalH * 2;
		return t;
	}
	
	public Creature makeTwin(Level l) {
		Creature t = new Creature();
		t.img = img;
		t.flippedImg = flippedImg;
		t.imgIndex = imgIndex;
		t.tint = tint;
		t.canSeeStats = canSeeStats;
		t.charges = charges;
		t.dodges = dodges;
		t.eating = eating;
		t.explodes = explodes;
		t.flipped = flipped;
		t.maxHP = maxHP;
		t.randomShootDelay = randomShootDelay;
		t.moveMode = moveMode;
		if (!items.isEmpty()) {
			t.items.add(items.get(0).makeTwin());
		}
		t.reviens = reviens;
		t.hpRegen = hpRegen;
		t.resistance = resistance;
		t.w = w;
		t.h = h;
		t.speed = speed;
		t.weapon = weapon.makeTwin();
		t.weapons.add(t.weapon);
		t.animCycleLength = animCycleLength;
		t.alwaysOnFire = alwaysOnFire;
		t.thief = thief;
		t.flyHeightBonus = l.r.nextInt(Level.GRID_SIZE);
		t.size = size;
		t.normalW = normalW;
		t.normalH = normalH;
		return t;
	}
	
	
	public void makePlayerAble() {
		playerControlled = true;
		hpRegen += 0.01;
		dropWeapon = false;
		dropItem = false;
	}
	
	@Override
	public void tick(Level l) {
		double originalDx = dx;
		double originalDy = dy;
		if (slipperiness > 0) {
			slipperiness--;
		}
		newThingTimer++;
		flamethrowerTicks++;
		if (hp <= 0) {
			if (!killMe) {
				explode(l);
			}
			killMe = true;
			return;
		}
		if (jar) {
			return;
		}
		if (canFly()) { ticksSinceGainingFlight++; }
		if (canHover()) { ticksSinceGainingHover++; }
		if (frozen == 0) {
			if (jumpElongate > 0) {
				jumpElongate = Math.max(0, jumpElongate - bottomInflateAmount);
			}
			if (eatSoundTimer > 0) { eatSoundTimer--; }
			if (fuse) {
				if (fuseTimer-- == 0) {
					sound("fuse", l);
					fuseTimer = 11;
				}
			} else {
				fuseTimer = 0;
			}
			int gLeft = (int) Math.floor((x) / Level.GRID_SIZE);
			int gRight = (int) Math.floor((x + w) / Level.GRID_SIZE);
			int gCenter = (int) Math.floor((x + w / 2) / Level.GRID_SIZE);
			int gy = (int) Math.floor((y + h) / Level.GRID_SIZE);
			if (gy > Level.LVL_H - 2) { gy = Level.LVL_H - 2; }
			if (gy < 0) { gy = 0; }
			if (gLeft < 0) { gLeft = 0; }
			if (gRight >= Level.LVL_W) { gRight = Level.LVL_W - 1; } 
			animCycle = (animCycle + 1) % animCycleLength;
			double relCycle = animCycle * 1.0 / animCycleLength;
			angle = 0;
			switch (realMoveMode()) {
				case SLIDE:
					gravityMult = 1;
					break;
				case CANTER:
					gravityMult = 1;
					if (PatentBlaster.lowGraphics) {
						angle = 0;
					} else {
						if (relCycle < 0.5) {
							angle = (-3 + 6 * 2 * relCycle) * Math.PI / 180;
						} else {
							angle = (3 - 6 * 2 * (relCycle - 0.5)) * Math.PI / 180;
						}
					}
					break;
				case FLY:
					gravityMult = 0;
					ticksSinceBottom = 0;
					break;
				case HOP:
					gravityMult = 1;
					if (animCycle == 0 && ticksSinceBottom < AIR_STEERING) {
						jump();
					}
					break;
				case HOVER:
					gravityMult = hoverPowerOff ? 1 : 0.3;
					if (l.tick % 5 == 0 && !hoverPowerOff) {
						if (l.tick % (5 * 5) == 0 && Math.abs(x + w / 2 - l.player.x - l.player.w / 2) < 700) {
							sound("hover", l);
						}
						l.shotsToAdd.add(new Shot(l, this));
					}
					break;
			}
			weapon.tick();
			for (Item it : items) { it.tick(l, this); }
			ticksSinceHit++;
			if (ticksSinceHit == PatentBlaster.FPS * 5 && this == l.player && hp <= totalMaxHP() * 0.7) {
				sound("regenerate", l);
			}
			accumulatedRegen += totalHPRegen();
			if (accumulatedRegen >= 1) {
				int reg = (int) Math.floor(accumulatedRegen);
				hp += reg;
				accumulatedRegen -= reg;
			}
			if (flees && hp > totalMaxHP() * 0.9) {
				fleeing = false;
			}
			
			if (!playerControlled && l.player.hp > 0) {
				targetX = l.player.x + l.player.w / 2;
				targetY = l.player.y + l.player.h / 2;
				if (voiceTimer > 0) { voiceTimer--; }
				double xpd = x + w / 2 - l.player.x - l.player.w / 2;
				if (xpd < 500) {
					ticksInView++;
				}
				if (encounterMessage != null && xpd < 400) {
					l.texts.add(new FloatingText(encounterMessage.toUpperCase(), x + w / 2, y));
					encounterMessage = null;
				}
				
				if (absorbTimer > 0) { absorbTimer--; }
				// Absorbing.
				if (absorber && Math.abs(xpd) < 400 && absorbTimer == 0) {
					for (Creature victim : l.monsters) {
						if (victim == this) { continue; }
						if (victim.frozen > 0) { continue; }
						if (victim.onFire > 0) { continue; }
						if (victim.hp <= 0) { continue; }
						if (victim.w >= w) { continue; }
						if (victim.reviens) { continue; }
						if (victim.doesResurrect()) { continue; }
						if (victim.finalForm != null) { continue; }
						if (victim.explodes) { continue; }
						if (victim.jar) { continue; }
						double dsq = (x + w / 2 - victim.x - victim.w / 2) * (x + w / 2 - victim.x - victim.w / 2) + (y + h / 2 - victim.y - victim.h / 2) * (y + h / 2 - victim.y - victim.h / 2);
						if (dsq < 250 * 250) {
							victim.dropItem = false;
							victim.dropWeapon = false;
							ArrayList<Shot> blood = victim.explode(l);
							victim.killMe = true;
							int i = 0;
							for (Shot s : blood) {
								s.eat(this, 1.0 / blood.size(), 1.0 / blood.size(), 0.4 / blood.size(), false);
								s.tint = victim.tint.mix(0.4, Clr.WHITE);
								s.lifeLeft = 40 + i++ * 2;
							}
							blood.get(0).sourceThingsTransfer = true;
							absorbTimer = PatentBlaster.FPS * 2;
							break;
						}
					}
				}
				
				if (fleeing) { // Run away!
					xpd *= -1;
					if (xpd == 0) { xpd = -1; } // Just go away.
				} else {
					if (evading > 0) {
						evading--;
						xpd = evadingLeft ? -1000 : 1000;
					}
				}
				double ypd = y + h / 2 - l.player.y - l.player.h / 2;
				double distSq = xpd * xpd + ypd * ypd;
				if (voice > -1 && voiceTimer == 0 && distSq < 800 * 800) {
					if (l.r.nextInt(PatentBlaster.FPS * 2) == 0) {
						sound("voice_" + voice, l);
						voiceTimer = PatentBlaster.FPS * 4;
					}
				}
				if (spotted && knockedBack == 0) {
					MoveMode rmm = realMoveMode();
					charging = Math.abs(xpd) < weapon.range() / 3;
					boolean far = Math.abs(distSq) > (w / 2 + l.player.w / 2 + 10) * (h / 2 + l.player.h / 2 + 10);
					double farBonus = rmm == MoveMode.FLY ? flyerHysteresis : 10;
					boolean xFar = Math.abs(xpd) > (w / 2 + l.player.w / 2 + farBonus);
					boolean yFar = Math.abs(ypd) > (h / 2 + l.player.h / 2 + farBonus);
					fuse = explodes && Math.abs(xpd) < (w / 2 + l.player.w / 2 + 160);
					hoverPowerOff = false;
					if (ticksSinceBottom < AIR_STEERING && dodges && l.player.weapon.reloadLeft != 0 && l.player.weapon.reloadLeft >= l.player.weapon.reload - PatentBlaster.FPS) {
						switch (rmm) {
							case CANTER:
							case HOP:
							case SLIDE:
								jump();
								break;
							case HOVER:
								jump();
							case FLY:
								//dy = -totalSpeed();
								if (evading == 0) {
									evading = 60;
									evadingLeft = l.r.nextBoolean();
								}
								//hoverPowerOff = true;
								break;
						}
					} else {
						if (ticksSinceBottom < AIR_STEERING) {
							if (((charges && explodes) || far || fleeing || thief)) {
								dx = 0;
								dy = 0;
								double ts = totalSpeed();
								if ((rmm == MoveMode.CANTER || rmm == MoveMode.SLIDE || rmm == MoveMode.HOP) &&
										((y + h - l.player.y - l.player.h) > 1 ||
										l.grid[gy][(xpd < 0 ? gRight + 1 : gLeft - 1)] >= Level.SOLID_START))
								{
									jump();
									if (xpd > 0) {
										dx = -ts;
									} else {
										dx = ts;
									}
								} else {
									if (xFar || fleeing) {
										if (xpd > 0) {
											dx = -ts;
										} else {
											dx = ts;
										}
										flyerHysteresis = 0;
									} else {
										flyerHysteresis = 100;
									}
									if (rmm == MoveMode.FLY) {
										if (Math.abs(ypd + (charges && !xFar ? 0 : ABOVE_PREF + flyHeightBonus)) > ts + h) {
											if (ypd + (charges && !xFar ? 0 : ABOVE_PREF + flyHeightBonus) > 0) {
												dy = -ts;
											} else {
												dy = ts;
											}
										}
									}
								}
							} else {
								// Match player xpos exactly.
								if (!fleeing && (rmm == MoveMode.FLY || rmm == MoveMode.HOVER) && l.player.totalSpeed() <= totalSpeed()) {
									dx = l.player.dx;
								}
							}
						}
						if (!far && explodes) {
							hp = 0;
						}
					}
					//if (dx != 0) { flipped = dx > 0; }
				}
				if (thief && Math.abs(x - l.player.x) < w / 2 + l.player.w / 2 && Math.abs(y - l.player.y) < h / 2 + l.player.h / 2 && stolenItem == null && stolenWeapon == null && (!l.player.items.isEmpty() || l.player.weapons.size() > 1))
				{
					boolean stealItem = !l.player.items.isEmpty() && (l.player.weapons.size() == 1 || l.r.nextBoolean());
					if (stealItem) {
						int index = l.r.nextInt(l.player.items.size());
						Item it = l.player.items.get(index);
						l.player.items.remove(index);
						items.add(it);
						stolenItem = it;
						sound("steal", l);
						l.texts.add(new FloatingText("STOLEN: " + it.desc(Clr.WHITE), x + w / 2, y));
					} else {
						int index = l.player.weapons.size() - 1;
						Weapon weap = l.player.weapons.get(index);
						while (weap == l.player.weapon) {
							index--;
							weap = l.player.weapons.get(index);
						}
						l.player.weapons.remove(index);
						weapons.add(weap);
						stolenWeapon = weap;
						if (stolenWeapon.dps() > weapon.dps()) {
							weapon = stolenWeapon;
						}
						sound("steal", l);
						l.texts.add(new FloatingText("STOLEN: " + weap.desc(Clr.WHITE), x + w / 2, y));
					}
				}
				if (Math.abs(xpd) < 512 + w) {
					spotted = true;
					if (weapon.reloadLeft == 0) {
						boolean doShoot = false;
						if (randomShootDelay) {
							shootDelay++;
							if (shootDelay > todaysShootDelay) {
								shootDelay = 0;
								todaysShootDelay = l.r.nextInt(weapon.reload / 2 + 1);
								doShoot = true;
							}
						} else {
							doShoot = true;
						}
						if (doShoot) {
							if (trackingAim) {
								Pt solution = l.player.targetIntersect(l, gunX(), gunY(), weapon.shotSpeed, weapon.shotLife);
								if (solution != null) {
									shoot(solution.x, solution.y, l);
								} else {
									weapon.reloadLeft = 10;
								}
							} else {
								if (xpd * xpd + ypd * ypd < weapon.range() * weapon.range() * 1.2) {
									shoot(l.player.x + l.player.w / 2, l.player.y + l.player.h / 2, l);
								}
							}
						}
					}
				}
			}
		} else {
			gravityMult = 1;
			frozen--;
			if (frozen == 0) {
				sound("ice_block_thaw", l);
			}
		}
		if (knockedBack > 0) { knockedBack--; }
		if (alwaysOnFire) {
			onFire = 5;
			fireWeapon = weapon;
			fireShooter = this;
		}
		if (onFire > 0) {
			if (!alwaysOnFire) { hp *= 0.995; }
			onFire--;
			lastShooter = fireShooter;
			if (fireSoundTimer++ % (PatentBlaster.FPS * 2) == 0 && Math.abs(x + w / 2 - l.player.x - l.player.w / 2) < 700) {
				sound("on_fire", l);
			}
			if (l.tick % PatentBlaster.shotDivider() == 0) {
				Shot fireShot = new Shot(l, fireWeapon, fireShooter, x + w / 2, -10000);
				fireShot.sprayProbability = alwaysOnFire ? 0.1 : 0.5;
				fireShot.immune = new ArrayList<Creature>();
				fireShot.immune.add(this);
				fireShot.dx = 0;
				fireShot.dy = 0;
				fireShot.w /= 2;
				fireShot.h /= 2;
				fireShot.dmgMultiplier = 0.01 * PatentBlaster.shotDivider();
				fireShot.gravityMult = -0.1;
				fireShot.x = x + l.r.nextDouble() * w - fireShot.w / 2;
				fireShot.y = y + h / 2 + l.r.nextDouble() * h / 2 - fireShot.h / 2;
				l.shotsToAdd.add(fireShot);
			}
		} else {
			fireSoundTimer = 0;
		}
		if (spotted && reproduces) {
			for (Iterator<Creature> it = babies.iterator(); it.hasNext();) {
				if (it.next().hp <= 0) { it.remove(); }
			}
			if (babies.size() < 4) {
				reproCooldown--;
				if (reproCooldown == 0) {
					reproCooldown = PatentBlaster.FPS * (babies.size() * 4 + 3);
					sound("squelch", l);
					for (int i = 0; i < 60; i++) {
						Shot s = new Shot(Clr.WHITE, w / 20 + 1, w / 20 + 1, false, 50 + l.r.nextInt(200), x + w / 2, y + h / 2, l.r.nextDouble() * 8 - 4, l.r.nextDouble() * 8 - 4, 1.0, null, false, false, false, 0, 0, 0);
						l.shotsToAdd.add(s);
					}
					Creature baby = makeTinyVersion(l);
					baby.heal();
					baby.x = x + w / 2 - baby.w / 2;
					baby.y = y + h / 2 - baby.h / 2;
					baby.knockedBack = PatentBlaster.FPS * 2;
					baby.dx = l.r.nextDouble() * 8 - 4;
					baby.dy = -l.r.nextDouble() * 3 - 1;
					l.monstersToAdd.add(baby);
					for (Shot s : l.shots) {
						if (s != null && s.weapon != null) {
							if (s.immune == null) { s.immune = new ArrayList<Creature>(); }
							s.immune.add(baby);
						}
					}
					babies.add(baby);
				}
			}
		}
		if (hp > totalMaxHP()) { hp = totalMaxHP(); }
		heat *= 0.986;
		
		ticksSinceDirChange++;
		if (slipperiness > 0 && !playerControlled && Math.abs(originalDx) > 0.5) {
			dx = originalDx;
		} else if (realMoveMode() == MoveMode.FLY && !playerControlled) {
			dx = 0.96 * originalDx + 0.04 * dx;
			dy = 0.96 * originalDy + 0.04 * dy;
			flipped = dx > 0;
		} else {
			int currentXDir = dx < 0 ? -1 : dx > 0 ? 1 : 0;
			if (currentXDir != xDir && !playerControlled && ticksSinceDirChange < MIN_DIR_CHANGE_WAIT) {
				dx = originalDx;
			}
			if (ticksSinceDirChange >= MIN_DIR_CHANGE_WAIT && currentXDir != 0) {
				xDir = currentXDir;
				flipped = xDir >= 0;
				ticksSinceDirChange = 0;
			}
		}
		
		if (frozen == 0) {
			for (Iterator<Shot> it = stuckShots.iterator(); it.hasNext();) {
				Shot s = it.next();
				s.lifeLeft--;
				if (s.lifeLeft <= 0) {
					it.remove();
				}
			}
			
			// Finally, adjust squish.
			if (leftPress > 0) {
				double newW = normalW * (1.0 - squish() * leftPress / maxPress);
				x -= newW - w - 0.0001;
				dx += newW - w;
				w = newW;
			} else if (rightPress > 0) {
				double newW = normalW * (1.0 - squish() * rightPress / maxPress);
				dx += w - newW;
				w = newW;
			} else {
				w = normalW;
			}
			double newH = normalH * (1.0 - squish() * (bottomPress - jumpElongate) / maxPress);
			y += h - newH;
			h = newH;
		}
	}
	
	public Shot shoot(double tx, double ty, Level l) {
		if (weapon.reloadLeft == 0 && (!weapon.sword || lastShot == null || lastShot.age > 3) && !weapon.flamethrower) {
			l.soundRequests.add(new SoundRequest(weapon.element.shotSound, x + w / 2, y + h / 2, 1.0));
		}
		if (weapon.reloadLeft == 0 && weapon.flamethrower && flamethrowerTicks >= 60) {
			l.soundRequests.add(new SoundRequest("flamethrower", x + w / 2, y + h / 2, 1.0));
			flamethrowerTicks = 0;
		}
		weapon.reloadLeft = weapon.reload;
		Shot s = null;
		for (int i = 0; i < weapon.numBullets; i++) {
			s = new Shot(l, weapon, this, tx, ty);
			if (weapon.swarm) {
				s.dx = (l.r.nextDouble() * 2 - 1) * weapon.shotSpeed;
				s.dy = (l.r.nextDouble() * 2 - 1) * weapon.shotSpeed;
			}
			if (weapon.shotgun || weapon.flamethrower) {
				double sMult = l.r.nextDouble() * 0.5 + 0.75;
				s.dx *= sMult;
				s.dy *= sMult;
			}
			if (weapon.scattershot) {
				double sMult = l.r.nextDouble() * 0.2 + 0.9;
				s.dx *= sMult;
				s.dy *= sMult;
			}
			if (weapon.sword) {
				s.swordpos();
			}
			l.shotsToAdd.add(s);
		}
		lastShot = s;
		return s;
	}
	
	void sound(String s, Level l) {
		l.soundRequests.add(new SoundRequest(s, x + w / 2, y + h / 2, w / 120));
	}
	
	public int takeDamage(Level l, Shot shot) {
		if (hp <= 0) { return 0; }
		Weapon src = shot.weapon;
		double dmg = src.dmg * shot.dmgMultiplier;
		if (jar) {
			dmg *= 10;
		}
		dmg *= 1.0 - resistance(src.element);
		for (Item it : items) {
			if (it.shield && it.shieldReload == 0) {
				it.shieldReload = it.shieldReloadTime;
				sound("shield_hit", l);
				return 0;
			}
		}
		
		int intDmg = 0;
		if (dmg < 1) {
			if (src.dmg > 0 && l.r.nextDouble() <= dmg) {
				intDmg = 1;
			}
		} else {
			intDmg = (int) dmg;
		}
		
		ticksSinceHit = 0;
		if (dmg <= 0) {
			return 0;
		}
		lastShooter = shot.shooter;

		if (dmg > 0) {
			ArrayList<Shot> bloodShots = new ArrayList<Shot>();
			if (hp - dmg > 0) {
				Clr blood = bloodClr();
				if (resistance != null) { blood = resistance.tint; }
				int nBlood = 20 * intDmg / totalMaxHP() / PatentBlaster.shotDivider() + 1;
				for (int i = 0; i < nBlood; i++) {
					bloodShots.add(new Shot(blood, w / 10, w / 10, false, 120 + l.r.nextInt(80), shot.x + shot.w / 2 - w / 20, shot.y + shot.h / 2 - h / 20, l.r.nextDouble() * 4 - 2, l.r.nextDouble() * 4 - 2, 1.0, this, false, false, false, 0, 0, frozen > 0 ? l.r.nextInt(40) + 10 : 0));
				}
				l.shotsToAdd.addAll(bloodShots);
				if (src.knockback && !massive) {
					dx += shot.dx * 0.5;
					dy += shot.dy * 0.5;
					knockedBack = 20;
				}
				double spltVolume = Math.min(1.3, 1.0 * dmg / totalMaxHP());
				if (spltVolume > 0.1) {
					l.soundRequests.add(new SoundRequest(jar ? "jarhit" : "splt", shot.x, shot.y, spltVolume));
				}
			} else {
				if (src.swarm && src.element == Element.FIRE && !l.bees) {
					l.bees = true;
					l.texts.add(new FloatingText("NOT THE BEES, THEY'RE IN MY EYES!", x + w / 2, y));
				}
				if (!reviens && !doesResurrect() && finalForm == null) {
					bloodShots = explode(l);
				} else {
					explode(l); // Shots not edible.
				}
				killMe = true;
			}
			double tv = shot.shooter.totalVamp();
			if (!bloodShots.isEmpty() && dmg * tv >= 1 & shot.shooter.hp > 0 && shot.shooter.hp < shot.shooter.totalMaxHP()) {
				for (Shot s : bloodShots) {
					s.eat(shot.shooter, 0, 0, 0, false);
				}
				bloodShots.get(0).eatHPGain = (int) (dmg * tv);
			}
		}

		hp -= intDmg;
		if (flees && hp < totalMaxHP() / 2) {
			fleeing = true;
		}
		if (src.element == Element.FIRE && !fireproof()) {
			heat += intDmg;
		}
		if (src.element == Element.ICE && !iceproof()) {
			heat -= intDmg;
		}
		if (heat > totalMaxHP() / 4 && hp > 0 && !fireproof()) {
			onFire = 60;
			fireWeapon = shot.weapon;
			fireShooter = shot.shooter;
		} else if (heat < -totalMaxHP() / 4 && hp > 0 && !iceproof()) {
			if (frozen == 0) {
				sound("ice_block", l);
				if (playerControlled) {
					l.texts.add(new FloatingText("FROZEN!", x + w / 2, y));
				}
			}
			frozen = 200;
			dx = 0;
			dy = 0;
		}
		return intDmg;
	}
	
	public double totalEating() {
		double e = eating;
		for (Item it : items) {
			e += it.eating;
		}
		return Math.min(1, e);
	}
	
	public double totalVamp() {
		double e = 0;
		for (Item it : items) {
			e += it.vampireMult;
		}
		return Math.min(1, e);
	}
	
	public boolean[][] grid() {
		return Grids.get(PatentBlaster.IMG_NAMES[imgIndex], flipped);
	}
	
	public static Creature make(long seed, int power, int numImages, boolean boss, boolean player, boolean allowFinalForm) {
		Random r = new Random(seed);
		Creature c = new Creature();
		c.seed = seed;
		Weapon w = Weapon.make(seed, power, !player);
		c.weapon = w;
		c.weapons.add(w);
		Element el = Element.values()[r.nextInt(Element.values().length)];
		
		Item it = Item.make(seed, power, c, null);
		c.items.add(it);
		c.canSeeStats = it.givesInfo;
		
		double hp = BASE_HP;
		if (r.nextInt(4) == 0) {
			c.resistance = el;
			hp *= (el == Element.FIRE || el == Element.ICE ? 0.8 : 0.9);
		}
		int sz = (boss ? 80 : 40) + r.nextInt(3) * (boss ? 20: 10);
		
		hp *= (1.0 * sz / (boss ? 100 : 50));
		c.speed = Math.min(w.sword ? 100 : w.shotSpeed - 2, player ? (2.5 + r.nextDouble() * 2) : (1 + r.nextDouble() * 4));
		hp /= (c.speed / 2.5);
		if (boss) {
			c.voice = r.nextInt(PatentBlaster.NUM_VOICES);
		}
		c.moveMode = player || r.nextInt(3) == 0 ? MoveMode.SLIDE : MoveMode.values()[r.nextInt(MoveMode.values().length)];
		if (c.weapon.sword) {
			c.moveMode = MoveMode.SLIDE;
		}
		if (c.moveMode == MoveMode.FLY) {
			hp *= 0.8;
			c.speed *= 1.2;
		} else {
			if (!player && !boss && !w.homing && !w.shotgun && !w.flamethrower && !w.sword && r.nextInt(4 / power + 4) == 0) {
				c.explodes = true;
				hp *= 0.8;
			}
		}
		if (r.nextInt(10 / power + 2) == 0) {
			c.hpRegen = BASE_REGEN * powerLvl(power);
			hp *= 0.85;
		}
		if (!player &&r.nextInt(10 / power + 2) == 0) {
			c.dodges = true;
			hp *= 0.9;
		}
		if (!player &&!c.dodges && c.moveMode != MoveMode.HOP && r.nextInt(6) == 0) {
			c.charges = true;
			hp *= 0.85;
		}
		if (boss || (sz > 60 && r.nextInt(3) == 0)) {
			c.massive = true;
		}
		if (!player && !PatentBlaster.DEMO && r.nextInt(10 / power + 1) == 0) {
			c.trackingAim = true;
			hp *= 0.8;
		}
		if (!PatentBlaster.DEMO && r.nextInt(8) == 0) {
			c.eating = Math.min(1.0 / 50, power * 0.001);
			hp *= 0.9;
		}
		if (!player && !PatentBlaster.DEMO && r.nextInt(10 / power + 2) == 0) {
			c.randomShootDelay = true;
			hp *= 0.9;
		}
		if (!player && !PatentBlaster.DEMO && power > 4 && sz > 60 && r.nextInt(30 / power + (boss ? 6 : 10)) == 0) {
			c.splitsIntoFour = true;
			hp *= 0.9;
		}
		if (!player && !PatentBlaster.DEMO && power > 4 && sz > 60 && r.nextInt(25 / power + (boss ? 4 : 10)) == 0) {
			c.reproduces = true;
			hp *= 0.8;
		}
		if (!player && power > 2 && !c.splitsIntoFour && r.nextInt((boss ? 10 : 30) / power + (boss ? 4 : 15)) == 0) {
			c.resurrects = true;
			hp *= 0.9;
		}
		if (!player && !PatentBlaster.DEMO && allowFinalForm && power > 3 && !c.splitsIntoFour && r.nextInt((boss ? 10 : 30) / power + (boss ? 3 : 12)) == 0) {
			c.finalForm = make(seed + 1349, power * 5 / 4 + 2, numImages, boss, player, false);
			hp *= 0.8;
		}
		if (!player && power > 2 && c.finalForm == null && !c.splitsIntoFour && !c.resurrects && !it.resurrect && r.nextInt(30 / power + 12) == 0) {
			c.reviens = true;
			hp *= 0.9;
		}
		if (!player && w.element == Element.FIRE && r.nextInt(8) == 0) {
			c.alwaysOnFire = true;
			hp *= 0.9;
		}
		if (!player && !boss && c.hpRegen > 0 && r.nextInt(8 / power + 2) == 0) {
			c.flees = true;
			hp *= 0.85;
		}
		if (!player && !boss && !c.reviens && !c.items.get(0).shield && power > 1 && c.finalForm == null && !c.doesResurrect() && r.nextInt(8) == 0) {
			c.jar = true;
			sz *= 2;
		}
		if (!player && !PatentBlaster.DEMO && !c.explodes && !c.jar && r.nextInt(20 / power + 10) == 0) {
			c.thief = true;
			hp *= 0.8;
		}
		if (!player && !PatentBlaster.DEMO && !c.jar && r.nextInt((boss ? 10 : 100) / power + (boss ? 3 : 10)) == 0) {
			c.absorber = true;
			c.hp *= 0.85;
		}
		if (boss) {
			c.hp *= 1.5;
		}
		
		int red = 0, green = 0, blue = 0;
		while (red + green + blue * 0.8 < 300) {
			red = r.nextInt(255);
			green = r.nextInt(255);
			blue = r.nextInt(255);
		}
		c.imgIndex = r.nextInt(numImages);
		c.img = PatentBlaster.CREATURE_IMGS.get(PatentBlaster.IMG_NAMES[c.imgIndex]);
		c.flippedImg = c.img.flip();
		c.tint = c.resistance != null ? c.resistance.tint : new Clr(red, green, blue);
		c.animCycleLength = 10 + r.nextInt(100);
		c.w = sz * PatentBlaster.IMG_W[c.imgIndex];
		c.h = sz * PatentBlaster.IMG_H[c.imgIndex];
		c.normalW = c.w;
		c.normalH = c.h;
		c.size = sz;
		if (c.finalForm != null) {
			if (c.finalForm.w > c.w) {
				double scaling = c.w / c.finalForm.w - 0.001;
				c.finalForm.w *= scaling;
				c.finalForm.h *= scaling;
				c.finalForm.size *= scaling;
				c.finalForm.normalW *= scaling;
				c.finalForm.normalH *= scaling;
			}
			if (c.finalForm.h > c.h) {
				double scaling = c.h / c.finalForm.h - 0.001;
				c.finalForm.w *= scaling;
				c.finalForm.h *= scaling;
				c.finalForm.size *= scaling;
				c.finalForm.normalW *= scaling;
				c.finalForm.normalH *= scaling;
			}
		}
		
		switch (r.nextInt(4)) {
			case 0: // Meat shield.
				w.dmg *= 0.8;
				hp *= 1.5;
				break;
			case 1: // Glass cannon.
				w.dmg *= 1.3;
				hp *= 0.8;
				break;
		}
				
		c.maxHP = (int) Math.ceil(hp * powerLvl(power));
		
		c.maxHP += it.creatureHPBonus;
		
		Random r2 = new Random();
		
		c.flyHeightBonus = r.nextInt(Level.GRID_SIZE);
		c.animCycle = r2.nextInt(c.animCycleLength);
		c.dropItem = r2.nextInt(6) == 0;
		c.dropWeapon = power > 2 && r2.nextInt(16) == 0;
		
		if (player) {
			c.makePlayerAble();
		}
		
		c.heal();
		return c;
	}
	
	public double resistance(Element e) {
		double dmg = 1;
		if (resistance == e) {
			dmg /= 2;
		}
		for (Item it : items) {
			if (it.resistanceVs == e) {
				dmg *= (1 - it.resistance);
			}
		}
		return Math.min(0.9, 1 - dmg);
	}
	
	public String name() {
		if (tint == null) { return "A Barrel"; }
		String n = PatentBlaster.PRETTY_IMG_NAMES[imgIndex];
		if (resistance != null) {
			n = resistance.name() + "-" + n;
		}
		if (thief) {
			n = "Thief-" + n;
		}
		if (isZombie) {
			n = "Zombie-" + n;
		}
		if (resistance == null) {
			n = Colors.getName(tint) + " " + n;
		}
		if (alwaysOnFire) {
			n = "Burning " + n;
		}
		if (explodes) {
			n = "Exploding " + n;
		}
		if (reproduces) {
			n = "Reproducing " + n;
		}
		if (absorber) {
			n = "Absorbing " + n;
		}
		switch (realMoveMode()) {
			case CANTER:
				n = "Cantering " + n;
				break;
			case HOP:
				n = "Hopping " + n;
				break;
			case HOVER:
				n = "Hovering " + n;
				break;
			case FLY:
				n = "Flying " + n;
				break;
		}
		if (size < 50) {
			n = "Tiny " + n;
		} else if (size < 60) {
			n = "Small " + n;
		} else if (size > 70) {
			n = "Big " + n;
		} else if (size > 100) {
			n = "Huge " + n;
		} else if (size > 150) {
			n = "Gigantic " + n;
		}
		if (n.equals(PatentBlaster.PRETTY_IMG_NAMES[imgIndex])) {
			n = "Perfectly Normal " + n;
		}
		return "Pat " + (Math.abs(seed % 10000) + 233) + ", " + n;
	}
	
	@Override
	public String desc(Clr textTint) {
		return desc(textTint, 0);
	}
	
	public String desc(Clr textTint, int numNums) {
		StringBuilder sb = new StringBuilder();
		if (resistance != null) {
			sb.append("[").append(resistance.tint.mix(0.4, textTint)).append("]").append(resistance.name()).append("[]");
		} else {
			sb.append("Normal");
		}
		sb.append(" Creature\n");
		sb.append("HP: ").append(hp).append("/").append(totalMaxHP());
		if (baseHPRegen() != 0) {
			sb.append(" + ").append(round(baseHPRegen() * PatentBlaster.FPS)).append("/sec\n");
		} else {
			sb.append("\n");
		}
		if (totalEating() > 0) {
			sb.append("Eating flesh gives you ").append(round(totalEating() * 100 * 50)).append("% of the victim's HP\n");
		}
		if (totalVamp() > 0) {
			sb.append(round(totalVamp() * 100)).append("% of damage gained as HP\n");
		}
		sb.append("Speed: ").append(round(totalSpeed() * PatentBlaster.FPS)).append(" px/sec\n");
		//sb.append("Move mode: ").append(realMoveMode().name()).append("\n");
		for (Element e : Element.values()) {
			double r = resistance(e);
			if (r > 0) {
				sb.append("[").append(e.tint.mix(0.4, textTint)).append("]").append(e.name()).append("[] Damage Resistance: ").append(round(r * 100)).append("%\n");
			}
		}
		if (fireproof()) { sb.append("Can't catch fire.\n"); }
		if (iceproof()) { sb.append("Can't be frozen.\n"); }
		if (charges) { sb.append("Charges.\n"); }
		if (explodes) { sb.append("Explodes.\n"); }
		if (dodges) { sb.append("Dodges.\n"); }
		if (flees) { sb.append("Flees when low on health.\n"); }
		if (massive) { sb.append("Massive.\n"); }
		if (trackingAim) { sb.append("Tracking Aim.\n"); }
		if (trackingAim) { sb.append("Thief.\n"); }
		if (randomShootDelay) { sb.append("Random Shoot Delay.\n"); }
		if (absorber) { sb.append("Absorbs other creatures.\n"); }
		if (reviens) { sb.append("Reviens.\n"); }
		if (resurrects) { sb.append("Resurrects.\n"); }
		if (finalForm != null) { sb.append("Resurrects into a new form.\n"); }
		if (splitsIntoFour) { sb.append("Splits into four smaller creatures.\n"); }
		if (jar) { sb.append("Creature jar.\n"); }
		sb.append(weapon.desc(textTint));
		for (Item it : items) {
			sb.append(it.desc(textTint));
		}
		if (numNums == 0) {
			return sb.toString();
		} else {
			String[] lines = sb.toString().split("\n");
			ArrayList<Integer> indices = new ArrayList<Integer>();
			for (int i = 0; i < lines.length - 1; i++) {
				indices.add(i);
			}
			Random r = new Random(seed);
			Collections.shuffle(indices, r);
			Collections.sort(indices.subList(0, numNums));
			int i = 0;
			for (; i < numNums && i < indices.size(); i++) {
				lines[indices.get(i)] = /*(i + 1) + " " + */lines[indices.get(i)] + " [555555](" + (i + 1) + ")[]";
			}
			/*for (; i < indices.size(); i++) {
				lines[indices.get(i)] = "  " + lines[indices.get(i)];
			}*/
			sb = new StringBuilder();
			for (String s : lines) {
				sb.append(s).append("\n");
			}
			return sb.toString();
		}
	}

	boolean canFly() {
		for (Item it : items) {
			if (it.fly) { return true; }
		}
		return moveMode == MoveMode.FLY;
	}
	
	boolean canHover() {
		for (Item it : items) {
			if (it.hover) { return true; }
		}
		return moveMode == MoveMode.HOVER;
	}

	void heal() {
		killMe = false;
		weapon.reloadLeft = 0;
		hp = totalMaxHP();
		heat = 0;
		onFire = 0;
		frozen = 0;
		knockedBack = 0;
		accumulatedRegen = 0;
		stuckShots.clear();
		for (Item it : items) {
			it.shieldReload = 0;
		}
	}

	void deResurrect(Level l) {
		for (Iterator<Item> itr = items.iterator(); itr.hasNext();) {
			Item it = itr.next();
			if (it.resurrect) {
				itr.remove();
				if (this != l.player && this.dropItem) {
					l.goodies.add(new Goodie(this, it));
					sound("drop", l);
				}
				return;
			}
		}
		resurrects = false;
	}

	public double getStickiness() {
		double stick = 0;
		for (Shot s : stuckShots) {
			stick += s.stickiness;
		}
		return stick;
	}

	void invert() {
		if (resistance != null) {
			switch (resistance) {
				case ACID:
					resistance = Element.STEEL;
					break;
				case STEEL:
					resistance = Element.ACID;
					break;
				case FIRE:
					resistance = Element.ICE;
					break;
				case ICE:
					resistance = Element.FIRE;
					break;
			}
			tint = resistance.tint;
		} else {
			tint = new Clr(255 - tint.r, 255 - tint.g, 255 - tint.b);
		}
		
		switch (weapon.element) {
			case ACID:
				weapon.element = Element.STEEL;
				break;
			case STEEL:
				weapon.element = Element.ACID;
				break;
			case FIRE:
				weapon.element = Element.ICE;
				break;
			case ICE:
				weapon.element = Element.FIRE;
				break;
		}
		weapon.tint = weapon.element.tint;
	}
}
