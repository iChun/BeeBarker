package me.ichun.mods.beebarker.common.item;

import me.ichun.mods.beebarker.client.render.ItemRenderBeeBarker;
import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.core.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemBeeBarker extends Item
{
    @SideOnly(Side.CLIENT)
    public ItemRenderBeeBarker renderer;

    public ItemBeeBarker()
    {
        maxStackSize = 1;
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
        //TODO play sound here
        return false;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList)
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityWolf wolf = (EntityWolf)EntityList.createEntityByName("Wolf", mc.theWorld);
        wolf.getEntityData().setBoolean(EventHandler.BARKABLE_STRING, true);
        NBTTagCompound wolfTag = new NBTTagCompound();
        wolf.writeToNBTOptional(wolfTag);
        ItemStack wolfStack = new ItemStack(BeeBarker.itemBeeBarker, 1, wolf.getCollarColor().getMetadata());
        wolfStack.setTagCompound(new NBTTagCompound());
        wolfStack.getTagCompound().setTag("WolfData", wolfTag);
        itemList.add(wolfStack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack is, EntityPlayer player, List list, boolean flag)
    {
        if(is.getTagCompound() != null && is.getTagCompound().hasKey("WolfData") && ((NBTTagCompound)is.getTagCompound().getTag("WolfData")).hasKey("CustomName"))
        {
            list.add(((NBTTagCompound)is.getTagCompound().getTag("WolfData")).getString("CustomName"));
        }
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack)
    {
        return EnumAction.BOW;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public net.minecraft.client.resources.model.ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining)
    {
        renderer.lastPlayer = player;
        return super.getModel(stack, player, useRemaining);
    }
}
