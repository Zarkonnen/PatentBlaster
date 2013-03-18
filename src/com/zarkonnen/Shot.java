package com.zarkonnen;

import com.zarkonnen.catengine.Draw;
import com.zarkonnen.catengine.util.Clr;
import com.zarkonnen.catengine.util.Pt;
import com.zarkonnen.catengine.util.Rect;
import java.util.ArrayList;

public class Shot extends Entity {
	public static final Clr ACID_ALT = new Clr(230, 210, 40);
	public static final Clr HOVER = new Clr(100, 100, 255);
	public static final Clr ZOMBIE = new Clr(100, 130, 90);
	public static final Clr STICKY_TINT = Element.ACID.tint.mix(0.5, Barrel.GLUE_TINT);
	
	public Weapon weapon;
	public Creature shooter;
	public double sprayProbability;
	public double dmgMultiplier = 1;
	public ArrayList<Creature> immune = null;
	public int lifeLeft = 0;
	public Creature hoverer;
	public Creature bleeder;
	public boolean reform;
	public boolean revenant;
	public boolean finalForm;
	public double originalX, originalY, returnFromX, returnFromY;
	public Clr originalTint;
	public Creature target;
	public int frozenAmt = 0;
	public Clr thawedTint;
	public Creature beingEatenBy;
	public double sourceHPTransfer;
	public double sourceMaxHPTransfer;
	public double sourceColorTransfer;
	public boolean sourceThingsTransfer;
	public int eatHPGain;
	public int knownKills;
	public int age = 0;
	public double friction = 0.99;
	public double stickiness = 0;
	public int slipperiness = 0;
	public boolean freeAgent = false;
	public boolean flammable = false;
	public Weapon flammableWeapon = null;
	public boolean remains;
	
	public Shot(Level l, Creature hoverer) {
		this.hoverer = hoverer;
		this.x = hoverer.x + hoverer.w / 4;
		this.y = hoverer.y + hoverer.h;
		this.dx = 0;
		this.dy = 5;
		this.w = hoverer.w / 2;
		this.h = 2;
		this.tint = HOVER;
		this.popOnWorldHit = true;
		this.lifeLeft = (int) (Level.GRID_SIZE * (1.2 + l.r.nextDouble() * 0.5) / this.dy);
	}

	public Shot(Level l, Weapon w, Creature shooter, double tx, double ty) {
		if (w.homing) {
			if (shooter == l.player) {
				for (Creature c : l.monsters) {
					if (new Rect(c.x, c.y, c.w, c.h).contains(new Pt(tx, ty))) {
						target = c;
					}
				}
			} else {
				target = l.player;
			}
		}
		if (w.penetrates()) {
			immune = new ArrayList<Creature>();
		}
		this.weapon = w;
		this.shooter = shooter;
		this.tint = w.element.tint;
		this.w = w.shotSize;
		this.h = w.shotSize;
		this.x = shooter.x - w.shotSize / 2 + (shooter.flipped ? (1 - PatentBlaster.IMG_SHOOT_X[shooter.imgIndex]) : PatentBlaster.IMG_SHOOT_X[shooter.imgIndex]) * shooter.w;
		this.y = shooter.y - w.shotSize / 2 + PatentBlaster.IMG_SHOOT_Y[shooter.imgIndex] * shooter.h;
		popOnWorldHit = true;
		gravityMult = 0;
		double dtx = tx - x, dty = ty - y;
		double angle = Math.atan2(dty, dtx);
		angle += (l.r.nextDouble() * 2 - 1) * (w.reduceInaccuracy ? w.jitter / 3 : w.jitter);
		dx = w.shotSpeed * Math.cos(angle);
		dy = w.shotSpeed * Math.sin(angle);
		lifeLeft = w.shotLife;
		switch (w.element) {
			case ACID:
				sprayProbability = 0.05;
				break;
			case FIRE:
				sprayProbability = 1;
				break;
			case ICE:
				sprayProbability = 1;
				break;
			case STEEL:
				sprayProbability = 0;
				break;
		}
		if (w.shotgun || w.flamethrower) {
			sprayProbability /= 8;
		}
		if (w.grenade) {
			sprayProbability /= 4;
		}
		if (w.sword) {
			sprayProbability /= 20;
			ignoresWalls = true;
		}
		if (w.scattershot) {
			sprayProbability /= 3;
		}
		if (w.sticky) {
			stickiness = w.dmg * 2;
			this.tint = STICKY_TINT;
		}
	}
	
