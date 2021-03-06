package me.ichun.mods.beebarker.common.core;

import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.entity.EntityBee;
import me.ichun.mods.beebarker.common.item.ItemBeeBarker;
import me.ichun.mods.beebarker.common.packet.PacketKeyState;
import me.ichun.mods.beebarker.common.packet.PacketSpawnParticles;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.item.ItemHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.ArrayList;
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
            Integer speed = cooldown.get(living.getName());
            if(speed == null)
            {
                speed = 0;
            }
            if(speed < 252)
            {
                cooldown.put(living.getName(), speed + 8);
            }

            if(speed > 10)
            {
                float pitchSpike = (float)Math.pow(MathHelper.clamp((speed - 10) / 250F, 0F, 1F), 1D);
                pitch += 1.5F * pitchSpike;
            }

            EntityPlayer player = (EntityPlayer)living;
            ItemStack is = ItemHandler.getUsableDualHandedItem(player);
            if(is.getItem() instanceof ItemBeeBarker && is.getTagCompound() != null && is.getTagCompound().hasKey(ItemBeeBarker.WOLF_DATA_STRING) && !player.capabilities.isCreativeMode)
            {
                NBTTagCompound tag = (NBTTagCompound)((NBTTagCompound)is.getTagCompound().getTag(ItemBeeBarker.WOLF_DATA_STRING)).getTag("ForgeData");

                if(!tag.getBoolean("IsSuperBeeDog"))
                {
                    if(tag.getInteger(EventHandlerServer.BEE_CHARGE_STRING) <= 0)
                    {
                        EntityHelper.playSoundAtEntity(living, SoundEvents.ENTITY_WOLF_HURT, SoundCategory.PLAYERS, 0.4F, (living.world.rand.nextFloat() - living.world.rand.nextFloat()) * 0.2F + pitch);
                        return;
                    }
                    tag.setInteger(EventHandlerServer.BEE_CHARGE_STRING, tag.getInteger(EventHandlerServer.BEE_CHARGE_STRING) - 1);
                    is.setItemDamage(1 + (int)((1.0F - (tag.getInteger(EventHandlerServer.BEE_CHARGE_STRING) / (float)tag.getInteger(EventHandlerServer.BEE_HIGHEST_CHARGE))) * 250F));
                }
            }
        }
        else
        {
            BeeBarker.channel.sendToAllAround(new PacketSpawnParticles(-1, living.getEntityId(), true), new NetworkRegistry.TargetPoint(living.dimension, living.posX, living.posY, living.posZ, 32D));
        }
        for(int i = 0; i < BeeBarker.config.beeCount; i++)
        {
            living.world.spawnEntity(new EntityBee(living.world, living));
        }
        EntityHelper.playSoundAtEntity(living, SoundEvents.ENTITY_WOLF_AMBIENT, SoundCategory.PLAYERS, 0.4F, (living.world.rand.nextFloat() - living.world.rand.nextFloat()) * 0.2F + pitch);
    }

    public static void removePressState(String name)
    {
        if(pressState.contains(name))
        {
            pressState.remove(name);
            BeeBarker.channel.sendToAll(new PacketKeyState(name, false));
        }
    }

    public static HashMap<String, Integer> cooldown = new HashMap<>();

    public static ArrayList<String> pressState = new ArrayList<>();
}
