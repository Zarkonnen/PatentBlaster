package com.zarkonnen;

import com.zarkonnen.catengine.Img;

public enum NonsensePatent {
	WRITING(null, "[bg=00000099]" + "Pat 3312890: A system for encoding language in shapes".toUpperCase()),
	LOVE(null, "[bg=00000099]" + "Pat 1908134: An emotional state comprising attraction and affection".toUpperCase()),
	COMMERCE(null, "[bg=00000099]" + "Pat 9123801: The exchange of goods for services".toUpperCase()),
	SOCKS(null, "[bg=00000099]" + "Pat 9180111: Foot coverings".toUpperCase()),
	BREATHING(null, "[bg=00000099]" + "Pat 1881244: a method of gas exchange in organisms".toUpperCase()),
	EYELIDS(null, "[bg=00000099]" + "Pat 10234555: Skin flaps for eye protection and lubrication".toUpperCase()),
	PERIODS(null, "[bg=00000099]" + "Pat 19812244: A system for denoting sentence boundaries using dots".toUpperCase()),
	DNA(null, "[bg=00000099]" + "Pat 19081214: Encoding information using a helix of base pairs".toUpperCase()),
	WASHING(null, "[bg=00000099]" + "Pat 3982901: Use of dihydrogen monoxide as a cleaning product".toUpperCase()),
	NAMES(null, "[bg=00000099]" + "Pat 6399113: Phoneme sequences for identity management".toUpperCase());
	
	public final Img img;
	public final String text;

	private NonsensePatent(Img img, String text) {
		this.img = PatentBlaster.NONPAT_IMGS.get(name());
		this.text = text;
	}
}
