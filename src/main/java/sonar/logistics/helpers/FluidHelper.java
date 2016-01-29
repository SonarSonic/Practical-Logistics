package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.fluid.StoredFluidStack;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.providers.FluidProvider;
import sonar.logistics.common.tileentity.TileEntityBlockNode;

public class FluidHelper {

	public static List<StoredFluidStack> getFluids(List<BlockCoords> coords) {
		List<StoredFluidStack> fluidList = new ArrayList();
		List<FluidProvider> providers = Logistics.fluidProviders.getObjects();

		for (FluidProvider provider : providers) {
			for (BlockCoords coord : coords) {
				TileEntity target = coord.getTileEntity();
				if (target != null && target instanceof TileEntityBlockNode) {
					TileEntityBlockNode node = (TileEntityBlockNode) target;
					ForgeDirection dir = ForgeDirection.getOrientation(SonarHelper.invertMetadata(node.getBlockMetadata())).getOpposite();
					if (provider.canProvideFluids(node.getWorldObj(), node.xCoord + dir.offsetX, node.yCoord + dir.offsetY, node.zCoord + dir.offsetZ, dir)) {
						List<StoredFluidStack> info = new ArrayList();
						provider.getFluids(info, node.getWorldObj(), node.xCoord + dir.offsetX, node.yCoord + dir.offsetY, node.zCoord + dir.offsetZ, dir);
						for (StoredFluidStack fluid : info) {
							addFluidToList(fluidList, fluid);
						}
					}
				}
			}
		}
		Collections.sort(fluidList, new Comparator<StoredFluidStack>() {
			public int compare(StoredFluidStack str1, StoredFluidStack str2) {
				if (str1.stored < str2.stored)
					return 1;
				if (str1.stored == str2.stored)
					return 0;
				return -1;
			}
		});
		return fluidList;
	}

	public static void addFluidToList(List<StoredFluidStack> list, StoredFluidStack stack) {
		boolean added = false;
		int pos = 0;
		for (StoredFluidStack storedTank : list) {
			if (storedTank.equalStack(stack.fluid)) {
				list.get(pos).add(stack);
				added = true;
			}
			pos++;
		}
		if (!added) {
			list.add(stack);
		}
	}
}
