package com.zarkonnen;

import com.zarkonnen.catengine.util.Clr;
import java.util.Random;
import static com.zarkonnen.PatentBlaster.round;
import static com.zarkonnen.Const.*;
import com.zarkonnen.catengine.Img;
import java.io.Serializable;

public class Weapon implements HasDesc, Serializable {
	public static final int MIN_RELOAD = PatentBlaster.FPS / 4;
	public static final int MAX_RELOAD = PatentBlaster.FPS * 2;
	public static final int AVG_RELOAD = MIN_RELOAD / 2 + MAX_RELOAD / 2;
	
	public static final double MIN_SHOT_SPEED = 4.5;
	public static final double MAX_SHOT_SPEED = 8;
	public static final double AVG_SHOT_SPEED = MIN_SHOT_SPEED / 2 + MAX_SHOT_SPEED / 2;
	
	public static final int MIN_RANGE = 300;
	public static final int MAX_RANGE = 800;
	public static final int AVG_RANGE = MIN_RANGE / 2 + MAX_RANGE / 2;
	
	public static final int MIN_SHOT_SIZE = 4;
	public static final int AVG_SHOT_SIZE = 6;
	
	public int imgIndex;
	public Img img;
	public Img largeImg;
	public Clr tint;
	public Element element;
	public double dmg;
	public int reload;
	public int reloadLeft;
	public double shotSpeed;
	public double jitter;
	public double shotSize;
	public boolean knockback;
	public int shotLife;
	public boolean homing;
	public boolean swarm;
	public boolean shotgun;
	public boolean scattershot;
	public boolean sticky;
	public boolean grenade;
	public boolean sword;
	public boolean flamethrower;
	public int numBullets = 1;
	public String name;
	public long seed;
	public int clickEmptyTimer;
	public boolean reduceInaccuracy;
	
	public void tick() {
		if (reloadLeft > 0) { reloadLeft--; } else { clickEmptyTimer = 0; }
		if (clickEmptyTimer > 0) { clickEmptyTimer--; }
		reduceInaccuracy = false;
	}
	
	public double range() {
		return shotLife * shotSpeed;
	}
	
	public double dps() {
		return dmg * 1.0 / reload * PatentBlaster.FPS * numBullets;
	}
	
	public static Weapon make(long seed, int power, boolean allowMelee) {
		Random r = new Random(seed);
		Weapon w = new Weapon();
		w.seed = seed;
		w.element = Element.values()[r.nextInt(Element.values().length)];
		double dmg = BASE_DMG * w.element.dmgMult;
		w.reload = MIN_RELOAD + r.nextInt(MAX_RELOAD - MIN_RELOAD);
		w.reloadLeft = w.reload;
		dmg *= Math.pow(1.0 * w.reload / AVG_RELOAD, 1.4);
		w.shotSpeed = MIN_SHOT_SPEED + r.nextDouble() * (MAX_SHOT_SPEED - MIN_SHOT_SPEED);
		dmg /= Math.pow(w.shotSpeed / AVG_SHOT_SPEED, 0.3);
		w.jitter = r.nextDouble() * (2.0 / (power + 5));
		dmg *= (0.6 + w.jitter * 2);
		int range = MIN_RANGE + r.nextInt(MAX_RANGE - MIN_RANGE);
		w.shotLife = (int) (range / w.shotSpeed);
		dmg /= Math.sqrt(1.0 * range / AVG_RANGE);
		w.homing = r.nextInt(8) == 0;
		w.swarm = w.homing && r.nextInt(2) == 0;
		w.shotSize = MIN_SHOT_SIZE + (AVG_SHOT_SIZE - MIN_SHOT_SIZE) * dmg / BASE_DMG;
		w.shotgun = !w.homing && !w.swarm && r.nextInt(10) == 0;
		w.grenade = !w.homing && !w.swarm && !w.shotgun && !w.scattershot && r.nextInt(8) == 0;
		w.flamethrower = allowMelee && !w.homing && !w.swarm && !w.shotgun && !w.scattershot && !w.grenade && !w.sword && w.element == Element.FIRE && r.nextInt(6) == 0;
		w.knockback = !w.swarm && !w.shotgun && !w.scattershot && !w.grenade && !w.sword && !w.flamethrower && r.nextInt(30) == 0;
		if (w.homing) { w.reload = w.reload * 3 / 2; dmg *= 0.8; w.shotLife = w.shotLife * 3 / 2; }
		if (w.swarm) { dmg /= 8; w.shotSize = w.shotSize / 4 + 1; w.numBullets = 8; }
		if (w.knockback) { dmg *= 0.8; }
		if (w.shotgun) { w.reload = w.reload * 3 / 2; dmg /= 4; w.shotLife *= 0.4; w.shotSize = w.shotSize / 2 + 1; w.numBullets = 8; w.jitter += 0.2; }
		if (w.scattershot) { dmg /= 8; w.shotSize = w.shotSize / 2 + 1; w.numBullets = 5; w.jitter += 0.08; }
		if (w.sticky) { dmg *= 0.8; w.shotSize = w.shotSize * 1.5 + 1; }
		if (w.grenade) { w.shotSize = w.shotSize * 1.5; w.reload *= 1.5; w.dmg *= 1.5; w.shotLife *= 0.6; w.shotSpeed *= 0.9; }
		if (w.flamethrower) { dmg /= w.reload; dmg *= 3; w.reload = 1; w.shotLife *= 0.4; w.jitter += 0.2; w.shotSize *= 0.75; }
		w.tint = w.element.tint;
		w.name = Names.pick(r);
		w.imgIndex = r.nextInt(PatentBlaster.NUM_ITEM_IMAGES);
		w.img = PatentBlaster.ITEM_IMGS.get(PatentBlaster.ITEM_NAMES[w.imgIndex]);
		w.largeImg = PatentBlaster.LARGE_ITEM_IMGS.get(PatentBlaster.ITEM_NAMES[w.imgIndex]);
		w.dmg = dmg * Math.pow(powerLvl(power), 1.2);
		w.shotSpeed *= w.element.speedMult;
		return w;
	}
	
