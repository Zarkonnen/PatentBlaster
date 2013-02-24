package com.zarkonnen;

import java.io.Serializable;

public class SoundRequest implements Serializable {
	public String sound;
	public double x;
	public double y;
	public double volume;

	public SoundRequest(String sound, double x, double y, double volume) {
		this.sound = sound;
		this.x = x;
		this.y = y;
		this.volume = volume;
	}
}
