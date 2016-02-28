package sonar.logistics.network;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.client.gui.GuiArray;
import sonar.logistics.client.gui.GuiChannelSelector;
import sonar.logistics.client.gui.GuiClock;
import sonar.logistics.client.gui.GuiDataModifier;
import sonar.logistics.client.gui.GuiDataReceiver;
import sonar.logistics.client.gui.GuiEnergyReader;
import sonar.logistics.client.gui.GuiEntityNode;
import sonar.logistics.client.gui.GuiFluidReader;
import sonar.logistics.client.gui.GuiHammer;
import sonar.logistics.client.gui.GuiInfoCreator;
import sonar.logistics.client.gui.GuiInfoReader;
import sonar.logistics.client.gui.GuiInventoryReader;
import sonar.logistics.client.gui.GuiItemRouter;
import sonar.logistics.client.gui.GuiRedstoneSignaller;
import sonar.logistics.client.gui.GuiRenameEmitter;
import sonar.logistics.common.containers.ContainerArray;
import sonar.logistics.common.containers.ContainerDataReceiver;
import sonar.logistics.common.containers.ContainerEmptySync;
import sonar.logistics.common.containers.ContainerFluidReader;
import sonar.logistics.common.containers.ContainerHammer;
import sonar.logistics.common.containers.ContainerInfoNode;
import sonar.logistics.common.containers.ContainerInventoryReader;
import sonar.logistics.common.containers.ContainerItemRouter;
import sonar.logistics.common.handlers.ArrayHandler;
import sonar.logistics.common.handlers.ChannelSelectorHandler;
import sonar.logistics.common.handlers.DataModifierHandler;
import sonar.logistics.common.handlers.EnergyReaderHandler;
import sonar.logistics.common.handlers.FluidReaderHandler;
import sonar.logistics.common.handlers.InfoCreatorHandler;
import sonar.logistics.common.handlers.InfoReaderHandler;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import sonar.logistics.common.handlers.ItemRouterHandler;
import sonar.logistics.common.tileentity.TileEntityChannelSelector;
import sonar.logistics.common.tileentity.TileEntityClock;
import sonar.logistics.common.tileentity.TileEntityDataEmitter;
import sonar.logistics.common.tileentity.TileEntityDataModifier;
import sonar.logistics.common.tileentity.TileEntityDataReceiver;
import sonar.logistics.common.tileentity.TileEntityEnergyReader;
import sonar.logistics.common.tileentity.TileEntityEntityNode;
import sonar.logistics.common.tileentity.TileEntityFluidReader;
import sonar.logistics.common.tileentity.TileEntityHammer;
import sonar.logistics.common.tileentity.TileEntityInfoCreator;
import sonar.logistics.common.tileentity.TileEntityInfoReader;
import sonar.logistics.common.tileentity.TileEntityInventoryReader;
import sonar.logistics.common.tileentity.TileEntityItemRouter;
import sonar.logistics.common.tileentity.TileEntityRedstoneSignaller;
import sonar.logistics.integration.multipart.DataModifierPart;
import sonar.logistics.integration.multipart.FluidReaderPart;
import sonar.logistics.integration.multipart.InfoCreatorPart;
import sonar.logistics.integration.multipart.InfoReaderPart;
import sonar.logistics.integration.multipart.InventoryReaderPart;
import sonar.logistics.network.packets.PacketCoordsSelection;
import sonar.logistics.network.packets.PacketDataEmitters;
import sonar.logistics.network.packets.PacketFluidReader;
import sonar.logistics.network.packets.PacketGuiChange;
import sonar.logistics.network.packets.PacketInfoBlock;
import sonar.logistics.network.packets.PacketInventoryReader;
import sonar.logistics.network.packets.PacketProviders;
import sonar.logistics.network.packets.PacketRouterGui;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;

public class LogisticsCommon implements IGuiHandler {

	private static final Map<String, NBTTagCompound> extendedEntityData = new HashMap<String, NBTTagCompound>();

