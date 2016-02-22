package sonar.logistics.common.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import sonar.logistics.Logistics;
import sonar.logistics.registries.BlockRegistry;

public class DigitalSign extends Item
{
    public DigitalSign()
    {
        this.maxStackSize = 16;
        this.setCreativeTab(Logistics.creativeTab);
    }

	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitx, float hity, float hitz)
    {
        if (side == 0)
        {
            return false;
        }
        else if (!world.getBlock(x, y, z).getMaterial().isSolid())
        {
            return false;
        }
        else
        {
            if (side == 1)
            {
                ++y;
            }

            if (side == 2)
            {
                --z;
            }

            if (side == 3)
            {
                ++z;
            }

            if (side == 4)
            {
                --x;
            }

            if (side == 5)
            {
                ++x;
            }

            if (!player.canPlayerEdit(x, y, z, side, stack))
            {
                return false;
            }
            else if (!BlockRegistry.digitalSign_standing.canPlaceBlockAt(world, x, y, z))
            {
                return false;
            }
            else if (world.isRemote)
            {
                return true;
            }
            else
            {
                if (side == 1)
                {
                    int i1 = MathHelper.floor_double((double)((player.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
                    world.setBlock(x, y, z, BlockRegistry.digitalSign_standing, i1, 3);
                }
                else
                {
                    world.setBlock(x, y, z, BlockRegistry.digitalSign_wall, side, 3);
                }

                --stack.stackSize;
                TileEntitySign tileentitysign = (TileEntitySign)world.getTileEntity(x, y, z);

                if (tileentitysign != null)
                {
                    player.func_146100_a(tileentitysign);
                }

                return true;
            }
        }
    }
}