package sonar.logistics.registries;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import net.minecraft.tileentity.TileEntity;
import sonar.core.utils.helpers.RegistryHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.render.InfoInteractionHandler;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.info.interaction.FluidInventoryInteraction;
import sonar.logistics.info.interaction.InventoryInteraction;
import sonar.logistics.info.interaction.StoredFluidInteraction;
import sonar.logistics.info.interaction.StoredStackInteraction;

public class InfoInteractionRegistry extends RegistryHelper<InfoInteractionHandler> {

	@Override
	public void register() {
		registerObject(new InventoryInteraction());
		registerObject(new StoredStackInteraction());
		registerObject(new FluidInventoryInteraction());
		registerObject(new StoredFluidInteraction());
	}

	@Override
	public String registeryType() {
		return "Info Interaction";
	}

	public InfoInteractionHandler getInteractionHandler(ILogicInfo info, ScreenType type, TileEntity te, TileEntity object) {
		for (InfoInteractionHandler handler : getObjects()) {
			try {
				Type subType = ((ParameterizedType) handler.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
				if (subType != null && info != null && subType == info.getClass()) {
					if (handler.canHandle(type, te, object)) {
						return handler;
					}
				}
			} catch (Exception exception) {
				Logistics.logger.error("FAILED TO RETRIEVE INTERACTION HANDLER", exception.getMessage());
			}
		}
		return null;
	}
}