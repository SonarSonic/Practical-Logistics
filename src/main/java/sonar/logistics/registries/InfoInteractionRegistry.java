package sonar.logistics.registries;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import net.minecraft.tileentity.TileEntity;
import sonar.core.helpers.RegistryHelper;
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

	public InfoInteractionHandler getInteractionHandler(ILogicInfo info, ScreenType type, TileEntity te) {
		for (InfoInteractionHandler handler : getObjects()) {
			Class handlerClass = handler.getClass();
			if (handlerClass != null) {
				Type subType = ((ParameterizedType) handlerClass.getGenericSuperclass()).getActualTypeArguments()[0];
				if (subType != null && info != null && subType == info.getClass()) {
					return handler;
				}
			}
		}
		return null;
	}
}