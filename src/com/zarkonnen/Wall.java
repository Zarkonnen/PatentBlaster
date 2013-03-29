package com.zarkonnen;

import com.zarkonnen.catengine.util.Clr;

public class Wall extends Entity {
	public Wall(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.tint = Clr.GREY;
	}
}
