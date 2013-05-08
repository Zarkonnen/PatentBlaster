package com.zarkonnen;

public enum DifficultyLevel {
	/*EASY(0.5, 0.5, 0.001, 30, 10),
	MEDIUM(0.5, 0.5, 0.01, 23, 8),
	HARD(1.5, 0.8, 0.02, 11, 4),
	BRUTAL(2.0, 1.0, 0.2, 5, 1),
	IMPOSSIBLE(5.4, 0.1, 0.7, 3, 0);*/
	EASY(0.2, 0.2, 0.05, 20, 5),
	MEDIUM(0.35, 0.35, 0.2, 12, 3),
	HARD(0.5, 0.5, 0.3, 9, 8),
	BRUTAL(0.5, 0.5, 0.3, 5, 2),
	IMPOSSIBLE(0.5, 0.3, 0.5, 3, 0);
	
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
