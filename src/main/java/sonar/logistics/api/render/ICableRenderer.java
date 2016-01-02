package sonar.logistics.api.render;

import net.minecraftforge.common.util.ForgeDirection;

public interface ICableRenderer {

	/**used by the client to check if the cable can connect, if it can it will render the connection*/
	public boolean canRenderConnection(ForgeDirection dir);
}
