package me.ichun.mods.beebarker.common.packet;

import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.core.BarkHelper;
import me.ichun.mods.beebarker.common.item.ItemBeeBarker;
import me.ichun.mods.ichunutil.common.entity.util.EntityHelper;
import me.ichun.mods.ichunutil.common.item.DualHandedItem;
import me.ichun.mods.ichunutil.common.network.AbstractPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class PacketBark extends AbstractPacket
{
    public boolean pressed;

    public PacketBark(){}

    public PacketBark(boolean press)
    {
        pressed = press;
    }

    @Override
    public void writeTo(PacketBuffer buffer)
    {
        buffer.writeBoolean(pressed);
    }

    @Override
    public void readFrom(PacketBuffer buffer)
    {
        pressed = buffer.readBoolean();
    }

    @Override
    public void process(NetworkEvent.Context context) // receivingSide SERVER
    {
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if(pressed)
            {
                ItemStack is = DualHandedItem.getUsableDualHandedItem(player);
                if(is.getItem() == BeeBarker.Items.BEE_BARKER.get() && is.getTag() != null && is.getTag().contains(ItemBeeBarker.WOLF_DATA_STRING))
                {
                    if(BeeBarker.configServer.easterEgg && is.getTag().getCompound(ItemBeeBarker.WOLF_DATA_STRING).contains("CustomName") && is.getTag().getCompound(ItemBeeBarker.WOLF_DATA_STRING).getString("CustomName").contains("\"text\":\"iChun\""))
                    {
                        if(!BarkHelper.pressState.contains(player.getName().getUnformattedComponentText()))
                        {
                            BarkHelper.pressState.add(player.getName().getUnformattedComponentText());
                            EntityHelper.playSound(player, SoundEvents.ENTITY_WOLF_GROWL, SoundCategory.PLAYERS, 0.4F, 1.0F);
                            BeeBarker.channel.sendTo(new PacketKeyState(player.getName().getUnformattedComponentText(), true), PacketDistributor.ALL.noArg());
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
                BarkHelper.removePressState(player.getName().getUnformattedComponentText());
            }
        });
    }
}
