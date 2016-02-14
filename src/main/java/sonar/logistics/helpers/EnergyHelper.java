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
import sonar.logistics.api.IdentifiedCoords;
import sonar.logistics.api.providers.EnergyHandler;
import sonar.logistics.api.providers.FluidProvider;
import sonar.logistics.common.tileentity.TileEntityBlockNode;
import sonar.logistics.info.types.StoredEnergyInfo;

public class EnergyHelper {

	public static List<StoredEnergyInfo> getEnergy(List<BlockCoords> coords) {
		List<StoredEnergyInfo> energyList = new ArrayList();
		List<EnergyHandler> handlers = Logistics.energyProviders.getObjects();

		for (BlockCoords coord : coords) {
			TileEntity target = coord.getTileEntity();
			if (target != null && target instanceof TileEntityBlockNode) {
				TileEntityBlockNode node = (TileEntityBlockNode) target;
				ForgeDirection dir = ForgeDirection.getOrientation(SonarHelper.invertMetadata(node.getBlockMetadata())).getOpposite();
				BlockCoords tileCoords = new BlockCoords(node.xCoord + dir.offsetX, node.yCoord + dir.offsetY, node.zCoord + dir.offsetZ);
				TileEntity energyTile = tileCoords.getTileEntity(node.getWorldObj());
				StoredEnergyStack stack = new StoredEnergyStack();
				for (EnergyHandler handler : handlers) {
					handler.getEnergyInfo(stack, energyTile, dir);
				}
				IdentifiedCoords iCoords = new IdentifiedCoords("", SonarHelper.createStackedBlock(energyTile.getBlockType(), energyTile.getBlockMetadata()), tileCoords);
				energyList.add(StoredEnergyInfo.createInfo(iCoords, stack));
			}
		}
		/*
		Collections.sort(energyList, new Comparator<StoredEnergyStack>() {
			public int compare(StoredEnergyStack str1, StoredEnergyStack str2) {
				if (str1.type > str2.type)
					return 1;
				if (str1.type == str2.type)
					return 0;
				return -1;
			}
		});
		*/
		return energyList;
	}
}
