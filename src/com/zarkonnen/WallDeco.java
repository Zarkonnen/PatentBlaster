package com.zarkonnen;

import java.io.Serializable;

public class WallDeco implements Serializable {
	public WallDecoType type;
	public int x;
	public int y;

	public WallDeco(WallDecoType type, int x, int y) {
		this.type = type;
		this.x = x;
		this.y = y;
	}
}
