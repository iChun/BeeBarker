package me.ichun.mods.beebarker.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

public class PacketKeyState extends AbstractPacket
{
    public String name;
    public boolean add;

    public PacketKeyState(){}

    public PacketKeyState(String name, boolean add)
    {
        this.name = name;
        this.add = add;
    }

    @Override
    public void writeTo(ByteBuf buffer)
    {
        ByteBufUtils.writeUTF8String(buffer, name);
        buffer.writeBoolean(add);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        name = ByteBufUtils.readUTF8String(buffer);
        add = buffer.readBoolean();
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        if(add)
        {
            if(!BeeBarker.eventHandlerClient.pressState.contains(name))
            {
                BeeBarker.eventHandlerClient.pressState.add(name);
            }
        }
        else
        {
            BeeBarker.eventHandlerClient.pressState.remove(name);
        }
    }

    @Override
    public Side receivingSide()
    {
        return Side.CLIENT;
    }
}
