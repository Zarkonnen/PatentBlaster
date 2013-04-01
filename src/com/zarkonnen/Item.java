package com.zarkonnen;

import com.zarkonnen.catengine.util.Clr;
import java.util.Random;
import static com.zarkonnen.PatentBlaster.round;
import static com.zarkonnen.Const.*;
import com.zarkonnen.catengine.Img;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;

public class Item implements HasDesc, Comparable<Item>, Serializable {
	public static final Clr SHIELD_ACTIVE = new Clr(215, 215, 215);
	
	public static enum Type {
		STEEL_RESIST(0, Element.STEEL.tint, 50) {
			@Override
			public void make(Item it, int power) {
				it.resistanceVs = Element.STEEL;
				it.resistance = 1 - 1 / (power / 20.0 + 1);
			}
			@Override
			public boolean useful(Creature c) { return c.resistance(Element.STEEL) < 0.8999; }
		},
		ACID_RESIST(0, Element.ACID.tint, 50) {
			@Override
			public void make(Item it, int power) {
				it.resistanceVs = Element.ACID;
				it.resistance = 1 - 1 / (power / 20.0 + 1);
			}
			@Override
			public boolean useful(Creature c) { return c.resistance(Element.ACID) < 0.8999; }
		},
		FIRE_RESIST(0, Element.FIRE.tint, 50) {
			@Override
			public void make(Item it, int power) {
				it.resistanceVs = Element.FIRE;
				it.resistance = 1 - 1 / (power / 30.0 + 1);
			}
			@Override
			public boolean useful(Creature c) { return c.resistance(Element.FIRE) < 0.8999; }
		},
		ICE_RESIST(0, Element.ICE.tint, 50) {
			@Override
			public void make(Item it, int power) {
				it.resistanceVs = Element.ICE;
				it.resistance = 1 - 1 / (power / 30.0 + 1);
			}
			@Override
			public boolean useful(Creature c) { return c.resistance(Element.ICE) < 0.8999; }
		},
		HP_BONUS(0, new Clr(150, 50, 0), 100) {
			@Override
			public void make(Item it, int power) {
				it.hpBonus = (int) (BASE_HP_BONUS * powerLvl(power));
			}
		},
		HP_REGEN(0, new Clr(255, 100, 100), 50) {
			@Override
			public void make(Item it, int power) {
				it.hpRegen = BASE_REGEN * powerLvl(power);
			}
		},
		SPEED_MULT(2, new Clr(255, 255, 0), 100) {
			@Override
			public void make(Item it, int power) {
				it.speedMult = 1.25;
				if (power > 2) {
					it.creatureHPBonus = (int) (BASE_HP_BONUS * (powerLvl(power) - powerLvl(2)));
				}
			}
			@Override
			public boolean useful(Creature c) { return c.totalSpeed() < Creature.MAX_SPEED; }
		},
		GIVES_INFO(3, new Clr(0, 255, 255), 50) {
			@Override
			public void make(Item it, int power) {
				it.givesInfo = true;
				if (power > 3) {
					it.creatureHPBonus = (int) (BASE_HP_BONUS * (powerLvl(power) - powerLvl(3)));
				}
			}
			@Override
			public boolean useful(Creature c) { return !c.canSeeStats; }
		},
		RESURRECT(4, new Clr(180, 180, 255), 30) {
			@Override
			public void make(Item it, int power) {
				it.resurrect = true;
				if (power > 4) {
					it.creatureHPBonus = (int) (BASE_HP_BONUS * (powerLvl(power) - powerLvl(4)));
				}
			}
		},
		SHIELD(4, Clr.GREY, 100) {
			@Override
			public void make(Item it, int power) {
				it.shield = true;
				if (power > 4) {
					it.creatureHPBonus = (int) (BASE_HP_BONUS * (powerLvl(power) - powerLvl(4)));
				}
			}
			@Override
			public boolean useful(Creature c) {
				int shields = 0;
				for (Item it2 : c.items) {
					if (it2.shield) { shields++; }
				}
				return shields < 3;
			}
		},
		HOVER(4, new Clr(100, 127, 100), 100) {
			@Override
			public void make(Item it, int power) {
				it.hover = true;
				if (power > 5) {
					it.creatureHPBonus = (int) (BASE_HP_BONUS * (powerLvl(power) - powerLvl(5)));
				}
			}
			@Override
			public boolean useful(Creature c) { return !c.canHover(); }
		},
		EATING(5, Clr.RED, 50) {
			@Override
			public void make(Item it, int power) {
				it.eating = Math.min(1.0 / 50, power * 0.0015);
				if (power > 5) {
					it.creatureHPBonus = (int) (BASE_HP_BONUS * (powerLvl(power) - powerLvl(5)));
				}
			}
			@Override
			public boolean useful(Creature c) { return c.totalEating() < 1.0; }
		},
		VAMPIRE(7, new Clr(100, 0, 100), 30) {
			@Override
			public void make(Item it, int power) {
				it.vampireMult = Math.min(1.0, power * 0.02);
				if (power > 7) {
					it.creatureHPBonus = (int) (BASE_HP_BONUS * (powerLvl(power) - powerLvl(7)));
				}
			}
			@Override
			public boolean useful(Creature c) { return c.totalVamp() < 1.0; }
		},
		FLY(9, new Clr(200, 255, 200), 30) {
			@Override
			public void make(Item it, int power) {
				it.fly = true;
				if (power > 9) {
					it.creatureHPBonus = (int) (BASE_HP_BONUS * (powerLvl(power) - powerLvl(9)));
				}
			}
			@Override
			public boolean useful(Creature c) { return !c.canFly(); }
		},
		CLOAKING(13, new Clr(60, 55, 50), 20) {
			@Override
			public void make(Item it, int power) {
				it.cloaking = true;
				if (power > 13) {
					it.creatureHPBonus = (int) (BASE_HP_BONUS * (powerLvl(power) - powerLvl(13)));
				}
			}
			@Override
			public boolean useful(Creature c) { return !c.canFly(); }
		};
		// etc.
		
