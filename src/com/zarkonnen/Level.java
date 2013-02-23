package com.zarkonnen;

import com.zarkonnen.catengine.Input;
import com.zarkonnen.catengine.MusicDone;
import com.zarkonnen.catengine.util.ScreenMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Level implements MusicDone {
	public static final int GRID_SIZE = 60;
	public static final double G = 0.2;
	public static final double MAX_SPEED = 10;
	public static final int LVL_W = 50;
	public static final int LVL_H = 15;
	public static final int SOLID_START = 2;
	public int[][] grid;
	public int[] gridH;
	public Creature player;
	public ArrayList<Creature> monsters = new ArrayList<Creature>();
	public ArrayList<Creature> monstersToAdd = new ArrayList<Creature>();
	public ArrayList<Goodie> goodies = new ArrayList<Goodie>();
	public ArrayList<Shot> shots = new ArrayList<Shot>();
	public ArrayList<Shot> shotsToAdd = new ArrayList<Shot>();
	public ArrayList<FloatingText> texts = new ArrayList<FloatingText>();
	public int tick = 0;
	public Random r;
	public Creature boss;
	public ArrayList<SoundRequest> soundRequests = new ArrayList<SoundRequest>();
	public String music;
	public boolean musicPlaying = false;
	public int power;
	public int shotsFired = 0;
	public boolean moved = false;
	public int background = -1;
	public int backgroundW = 512;
	public int backgroundH;
	
	public static final String[] MUSICS = { "DST-1990", "DST-4Tran", "DST-ClubNight", "DST-CreepAlong", "DST-Cv-X", "DST-AngryMod" };
	public static final int[] BACKGROUND_HS = {406, 452, 512, 512, 256, 308};
	public static final int NUM_BACKGROUNDS = 6;
	
	public Level(long seed, int power, Creature player) {
		this.power = power;
		this.player = player;
		r = new Random(seed);
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
			if (i != 0 && i != LVL_W - 1 && r.nextInt(8) == 0) {
				grid[LVL_H - 2 - h][i] = 1;
			}
		}
		
		for (int i = 10; i < LVL_W - 10; i++) {
			if (r.nextInt(4) == 0) {
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
		
		boss = Creature.make(seed + 92318, power + 2, PatentBlaster.NUM_IMAGES, true, false, true);
		boss.x = (LVL_W - 8) * GRID_SIZE;
		boss.y = (LVL_H - gridH[LVL_W - 8] - 1) * GRID_SIZE - boss.h - (boss.moveMode == MoveMode.FLY || boss.moveMode == MoveMode.HOVER ? boss.h / 4 : 0) - 1;
		if (r.nextBoolean()) {
			boss.dropItem = true;
		} else {
			boss.dropWeapon = true;
		}
		monsters.add(boss);
	}
	
	public boolean lost() {
		return player.hp <= 0;
	}
	
	public boolean won() {
		if (!monsters.isEmpty()) { return false; }
		if (!goodies.isEmpty()) { return false; }
		if (!texts.isEmpty()) { return false; }
		for (Shot s : shots) {
			if (s.doNotEndLevel()) { return false; }
		}
		return true;
	}
	
	public void tick(Input in) {
		if (tick > 1 && !musicPlaying) {
			musicPlaying = true;
			if (PatentBlaster.musicVolume > 0) {
				in.playMusic(music, PatentBlaster.musicVolume * 1.0 / 9, this);
			}
		}
		try {
			player.tick(this);
			if (player.hp > 0) {
				if (tick % PatentBlaster.FPS / 6 == 0 && player.totalEating() > 0) {
					for (Shot s : shots) {
						eatTest(player, s);
					}
				}
				physics(player);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
					for (Shot s : shots) {
						eatTest(c, s);
					}
				}
				c.tick(this);
				physics(c);
			} catch (Exception e) {
				e.printStackTrace();
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
					}
					if (g.weapon != null) {
						player.newThing = g.weapon;
						player.newThingTimer = 0;
						player.weapons.add(g.weapon);
					}
					g.killMe = true;
					soundRequests.add(new SoundRequest("pickup", player.x + player.w / 2, player.y + player.h / 2, 1.0));
				}
			} catch (Exception e) {
				e.printStackTrace();
				g.killMe = true;
			}
			if (g.killMe) { it.remove(); }
		}
		for (Iterator<Shot> it = shots.iterator(); it.hasNext();) {
			Shot s = it.next();
			try {
				s.tick(this);
				boolean kill = s.killMe;
				physics(s);
				if (s.killMe && !kill && s.hoverer != null) {
					s.hoverer.dy = -0.7;
					s.hoverer.y -= 2.5;
					s.hoverer.ticksSinceBottom = 0;
				}
				if (s.shooter != null) {
					if (s.shooter == player) {
						for (Creature c : monsters) {
							if (hitTest(c, s)) { break; }
						}
					} else {
						hitTest(player, s);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				s.killMe = true;
			}
			if (s.killMe) {
				it.remove();
			}
		}
		shots.addAll(shotsToAdd);
		shotsToAdd.clear();
		for (Iterator<FloatingText> it = texts.iterator(); it.hasNext();) {
			FloatingText ft = it.next();
			try {
				ft.tick(this);
				physics(ft);
			} catch (Exception e) {
				e.printStackTrace();
				ft.killMe = true;
			}
			if (ft.killMe) { it.remove(); }
		}
		ScreenMode sMode = in.mode();
		if (PatentBlaster.soundVolume > 0) {
			for (SoundRequest sr : soundRequests) {
				double sx = (sr.x - player.x - player.w / 2) / sMode.width * 2;
				double sy = (sr.y - player.y - player.h / 2) / sMode.height * 2;
				//System.out.println(sx + "/" + sy);
				in.play(sr.sound, 1.0, sr.volume * PatentBlaster.soundVolume * 1.0 / 9, sx, sy);
			}
		}
		soundRequests.clear();
		tick++;
	}
	
	public void physics(Entity e) {
		e.dy += e.gravityMult * G;
		e.x += e.dx;
		if (!e.collides) { e.y += e.dy; return; }
		int left = (int) Math.floor(e.x / GRID_SIZE);
		int right = (int) Math.floor((e.x + e.w) / GRID_SIZE);
		int top = (int) Math.floor(e.y / GRID_SIZE);
		int bottom = (int) Math.floor((e.y + e.h) / GRID_SIZE);
		if (e.dx < 0 && (grid[top][left] >= SOLID_START || grid[bottom][left] >= SOLID_START)) {
			e.x = (left + 1) * GRID_SIZE + 0.001;
			e.dx = 0;
			left = (int) Math.floor(e.x / GRID_SIZE);
			right = (int) Math.floor((e.x + e.w) / GRID_SIZE);
			e.ticksSinceBottom = 0;
			e.ticksSinceSide = 0;
			if (e.popOnWorldHit) { e.killMe = true; }
		} else if (e.dx > 0 && (grid[top][right] >= SOLID_START || grid[bottom][right] >= SOLID_START)) {
			e.x = right * GRID_SIZE - e.w - 0.001;
			e.dx = 0;
			left = (int) Math.floor(e.x / GRID_SIZE);
			right = (int) Math.floor((e.x + e.w) / GRID_SIZE);
			e.ticksSinceBottom = 0;
			e.ticksSinceSide = 0;
			if (e.popOnWorldHit) { e.killMe = true; }
		}
		e.y += e.dy;
		top = (int) Math.floor(e.y / GRID_SIZE);
		bottom = (int) Math.floor((e.y + e.h) / GRID_SIZE);
		if (e.dy > 0 && (grid[bottom][left] >= SOLID_START || grid[bottom][right] >= SOLID_START)) {
			e.y = bottom * GRID_SIZE - e.h - 0.001;
			e.dy = 0;
			e.ticksSinceBottom = 0;
			if (e.popOnWorldHit) { e.killMe = true; }
		} else if (e.dy < 0 && (grid[top][left] >= SOLID_START || grid[top][right] >= SOLID_START)) {
			e.y = (top + 1) * GRID_SIZE + 0.001;
			e.dy = 0;
			if (e.popOnWorldHit) { e.killMe = true; }
		}
		e.ticksSinceBottom++;
		e.ticksSinceSide++;
	}
	
	public boolean intersectsShot(Creature c, Shot s) {
		if (!intersects(c, s)) { return false; }
		int myX = (int) ((s.x + s.w / 2 - c.x) / c.w * Grids.GRID_SZ);
		int myY = (int) ((s.y + s.h / 2 - c.y) / c.h * Grids.GRID_SZ);
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
		if (s.weapon != null && intersectsShot(c, s) && !s.immune.contains(c)) {
			c.takeDamage(this, s);
			if (c.massive || !s.weapon.penetrates() || s.weapon.homing) {
				s.killMe = true;
				return true;
			} else {
				s.immune.add(c);
			}
		}
		return false;
	}

	@Override
	public void run(String music, double volume) {
		musicPlaying = false;
	}
}
