package sonar.logistics.integration.multipart;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.SonarTilePart;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.Info;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.api.connecting.IInfoReader;
import sonar.logistics.client.renderers.RenderDisplayScreen;
import sonar.logistics.common.tileentity.TileEntityInventoryReader;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.registries.BlockRegistry;
import sonar.logistics.registries.ItemRegistry;
import codechicken.lib.vec.Cuboid6;

public class DisplayScreenPart extends SonarTilePart implements IDataConnection {

	public Info info;
	
	public DisplayScreenPart() {
		super();
	}

	public DisplayScreenPart(int meta) {
		super(meta);
	}

	@Override
	public Cuboid6 getBounds() {
		float f = 0.28125F;
		float f1 = 0.78125F;
		float f2 = 0.0F;
		float f3 = 1.0F;
		float f4 = 0.125F;
		if (meta == 2) {
			return new Cuboid6(f2, f, 1.0F - f4, f3, f1, 1.0F);
		}

		if (meta == 3) {
			return new Cuboid6(f2, f, 0.0F, f3, f1, f4);
		}

		if (meta == 4) {
			return new Cuboid6(1.0F - f4, f, f2, 1.0F, f1, f3);
		}

		if (meta == 5) {
			return new Cuboid6(0.0F, f, f2, f4, f1, f3);
		}
		return new Cuboid6(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public ItemStack pickItem(MovingObjectPosition hit) {
		return new ItemStack(ItemRegistry.displayScreen);
	}

	@Override
	public Iterable<ItemStack> getDrops() {
		return Arrays.asList(new ItemStack(ItemRegistry.displayScreen));
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.displayScreen;
	}

	@Override
	public String getType() {
		return "Screen Part";
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			if (nbt.hasKey("currentInfo")) {
				info = InfoHelper.readInfo(nbt.getCompoundTag("currentInfo"));
			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			if (info != null) {
				NBTTagCompound infoTag = new NBTTagCompound();
				InfoHelper.writeInfo(infoTag, info);
				nbt.setTag("currentInfo", infoTag);
			}
		}
	}

	public void update() {
		if (!world().isRemote) {
			this.updateData(ForgeDirection.getOrientation(meta));
		}
	}

	// IDATACONNECTION

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return dir.equals(ForgeDirection.getOrientation(meta).getOpposite());
	}

	@Override
	public void updateData(ForgeDirection dir) {
		Object target = CableHelper.getConnectedTile(tile(), dir.getOpposite());
		target = FMPHelper.checkObject(target);
		if (target == null) {
			return;
		} else {
			if (target instanceof IInfoReader) {
				IInfoReader infoReader= (IInfoReader) target;
				if(infoReader.currentInfo() != null && infoReader.getSecondaryInfo()!=null){
					this.info = InfoHelper.combineData(infoReader.currentInfo(), infoReader.getSecondaryInfo());
				}
				else if (infoReader.currentInfo() != null) {
					this.info = infoReader.currentInfo();
				} else if (this.info != null) {
					this.info.emptyData();
				}
			} else if(target instanceof TileEntityInventoryReader){
				TileEntityInventoryReader infoNode = (TileEntityInventoryReader) target;
				if (infoNode.currentInfo() != null) {
					this.info = infoNode.currentInfo();
				} else if (this.info != null) {
					this.info.emptyData();
				}
			}
			else if (target instanceof IDataConnection) {
				IDataConnection infoNode = (IDataConnection) target;
				if (infoNode.currentInfo() != null) {
					this.info = infoNode.currentInfo();
				} else if (this.info != null) {
					this.info.emptyData();
				}
			}
		}
	}

	@Override
	public Info currentInfo() {
		return info;
	}

	
	@Override
	public Object getSpecialRenderer() {
		return new RenderDisplayScreen();
	}

}
