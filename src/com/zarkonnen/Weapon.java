package com.zarkonnen;

import com.zarkonnen.catengine.util.Clr;
import java.util.Random;
import static com.zarkonnen.PatentBlaster.round;
import static com.zarkonnen.Util.*;

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
	
	public int img;
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
	public String name;
	
	public void tick() {
		if (reloadLeft > 0) { reloadLeft--; }
	}
	
	public double range() {
		return shotLife * shotSpeed;
	}
	
	public double dps() {
		return 1.0 * dmg / reload * PatentBlaster.FPS * (swarm ? 8 : 1);
	}
	
	public static Weapon make(long seed, int power, int numImages) {
		Random r = new Random(seed);
		Weapon w = new Weapon();
		w.element = Element.values()[r.nextInt(Element.values().length)];
		double dmg = BASE_DMG * w.element.dmgMult;
		w.reload = MIN_RELOAD + r.nextInt(MAX_RELOAD - MIN_RELOAD);
		w.reloadLeft = w.reload;
		dmg *= Math.pow(1.0 * w.reload / AVG_RELOAD, 1.4);// System.out.println("A: " + dmg);
		w.shotSpeed = MIN_SHOT_SPEED + r.nextDouble() * (MAX_SHOT_SPEED - MIN_SHOT_SPEED);
		dmg /= Math.pow(w.shotSpeed / AVG_SHOT_SPEED, 0.3);// System.out.println("B: " + dmg);
		w.jitter = r.nextDouble() * (2.0 / (power + 5));
		dmg *= (0.8 + w.jitter);// System.out.println("C: " + dmg);
		int range = MIN_RANGE + r.nextInt(MAX_RANGE - MIN_RANGE);
		w.shotLife = (int) (range / w.shotSpeed);
		dmg /= Math.sqrt(1.0 * range / AVG_RANGE);// System.out.println("D: " + dmg);
		w.homing = r.nextInt(8) == 0;
		w.swarm = w.homing && r.nextInt(2) == 0;
		w.shotSize = MIN_SHOT_SIZE + (AVG_SHOT_SIZE - MIN_SHOT_SIZE) * dmg / BASE_DMG;
		w.knockback = !w.swarm && r.nextInt(20) == 0;
		if (w.homing) { w.reload = w.reload * 3 / 2; dmg /= 2; w.shotLife = w.shotLife * 3 / 2; }// System.out.println("E: " + dmg);
		if (w.swarm) { dmg /= 8; w.shotSize = w.shotSize / 4 + 1; }// System.out.println("F: " + dmg);
		if (w.knockback) { dmg *= 0.8; }// System.out.println("G: " + dmg);
		w.tint = w.element.tint;
		w.name = Names.pick(r);
		w.img = r.nextInt(numImages);
		w.dmg = Math.max(1, (int) Math.ceil(dmg * powerLvl(power)));
		w.shotSpeed *= w.element.speedMult;
		/*w.element = Element.values()[r.nextInt(Element.values().length)];
		w.dmg = (int) ((power * 8 + r.nextInt(4) * Math.pow(power, 1.05) * 4) * w.element.dmgMult) + 7;
		w.reload = (900 / (5 + r.nextInt(power + 5) + power / 4)) * w.dmg / (power * 3 * 8) + 10;
		w.reloadLeft = w.reload;
		w.shotSpeed = 4 + (r.nextDouble() * 3) * w.element.speedMult;
		w.jitter = r.nextDouble() / power / 2;
		w.shotSize = Math.max(3, Math.min(8, w.dmg / Math.pow(power, 1.05) / 3));
		w.img = r.nextInt(numImages);
		w.tint = w.element.tint;
		w.shotLife = 100 + r.nextInt(100);
		w.name = Names.pick(r);
		w.homing = r.nextInt(8) == 0;
		w.swarm = w.homing && r.nextInt(3) == 0;
		if (w.homing) { w.reload = w.reload * 3 / 2; w.dmg /= 2; w.shotLife = w.shotLife * 3 / 2; }
		if (w.swarm) { w.dmg /= 6; w.shotSize = w.shotSize / 4 + 1; }
		w.knockback = !w.swarm && r.nextInt(20) == 0;*/
		return w;
	}
	
	@Override
	public String desc() {
		StringBuilder sb = new StringBuilder();
		sb.append(" ").append(name.toUpperCase()).append("");
		sb.append(", [").append(element.tint).append("]").append(element.name()).append("[] Weapon\n");
		sb.append("  Damage: ").append(dmg * (swarm ? 8 : 1)).append("\n");
		sb.append("  Reload: ").append(round(reload * 1.0 / PatentBlaster.FPS, 2)).append("sec (").append(round(dps(), 0)).append(" DPS)\n");
		sb.append("  Shot Speed: ").append(round(shotSpeed, 1)).append("\n");
		sb.append("  Range: ").append(round(range(), 0)).append("\n");
		sb.append("  Inaccuracy: ").append(round(jitter * 100, 0)).append("\n");
		sb.append("  Shot Size: ").append(round(shotSize, 0)).append("\n");
		if (knockback) {
			sb.append("  Knockback.\n");
		}
		if (homing) {
			if (swarm) {
				sb.append("  Homing Swarm.\n");
			} else {
				sb.append("  Homing.\n");
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
		hash = 59 * hash + this.img;
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
		t.dmg = Math.max(1, dmg / 4);
		t.element = element;
		t.img = img;
		t.jitter = jitter * 0.7;
		t.name = "Tiny " + name;
		t.reload = reload;
		t.shotLife = shotLife / 2;
		t.shotSize = shotSize / 2 + 1;
		t.tint = tint;
		t.shotSpeed = shotSpeed;
		return t;
	}
}
