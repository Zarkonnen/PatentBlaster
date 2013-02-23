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
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;

public class PatentBlaster implements Game {
	public static final boolean DEMO = false;
	public static final int DEMO_LEVELS = 4;
	
	public static final int NUM_IMAGES = DEMO ? 3 : 5;
	public static final int NUM_VOICES = DEMO ? 3 : 14;
	public static final int FPS = 60;
	public static final String ALPHABET = " qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890-=+_!?<>,.;:\"'@£$%^&*()[]{}|\\~/±";
	public static final Fount FOUNT = new Fount("LiberationMono18", 14, 24, 12, 24, ALPHABET);
	public static final Fount SMOUNT = new Fount("Courier12", 10, 15, 7, 15, ALPHABET);
	public static final String[] IMG_NAMES = {"bat", "bear", "elephant", "thing", "mummy", "tongue"};
	public static final String[] PRETTY_IMG_NAMES = {"Bat", "Bear", "Elephant", "Brain-Thing", "Mummy", "Tongue"};
	public static final int[] IMG_NUMS = { 1, 3, 3, 3, 1, 2 };
	public static final double[] IMG_SHOOT_X = { 0.44, 0.46, 0.18, 0.55, 0.49, 0.50 };
	public static final double[] IMG_SHOOT_Y = { 0.61, 0.65, 0.53, 0.08, 0.12, 0.58 };
	public static final double[] IMG_MOUTH_X = { 0.44, 0.50, 0.10, 0.53, 0.48, 0.48 };
	public static final double[] IMG_MOUTH_Y = { 0.61, 0.43, 0.85, 0.57, 0.24, 0.77 };
	
	public static final Clr PAPER = new Clr(230, 230, 225);
	
	public static final HashMap<String, Img> CREATURE_IMGS;
	
