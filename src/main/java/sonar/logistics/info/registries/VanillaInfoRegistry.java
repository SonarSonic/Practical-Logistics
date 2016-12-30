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
import sonar.logistics.api.asm.InfoRegistry;
import sonar.logistics.api.info.IInfoRegistry;
import sonar.logistics.info.LogicInfoRegistry;
import sonar.logistics.info.LogicInfoRegistry.RegistryType;

@InfoRegistry(modid = Logistics.MODID)
public class VanillaInfoRegistry extends IInfoRegistry {

	@Override
	public void registerBaseReturns() {
		LogicInfoRegistry.registerReturn(WorldInfo.class);
		LogicInfoRegistry.registerReturn(WorldProvider.class);
		LogicInfoRegistry.registerReturn(DimensionType.class);
		LogicInfoRegistry.registerReturn(Fluid.class);
		LogicInfoRegistry.registerReturn(FoodStats.class);
		LogicInfoRegistry.registerReturn(Team.class);
	}

	@Override
	public void registerBaseMethods() {
		LogicInfoRegistry.registerMethods(Block.class, RegistryType.BLOCK, Lists.newArrayList("getUnlocalizedName", "getMetaFromState", "getHarvestLevel", "isFoliage", "isWood", "canSustainLeaves"));
		LogicInfoRegistry.registerMethods(Block.class, RegistryType.BLOCK, Lists.newArrayList("getWeakPower", "getStrongPower", "isSideSolid", "getBlockHardness"));
		LogicInfoRegistry.registerMethods(BlockFluidBase.class, RegistryType.BLOCK, Lists.newArrayList("getFluid"));
		LogicInfoRegistry.registerMethods(BlockCrops.class, RegistryType.BLOCK, Lists.newArrayList("isMaxAge"));
		LogicInfoRegistry.registerMethods(Fluid.class, RegistryType.BLOCK, Lists.newArrayList("getLuminosity", "getDensity", "getTemperature", "getViscosity"));
		LogicInfoRegistry.registerMethods(BlockPos.class, RegistryType.POS, Lists.newArrayList("getX", "getY", "getZ"));
		LogicInfoRegistry.registerMethods(EnumFacing.class, RegistryType.FACE, Lists.newArrayList("toString"));
		LogicInfoRegistry.registerMethods(World.class, RegistryType.WORLD, Lists.newArrayList("isBlockIndirectlyGettingPowered", "getWorldInfo"));
		LogicInfoRegistry.registerMethods(WorldInfo.class, RegistryType.WORLD, Lists.newArrayList("isRaining", "isThundering", "getWorldName"));
		LogicInfoRegistry.registerMethods(WorldProvider.class, RegistryType.WORLD, Lists.newArrayList("getDimension", "getDimensionType"));
		LogicInfoRegistry.registerMethods(DimensionType.class, RegistryType.WORLD, Lists.newArrayList("getName"));
		LogicInfoRegistry.registerMethods(EntityLivingBase.class, RegistryType.ENTITY, Lists.newArrayList("getHealth", "getMaxHealth", "getAge", "getTotalArmorValue", "getPosition", "getName"));
		LogicInfoRegistry.registerMethods(EntityAgeable.class, RegistryType.ENTITY, Lists.newArrayList("getGrowingAge"));
		LogicInfoRegistry.registerMethods(EntityPlayer.class, RegistryType.ENTITY, Lists.newArrayList("isCreative", "isSpectator", "getFoodStats", "getAbsorptionAmount", "getTeam", "getExperiencePoints"));
		LogicInfoRegistry.registerMethods(FoodStats.class, RegistryType.ENTITY, Lists.newArrayList("getFoodLevel", "needFood", "getSaturationLevel"));
		LogicInfoRegistry.registerMethods(Team.class, RegistryType.ENTITY, Lists.newArrayList("getRegisteredName"));
	}

	@Override
	public void registerAllFields() {
		HashMap<String, Integer> furnaceFields = Maps.<String, Integer>newHashMap();
		furnaceFields.put("furnaceBurnTime", 0);
		furnaceFields.put("currentItemBurnTime", 1);
		furnaceFields.put("cookTime", 2);
		furnaceFields.put("totalCookTime", 3);
		LogicInfoRegistry.registerInvFields(TileEntityFurnace.class, furnaceFields);
		LogicInfoRegistry.registerFields(TileEntityNote.class, RegistryType.TILE, Lists.newArrayList("note"));
	}

	@Override
	public void registerAdjustments() {
		LogicInfoRegistry.registerInfoAdjustments(Lists.newArrayList("EntityLivingBase.getHealth", "EntityLivingBase.getMaxHealth"), "", "HP");
		LogicInfoRegistry.registerInfoAdjustments(Lists.newArrayList("TileEntityFurnace.furnaceBurnTime", "TileEntityFurnace.currentItemBurnTime","TileEntityFurnace.cookTime","TileEntityFurnace.totalCookTime"), "", "ticks");
		//LogicRegistry.registerInfoAdjustments(Lists.newArrayList("Block.getUnlocalizedName"), "", ".name");
	}

}
