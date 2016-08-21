package sonar.logistics.api.wrappers;

import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.render.ScreenType;

public class RenderWrapper {

	/** used for rendering {@link DEADLogicInfo} and sometimes for other Info Types to
	 * 
	 * @param info the {@link DEADILogicInfo} to render
	 * @param minX screen start X
	 * @param minY screen start Y
	 * @param maxX screen finish X
	 * @param maxY screen finish Y
	 * @param zOffset screen offset
	 * @param type screen scaling */
	public void renderStandardInfo(LogicInfo info, float minX, float minY, float maxX, float maxY, float zOffset, ScreenType type) {
	}

	/** renders a string in the centre of the screen
	 * 
	 * @param string {@link String} to render
	 * @param minX screen start X
	 * @param minY screen start Y
	 * @param maxX screen finish X
	 * @param maxY screen finish Y
	 * @param zOffset screen offset
	 * @param type the {@link ScreenType} */
	public void renderCenteredString(String string, float x, float y, float width, float scale, int color) {
	}

	/** gets the appropriate scaling for the given {@link ScreenType}
	 * 
	 * @param type the {@link ScreenType}
	 * @return float value for scale */
	public float getScaling(ScreenType type) {
		return 120F;
	}

	/** get the screen scaling for a given size
	 * 
	 * @param sizing screen size
	 * @return scale */
	public double getScale(int sizing) {
		return 1.0;
	}
}
