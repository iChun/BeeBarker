package me.ichun.mods.beebarker.common.core;

import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.item.ItemBeeBarker;
import me.ichun.mods.beebarker.common.packet.PacketKeyState;
import me.ichun.mods.beebarker.common.packet.PacketSpawnParticles;
import me.ichun.mods.ichunutil.common.entity.util.EntityHelper;
import me.ichun.mods.ichunutil.common.item.DualHandedItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;

public class BarkHelper
{
    public static void bark(LivingEntity living)
    {
        float pitch = 1F;
        if(living instanceof PlayerEntity)
        {
            Integer speed = cooldown.get(living.getName().getUnformattedComponentText());
            if(speed == null)
            {
                speed = 0;
            }
            if(speed < 252)
            {
                cooldown.put(living.getName().getUnformattedComponentText(), speed + 8);
            }

            if(speed > 10)
            {
                float pitchSpike = (float)Math.pow(MathHelper.clamp((speed - 10) / 250F, 0F, 1F), 1D);
                pitch += 1.5F * pitchSpike;
            }

            PlayerEntity player = (PlayerEntity)living;
            ItemStack is = DualHandedItem.getUsableDualHandedItem(player);
            if(is.getItem() instanceof ItemBeeBarker && is.getTag() != null && is.getTag().contains(ItemBeeBarker.WOLF_DATA_STRING) && !player.abilities.isCreativeMode)
            {
                CompoundNBT tag = is.getTag().getCompound(ItemBeeBarker.WOLF_DATA_STRING).getCompound("ForgeData");

                if(!tag.getBoolean("IsSuperBeeDog"))
                {
                    if(tag.getInt(EventHandlerServer.BEE_CHARGE_STRING) <= 0)
                    {
                        EntityHelper.playSound(living, SoundEvents.ENTITY_WOLF_HURT, SoundCategory.PLAYERS, 0.4F, (living.world.rand.nextFloat() - living.world.rand.nextFloat()) * 0.2F + pitch);
                        return;
                    }
                    tag.putInt(EventHandlerServer.BEE_CHARGE_STRING, tag.getInt(EventHandlerServer.BEE_CHARGE_STRING) - 1);
                    is.setDamage(1 + (int)((1.0F - (tag.getInt(EventHandlerServer.BEE_CHARGE_STRING) / (float)tag.getInt(EventHandlerServer.BEE_HIGHEST_CHARGE))) * 250F));
                }
            }
        }
        else
        {
            BeeBarker.channel.sendTo(new PacketSpawnParticles(-1, living.getEntityId(), true), PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(living.getPosX(), living.getPosY(), living.getPosZ(), 32D, living.world.func_234923_W_())));
        }
        for(int i = 0; i < BeeBarker.configCommon.beeCount; i++)
        {
            living.world.addEntity(BeeBarker.EntityTypes.BEE.get().create(living.world).setShooter(living));
        }
        EntityHelper.playSound(living, SoundEvents.ENTITY_WOLF_AMBIENT, SoundCategory.PLAYERS, 0.4F, (living.world.rand.nextFloat() - living.world.rand.nextFloat()) * 0.2F + pitch);
    }

    public static void removePressState(String name)
    {
        if(pressState.contains(name))
        {
            pressState.remove(name);
            BeeBarker.channel.sendTo(new PacketKeyState(name, false), PacketDistributor.ALL.noArg());
        }
    }

    public static HashMap<String, Integer> cooldown = new HashMap<>();

    public static ArrayList<String> pressState = new ArrayList<>();
}
