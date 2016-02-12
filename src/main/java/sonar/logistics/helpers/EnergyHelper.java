package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cofh.api.energy.IEnergyProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.energy.StoredEnergyStack;
import sonar.core.fluid.StoredFluidStack;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.providers.EnergyHandler;
import sonar.logistics.api.providers.FluidProvider;
import sonar.logistics.common.tileentity.TileEntityBlockNode;

public class EnergyHelper {

	public static List<StoredEnergyStack> getEnergy(List<BlockCoords> coords) {
		List<StoredEnergyStack> energyList = new ArrayList();
		List<EnergyHandler> handlers = Logistics.energyProviders.getObjects();

		for (EnergyHandler handler : handlers) {
			for (BlockCoords coord : coords) {
				TileEntity target = coord.getTileEntity();
				if (target != null && target instanceof TileEntityBlockNode) {
					TileEntityBlockNode node = (TileEntityBlockNode) target;
					ForgeDirection dir = ForgeDirection.getOrientation(SonarHelper.invertMetadata(node.getBlockMetadata())).getOpposite();
					TileEntity energyTile = node.getWorldObj().getTileEntity(node.xCoord + dir.offsetX, node.yCoord + dir.offsetY, node.zCoord + dir.offsetZ);
					
					//if (energyTile != null && handler.canProvideInfo(energyTile, dir)) {
						List<StoredEnergyStack> info = new ArrayList();
						handler.getEnergyInfo(info, energyTile, dir);
						for (StoredEnergyStack energy : info) {
							addEnergyToList(energyList, energy);
						}
					//}
				}
			}
		}
		Collections.sort(energyList, new Comparator<StoredEnergyStack>() {
			public int compare(StoredEnergyStack str1, StoredEnergyStack str2) {
				if (str1.type > str2.type)
					return 1;
				if (str1.type == str2.type)
					return 0;
				return -1;
			}
		});
		return energyList;
	}

	public static void addEnergyToList(List<StoredEnergyStack> list, StoredEnergyStack stack) {
		boolean added = false;
		int pos = 0;
		for (StoredEnergyStack storedTank : list) {
			if (storedTank.type==stack.type) {
				list.get(pos).add(new StoredEnergyStack(stack.type, 1, stack.stored/stack.rfModifier, stack.capacity/stack.rfModifier));
				added = true;
			}
			pos++;
		}
		if (!added) {
			list.add(stack);
		}
	}
}
