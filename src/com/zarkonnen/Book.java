package com.zarkonnen;

import com.zarkonnen.catengine.Draw;
import com.zarkonnen.catengine.util.Clr;

public class Book extends Wall {
	public static final Clr[] SPINES = { new Clr(41, 118, 48), new Clr(118, 41, 41), new Clr(118, 86, 41), new Clr(71, 118, 108), new Clr(118, 70, 70) };
	public static final String[] TITLES = { "[000000ff88]A", "[000000ff88]B", "[000000ff88]C", "[000000ff88]1", "[000000ff88]2", "[000000ff88]3", "[000000ff88]G\nO\nD", "[000000ff88]J\nA\nM", "[000000ff88]L\nA\nW", "[000000ff88]L\nA\nW", "[000000ff88]L\nA\nW", "[000000ff88]E\nY\nE" };
	
	String title;
	
	public Book(int x, int baseline, Level l) {
		super(x, baseline, 15, 45 + l.r.nextInt(20));
		y -= h;
		tint = SPINES[l.r.nextInt(SPINES.length)];
		title = TITLES[l.r.nextInt(TITLES.length)];
		destructible = true;
		initialHP = initialHP / 4 + 1;
		hp = initialHP;
	}
	
	@Override
	public void draw(Draw d, Level l, double scrollX, double scrollY) {
		super.draw(d, l, scrollX, scrollY);
		d.text(title, PatentBlaster.SMOUNT, x + 4 + scrollX, y + 2 + scrollY);
	}
}
