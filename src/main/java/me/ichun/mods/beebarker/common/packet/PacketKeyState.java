package me.ichun.mods.beebarker.common.packet;

import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.ichunutil.common.network.AbstractPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

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
    public void writeTo(PacketBuffer buffer)
    {
        buffer.writeString(name);
        buffer.writeBoolean(add);
    }

    @Override
    public void readFrom(PacketBuffer buffer)
    {
        name = readString(buffer);
        add = buffer.readBoolean();
    }

    @Override
    public void process(NetworkEvent.Context context) //receivingSide CLIENT
    {
        context.enqueueWork(() -> {
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
        });
    }
}
