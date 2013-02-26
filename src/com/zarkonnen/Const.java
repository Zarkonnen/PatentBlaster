package com.zarkonnen;

public class Const {
	public static double powerLvl(int power) {
		return PatentBlaster.difficultyLevel.base + power * PatentBlaster.difficultyLevel.linear + power * power * PatentBlaster.difficultyLevel.quadratic;
	}
	
	public static final double BASE_BARREL_HP = 10;
	public static final double BASE_HP = 8;
	public static final double BASE_HP_BONUS = BASE_HP * 0.4;
	public static final double BASE_DMG = 3;
	public static final double BASE_REGEN = 0.04 * BASE_HP / PatentBlaster.FPS; // 4% / second
}
