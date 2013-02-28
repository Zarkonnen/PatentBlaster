package com.zarkonnen;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Grids {
	public static final int GRID_SZ = 10;
	static final HashMap<String, boolean[][]> L_GRID = new HashMap<String, boolean[][]>();
	static final HashMap<String, boolean[][]> R_GRID = new HashMap<String, boolean[][]>();
	
	static {
		try {
			for (int i = 0; i < PatentBlaster.NUM_IMAGES; i++) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(Grids.class.getResourceAsStream("images/grids/" + PatentBlaster.IMG_NAMES[i] + ".txt")));
				String line = reader.readLine();
				reader.close();
				boolean[][] lGrid = new boolean[GRID_SZ][GRID_SZ];
				boolean[][] rGrid = new boolean[GRID_SZ][GRID_SZ];
				for (int y = 0; y < GRID_SZ; y++) {
					for (int x = 0; x < GRID_SZ; x++) {
						lGrid[y][x] = line.charAt(y * GRID_SZ + x) == '1';
						rGrid[y][GRID_SZ - x - 1] = lGrid[y][x];
					}
				}
				L_GRID.put(PatentBlaster.IMG_NAMES[i], lGrid);
				R_GRID.put(PatentBlaster.IMG_NAMES[i], rGrid);
			}
		} catch (Exception e) {
			e.printStackTrace(PatentBlaster.ERR_STREAM);
		}
	}
	
	public static boolean[][] get(String img, boolean flipped) {
		return (flipped ? R_GRID : L_GRID).get(img);
	}
}
