package com.zarkonnen;

public class Util {
	public static double powerLvl(int power) {
		return 2 + power + 0.2 * power * power;
	}
	
	public static final double BASE_HP = 8;
	public static final double BASE_HP_BONUS = BASE_HP * 0.4;
	public static final double BASE_DMG = 3.5;
	public static final double BASE_REGEN = 0.04 * BASE_HP / PatentBlaster.FPS; // 4% / second
}
