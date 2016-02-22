package sonar.logistics.api.render;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.connecting.CableType;

/**used by tiles which render cables, this includes Cables themselves*/
public interface ICableRenderer {

	/**used by the client to check if the cable can connect, if it can it will render the connection*/
	public CableType canRenderConnection(ForgeDirection dir);
}