	static {
		HashMap<String, Img> cis = null;
		try {
			InputStream is = PatentBlaster.class.getResourceAsStream("images/units.txt");
			cis = Img.loadMap(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		CREATURE_IMGS = cis;
	}
			
	public static void main(String[] args) {
		Engine e = new SlickEngine("Patent Blaster", "/com/zarkonnen/images/", "/com/zarkonnen/sounds/", FPS);
		e.setup(new PatentBlaster());
		e.runUntil(Condition.ALWAYS);
	}
	
	Hooks h = new Hooks();
	Hooks pastH;
	double scrollX = 0;
	double scrollY = 0;
	Level l;
	Pt curs = new Pt(0, 0);
	int nextLvlTime = 0;
	int power = 1;
	String info = "";
	boolean screened = false;
	boolean setup = true;
	ArrayList<Creature> setupCreatures = new ArrayList<Creature>();
	ArrayList<Object> shopItems = new ArrayList<Object>();
	int cooldown = 0;
	Weapon hoverWeapon;
	boolean paused = false;
	boolean mainMenu = true;
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
	
	// Prefs stuff
	public static DifficultyLevel difficultyLevel = DifficultyLevel.EASY;
	public static final HashMap<String, String> keyBindings = new HashMap<String, String>();
	public static int soundVolume = 9;
	public static int musicVolume = 2;
	
	public static final List<Pair<String, Pair<String, String>>> KEY_NAMES = l(
		p("Move left     ", p("A", "LEFT")),
		p("Move right    ", p("D", "RIGHT")),
		p("Move up / jump", p("W", "UP")),
		p("Move down     ", p("S", "DOWN")),
		p("Prev weapon   ", p("Q", (String) null)),
		p("Next weapon   ", p("E", (String) null)),
		p("Pause         ", p("P", (String) null)),
		p("Show FPS      ", p("F", (String) null))
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
			soundVolume = p.getInt("soundVolume", 9);
			musicVolume = p.getInt("musicVolume", 2);
			for (Map.Entry<String, String> kb : keyBindings.entrySet()) {
				//p.put("KEY_" + kb.getKey(), kb.getValue());
				kb.setValue(p.get("KEY_" + kb.getKey(), kb.getValue()));
			}
		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
		} catch (Exception e) { e.printStackTrace(); }
	}
	private int tick;
	
	void hit(Input in) {
		//if (!lowGraphics || doRender) {
			h.hit(in);
		//}
	}

	@Override
	public void input(Input in) {
		tick++;
		if (in.keyDown("ESCAPE") || in.keyDown("ESC") || in.keyDown("⎋")) {
			if (mainMenu && cooldown == 0) {
				if (showCredits) {
					showCredits = false;
					cooldown = 15;
				} else {
					in.quit();
				}
			} else {
				mainMenu = true;
				cooldown = 15;
			}
			return;
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
			if (musicVolume > 0) { in.playMusic(Level.MUSICS[0], musicVolume * 1.0 / 9, null); }
		}
		curs = in.cursor();
		if (cooldown > 0) { cooldown--; }
		hoverWeapon = null;
		
		if (cooldown == 0 && in.keyDown(key("F"))) {
			showFPS = !showFPS;
			cooldown = 10;
		}
		
		menuHover = "FISHCAKES";
		
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
			hit(in);
			return;
		}
		
		if (setup) {
			if (setupCreatures.isEmpty()) {
				cooldown = 10;
				for (int i = 0; i < 4; i++) {
					Creature c = Creature.make(System.currentTimeMillis() + i * 32980, difficultyLevel.playerLevel, NUM_IMAGES, false, true, false);
					setupCreatures.add(c);
				}
			}
			hit(in);
			return;
		}
		
		if (!shopItems.isEmpty()) {
			info = "";
			hit(in);
			if (cooldown == 0 && hoverWeapon != null && l.player.weapons.size() > 1 && (in.keyDown("DELETE") || in.keyDown("BACKSPACE") || in.keyDown("BACK"))) {
				l.player.weapons.remove(hoverWeapon);
				cooldown = 10;
				if (hoverWeapon == l.player.weapon) {
					l.player.weapon = l.player.weapons.get(0);
				}
			}
			return;
		}
		
		if (cooldown == 0 && in.keyDown(key("P"))) {
			paused = !paused;
			cooldown = 10;
		}
		
		if (paused) {
			info = "";
			hit(in);
			return;
		}
		
		if (l.lost()) {
			nextLvlTime++;
			if (nextLvlTime >= 140) {
				setup = true;
				nextLvlTime = 0;
				power = 1;
				
			}
		} else if (l.won()) {
			if (power == DEMO_LEVELS && DEMO) {
				l.player.hp = -1;
				l.player.explodes = true;
				l.player.lastShooter = null;
				l.player.explode(l);
				nextLvlTime = 0;
				l.texts.add(new FloatingText("KILLED BY THE DEMO LIMIT", l.player.x + l.player.w / 2, l.player.y));
				return;
			}
			nextLvlTime++;
			if (nextLvlTime >= 140) {
				l.player.newThing = null;
				l = new Level(System.currentTimeMillis(), ++power, l.player);
				l.player.heal();
				nextLvlTime = 0;
				int attempt = 0;
				Weapon w = null;
				cooldown = 20;
				do {
					w = Weapon.make(System.currentTimeMillis() + attempt++ * 1234, power, NUM_IMAGES);
				} while (attempt < 50 && !l.player.isUseful(w));
				shopItems.add(w);
				for (int i = 0; i < 3; i++) {
					Item it = null;
					attempt = 0;
					lp: do {
						it = Item.make(System.currentTimeMillis() + i * 90238 + attempt++ * 1299, power, NUM_IMAGES);
						for (Object i2 : shopItems) {
							if (i2 instanceof Item && ((Item) i2).samePowersAs(it)) {
								continue lp;
							}
						}
					} while (attempt < 500 && !l.player.isUseful(it));
					shopItems.add(it);
				}
			}
		}
		
		if (l.player.frozen == 0 && l.player.hp > 0) {
			if (l.player.knockedBack == 0) {
				if (l.player.canFly()) {
					l.player.dx = 0;
					l.player.dy = 0;
					if ((in.keyDown(key("UP")) || in.keyDown(key("W")))) {
						l.player.dy = -l.player.totalSpeed();
						l.moved = true;
					}
					if ((in.keyDown(key(("DOWN"))) || in.keyDown(key("S")))) {
						l.player.dy = l.player.totalSpeed();
						l.moved = true;
					}
				} else {
					if (l.player.ticksSinceBottom < Creature.AIR_STEERING && (in.keyDown(key("UP")) || in.keyDown(key("W")))) {
						l.player.dy = -l.player.totalSpeed() - Creature.HOP_BONUS;
						l.moved = true;
					}
				}
				if (l.player.ticksSinceBottom == 1) {
					l.player.dx = 0;
				}
				if (l.player.ticksSinceBottom < Creature.AIR_STEERING && (in.keyDown(key("LEFT")) || in.keyDown(key("A")))) {
					l.player.dx = -l.player.totalSpeed();
					l.player.flipped = false;
					l.moved = true;
				}
				if (l.player.ticksSinceBottom < Creature.AIR_STEERING && (in.keyDown(key("RIGHT")) || in.keyDown(key("D")))) {
					l.player.dx = l.player.totalSpeed();
					l.player.flipped = true;
					l.moved = true;
				}
			}
			if (l.player.weapon.reloadLeft == 0 && in.click() != null) {
				l.player.shoot(in.cursor().x - scrollX, in.cursor().y - scrollY, l);
				l.shotsFired++;
			}
		}
		for (int i = 1; i <= 9; i++) {
			if (in.keyDown("" + i) && l.player.weapons.size() >= i) {
				l.player.weapon = l.player.weapons.get(i - 1);
				l.player.changedGun = true;
			}
		}
		if (cooldown == 0) {
			if (in.keyDown(key("Q")) && l.player.weapons.indexOf(l.player.weapon) > 0) {
				l.player.weapon = l.player.weapons.get(l.player.weapons.indexOf(l.player.weapon) - 1);
				cooldown = 15;
				l.player.changedGun = true;
			}
			if (in.keyDown(key("E")) && l.player.weapons.indexOf(l.player.weapon) < l.player.weapons.size() - 1) {
				l.player.weapon = l.player.weapons.get(l.player.weapons.indexOf(l.player.weapon) + 1);
				cooldown = 15;
				l.player.changedGun = true;
			}
		}
		l.tick(in);
		scrollX = in.mode().width / 2 - l.player.x - l.player.w / 2;
		scrollY = in.mode().height * 2 / 3 - l.player.y - l.player.h / 2;
		info = "";
		hit(in);
		if (info.equals("") && l.player.newThingTimer < 6 * FPS && l.player.newThing != null) {
			info = l.player.newThing.desc(Clr.WHITE);
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
		d.rect(lowGraphics && l != null && l.player != null && l.player.hp < l.player.totalMaxHP() / 10 ? new Clr(100, 20, 20) : new Clr(32, 32, 32), 0, 0, sm.width, sm.height);
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
		
		if (mainMenu) {
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
						if (cooldown == 0) { showCredits = false; cooldown = 10; }
					}
				});
			} else {
				d.rect(Clr.BLACK, sm.width / 2, spacing + 18, 2, sm.height);
				if (newPatentTimer > FPS * 19) {
					int speed = FPS / (newPatentTimer - FPS * 19) + 1;
					Random r = new Random(patN += (tick % speed == 0 ? 1 : 0));
					d.text("[BLACK]PAT " + (Math.abs(r.nextInt()) % 10000), FOUNT, sm.width / 2 + spacing * 2, y);
				} else {
					d.text("[BLACK]" + patentName, FOUNT, sm.width / 2 + spacing * 2, y);
					d.blit("drawings/" + IMG_NAMES[patentImg] + "_drawing_large", PAPER, sm.width / 2 + spacing * 2, y + 40);
					d.text("[BLACK]" + patentText, FOUNT, sm.width / 2 + spacing * 2, y + 465, sm.width / 2 - spacing * 4);
					d.hook(sm.width / 2, 0, sm.width / 2, sm.height, new Hook(Type.MOUSE_1) {
						@Override
						public void run(Input in, Pt p, Type type) {
							if (cooldown == 0) {
								newPatentTimer = 0;
								cooldown = 15;
							}
						}
					});
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
							mainMenu = false;
							cooldown = 10;
						}
					});
					menu.append("  ");
				}
				menuItem("play", hasContinue ? "PLAY NEW" : "PLAY", true, menu, hoox, new Hook(Hook.Type.MOUSE_1) {
					@Override
					public void run(Input in, Pt p, Hook.Type type) {
						mainMenu = false;
						setup = true;
						cooldown = 10;
					}
				});
				menu.append("  ");
				menuItem("credits", "CREDITS", true, menu, hoox, new Hook(Hook.Type.MOUSE_1) {
					@Override
					public void run(Input in, Pt p, Hook.Type type) {
						showCredits = true;
						cooldown = 10;
					}
				});
				menu.append("  ");
				menuItem("quit", "QUIT", true, menu, hoox, new Hook(Hook.Type.MOUSE_1) {
					@Override
					public void run(Input in, Pt p, Hook.Type type) {
						in.quit();
					}
				});
				menu.append("\n\n");
				if (DEMO) {
					d.rect(Clr.BLACK, spacing + "EASY NORMAL HARD ".length() * FOUNT.displayWidth, y + FOUNT.lineHeight * 2 + 8, "BRUTAL".length() * FOUNT.displayWidth, 2);
				}
				for (final DifficultyLevel dl : DifficultyLevel.values()) {
					menuItem("diff", dl.name(), dl == difficultyLevel, menu, hoox, new Hook(Hook.Type.MOUSE_1) {
						@Override
						public void run(Input in, Pt p, Hook.Type type) {
							if (!DEMO || dl != DifficultyLevel.BRUTAL) {
								difficultyLevel = dl;
								savePrefs();
							} else {
								if (cooldown == 0) {
									cooldown = 10;
									in.play("boop", 1.0, 1.0, 0, 0);
								}
							}
						}
					});
					menu.append(" ");
				}
				menu.append("\n\n");
				for (final Pair<String, Pair<String, String>> kb : KEY_NAMES) {
					menu.append(kb.a).append("  ");
					menu.append(kb.equals(inputMappingToDo) && !secondaryInputMapping ? "[bg=550000]" : "");
					menuItem("key_", " " + key(kb.b.a) + " ", true, menu, hoox, new Hook(Hook.Type.MOUSE_1) {
						@Override
						public void run(Input in, Pt p, Type type) {
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
								in.playMusic(Level.MUSICS[0], ii * 1.0 / 9, null);
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
							cooldown = 10;
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
				menuR = d.textSize(menu.toString(), FOUNT, spacing, y);
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
			if (!setupCreatures.isEmpty()) {
				int spacing = 12;
				
				if (lowGraphics) {
					d.rect(PAPER, 0, 0, sm.width, sm.height);
				} else {
					for (int y = 0; y < sm.width; y += 600) {
						for (int x = 0; x < sm.height; x += 600) {
							d.blit("paper.jpg", x, y);
						}
					}
				}
				
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
						Clr t = !hover ? c.tint.mix(0.9, PAPER) : c.tint;
						d.blit("drawings/" + IMG_NAMES[c.imgIndex] + "_drawing", t, xOffset + tileX * tileW, yOffset + tileY * tileH + 35);
						d.text("[BLACK][default=BLACK]" + c.desc(Clr.BLACK, IMG_NUMS[c.imgIndex]), SMOUNT, xOffset + tileX * tileW, yOffset + tileY * tileH + 150, (int) (tileW - 10));
						d.hook(tileR.x, tileR.y, tileR.width, tileR.height, new Hook(Hook.Type.MOUSE_1) {

							@Override
							public void run(Input in, Pt p, Hook.Type type) {
								if (cooldown != 0) { return; }
								l = new Level(System.currentTimeMillis() + 10981, power = 1, c);
								l.player.makePlayerAble();
								l.player.heal();
								l.player.weapon.reloadLeft = 10;
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
							mainMenu = true;
							cooldown = 10;
						}
					}
				});
			}
		} else if (!shopItems.isEmpty()) {
			int spacing = 12;
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

			Rect titleR = d.textSize("SHOP: PICK ONE NEW ITEM", FOUNT, spacing, spacing + bottomMargin);
			d.text("[BLACK]SHOP: PICK ONE NEW ITEM", FOUNT, spacing, spacing + bottomMargin);
			d.rect(Clr.BLACK, 0, spacing + 18 + topMargin, sm.width * 3 / 4, 2);
			Rect pgR = d.textSize("Page " + l.power + 1, FOUNT, spacing, spacing + bottomMargin);
			d.text("[BLACK]Page " + l.power, FOUNT, sm.width - spacing - pgR.width, spacing + bottomMargin);
			int yOffset = spacing * 2 + (int) titleR.height + topMargin;
			int availableH = sm.height - yOffset - topMargin - bottomMargin;
			int xOffset = spacing * 2;
			int availableW = sm.width - spacing;
			int tileH = availableH / 2 + 80;
			int tileW = availableW / 2;
			for (int tileY = 0; tileY < 2; tileY++) {
				for (int tileX = 0; tileX < 2; tileX++) {
					final Object o = shopItems.get(tileY * 2 + tileX);
					String name = "";
					String desc = "";
					Img img = null;
					Clr tint = Clr.BLACK;
					if (o instanceof Weapon) {
						name = ((Weapon) o).name();
						desc = ((Weapon) o).desc(Clr.BLACK, false, false);
						img = ((Weapon) o).img;
						tint = ((Weapon) o).tint;
					} else if (o instanceof Item) {
						name = ((Item) o).name();
						desc = ((Item) o).desc(Clr.BLACK, false, false);
						img = ((Item) o).img;
						tint = ((Item) o).tint;
					} 
					Rect tileR = new Rect(xOffset + tileX * tileW, yOffset + tileY * tileH, tileW, tileH);
					boolean hover = tileR.contains(curs);
					d.text("[BLACK]" + name.toUpperCase(), FOUNT, xOffset + tileX * tileW, yOffset + tileY * tileH);
					Clr t = !hover ? tint.mix(0.9, PAPER) : tint;
					d.blit(img, t, xOffset + tileX * tileW, yOffset + tileY * tileH + 35);
					d.text("[BLACK][default=BLACK]" + desc, FOUNT, xOffset + tileX * tileW, yOffset + tileY * tileH + 220, (int) (tileW - 10));
					d.hook(tileR.x, tileR.y, tileR.width, tileR.height, new Hook(Hook.Type.MOUSE_1) {

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
							shopItems.clear();
						}
					});
				}
			}
			
			showEquipment(d, sm, FOUNT, textBGTint, false);
		} else {
			// Background texture
			if (!lowGraphics && l.background != -1) {
				for (int y = -l.backgroundH; y < (Level.LVL_H + 1) * Level.GRID_SIZE + 300; y += l.backgroundH) {
					for (int x = -l.backgroundW; x < (Level.LVL_W + 1) * Level.GRID_SIZE; x += l.backgroundW) {
						d.blit("background_" + l.background, x + scrollX, y + scrollY);
					}
				}
			}
			
			for (int y = 0; y < l.grid.length; y++) {
				for (int x = 0; x < l.grid[0].length; x++) {
					if (l.grid[y][x] == 2) {
						d.rect(Clr.GREY, x * Level.GRID_SIZE + scrollX, y * Level.GRID_SIZE + scrollY, Level.GRID_SIZE, Level.GRID_SIZE);
					}
					if (l.grid[y][x] == 1) {
						d.rect(new Clr(70, 50, 20), x * Level.GRID_SIZE + scrollX + 5, y * Level.GRID_SIZE + scrollY + 5, Level.GRID_SIZE - 10, Level.GRID_SIZE - 10);
						d.rect(new Clr(255, 255, 230), x * Level.GRID_SIZE + scrollX + 10, y * Level.GRID_SIZE + scrollY + 10, Level.GRID_SIZE - 20, Level.GRID_SIZE - 20);
						d.blit(l.boss.img, l.boss.tint, x * Level.GRID_SIZE + scrollX + 20, y * Level.GRID_SIZE + scrollY + 20, Level.GRID_SIZE - 40, Level.GRID_SIZE - 40);
					}
				}
			}
			for (Creature c : l.monsters) {
				c.draw(d, l, scrollX, scrollY);
			}
			l.player.draw(d, l, scrollX, scrollY);
			for (Goodie g : l.goodies) {
				g.draw(d, l, scrollX, scrollY);
			}
			for (Shot s : l.shots) {
				s.draw(d, l, scrollX, scrollY);
			}
			for (FloatingText ft : l.texts) {
				ft.draw(d, l, scrollX, scrollY);
			}
			if (!lowGraphics && l.player.hp < l.player.totalMaxHP() / 10) {
				d.rect(new Clr(255, 100, 100, 32), 0, 0, sm.width, sm.height);
			}
			/*if (l.tick < 1000) {
				d.text(l.player.desc(), new Fount("LiberationMono18", 12, 12, 24), 20, 20);
			}*/
			
			showEquipment(d, sm, FOUNT, textBGTint, true);
			Pt ts = d.textSize("Level " + power, FOUNT);
			d.text("Level " + power, FOUNT, sm.width - ts.x - 10, 10);
		}
		
		if (showFPS) {
			d.text(f.fps() + " FPS", FOUNT, sm.width - 100, 40);
		}
		
		if (!setup && !mainMenu && l != null && !l.moved && l.power == 1 && l.player.hp > 0 && difficultyLevel.ordinal() < DifficultyLevel.HARD.ordinal()) {
			d.text(textBGTint + key("D") + " or " + key("RIGHT") + " to move right", FOUNT, sm.width / 2 + 50, sm.height * 2 / 3 - FOUNT.lineHeight / 2);
			Pt sz = d.textSize(key("A") + " or " + key("LEFT") + " to move left", FOUNT);
			d.text(textBGTint + key("A") + " or " + key("LEFT") + " to move left", FOUNT, sm.width / 2 - 50 - sz.x, sm.height * 2 / 3 - FOUNT.lineHeight / 2);
			sz = d.textSize(key("W") + " or " + key("UP") + " to jump", FOUNT);
			d.text(textBGTint + key("W") + " or " + key("UP") + " to jump", FOUNT, sm.width / 2 - sz.x / 2, sm.height * 2 / 3 - FOUNT.lineHeight / 2 - 50);
		}
		Clr recC = Clr.RED;
		if (!setup && !mainMenu && l != null && shopItems.isEmpty() && l.player.hp > 0 && l.player.weapon.reloadLeft == 0) {
			double dx = curs.x - scrollX - l.player.gunX(), dy = curs.y - scrollY - l.player.gunY();
			if ((dx * dx + dy * dy) <= l.player.weapon.range() * l.player.weapon.range()) {
				recC = Clr.WHITE;
			} else {
				recC = new Clr(150, 150, 150);
			}
		}
		d.rect(recC, curs.x - 1, curs.y - 8, 2, 16);
		d.rect(recC, curs.x - 8, curs.y - 1, 16, 2);
		if (!setup && !mainMenu && l != null && l.power == 1 && l.moved && l.player.hp > 0 && difficultyLevel.ordinal() < DifficultyLevel.HARD.ordinal()) {
			if (l.shotsFired == 0) {
				Pt sz = d.textSize("Click to shoot", FOUNT);
				d.text(((l.tick / 20 % 2 == 0) ? "[dddddd]" : "") + textBGTint + "Click to shoot", FOUNT, Math.min(sm.width - sz.x, curs.x + 3), curs.y + 3);
			}
			if (l.shotsFired == 1 && l.player.hp > 0) {
				Pt sz = d.textSize("White recticle = weapon ready", FOUNT);
				d.text(((l.tick / 20 % 2 == 0) ? "[dddddd]" : "") + textBGTint + "White recticle = weapon ready", FOUNT, Math.min(sm.width - sz.x, curs.x + 3), curs.y + 3);
			}
		}
		h = d.getHooks();
	}
	
	private void showEquipment(Draw d, ScreenMode sm, Fount fount, String textBGTint, boolean hilite) {
		int i = 0;
		d.blit(l.player.img, l.player.tint, 15 + i * 40, 15, 30, 30, new Hook(Hook.Type.HOVER) {
			@Override
			public void run(Input in, Pt p, Hook.Type type) {
				info = l.player.desc(Clr.WHITE);
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
			d.blit(w.img, t, 15 + i * 40, 15, 30, 30, new Hook(Hook.Type.HOVER) {
				@Override
				public void run(Input in, Pt p, Hook.Type type) {
					hoverWeapon = w;
					info = w.desc(Clr.WHITE) + (l.player.weapons.size() > 1 && !shopItems.isEmpty() ? "Hit delete to delete weapon from inventory." : "");
				}
			});
			if (i < 11 && l.player.weapons.size() > 1 && hilite) {
				d.text(textBGTint + (i - 1), FOUNT, 15 + i * 40, 15);
			}
			i++;
		}
		if (shopItems.isEmpty() && !l.player.changedGun && l.player.weapons.size() > 1 && l.player.weapons.size() < 5 && l.player.showingWeaponSwitchInfo++ < FPS * 10 && difficultyLevel.ordinal() < DifficultyLevel.HARD.ordinal()) {
			d.text(((l.tick / 20 % 2 == 0) ? "[dddddd]" : "") + textBGTint + "Press " + key("Q") + " and " + key("E") + " or the number keys to switch weapons.", FOUNT, 20 + i * 40, 20);
		}
		i = 0;
		int spacing = 40;
		if (l.player.items.size() * 40 > sm.width) {
			spacing = Math.max(1, sm.width / (l.player.items.size()));
		}
		for (final Item it : l.player.items) {
			Clr t = it.tint;
			if (it == l.player.newThing && hilite) {
				t = (l.tick / 20) % 2 == 0 ? Clr.WHITE : t;
			}
			d.blit(it.img, t, 15 + i * spacing, sm.height - 45, 30, 30, new Hook(Hook.Type.HOVER) {
				@Override
				public void run(Input in, Pt p, Hook.Type type) {
					info = it.desc(Clr.WHITE);
				}
			});
			i++;
		}
		d.text(textBGTint + info, fount, 10, 60);
	}
	
	public static String round(double amt, int decimals) {
		if (decimals == 0) {
			return "" + ((int) Math.round(amt));
		}
		return "" + (((int) Math.round(amt * Math.pow(10, decimals))) * 1.0 / (Math.pow(10, decimals)));
	}
}