	String accuracy() {
		if (swarm || sword) { return ""; }
		if (jitter == 0) { return "Perfect accuracy.\n"; }
		if (jitter < 2 * Math.PI / 180) { return "Highly accurate (±" + round(jitter * 180 / Math.PI) + " degrees)\n"; }
		if (jitter < 4 * Math.PI / 180) { return "Accurate (±" + round(jitter * 180 / Math.PI) + " degrees)\n"; }
		if (jitter < 9 * Math.PI / 180) { return ""; }
		if (jitter < 15 * Math.PI / 180) { return "Inaccurate (±" + round(jitter * 180 / Math.PI) + " degrees)\n"; }
		return "Highly inaccurate (±" + round(jitter * 180 / Math.PI) + " degrees)\n";
	}
	
	String pad(int n) {
		return pad("" + n);
	}
	
	String pad(String n) {
		switch (n.length()) {
			case 0:
				return "     ";
			case 1:
				return "    " + n;
			case 2:
				return "   " + n;
			case 3:
				return "  " + n;
			case 4:
				return " " + n;
			default:
				return n;
		}
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
		if (showName) {
			sb.append("").append(name.toUpperCase()).append("");
			sb.append(", [").append(element.tint.mix(0.4, textTint)).append("]").append(element.name()).append("[] Weapon\n");
		} else {
			sb.append(is).append("[").append(element.tint.mix(0.4, textTint)).append("]").append(element.name()).append("[] Weapon\n");
		}
		sb.append(is).append(round(dmg * numBullets));
		sb.append(" damage every ").append(round(reload * 1.0 / PatentBlaster.FPS)).append("sec (").append(round(dps())).append(" DPS)\n");
		sb.append(is).append(round(shotSpeed * PatentBlaster.FPS));
		sb.append(" px/sec shot speed, ").append(round(range())).append(" px range\n");
		sb.append(accuracy().length() == 0 ? "" : is).append(accuracy());
		if (knockback) {
			sb.append(is).append("Knockback.\n");
		}
		if (sticky) {
			sb.append(is).append("Sticky.\n");
		}
		if (shotgun) {
			sb.append(is).append("Shotgun.\n");
		}
		if (scattershot) {
			sb.append(is).append("Scattershot.\n");
		}
		if (grenade) {
			sb.append(is).append("Grenade.\n");
		}
		if (sword) {
			sb.append(is).append("Sword.\n");
		}
		if (flamethrower) {
			sb.append(is).append("Flamethrower.\n");
		}
		if (homing) {
			if (swarm) {
				sb.append(is).append("Homing Swarm.\n");
			} else {
				sb.append(is).append("Homing.\n");
			}
		}
		return sb.toString();
	}

