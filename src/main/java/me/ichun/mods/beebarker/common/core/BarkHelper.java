package me.ichun.mods.beebarker.common.core;

import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.entity.EntityBee;
import me.ichun.mods.beebarker.common.item.ItemBeeBarker;
import me.ichun.mods.beebarker.common.packet.PacketKeyState;
import me.ichun.mods.beebarker.common.packet.PacketSpawnParticles;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import us.ichun.mods.ichunutil.common.core.EntityHelperBase;

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

            EntityPlayer player = (EntityPlayer)living;
            ItemStack is = player.getHeldItem();
            if(is != null && is.getItem() instanceof ItemBeeBarker && is.getTagCompound() != null && is.getTagCompound().hasKey(ItemBeeBarker.WOLF_DATA_STRING) && !player.capabilities.isCreativeMode)
            {
                NBTTagCompound tag = (NBTTagCompound)((NBTTagCompound)is.getTagCompound().getTag(ItemBeeBarker.WOLF_DATA_STRING)).getTag("ForgeData");

                if(!tag.getBoolean("IsSuperBeeDog"))
                {
                    if(tag.getInteger(EventHandler.BEE_CHARGE_STRING) <= 0)
                    {
                        living.worldObj.playSoundAtEntity(living, "mob.wolf.hurt", 0.4F, (living.worldObj.rand.nextFloat() - living.worldObj.rand.nextFloat()) * 0.2F + pitch);
                        return;
                    }
                    tag.setInteger(EventHandler.BEE_CHARGE_STRING, tag.getInteger(EventHandler.BEE_CHARGE_STRING) - 1);
                    is.setItemDamage(1 + (int)((1.0F - (tag.getInteger(EventHandler.BEE_CHARGE_STRING) / (float)tag.getInteger(EventHandler.BEE_HIGHEST_CHARGE))) * 250F));
                }
            }
        }
        else
        {
            BeeBarker.channel.sendToAllAround(new PacketSpawnParticles(-1, living.getEntityId(), true), new NetworkRegistry.TargetPoint(living.dimension, living.posX, living.posY, living.posZ, 32D));
        }
        for(int i = 0; i < BeeBarker.config.beeCount; i++)
        {
            living.worldObj.spawnEntityInWorld(new EntityBee(living.worldObj, living));
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
            for(int i = pressState.size() - 1; i >= 0; i--)
            {
                String name = pressState.get(i);
                EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerByUsername(name);
                if(player != null)
                {
                    ItemStack is = player.getHeldItem();
                    if(is != null && is.getItem() == BeeBarker.itemBeeBarker && is.getTagCompound() != null && is.getTagCompound().hasKey(ItemBeeBarker.WOLF_DATA_STRING) && BeeBarker.config.easterEgg == 1 && ((NBTTagCompound)is.getTagCompound().getTag(ItemBeeBarker.WOLF_DATA_STRING)).hasKey("CustomName") && ((NBTTagCompound)is.getTagCompound().getTag(ItemBeeBarker.WOLF_DATA_STRING)).getString("CustomName").equals("iChun"))
                    {
                        if(player.ticksExisted % 4 == 0)
                        {
                            MovingObjectPosition mop = EntityHelperBase.getEntityLook(player, 6D);
                            if(mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && !mop.entityHit.isImmuneToFire())
                            {
                                mop.entityHit.setFire(2);
                                mop.entityHit.attackEntityFrom((new EntityDamageSourceIndirect("beeburnt", mop.entityHit, player)).setFireDamage(), 2);
                            }
                        }
                        if(player.ticksExisted % 13 == 0)
                        {
                            player.worldObj.playSoundAtEntity(player, "mob.wolf.panting", 0.6F, 1.0F);
                        }
                    }
                    else
                    {
                        removePressState(name);
                    }
                }
                else
                {
                    removePressState(name);
                }
            }
        }
    }

    public static void removePressState(String name)
    {
        if(pressState.contains(name))
        {
            pressState.remove(name);
            BeeBarker.channel.sendToAll(new PacketKeyState(name, false));
        }
    }

    public static HashMap<String, Integer> cooldown = new HashMap<String, Integer>();

    public static ArrayList<String> pressState = new ArrayList<String>();
}
