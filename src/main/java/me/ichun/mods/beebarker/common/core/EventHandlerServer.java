package me.ichun.mods.beebarker.common.core;

import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.goal.FlowerBegGoal;
import me.ichun.mods.beebarker.common.item.ItemBeeBarker;
import me.ichun.mods.beebarker.common.packet.PacketSpawnParticles;
import me.ichun.mods.ichunutil.common.entity.util.EntityHelper;
import me.ichun.mods.ichunutil.common.item.DualHandedItem;
import net.minecraft.block.Block;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.BegGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Iterator;
import java.util.Map;

public class EventHandlerServer
{
    public static final String BARKABLE_STRING = "BeeBarker_barkable";
    public static final String BEE_HIGHEST_CHARGE = "BeeBarker_beeHighestCharge";
    public static final String BEE_CHARGE_STRING = "BeeBarker_beeCharge";

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event)
    {
        ItemStack is = DualHandedItem.getUsableDualHandedItem(event.getPlayer());
        if(is.getItem() instanceof ItemBeeBarker)
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onClickBlock(PlayerInteractEvent.LeftClickBlock event)
    {
        ItemStack is = event.getPlayer().getHeldItem(Hand.MAIN_HAND);
        if(is.getItem() == BeeBarker.Items.BEE_BARKER.get())
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event)
    {
        if(!event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof ZombieEntity)
        {
            ZombieEntity zombie = (ZombieEntity)event.getEntityLiving();
            if(zombie.getHeldItem(Hand.MAIN_HAND).getItem() == BeeBarker.Items.BEE_BARKER.get())
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
        if(event.getEntity() instanceof WolfEntity && event.getSound() == SoundEvents.ENTITY_WOLF_AMBIENT && !event.getEntity().world.isRemote)
        {
            WolfEntity wolf = (WolfEntity)event.getEntity();
            CompoundNBT wolfData = wolf.getPersistentData();
            if(wolfData.getBoolean(BARKABLE_STRING) && wolf.getRNG().nextFloat() < 0.05F)
            {
                for(int i = 0; i < wolf.getRNG().nextInt(5) + 1; i++)
                {
                    wolf.world.addEntity(BeeBarker.EntityTypes.BEE.get().create(wolf.world).setShooter(wolf));
                }
            }
        }
    }

    //Test the entity interact
    @SubscribeEvent
    public void onEntityInteract(EntityInteract event)
    {
        if(event.getTarget() instanceof WolfEntity)
        {
            WolfEntity wolf = (WolfEntity)event.getTarget();
            if(wolf.isTamed() && !wolf.isInvisible() && wolf.getOwner() == event.getPlayer() && !event.getPlayer().isSneaking())
            {
                ItemStack is = event.getPlayer().getHeldItem(Hand.MAIN_HAND);
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
                if(is.isEmpty())
                {
                    CompoundNBT wolfData = wolf.getPersistentData();
                    if(wolfData.getBoolean(BARKABLE_STRING))
                    {
                        if(!event.getPlayer().world.isRemote)
                        {
                            CompoundNBT wolfTag = new CompoundNBT();
                            wolfData.putInt(BEE_HIGHEST_CHARGE, wolfData.getInt(BEE_CHARGE_STRING));
                            wolf.func_233686_v_(false); //setSitting
                            wolf.writeUnlessRemoved(wolfTag);
                            ItemStack wolfStack = new ItemStack(BeeBarker.Items.BEE_BARKER.get()); //1 damage so you can see the damage bar.
                            wolfStack.setDamage(wolfData.getBoolean("IsSuperBeeDog") ? 0 : wolfData.getInt(BEE_CHARGE_STRING) == 0 ? 250 : 1);
                            wolfStack.setTag(new CompoundNBT());
                            wolfStack.getTag().put(ItemBeeBarker.WOLF_DATA_STRING, wolfTag);
                            event.getPlayer().setHeldItem(Hand.MAIN_HAND, wolfStack);
                            event.getPlayer().inventory.markDirty();
                            wolf.remove();
                        }
                        event.setCanceled(true);
                    }
                }
                else if(Block.getBlockFromItem(is.getItem()) instanceof FlowerBlock || forestryCheck)
                {
                    CompoundNBT wolfData = wolf.getPersistentData();
                    if(!event.getPlayer().world.isRemote && !event.getPlayer().abilities.isCreativeMode)
                    {
                        is.shrink(1);
                        if(is.isEmpty())
                        {
                            event.getPlayer().inventory.mainInventory.set(event.getPlayer().inventory.currentItem, ItemStack.EMPTY);
                            event.getPlayer().inventory.markDirty();
                        }
                    }

                    if(!event.getPlayer().world.isRemote)
                    {
                        float chance = forestryCheck ? 0.0F : event.getPlayer().getRNG().nextFloat(); //If forestryCheck, change = 0, bee will be added.
                        if(wolfData.getBoolean("IsSuperBeeDog") || wolfData.getBoolean(BARKABLE_STRING) && wolfData.getInt(BEE_CHARGE_STRING) >= BeeBarker.configCommon.maxBeeCharge)
                        {
                            wolf.playSound(SoundEvents.ENTITY_WOLF_WHINE, 0.4F, (wolf.getRNG().nextFloat() - wolf.getRNG().nextFloat()) * 0.2F + 1F);
                        }
                        else
                        {
                            if(chance < BeeBarker.configCommon.beeBarkerChance / 100F)
                            {
                                BeeBarker.channel.sendTo(new PacketSpawnParticles(wolf.getEntityId(), event.getPlayer().getEntityId(), false), PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(wolf.getPosX(), wolf.getPosY(), wolf.getPosZ(), 64D, wolf.world.func_234923_W_())));
                                wolfData.putBoolean(BARKABLE_STRING, true);
                                wolfData.putInt(BEE_CHARGE_STRING, wolfData.getInt(BEE_CHARGE_STRING) + 1);
                                wolfData.putInt(BEE_HIGHEST_CHARGE, wolfData.getInt(BEE_CHARGE_STRING));
                            }
                            else
                            {
                                BeeBarker.channel.sendTo(new PacketSpawnParticles(wolf.getEntityId(), event.getPlayer().getEntityId(), true), PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(wolf.getPosX(), wolf.getPosY(), wolf.getPosZ(), 64D, wolf.world.func_234923_W_())));
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
    public void onEntitySpawn(EntityJoinWorldEvent event)
    {
        if(!event.getWorld().isRemote)
        {
            if(event.getEntity() instanceof WolfEntity)
            {
                WolfEntity wolf = (WolfEntity)event.getEntity();
                for(PrioritizedGoal goal : wolf.goalSelector.goals)
                {
                    if(goal.inner instanceof BegGoal)
                    {
                        goal.inner = new FlowerBegGoal((BegGoal)goal.inner);
                    }
                }
            }
            else if(event.getEntity() instanceof ItemEntity && spawnWolfFromItem(null, (ItemEntity)event.getEntity(), null))
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event)
    {
        if(!event.getEntityLiving().world.isRemote)
        {
            event.getDrops().removeIf(item -> spawnWolfFromItem(null, item, null));
        }
    }

    @SubscribeEvent
    public void onItemToss(ItemTossEvent event)
    {
        if(!event.getPlayer().world.isRemote)
        {
            if(spawnWolfFromItem(event.getPlayer(), event.getEntityItem(), event.getPlayer()))
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            Iterator<Map.Entry<String, Integer>> ite = BarkHelper.cooldown.entrySet().iterator();
            while(ite.hasNext())
            {
                Map.Entry<String, Integer> e = ite.next();
                if(e.getValue() > 0)
                {
                    if(e.getValue() > 120)
                    {
                        PlayerEntity player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUsername(e.getKey());
                        if(player != null)
                        {
                            BeeBarker.channel.sendTo(new PacketSpawnParticles(-1, player.getEntityId(), true), PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(player.getPosX(), player.getPosY(), player.getPosZ(), 32D, player.world.func_234923_W_())));
                        }
                    }
                    e.setValue(e.getValue() - 2);
                }
                else
                {
                    ite.remove();
                }
            }
            for(int i = BarkHelper.pressState.size() - 1; i >= 0; i--)
            {
                String name = BarkHelper.pressState.get(i);
                PlayerEntity player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUsername(name);
                if(player != null)
                {
                    ItemStack is = DualHandedItem.getUsableDualHandedItem(player);
                    if(is.getItem() == BeeBarker.Items.BEE_BARKER.get() && is.getTag() != null && is.getTag().contains(ItemBeeBarker.WOLF_DATA_STRING) && BeeBarker.configServer.easterEgg && is.getTag().getCompound(ItemBeeBarker.WOLF_DATA_STRING).contains("CustomName") && is.getTag().getCompound(ItemBeeBarker.WOLF_DATA_STRING).getString("CustomName").contains("\"text\":\"iChun\""))
                    {
                        if(player.ticksExisted % 4 == 0)
                        {
                            RayTraceResult result = EntityHelper.getEntityLook(player, 6D);
                            if(result.getType() == RayTraceResult.Type.ENTITY && !((EntityRayTraceResult)result).getEntity().isImmuneToFire())
                            {
                                Entity ent = ((EntityRayTraceResult)result).getEntity();
                                ent.setFire(2);
                                ent.attackEntityFrom((new IndirectEntityDamageSource("beeburnt", ent, player)).setFireDamage(), 2);
                            }
                        }
                        if(player.ticksExisted % 13 == 0)
                        {
                            EntityHelper.playSound(player, SoundEvents.ENTITY_WOLF_PANT, SoundCategory.PLAYERS, 0.6F, 1F);
                        }
                    }
                    else
                    {
                        BarkHelper.removePressState(name);
                    }
                }
                else
                {
                    BarkHelper.removePressState(name);
                }
            }
        }
    }

    public static boolean spawnWolfFromItem(PlayerEntity player, ItemEntity item, LivingEntity dropper)
    {
        ItemStack is = item.getItem();
        if(is.getItem() instanceof ItemBeeBarker && is.getTag() != null && is.getTag().contains(ItemBeeBarker.WOLF_DATA_STRING))
        {
            if(!is.getTag().getCompound(ItemBeeBarker.WOLF_DATA_STRING).contains("id"))
            {
                WolfEntity wolf = EntityType.WOLF.create(item.world);
                if(player != null)
                {
                    wolf.setOwnerId(player.getGameProfile().getId());
                    wolf.setTamed(true);
                    wolf.setCollarColor(DyeColor.RED);
                }
                if(is.getTag().getCompound(ItemBeeBarker.WOLF_DATA_STRING).contains("CustomName"))
                {
                    wolf.setCollarColor(DyeColor.BLUE);
                    wolf.setCustomName(new StringTextComponent(is.getTag().getCompound(ItemBeeBarker.WOLF_DATA_STRING).getString("CustomName")));
                }
                wolf.setHealth(20.0F);
                wolf.getPersistentData().putBoolean(EventHandlerServer.BARKABLE_STRING, true);
                wolf.getPersistentData().putBoolean("IsSuperBeeDog", true);

                CompoundNBT wolfTag = new CompoundNBT();
                wolf.writeUnlessRemoved(wolfTag);
                is.getTag().put(ItemBeeBarker.WOLF_DATA_STRING, wolfTag);
            }

            Entity entity = EntityType.loadEntityAndExecute(is.getTag().getCompound(ItemBeeBarker.WOLF_DATA_STRING), item.world, (ent -> {
                if(dropper != null)
                {
                    ent.setLocationAndAngles(item.getPosX(), item.getPosY(), item.getPosZ(), dropper.rotationYaw, dropper.rotationPitch);
                }
                else
                {
                    ent.setPosition(item.getPosX(), item.getPosY(), item.getPosZ());
                }

                if(ent instanceof WolfEntity)
                {
                    CompoundNBT wolfData = ent.getPersistentData();
                    if(wolfData.getBoolean(BARKABLE_STRING))
                    {
                        if(wolfData.getInt(BEE_CHARGE_STRING) <= 0)
                        {
                            wolfData.remove(BARKABLE_STRING);
                            wolfData.remove(BEE_HIGHEST_CHARGE);
                            wolfData.remove(BEE_CHARGE_STRING);
                        }
                    }
                }
                ent.setMotion(item.getMotion());
                ent.fallDistance = 0F;

                ent.world.addEntity(ent);

                return ent;
            }));
            return entity != null;
        }
        return false;
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event)
    {
        BarkHelper.cooldown.clear();
        BarkHelper.pressState.clear();
    }
}
