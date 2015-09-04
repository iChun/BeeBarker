package me.ichun.mods.beebarker.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.beebarker.common.core.BarkHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import us.ichun.mods.ichunutil.common.core.network.AbstractPacket;

public class PacketBark extends AbstractPacket
{
    public PacketBark(){}

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        BarkHelper.bark(player);
    }
}
