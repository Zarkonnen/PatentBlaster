package com.zarkonnen;

import com.zarkonnen.catengine.Input;
import static com.zarkonnen.catengine.util.Utils.*;
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
	public static boolean preloaded;
	
	public static void preload(Input in) {
		if (preloaded) { return; }
		preloaded = true;
		in.preloadSounds(SOUNDS_TO_PRELOAD);
	}
}