	public Shot(Level l, Shot p) {
		if (p.weapon.penetrates()) {
			immune = new ArrayList<Creature>();
		}
		freeAgent = p.freeAgent;
		weapon = p.weapon;
		shooter = p.shooter;
		tint = p.tint;
		w = p.w / 2;
		h = p.h / 2;
		if (weapon.element == Element.ACID) {
			w = Math.max(w, 3);
			h = Math.max(h, 3);
		}
		x = p.x + p.w / 2 - w / 2;
		y = p.y + p.h / 2 - h / 2;
		dx = p.weapon.shotSpeed * (l.r.nextDouble() * 0.1 - 0.05);
		dy = p.weapon.shotSpeed * (l.r.nextDouble() * 0.1 - 0.05);
		lifeLeft = p.weapon.shotLife / PatentBlaster.shotDivider();
		switch (weapon.element) {
			case ACID:
				gravityMult = 1;
				popOnWorldHit = false;
				dmgMultiplier = 0.25 * PatentBlaster.shotDivider();
				lifeLeft *= 1.8;
				break;
			case FIRE:
				gravityMult = -0.1;
				popOnWorldHit = true;
				dmgMultiplier = 0.015 * PatentBlaster.shotDivider();
				break;
			case ICE:
				gravityMult = 0.12;
				popOnWorldHit = true;
				dmgMultiplier = 0.02 * PatentBlaster.shotDivider();
				break;
			case STEEL:
				gravityMult = 0;
				popOnWorldHit = true;
				dmgMultiplier = 0.2 * PatentBlaster.shotDivider();
				break;
		}
		
		stickiness = p.stickiness * dmgMultiplier;
	}
	
	public boolean doNotEndLevel() {
		return reform || finalForm || revenant;
	}
	
	public void eat(Creature eater, double sourceHPTransfer, double sourceMaxHPTransfer, double sourceColorTransfer, boolean sourceThingsTransfer) {
		this.beingEatenBy = eater;
		this.sourceHPTransfer = sourceHPTransfer;
		this.sourceMaxHPTransfer = sourceMaxHPTransfer;
		this.sourceColorTransfer = sourceColorTransfer;
		this.sourceThingsTransfer = sourceThingsTransfer;
		this.lifeLeft = 20;
		this.returnFromX = x;
		this.returnFromY = y;
		this.tint = new Clr(this.tint.r, this.tint.g, this.tint.b, 50);
	}
	
	public void swordpos() {
		double dtx = shooter.targetX - shooter.gunX();
		double dty = shooter.targetY - shooter.gunY();
		double angle = Math.atan2(dty, dtx);
		x = shooter.gunX() + Math.cos(angle) * (age) * weapon.shotSpeed - w / 2;
		y = shooter.gunY() + Math.sin(angle) * (age) * weapon.shotSpeed - h / 2;
	}
	
