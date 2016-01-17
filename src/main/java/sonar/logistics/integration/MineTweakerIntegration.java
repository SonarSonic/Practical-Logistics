package sonar.logistics.integration;

import minetweaker.MineTweakerAPI;

public class MineTweakerIntegration {
	
	public static void integrate() {
		MineTweakerAPI.registerClass(HammerHandler.class);
	}
}