		public final int minPower;
		public final Clr tint;
		public final int freq;
		public abstract void make(Item it, int power);
		public boolean useful(Creature c) { return true; }

		private Type(int minPower, Clr tint, int freq) {
			this.minPower = minPower;
			this.tint = tint;
			this.freq = freq;
		}
	}
	
	public String name;
	public Type type;
	public int power;
	public int imgIndex;
	public Img img;
	public Img largeImg;
	public Clr tint;
	public double resistance;
	public Element resistanceVs;
	public double speedMult = 1;
	public double hpRegen;
	public int hpBonus = 0;
	public boolean fly;
	public boolean hover;
	public boolean shield;
	public double vampireMult;
	public boolean givesInfo;
	public int shieldReload = 0;
	public int shieldReloadTime = 300;
	public double eating = 0;
	public boolean resurrect;
	public boolean cloaking;
	public int creatureHPBonus = 0;
	public long seed;
	
	public Item makeTwin() {
		return new Item(name, type, power, imgIndex, img, largeImg, tint, resistance, resistanceVs, hpRegen, fly, hover, shield, vampireMult, givesInfo, resurrect, cloaking, seed);
	}

	public Item() {}
	
	public Item(String name, Type type, int power, int imgIndex, Img img, Img largeImg, Clr tint, double resistance, Element resistanceVs, double hpRegen, boolean fly, boolean hover, boolean shield, double vampireMult, boolean givesInfo, boolean resurrect, boolean cloaking, long seed) {
		this.name = name;
		this.type = type;
		this.power = power;
		this.imgIndex = imgIndex;
		this.img = img;
		this.largeImg = largeImg;
		this.tint = tint;
		this.resistance = resistance;
		this.resistanceVs = resistanceVs;
		this.hpRegen = hpRegen;
		this.fly = fly;
		this.hover = hover;
		this.shield = shield;
		this.vampireMult = vampireMult;
		this.givesInfo = givesInfo;
		this.resurrect = resurrect;
		this.cloaking = cloaking;
		this.seed = seed;
	}
	
