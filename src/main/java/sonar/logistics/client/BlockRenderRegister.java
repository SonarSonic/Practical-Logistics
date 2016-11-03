package sonar.logistics.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import sonar.core.SonarCore;
import sonar.core.client.renderers.ISonarCustomRenderer;
import sonar.core.client.renderers.ISonarRendererProvider;
import sonar.core.client.renderers.SonarCustomStateMapper;
import sonar.core.common.block.properties.IMetaRenderer;
import sonar.core.registries.ISonarRegistryBlock;
import sonar.logistics.Logistics;
import sonar.logistics.LogisticsBlocks;

public class BlockRenderRegister {

	public static void register() {
		/* registerBlock(Calculator.reinforcedStoneBlock); registerBlock(Calculator.reinforcedStoneStairs); registerBlock(Calculator.reinforcedStoneBrick); registerBlock(Calculator.reinforcedStoneBrickStairs); registerBlock(Calculator.reinforcedDirtBlock); registerBlock(Calculator.reinforcedDirtStairs); registerBlock(Calculator.reinforcedDirtBrick); registerBlock(Calculator.reinforcedDirtBrickStairs); registerBlock(Calculator.stableStone); registerBlock(Calculator.powerCube); registerBlock(Calculator.advancedPowerCube); registerBlock(Calculator.atomicCalculator); registerBlock(Calculator.dynamicCalculator); registerBlock(Calculator.reinforcedFurnace); registerBlock(Calculator.stoneSeparator); registerBlock(Calculator.algorithmSeparator); registerBlock(Calculator.hungerProcessor); registerBlock(Calculator.healthProcessor); registerBlock(Calculator.basicGreenhouse); registerBlock(Calculator.advancedGreenhouse); registerBlock(Calculator.flawlessGreenhouse); registerBlock(Calculator.CO2Generator); */
		for (ISonarRegistryBlock block : LogisticsBlocks.registeredBlocks) {
			if (block.getBlock() instanceof ISonarRendererProvider) {
				ISonarCustomRenderer renderer = ((ISonarRendererProvider) block.getBlock()).getRenderer();
				registerSpecial(renderer);
			} else {
				Item item = Item.getItemFromBlock(block.getBlock());
				if (item == null) {
					SonarCore.logger.error("Enable to register renderer for " + block.getBlock());
					return;
				}
				if (item != null && item.getHasSubtypes()) {
					List<ItemStack> stacks = new ArrayList();
					item.getSubItems(item, Logistics.creativeTab, stacks);
					for (ItemStack stack : stacks) {
						String variant = "variant=meta" + stack.getItemDamage();
						if (block instanceof IMetaRenderer) {
							IMetaRenderer meta = (IMetaRenderer) block;
							variant = "variant=" + meta.getVariant(stack.getItemDamage()).getName();
						}
						ModelLoader.setCustomModelResourceLocation(item, stack.getItemDamage(), new ModelResourceLocation(Logistics.MODID + ":" + item.getUnlocalizedName().substring(5), variant));
					}
				} else {
					registerBlock(block.getBlock());
				}
			}
		}
	}

	public static void registerBlock(Block block) {
		if (block != null) {
			Item item = Item.getItemFromBlock(block);
			if (item == null) {
				SonarCore.logger.error("Enable to register renderer for " + block);
				return;
			}
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Logistics.MODID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
		}
	}

	public static void registerSpecial(ISonarCustomRenderer renderer) {
		((SonarCustomStateMapper) SonarCore.proxy.getStateMapper()).registerCustomBlockRenderer(renderer);
	}
}
