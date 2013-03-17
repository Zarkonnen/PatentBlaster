package com.zarkonnen;

import java.io.Serializable;

public class SoundRequest implements Serializable, Comparable<SoundRequest> {
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

	@Override
	public int compareTo(SoundRequest t) {
		if (volume == volume) {
			return Double.compare(x * x + y * y, t.x * x + t.y * t.y);
		} else {
			return -Double.compare(volume, t.volume);
		}
	}
}