	public void tick(Level l, Creature owner) {
		if (shieldReload > 0) {
			shieldReload--;
			if (shieldReload == 0) {
				owner.sound("shield_restore", l);
			}
		}
		if (shield) {
			tint = shieldReload == 0 ? SHIELD_ACTIVE : Type.SHIELD.tint;
		}
	}
	
	@Override
	public int compareTo(Item t) {
		int tc = type.compareTo(t.type);
		if (tc != 0) { return tc; }
		int pc = power - t.power;
		if (pc != 0) { return pc; }
		return name.compareTo(t.name);
	}
	
	public static Item make(long seed, int power, Creature forC, EnumSet<Type> alreadyChosen) {
		Random r = new Random(seed);
		Item it = new Item();
		it.seed = seed;
		it.imgIndex = r.nextInt(PatentBlaster.NUM_ITEM_IMAGES);
		it.img = PatentBlaster.ITEM_IMGS.get(PatentBlaster.ITEM_NAMES[it.imgIndex]);
		it.largeImg = PatentBlaster.LARGE_ITEM_IMGS.get(PatentBlaster.ITEM_NAMES[it.imgIndex]);
		it.name = Names.pick(r);
		
		ArrayList<Type> ts = new ArrayList<Type>();
		int range = 0;
		for (Type t : Type.values()) {
			if (t.minPower <= power && (forC == null || t.useful(forC)) && (alreadyChosen == null || !alreadyChosen.contains(t))) {
				ts.add(t);
				range += t.freq;
			}
		}
		
		int pick = r.nextInt(range);
		int pickIndex = 0;
		while (pick > ts.get(pickIndex).freq) {
			pick -= ts.get(pickIndex++).freq;
		}
		ts.get(pickIndex).make(it, power);
		it.type = ts.get(pickIndex);
		it.tint = it.type.tint;
		it.power = power;
		
		return it;
	}
	
	public String name() {
		return "Pat " + (Math.abs(seed % 10000) + 1934) + ", " + name;
	}
	
	@Override
	public String desc(Clr textTint) {
		return desc(textTint, true, true, null);
	}
	
	public String desc(Clr textTint, boolean showName, boolean inset, Creature forC) {
		String is = inset ? "  " : "";
		StringBuilder sb = new StringBuilder();
		if (showName) { sb.append("").append(name.toUpperCase()).append("\n"); }
		sb.append("[").append(tint.mix(0.4, textTint).toString()).append("]");
		if (resistanceVs != null) {
			sb.append(is).append(round(resistance * 100, 0)).append("% resistance to ").append(resistanceVs.name()).append(" Damage\n");
			if (forC != null) {
				sb.append(is).append("Current ").append(resistanceVs.name()).append(" damage resistance: ").append(round(forC.resistance(resistanceVs) * 100, 0)).append("%\n");
			}
		}
		if (speedMult != 1) {
			sb.append(is).append(round((speedMult - 1) * 100, 0)).append("% speed increase\n");
			if (forC != null) {
				sb.append(is).append("Current speed: ").append(round(forC.totalSpeed() * PatentBlaster.FPS, 0)).append(" px/sec\n");
			}
		}
		if (hpRegen != 0) {
			sb.append(is).append(round(hpRegen * PatentBlaster.FPS, 1)).append(" HP regeneration/sec\n");
			if (forC != null) {
				sb.append(is).append("Current regeneration: ").append(round(forC.baseHPRegen() * PatentBlaster.FPS, 1)).append(" HP/sec\n");
			}
		}
		if (hpBonus > 0) {
			sb.append(is).append(hpBonus).append(" max HP increase\n");
			if (forC != null) {
				sb.append(is).append("Current max HP: ").append(forC.totalMaxHP()).append("\n");
			}
		}
		if (givesInfo) {
			sb.append("  ").append("Shows enemy hit points\n");
		}
		if (shield) {
			sb.append(is).append("Energy shield\n");
			if (shieldReload > 0) {
				sb.append(is).append(shieldReload).append("/").append(shieldReloadTime).append(" reloaded\n");
			} else {
				sb.append(is).append("Active\n");
			}
			if (forC != null) {
				int shields = 0;
				for (Item it2 : forC.items) {
					if (it2.shield) { shields++; }
				}
				if (shields != 0) {
					sb.append(is).append("Currently: ").append(shields).append(" shields\n");
				}
			}
		}
		if (vampireMult > 0) {
			sb.append(is).append(round(vampireMult * 100, 0)).append("% of damage gained as HP\n");
			if (forC != null) {
				sb.append(is).append("Currently: ").append(round(forC.totalVamp() * 100, 0)).append(" %\n");
			}
		}
		if (fly) {
			sb.append(is).append("Ability to fly\n");
		}
		if (hover) {
			sb.append(is).append("Ability to hover\n");
		}
		if (eating > 0) {
			sb.append(is).append("Eating flesh gives you ").append(round(eating * 100 * 50, 0)).append("% of the victim's HP\n");
			if (forC != null) {
				sb.append(is).append("Currently: ").append(round(forC.totalEating() * 100 * 50, 0)).append("%\n");
			}
		}
		if (resurrect) {
			sb.append(is).append("Resurrects once\n");
			if (forC != null) {
				int reses = 0;
				for (Item it2 : forC.items) {
					if (it2.resurrect) { reses++; }
				}
				if (reses != 0) {
					sb.append(is).append("Currently: ").append(reses).append(" resurrections\n");
				}
			}
		}
		if (cloaking) {
			sb.append(is).append("Turns wearer invisible\n");
		}
		sb.append("[]");
		return sb.toString();
	}

