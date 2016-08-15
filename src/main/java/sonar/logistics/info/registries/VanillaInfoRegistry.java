package sonar.logistics.info.registries;

import java.util.HashMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import sonar.logistics.Logistics;
import sonar.logistics.api.info.IInfoRegistry;
import sonar.logistics.api.info.InfoRegistry;
import sonar.logistics.registries.LogicRegistry;
import sonar.logistics.registries.LogicRegistry.RegistryType;

@InfoRegistry(modid = Logistics.MODID)
public class VanillaInfoRegistry extends IInfoRegistry {

	@Override
	public void registerBaseReturns() {
		LogicRegistry.registerReturn(WorldInfo.class);
		LogicRegistry.registerReturn(WorldProvider.class);
		LogicRegistry.registerReturn(DimensionType.class);
		LogicRegistry.registerReturn(Fluid.class);
		LogicRegistry.registerReturn(FoodStats.class);
		LogicRegistry.registerReturn(Team.class);
	}

	@Override
	public void registerBaseMethods() {
		LogicRegistry.registerMethods(Block.class, RegistryType.BLOCK, Lists.newArrayList("getUnlocalizedName", "getMetaFromState", "getHarvestLevel", "isFoliage", "isWood", "canSustainLeaves"));
		LogicRegistry.registerMethods(Block.class, RegistryType.BLOCK, Lists.newArrayList("getWeakPower", "getStrongPower", "isSideSolid", "getBlockHardness"));
		LogicRegistry.registerMethods(BlockFluidBase.class, RegistryType.BLOCK, Lists.newArrayList("getFluid"));
		LogicRegistry.registerMethods(BlockCrops.class, RegistryType.BLOCK, Lists.newArrayList("isMaxAge"));
		LogicRegistry.registerMethods(Fluid.class, RegistryType.BLOCK, Lists.newArrayList("getLuminosity", "getDensity", "getTemperature", "getViscosity"));
		LogicRegistry.registerMethods(BlockPos.class, RegistryType.POS, Lists.newArrayList("getX", "getY", "getZ"));
		LogicRegistry.registerMethods(EnumFacing.class, RegistryType.FACE, Lists.newArrayList("toString"));
		LogicRegistry.registerMethods(World.class, RegistryType.WORLD, Lists.newArrayList("isBlockIndirectlyGettingPowered", "getWorldInfo"));
		LogicRegistry.registerMethods(WorldInfo.class, RegistryType.WORLD, Lists.newArrayList("isRaining", "isThundering", "getWorldName"));
		LogicRegistry.registerMethods(WorldProvider.class, RegistryType.WORLD, Lists.newArrayList("getDimension", "getDimensionType"));
		LogicRegistry.registerMethods(DimensionType.class, RegistryType.WORLD, Lists.newArrayList("getName"));
		LogicRegistry.registerMethods(EntityLivingBase.class, RegistryType.ENTITY, Lists.newArrayList("getHealth", "getMaxHealth", "getAge", "getTotalArmorValue", "getPosition", "getName"));
		LogicRegistry.registerMethods(EntityAgeable.class, RegistryType.ENTITY, Lists.newArrayList("getGrowingAge"));
		LogicRegistry.registerMethods(EntityPlayer.class, RegistryType.ENTITY, Lists.newArrayList("isCreative", "isSpectator", "getFoodStats", "getAbsorptionAmount", "getTeam", "getExperiencePoints"));
		LogicRegistry.registerMethods(FoodStats.class, RegistryType.ENTITY, Lists.newArrayList("getFoodLevel", "needFood", "getSaturationLevel"));
		LogicRegistry.registerMethods(Team.class, RegistryType.ENTITY, Lists.newArrayList("getRegisteredName"));
	}

	@Override
	public void registerAllFields() {
		HashMap<String, Integer> furnaceFields = Maps.<String, Integer>newHashMap();
		furnaceFields.put("furnaceBurnTime", 0);
		furnaceFields.put("currentItemBurnTime", 1);
		furnaceFields.put("cookTime", 2);
		furnaceFields.put("totalCookTime", 3);
		LogicRegistry.registerInvFields(TileEntityFurnace.class, furnaceFields);
		LogicRegistry.registerFields(TileEntityNote.class, RegistryType.TILE, Lists.newArrayList("note"));
	}

	@Override
	public void registerAdjustments() {
		LogicRegistry.registerInfoAdjustments(Lists.newArrayList("EntityLivingBase.getHealth", "EntityLivingBase.getMaxHealth"), "", "HP");
		LogicRegistry.registerInfoAdjustments(Lists.newArrayList("TileEntityFurnace.furnaceBurnTime", "TileEntityFurnace.currentItemBurnTime","TileEntityFurnace.cookTime","TileEntityFurnace.totalCookTime"), "", "ticks");
		//LogicRegistry.registerInfoAdjustments(Lists.newArrayList("Block.getUnlocalizedName"), "", ".name");
	}

}
