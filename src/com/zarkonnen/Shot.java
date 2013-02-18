package com.zarkonnen;

import com.zarkonnen.catengine.util.Clr;
import com.zarkonnen.catengine.util.Pt;
import com.zarkonnen.catengine.util.Rect;
import java.util.ArrayList;

public class Shot extends Entity {
	public static final Clr ACID_ALT = new Clr(230, 210, 40);
	
	public Weapon weapon;
	public Creature shooter;
	public double sprayProbability;
	public double dmgMultiplier = 1;
	public ArrayList<Creature> immune = new ArrayList<Creature>();
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
	
	public Shot(Level l, Creature hoverer) {
		this.hoverer = hoverer;
		this.x = hoverer.x + hoverer.w / 4;
		this.y = hoverer.y + hoverer.h;
		this.dx = 0;
		this.dy = 5;
		this.w = hoverer.w / 2;
		this.h = 2;
		this.tint = new Clr(100, 100, 255);
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
		this.weapon = w;
		this.shooter = shooter;
		this.tint = w.element.tint;
		this.w = w.shotSize;
		this.h = w.shotSize;
		this.x = shooter.x + shooter.w / 2 - w.shotSize / 2;
		this.y = shooter.y + shooter.h / 2 - w.shotSize / 2;
		popOnWorldHit = true;
		gravityMult = 0;
		double dtx = tx - (shooter.x + shooter.w / 2), dty = ty - (shooter.y + shooter.h / 2);
		double dtx2 = dtx + dty * l.r.nextDouble() * w.jitter;
		dty += dtx * l.r.nextDouble() * w.jitter;
		dtx = dtx2;
		dx = w.shotSpeed * dtx / (Math.abs(dtx) + Math.abs(dty) + 0.001);
		dy = w.shotSpeed * dty / (Math.abs(dtx) + Math.abs(dty) + 0.001);
		lifeLeft = w.shotLife;
		immune.add(shooter);
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
	}
	
	public Shot(Level l, Shot p) {
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
				dmgMultiplier = 0.01 * PatentBlaster.shotDivider();
				break;
			case ICE:
				gravityMult = 0.1;
				popOnWorldHit = true;
				dmgMultiplier = 0.005 * PatentBlaster.shotDivider();
				break;
			case STEEL:
				gravityMult = 0;
				popOnWorldHit = true;
				dmgMultiplier = 0.2 * PatentBlaster.shotDivider();
				break;
		}
		immune.add(shooter);
	}
	
	public boolean doNotEndLevel() {
		return reform || finalForm || revenant;
	}
	
	@Override
	public void tick(Level l) {
		if (frozenAmt > 0) {
			frozenAmt--;
			if (frozenAmt == 0 && thawedTint != null) {
				tint = thawedTint;
			}
		}
		if ((reform || finalForm || revenant) && lifeLeft <= 60) {
			if (lifeLeft == 60) {
				returnFromX = x;
				returnFromY = y;
				originalTint = tint;
				if (!bleeder.unsplorted) {
					l.soundRequests.add(new SoundRequest("desquelch", bleeder.x + bleeder.w / 2, bleeder.y + bleeder.h / 2, bleeder.w / 40));
					bleeder.unsplorted = true;
					if (revenant) {
						// Make bleeder into zombie.
						bleeder.tint = new Clr(100, 130, 90);
						bleeder.maxHP *= 3;
						bleeder.weapon.element = Element.ACID;
						bleeder.weapon.shotSpeed /= 2;
						bleeder.weapon.jitter += 0.15;
						bleeder.speed /= 3;
						bleeder.reviens = false;
						bleeder.isZombie = true;
						bleeder.resistance = null;
						Item zombieVirus = new Item();
						zombieVirus.name = "Zombie Virus";
						zombieVirus.eating = 0.05;
						bleeder.items.add(zombieVirus);
						bleeder.dropItem = false;
						bleeder.dropWeapon = false;
						bleeder.voice = 1;
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
					for (Shot s : l.shots) { // No sneaking up!
						s.immune.add(reborn);
					}
				}
				killMe = true;
			}
			return;
		}
		
		if (lifeLeft-- <= 0) {
			killMe = true;
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
				dx += weapon.shotSpeed * 0.1 * tdx / (Math.abs(tdx) + Math.abs(tdy) + 1);
				dy += weapon.shotSpeed * 0.1 * tdy / (Math.abs(tdx) + Math.abs(tdy) + 1);
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
	
	public Shot(Clr c, double sz, boolean pop, int life, double x, double y, double dx, double dy, double grav, Creature origin, boolean reform, boolean revenant, boolean finalForm, double originalX, double originalY, int frozenAmt) {
		this.tint = c;
		this.thawedTint = c;
		this.popOnWorldHit = pop;
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
		this.lifeLeft = life;
		this.w = sz;
		this.h = sz;
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
