package me.ichun.mods.beebarker.common.item;

import me.ichun.mods.beebarker.client.render.ItemRenderBeeBarker;
import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.core.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.ichun.mods.ichunutil.common.iChunUtil;

import java.util.List;

public class ItemBeeBarker extends Item
{
    @SideOnly(Side.CLIENT)
    public ItemRenderBeeBarker renderer;

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
            iChunUtil.proxy.tickHandlerClient.nudgeHand(-50F);
        }
        player.worldObj.playSoundAtEntity(player, "random.eat", 0.6F, (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.2F + 1F);
        return false;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        return true;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target)
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList)
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityWolf wolf = (EntityWolf)EntityList.createEntityByName("Wolf", mc.theWorld);
        wolf.setOwnerId(mc.thePlayer.getUniqueID().toString());
        wolf.setTamed(true);
        wolf.setHealth(20.0F);
        wolf.getEntityData().setBoolean(EventHandler.BARKABLE_STRING, true);
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
                    list.add(StatCollector.translateToLocal("beebarker.easteregg.item"));
                }
                list.add(((NBTTagCompound)is.getTagCompound().getTag(WOLF_DATA_STRING)).getString("CustomName"));
            }
            StringBuilder sb = new StringBuilder();
            sb.append(StatCollector.translateToLocal("beebarker.beeCharge")).append(": ");
            if(is.getItemDamage() == 0)
            {
                sb.append(StatCollector.translateToLocal("beebarker.beeCharge.unlimited"));
            }
            else
            {
                sb.append(((NBTTagCompound)((NBTTagCompound)is.getTagCompound().getTag(WOLF_DATA_STRING)).getTag("ForgeData")).getInteger(EventHandler.BEE_CHARGE_STRING));
            }
            list.add(sb.toString());
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
