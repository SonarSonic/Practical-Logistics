package sonar.logistics.network;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import sonar.core.integration.fmp.FMPHelper;
import sonar.logistics.Logistics;
import sonar.logistics.client.gui.*;
import sonar.logistics.common.containers.*;
import sonar.logistics.common.tileentity.*;
import sonar.logistics.integration.multipart.InfoReaderPart;
import sonar.logistics.network.packets.*;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;

public class LogisticsCommon implements IGuiHandler {

	private static final Map<String, NBTTagCompound> extendedEntityData = new HashMap<String, NBTTagCompound>();

	public static void registerPackets() {
		Logistics.network.registerMessage(PacketProviders.Handler.class, PacketProviders.class, 0, Side.CLIENT);
		Logistics.network.registerMessage(PacketInfoBlock.Handler.class, PacketInfoBlock.class, 1, Side.SERVER);
		Logistics.network.registerMessage(PacketDataEmitters.Handler.class, PacketDataEmitters.class, 2, Side.CLIENT);
		Logistics.network.registerMessage(PacketDataReceiver.Handler.class, PacketDataReceiver.class, 3, Side.SERVER);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		Object entity = world.getTileEntity(x, y, z);
		entity = FMPHelper.checkObject(entity);
		if (entity != null) {
			switch (ID) {
			case LogisticsGui.infoNode:
				if (entity instanceof TileEntityInfoReader) {
					return new ContainerInfoNode((TileEntityInfoReader) entity);
				}
				if (entity instanceof InfoReaderPart) {
					return new ContainerInfoNode.Multipart((InfoReaderPart) entity);
				}
			case LogisticsGui.dataReceiver:
				if (entity instanceof TileEntityDataReceiver) {
					return new ContainerDataReceiver((TileEntityDataReceiver) entity, player.inventory);
				}

			case LogisticsGui.dataEmitter:
				if (entity instanceof TileEntityDataEmitter) {
					return new ContainerEmptySync((TileEntityDataEmitter) entity);
				}

			case LogisticsGui.inventoryReader:
				if (entity instanceof TileEntityInventoryReader) {
					return new ContainerInventoryReader((TileEntityInventoryReader) entity, player.inventory);
				}

			case LogisticsGui.redstoneSignaller:
				if (entity instanceof TileEntityRedstoneSignaller) {
					return new ContainerEmptySync((TileEntityRedstoneSignaller) entity);
				}
			case LogisticsGui.dataModifier:
				if (entity instanceof TileEntityDataModifier) {
					return new ContainerEmptySync((TileEntityDataModifier) entity);
				}
			case LogisticsGui.infoCreator:
				if (entity instanceof TileEntityInfoCreator) {
					return new ContainerEmptySync((TileEntityInfoCreator) entity);
				}
			case LogisticsGui.hammer:
				if (entity instanceof TileEntityHammer) {
					return new ContainerHammer(player.inventory, (TileEntityHammer) entity);
				}
			case LogisticsGui.entityNode:
				if (entity instanceof TileEntityEntityNode) {
					return new ContainerEmptySync((TileEntityEntityNode) entity);
				}
			}

		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

		Object entity = world.getTileEntity(x, y, z);
		entity = FMPHelper.checkObject(entity);

		if (entity != null) {
			switch (ID) {
			case LogisticsGui.infoNode:
				if (entity instanceof TileEntityInfoReader) {
					return new GuiInfoNode.Normal((TileEntityInfoReader) entity);
				}
				if (entity instanceof InfoReaderPart) {
					return new GuiInfoNode.Multipart((InfoReaderPart) entity);
				}
			case LogisticsGui.dataReceiver:
				if (entity instanceof TileEntityDataReceiver) {
					return new GuiDataReceiver(player.inventory, (TileEntityDataReceiver) entity);
				}
			case LogisticsGui.dataEmitter:
				if (entity instanceof TileEntityDataEmitter) {
					return new GuiRenameEmitter.DataEmitter((TileEntityDataEmitter) entity);
				}
			case LogisticsGui.inventoryReader:
				if (entity instanceof TileEntityInventoryReader) {
					return new GuiInventoryReader.Normal((TileEntityInventoryReader) entity, player.inventory);
				}
			case LogisticsGui.redstoneSignaller:
				if (entity instanceof TileEntityRedstoneSignaller) {
					return new GuiRedstoneSignaller.RedstoneSignaller((TileEntityRedstoneSignaller) entity);
				}
			case LogisticsGui.dataModifier:
				if (entity instanceof TileEntityDataModifier) {
					return new GuiDataModifier.Normal((TileEntityDataModifier) entity);
				}
			case LogisticsGui.infoCreator:
				if (entity instanceof TileEntityInfoCreator) {
					return new GuiInfoCreator.Normal((TileEntityInfoCreator) entity);
				}
			case LogisticsGui.hammer:
				if (entity instanceof TileEntityHammer) {
					return new GuiHammer(player.inventory, (TileEntityHammer) entity);
				}
			case LogisticsGui.entityNode:
				if (entity instanceof TileEntityEntityNode) {
					return new GuiEntityNode.EntityNode((TileEntityEntityNode) entity);
				}
			}

		}

		return null;
	}

	public void registerRenderThings() {

	}

}
