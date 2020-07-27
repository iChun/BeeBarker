package me.ichun.mods.beebarker.client.core;

import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.item.ItemBeeBarker;
import me.ichun.mods.beebarker.common.packet.PacketBark;
import me.ichun.mods.ichunutil.common.entity.util.EntityHelper;
import me.ichun.mods.ichunutil.common.item.DualHandedItem;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;

public class EventHandlerClient
{
    public boolean pressing = false;

    @SubscribeEvent
    public void onClickInput(InputEvent.ClickInputEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        ItemStack currentInv = DualHandedItem.getUsableDualHandedItem(mc.player);
        if(currentInv.getItem() instanceof ItemBeeBarker && (event.isAttack() || event.isUseItem()))
        {
            event.setSwingHand(false);
            event.setCanceled(true);

            if(event.isUseItem())
            {
                BeeBarker.channel.sendToServer(new PacketBark(true));
                BeeBarker.eventHandlerClient.pullTime = EventHandlerClient.PULL_TIME;
                pressing = true;
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            Minecraft mc = Minecraft.getInstance();
            if(mc.player != null && !mc.isGamePaused())
            {
                prevYaw = currentYaw;
                prevPitch = currentPitch;
                currentYaw = EntityHelper.updateRotation(currentYaw, targetYaw, 10F);
                currentPitch = EntityHelper.updateRotation(currentPitch, targetPitch, 10F);
                if(pullTime > 0)
                {
                    if(!(pressState.contains(mc.player.getName().getUnformattedComponentText()) && pullTime == 7))
                    {
                        pullTime--;
                    }
                    idleTime = 0;
                    prevYaw = prevPitch = currentYaw = currentPitch = targetYaw = targetPitch = 0F;
                }
                else
                {
                    idleTime++;
                    if(idleTime > 60) //3 second idle.
                    {
                        if(mc.world.rand.nextFloat() < 0.008F)
                        {
                            targetYaw = mc.world.rand.nextFloat() * 90F - 45F;
                            targetPitch = mc.world.rand.nextFloat() * 60F - 30F;
                        }
                    }
                }

                if(pressing && !mc.gameSettings.keyBindUseItem.isKeyDown())
                {
                    pressing = false;
                    ItemStack currentInv = DualHandedItem.getUsableDualHandedItem(mc.player);
                    if(currentInv.getItem() instanceof ItemBeeBarker)
                    {
                        BeeBarker.channel.sendToServer(new PacketBark(false));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END && event.side.isClient() && pressState.contains(event.player.getName().getUnformattedComponentText()))
        {
            Vector3d look = event.player.getLookVec();
            Vector3d pos;
            HandSide side = DualHandedItem.getHandSide(event.player, DualHandedItem.getUsableDualHandedItem(event.player));
            if(event.player == Minecraft.getInstance().getRenderViewEntity() && Minecraft.getInstance().gameSettings.thirdPersonView == 0) //is first person
            {
                double d = - look.z * 0.75D;
                double d1 = look.y * 1.5D + 1.15D;
                double d2 = + look.x * 0.75D;
                if(side == HandSide.LEFT)
                {
                    d = -d;
                    d2 = -d2;
                }
                pos = event.player.getPositionVec().add(look.x * 1.5D + d, d1, look.z * 1.5D + d2);
            }
            else
            {
                double d = - look.z * (event.player.getWidth() * 0.2D);
                double d1 = look.y * 1.5D + (event.player.getEyeHeight() * 0.8D);
                double d2 = + look.x * (event.player.getWidth() * 0.2D);
                if(side == HandSide.LEFT)
                {
                    d = -d;
                    d2 = -d2;
                }
                pos = event.player.getPositionVec().add(look.x * 1.5D + d, d1, look.z * 1.5D + d2);
            }
            for(int i = 0; i < 4; i++)
            {
                double d0 = event.player.world.rand.nextGaussian() * 0.02D;
                double d1 = event.player.world.rand.nextGaussian() * 0.02D + event.player.world.rand.nextFloat() * 0.1D;
                double d2 = event.player.world.rand.nextGaussian() * 0.02D;
                event.player.world.addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, look.x * 0.3D + d0, look.y * 0.3D + d1, look.z * 0.3D + d2);
                event.player.world.addParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, look.x * 0.3D + d0, look.y * 0.3D + d1, look.z * 0.3D + d2);
            }
        }
    }

    @SubscribeEvent
    public void onDisconnect(ClientPlayerNetworkEvent.LoggedOutEvent event)
    {
        pressState.clear();
    }

    public static final int PULL_TIME = 10;
    public int pullTime;
    public int idleTime;
    public float prevYaw;
    public float prevPitch;
    public float currentYaw;
    public float currentPitch;
    public float targetYaw;
    public float targetPitch;

    public ArrayList<String> pressState = new ArrayList<>();
}