	@Override
	public void tick(Level l) {
		age++;
		if (ticksSinceBottom < 2) {
			dx *= friction;
			dy *= friction;
		}
		if (beingEatenBy != null && beingEatenBy.hp <= 0) {
			beingEatenBy = null;
		}
		if (frozenAmt > 0) {
			frozenAmt--;
			if (frozenAmt == 0 && thawedTint != null) {
				tint = thawedTint;
			}
		}
		if (weapon != null && shooter != null && weapon.sword && dmgMultiplier == 1) {
			if (shooter.hp > 0) {
				swordpos();
				if (shooter.lastShot != null && shooter.lastShot.age > 1) {
					killMe = true;
				}
				if (shooter.weapon != weapon) {
					killMe = true;
				}
			} else {
				killMe = true;
			}
		}
		
		if ((reform || finalForm || revenant) && lifeLeft <= 60 && bleeder != null) {
			if (lifeLeft == 60) {
				returnFromX = x;
				returnFromY = y;
				originalTint = tint;
				if (!bleeder.unsplorted) {
					l.soundRequests.add(new SoundRequest("desquelch", bleeder.x + bleeder.w / 2, bleeder.y + bleeder.h / 2, bleeder.w / 40));
					bleeder.unsplorted = true;
					if (revenant) {
						// Make bleeder into zombie.
						bleeder.tint = ZOMBIE;
						bleeder.maxHP *= 2.4;
						bleeder.weapon.element = Element.ACID;
						bleeder.weapon.shotSpeed /= 2;
						bleeder.weapon.shotLife *= 1.2;
						bleeder.weapon.jitter += 0.15;
						bleeder.weapon.dmg *= 1.5;
						bleeder.speed /= 3;
						bleeder.reviens = false;
						bleeder.isZombie = true;
						bleeder.resistance = null;
					}
				}
			}
			gravityMult = 0;
			collides = false;
			dx = 0;
			dy = 0;
			if (lifeLeft < 30) {
				double proportion = (30.0 - lifeLeft) / 30;
				Creature becomes = (reform || revenant) ? bleeder : bleeder.finalForm;
				tint = originalTint.mix(proportion, becomes.tint);
				x = originalX;
				y = originalY;
			} else {
				x = returnFromX + (originalX - returnFromX) * (60 - lifeLeft) / 30;
				y = returnFromY + (originalY - returnFromY) * (60 - lifeLeft) / 30;
			}
			
			if (lifeLeft-- <= 0) {
				if (bleeder.hp <= 0) {
					// We get to resurrect our god!
					bleeder.heal();
					bleeder.unsplorted = false;
					l.soundRequests.add(new SoundRequest(reform ? "resurrect" : revenant ? "revenant" : "final_form", bleeder.x + bleeder.w / 2, bleeder.y + bleeder.h / 2, bleeder.w / 40));
					Creature reborn = null;
					if (revenant) {
						reborn = bleeder;
						if (bleeder != l.player) {
							l.monsters.add(bleeder);
						}
						l.texts.add(new FloatingText("REVENANT", bleeder.x + bleeder.w / 2, bleeder.y));
					} else if (reform) {
						bleeder.deResurrect(l);
						reborn = bleeder;
						if (bleeder != l.player) {
							l.monsters.add(bleeder);
						}
						l.texts.add(new FloatingText("RESURRECT", bleeder.x + bleeder.w / 2, bleeder.y));
					} else {
						reborn = bleeder.finalForm;
						bleeder.finalForm.heal();
						bleeder.finalForm.x = bleeder.x;
						bleeder.finalForm.y = bleeder.y;
						bleeder.finalForm.dx = bleeder.dx;
						bleeder.finalForm.dy = bleeder.dy;
						l.monsters.add(bleeder.finalForm);
						l.texts.add(new FloatingText("FINAL FORM", bleeder.x + bleeder.w / 2, bleeder.y));
					}
					reborn.weapon.reloadLeft = reborn.weapon.reload;
					for (Shot s : l.shots) {
						if (s != null && s.weapon != null) {
							if (s.immune == null) { s.immune = new ArrayList<Creature>(); }
							s.immune.add(reborn);
						}
					}
				}
				killMe = true;
			}
			return;
		}
		if (beingEatenBy != null) {
			collides = false;
			gravityMult = 0;
			dx = 0;
			dy = 0;
			if (lifeLeft <= 20) {
				x = returnFromX * lifeLeft / 20.0 + beingEatenBy.mouthX() * (20 - lifeLeft) / 20.0;
				y = returnFromY * lifeLeft / 20.0 + beingEatenBy.mouthY() * (20 - lifeLeft) / 20.0;
			}
		}
		if (lifeLeft-- <= 0) {
			killMe = true;
			if (beingEatenBy != null) {
				beingEatenBy.maxHP += bleeder.maxHP * sourceMaxHPTransfer;
				beingEatenBy.hp += Math.max(1, bleeder.totalMaxHP() * sourceHPTransfer) + eatHPGain;
				if (sourceThingsTransfer) {
					beingEatenBy.items.addAll(bleeder.items);
					beingEatenBy.weapons.addAll(bleeder.weapons);
					l.texts.add(new FloatingText("ABSORB", beingEatenBy.x + beingEatenBy.w / 2, beingEatenBy.y));
				}
				if (sourceColorTransfer > 0) {
					beingEatenBy.tint = beingEatenBy.tint.mix(sourceColorTransfer, bleeder.tint);
				}
				if (beingEatenBy.eatSoundTimer == 0) {
					beingEatenBy.sound("eat", l);
					beingEatenBy.eatSoundTimer = PatentBlaster.FPS;
				}
			}
			
			if (weapon != null && weapon.grenade && dmgMultiplier == 1) {
				explode(l);
			}
		}
		if (sprayProbability > 0 && l.r.nextDouble() < sprayProbability / PatentBlaster.shotDivider()) {
			l.shotsToAdd.add(new Shot(l, this));
		}
		if (weapon != null && weapon.homing && dmgMultiplier == 1) {
			if ((target == null || target.hp <= 0) && shooter == l.player) {
				// Acquire target!
				double minDistSq = 0;
				target = null;
				for (Creature c : l.monsters) {
					double distSq = (c.x - x) * (c.x - x) + (c.y - y) * (c.y - y);
					if (target == null || distSq < minDistSq) {
						target = c;
						minDistSq = distSq;
					}
				}
			}
			if (target != null && target.hp > 0) {
				double tdx = target.x + target.w / 2 - x - w / 2;
				double tdy = target.y + target.h / 2 - y - h / 2;
				dx += weapon.shotSpeed * 0.15 * tdx / (Math.abs(tdx) + Math.abs(tdy) + 1);
				dy += weapon.shotSpeed * 0.15 * tdy / (Math.abs(tdx) + Math.abs(tdy) + 1);
				double spdSq = dx * dx + dy * dy;
				if (spdSq > weapon.shotSpeed * weapon.shotSpeed) {
					double total = Math.abs(dx) + Math.abs(dy) + 1;
					dx = dx * weapon.shotSpeed / total;
					dy = dy * weapon.shotSpeed / total;
				}
			}
		}
		if (weapon != null && weapon.element == Element.ACID && shooter != null && shooter != l.player && frozenAmt == 0) {
			tint = (l.tick / 8) % 2 == 0 ? ACID_ALT : Element.ACID.tint;
		}
	}
	
