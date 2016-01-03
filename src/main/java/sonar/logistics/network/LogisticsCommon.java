package sonar.logistics.network;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.ITileHandler;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.logistics.Logistics;
import sonar.logistics.client.gui.GuiDataModifier;
import sonar.logistics.client.gui.GuiDataReceiver;
import sonar.logistics.client.gui.GuiEntityNode;
import sonar.logistics.client.gui.GuiHammer;
import sonar.logistics.client.gui.GuiInfoCreator;
import sonar.logistics.client.gui.GuiInfoReader;
import sonar.logistics.client.gui.GuiInventoryReader;
import sonar.logistics.client.gui.GuiRedstoneSignaller;
import sonar.logistics.client.gui.GuiRenameEmitter;
import sonar.logistics.common.containers.ContainerDataReceiver;
import sonar.logistics.common.containers.ContainerEmptySync;
import sonar.logistics.common.containers.ContainerHammer;
import sonar.logistics.common.containers.ContainerInfoNode;
import sonar.logistics.common.containers.ContainerInventoryReader;
import sonar.logistics.common.handlers.DataModifierHandler;
import sonar.logistics.common.handlers.InfoCreatorHandler;
import sonar.logistics.common.handlers.InfoReaderHandler;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import sonar.logistics.common.tileentity.TileEntityDataEmitter;
import sonar.logistics.common.tileentity.TileEntityDataModifier;
import sonar.logistics.common.tileentity.TileEntityDataReceiver;
import sonar.logistics.common.tileentity.TileEntityEntityNode;
import sonar.logistics.common.tileentity.TileEntityHammer;
import sonar.logistics.common.tileentity.TileEntityInfoCreator;
import sonar.logistics.common.tileentity.TileEntityInfoReader;
import sonar.logistics.common.tileentity.TileEntityInventoryReader;
import sonar.logistics.common.tileentity.TileEntityRedstoneSignaller;
import sonar.logistics.integration.multipart.DataModifierPart;
import sonar.logistics.integration.multipart.InfoCreatorPart;
import sonar.logistics.integration.multipart.InfoReaderPart;
import sonar.logistics.integration.multipart.InventoryReaderPart;
import sonar.logistics.network.packets.PacketDataEmitters;
import sonar.logistics.network.packets.PacketDataReceiver;
import sonar.logistics.network.packets.PacketInfoBlock;
import sonar.logistics.network.packets.PacketProviders;
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
		TileEntity tile = world.getTileEntity(x, y, z);
		Object entity = FMPHelper.checkObject(tile);
		if (entity != null) {
			switch (ID) {
			case LogisticsGui.infoNode:
				if (entity instanceof TileEntityInfoReader || entity instanceof InfoReaderPart) {
					TileHandler handler = FMPHelper.getHandler(tile);
					if (handler != null && handler instanceof InfoReaderHandler)
						return new ContainerInfoNode((InfoReaderHandler) handler, tile);
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
				if (entity instanceof TileEntityInventoryReader || entity instanceof InventoryReaderPart) {
					TileHandler handler = FMPHelper.getHandler(tile);
					if (handler != null && handler instanceof InventoryReaderHandler)
						return new ContainerInventoryReader((InventoryReaderHandler) handler, tile, player.inventory);
				}

			case LogisticsGui.redstoneSignaller:
				if (entity instanceof TileEntityRedstoneSignaller) {
					return new ContainerEmptySync((TileEntityRedstoneSignaller) entity);
				}
			case LogisticsGui.dataModifier:
				if (entity instanceof TileEntityDataModifier || entity instanceof DataModifierPart) {
					TileHandler handler = FMPHelper.getHandler(tile);
					if (handler != null && handler instanceof DataModifierHandler)
						return new ContainerEmptySync((DataModifierHandler) handler, tile);
				}
			case LogisticsGui.infoCreator:
				if (entity instanceof TileEntityInfoCreator || entity instanceof InfoCreatorPart) {
					TileHandler handler = FMPHelper.getHandler(tile);
					if (handler != null && handler instanceof InfoCreatorHandler)
						return new ContainerEmptySync((InfoCreatorHandler) handler, tile);
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

		TileEntity tile = world.getTileEntity(x, y, z);
		Object entity = FMPHelper.checkObject(tile);

		if (entity != null) {
			switch (ID) {
			case LogisticsGui.infoNode:
				if (entity instanceof TileEntityInfoReader || entity instanceof InfoReaderPart) {
					TileHandler handler = FMPHelper.getHandler(tile);
					if (handler != null && handler instanceof InfoReaderHandler)
						return new GuiInfoReader((InfoReaderHandler) handler, tile);
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
				if (entity instanceof TileEntityInventoryReader || entity instanceof InventoryReaderPart) {
					TileHandler handler = FMPHelper.getHandler(tile);
					if (handler != null && handler instanceof InventoryReaderHandler)
						return new GuiInventoryReader((InventoryReaderHandler) handler, tile, player.inventory);
				}
			case LogisticsGui.redstoneSignaller:
				if (entity instanceof TileEntityRedstoneSignaller) {
					return new GuiRedstoneSignaller.RedstoneSignaller((TileEntityRedstoneSignaller) entity);
				}
			case LogisticsGui.dataModifier:
				if (entity instanceof TileEntityDataModifier || entity instanceof DataModifierPart) {
					TileHandler handler = FMPHelper.getHandler(tile);
					if (handler != null && handler instanceof DataModifierHandler)
						return new GuiDataModifier((DataModifierHandler) handler, tile);
				}
			case LogisticsGui.infoCreator:
				if (entity instanceof TileEntityInfoCreator || entity instanceof InfoCreatorPart) {
					TileHandler handler = FMPHelper.getHandler(tile);
					if (handler != null && handler instanceof InfoCreatorHandler)
						return new GuiInfoCreator((InfoCreatorHandler) handler, tile);
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
