package me.ichun.mods.beebarker.common.item;

import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.core.EventHandlerServer;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
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
        if(player.world.isRemote)
        {
            iChunUtil.proxy.nudgeHand(-50F);
        }
        EntityHelper.playSoundAtEntity(entity, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.NEUTRAL, 0.6F, (entity.world.rand.nextFloat() - entity.world.rand.nextFloat()) * 0.2F + 1F);
        return false;
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
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
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
            ItemStack wolfStack = new ItemStack(BeeBarker.itemBeeBarker, 1, 0); //Special unlimited BeeBarker
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagCompound wolfTag = new NBTTagCompound();
            wolfTag.setByte("CollarColor", (byte)1);
            wolfStack.setTagCompound(tag);
            wolfStack.getTagCompound().setTag(WOLF_DATA_STRING,wolfTag);
            items.add(wolfStack);

            if(BeeBarker.config.easterEgg == 1)
            {
                wolfStack = new ItemStack(BeeBarker.itemBeeBarker, 1, 0); //Special unlimited BeeBarker
                tag = new NBTTagCompound();
                wolfTag = new NBTTagCompound();
                wolfTag.setString("CustomName", "iChun");
                wolfTag.setByte("CollarColor", (byte)4);
                wolfStack.setTagCompound(tag);
                wolfStack.getTagCompound().setTag(WOLF_DATA_STRING, wolfTag);
                items.add(wolfStack);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack is, @Nullable World worldIn, List<String> list, ITooltipFlag flag)
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