	Item makeTinyVersion() {
		Item it = new Item();
		it.eating = eating;
		it.fly = fly;
		it.givesInfo = givesInfo;
		it.hpBonus = hpBonus / 4;
		it.hpRegen = hpRegen / 4;
		it.imgIndex = imgIndex;
		it.img = img;
		it.largeImg = largeImg;
		it.name = name;
		it.type = type;
		it.resistance = resistance;
		it.resistanceVs = resistanceVs;
		it.shield = shield;
		it.shieldReload = shieldReload;
		it.shieldReloadTime = shieldReloadTime;
		it.speedMult = speedMult;
		it.tint = tint;
		it.vampireMult = vampireMult;
		it.resurrect = resurrect;
		it.creatureHPBonus = creatureHPBonus / 4;
		it.hover = hover;
		it.cloaking = cloaking;
		return it;
	}
	
	Item makeGiantVersion() {
		Item it = new Item();
		it.eating = eating;
		it.fly = fly;
		it.givesInfo = givesInfo;
		it.hpBonus = hpBonus * 4;
		it.hpRegen = hpRegen * 4;
		it.imgIndex = imgIndex;
		it.img = img;
		it.largeImg = largeImg;
		it.name = name;
		it.type = type;
		it.resistance = resistance;
		it.resistanceVs = resistanceVs;
		it.shield = shield;
		it.shieldReload = shieldReload;
		it.shieldReloadTime = shieldReloadTime;
		it.speedMult = speedMult;
		it.tint = tint;
		it.vampireMult = vampireMult;
		it.resurrect = resurrect;
		it.creatureHPBonus = creatureHPBonus * 4;
		it.hover = hover;
		it.cloaking = cloaking;
		return it;
	}

	boolean samePowersAs(Item it) {
		return
				hover == it.hover &&
				fly == it.fly &&
				givesInfo == it.givesInfo &&
				resurrect == it.resurrect &&
				shield == it.shield &&
				eating == it.eating &&
				speedMult == it.speedMult &&
				hpBonus == it.hpBonus &&
				hpRegen == it.hpRegen &&
				resistance == it.resistance &&
				resistanceVs == it.resistanceVs &&
				vampireMult == it.vampireMult &&
				cloaking == it.cloaking;
	}
}