	public static void registerPackets() {
		Logistics.network.registerMessage(PacketProviders.Handler.class, PacketProviders.class, 0, Side.CLIENT);
		Logistics.network.registerMessage(PacketInfoBlock.Handler.class, PacketInfoBlock.class, 1, Side.SERVER);
		Logistics.network.registerMessage(PacketDataEmitters.Handler.class, PacketDataEmitters.class, 2, Side.CLIENT);
		Logistics.network.registerMessage(PacketCoordsSelection.Handler.class, PacketCoordsSelection.class, 3, Side.SERVER);
		Logistics.network.registerMessage(PacketInventoryReader.Handler.class, PacketInventoryReader.class, 4, Side.SERVER);
		Logistics.network.registerMessage(PacketFluidReader.Handler.class, PacketFluidReader.class, 5, Side.SERVER);
		Logistics.network.registerMessage(PacketRouterGui.Handler.class, PacketRouterGui.class, 6, Side.SERVER);
		Logistics.network.registerMessage(PacketGuiChange.Handler.class, PacketGuiChange.class, 7, Side.SERVER);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		Object entity = FMPHelper.checkObject(tile);
		TileHandler handler = FMPHelper.getHandler(tile);
		if (entity != null) {
			switch (ID) {
			case LogisticsGui.infoNode:
				if (handler != null && handler instanceof InfoReaderHandler)
					return new ContainerInfoNode((InfoReaderHandler) handler, tile);

			case LogisticsGui.dataReceiver:
				if (entity instanceof TileEntityDataReceiver)
					return new ContainerDataReceiver((TileEntityDataReceiver) entity, player.inventory);

			case LogisticsGui.dataEmitter:
				if (entity instanceof TileEntityDataEmitter)
					return new ContainerEmptySync((TileEntityDataEmitter) entity);

			case LogisticsGui.inventoryReader:
				if (handler != null && handler instanceof InventoryReaderHandler)
					return new ContainerInventoryReader((InventoryReaderHandler) handler, tile, player.inventory);

			case LogisticsGui.redstoneSignaller:
				if (entity instanceof TileEntityRedstoneSignaller) {
					return new ContainerEmptySync((TileEntityRedstoneSignaller) entity);
				}
			case LogisticsGui.dataModifier:
				if (handler != null && handler instanceof DataModifierHandler)
					return new ContainerEmptySync((DataModifierHandler) handler, tile);

			case LogisticsGui.infoCreator:
				if (handler != null && handler instanceof InfoCreatorHandler)
					return new ContainerEmptySync((InfoCreatorHandler) handler, tile);

			case LogisticsGui.hammer:
				if (entity instanceof TileEntityHammer)
					return new ContainerHammer(player.inventory, (TileEntityHammer) entity);

			case LogisticsGui.entityNode:
				if (entity instanceof TileEntityEntityNode)
					return new ContainerEmptySync((TileEntityEntityNode) entity);

			case LogisticsGui.fluidReader:
				if (handler != null && handler instanceof FluidReaderHandler)
					return new ContainerFluidReader((FluidReaderHandler) handler, tile, player.inventory);

			case LogisticsGui.itemRouter:
				if (handler != null && handler instanceof ItemRouterHandler)
					return new ContainerItemRouter((TileEntityItemRouter) tile, player.inventory);

			case LogisticsGui.channelSelector:
				if (handler != null && handler instanceof ChannelSelectorHandler)
					return new ContainerEmptySync((ChannelSelectorHandler) handler, tile).setTypes(new SyncType[] { SyncType.SPECIAL });

			case LogisticsGui.clock:
				if (entity instanceof TileEntityClock) {
					return new ContainerEmptySync((TileEntityClock) entity);
				}
			case LogisticsGui.energyReader:
				if (handler != null && handler instanceof EnergyReaderHandler) {
					return new ContainerEmptySync((EnergyReaderHandler) handler, tile).setTypes(new SyncType[] { SyncType.SPECIAL });
				}

			case LogisticsGui.transceiverArray:
				if (handler != null && handler instanceof ArrayHandler) {
					return new ContainerArray(player.inventory, (ArrayHandler) handler, tile);
				}
			}

		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

		TileEntity tile = world.getTileEntity(x, y, z);
		Object entity = FMPHelper.checkObject(tile);
		TileHandler handler = FMPHelper.getHandler(tile);
		if (entity != null) {
			switch (ID) {
			case LogisticsGui.infoNode:
				if (handler != null && handler instanceof InfoReaderHandler)
					return new GuiInfoReader((InfoReaderHandler) handler, tile);

			case LogisticsGui.dataReceiver:
				if (entity instanceof TileEntityDataReceiver) {
					return new GuiDataReceiver(player.inventory, (TileEntityDataReceiver) entity);
				}
			case LogisticsGui.dataEmitter:
				if (entity instanceof TileEntityDataEmitter) {
					return new GuiRenameEmitter.DataEmitter((TileEntityDataEmitter) entity);
				}
			case LogisticsGui.inventoryReader:
				if (handler != null && handler instanceof InventoryReaderHandler)
					return new GuiInventoryReader((InventoryReaderHandler) handler, tile, player.inventory);

			case LogisticsGui.redstoneSignaller:
				if (entity instanceof TileEntityRedstoneSignaller) {
					return new GuiRedstoneSignaller.RedstoneSignaller((TileEntityRedstoneSignaller) entity);
				}
			case LogisticsGui.dataModifier:
				if (entity instanceof TileEntityDataModifier || entity instanceof DataModifierPart) {
					if (handler != null && handler instanceof DataModifierHandler)
						return new GuiDataModifier((DataModifierHandler) handler, tile);
				}
			case LogisticsGui.infoCreator:
				if (handler != null && handler instanceof InfoCreatorHandler)
					return new GuiInfoCreator((InfoCreatorHandler) handler, tile);

			case LogisticsGui.hammer:
				if (entity instanceof TileEntityHammer) {
					return new GuiHammer(player.inventory, (TileEntityHammer) entity);
				}
			case LogisticsGui.entityNode:
				if (entity instanceof TileEntityEntityNode) {
					return new GuiEntityNode((TileEntityEntityNode) entity);
				}
			case LogisticsGui.fluidReader:
				if (handler != null && handler instanceof FluidReaderHandler)
					return new GuiFluidReader((FluidReaderHandler) handler, tile, player.inventory);

			case LogisticsGui.itemRouter:
				if (handler != null && handler instanceof ItemRouterHandler)
					return new GuiItemRouter((ItemRouterHandler) handler, (TileEntityItemRouter) tile, player);

			case LogisticsGui.channelSelector:
				if (handler != null && handler instanceof ChannelSelectorHandler)
					return new GuiChannelSelector(tile, (ChannelSelectorHandler) handler, player.inventory);

			case LogisticsGui.clock:
				if (entity instanceof TileEntityClock) {
					return new GuiClock((TileEntityClock) entity);
				}
			case LogisticsGui.energyReader:
				if (handler != null && handler instanceof EnergyReaderHandler)
					return new GuiEnergyReader(tile, (EnergyReaderHandler) handler, player.inventory);
				
			case LogisticsGui.transceiverArray:
				if (handler != null && handler instanceof ArrayHandler)
					return new GuiArray(player.inventory, (ArrayHandler) handler, tile);

			}

		}

		return null;
	}

	public void registerRenderThings() {

	}

}
