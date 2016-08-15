package sonar.logistics.client;

import java.util.ArrayList;
import java.util.List;

import mcmultipart.item.ItemMultiPart;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import sonar.core.common.block.properties.IMetaRenderer;
import sonar.core.registries.ISonarRegistryItem;
import sonar.logistics.Logistics;
import sonar.logistics.LogisticsItems;

public class ItemRenderRegister {
	public static void register() {
		for (ISonarRegistryItem register : LogisticsItems.registeredItems) {
			Item item = register.getItem();
			if (item.getHasSubtypes()) {
				List<ItemStack> stacks = new ArrayList();
				item.getSubItems(item, Logistics.creativeTab, stacks);
				for (ItemStack stack : stacks) {
					String variant = "variant=meta" + stack.getItemDamage();
					if (item instanceof IMetaRenderer) {
						IMetaRenderer meta = (IMetaRenderer) item;
						variant = "variant=" + meta.getVariant(stack.getItemDamage()).getName();
					}

					ModelLoader.setCustomModelResourceLocation(item, stack.getItemDamage(), new ModelResourceLocation(Logistics.MODID + ":" + "items/" + register.getRegistryName().substring(5), variant));

				}
			} else {
				if (item instanceof ItemMultiPart) {
					ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Logistics.MODID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
				} else {
					//Logistics.logger.info(new ModelResourceLocation(new ResourceLocation(Logistics.MODID, "items/" + item.getUnlocalizedName().substring(5)), "inventory").toString());
					ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(new ResourceLocation(Logistics.MODID, item.getUnlocalizedName().substring(5)), "inventory"));
				}
			}
		}
	}
}
