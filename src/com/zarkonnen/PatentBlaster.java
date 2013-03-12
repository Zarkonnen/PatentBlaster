package com.zarkonnen;

import com.zarkonnen.catengine.Condition;
import com.zarkonnen.catengine.Draw;
import com.zarkonnen.catengine.Engine;
import com.zarkonnen.catengine.Fount;
import com.zarkonnen.catengine.Frame;
import com.zarkonnen.catengine.Game;
import com.zarkonnen.catengine.Hook;
import com.zarkonnen.catengine.Hook.Type;
import com.zarkonnen.catengine.Hooks;
import com.zarkonnen.catengine.Img;
import com.zarkonnen.catengine.Input;
import com.zarkonnen.catengine.MusicCallback;
import com.zarkonnen.catengine.SlickEngine;
import com.zarkonnen.catengine.util.Clr;
import com.zarkonnen.catengine.util.Pt;
import com.zarkonnen.catengine.util.Rect;
import com.zarkonnen.catengine.util.ScreenMode;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import static com.zarkonnen.catengine.util.Utils.*;
import com.zarkonnen.trigram.Trigrams;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;

public class PatentBlaster implements Game {
	public static final boolean DEMO = false;
	public static final int DEMO_LEVELS = 3;
	
	public static final int NUM_ITEM_IMAGES = DEMO ? 5 : 14;
	public static final int NUM_IMAGES = DEMO ? 5 : 18;
	public static final int NUM_VOICES = DEMO ? 3 : 14;
	public static final int FPS = 60;
	public static final String ALPHABET = " qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890-=+_!?<>,.;:\"'@£$%^&*()[]{}|\\~/±";
	public static final Fount GOUNT = new Fount("LiberationMono64", 50, 84, 50, 84, ALPHABET);

	public static final Fount FOUNT = new Fount("LiberationMono18", 14, 24, 12, 24, ALPHABET);
	public static final Fount SMOUNT = new Fount("Courier12", 10, 15, 7, 15, ALPHABET);
	public static final String[] IMG_NAMES = {"boxer", "bat", "bear", "elephant", "thing", "mummy", "tongue", "eye", "brain", "robot", "head", "duck", "empty_bear", "screenhead", "crawler", "shrugger", "stick_demon", "smiler"};
	public static final String[] PRETTY_IMG_NAMES = {"Boxer", "Bat", "Bear", "Elephant", "Brain-Thing", "Mummy", "Tongue", "Eye", "Brain", "Robot", "Head", "Duck", "Empty Bear", "Screenhead", "Crawler", "Shrugger", "Stick Demon", "Smiler"};
	public static final boolean[] ANIM = { true, true, true, false, true, true, false, false, false, false, false, true, true, true, false, false, false, true };
	public static final int[] ANIM_LENGTH = { 200, 500, 500, 0, 20, 120, 0, 0, 0, 0, 0, 500, 8, 20, 0, 0, 0, 300 };
	public static final int[] ANIM_B_LENGTH = { 20, 10, 5, 0, 10, 60, 0, 0, 0, 0, 0, 40, 4, 10, 0, 0, 0, 50 };
	public static final int[] IMG_NUMS = { 4, 1, 3, 3, 3, 1, 2, 0, 1, 4, 2, 0, 5, 3, 0, 0, 0, 0 };
	public static final double[] IMG_SHOOT_X = { 0.32, 0.44, 0.45, 0.18, 0.56, 0.49, 0.50, 0.20, 0.37, 0.15, 0.50, 0.33, 0.50,
	0.50, 0.21, 0.50, 0.50, 0.17};
	public static final double[] IMG_SHOOT_Y = { 0.41, 0.51, 0.65, 0.47, 0.08, 0.12, 0.14, 0.50, 0.37, 0.48, 0.28, 0.50, 0.39,
	0.11, 0.54, 0.33, 0.50, 0.28};
	public static final double[] IMG_MOUTH_X = { 0.41, 0.44, 0.50, 0.10, 0.54, 0.47, 0.48, 0.20, 0.67, 0.47, 0.50, 0.19, 0.50,
	0.50, 0.21, 0.46, 0.50, 0.65};
	public static final double[] IMG_MOUTH_Y = { 0.21, 0.51, 0.43, 0.83, 0.57, 0.24, 0.53, 0.50, 0.67, 0.18, 0.77, 0.40, 0.39,
	0.17, 0.54, 0.55, 0.50, 0.30};
	
	public static final double[] IMG_W =       { 0.45, 1.00, 0.82, 1.00, 0.83, 0.69, 1.00, 0.75, 1.00, 0.85, 0.71, 1.00, 0.75,
	0.35, 1.00, 1.00, 0.73, 0.95};
	public static final double[] IMG_H =       { 1.00, 0.79, 1.00, 0.89, 1.00, 1.00, 0.49, 1.00, 0.79, 1.00, 1.00, 0.70, 1.00,
	1.00, 0.64, 0.86, 1.00, 1.00};
	
	public static final String[] ITEM_NAMES = { "pan", "orientation", "mycology", "balls", "db", "controller", "no", "soup", "cloud", "camera", "save", "sword", "tv", "manacle"};
	
	public static final Clr PAPER = new Clr(230, 230, 225);
	public static final Clr PAINTING_FRAME = new Clr(70, 50, 20);
	public static final Clr PAINTING_BG = new Clr(150, 150, 100);
	public static final Clr RELOADING_CURSOR = new Clr(200, 200, 200);
	public static final Clr DYING = new Clr(255, 100, 100, 32);
	public static final Clr DEAD = new Clr(255, 100, 100, 127);
	
	public static final int[] WIN_Y_INDEX = { 1, 1, 1, 0, 1, -1 };
	
	public static final int GOODIE_FETCH_TICKS = 20;
	
	public static final HashMap<String, Img> CREATURE_IMGS;
	public static final HashMap<String, Img> ITEM_IMGS;
	public static final HashMap<String, Img> LARGE_ITEM_IMGS;
	
	public static final PrintStream ERR_STREAM;
	
