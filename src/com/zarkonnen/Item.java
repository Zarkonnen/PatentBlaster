package com.zarkonnen;

import com.zarkonnen.catengine.util.Clr;
import java.util.Random;
import static com.zarkonnen.PatentBlaster.round;
import static com.zarkonnen.Util.*;
import com.zarkonnen.catengine.Img;

public class Item implements HasDesc {
	public String name;
	public int imgIndex;
	public Img img;
	public Clr tint;
	public double resistance;
	public Element resistanceVs;
	public double speedMult = 1;
	public double hpRegen;
	public int hpBonus = 0;
	public boolean fly;
	public boolean shield;
	public double vampireMult;
	public boolean givesInfo;
	public int shieldReload = 0;
	public int shieldReloadTime = 300;
	public double eating = 0;
	public boolean resurrect;
	public int creatureHPBonus = 0;
	public long seed;
	
	public void tick(Level l, Creature owner) {
		if (shieldReload > 0) {
			shieldReload--;
			if (shieldReload == 0) {
				owner.sound("shield_restore", l);
			}
		}
	}
	
	public static Item make(long seed, int power, int numImages) {
		Random r = new Random(seed);
		Item i = new Item();
		i.seed = seed;
		int type = r.nextInt(77) % (Math.min(power * 2 + 1, power > 9 ? 10 : 9));
		switch (type) {
			case 0:
				i.resistanceVs = Element.values()[r.nextInt(Element.values().length)];
				double div = (i.resistanceVs == Element.FIRE || i.resistanceVs == Element.ICE) ? 8.0 : 6.0;
				i.resistance = 1 - 1 / (power / div + 1);
				/*i.resistance = r.nextInt(Math.min(7, power * 3)) * 0.1 + 0.2;*/
				i.tint = i.resistanceVs.tint;
				break;
			case 1:
				//i.hpBonus = (int) ((2 + r.nextInt(4)) * Math.pow(power, 1.2) * 3) + 15;
				i.hpBonus = (int) (BASE_HP_BONUS * powerLvl(power));
				i.tint = new Clr(150, 50, 0);
				break;
			case 2:
				i.speedMult = 1.1 + r.nextInt(Math.min(8, power)) * 0.1;
				double value = i.speedMult * BASE_HP_BONUS;
				i.creatureHPBonus = (int) (BASE_HP_BONUS * powerLvl(power) - value);
				i.tint = Clr.YELLOW;
				break;
			case 3:
				//i.hpRegen = r.nextInt((int) Math.pow(power, 1.2) / 2 + 1) * 0.005 + 0.005;
				i.hpRegen = BASE_REGEN * powerLvl(power);
				i.tint = new Clr(255, 100, 100);
				break;
			case 4:
				i.eating = Math.min(1.0 / 50, power * 0.001);
				if (power > 20) {
					i.creatureHPBonus = (int) (BASE_HP_BONUS * (powerLvl(power) - powerLvl(20)));
				}
				i.tint = Clr.RED;
				break;
			case 5:
				i.tint = new Clr(0, 255, 255);
				if (power > 5) {
					i.creatureHPBonus = (int) (BASE_HP_BONUS * (powerLvl(power) - powerLvl(5)));
				}
				i.givesInfo = true;
				break;
			case 6:
				i.tint = Clr.GREY;
				i.shield = true;
				if (power > 8) {
					i.creatureHPBonus = (int) (BASE_HP_BONUS * (powerLvl(power) - powerLvl(8)));
				}
				break;
			case 7:
				i.tint = new Clr(100, 0, 100);
				i.vampireMult = Math.min(1.0, power * 0.05);
				if (power > 20) {
					i.creatureHPBonus = (int) (BASE_HP_BONUS * (powerLvl(power) - powerLvl(20)));
				}
				break;
			case 8:
				i.resurrect = true;
				i.tint = new Clr(180, 180, 255);
				break;
			case 9:
				i.tint = new Clr(200, 255, 200);
				i.fly = true;
				if (power > 10) {
					i.creatureHPBonus = (int) (BASE_HP_BONUS * (powerLvl(power) - powerLvl(10)));
				}
				break;
		}
		i.imgIndex = r.nextInt(numImages);
		i.img = PatentBlaster.CREATURE_IMGS.get(PatentBlaster.IMG_NAMES[i.imgIndex]);
		i.name = Names.pick(r);
		return i;
	}
	
	public String name() {
		return "Pat " + (Math.abs(seed % 10000) + 1934) + ", " + name;
	}
	
	@Override
	public String desc(Clr textTint) {
		return desc(textTint, true, true);
	}
	
	public String desc(Clr textTint, boolean showName, boolean inset) {
		String is = inset ? "  " : "";
		StringBuilder sb = new StringBuilder();
		if (showName) { sb.append("").append(name.toUpperCase()).append("\n"); }
		sb.append("[").append(tint.mix(0.4, textTint).toString()).append("]");
		if (resistanceVs != null) {
			sb.append(is).append(round(resistance * 100, 0)).append("% resistance to ").append(resistanceVs.name()).append("\n");
		}
		if (speedMult != 1) {
			sb.append(is).append(round((speedMult - 1) * 100, 0)).append("% speed increase\n");
		}
		if (hpRegen != 0) {
			sb.append(is).append(round(hpRegen * PatentBlaster.FPS, 1)).append(" HP regeneration/sec\n");
		}
		if (hpBonus > 0) {
			sb.append(is).append(hpBonus).append(" max HP increase\n");
		}
		if (givesInfo) {
			sb.append(is).append("Gives detailed enemy info\n");
		}
		if (shield) {
			sb.append(is).append("Energy shield\n");
			if (shieldReload > 0) {
				sb.append(is).append(shieldReload).append("/").append(shieldReloadTime).append(" reloaded\n");
			} else {
				sb.append(is).append("Active\n");
			}
		}
		if (vampireMult > 0) {
			sb.append(is).append(round(vampireMult * 100, 0)).append("% of damage gained as HP\n");
		}
		if (fly) {
			sb.append(is).append("Ability to fly\n");
		}
		if (eating > 0) {
			sb.append(is).append("Eating flesh gives you ").append(round(eating * 100 * 50, 0)).append("% of the victim's HP\n");
		}
		if (resurrect) {
			sb.append(is).append("Resurrects once\n");
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
		it.img = img;
		it.name = name;
		it.resistance = resistance;
		it.resistanceVs = resistanceVs;
		it.shield = shield;
		it.shieldReload = shieldReload;
		it.shieldReloadTime = shieldReloadTime;
		it.speedMult = speedMult;
		it.tint = tint;
		it.vampireMult = vampireMult;
		it.resurrect = resurrect;
		return it;
	}

	boolean samePowersAs(Item it) {
		return
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
				vampireMult == it.vampireMult;
	}
}
