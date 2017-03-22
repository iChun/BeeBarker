package me.ichun.mods.beebarker.common.core;

import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.entity.EntityBee;
import me.ichun.mods.beebarker.common.item.ItemBeeBarker;
import me.ichun.mods.beebarker.common.packet.PacketSpawnParticles;
import me.ichun.mods.ichunutil.common.item.ItemHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class EventHandlerServer
{
    public static final String BARKABLE_STRING = "BeeBarker_barkable";
    public static final String BEE_HIGHEST_CHARGE = "BeeBarker_beeHighestCharge";
    public static final String BEE_CHARGE_STRING = "BeeBarker_beeCharge";

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event)
    {
        ItemStack is = ItemHandler.getUsableDualHandedItem(event.getPlayer());
        if(is != null && is.getItem() instanceof ItemBeeBarker)
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onClickBlock(PlayerInteractEvent.LeftClickBlock event)
    {
        ItemStack is = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
        if(is != null && is.getItem() instanceof ItemBeeBarker)
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event)
    {
        if(!event.getEntityLiving().worldObj.isRemote && event.getEntityLiving() instanceof EntityZombie)
        {
            EntityZombie zombie = (EntityZombie)event.getEntityLiving();
            if(zombie.getHeldItem(EnumHand.MAIN_HAND) != null && zombie.getHeldItem(EnumHand.MAIN_HAND).getItem() == BeeBarker.itemBeeBarker)
            {
                if(zombie.getRNG().nextFloat() < 0.008F)
                {
                    BarkHelper.bark(zombie);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlaySoundAtEntity(PlaySoundAtEntityEvent event)
    {
        if(event.getEntity() instanceof EntityWolf && event.getSound() == SoundEvents.ENTITY_WOLF_AMBIENT && !event.getEntity().worldObj.isRemote)
        {
            EntityWolf wolf = (EntityWolf)event.getEntity();
            NBTTagCompound wolfData = wolf.getEntityData();
            if(wolfData.getBoolean(BARKABLE_STRING) && wolf.getRNG().nextFloat() < 0.05F)
            {
                for(int i = 0; i < wolf.getRNG().nextInt(5) + 1; i++)
                {
                    wolf.worldObj.spawnEntityInWorld(new EntityBee(wolf.worldObj, wolf));
                }
            }
        }
    }

    //Test the entity interact
    @SubscribeEvent
    public void onEntityInteract(EntityInteract event)
    {
        if(event.getTarget() instanceof EntityWolf)
        {
            EntityWolf wolf = (EntityWolf)event.getTarget();
            if(wolf.isTamed() && !wolf.isInvisible() && wolf.getOwner() == event.getEntityPlayer() && !event.getEntityPlayer().isSneaking())
            {
                ItemStack is = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
                boolean forestryCheck = false; //Check if the item is a forestry bee.
                if(BeeBarker.isForestryInstalled)
                {
                    try
                    {
                        Class clz = Class.forName("forestry.apiculture.items.ItemBeeGE");
                        if(clz.isInstance(is.getItem()))
                        {
                            forestryCheck = true;
                        }
                    }
                    catch(ClassNotFoundException ignored){}
                }
                if(is == null)
                {
                    NBTTagCompound wolfData = wolf.getEntityData();
                    if(wolfData.getBoolean(BARKABLE_STRING))
                    {
                        if(!event.getEntityPlayer().worldObj.isRemote)
                        {
                            NBTTagCompound wolfTag = new NBTTagCompound();
                            wolfData.setInteger(BEE_HIGHEST_CHARGE, wolfData.getInteger(BEE_CHARGE_STRING));
                            wolf.setSitting(false);
                            wolf.writeToNBTOptional(wolfTag);
                            ItemStack wolfStack = new ItemStack(BeeBarker.itemBeeBarker, 1, wolfData.getBoolean("IsSuperBeeDog") ? 0 : wolfData.getInteger(BEE_CHARGE_STRING) == 0 ? 250 : 1); //1 damage so you can see the damage bar.
                            wolfStack.setTagCompound(new NBTTagCompound());
                            wolfStack.getTagCompound().setTag(ItemBeeBarker.WOLF_DATA_STRING, wolfTag);
                            event.getEntityPlayer().setHeldItem(EnumHand.MAIN_HAND, wolfStack);
                            event.getEntityPlayer().inventory.markDirty();
                            wolf.setDead();
                        }
                        event.setCanceled(true);
                    }
                }
                else if(Block.getBlockFromItem(is.getItem()) instanceof BlockFlower || forestryCheck)
                {
                    NBTTagCompound wolfData = wolf.getEntityData();
                    if(!event.getEntityPlayer().worldObj.isRemote && !event.getEntityPlayer().capabilities.isCreativeMode)
                    {
                        is.stackSize--;
                        if(is.stackSize <= 0)
                        {
                            event.getEntityPlayer().inventory.mainInventory[event.getEntityPlayer().inventory.currentItem] = null;
                            event.getEntityPlayer().inventory.markDirty();
                        }
                    }

                    if(!event.getEntityPlayer().worldObj.isRemote)
                    {
                        float chance = forestryCheck ? 0.0F : event.getEntityPlayer().getRNG().nextFloat(); //If forestryCheck, change = 0, bee will be added.
                        if(wolfData.getBoolean("IsSuperBeeDog") || wolfData.getBoolean(BARKABLE_STRING) && wolfData.getInteger(BEE_CHARGE_STRING) >= BeeBarker.config.maxBeeCharge)
                        {
                            wolf.playSound(SoundEvents.ENTITY_WOLF_WHINE, 0.4F, (wolf.getRNG().nextFloat() - wolf.getRNG().nextFloat()) * 0.2F + 1F);
                        }
                        else
                        {
                            if(chance < BeeBarker.config.beeBarkerChance / 100F)
                            {
                                BeeBarker.channel.sendToAllAround(new PacketSpawnParticles(wolf.getEntityId(), event.getEntityPlayer().getEntityId(), false), new NetworkRegistry.TargetPoint(wolf.dimension, wolf.posX, wolf.posY, wolf.posZ, 64D));
                                wolfData.setBoolean(BARKABLE_STRING, true);
                                wolfData.setInteger(BEE_CHARGE_STRING, wolfData.getInteger(BEE_CHARGE_STRING) + 1);
                                wolfData.setInteger(BEE_HIGHEST_CHARGE, wolfData.getInteger(BEE_CHARGE_STRING));
                            }
                            else
                            {
                                BeeBarker.channel.sendToAllAround(new PacketSpawnParticles(wolf.getEntityId(), event.getEntityPlayer().getEntityId(), true), new NetworkRegistry.TargetPoint(wolf.dimension, wolf.posX, wolf.posY, wolf.posZ, 64D));
                            }
                            wolf.playSound(SoundEvents.ENTITY_PLAYER_BURP, 0.3F, 1.0F + (wolf.getRNG().nextFloat() - wolf.getRNG().nextFloat()) * 0.2F);
                        }
                    }
                    event.setCanceled(true);
                }
            }
        }
    }

    //Recreate the entity
    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event)
    {
        if(!event.getEntityLiving().worldObj.isRemote)
        {
            for(int i = event.getDrops().size() - 1; i >= 0; i--)
            {
                if(spawnWolfFromItem(event.getDrops().get(i), null))
                {
                    event.getDrops().remove(i);
                }
            }
        }
    }

    @SubscribeEvent
    public void onItemToss(ItemTossEvent event)
    {
        if(!event.getPlayer().worldObj.isRemote)
        {
            if(spawnWolfFromItem(event.getEntityItem(), event.getPlayer()))
            {
                event.setCanceled(true);
            }
        }
    }

    public static boolean spawnWolfFromItem(EntityItem item, EntityLivingBase dropper)
    {
        ItemStack is = item.getEntityItem();
        if(is.getItem() instanceof ItemBeeBarker && is.getTagCompound() != null && is.getTagCompound().hasKey(ItemBeeBarker.WOLF_DATA_STRING))
        {
            Entity ent = EntityList.createEntityFromNBT((NBTTagCompound)is.getTagCompound().getTag(ItemBeeBarker.WOLF_DATA_STRING), item.worldObj);
            if(ent instanceof EntityWolf)
            {
                if(dropper != null)
                {
                    ent.setLocationAndAngles(item.posX, item.posY, item.posZ, dropper.rotationYaw, dropper.rotationPitch);
                }
                else
                {
                    ent.setPosition(item.posX, item.posY, item.posZ);
                }
                ent.motionX = item.motionX;
                ent.motionY = item.motionY;
                ent.motionZ = item.motionZ;
                ent.fallDistance = 0F;

                ent.worldObj.spawnEntityInWorld(ent);
                return true;
            }
        }
        return false;
    }
}
