package me.ichun.mods.beebarker.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.core.BarkHelper;
import me.ichun.mods.beebarker.common.item.ItemBeeBarker;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;

public class PacketBark extends AbstractPacket
{
    public boolean pressed;

    public PacketBark(){}

    public PacketBark(boolean press)
    {
        pressed = press;
    }

    @Override
    public void writeTo(ByteBuf buffer)
    {
        buffer.writeBoolean(pressed);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        pressed = buffer.readBoolean();
    }

    @Override
    public Side receivingSide()
    {
        return Side.SERVER;
    }

    @Override
    public AbstractPacket execute(Side side, EntityPlayer player)
    {
        if(pressed)
        {
            ItemStack is = player.getHeldItem(EnumHand.MAIN_HAND);
            if(is != null && is.getItem() == BeeBarker.itemBeeBarker && is.getTagCompound() != null && is.getTagCompound().hasKey(ItemBeeBarker.WOLF_DATA_STRING))
            {
                if(BeeBarker.config.easterEgg == 1 && ((NBTTagCompound)is.getTagCompound().getTag(ItemBeeBarker.WOLF_DATA_STRING)).hasKey("CustomName") && ((NBTTagCompound)is.getTagCompound().getTag(ItemBeeBarker.WOLF_DATA_STRING)).getString("CustomName").equals("iChun"))
                {
                    if(!BarkHelper.pressState.contains(player.getName()))
                    {
                        BarkHelper.pressState.add(player.getName());
                        player.worldObj.playSound(null, player.posX, player.posY + player.getEyeHeight(), player.posZ, SoundEvents.ENTITY_WOLF_GROWL, SoundCategory.PLAYERS, 0.4F, 1.0F);
                        BeeBarker.channel.sendToAll(new PacketKeyState(player.getName(), true));
                    }
                }
                else
                {
                    BarkHelper.bark(player);
                }
            }
        }
        else
        {
            BarkHelper.removePressState(player.getName());
        }
        return null;
    }
}
