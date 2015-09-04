package me.ichun.mods.beebarker.common.core;

import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.packet.PacketSpawnParticles;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BarkHelper
{
    public static void bark(EntityLivingBase living)
    {
        float pitch = 1F;
        if(living instanceof EntityPlayer)
        {
            Integer speed = cooldown.get(living.getCommandSenderName());
            if(speed == null)
            {
                speed = 0;
            }
            if(speed < 252)
            {
                cooldown.put(living.getCommandSenderName(), speed + 8);
            }

            if(speed > 10)
            {
                float pitchSpike = (float)Math.pow(MathHelper.clamp_float((speed - 10) / 250F, 0F, 1F), 1D);
                pitch += 1.5F * pitchSpike;
            }
        }
        else
        {
            BeeBarker.channel.sendToAllAround(new PacketSpawnParticles(-1, living.getEntityId(), true), new NetworkRegistry.TargetPoint(living.dimension, living.posX, living.posY, living.posZ, 32D));
        }
        living.worldObj.playSoundAtEntity(living, "mob.wolf.bark", 0.4F, (living.worldObj.rand.nextFloat() - living.worldObj.rand.nextFloat()) * 0.2F + pitch);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            Iterator<Map.Entry<String, Integer>> ite = cooldown.entrySet().iterator();
            while(ite.hasNext())
            {
                Map.Entry<String, Integer> e = ite.next();
                if(e.getValue() > 0)
                {
                    if(e.getValue() > 120)
                    {
                        EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerByUsername(e.getKey());
                        if(player != null)
                        {
                            BeeBarker.channel.sendToAllAround(new PacketSpawnParticles(-1, player.getEntityId(), true), new NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 32D));
                        }
                    }
                    e.setValue(e.getValue() - 2);
                }
                else
                {
                    ite.remove();
                }
            }
        }
    }

    public static HashMap<String, Integer> cooldown = new HashMap<String, Integer>();
}
