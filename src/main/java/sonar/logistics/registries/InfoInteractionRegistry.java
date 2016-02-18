package sonar.logistics.registries;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import net.minecraft.tileentity.TileEntity;
import sonar.core.utils.helpers.NBTRegistryHelper;
import sonar.core.utils.helpers.RegistryHelper;
import sonar.logistics.api.EntityInfo;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.render.InfoInteractionHandler;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.info.interaction.FluidInventoryInteraction;
import sonar.logistics.info.interaction.InventoryInteraction;
import sonar.logistics.info.interaction.StoredFluidInteraction;
import sonar.logistics.info.interaction.StoredStackInteraction;
import sonar.logistics.info.types.BlockCoordsInfo;
import sonar.logistics.info.types.CategoryInfo;
import sonar.logistics.info.types.FluidInfo;
import sonar.logistics.info.types.FluidInventoryInfo;
import sonar.logistics.info.types.FluidStackInfo;
import sonar.logistics.info.types.InventoryInfo;
import sonar.logistics.info.types.ManaInfo;
import sonar.logistics.info.types.ProgressInfo;
import sonar.logistics.info.types.StoredEnergyInfo;
import sonar.logistics.info.types.StoredStackInfo;
import sonar.logistics.info.types.ThaumcraftAspectInfo;

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

	public InfoInteractionHandler getInteractionHandler(Info info, ScreenType type, TileEntity te, TileEntity object) {
		for (InfoInteractionHandler handler : getObjects()) {
			Type subType = ((ParameterizedType) handler.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			if (subType.getClass() != null && subType == info.getClass()) {
				if (handler.canHandle(type, te, object)) {
					return handler;
				}
			}
		}
		return null;
	}
}