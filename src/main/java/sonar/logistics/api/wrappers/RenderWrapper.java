package sonar.logistics.api.wrappers;

import net.minecraft.client.gui.FontRenderer;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;

public class RenderWrapper {

	/**used for rendering {@link StandardInfo} and sometimes for other Info Types to
	 * @param info the {@Info info} to render
	 * @param rend the Minecraft {@list FontRenderer}
	 * @param minX screen start X
	 * @param minY screen start Y
	 * @param maxX screen finish X
	 * @param maxY screen finish Y
	 * @param zOffset screen offset
	 * @param scale screen scaling
	 */
	public void renderStandardInfo(Info info, FontRenderer rend, float minX, float minY, float maxX, float maxY, float zOffset, float scale) {}

	/**renders a string in the centre of the screen
	 * @param string {@link String} to render
	 * @param minX screen start X
	 * @param minY screen start Y
	 * @param maxX screen finish X
	 * @param maxY screen finish Y
	 * @param zOffset screen offset
	 * @param scale screen scaling
	 */
	public void renderCenteredString(String string, float minX, float minY, float maxX, float maxY, float scale) {}

	/**get the screen scaling for a given size 
	 * @param sizing screen size
	 * @return scale
	 */
	public double getScale(int sizing) {
		return 1.0;
	}
}
