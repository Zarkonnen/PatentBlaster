package com.zarkonnen;

import com.zarkonnen.catengine.util.Clr;
import java.util.Random;
import static com.zarkonnen.PatentBlaster.round;
import static com.zarkonnen.Util.*;
import com.zarkonnen.catengine.Img;

public class Weapon implements HasDesc {
	public static final int MIN_RELOAD = PatentBlaster.FPS / 4;
	public static final int MAX_RELOAD = PatentBlaster.FPS * 2;
	public static final int AVG_RELOAD = MIN_RELOAD / 2 + MAX_RELOAD / 2;
	
	public static final double MIN_SHOT_SPEED = 4.5;
	public static final double MAX_SHOT_SPEED = 8;
	public static final double AVG_SHOT_SPEED = MIN_SHOT_SPEED / 2 + MAX_SHOT_SPEED / 2;
	
	public static final int MIN_RANGE = 300;
	public static final int MAX_RANGE = 800;
	public static final int AVG_RANGE = MIN_RANGE / 2 + MAX_RANGE / 2;
	
	public static final int MIN_SHOT_SIZE = 3;
	public static final int AVG_SHOT_SIZE = 5;
	
	public int imgIndex;
	public Img img;
	public Clr tint;
	public Element element;
	public int dmg;
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
	public int numBullets = 1;
	public String name;
	public long seed;
	public int clickEmptyTimer;
	
	public void tick() {
		if (reloadLeft > 0) { reloadLeft--; } else { clickEmptyTimer = 0; }
		if (clickEmptyTimer > 0) { clickEmptyTimer--; }
	}
	
	public double range() {
		return shotLife * shotSpeed;
	}
	
	public double dps() {
		return dmg * 1.0 / reload * PatentBlaster.FPS * numBullets;
	}
	
	public static Weapon make(long seed, int power, int numImages) {
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
		w.knockback = !w.swarm && !w.shotgun && r.nextInt(30) == 0;
		if (w.homing) { w.reload = w.reload * 3 / 2; dmg /= 2; w.shotLife = w.shotLife * 3 / 2; }
		if (w.swarm) { dmg /= 8; w.shotSize = w.shotSize / 4 + 1; w.numBullets = 8; }
		if (w.knockback) { dmg *= 0.8; }
		if (w.shotgun) { w.reload = w.reload * 3 / 2; dmg /= 4; w.shotLife *= 0.35; w.shotSize = w.shotSize / 2 + 1; w.numBullets = 8; w.jitter += 0.3; }
		w.tint = w.element.tint;
		w.name = Names.pick(r);
		w.imgIndex = r.nextInt(numImages);
		w.img = PatentBlaster.CREATURE_IMGS.get(PatentBlaster.IMG_NAMES[w.imgIndex]);
		w.dmg = Math.max(1, (int) Math.ceil(dmg * powerLvl(power)));
		w.shotSpeed *= w.element.speedMult;
		return w;
	}
	
	String accuracy() {
		if (swarm) { return ""; }
		if (jitter == 0) { return "Perfect accuracy.\n"; }
		if (jitter < 2 * Math.PI / 180) { return "Highly accurate (+-" + round(jitter * 180 / Math.PI, 1) + " degrees)\n"; }
		if (jitter < 6 * Math.PI / 180) { return "Accurate (+-" + round(jitter * 180 / Math.PI, 0) + " degrees)\n"; }
		if (jitter < 9 * Math.PI / 180) { return ""; }
		if (jitter < 15 * Math.PI / 180) { return "Inaccurate (+-" + round(jitter * 180 / Math.PI, 0) + " degrees)\n"; }
		return "Highly inaccurate (+-" + round(jitter * 180 / Math.PI, 0) + " degrees)\n";
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
		sb.append(is).append(dmg * numBullets);
		sb.append(" damage every ").append(round(reload * 1.0 / PatentBlaster.FPS, 2)).append("sec (").append(round(dps(), 0)).append(" DPS)\n");
		sb.append(is).append(round(shotSpeed * PatentBlaster.FPS, 0));
		sb.append(" px/sec shot speed, ").append(round(range(), 0)).append(" px range\n");
		sb.append(accuracy().length() == 0 ? "" : is).append(accuracy());
		//sb.append("  Shot Size: ").append(round(shotSize, 0)).append("\n");
		if (knockback) {
			sb.append(is).append("Knockback.\n");
		}
		if (shotgun) {
			sb.append(is).append("Shotgun.\n");
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
		return element == Element.STEEL;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 59 * hash + this.img.hashCode();
		hash = 59 * hash + (this.tint != null ? this.tint.hashCode() : 0);
		hash = 59 * hash + (this.element != null ? this.element.hashCode() : 0);
		hash = 59 * hash + this.dmg;
		hash = 59 * hash + this.reload;
		hash = 59 * hash + (int) (Double.doubleToLongBits(this.shotSpeed) ^ (Double.doubleToLongBits(this.shotSpeed) >>> 32));
		hash = 59 * hash + (int) (Double.doubleToLongBits(this.jitter) ^ (Double.doubleToLongBits(this.jitter) >>> 32));
		hash = 59 * hash + (int) (Double.doubleToLongBits(this.shotSize) ^ (Double.doubleToLongBits(this.shotSize) >>> 32));
		hash = 59 * hash + (this.knockback ? 1 : 0);
		hash = 59 * hash + this.shotLife;
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
		if (this.img != other.img) {
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
		return true;
	}

	public Weapon makeTinyVersion() {
		Weapon t = new Weapon();
		t.dmg = Math.max(1, dmg / 3);
		t.element = element;
		t.img = img;
		t.jitter = jitter * 0.7;
		t.name = "Tiny " + name;
		t.reload = reload;
		t.shotLife = shotLife * 2 / 3;
		t.shotSize = shotSize / 2 + 1;
		t.tint = tint;
		t.shotSpeed = shotSpeed;
		return t;
	}
}
