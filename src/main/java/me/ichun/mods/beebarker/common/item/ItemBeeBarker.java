package me.ichun.mods.beebarker.common.item;

import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.core.EventHandlerServer;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemBeeBarker extends Item
{
    public static final String WOLF_DATA_STRING = "WolfData";

    public ItemBeeBarker()
    {
        maxStackSize = 1;
        setMaxDamage(251); //Max + 2
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
        if(player.worldObj.isRemote)
        {
            iChunUtil.proxy.nudgeHand(-50F);
        }
        EntityHelper.playSoundAtEntity(entity, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.NEUTRAL, 0.6F, (entity.worldObj.rand.nextFloat() - entity.worldObj.rand.nextFloat()) * 0.2F + 1F);
        return false;
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
    {
        return EnumActionResult.FAIL; // Return PASS to allow vanilla handling, any other to skip normal code.
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand)
    {
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return slotChanged || !ItemStack.areItemStacksEqual(oldStack, newStack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList)
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityWolf wolf = (EntityWolf)EntityList.createEntityByName("Wolf", mc.theWorld);
        wolf.setOwnerId(mc.thePlayer.getUniqueID());
        wolf.setTamed(true);
        wolf.setHealth(20.0F);
        wolf.getEntityData().setBoolean(EventHandlerServer.BARKABLE_STRING, true);
        wolf.getEntityData().setBoolean("IsSuperBeeDog", true);

        NBTTagCompound wolfTag = new NBTTagCompound();
        wolf.writeToNBTOptional(wolfTag);
        ItemStack wolfStack = new ItemStack(BeeBarker.itemBeeBarker, 1, 0); //Special unlimited BeeBarker
        wolfStack.setTagCompound(new NBTTagCompound());
        wolfStack.getTagCompound().setTag(WOLF_DATA_STRING, wolfTag);
        itemList.add(wolfStack);

        if(BeeBarker.config.easterEgg == 1)
        {
            wolfTag = new NBTTagCompound();
            wolf.setCollarColor(EnumDyeColor.BLUE);
            wolf.setCustomNameTag("iChun");
            wolf.writeToNBTOptional(wolfTag);
            wolfStack = new ItemStack(BeeBarker.itemBeeBarker, 1, 0); //Special unlimited BeeBarker
            wolfStack.setTagCompound(new NBTTagCompound());
            wolfStack.getTagCompound().setTag(WOLF_DATA_STRING, wolfTag);
            itemList.add(wolfStack);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack is, EntityPlayer player, List list, boolean flag)
    {
        if(is.getTagCompound() != null && is.getTagCompound().hasKey(WOLF_DATA_STRING))
        {
            if(((NBTTagCompound)is.getTagCompound().getTag(WOLF_DATA_STRING)).hasKey("CustomName"))
            {
                if(((NBTTagCompound)is.getTagCompound().getTag(WOLF_DATA_STRING)).getString("CustomName").equals("iChun") && BeeBarker.config.easterEgg == 1)
                {
                    list.add(I18n.translateToLocal("beebarker.easteregg.item"));
                }
                list.add(((NBTTagCompound)is.getTagCompound().getTag(WOLF_DATA_STRING)).getString("CustomName"));
            }
            StringBuilder sb = new StringBuilder();
            sb.append(I18n.translateToLocal("beebarker.beeCharge")).append(": ");
            if(is.getItemDamage() == 0)
            {
                sb.append(I18n.translateToLocal("beebarker.beeCharge.unlimited"));
            }
            else
            {
                sb.append(((NBTTagCompound)((NBTTagCompound)is.getTagCompound().getTag(WOLF_DATA_STRING)).getTag("ForgeData")).getInteger(EventHandlerServer.BEE_CHARGE_STRING));
            }
            list.add(sb.toString());
        }
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return Integer.MAX_VALUE;
    }
}
