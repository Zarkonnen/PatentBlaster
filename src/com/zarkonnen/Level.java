package com.zarkonnen;

import com.zarkonnen.catengine.Input;
import com.zarkonnen.catengine.MusicCallback;
import com.zarkonnen.catengine.util.ScreenMode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class Level implements MusicCallback, Serializable {
	public static final int GRID_SIZE = 60;
	public static final double G = 0.2;
	public static final double MAX_SPEED = 10;
	public static final int LVL_W = 50;
	public static final int LVL_H = 15;
	public static final int SOLID_START = 2;
	public static final int MAX_SOUND_REQUESTS = 4;
	public int[][] grid;
	public int[] gridH;
	public Creature player;
	public ArrayList<Creature> monsters = new ArrayList<Creature>();
	public ArrayList<Creature> monstersToAdd = new ArrayList<Creature>();
	public ArrayList<Goodie> goodies = new ArrayList<Goodie>();
	public ArrayList<Shot> shots = new ArrayList<Shot>(100);
	public LinkedList<Shot> flammables = new LinkedList<Shot>();
	public ArrayList<Shot> shotsToAdd = new ArrayList<Shot>();
	public ArrayList<FloatingText> texts = new ArrayList<FloatingText>();
	public ArrayList<Barrel> barrels = new ArrayList<Barrel>();
	public int tick = 0;
	public Random r;
	public Creature boss;
	public ArrayList<SoundRequest> soundRequests = new ArrayList<SoundRequest>();
	public String music;
	public transient boolean musicPlaying = false;
	public int power;
	public int shotsFired = 0;
	public int ticksWhiteRectShown = 0;
	public boolean movedLeft = false;
	public boolean movedRight = false;
	public boolean movedUp = false;
	public boolean bees;
	public int background = -1;
	public int backgroundW = 512;
	public int backgroundH;
	public boolean releasedSinceShot = false;
	public ArrayList<Object> shopItems = new ArrayList<Object>();
	public LinkedList<Integer> bbqVictims = new LinkedList<Integer>();
	public LinkedList<Goodie> goodiesBeingTaken = new LinkedList<Goodie>();
	public boolean[] window = new boolean[LVL_W * GRID_SIZE / 512 + 2];
	
	public static final String[] MUSICS = { "DST-1990", "DST-4Tran", "DST-ClubNight", "DST-CreepAlong", "DST-Cv-X", "DST-AngryMod" };
	public static final int[] BACKGROUND_HS = {406, 452, 512, 512, 256, 308};
	public static final int NUM_BACKGROUNDS = 6;
	
	public Level(long seed, int power, Creature player) {
		this.power = power;
		this.player = player;
		r = new Random(seed);
		boolean hasBarrels = power > 1 && r.nextBoolean();
		Barrel.Type bType = Barrel.Type.available()[r.nextInt(Barrel.Type.available().length)];
		music = MUSICS[r.nextInt(MUSICS.length)];
		background = r.nextInt(NUM_BACKGROUNDS);
		backgroundH = background > -1 ? BACKGROUND_HS[background] : 512;
		grid = new int[LVL_H][LVL_W];
		gridH = new int[LVL_W];
		for (int i = 0; i < LVL_H; i++) {
			grid[i][0] = SOLID_START;
			grid[i][LVL_W - 1] = SOLID_START;
		}
		int h = 0;
		for (int i = 0; i < LVL_W; i++) {
			grid[0][i] = SOLID_START;
			if (i < LVL_W - 4 && i > 3 && i % 3 == 0 && r.nextInt(5) != 0) {
				h = h - 1 + r.nextInt(3);
				h = Math.min(LVL_H - 6, Math.max(0, h));
			}
			gridH[i] = h;
			for (int y = LVL_H - 1 - h; y < LVL_H; y++) {
				grid[y][i] = SOLID_START;
			}
			if (i != 0 && i != LVL_W - 1) {
				if (r.nextInt(12) == 0) {
					grid[LVL_H - 2 - h][i] = 1;
				} else if (i > 4 && hasBarrels && r.nextInt(9) == 0) {
					barrels.add(new Barrel(bType, seed, power, i * GRID_SIZE + 1 + r.nextInt(7), (LVL_H - gridH[i] - 1) * GRID_SIZE - 61, r));
				}
			}
		}
		
		for (int i = 0; i < window.length; i++) {
			window[i] = r.nextInt(20) == 0;
		}
		
		int cFreq = power > 30 ? 1 : power > 15 ? 2 : 3;
		int monsterStart = (power < 3 && PatentBlaster.difficultyLevel.ordinal() < DifficultyLevel.BRUTAL.ordinal())
				? 18 : 9;
		for (int i = monsterStart; i < LVL_W - 10; i++) {
			if (r.nextInt(cFreq) == 0) {
				int type = r.nextInt(4);
				Creature c = Creature.make(seed + type * 12345, power, PatentBlaster.NUM_IMAGES, false, false, true);
				int reach = (int) Math.floor(c.w / GRID_SIZE);
				if (grid[LVL_H - gridH[i] - 2][i + reach] >= SOLID_START) {
					continue;
				}
				c.x = i * GRID_SIZE;
				c.y = (LVL_H - gridH[i] - 1) * GRID_SIZE - c.h - (c.moveMode == MoveMode.FLY || c.moveMode == MoveMode.HOVER ? c.h / 4 : 0) - 1;
				monsters.add(c);
			}
		}
		
		player.x = GRID_SIZE * 2;
		player.y = (LVL_H - gridH[2] - 1) * GRID_SIZE - player.h - (player.moveMode == MoveMode.FLY || player.moveMode == MoveMode.HOVER ? player.h / 4 : 0) - 1;
		
		int numBosses = (power % 20) == 10 ? 4 : 1;
		for (int b = 0; b < numBosses; b++) {
			boss = Creature.make(seed + 92318, power * 3 / 2 + 5, PatentBlaster.NUM_IMAGES, true, false, true);
			if ((power % 20) == 10) {
				boss = player.makeTinyVersion(this);
				boss.encounterMessage = "MEET TINY YOU";
				boss.dropWeapon = false;
				boss.dropItem = false;
			}
			if ((power % 20) == 5) {
				boss = player.makeTwin(this);
				boss.invert();
				boss.encounterMessage = "MEET YOUR EVIL TWIN";
				boss.dropWeapon = false;
				boss.dropItem = true;
			}
			if ((power % 20) == 15) {
				boss = player.makeGiantVersion(this);
				boss.encounterMessage = "MEET GIANT YOU";
				boss.dropWeapon = false;
				boss.dropItem = true;
			}
			boss.heal();
			boss.x = (LVL_W - 8 - b) * GRID_SIZE;
			boss.y = (LVL_H - gridH[LVL_W - 8 - b] - 1) * GRID_SIZE - boss.h - (boss.moveMode == MoveMode.FLY || boss.moveMode == MoveMode.HOVER ? boss.h / 4 : 0) - 1;
			if (r.nextBoolean()) {
				boss.dropItem = true;
			} else {
				boss.dropWeapon = true;
			}
			monsters.add(boss);
		}
	}
	
	public boolean lost() {
		return player.hp <= 0;
	}
	
	public boolean won() {
		if (!monsters.isEmpty()) { return false; }
		if (!goodies.isEmpty()) { return false; }
		if (!texts.isEmpty()) { return false; }
		for (Shot s : shots) { if (s == null) { continue; }
			if (s.doNotEndLevel()) { return false; }
		}
		return true;
	}
	
	public void tick(Input in) {
		for (Iterator<Integer> it = bbqVictims.iterator(); it.hasNext();) {
			if (it.next() + PatentBlaster.FPS < tick) {
				it.remove();
			}
		}
		if (tick > 1 && !musicPlaying) {
			musicPlaying = true;
			if (PatentBlaster.musicVolume > 0) {
				in.playMusic(music, PatentBlaster.musicVolume * 1.0 / 9, null, this);
			}
		}
		try {
			player.tick(this);
			if (player.hp > 0) {
				if (tick % PatentBlaster.FPS / 6 == 0 && player.totalEating() > 0) {
					for (Shot s : shots) { if (s == null) { continue; }
						eatTest(player, s);
					}
				}
				physics(player);
			}
		} catch (Exception e) {
			e.printStackTrace(PatentBlaster.ERR_STREAM);
			player.killMe = true;
			player.hp = -1;
			texts.add(new FloatingText("KILLED BY PHYSICS!", player.x + player.w / 2, player.y));
			soundRequests.add(new SoundRequest("killed_by_physics", player.x + player.w / 2, player.y + player.h / 2, 1.0));
		}
		int cIndex = 0;
		for (Iterator<Creature> it = monsters.iterator(); it.hasNext();) {
			Creature c = it.next();
			try {
				if (tick % (PatentBlaster.FPS / 6) == cIndex++ % PatentBlaster.FPS / 6 && c.totalEating() > 0) {
					for (Shot s : shots) {  if (s == null) { continue; }
						eatTest(c, s);
					}
				}
				c.tick(this);
				physics(c);
			} catch (Exception e) {
				e.printStackTrace(PatentBlaster.ERR_STREAM);
				c.killMe = true;
				c.hp = -1;
				texts.add(new FloatingText("KILLED BY PHYSICS!", c.x + c.w / 2, c.y));
				if (Math.abs(c.x + c.w / 2 - player.x - player.w / 2) < 500) {
					soundRequests.add(new SoundRequest("killed_by_physics", c.x + c.w / 2, c.y + c.h / 2, 0.7));
				}
			}
			if (c.killMe) { it.remove(); }
		}
		monsters.addAll(monstersToAdd);
		monstersToAdd.clear();
		for (Iterator<Goodie> it = goodies.iterator(); it.hasNext();) {
			Goodie g = it.next();
			try {
				g.tick(this);
				physics(g);
				if (intersects(g, player)) {
					if (g.item != null) {
						player.newThing = g.item;
						player.newThingTimer = 0;
						player.items.add(g.item);
						player.canSeeStats = player.canSeeStats || g.item.givesInfo;
						goodiesBeingTaken.add(g);
					}
					if (g.weapon != null) {
						if (!player.weapons.contains(g.weapon)) {
							player.newThing = g.weapon;
							player.newThingTimer = 0;
							player.weapons.add(g.weapon);
							goodiesBeingTaken.add(g);
						}
					}
					g.killMe = true;
					soundRequests.add(new SoundRequest("pickup", player.x + player.w / 2, player.y + player.h / 2, 1.0));
				}
			} catch (Exception e) {
				e.printStackTrace(PatentBlaster.ERR_STREAM);
				g.killMe = true;
			}
			if (g.killMe) { it.remove(); }
		}
		addShots();
		for (int i = 0; i < shots.size(); i++) {
			Shot s = shots.get(i);
			if (s == null) { continue; }
			try {
				s.tick(this);
				boolean kill = s.killMe;
				physics(s);
				if (s.killMe && !kill && s.weapon != null && s.weapon.grenade && s.dmgMultiplier == 1) {
					s.explode(this);
				}
				if (s.killMe && !kill && s.hoverer != null) {
					s.hoverer.dy = -0.7;
					s.hoverer.y -= 2.5;
					s.hoverer.ticksSinceBottom = 0;
				}
				if ((s.age < 2 || s.killMe) && s.weapon != null && s.weapon.element == Element.FIRE) {
					for (Iterator<Shot> fit = flammables.iterator(); fit.hasNext();) {
						Shot flam = fit.next();
						if (flam.killMe) { // Doing some GC while we're at it.
							fit.remove();
							continue;
						}
						if (intersects(s, flam)) {
							flam.weapon = flam.flammableWeapon;
							flam.shooter = s.shooter;
							flam.dmgMultiplier = 0; // All damage done via spray.
							flam.slipperiness = 0;
							flam.sprayProbability = 0.03;
							flam.tint = Element.FIRE.tint;
							flam.flammable = false;
							fit.remove();
							flam.remains = true;
							flam.lifeLeft /= 10;
							flam.freeAgent = true;
							flam.dy = -1;
							flam.age = 0;
						}
					}
				}
				if (s.shooter != null) {
					if (s.shooter == player || s.freeAgent) {
						for (Creature c : monsters) {
							if (hitTest(c, s)) { break; }
						}
					}
					if (s.shooter != player || s.freeAgent) {
						hitTest(player, s);
					}
				}
				if (s.slipperiness > 0) {
					if (intersects(player, s)) {
						player.slipperiness = s.slipperiness;
					}
					for (Creature c : monsters) {
						if (intersects(c, s)) {
							c.slipperiness = s.slipperiness;
						}
					}
				}
				if (s.stickiness > 0) {
					boolean playerStuck = false;
					if (s.shooter != player) {
						if (intersectsShot(player, s)) {
							if (s.stickiness > 0) {
								s.killMe = true;
								s.x -= player.x;
								if (player.flipped) {
									s.x = player.w - s.x;
								}
								s.y -= player.y;
								s.lifeLeft = PatentBlaster.FPS * 4;
								player.stuckShots.add(s);
								playerStuck = true;
							}
						}
					}
					if (!playerStuck) {
						for (Creature c : monsters) {
							if (s.shooter != c && intersectsShot(c, s)) {
								if (s.stickiness > 0) {
									s.killMe = true;
									s.x -= c.x;
									if (c.flipped) {
										s.x = c.w - s.x;
									}
									s.y -= c.y;
									s.lifeLeft = PatentBlaster.FPS * 4;
									c.stuckShots.add(s);
									break;
								}
							}
						}
					}
				}
				if (!s.killMe && s.weapon != null) {
					for (Barrel b : barrels) {
						if (intersects(s, b)) {
							if (!s.remains && !s.weapon.sword) {
								s.killMe = true;
							}
							b.doDamage(this, s);
							if (b.killMe) {
								barrels.remove(b);
							}
							break;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace(PatentBlaster.ERR_STREAM);
				s.killMe = true;
			}
			if (s.killMe) {
				//it.remove();
				shots.set(i, null);
			}
		}
		for (Iterator<Goodie> it = goodiesBeingTaken.iterator(); it.hasNext();) {
			if (++it.next().timeSpentTaken > PatentBlaster.GOODIE_FETCH_TICKS) {
				it.remove();
			}
		}
		addShots();
		for (Iterator<FloatingText> it = texts.iterator(); it.hasNext();) {
			FloatingText ft = it.next();
			try {
				ft.tick(this);
				physics(ft);
			} catch (Exception e) {
				e.printStackTrace(PatentBlaster.ERR_STREAM);
				ft.killMe = true;
			}
			if (ft.killMe) { it.remove(); }
		}
		ScreenMode sMode = in.mode();
		if (PatentBlaster.soundVolume > 0) {
			Collections.sort(soundRequests);
			int played = 0;
			for (SoundRequest sr : soundRequests) {
				double sx = (sr.x - player.x - player.w / 2) / sMode.width * 2;
				double sy = (sr.y - player.y - player.h / 2) / sMode.height * 2;
				//System.out.println(sx + "/" + sy);
				in.play(sr.sound, 1.0, sr.volume * PatentBlaster.soundVolume * 1.0 / 9, sx, sy);
				if (++played >= MAX_SOUND_REQUESTS) { break; }
			}
		}
		soundRequests.clear();
		tick++;
	}
	
	void addShots() {
		int i = 0;
		for (Shot s : shotsToAdd) {
			if (s.flammable) {
				flammables.add(s);
			}
			while (i < shots.size() && shots.get(i) != null) {
				i++;
			}
			if (i == shots.size()) {
				shots.add(s);
			} else {
				shots.set(i, s);
			}
		}
		shotsToAdd.clear();
	}
	
	public void physics(Entity e) {
		e.dy += e.gravityMult * G;
		if (e instanceof Creature && ((Creature) e).slipperiness > 0) {
			e.dx *= 2;
		}
		int iters = 1;
		if (!e.popOnWorldHit && (Math.abs(e.dx) > e.w / 2 || Math.abs(e.dy) > e.h / 2)) {
			while ((e.dx > e.w / 2 || e.dy > e.h / 2)) {
				iters *= 2;
				e.dx /= 2;
				e.dy /= 2;
			}
		}
		for (int i = 0; i < iters; i++) {
			e.x += e.dx;
			if (!e.collides || e.ignoresWalls) { e.y += e.dy; return; }
			int left = Math.min(LVL_W - 1, Math.max(0, (int) Math.floor(e.x / GRID_SIZE)));
			int right = Math.min(LVL_W - 1, Math.max(0, (int) Math.floor((e.x + e.w) / GRID_SIZE)));
			int top = Math.min(LVL_H - 1, Math.max(0, (int) Math.floor(e.y / GRID_SIZE)));
			int bottom = Math.min(LVL_H - 1, Math.max(0, (int) Math.floor((e.y + e.h) / GRID_SIZE)));
			
			if (grid[top][left] >= SOLID_START &&
				grid[bottom][left] >= SOLID_START &&
				grid[top][right] >= SOLID_START &&
				grid[bottom][right] >= SOLID_START)
			{
				if (e.popOnWorldHit) {
					e.killMe = true;
					return;
				}
				double cx = e.x + e.w / 2;
				int gx = Math.min(LVL_W - 1, Math.max(0, (int) Math.floor(cx / GRID_SIZE)));
				double cy = e.y + e.h / 2;
				int gy = Math.min(LVL_H - 1, Math.max(0, (int) Math.floor(cy / GRID_SIZE)));
				boolean found = false;
				double leastDistSq = 1000000000.0;
				int bestDx = 0;
				int bestDy = 0;
				for (int dy = -1; dy < 2; dy++) { for (int dx = -1; dx < 2; dx++) {
					int tgx = gx + dx;
					int tgy = gy + dy;
					if (tgx < 0 || tgx >= LVL_W || tgy < 0 || tgx >= LVL_H || grid[tgy][tgx] >= SOLID_START) {
						continue;
					}
					int tcx = tgx * GRID_SIZE + GRID_SIZE / 2;
					int tcy = tgy * GRID_SIZE + GRID_SIZE / 2;
					double distSq = (cx - tcx) * (cx - tcx) + (cy - tcy) * (cy - tcy);
					if (distSq < leastDistSq) {
						found = true;
						bestDx = dx;
						bestDy = dy;
						leastDistSq = distSq;
					}
				}}
				
				if (found) {
					if (bestDx == 1) {
						e.x = (gx + bestDx) * GRID_SIZE;
					}
					if (bestDx == -1) {
						e.x = (gx + bestDx + 1) * GRID_SIZE - e.w - 0.0001;
					}
					if (bestDy == 1) {
						e.y = (gy + bestDy) * GRID_SIZE;
					}
					if (bestDy == -1) {
						e.y = (gy + bestDy + 1) * GRID_SIZE - e.h - 0.0001;
					}
				} else {
					// Sob, put it into a safe place.
					if (e instanceof Shot) {
						e.killMe = true;
						return;
					} else {
						e.x = LVL_W * GRID_SIZE / 2;
						e.y = 3 * GRID_SIZE;
						e.dx = 0;
						e.dy = 0;
						texts.add(new FloatingText("RANDOM TELEPORT!", e.x + e.w / 2, e.y));
					}
				}
				
				left = Math.min(LVL_W - 1, Math.max(0, (int) Math.floor(e.x / GRID_SIZE)));
				right = Math.min(LVL_W - 1, Math.max(0, (int) Math.floor((e.x + e.w) / GRID_SIZE)));
				top = Math.min(LVL_H - 1, Math.max(0, (int) Math.floor(e.y / GRID_SIZE)));
				bottom = Math.min(LVL_H - 1, Math.max(0, (int) Math.floor((e.y + e.h) / GRID_SIZE)));
			}
			
			if ((grid[top][left] >= SOLID_START || grid[bottom][left] >= SOLID_START)) {
				e.x = (left + 1) * GRID_SIZE + 0.001;
				left = Math.min(LVL_W - 1, Math.max(0, (int) Math.floor(e.x / GRID_SIZE)));
				right = Math.min(LVL_W - 1, Math.max(0, (int) Math.floor((e.x + e.w) / GRID_SIZE)));
				e.ticksSinceBottom = 0;
				e.ticksSinceSide = 0;
				e.leftPress += e.pressAmount;
				if (e.popOnWorldHit) { e.killMe = true; }
			} else if ((grid[top][right] >= SOLID_START || grid[bottom][right] >= SOLID_START)) {
				e.x = right * GRID_SIZE - e.w - 0.001;
				left = Math.min(LVL_W - 1, Math.max(0, (int) Math.floor(e.x / GRID_SIZE)));
				right = Math.min(LVL_W - 1, Math.max(0, (int) Math.floor((e.x + e.w) / GRID_SIZE)));
				e.ticksSinceBottom = 0;
				e.ticksSinceSide = 0;
				e.rightPress += e.pressAmount;
				if (e.popOnWorldHit) { e.killMe = true; }
			}
			e.y += e.dy;
			top = Math.min(LVL_H - 1, Math.max(0, (int) Math.floor(e.y / GRID_SIZE)));
			bottom = Math.min(LVL_H - 1, Math.max(0, (int) Math.floor((e.y + e.h) / GRID_SIZE)));
			if ((grid[bottom][left] >= SOLID_START || grid[bottom][right] >= SOLID_START)) {
				e.y = bottom * GRID_SIZE - e.h - 0.001;
				if (e.dy > G * 2) {
					e.bottomPress = (int) (e.dy * e.bottomPressSpeedMult);
				}
				e.dy = 0;
				e.ticksSinceBottom = 0;
				if (e.popOnWorldHit) { e.killMe = true; }
			} else if ((grid[top][left] >= SOLID_START || grid[top][right] >= SOLID_START)) {
				e.y = (top + 1) * GRID_SIZE + 0.001;
				e.dy = 0;
				if (e.popOnWorldHit) { e.killMe = true; }
			}
		}
		e.dx *= iters;
		e.dy *= iters;
		e.ticksSinceBottom++;
		e.ticksSinceSide++;
		e.leftPress = Math.min(e.maxPress, Math.max(0, e.leftPress - e.inflateAmount));
		e.rightPress = Math.min(e.maxPress, Math.max(0, e.rightPress - e.inflateAmount));
		e.bottomPress = Math.min(e.maxPress, Math.max(0, e.bottomPress - e.bottomInflateAmount));
		if (e instanceof Creature && ((Creature) e).slipperiness > 0) {
			e.dx /= 2.05;
		}
	}
	
	public boolean intersectsShot(Creature c, Shot s) {
		if (!intersects(c, s)) { return false; }
		double imgW = c.w / PatentBlaster.IMG_W[c.imgIndex];
		double imgH = c.h / PatentBlaster.IMG_H[c.imgIndex];
		double imgX = c.x - imgW / 2 + c.w / 2;
		double imgY = c.y - imgH + c.h;
		int myX = (int) ((s.x + s.w / 2 - imgX) / imgW * Grids.GRID_SZ);
		int myY = (int) ((s.y + s.h / 2 - imgY) / imgH * Grids.GRID_SZ);
		if (myX < 0 || myY < 0 || myX >= Grids.GRID_SZ || myY >= Grids.GRID_SZ) {
			return false;
		}
 		return c.grid()[myY][myX];
	}
    	
	 public boolean intersects(Entity e1, Entity e2) {
		return	
				e1.collides && e2.collides &&
				e1.x < e2.x + e2.w &&
				e1.x + e1.w > e2.x &&
				e1.y < e2.y + e2.h &&
				e1.y + e1.h > e2.y;
	}
	
	private void eatTest(Creature c, Shot s) {
		if (s.beingEatenBy == null && c.frozen == 0 && s.bleeder != null && s.bleeder != c && !s.bleeder.jar && s.frozenAmt == 0 && !s.reform && !s.finalForm && !s.revenant && (s.bleeder.resistance == null || s.bleeder.resistance == c.resistance) && c.totalEating() > 0 && intersects(c, s))
		{
			s.eat(c, c.totalEating(), 0, 0, false);
		}
	}

	private boolean hitTest(Creature c, Shot s) {
		if (s.weapon != null && intersectsShot(c, s) && (s.freeAgent || (s.shooter != c && (s.immune == null || !s.immune.contains(c))))) {
			int preHP = c.hp;
			boolean preFrozen = c.frozen > 0;
			boolean preOnFire = c.onFire > 0;
			c.takeDamage(this, s);
			double dx = s.x + s.w / 2 - player.x - player.w / 2;
			double dy = s.y + s.h / 2 - player.y - player.h / 2;
			if (s.shooter == player && dx * dx + dy * dy < 550 * 550 && dx * dx + dy * dy > player.w * player.w) {
				if (preHP > 0 && c.hp <= 0) {
					s.knownKills++;
					if (s.knownKills == 1 && s.weapon.element == Element.ACID && s.age >= 20 && s.dy != 0 && s.dmgMultiplier < 1) {
						texts.add(new FloatingText("ACID RAIN", s.x + s.w / 2, s.y));
						//soundRequests.add(new SoundRequest("acid_rain", s.x + s.w / 2, s.y + s.h / 2, 1.0));
					}
					if (s.knownKills == 3 && s.weapon.element == Element.STEEL) {
						texts.add(new FloatingText("SHISH KEBAB", s.x + s.w / 2, s.y));
						//soundRequests.add(new SoundRequest("shish_kebab", s.x + s.w / 2, s.y + s.h / 2, 1.0));
					}
				}
				if (!preFrozen && c.frozen > 0 && s.dmgMultiplier < 1 && s.weapon.element == Element.ICE && s.age >= 20) {
					texts.add(new FloatingText("IT'S SNOWING", s.x + s.w / 2, s.y));
					//soundRequests.add(new SoundRequest("its_snowing", s.x + s.w / 2, s.y + s.h / 2, 1.0));
				}
				if (!preOnFire && c.onFire > 0 && s.weapon.element == Element.FIRE) {
					bbqVictims.add(tick);
					if (bbqVictims.size() == 3) {
						texts.add(new FloatingText("BBQ", s.x + s.w / 2, s.y));
						//soundRequests.add(new SoundRequest("BBQ", s.x + s.w / 2, s.y + s.h / 2, 1.0));
					}
				}
			}
			if (!s.remains && !s.weapon.sword && (c.massive || !s.weapon.penetrates() || s.weapon.homing)) {
				s.killMe = true;
				return true;
			} else {
				if (s.immune != null) {
					s.immune.add(c);
				}
			}
		}
		return false;
	}

	@Override
	public void run(String music, double volume) {
		musicPlaying = false;
	}
}