	static {
		PrintStream es = System.err;
		try {
			es = new PrintStream(new FileOutputStream(new File("patent_blaster_log.txt"), true), true);
		} catch (Exception e) {
			e.printStackTrace(es);
		}
		ERR_STREAM = es;
		
		HashMap<String, Img> cis = null;
		try {
			InputStream is = PatentBlaster.class.getResourceAsStream("images/units.txt");
			cis = Img.loadMap(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace(ERR_STREAM);
			System.exit(1);
		}
		CREATURE_IMGS = cis;
		
		HashMap<String, Img> iis = null;
		try {
			InputStream is = PatentBlaster.class.getResourceAsStream("images/items.txt");
			iis = Img.loadMap(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace(ERR_STREAM);
			System.exit(1);
		}
		ITEM_IMGS = iis;
		
		HashMap<String, Img> liis = null;
		try {
			InputStream is = PatentBlaster.class.getResourceAsStream("images/large_items.txt");
			liis = Img.loadMap(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace(ERR_STREAM);
			System.exit(1);
		}
		LARGE_ITEM_IMGS = liis;
	}
			
	public static void main(String[] args) {
		Names.namesLoaded();
		autoLoad();
		Engine e = new SlickEngine("Patent Blaster", "/com/zarkonnen/images/", "/com/zarkonnen/sounds/", FPS);
		e.setup(new PatentBlaster());
		e.runUntil(Condition.ALWAYS);
	}
	
	Hooks h = new Hooks();
	Hooks pastH;
	double scrollX = 0;
	double scrollY = 0;
	static Level l;
	Pt curs = new Pt(0, 0);
	Pt lastCurs = new Pt(0, 0);
	int hideCurs = 0;
	int nextLvlTime = 0;
	Object infoFor = null;
	String info = "";
	Img infoImg;
	boolean screened = false;
	static boolean setup = true;
	ArrayList<Creature> setupCreatures = new ArrayList<Creature>();
	int cooldown = 0;
	Weapon hoverWeapon;
	boolean paused = false;
	boolean mainMenu = true;
	boolean settings = false;
	ArrayList<ScreenMode> availableModes = new ArrayList<ScreenMode>();
	ScreenMode chosenMode = null;
	ScreenMode hoverMode = null;
	public static boolean lowGraphics = false;
	boolean showFPS = false;
	String menuHover = "FISHCAKES";
	boolean showCredits;
	Pair<String, Pair<String, String>> inputMappingToDo = null;
	boolean secondaryInputMapping;
	int newPatentTimer = 0;
	String patentText = "";
	int patentImg;
	String patentName;
	long patN = System.currentTimeMillis();
	boolean targetingBooped = false;
	static boolean musicStarted = false;
	boolean splash = true;
	boolean buyScreen = false;
	boolean exitAfterBuyScreen = false;
	boolean buyScreenAfterDefeat = false;
	ArrayList<BuyScreenArgument> buyArguments = new ArrayList<BuyScreenArgument>();
	int gamesPlayed = 0;
	int nothingInViewTicks = 0;
	boolean thingsToRight = false;
	boolean hasHadBarrelWarning = false;
	
	// Prefs stuff
	public static DifficultyLevel difficultyLevel = DifficultyLevel.EASY;
	public static final HashMap<String, String> keyBindings = new HashMap<String, String>();
	public static int soundVolume = 7;
	public static int musicVolume = 3;
	public static int plays = 0;
	
	public static final List<Pair<String, Pair<String, String>>> KEY_NAMES = l(
		p("Move left     ", p("A",     "LEFT")),
		p("Move right    ", p("D",     "RIGHT")),
		p("Move up / jump", p("W",     "UP")),
		p("Move down     ", p("S",     "DOWN")),
		p("Toggle hover  ", p("H",     (String) null)),
		p("Toggle flight ", p("F",     (String) null)),
		p("Prev weapon   ", p("Q",     (String) null)),
		p("Next weapon   ", p("E",     (String) null)),
		p("Autofire      ", p("SPACE", (String) null)),
		p("Pause         ", p("P",     (String) null)),
		p("Show FPS      ", p("I",     (String) null))
	);
	
	static {
		for (Pair<String, Pair<String, String>> n : KEY_NAMES) {
			keyBindings.put(n.b.a, n.b.a);
			if (n.b.b != null) {
				keyBindings.put(n.b.b, n.b.b);
			}
		}
		
		try {
			Preferences p = Preferences.userNodeForPackage(PatentBlaster.class);
			difficultyLevel = DifficultyLevel.values()[p.getInt("difficultyLevel", 0)];
			soundVolume = p.getInt("soundVolume", 7);
			musicVolume = p.getInt("musicVolume", 3);
			for (Map.Entry<String, String> kb : keyBindings.entrySet()) {
				//p.put("KEY_" + kb.getKey(), kb.getValue());
				kb.setValue(p.get("KEY_" + kb.getKey(), kb.getValue()));
			}
			plays = p.getInt("plays", 0) + 1;
			p.putInt("plays", plays);
			p.flush();
		} catch (Exception e) {
			e.printStackTrace(ERR_STREAM);
		}
	}
	
	public static String key(String normal) {
		return keyBindings.get(normal);
	}
	
	public static void savePrefs() {
		Preferences p = Preferences.userNodeForPackage(PatentBlaster.class);
		p.putInt("difficultyLevel", difficultyLevel.ordinal());
		p.putInt("soundVolume", soundVolume);
		p.putInt("musicVolume", musicVolume);
		for (Map.Entry<String, String> kb : keyBindings.entrySet()) {
			p.put("KEY_" + kb.getKey(), kb.getValue());
		}
		try {
			p.flush();
		} catch (Exception e) {
			e.printStackTrace(ERR_STREAM);
		}
	}
	
	public static int shotDivider() { return lowGraphics ? 4 : 1; }
	
	static String introTxt = "Patent Blaster! Click to proceed.";
	
	static {
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(PatentBlaster.class.getResourceAsStream("intro.txt"), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String l = null;
			while ((l = r.readLine()) != null) {
				sb.append(l).append("\n");
			}
			introTxt = sb.toString();
		} catch (Exception e) { e.printStackTrace(ERR_STREAM); }
	}
	private int tick;
	
	void hit(Input in) {
		//if (!lowGraphics || doRender) {
			h.hit(in);
		//}
	}
	
	@Override
	public void input(Input in) {
		Preload.preload(in);
		tick++;
		if (in.keyDown("ESCAPE") || in.keyDown("ESC") || in.keyDown("⎋")) {
			if (settings && cooldown == 0) {
				settings = false;
				cooldown += 10;
			} else if (buyScreen && cooldown == 0) {
				if (exitAfterBuyScreen) {
					in.quit();
				} else {
					buyScreen = false;
				}
			} else if (mainMenu && cooldown == 0) {
				if (showCredits) {
					showCredits = false;
					cooldown += 15;
				} else {
					if (DEMO && plays > 1) {
						buyScreen = true;
						buyArguments.clear();
						exitAfterBuyScreen = true;
						cooldown += 10;
					} else {
						in.quit();
					}
				}
			} else {
				if (cooldown == 0) {
					autoSave();
					mainMenu = true;
					cooldown += 15;
				}
			}
			return;
		}
		
		if (!System.getProperty("os.name").toLowerCase().matches(".*[iu]n[ui]x.*")) {
			chosenMode = new ScreenMode(1024, 768, true);
		}
		if (chosenMode == null) {
			if (availableModes.isEmpty()) {
				availableModes.add(new ScreenMode(1024, 768, false));
				for (ScreenMode sm : in.modes()) {
					if (sm.width == 1024 && sm.height == 768 && sm.fullscreen) {
						availableModes.add(sm);
						break;
					}
				}
			}
			if (availableModes.size() == 1) {
				chosenMode = availableModes.get(0);
				return;
			}
			hit(in);
			return;
		}
		if (!screened) {
			in.setMode(chosenMode);
			screened = true;
			in.setCursorVisible(false);
			cooldown += 10;
			if (musicVolume > 0) {
				in.playMusic(Level.MUSICS[0], musicVolume * 1.0 / 9, new MusicCallback() {
					@Override
					public void run(String music, double volume) {
						musicStarted = true;
					}
				}, null);
			} else {
				musicStarted = true;
			}
		}
		
		if (!curs.equals(lastCurs)) {
			hideCurs = 0;
		}
		lastCurs = curs;
		
		curs = in.cursor();
		if (cooldown > 0) { cooldown--; }
		hoverWeapon = null;
		
		if (cooldown == 0 && in.keyDown(key("I"))) {
			showFPS = !showFPS;
			cooldown += 10;
		}
		
		menuHover = "FISHCAKES";
		
		if (buyScreen) {
			hit(in);
			if (cooldown == 0 && in.clickButton() != 0) {
				if (exitAfterBuyScreen) {
					in.quit();
				} else {
					buyScreen = false;
				}
			}
			return;
		}
		
		if (splash) {
			if (Preload.allPreloaded() || cooldown == 0 && (in.clickButton() != 0 || in.lastKeyPressed() != null)) {
				splash = false;
				cooldown += 10;
			}
			return;
		}
		
		if (mainMenu) {
			if (inputMappingToDo != null) {
				String newMapping = in.lastKeyPressed();
				if (newMapping != null) {
					keyBindings.put(secondaryInputMapping ? inputMappingToDo.b.b : inputMappingToDo.b.a, newMapping);
					inputMappingToDo = null;
					secondaryInputMapping = false;
					savePrefs();
				}
			}
			if (Trigrams.TRIGRAMS != null) {
				if (newPatentTimer > 0) {
					newPatentTimer--;
				} else {
					Random r = new Random(patN += 32908);
					patentText = Trigrams.TRIGRAMS.generate(65, r);
					patentText = patentText.substring(0, patentText.length() - 1) + "...";
					patentImg = r.nextInt(NUM_IMAGES);
					patentName = "PAT " + (patN % 10000);
					newPatentTimer = FPS * 20;
				}
			}
			hit(in);
			return;
		}
		
		if (setup) {
			if (Names.namesLoaded() && setupCreatures.isEmpty()) {
				cooldown += 10;
				for (int i = 0; i < 4; i++) {
					Creature c = Creature.make(System.currentTimeMillis() + i * 32980, difficultyLevel.playerLevel, NUM_IMAGES, false, true, false);
					setupCreatures.add(c);
				}
			}
			hit(in);
			return;
		}
		
		if (!l.shopItems.isEmpty()) {
			info = "";
			hit(in);
			if (cooldown == 0 && hoverWeapon != null && l.player.weapons.size() > 1 && (in.keyDown("DELETE") || in.keyDown("BACKSPACE") || in.keyDown("BACK"))) {
				l.player.weapons.remove(hoverWeapon);
				cooldown += 10;
				if (hoverWeapon == l.player.weapon) {
					l.player.weapon = l.player.weapons.get(0);
				}
			}
			return;
		}
		
		if (cooldown == 0 && in.keyDown(key("P"))) {
			paused = !paused;
			cooldown += 20;
		}
		
		if (paused) {
			info = "";
			hit(in);
			return;
		}
		
		if (in.keyPressed(key("SPACE"))) {
			targetingBooped = false;
		}
		
		if (l.lost()) {
			nextLvlTime++;
			if (nextLvlTime >= 140) {
				l = null;
				autoSave();
				setup = true;
				nextLvlTime = 0;
				if (buyScreenAfterDefeat && gamesPlayed++ % 8 == 0) {
					buyArguments.clear();
					buyScreen = true;
					exitAfterBuyScreen = false;
				}
				cooldown += difficultyLevel == DifficultyLevel.EASY ? 100 : 30;
				return;
			}
		} else if (l.won()) {
			if (l.power == DEMO_LEVELS && DEMO) {
				l.player.hp = -1;
				l.player.explodes = true;
				l.player.lastShooter = null;
				l.player.explode(l);
				nextLvlTime = 0;
				l.texts.add(new FloatingText("KILLED BY THE DEMO LIMIT", l.player.x + l.player.w / 2, l.player.y));
				buyScreenAfterDefeat = true;
				return;
			}
			nextLvlTime++;
			if (nextLvlTime >= 140) {
				l.player.newThing = null;
				l = new Level(System.currentTimeMillis(), l.power + 1, l.player);
				l.player.heal();
				nextLvlTime = 0;
				int attempt = 0;
				Weapon w = null;
				cooldown += difficultyLevel == DifficultyLevel.EASY && l.power < 3 && gamesPlayed < 2 ? 100 : 30;
				do {
					w = Weapon.make(System.currentTimeMillis() + attempt++ * 1234, l.power + difficultyLevel.shopBonus, true);
				} while (attempt < 50 && !l.player.isUseful(w));
				l.shopItems.add(w);
				EnumSet<Item.Type> takenTypes = EnumSet.noneOf(Item.Type.class);
				for (int i = 0; i < 3; i++) {
					Item it  = Item.make(System.currentTimeMillis() + i * 90238, l.power + difficultyLevel.shopBonus, l.player, takenTypes);
					takenTypes.add(it.type);
					l.shopItems.add(it);
				}
				autoSave();
			}
		}
		
		if (l.player.frozen == 0 && l.player.hp > 0) {
			if (l.player.weapon.reloadLeft == 0 && l.shotsFired > 0) {
				l.ticksWhiteRectShown++;
			}
			
			nothingInViewTicks++;
			thingsToRight = false;
			for (Creature c : l.monsters) {
				/*if (c.x - l.player.x - l.player.w / 2 < 500 || c.x + c.w - l.player.x - l.player.w / 2 > -500) {
					nothingInViewTicks = 0;
				}*/
				if (Math.abs(c.x + c.w / 2 - l.player.x - l.player.w / 2) < 500 - c.w / 2) {
					nothingInViewTicks = 0;
				}
				if (c.x > l.player.x) {
					thingsToRight = true;
				}
			}
			for (Goodie it : l.goodies) {
				/*if (it.x - l.player.x - l.player.w / 2 < 500 || it.x + it.w - l.player.x - l.player.w / 2 > -500) {
					nothingInViewTicks = 0;
				}*/
				if (Math.abs(it.x + it.w / 2 - l.player.x - l.player.w / 2) < 500 - it.w / 2) {
					nothingInViewTicks = 0;
				}
				if (it.x > l.player.x) {
					thingsToRight = true;
				}
			}
			
			// Stealing means dropping.
			if (l.player.playerMoveModeSelection == MoveMode.HOVER && !l.player.canHover()) {
				l.player.playerMoveModeSelection = MoveMode.SLIDE;
			}
			if (l.player.playerMoveModeSelection == MoveMode.FLY && !l.player.canFly()) {
				l.player.playerMoveModeSelection = MoveMode.SLIDE;
			}
			// Hover on
			if (cooldown == 0 && l.player.playerMoveModeSelection != MoveMode.HOVER && l.player.canHover() && in.keyDown(key("H"))) {
				cooldown += 10;
				l.player.playerMoveModeSelection = MoveMode.HOVER;
				l.player.hovered = true;
			}
			// Hover off
			if (cooldown == 0 && l.player.playerMoveModeSelection == MoveMode.HOVER && in.keyDown(key("H"))) {
				cooldown += 10;
				l.player.playerMoveModeSelection = MoveMode.SLIDE;
			}
			// Flight on
			if (cooldown == 0 && l.player.playerMoveModeSelection != MoveMode.FLY && l.player.canFly() && in.keyDown(key("F"))) {
				cooldown += 10;
				l.player.playerMoveModeSelection = MoveMode.FLY;
				l.player.flown = true;
			}
			// Flight off
			if (cooldown == 0 && l.player.playerMoveModeSelection == MoveMode.FLY && in.keyDown(key("F"))) {
				cooldown += 10;
				l.player.playerMoveModeSelection = MoveMode.SLIDE;
			}
			if (l.player.knockedBack == 0) {
				double oldDx = l.player.dx;
				double oldDy = l.player.dy;
				if (l.player.realMoveMode() == MoveMode.FLY) {
					l.player.dx = 0;
					l.player.dy = 0;
					if ((in.keyDown(key("UP")) || in.keyDown(key("W")))) {
						l.player.dy = -l.player.totalSpeed();
						l.movedUp = true;
					}
					if ((in.keyDown(key(("DOWN"))) || in.keyDown(key("S")))) {
						l.player.dy = l.player.totalSpeed();
					}
				} else if (l.player.realMoveMode() == MoveMode.HOVER) {
					l.player.hoverPowerOff = false;
					if (l.player.ticksSinceBottom < Creature.AIR_STEERING && (in.keyDown(key("UP")) || in.keyDown(key("W")))) {
						l.player.dy = -l.player.totalSpeed();
						l.movedUp = true;
					}
					if ((in.keyDown(key(("DOWN"))) || in.keyDown(key("S")))) {
						l.player.hoverPowerOff = true;
					}
				} else {
					if (l.player.ticksSinceBottom < Creature.AIR_STEERING && (in.keyDown(key("UP")) || in.keyDown(key("W")))) {
						double spd = l.player.ticksSinceSide < Creature.AIR_STEERING ? Math.min(3.0, l.player.totalSpeed() * 1.5) : l.player.totalSpeed() + Creature.HOP_BONUS;
						l.player.jump();
						l.player.dy = -spd;
						l.movedUp = true;
					}
				}
				if (l.player.ticksSinceBottom == 1 && l.player.slipperiness == 0) {
					l.player.dx = 0;
				}
				if (l.player.ticksSinceBottom < Creature.AIR_STEERING && (l.player.slipperiness == 0 || Math.abs(l.player.dx) < 0.5) && (in.keyDown(key("LEFT")) || in.keyDown(key("A")))) {
					l.player.dx = -(l.player.ticksSinceSide < Creature.AIR_STEERING ? Math.min(2.5, l.player.totalSpeed()) : l.player.totalSpeed());
					l.player.flipped = false;
					l.movedLeft = true;
				}
				if (l.player.ticksSinceBottom < Creature.AIR_STEERING && (l.player.slipperiness == 0 || Math.abs(l.player.dx) < 0.5) && (in.keyDown(key("RIGHT")) || in.keyDown(key("D")))) {
					l.player.dx = l.player.ticksSinceSide < Creature.AIR_STEERING ? Math.min(2.5, l.player.totalSpeed()) : l.player.totalSpeed();
					l.player.flipped = true;
					l.movedRight = true;
				}
				
				if (l.player.realMoveMode() == MoveMode.FLY) {
					l.player.dx = oldDx * 0.96 + l.player.dx * 0.04;
					l.player.dy = oldDy * 0.96 + l.player.dy * 0.04;
					double speedLimit = l.player.totalSpeed();
					/*double speedSq = l.player.dx * l.player.dx + l.player.dy * l.player.dy;
					if (speedSq > 0 && speedSq > speedLimit * speedLimit) {
						double total = Math.abs(l.player.dx) + Math.abs(l.player.dx) + 1;
						l.player.dx = l.player.dx * speedLimit / total;
						l.player.dy = l.player.dy * speedLimit / total;
					}*/
					if (Math.abs(l.player.dx) > speedLimit) {
						l.player.dx = l.player.dx / Math.abs(l.player.dx) * speedLimit;
					}
					if (Math.abs(l.player.dy) > speedLimit) {
						l.player.dy = l.player.dy / Math.abs(l.player.dy) * speedLimit;
					}
				}
			}
			if (in.click() == null) { l.releasedSinceShot = true; }
			if (in.click() != null || l.player.weapon.sword) {
				if (l.player.weapon.reloadLeft == 0) {
					l.player.shoot(in.cursor().x - scrollX, in.cursor().y - scrollY, l);
					l.shotsFired++;
					l.releasedSinceShot = false;
				} else if (l.player.lastShot != null && !l.player.lastShot.killMe && l.player.lastShot.weapon.grenade && l.releasedSinceShot && in.clickButton() != 1) {
					l.player.lastShot.explode(l);
					l.player.lastShot.killMe = true;
					int index = l.shots.indexOf(l.player.lastShot);
					if (index != -1) {
						l.shots.set(index, null);
					}
				} else if (l.player.weapon.clickEmptyTimer == 0 && l.player.weapon.reloadLeft < l.player.weapon.reload - 10 && l.player.weapon.reloadLeft > 30) {
					in.play("empty", 1.0, 1.0, 0, 0);
					l.player.weapon.clickEmptyTimer = Math.min(FPS, l.player.weapon.reload);
				}
			} else if (in.keyDown(key("SPACE"))) {
				if (l.player.weapon.reloadLeft == 0) {
					if (difficultyLevel.ordinal() < DifficultyLevel.HARD.ordinal()) {
						l.player.weapon.reduceInaccuracy = true;
					}
					boolean barrelWarning = false;
					for (Barrel b : l.barrels) {
						if (l.player.gunX() >= b.x && l.player.gunX() <= b.x + b.w && l.player.gunY() >= b.y && l.player.gunY() <= b.y + b.h) {
							barrelWarning = true;
						}
					}
					if (barrelWarning && !hasHadBarrelWarning) {
						l.texts.add(new FloatingText("DON'T SHOOT THE BARREL!", l.player.x + l.player.w / 2, l.player.y));
					}
					hasHadBarrelWarning = barrelWarning;
					if (!barrelWarning) {
						hideCurs++;
						Creature targ = null;
						Creature nearest = null;
						double bestDsq = 100000 * 100000;
						for (Creature c : l.monsters) {
							double dx = (l.player.gunX() - c.x - c.w / 2);
							double dy = (l.player.gunY() - c.y - c.h / 2);
							double dsq = dx * dx + dy * dy;
							if (dsq < bestDsq) {
								nearest = c;
								bestDsq = dsq;
								if (dsq < l.player.weapon.range() * l.player.weapon.range()) {
									targ = c;
								}
							}
						}
						if (targ != null) {
							double tx = targ.x + targ.w / 2;
							double ty = targ.y + targ.h / 2;
							l.player.shoot(tx, ty, l);
							l.shotsFired++;
						} else if (nearest != null) {
							if (!targetingBooped) {
								in.play("boop", 1.0, 0.5, 0, 0);
								if (!l.player.noTargetInRangeWarning) {
									l.texts.add(new FloatingText("NO TARGET IN RANGE", l.player.x + l.player.w / 2, l.player.y));
									l.player.noTargetInRangeWarning = true;
								}
								targetingBooped = true;
							}
						}
					}
				} else if (l.player.weapon.clickEmptyTimer == 0 && l.player.weapon.reloadLeft < l.player.weapon.reload - 10 && l.player.weapon.reloadLeft > 30) {
					in.play("empty", 1.0, 1.0, 0, 0);
					l.player.weapon.clickEmptyTimer = Math.min(FPS, l.player.weapon.reload);
				}
			}
		}
		for (int i = 1; i <= 9; i++) {
			if (in.keyDown("" + i) && l.player.weapons.size() >= i) {
				l.player.weapon = l.player.weapons.get(i - 1);
				l.player.changedGun = true;
			}
		}
		if (cooldown == 0) {
			if (in.keyDown(key("Q"))) {
				int newIndex = (l.player.weapons.indexOf(l.player.weapon) - 1 + l.player.weapons.size()) % l.player.weapons.size();
				l.player.weapon = l.player.weapons.get(newIndex);
				cooldown += 15;
				l.player.changedGun = true;
			}
			if (in.keyDown(key("E"))) {
				int newIndex = (l.player.weapons.indexOf(l.player.weapon) + 1) % l.player.weapons.size();
				l.player.weapon = l.player.weapons.get(newIndex);
				cooldown += 15;
				l.player.changedGun = true;
			}
		}
		if (curs != null) {
			l.player.targetX = in.cursor().x - scrollX;
			l.player.targetY = in.cursor().y - scrollY;
		}
		l.tick(in);
		ScreenMode sm = in.mode();
		scrollX = sm.width / 2 - l.player.x - l.player.w / 2;
		scrollY = sm.height * 2 / 3 - l.player.y - l.player.h / 2;
		for (Goodie g : l.goodiesBeingTaken) {
			goodiePos(g, sm);
		}
		info = "";
		hit(in);
		if (info.equals("") && l.player.newThingTimer < 6 * FPS && l.player.newThing != null) {
			infoFor = l.player.newThing;
			if (l.player.newThing instanceof Item) {
				info = ((Item) l.player.newThing).desc(Clr.WHITE, true, true, l.player);
				infoImg = ((Item) l.player.newThing).largeImg;
			} else {
				info = l.player.newThing.desc(Clr.WHITE);
				infoImg = ((Weapon) l.player.newThing).largeImg;
			}
		} else {
			l.player.newThing = null;
		}
	}
	
	boolean doRender = false;

	String hl(String prefix, String s, boolean enabled) {
		return ((prefix + s).equals(menuHover) ? "[RED]" : (enabled ? "[333333]" : "[666666]")) + s + "[]";
	}
	
	void menuItem(final String s, boolean enabled, StringBuilder sb, HashMap<String, Hook> hs, final Hook hk) {
		menuItem("", s, enabled, sb, hs, hk);
	}
		
	
	void menuItem(final String prefix, final String s, boolean enabled, StringBuilder sb, HashMap<String, Hook> hs, final Hook hk) {
		hs.put(s, new Hook(hk.types[0], Hook.Type.HOVER) {
			@Override
			public void run(Input in, Pt p, Hook.Type type) {
				if (hk.ofType(type)) {
					hk.run(in, p, type);
				}
				if (type == Hook.Type.HOVER) {
					menuHover = prefix + s;
				}
			}
		});
		sb.append(hl(prefix, s, enabled));
	}
	
	@Override
	public void render(Frame f) {
		//if (lowGraphics && (doRender = !doRender)) { return; } // Go down to 30 FPS for low gfx.
		Draw d = new Draw(f);
		ScreenMode sm = f.mode();
		d.rect(Clr.DARK_GREY, 0, 0, sm.width, sm.height);
		String textBGTint = !lowGraphics ? "[bg=00000099]" : "[bg=222222]";
		
		if (chosenMode == null) {
			d.text("Choose a screen mode.", FOUNT, 100, 100);
			int y = 100;
			for (final ScreenMode m : availableModes) {
				y += 40;
				d.text((m == hoverMode ? "[bg=333333]" : "") + m.toString(), FOUNT, 120, y);
				Rect r = d.textSize(m.toString(), FOUNT, 120, y);
				d.hook(r.x, r.y, r.width, r.height, new Hook(Hook.Type.MOUSE_1) {
					@Override
					public void run(Input in, Pt p, Hook.Type type) {
						chosenMode = m;
					}
				});
				d.hook(r.x, r.y, r.width, r.height, new Hook(Hook.Type.HOVER) {
					@Override
					public void run(Input in, Pt p, Hook.Type type) {
						hoverMode = m;
					}
				});
			}
			h = d.getHooks();
			return;
		}
		
		if (buyScreen) {
			if (buyArguments.isEmpty()) {
				buyArguments.addAll(Arrays.asList(BuyScreenArgument.values()));
				Collections.shuffle(buyArguments);
			}
			if (lowGraphics) {
				d.rect(PAPER, 0, 0, sm.width, sm.height);
			} else {
				for (int y = 0; y < sm.width; y += 600) {
					for (int x = 0; x < sm.height; x += 600) {
						d.blit("paper.jpg", x, y);
					}
				}
			}
			
			int spacing = 40;
			for (int i = 0; i < 3; i++) {
				BuyScreenArgument arg = buyArguments.get(i);
				int x = sm.width / 2 * (i % 2) + spacing;
				int y = (sm.height / 2 - 30) * (i / 2) + spacing;
				d.text("[BLACK]" + arg.text, FOUNT, x, y);
				d.blit(arg.name(), x, y + FOUNT.lineHeight + 5);
			}
			
			StringBuilder menu = new StringBuilder();
			HashMap<String, Hook> hoox = new HashMap<String, Hook>();
			menu.append("[bg=ff5555]");
			menuItem("buy", "  BUY THE FULL VERSION  ", true, menu, hoox, new Hook(Hook.Type.MOUSE_1) {
				@Override
				public void run(Input in, Pt p, Type type) {
					in.setMode(new ScreenMode(1024, 768, false));
					try {
						Desktop.getDesktop().browse(new URI("http://www.patent-blaster.com/buy/"));
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Oops. Looks like we can't get a web browser open. But if you go to http://www.patent-blaster.com/buy/ , you'll be able to get the full version!");
					}
					if (exitAfterBuyScreen) {
						in.quit();
					} else {
						buyScreen = false;
					}
				}
			});
			d.text(menu.toString(), FOUNT, sm.width / 2 + spacing, sm.height / 2 + spacing, hoox);
		} else if (splash) {
			d.blit("splash.jpg", 0, 0);
			d.text("[BLACK]" + Preload.preloadStatus(), FOUNT, 220, 620);
		} else if (settings) {
			if (lowGraphics) {
				d.rect(PAPER, 0, 0, sm.width, sm.height);
			} else {
				for (int y = 0; y < sm.width; y += 600) {
					for (int x = 0; x < sm.height; x += 600) {
						d.blit("paper.jpg", x, y);
					}
				}
			}
			
			int spacing = 12;
			
			Rect titleR = d.textSize("SETTINGS", FOUNT, spacing, spacing);
			d.text("[BLACK]SETTINGS", FOUNT, spacing, spacing);
			d.rect(Clr.BLACK, 0, spacing + 18, sm.width * 3 / 4, 2);
			
			int y = (int) (titleR.y + titleR.height + spacing * 2);

			d.rect(Clr.BLACK, sm.width / 2, spacing + 18, 2, sm.height);
			if (newPatentTimer > FPS * 19) {
				int speed = FPS / (newPatentTimer - FPS * 19) + 1;
				Random r = new Random(patN += (tick % speed == 0 ? 1 : 0));
				d.text("[BLACK]PAT " + (Math.abs(r.nextInt()) % 10000), FOUNT, sm.width / 2 + spacing * 2, y);
			} else {
				if (patentName != null) {
					d.text("[BLACK]" + patentName, FOUNT, sm.width / 2 + spacing * 2, y);
					d.blit("drawings/" + IMG_NAMES[patentImg] + "_drawing_large", PAPER, sm.width / 2 + spacing * 2, y + 40);
					d.text("[BLACK]" + patentText, FOUNT, sm.width / 2 + spacing * 2, y + 465, sm.width / 2 - spacing * 4);
					d.hook(sm.width / 2, 0, sm.width / 2, sm.height, new Hook(Type.MOUSE_1) {
						@Override
						public void run(Input in, Pt p, Type type) {
							if (cooldown == 0) {
								newPatentTimer = 0;
								cooldown += 15;
							}
						}
					});
				}
			}
			
			if (inputMappingToDo != null) {
				d.hook(0, 0, sm.width, sm.height, new Hook(Hook.Type.MOUSE_1) {
					@Override
					public void run(Input in, Pt p, Type type) {
						if (cooldown > 0) { return; }
						inputMappingToDo = null;
					}
				});
			}
			StringBuilder menu = new StringBuilder();
			menu.append("[default=BLACK][BLACK]");
			HashMap<String, Hook> hoox = new HashMap<String, Hook>();
			for (final Pair<String, Pair<String, String>> kb : KEY_NAMES) {
				menu.append(kb.a).append("  ");
				menu.append(kb.equals(inputMappingToDo) && !secondaryInputMapping ? "[bg=550000]" : "");
				menuItem("key_", " " + key(kb.b.a) + " ", true, menu, hoox, new Hook(Hook.Type.MOUSE_1) {
					@Override
					public void run(Input in, Pt p, Type type) {
						if (cooldown > 0) { return; }
						inputMappingToDo = kb;
						secondaryInputMapping = false;
					}
				});
				menu.append("[bg=]");
				if (kb.b.b != null) {
					int gap = 3 - key(kb.b.a).length();
					for (int i = 0; i < gap; i++) {
						menu.append(" ");
					}
					menu.append(kb.equals(inputMappingToDo) && secondaryInputMapping ? "[bg=550000]" : "");
					menuItem("key2_", " " + key(kb.b.b) + " ", true, menu, hoox, new Hook(Hook.Type.MOUSE_1) {
						@Override
						public void run(Input in, Pt p, Type type) {
							if (cooldown > 0) { return; }
							inputMappingToDo = kb;
							secondaryInputMapping = true;
						}
					});
					menu.append("[bg=]");
				}
				menu.append("\n");
			}

			d.text(menu.toString(), FOUNT, spacing, y, hoox);
			Rect menuR = d.textSize(menu.toString(), FOUNT, spacing, y);
			y += menuR.height + spacing;
			menu = new StringBuilder();
			menu.append("[default=BLACK][BLACK]");
			hoox = new HashMap<String, Hook>();
			menu.append("Sound  ");
			for (int i = 0; i < 10; i++) {
				final int ii = i;
				menuItem("sound", " " + i, soundVolume == i, menu, hoox, new Hook(Hook.Type.MOUSE_1) {
					@Override
					public void run(Input in, Pt p, Type type) {
						if (ii > 0 && ii != soundVolume) {
							in.play("squelch", 1.0, ii * 1.0 / 9, 0, 0);
						}
						soundVolume = ii;
						savePrefs();
					}
				});
			}

			d.text(menu.toString(), FOUNT, spacing, y, hoox);
			menuR = d.textSize(menu.toString(), FOUNT, spacing, y);
			y += menuR.height + spacing;
			menu = new StringBuilder();
			menu.append("[default=BLACK][BLACK]");
			hoox = new HashMap<String, Hook>();
			menu.append("Music  ");
			for (int i = 0; i < 10; i++) {
				final int ii = i;
				menuItem("music", " " + i, musicVolume == i, menu, hoox, new Hook(Hook.Type.MOUSE_1) {
					@Override
					public void run(Input in, Pt p, Type type) {
						in.stopMusic();
						if (ii != 0/* && ii != musicVolume*/) {
							in.playMusic(Level.MUSICS[0], ii * 1.0 / 9, null, null);
						}
						musicVolume = ii;
						savePrefs();
					}
				});
			}
			menu.append("\n\n");
			menuItem("screen", "Switch to " + (f.mode().fullscreen ? "windowed" : "fullscreen"), true, menu, hoox, new Hook(Hook.Type.MOUSE_1) {
				@Override
				public void run(Input in, Pt p, Type type) {
					if (cooldown == 0) {
						cooldown += 10;
						ScreenMode sm = in.mode();
						if (sm.fullscreen) {
							in.setMode(new ScreenMode(1024, 768, false));
						} else {
							in.setMode(new ScreenMode(1024, 768, true));
						}
					}
				}
			});

			d.text(menu.toString(), FOUNT, spacing, y, hoox);
			
			Rect backR = d.textSize("(Back to Menu)", FOUNT, spacing, spacing);
			d.text((menuHover.equals("BACKTOMAIN") ? "[RED]" : "[GREY]") + "(Back to Menu)", FOUNT, sm.width - spacing - backR.width, spacing);
			d.hook(sm.width - spacing - backR.width, spacing, backR.width, backR.height, new Hook(Hook.Type.MOUSE_1, Hook.Type.HOVER) {
				@Override
				public void run(Input in, Pt p, Hook.Type type) {
					if (type == Hook.Type.HOVER) {
						menuHover = "BACKTOMAIN";
					} else {
						settings = false;
						cooldown += 10;
					}
				}
			});
		} else if (mainMenu) {
			if (lowGraphics) {
				d.rect(PAPER, 0, 0, sm.width, sm.height);
			} else {
				for (int y = 0; y < sm.width; y += 600) {
					for (int x = 0; x < sm.height; x += 600) {
						d.blit("paper.jpg", x, y);
					}
				}
			}
			
			int spacing = 12;
			
			Rect titleR = d.textSize("PATENT BLASTER" + (DEMO ? " DEMO" : ""), FOUNT, spacing, spacing);
			d.text("[BLACK]PATENT BLASTER" + (DEMO ? " DEMO" : ""), FOUNT, spacing, spacing);
			d.rect(Clr.BLACK, 0, spacing + 18, sm.width * 3 / 4, 2);
			
			int y = (int) (titleR.y + titleR.height + spacing * 2);
			
			if (showCredits) {
				d.text("[BLACK]" + introTxt, FOUNT, spacing, y, sm.width - spacing * 2);
				d.hook(0, 0, sm.width, sm.height, new Hook(Type.MOUSE_1) {
					@Override
					public void run(Input in, Pt p, Type type) {
						if (cooldown == 0) { showCredits = false; cooldown += 10; }
					}
				});
			} else {
				d.rect(Clr.BLACK, sm.width / 2, spacing + 18, 2, sm.height);
				if (newPatentTimer > FPS * 19) {
					int speed = FPS / (newPatentTimer - FPS * 19) + 1;
					Random r = new Random(patN += (tick % speed == 0 ? 1 : 0));
					d.text("[BLACK]PAT " + (Math.abs(r.nextInt()) % 10000), FOUNT, sm.width / 2 + spacing * 2, y);
				} else {
					if (patentName != null) {
						d.text("[BLACK]" + patentName, FOUNT, sm.width / 2 + spacing * 2, y);
						d.blit("drawings/" + IMG_NAMES[patentImg] + "_drawing_large", PAPER, sm.width / 2 + spacing * 2, y + 40);
						d.text("[BLACK]" + patentText, FOUNT, sm.width / 2 + spacing * 2, y + 465, sm.width / 2 - spacing * 4);
						d.hook(sm.width / 2, 0, sm.width / 2, sm.height, new Hook(Type.MOUSE_1) {
							@Override
							public void run(Input in, Pt p, Type type) {
								if (cooldown == 0) {
									newPatentTimer = 0;
									cooldown += 15;
								}
							}
						});
					}
				}
				
				
				// Play game                  Left        A <-
				//                            Right       D ->
				// Easy Medium Hard Brutal    Up          W ^
				//                            Down        S v
				// Credits                    Prev Weapon Q
				//                            Next Weapon E
				// Quit                       Music       <--->
				//                            Sounds      <--->

				if (inputMappingToDo != null) {
					d.hook(0, 0, sm.width, sm.height, new Hook(Hook.Type.MOUSE_1) {
						@Override
						public void run(Input in, Pt p, Type type) {
							inputMappingToDo = null;
						}
					});
				}
				StringBuilder menu = new StringBuilder();
				menu.append("[default=BLACK][BLACK]");
				HashMap<String, Hook> hoox = new HashMap<String, Hook>();
				boolean hasContinue = l != null && !l.lost();
				if (hasContinue) {
					menuItem("continue", "CONTINUE", true, menu, hoox, new Hook(Hook.Type.MOUSE_1) {
						@Override
						public void run(Input in, Pt p, Hook.Type type) {
							if (cooldown > 0) { return; }
							setup = false;
							mainMenu = false;
							cooldown += 10;
						}
					});
					menu.append("\n\n");
				}
				if (!hasContinue) { menu.append("[bg=ff5555]"); }
				menuItem("play", hasContinue ? "PLAY NEW" : "PLAY", true, menu, hoox, new Hook(Hook.Type.MOUSE_1) {
					@Override
					public void run(Input in, Pt p, Hook.Type type) {
						if (cooldown > 0) { return; }
						mainMenu = false;
						setup = true;
						cooldown += difficultyLevel == DifficultyLevel.EASY ? 100 : 30;
					}
				});
				menu.append("[bg=]");
				menu.append("\n\n");
				menuItem("settings", "SETTINGS", true, menu, hoox, new Hook(Hook.Type.MOUSE_1) {
					@Override
					public void run(Input in, Pt p, Hook.Type type) {
						if (cooldown > 0) { return; }
						settings = true;
						cooldown += 10;
					}
				});
				menu.append("\n\n");
				menuItem("credits", "CREDITS", true, menu, hoox, new Hook(Hook.Type.MOUSE_1) {
					@Override
					public void run(Input in, Pt p, Hook.Type type) {
						if (cooldown > 0) { return; }
						showCredits = true;
						cooldown += 10;
					}
				});
				menu.append("\n\n");
				menuItem("quit", "QUIT", true, menu, hoox, new Hook(Hook.Type.MOUSE_1) {
					@Override
					public void run(Input in, Pt p, Hook.Type type) {
						if (cooldown > 0) { return; }
						in.quit();
					}
				});
				menu.append("\n\n");
				if (DEMO) {
					d.rect(Clr.BLACK, spacing + "EASY MEDIUM HARD ".length() * FOUNT.displayWidth, y + FOUNT.lineHeight * (hasContinue ? 10 : 8) + 8, "BRUTAL".length() * FOUNT.displayWidth, 2);
					d.rect(Clr.BLACK, spacing + "EASY MEDIUM HARD BRUTAL ".length() * FOUNT.displayWidth, y + FOUNT.lineHeight * (hasContinue ? 10 : 8)  + 8, "IMPOSSIBLE".length() * FOUNT.displayWidth, 2);
				}
				for (final DifficultyLevel dl : DifficultyLevel.values()) {
					menuItem("diff", dl.name(), dl == difficultyLevel, menu, hoox, new Hook(Hook.Type.MOUSE_1) {
						@Override
						public void run(Input in, Pt p, Hook.Type type) {
							if (!DEMO || dl.ordinal() < DifficultyLevel.BRUTAL.ordinal()) {
								if (difficultyLevel != dl) {
									setupCreatures.clear();
								}
								difficultyLevel = dl;
								savePrefs();
							} else {
								if (cooldown == 0) {
									cooldown += 10;
									in.play("boop", 1.0, 1.0, 0, 0);
								}
							}
						}
					});
					menu.append(" ");
				}
				
				menu.append("\n\n");
				
				d.text(menu.toString(), FOUNT, spacing, y, hoox);
				Rect menuR = d.textSize(menu.toString(), FOUNT, spacing, y);
				y += menuR.height + spacing;
				if (DEMO) {
					menu = new StringBuilder();
					menu.append("[bg=ff5555]");
					hoox = new HashMap<String, Hook>();
					menuItem("buy", "  BUY THE FULL VERSION  ", true, menu, hoox, new Hook(Hook.Type.MOUSE_1) {
						@Override
						public void run(Input in, Pt p, Type type) {
							in.setMode(new ScreenMode(1024, 768, false));
							try {
								Desktop.getDesktop().browse(new URI("http://www.patent-blaster.com/buy/"));
							} catch (Exception e) {
								JOptionPane.showMessageDialog(null, "Oops. Looks like we can't get a web browser open. But if you go to http://www.patent-blaster.com/buy/ , you'll be able to get the full version!");
							}
						}
					});
					d.text(menu.toString(), FOUNT, spacing, y + spacing * 3, hoox);
				}
			}
		} else if (setup) {
			int spacing = 24;

			if (lowGraphics) {
				d.rect(PAPER, 0, 0, sm.width, sm.height);
			} else {
				for (int y = 0; y < sm.width; y += 600) {
					for (int x = 0; x < sm.height; x += 600) {
						d.blit("paper.jpg", x, y);
					}
				}
			}
			if (setupCreatures.isEmpty()) {
				Pt infoP = d.textSize("[BLACK]LOADING RANDOM NAMES FROM WIKIPEDIA", FOUNT);
				String dots = "";
				for (int i = 0; i < (tick / 15) % 4; i++) {
					dots += ".";
				}
				d.text("[BLACK]LOADING RANDOM NAMES FROM WIKIPEDIA" + dots, FOUNT, sm.width / 2 - infoP.x / 2, sm.height / 2 - infoP.y / 2);
			} else {
				Rect titleR = d.textSize("SELECT YOUR CREATURE", FOUNT, spacing, spacing);
				d.text("[BLACK]SELECT YOUR CREATURE", FOUNT, spacing, spacing);
				d.rect(Clr.BLACK, 0, spacing + 18, sm.width * 3 / 4, 2);
				Rect pgR = d.textSize("Page 1", FOUNT, spacing, spacing);
				d.text("[BLACK]Page 1", FOUNT, sm.width - spacing - pgR.width, spacing);
				int yOffset = spacing * 2 + (int) titleR.height;
				int availableH = sm.height - yOffset;
				int xOffset = spacing * 2;
				int availableW = sm.width - spacing;
				int tileH = availableH / 2;
				int tileW = availableW / 2;
				for (int tileY = 0; tileY < 2; tileY++) {
					for (int tileX = 0; tileX < 2; tileX++) {
						final Creature c = setupCreatures.get(tileY * 2 + tileX);
						Rect tileR = new Rect(xOffset + tileX * tileW, yOffset + tileY * tileH, tileW, tileH);
						boolean hover = tileR.contains(curs);
						d.text("[BLACK]" + c.name().toUpperCase(), FOUNT, xOffset + tileX * tileW, yOffset + tileY * tileH);
						Clr t = c.tint;//!hover ? c.tint.mix(0.9, PAPER) : c.tint;
						d.blit("drawings/" + IMG_NAMES[c.imgIndex] + "_drawing", t, xOffset + tileX * tileW, yOffset + tileY * tileH + 35);
						d.text("[BLACK][default=BLACK]" + c.desc(Clr.BLACK, IMG_NUMS[c.imgIndex]), SMOUNT, xOffset + tileX * tileW, yOffset + tileY * tileH + 150, (int) (tileW - 10));
						/*d.hook(tileR.x, tileR.y, tileR.width, tileR.height, */
						button(d, "Select", xOffset + tileX * tileW + 100 + spacing, yOffset + tileY * tileH + 35 + 50 - 14, 0, new Hook(Hook.Type.MOUSE_1) {

							@Override
							public void run(Input in, Pt p, Hook.Type type) {
								if (cooldown != 0) { return; }
								l = new Level(System.currentTimeMillis() + 10981, 1, c);
								l.player.makePlayerAble();
								l.player.heal();
								l.player.weapon.reloadLeft = 10;
								l.player.weapon.clickEmptyTimer = 12;
								nextLvlTime = 0;
								setupCreatures.clear();
								setup = false;
							}
						});
					}
				}
				Rect backR = d.textSize("(Back to Menu)", FOUNT, spacing, spacing);
				d.text((menuHover.equals("BACKTOMAIN") ? "[RED]" : "[GREY]") + "(Back to Menu)", FOUNT, sm.width - spacing - pgR.width - spacing - backR.width, spacing);
				d.hook(sm.width - spacing - pgR.width - spacing - backR.width, spacing, backR.width, backR.height, new Hook(Hook.Type.MOUSE_1, Hook.Type.HOVER) {
					@Override
					public void run(Input in, Pt p, Hook.Type type) {
						if (type == Hook.Type.HOVER) {
							menuHover = "BACKTOMAIN";
						} else {
							setup = false;
							mainMenu = true;
							cooldown += 10;
						}
					}
				});
				
				// CYA
				Rect cyaR = d.textSize("[BLACK][bg=ff5555] Item names randomly chosen from Wikipedia. ", SMOUNT, 0, 0);
				d.text("[BLACK][bg=ff5555] Item names randomly chosen from Wikipedia. ", SMOUNT, sm.width - cyaR.width, sm.height - cyaR.height);
				
				if (cooldown > 20 && difficultyLevel.ordinal() < DifficultyLevel.MEDIUM.ordinal() && gamesPlayed < 2) {
					d.rect(new Clr(0, 0, 0, 63), 0, 0, sm.width, sm.height);
					Pt chooseR = d.textSize("CHOOSE YOUR CREATURE", GOUNT);
					d.rect(Clr.BLACK, 0, sm.height / 2 - chooseR.y / 2 - 10, sm.width, chooseR.y + 20);
					d.text("CHOOSE YOUR CREATURE", GOUNT, sm.width / 2 - chooseR.x / 2, sm.height / 2 - chooseR.y / 2 + 10);
				}
			}
		} else if (!l.shopItems.isEmpty()) {
			int spacing = 24;
			if (lowGraphics) {
				d.rect(PAPER, 0, 0, sm.width, sm.height);
			} else {
				for (int y = 0; y < sm.width; y += 600) {
					for (int x = 0; x < sm.height; x += 600) {
						d.blit("paper.jpg", x, y);
					}
				}
			}
			
			int topMargin = 60;
			int bottomMargin = 60;

			Rect titleR = d.textSize("SHOP: PICK ONE NEW ITEM", FOUNT, spacing, topMargin);
			d.text("[BLACK]SHOP: PICK ONE NEW ITEM", FOUNT, spacing, topMargin);
			d.rect(Clr.BLACK, 0, 18 + topMargin, sm.width * 3 / 4, 2);
			Rect pgR = d.textSize("Page " + l.power + 1, FOUNT, spacing, topMargin);
			d.text("[BLACK]Page " + l.power, FOUNT, sm.width - spacing - pgR.width, topMargin);
			int yOffset = spacing + (int) titleR.height + topMargin;
			int availableH = sm.height - yOffset - topMargin - bottomMargin;
			int xOffset = spacing * 2;
			int availableW = sm.width - spacing;
			int tileH = availableH / 2 + 80;
			int tileW = availableW / 2;
			for (int tileY = 0; tileY < 2; tileY++) {
				for (int tileX = 0; tileX < 2; tileX++) {
					final Object o = l.shopItems.get(tileY * 2 + tileX);
					String name = "";
					String desc = "";
					Img img = null;
					Clr tint = Clr.BLACK;
					if (o instanceof Weapon) {
						name = ((Weapon) o).name();
						desc = ((Weapon) o).desc(Clr.BLACK, false, false);
						img = ((Weapon) o).largeImg;
						tint = ((Weapon) o).tint;
					} else if (o instanceof Item) {
						name = ((Item) o).name();
						desc = ((Item) o).desc(Clr.BLACK, false, false, l.player);
						img = ((Item) o).largeImg;
						tint = ((Item) o).tint;
					} 
					Rect tileR = new Rect(xOffset + tileX * tileW, yOffset + tileY * tileH, tileW, tileH);
					boolean hover = tileR.contains(curs);
					d.text("[BLACK]" + name.toUpperCase(), FOUNT, xOffset + tileX * tileW, yOffset + tileY * tileH);
					Clr t = tint;//!hover ? tint.mix(0.9, PAPER) : tint;
					d.blit(img, t, xOffset + tileX * tileW, yOffset + tileY * tileH + 35);
					d.text("[BLACK][default=BLACK]" + desc, FOUNT, xOffset + tileX * tileW, yOffset + tileY * tileH + 205, (int) (tileW - 10));
					//d.hook(tileR.x, tileR.y, tileR.width, tileR.height,
					button(d, "Select", xOffset + tileX * tileW + 135 + spacing, yOffset + tileY * tileH + 35 + 75 - 14, 0, new Hook(Hook.Type.MOUSE_1) {

						@Override
						public void run(Input in, Pt p, Hook.Type type) {
							if (cooldown != 0) { return; }
							if (o instanceof Weapon) {
								l.player.weapon = (Weapon) o;
								l.player.weapons.add((Weapon) o);
							}
							if (o instanceof Item) {
								l.player.items.add((Item) o);
								l.player.canSeeStats = l.player.canSeeStats || ((Item) o).givesInfo;
							}
							l.player.weapon.reloadLeft = 10;
							l.player.weapon.clickEmptyTimer = 12;
							l.shopItems.clear();
						}
					});
				}
			}
			
			showEquipment(d, sm, FOUNT, textBGTint, false);
			
			// CYA
			Rect cyaR = d.textSize("[BLACK][bg=ff5555] Item names randomly chosen from Wikipedia. ", SMOUNT, 0, 0);
			d.text("[BLACK][bg=ff5555] Item names randomly chosen from Wikipedia. ", SMOUNT, sm.width - cyaR.width, 3);
			
			if (cooldown > 20 && difficultyLevel.ordinal() < DifficultyLevel.MEDIUM.ordinal() && l.power < 3 && gamesPlayed < 2) {
				d.rect(new Clr(0, 0, 0, 63), 0, 0, sm.width, sm.height);
				Pt chooseR = d.textSize("PICK A NEW ITEM", GOUNT);
				d.rect(Clr.BLACK, 0, sm.height / 2 - chooseR.y / 2 - 10, sm.width, chooseR.y + 20);
				d.text("PICK A NEW ITEM", GOUNT, sm.width / 2 - chooseR.x / 2, sm.height / 2 - chooseR.y / 2 + 10);
			}
		} else {
			// Background texture
			if (l.background != -1) {
				int winYIndex = 0;
				for (int y = 0; y < (Level.LVL_H) * Level.GRID_SIZE; y += l.backgroundH) {
					int winIndex = 0;
					for (int x = 0; x < (Level.LVL_W) * Level.GRID_SIZE; x += l.backgroundW) {
						if (winYIndex == WIN_Y_INDEX[l.background] && l.window[winIndex++]) {
							d.blit("landscape", x + (int) scrollX, y + (int) scrollY);
							d.blit("background_" + l.background + "_window", x + (int) scrollX, y + (int) scrollY);
						}
					}
					winYIndex++;
				}
				winYIndex = 0;
				for (int y = 0; y < (Level.LVL_H) * Level.GRID_SIZE; y += l.backgroundH) {
					int winIndex = 0;
					for (int x = 0; x < (Level.LVL_W) * Level.GRID_SIZE; x += l.backgroundW) {
						if (winYIndex == WIN_Y_INDEX[l.background] && l.window[winIndex++]) {
							// Nothing
						} else {
							d.blit("background_" + l.background, x + (int) scrollX, y + (int) scrollY);
						}
					}
					winYIndex++;
				}
			}
			
			for (int y = 0; y < l.grid.length; y++) {
				for (int x = 0; x < l.grid[0].length; x++) {
					if (l.grid[y][x] == 2) {
						d.rect(Clr.GREY, x * Level.GRID_SIZE + scrollX, y * Level.GRID_SIZE + scrollY, Level.GRID_SIZE, Level.GRID_SIZE);
					}
					if (l.grid[y][x] == 1) {
						d.rect(PAINTING_FRAME, x * Level.GRID_SIZE + scrollX + 5, y * Level.GRID_SIZE + scrollY + 5, Level.GRID_SIZE - 10, Level.GRID_SIZE - 10);
						d.rect(PAINTING_BG, x * Level.GRID_SIZE + scrollX + 10, y * Level.GRID_SIZE + scrollY + 10, Level.GRID_SIZE - 20, Level.GRID_SIZE - 20);
						d.blit(l.boss.img, l.boss.tint, x * Level.GRID_SIZE + scrollX + 20, y * Level.GRID_SIZE + scrollY + 20, Level.GRID_SIZE - 40, Level.GRID_SIZE - 40);
					}
				}
			}
			
			// Bracket this in
			// bottom
			d.rect(Clr.DARK_GREY, scrollX - 600, scrollY + Level.LVL_H * Level.GRID_SIZE, Level.LVL_W * Level.GRID_SIZE + 1200, 600);
			// right
			d.rect(Clr.DARK_GREY, scrollX + Level.LVL_W * Level.GRID_SIZE, scrollY - 600, 600, Level.LVL_H * Level.GRID_SIZE + 1200);
			
			for (Barrel b : l.barrels) {
				b.draw(d, l, scrollX, scrollY);
			}
			for (Creature c : l.monsters) {
				c.draw(d, l, scrollX, scrollY);
			}
			l.player.draw(d, l, scrollX, scrollY);
			for (Goodie g : l.goodies) {
				g.draw(d, l, scrollX, scrollY);
			}
			for (Shot s : l.shots) { if (s == null) { continue; }
				s.draw(d, l, scrollX, scrollY);
			}
			for (Creature c : l.monsters) {
				c.drawBars(d, l, scrollX, scrollY);
			}
			l.player.drawBars(d, l, scrollX, scrollY);
			for (FloatingText ft : l.texts) {
				ft.draw(d, l, scrollX, scrollY);
			}
			for (Goodie g : l.goodiesBeingTaken) {
				g.draw(d, l, 0, 0);
			}
			if (!lowGraphics && l.player.hp <= 0) {
				d.rect(DEAD, 0, 0, sm.width, sm.height);
			} else if (!lowGraphics && l.player.hp < l.player.totalMaxHP() / 8) {
				d.rect(DYING, 0, 0, sm.width, sm.height);
			}
			/*if (l.tick < 1000) {
				d.text(l.player.desc(), new Fount("LiberationMono18", 12, 12, 24), 20, 20);
			}*/
			
			if (l.player.hp > 0 && difficultyLevel.ordinal() < DifficultyLevel.HARD.ordinal()) {
				for (Goodie g : l.goodies) {
					if (g.age > FPS * 2) {
						Pt sz = d.textSize("Pick me up!", FOUNT);
						d.text(textBGTint + "Pick me up!", FOUNT, scrollX + g.x + g.w / 2 - sz.x / 2, scrollY + g.y - sz.y);
					}
				}
				
				if (l.power < 2) {
					for (Creature c : l.monsters) {
						if (c.ticksInView > FPS * 5) {
							Pt sz = d.textSize("Shoot me!", FOUNT);
							d.text(textBGTint + "Shoot me!", FOUNT, scrollX + c.x + c.w / 2 - sz.x / 2, scrollY + c.y - sz.y);
						}
					}
				}
			}
			
			showEquipment(d, sm, FOUNT, textBGTint, true);
			Pt ts = d.textSize("Level " + l.power, FOUNT);
			d.text("Level " + l.power, FOUNT, sm.width - ts.x - 10, 10);
			
			if (l.player.hp > 0 && nothingInViewTicks > FPS * 2 + (difficultyLevel.ordinal() * difficultyLevel.ordinal()) && (!l.monsters.isEmpty() || !l.goodies.isEmpty())) {
				d.blit("rightarrow", sm.width / 2 - 200, sm.height / 4, 0, 0, !thingsToRight);
			}
		}
		
		if (showFPS) {
			d.text(f.fps() + " FPS", FOUNT, sm.width - 100, 40);
		}
		
		if (!splash && !setup && !mainMenu && l != null && l.power == 1 && l.player.hp > 0 && difficultyLevel.ordinal() < DifficultyLevel.HARD.ordinal()) {
			if (!l.movedRight && !l.movedLeft) {
				d.text(textBGTint + key("D") + " or " + key("RIGHT") + " to move right", FOUNT, sm.width / 2 + 50, sm.height * 2 / 3 - FOUNT.lineHeight / 2);
			}
			if (!l.movedLeft && !l.movedRight) {
				Pt sz = d.textSize(key("A") + " or " + key("LEFT") + " to move left", FOUNT);
			d.text(textBGTint + key("A") + " or " + key("LEFT") + " to move left", FOUNT, sm.width / 2 - 50 - sz.x, sm.height * 2 / 3 - FOUNT.lineHeight / 2);
			}
			if (!l.movedUp) {
				Pt sz = d.textSize(key("W") + " or " + key("UP") + " to jump", FOUNT);
				d.text(textBGTint + key("W") + " or " + key("UP") + " to jump", FOUNT, sm.width / 2 - sz.x / 2, sm.height * 2 / 3 - FOUNT.lineHeight / 2 - 50);
			}
		} else if (!setup && !mainMenu && l != null && l.shopItems.isEmpty() && !l.player.hovered && l.player.ticksSinceGainingHover < FPS * 8 && l.player.canHover() && l.player.hp > 0 && difficultyLevel.ordinal() < DifficultyLevel.BRUTAL.ordinal()) {
			Pt sz = d.textSize(key("H") + " to toggle hover mode", FOUNT);
			d.text(textBGTint + key("H") + " to toggle hover mode", FOUNT, sm.width / 2 - sz.x / 2, sm.height * 2 / 3 - FOUNT.lineHeight / 2 - 50);
		} else if (!setup && !mainMenu && l != null && l.shopItems.isEmpty() && !l.player.flown && l.player.ticksSinceGainingFlight < FPS * 8 && l.player.canFly() && l.player.hp > 0 && difficultyLevel.ordinal() < DifficultyLevel.BRUTAL.ordinal()) {
			Pt sz = d.textSize(key("F") + " to toggle flight mode", FOUNT);
			d.text(textBGTint + key("F") + " to toggle flight mode", FOUNT, sm.width / 2 - sz.x / 2, sm.height * 2 / 3 - FOUNT.lineHeight / 2 - 50);
		}
		if (hideCurs < 3 || mainMenu || setup || splash || !l.shopItems.isEmpty()) {
			Clr recC = Clr.RED;
			boolean tooFar = false;
			if (!splash && !setup && !mainMenu && l != null && l.shopItems.isEmpty() && l.player.hp > 0) {
				double dx = curs.x - scrollX - l.player.gunX(), dy = curs.y - scrollY - l.player.gunY();
				double dist = Math.sqrt(dx * dx + dy * dy);
				if (l.player.weapon.reloadLeft == 0) {
					recC = RELOADING_CURSOR;
				}
				tooFar = dist > l.player.weapon.range();
				if (!l.player.weapon.swarm && (dx != 0 || dy != 0) && !tooFar) {
					double angle = Math.atan2(dy, dx);
					d.rect(recC, l.player.gunX() + scrollX + Math.cos(angle + l.player.weapon.jitter) * dist - 1, l.player.gunY() + scrollY + Math.sin(angle + l.player.weapon.jitter) * dist - 1, 2, 2);
					d.rect(recC, l.player.gunX() + scrollX + Math.cos(angle - l.player.weapon.jitter) * dist - 1, l.player.gunY() + scrollY + Math.sin(angle - l.player.weapon.jitter) * dist - 1, 2, 2);
				}
			}
			if (tooFar) {
				d.rect(recC, curs.x - 1, curs.y - 10, 2, 5);
				d.rect(recC, curs.x - 1, curs.y + 5, 2, 5);
				d.rect(recC, curs.x - 10, curs.y - 1, 5, 2);
				d.rect(recC, curs.x + 5, curs.y - 1, 5, 2);
			} else {
				d.rect(recC, curs.x - 1, curs.y - 10, 2, 20);
				d.rect(recC, curs.x - 10, curs.y - 1, 20, 2);
			}
			if (!splash && !setup && !mainMenu && l != null && l.power == 1 && l.movedRight && l.player.hp > 0 && difficultyLevel.ordinal() < DifficultyLevel.HARD.ordinal()) {
				if (l.shotsFired == 0) {
					Pt sz = d.textSize("Mouse to aim, click to shoot\nOr press " + key("SPACE") + " to auto-aim", FOUNT);
					d.text(((l.tick / 20 % 2 == 0) ? "[dddddd]" : "") + textBGTint + "Mouse to aim, click to shoot\nOr press " + key("SPACE") + " to auto-aim", FOUNT, Math.min(sm.width - sz.x, curs.x + 3), curs.y + 3);
				}
				if (l.shotsFired > 0 && l.ticksWhiteRectShown < FPS * 2 && l.player.hp > 0) {
					Pt sz = d.textSize("White reticle = weapon ready", FOUNT);
					d.text(((l.tick / 20 % 2 == 0) ? "[dddddd]" : "") + textBGTint + "White reticle = weapon ready", FOUNT, Math.min(sm.width - sz.x, curs.x + 3), curs.y + 3);
				}
			}
		}
		h = d.getHooks();
	}
	
	private void showEquipment(Draw d, ScreenMode sm, Fount fount, String textBGTint, boolean hilite) {
		int i = 0;
		d.blit(l.player.img, l.player.tint, 15 + i * 40, 15, 30, 30, new Hook(Hook.Type.HOVER) {
			@Override
			public void run(Input in, Pt p, Hook.Type type) {
				infoFor = l.player;
				info = l.player.desc(Clr.WHITE);
				infoImg = l.player.img;
			}
		});
		i = 2;
		for (final Weapon w : l.player.weapons) {
			if (w == l.player.weapon && hilite) {
				d.rect(Clr.WHITE, 10 + i * 40, 10, 40, 40);
			}
			Clr t = w.tint;
			if (w == l.player.newThing && hilite) {
				t = (l.tick / 20) % 2 == 0 ? Clr.WHITE : t;
			}
			d.blit(w.img, t, 15 + i * 40, 15, 30, 30, new Hook(Hook.Type.HOVER, Hook.Type.MOUSE_1) {
				@Override
				public void run(Input in, Pt p, Hook.Type type) {
					if (type == Hook.Type.MOUSE_1) {
						l.player.weapon = w;
					}
					hoverWeapon = w;
					infoFor = w;
					info = w.desc(Clr.WHITE) + (l.player.weapons.size() > 1 && !l.shopItems.isEmpty() ? "Hit delete to delete weapon from inventory." : "");
					infoImg = w.largeImg;
				}
			});
			if (i < 11 && l.player.weapons.size() > 1 && hilite) {
				d.text(textBGTint + (i - 1), FOUNT, 15 + i * 40, 15);
			}
			i++;
		}
		if (l.shopItems.isEmpty() && !l.player.changedGun && l.player.weapons.size() > 1 && l.player.weapons.size() < 5 && l.player.showingWeaponSwitchInfo++ < FPS * 10 && difficultyLevel.ordinal() < DifficultyLevel.HARD.ordinal()) {
			d.text(((l.tick / 20 % 2 == 0) ? "[dddddd]" : "") + textBGTint + "Press " + key("Q") + " and " + key("E") + " or the number keys to switch weapons.", FOUNT, 20 + i * 40, 20);
		}
		i = 0;
		int spacing = 40;
		if ((l.player.items.size() + 1) * 40 > sm.width) {
			spacing = Math.max(1, sm.width / (l.player.items.size()));
		}
		ArrayList<Item> sortedItems = new ArrayList<Item>(l.player.items);
		Collections.sort(sortedItems);
		for (final Item it : sortedItems) {
			Clr t = it.tint;
			if (it == l.player.newThing && hilite) {
				t = (l.tick / 20) % 2 == 0 ? Clr.WHITE : t;
			}
			d.blit(it.img, t, 15 + i * spacing, sm.height - 45, 30, 30, new Hook(Hook.Type.HOVER) {
				@Override
				public void run(Input in, Pt p, Hook.Type type) {
					infoFor = it;
					info = it.desc(Clr.WHITE, true, true, l.player);
					infoImg = it.largeImg;
				}
			});
			i++;
		}
		if (!info.equals("")) {
			double y = (infoFor instanceof Item) ? (sm.height - 50 - 160 - d.textSize(info, fount).y) : 50;
			d.blit(infoImg, infoFor instanceof Creature ? ((Creature) infoFor).tint : infoFor instanceof Weapon ? ((Weapon) infoFor).tint : ((Item) infoFor).tint, 10, y);
			d.text(textBGTint + info, fount, 10, y + 160);
		}
	}
	
	void goodiePos(Goodie g, ScreenMode sm) {
		if (g.timeSpentTaken == 1) {
			g.srcX = g.x + scrollX;
			g.srcY = g.y + scrollY;
		}
		if (g.weapon != null) {
			int i = 2;
			for (final Weapon w : l.player.weapons) {
				if (w == g.weapon) {
					g.targX = 15 + i * 40;
					g.targY = 15;
					break;
				}
				i++;
			}
		} else {
			int i = 0;
			int spacing = 40;
			if ((l.player.items.size() + 1) * 40 > sm.width) {
				spacing = Math.max(1, sm.width / (l.player.items.size()));
			}
			ArrayList<Item> sortedItems = new ArrayList<Item>(l.player.items);
			Collections.sort(sortedItems);
			for (final Item it : sortedItems) {
				if (g.item == it) {
					g.targX = 15 + i * spacing;
					g.targY = sm.height - 45;
					break;
				}
				i++;
			}
		}
		
		g.x = g.timeSpentTaken * g.targX / GOODIE_FETCH_TICKS + (GOODIE_FETCH_TICKS - g.timeSpentTaken) * g.srcX / GOODIE_FETCH_TICKS;
		g.y = g.timeSpentTaken * g.targY / GOODIE_FETCH_TICKS + (GOODIE_FETCH_TICKS - g.timeSpentTaken) * g.srcY / GOODIE_FETCH_TICKS;
	}
	
	static final Clr BUTTON_BORDER = new Clr(100, 100, 100, 63);
	static final Clr BUTTON_BG = new Clr(200, 200, 200, 63);
	static final Clr BUTTON_HIGHLIGHT = new Clr(255, 255, 255, 63);
	static final String BUTTON_TEXT = "[333333]";
	
	void button(Draw d, String text, int x, int y, int width, Hook h) {
		Pt size = buttonSize(d, text, width);
		d.rect(BUTTON_BORDER, x, y, size.x, size.y);
		d.rect(curs.x >= x && curs.x <= x + size.x && curs.y >= y && curs.y <= y + size.y
				? BUTTON_HIGHLIGHT : BUTTON_BG
				, x + 1, y + 1, size.x - 2, size.y - 2);
		d.text(BUTTON_TEXT + text, FOUNT, x + size.x / 2 - d.textSize(text, FOUNT).x / 2, y + 4);
		d.hook(x, y, size.x, size.y, h);
	}
	
	Pt buttonSize(Draw d, String text, int width) {
		Pt textSize = d.textSize(text, FOUNT);
		return new Pt(width == 0 ? textSize.x + 20 : Math.max(textSize.x + 20, width), textSize.y);
	}
	
	public static String round(double amt, int decimals) {
		if (decimals == 0) {
			return "" + ((int) Math.round(amt));
		}
		return "" + (((int) Math.round(amt * Math.pow(10, decimals))) * 1.0 / (Math.pow(10, decimals)));
	}
	
	public static String round(double amt) {
		return (new BigDecimal(amt, MathContext.UNLIMITED).round(new MathContext(2, RoundingMode.CEILING))).toPlainString();
	}
	
	public static void autoSave() {
		File f = new File("patentblaster_autosave");
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(l);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace(ERR_STREAM);
		}
	}
	
	public static void autoLoad() {
		File f = new File("patentblaster_autosave");
		if (!f.exists()) { return; }
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			l = (Level) ois.readObject();
			ois.close();
		} catch (Exception e) {
			// Ignore.
		}
		if (l != null) { setup = false; }
	}
}
