package sonar.logistics.api.wrappers;

import net.minecraft.client.gui.FontRenderer;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.render.ScreenType;

public class RenderWrapper {

	/** used for rendering {@link LogicInfo} and sometimes for other Info Types to
	 * @param info the {@link ILogicInfo} to render
	 * @param rend the Minecraft {@link FontRenderer}
	 * @param minX screen start X
	 * @param minY screen start Y
	 * @param maxX screen finish X
	 * @param maxY screen finish Y
	 * @param zOffset screen offset
	 * @param type screen scaling */
	public void renderStandardInfo(ILogicInfo info, FontRenderer rend, float minX, float minY, float maxX, float maxY, float zOffset, ScreenType type) {
	}

	/** renders a string in the centre of the screen
	 * @param string {@link String} to render
	 * @param minX screen start X
	 * @param minY screen start Y
	 * @param maxX screen finish X
	 * @param maxY screen finish Y
	 * @param zOffset screen offset
	 * @param type the {@link ScreenType} */
	public void renderCenteredString(String string, float minX, float minY, float maxX, float maxY, ScreenType type) {
	}

	/** gets the appropriate scaling for the given {@link ScreenType}
	 * @param type the {@link ScreenType}
	 * @return float value for scale */
	public float getScaling(ScreenType type) {
		return 120F;
	}

	/** get the screen scaling for a given size
	 * @param sizing screen size
	 * @return scale */
	public double getScale(int sizing) {
		return 1.0;
	}
}
