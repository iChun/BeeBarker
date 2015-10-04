package me.ichun.mods.beebarker.common.core;

import me.ichun.mods.beebarker.client.core.TickHandlerClient;
import me.ichun.mods.beebarker.client.render.ItemRenderBeeBarker;
import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.entity.EntityBee;
import me.ichun.mods.beebarker.common.item.ItemBeeBarker;
import me.ichun.mods.beebarker.common.packet.PacketBark;
import me.ichun.mods.beebarker.common.packet.PacketSpawnParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.ichun.mods.ichunutil.client.keybind.KeyEvent;
import us.ichun.mods.ichunutil.client.model.itemblock.PerspectiveAwareModelBaseWrapper;
import us.ichun.mods.ichunutil.common.iChunUtil;

public class EventHandler
{
    public static final String BARKABLE_STRING = "BeeBarker_barkable";
    public static final String BEE_HIGHEST_CHARGE = "BeeBarker_beeHighestCharge";
    public static final String BEE_CHARGE_STRING = "BeeBarker_beeCharge";

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event)
    {
        ((ItemBeeBarker)BeeBarker.itemBeeBarker).renderer = new ItemRenderBeeBarker();
        event.modelRegistry.putObject(new ModelResourceLocation("beebarker:BeeBarker", "inventory"), new PerspectiveAwareModelBaseWrapper(((ItemBeeBarker)BeeBarker.itemBeeBarker).renderer));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKeyEvent(KeyEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if(mc.currentScreen == null && !iChunUtil.proxy.tickHandlerClient.hasScreen)
        {
            ItemStack currentInv = mc.thePlayer.inventory.getCurrentItem();
            if(currentInv != null && currentInv.getItem() instanceof ItemBeeBarker)
            {
                if(event.keyBind.isMinecraftBind() && event.keyBind.keyIndex == mc.gameSettings.keyBindUseItem.getKeyCode())
                {
                    BeeBarker.channel.sendToServer(new PacketBark(BeeBarker.config.easterEgg != 1 || event.keyBind.isPressed()));
                    if(event.keyBind.isPressed())
                    {
                        BeeBarker.proxy.tickHandlerClient.pullTime = TickHandlerClient.PULL_TIME;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event)
    {
        if(event.getPlayer().getHeldItem() != null && event.getPlayer().getHeldItem().getItem() instanceof ItemBeeBarker)
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event)
    {
        if(!event.entityLiving.worldObj.isRemote && event.entityLiving instanceof EntityZombie)
        {
            EntityZombie zombie = (EntityZombie)event.entityLiving;
            if(zombie.getHeldItem() != null && zombie.getHeldItem().getItem() == BeeBarker.itemBeeBarker)
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
        if(event.entity instanceof EntityWolf && event.name.equals("mob.wolf.bark") && !event.entity.worldObj.isRemote)
        {
            EntityWolf wolf = (EntityWolf)event.entity;
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
    public void onEntityInteract(EntityInteractEvent event)
    {
        if(event.target instanceof EntityWolf)
        {
            EntityWolf wolf = (EntityWolf)event.target;
            if(wolf.isTamed() && !wolf.isInvisible() && wolf.getOwner() == event.entityPlayer && !event.entityPlayer.isSneaking())
            {
                ItemStack is = event.entityPlayer.getHeldItem();
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
                        if(!event.entityPlayer.worldObj.isRemote)
                        {
                            NBTTagCompound wolfTag = new NBTTagCompound();
                            wolfData.setInteger(BEE_HIGHEST_CHARGE, wolfData.getInteger(BEE_CHARGE_STRING));
                            wolf.setSitting(false);
                            wolf.writeToNBTOptional(wolfTag);
                            ItemStack wolfStack = new ItemStack(BeeBarker.itemBeeBarker, 1, wolfData.getBoolean("IsSuperBeeDog") ? 0 : wolfData.getInteger(BEE_CHARGE_STRING) == 0 ? 250 : 1); //1 damage so you can see the damage bar.
                            wolfStack.setTagCompound(new NBTTagCompound());
                            wolfStack.getTagCompound().setTag(ItemBeeBarker.WOLF_DATA_STRING, wolfTag);
                            event.entityPlayer.setCurrentItemOrArmor(0, wolfStack);
                            event.entityPlayer.inventory.markDirty();
                            wolf.setDead();
                        }
                        event.setCanceled(true);
                    }
                }
                else if(Block.getBlockFromItem(is.getItem()) instanceof BlockFlower || forestryCheck)
                {
                    NBTTagCompound wolfData = wolf.getEntityData();
                    if(!event.entityPlayer.worldObj.isRemote && !event.entityPlayer.capabilities.isCreativeMode)
                    {
                        is.stackSize--;
                        if(is.stackSize <= 0)
                        {
                            event.entityPlayer.inventory.mainInventory[event.entityPlayer.inventory.currentItem] = null;
                            event.entityPlayer.inventory.markDirty();
                        }
                    }

                    if(!event.entityPlayer.worldObj.isRemote)
                    {
                        float chance = forestryCheck ? 0.0F : event.entityPlayer.getRNG().nextFloat(); //If forestryCheck, change = 0, bee will be added.
                        if(wolfData.getBoolean("IsSuperBeeDog") || wolfData.getBoolean(BARKABLE_STRING) && wolfData.getInteger(BEE_CHARGE_STRING) >= BeeBarker.config.maxBeeCharge)
                        {
                            wolf.playSound("mob.wolf.whine", 0.4F, (wolf.getRNG().nextFloat() - wolf.getRNG().nextFloat()) * 0.2F + 1F);
                        }
                        else
                        {
                            if(chance < BeeBarker.config.beeBarkerChance / 100F)
                            {
                                BeeBarker.channel.sendToAllAround(new PacketSpawnParticles(wolf.getEntityId(), event.entityPlayer.getEntityId(), false), new NetworkRegistry.TargetPoint(wolf.dimension, wolf.posX, wolf.posY, wolf.posZ, 64D));
                                wolfData.setBoolean(BARKABLE_STRING, true);
                                wolfData.setInteger(BEE_CHARGE_STRING, wolfData.getInteger(BEE_CHARGE_STRING) + 1);
                                wolfData.setInteger(BEE_HIGHEST_CHARGE, wolfData.getInteger(BEE_CHARGE_STRING));
                            }
                            else
                            {
                                BeeBarker.channel.sendToAllAround(new PacketSpawnParticles(wolf.getEntityId(), event.entityPlayer.getEntityId(), true), new NetworkRegistry.TargetPoint(wolf.dimension, wolf.posX, wolf.posY, wolf.posZ, 64D));
                            }
                            wolf.playSound("random.burp", 0.3F, 1.0F + (wolf.getRNG().nextFloat() - wolf.getRNG().nextFloat()) * 0.2F);
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
        if(!event.entityLiving.worldObj.isRemote)
        {
            for(int i = event.drops.size() - 1; i >= 0; i--)
            {
                if(spawnWolfFromItem(event.drops.get(i), null))
                {
                    event.drops.remove(i);
                }
            }
        }
    }

    @SubscribeEvent
    public void onItemToss(ItemTossEvent event)
    {
        if(!event.player.worldObj.isRemote)
        {
            if(spawnWolfFromItem(event.entityItem, event.player))
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
