package com.zarkonnen;

import com.zarkonnen.catengine.Draw;
import com.zarkonnen.catengine.util.Pt;

public class FloatingText extends Entity {
	public String text;
	public int life = PatentBlaster.FPS * 2;

	public FloatingText(String text, double x, double y) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.gravityMult = -0.3;
		this.dy = -0.4;
		this.collides = false;
	}
	
	@Override
	public void tick(Level l) {
		if (life-- <= 0) {
			killMe = true;
		}
	}
	
	@Override
	public void draw(Draw d, Level l, double scrollX, double scrollY) {
		if (w == 0) {
			Pt sz = d.textSize(text, PatentBlaster.FOUNT);
			x = x - sz.x / 2;
			y = y - sz.y / 2;
			w = sz.x;
			h = sz.y;
		}
		d.text(((l.tick / 10) % 2 == 0 ? "[ffaaaa]" : "") + text, PatentBlaster.FOUNT, x + scrollX, y + scrollY);
	}
}
