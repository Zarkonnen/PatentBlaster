package com.zarkonnen;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Grids {
	public static final int GRID_SZ = 10;
	static boolean[][][] L_GRID = new boolean[PatentBlaster.NUM_IMAGES][GRID_SZ][GRID_SZ];
	static boolean[][][] R_GRID = new boolean[PatentBlaster.NUM_IMAGES][GRID_SZ][GRID_SZ];
	
	static {
		try {
			for (int r = 0; r < 2; r++) {
				boolean[][][] grids = r == 0 ? L_GRID : R_GRID;
				String postfix = r == 0 ? ".png.grid.txt" : "r.png.grid.txt";
				for (int i = 0; i < PatentBlaster.NUM_IMAGES; i++) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(Grids.class.getResourceAsStream("images/units/" + i + postfix)));
					String line = reader.readLine();
					reader.close();
					for (int y = 0; y < GRID_SZ; y++) {
						for (int x = 0; x < GRID_SZ; x++) {
							grids[i][y][x] = line.charAt(y * GRID_SZ + x) == '1';
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean[][] get(int img, boolean flipped) {
		return (flipped ? R_GRID : L_GRID)[img];
	}
}
