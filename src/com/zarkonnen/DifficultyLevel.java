package com.zarkonnen;

public enum DifficultyLevel {
	EASY(0.5, 0.5, 0, 23),
	NORMAL(1.5, 0.8, 0.02, 11),
	HARD(2.0, 1.0, 0.2, 5),
	BRUTAL(5.4, 0.1, 0.7, 3);
	public final double base;
	public final double linear;
	public final double quadratic;
	public final int playerLevel;

	private DifficultyLevel(double base, double linear, double quadratic, int playerLevel) {
		this.base = base;
		this.linear = linear;
		this.quadratic = quadratic;
		this.playerLevel = playerLevel;
	}
}