	public boolean penetrates() {
		return element == Element.STEEL || sword;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 19 * hash + this.imgIndex;
		hash = 19 * hash + (this.img != null ? this.img.hashCode() : 0);
		hash = 19 * hash + (this.tint != null ? this.tint.hashCode() : 0);
		hash = 19 * hash + (this.element != null ? this.element.hashCode() : 0);
		hash = 19 * hash + (int) (Double.doubleToLongBits(this.dmg) ^ (Double.doubleToLongBits(this.dmg) >>> 32));
		hash = 19 * hash + this.reload;
		hash = 19 * hash + (int) (Double.doubleToLongBits(this.shotSpeed) ^ (Double.doubleToLongBits(this.shotSpeed) >>> 32));
		hash = 19 * hash + (int) (Double.doubleToLongBits(this.jitter) ^ (Double.doubleToLongBits(this.jitter) >>> 32));
		hash = 19 * hash + (int) (Double.doubleToLongBits(this.shotSize) ^ (Double.doubleToLongBits(this.shotSize) >>> 32));
		hash = 19 * hash + (this.knockback ? 1 : 0);
		hash = 19 * hash + this.shotLife;
		hash = 19 * hash + (this.homing ? 1 : 0);
		hash = 19 * hash + (this.swarm ? 1 : 0);
		hash = 19 * hash + (this.shotgun ? 1 : 0);
		hash = 19 * hash + (this.scattershot ? 1 : 0);
		hash = 19 * hash + (this.sticky ? 1 : 0);
		hash = 19 * hash + (this.grenade ? 1 : 0);
		hash = 19 * hash + (this.sword ? 1 : 0);
		hash = 19 * hash + (this.flamethrower ? 1 : 0);
		hash = 19 * hash + this.numBullets;
		hash = 19 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 19 * hash + (int) (this.seed ^ (this.seed >>> 32));
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Weapon other = (Weapon) obj;
		if (this.imgIndex != other.imgIndex) {
			return false;
		}
		if (this.img != other.img && (this.img == null || !this.img.equals(other.img))) {
			return false;
		}
		if (this.tint != other.tint && (this.tint == null || !this.tint.equals(other.tint))) {
			return false;
		}
		if (this.element != other.element) {
			return false;
		}
		if (this.dmg != other.dmg) {
			return false;
		}
		if (this.reload != other.reload) {
			return false;
		}
		if (Double.doubleToLongBits(this.shotSpeed) != Double.doubleToLongBits(other.shotSpeed)) {
			return false;
		}
		if (Double.doubleToLongBits(this.jitter) != Double.doubleToLongBits(other.jitter)) {
			return false;
		}
		if (Double.doubleToLongBits(this.shotSize) != Double.doubleToLongBits(other.shotSize)) {
			return false;
		}
		if (this.knockback != other.knockback) {
			return false;
		}
		if (this.shotLife != other.shotLife) {
			return false;
		}
		if (this.homing != other.homing) {
			return false;
		}
		if (this.swarm != other.swarm) {
			return false;
		}
		if (this.shotgun != other.shotgun) {
			return false;
		}
		if (this.scattershot != other.scattershot) {
			return false;
		}
		if (this.sticky != other.sticky) {
			return false;
		}
		if (this.grenade != other.grenade) {
			return false;
		}
		if (this.sword != other.sword) {
			return false;
		}
		if (this.flamethrower != other.flamethrower) {
			return false;
		}
		if (this.numBullets != other.numBullets) {
			return false;
		}
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		if (this.seed != other.seed) {
			return false;
		}
		return true;
	}

	public Weapon makeTinyVersion() {
		Weapon t = new Weapon();
		t.dmg = dmg / 3;
		t.element = element;
		t.img = img;
		t.largeImg = largeImg;
		t.jitter = jitter * 0.7;
		t.name = "Tiny " + name;
		t.reload = reload;
		t.shotLife = shotLife * 2 / 3;
		t.shotSize = shotSize / 2 + 1;
		t.tint = tint;
		t.shotSpeed = shotSpeed;
		t.shotgun = shotgun;
		t.scattershot = scattershot;
		t.sticky = sticky;
		t.homing = homing;
		t.swarm = swarm;
		t.numBullets = numBullets;
		t.grenade = grenade;
		t.sword = sword;
		t.flamethrower = flamethrower;
		return t;
	}
	
	public Weapon makeGiantVersion() {
		Weapon t = new Weapon();
		t.dmg = dmg * 3;
		t.element = element;
		t.img = img;
		t.largeImg = largeImg;
		t.jitter = jitter * 0.7;
		t.name = "Giant " + name;
		t.reload = reload;
		t.shotLife = shotLife * 3 / 2;
		t.shotSize = shotSize * 2;
		t.tint = tint;
		t.shotSpeed = shotSpeed;
		t.shotgun = shotgun;
		t.scattershot = scattershot;
		t.sticky = sticky;
		t.homing = homing;
		t.swarm = swarm;
		t.numBullets = numBullets;
		t.grenade = grenade;
		t.sword = sword;
		t.flamethrower = flamethrower;
		return t;
	}
	

	public Weapon makeTwin() {
		return new Weapon(imgIndex, img, largeImg, tint, element, dmg, reload, reloadLeft, shotSpeed, jitter, shotSize, knockback, shotLife, homing, swarm, shotgun, scattershot, sticky, grenade, sword, flamethrower, numBullets, name, seed);
	}
	
	public Weapon() {}

	public Weapon(int imgIndex, Img img, Img largeImg, Clr tint, Element element, double dmg, int reload, int reloadLeft, double shotSpeed, double jitter, double shotSize, boolean knockback, int shotLife, boolean homing, boolean swarm, boolean shotgun, boolean scattershot, boolean sticky, boolean grenade, boolean sword, boolean flamethrower, int numBullets, String name, long seed) {
		this.imgIndex = imgIndex;
		this.img = img;
		this.largeImg = largeImg;
		this.tint = tint;
		this.element = element;
		this.dmg = dmg;
		this.reload = reload;
		this.reloadLeft = reloadLeft;
		this.shotSpeed = shotSpeed;
		this.jitter = jitter;
		this.shotSize = shotSize;
		this.knockback = knockback;
		this.shotLife = shotLife;
		this.homing = homing;
		this.swarm = swarm;
		this.shotgun = shotgun;
		this.scattershot = scattershot;
		this.sticky = sticky;
		this.grenade = grenade;
		this.sword = sword;
		this.flamethrower = flamethrower;
		this.name = name;
		this.seed = seed;
		this.numBullets = numBullets;
	}
}