	public void explode(Level l) {
		l.soundRequests.add(new SoundRequest("explode", x + w / 2, y + h / 2, 0.5));
		for (int i = 0; i < 40; i++) {
			double dir = l.r.nextDouble() * 2 * Math.PI;
			Shot s = new Shot(l, this);
			s.dx = Math.cos(dir) * weapon.shotSpeed * 1 * (0.3 + l.r.nextDouble());
			s.dy = Math.sin(dir) * weapon.shotSpeed * 1 * (0.3 + l.r.nextDouble());
			s.lifeLeft *= 0.3 + l.r.nextDouble() * 0.3;
			s.gravityMult += 0.4;
			s.dmgMultiplier = 1.0 / 15;
			l.shotsToAdd.add(s);
		}
	}
	
	public Shot(Clr c, double w, double h, boolean pop, int life, double x, double y, double dx, double dy, double grav, Creature origin, boolean reform, boolean revenant, boolean finalForm, double originalX, double originalY, int frozenAmt) {
		this.tint = c;
		this.thawedTint = c;
		this.popOnWorldHit = pop;
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.lifeLeft = life;
		this.w = w;
		this.h = h;
		this.gravityMult = grav;
		this.bleeder = origin;
		this.reform = reform;
		this.finalForm = finalForm;
		this.revenant = revenant;
		this.originalX = originalX;
		this.originalY = originalY;
		this.frozenAmt = frozenAmt;
		if (this.frozenAmt > 0) {
			this.tint = Element.ICE.tint;
		}
		if (reform || finalForm || revenant) {
			this.lifeLeft = 120;
		}
	}
}
