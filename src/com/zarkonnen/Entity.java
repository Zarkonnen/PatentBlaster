package com.zarkonnen;

import com.zarkonnen.catengine.Draw;
import com.zarkonnen.catengine.Img;
import com.zarkonnen.catengine.util.Clr;
import java.io.Serializable;

public abstract class Entity implements Serializable {
	public Img img = null;
	public Clr tint;
	public double x, y, w, h, dx, dy;
	public boolean popOnWorldHit = false;
	public boolean killMe = false;
	public double gravityMult = 1;
	public int ticksSinceBottom = 0;
	public int ticksSinceSide = 10000;
	public int leftPress = 0;
	public int rightPress = 0;
	public int bottomPress = 0;
	public boolean collides = true;
	
	public double bottomPressSpeedMult = 10.0;
	public int bottomInflateAmount = 1;
	
	public int inflateAmount = 7;
	public int pressAmount = 10;
	public int maxPress = 30;
	
	public void tick(Level l) {}
	public void draw(Draw d, Level l, double scrollX, double scrollY) {
		if (img != null) {
			d.blit(img, tint, x + scrollX, y + scrollY, w, h);
		} else {
			d.rect(tint, x + scrollX, y + scrollY, w, h);
		}
	}
}
