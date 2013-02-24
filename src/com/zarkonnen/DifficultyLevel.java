package com.zarkonnen;

public enum DifficultyLevel {
	EASY(0.5, 0.5, 0, 23, 8),
	NORMAL(1.5, 0.8, 0.02, 11, 3),
	HARD(2.0, 1.0, 0.2, 5, 0),
	BRUTAL(5.4, 0.1, 0.7, 3, 0);
	public final double base;
	public final double linear;
	public final double quadratic;
	public final int playerLevel;
	public final int shopBonus;

	private DifficultyLevel(double base, double linear, double quadratic, int playerLevel, int shopBonus) {
		this.base = base;
		this.linear = linear;
		this.quadratic = quadratic;
		this.playerLevel = playerLevel;
		this.shopBonus = shopBonus;
	}
}
