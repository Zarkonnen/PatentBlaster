package com.zarkonnen;

import com.zarkonnen.catengine.Input;
import static com.zarkonnen.catengine.util.Utils.*;
import com.zarkonnen.trigram.Trigrams;
import java.util.List;

public class Preload {
	public static final List<String> SOUNDS_TO_PRELOAD = l(
			"STEEL", "ACID", "FIRE", "ICE", "empty",
			"hover",
			"squelch", "splt", "drop", "pickup",
			"fuse", "explode",
			"jarhit", "shatter",
			"ice_block", "ice_block_thaw", "on_fire",
			"shield", "shield_restore",
			"desquelch", "revenant", "regenerate", "resurrect", "final_form",
			"eat", "steal", "voice_1", "shish_kebab", "acid_rain", "its_snowing", "BBQ"
	);
	public static boolean preloadStarted;
	public static boolean preloadCompleted;
	
	public static void preload(Input in) {
		if (preloadStarted) { return; }
		preloadStarted = true;
		/*in.preloadSounds(SOUNDS_TO_PRELOAD, new Runnable() {
			@Override
			public void run() {
				preloadCompleted = true;
			}
		});*/
		preloadCompleted = true; // qqDPS
	}
	
	public static boolean allPreloaded() {
		return preloadCompleted && Names.namesLoaded() && PatentBlaster.musicStarted && Trigrams.TRIGRAMS != null;
	}
	
	public static String preloadStatus() {
		StringBuilder sb = new StringBuilder();
		if (!preloadCompleted) {
			sb.append("Caching sounds.\n");
		}
		if (!Names.namesLoaded()) {
			sb.append("Downloading random names.\n");
		}
		if (!PatentBlaster.musicStarted) {
			sb.append("Loading music.\n");
		}
		if (Trigrams.TRIGRAMS == null) {
			sb.append("Loading patent trigrams.\n");
		}
		return sb.length() == 0 ? "Reticulating splines." : sb.toString();
	}
}
