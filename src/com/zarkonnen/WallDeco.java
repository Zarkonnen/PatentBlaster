package com.zarkonnen;

import com.zarkonnen.catengine.Img;
import java.io.Serializable;

public class WallDeco implements Serializable {
	public WallDecoType type;
	public int x;
	public int y;
	public String text;
	public Img img;

	public WallDeco(WallDecoType type, int x, int y) {
		this.type = type;
		this.x = x;
		this.y = y;
	}
}
