package com.zarkonnen;

public enum BuyScreenArgument {
	MORE_CREATURES("More creatures."),
	MORE_WEAPONS("More weapons."),
	MORE_POWERS("More enemy powers."),
	NOT_EXPLODING("Not exploding at the end of level 3!"),
	BARRELS("Barrels o'mysterious substances.");
	//SUPPORT_ME("Feed the developer.");
	
	public final String text;

	private BuyScreenArgument(String text) {
		this.text = text;
	}
}
