package me.ichun.mods.beebarker.client.core;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import us.ichun.mods.ichunutil.common.core.EntityHelperBase;

import java.util.ArrayList;

public class TickHandlerClient
{
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            Minecraft mc = Minecraft.getMinecraft();
            if(mc.theWorld != null && !mc.isGamePaused())
            {
                prevYaw = currentYaw;
                prevPitch = currentPitch;
                currentYaw = EntityHelperBase.updateRotation(currentYaw, targetYaw, 10F);
                currentPitch = EntityHelperBase.updateRotation(currentPitch, targetPitch, 10F);
                if(pullTime > 0)
                {
                    if(!(pressState.contains(mc.thePlayer.getCommandSenderName()) && pullTime == 7))
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
                        if(mc.theWorld.rand.nextFloat() < 0.008F)
                        {
                            targetYaw = mc.theWorld.rand.nextFloat() * 90F - 45F;
                            targetPitch = mc.theWorld.rand.nextFloat() * 60F - 30F;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END && event.side.isClient() && pressState.contains(event.player.getCommandSenderName()))
        {
            Vec3 look = event.player.getLookVec();
            Vec3 pos;
            if(event.player == Minecraft.getMinecraft().getRenderViewEntity() && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) //is first person
            {
                pos = event.player.getPositionVector().addVector(look.xCoord * 1.5D - look.zCoord * 0.75D, look.yCoord * 1.5D + 1.15D, look.zCoord * 1.5D + look.xCoord * 0.75D);
            }
            else
            {
                pos = event.player.getPositionVector().addVector(look.xCoord * 1.5D - look.zCoord * (event.player.width * 0.2D), look.yCoord * 1.5D + (event.player.getEyeHeight() * 0.8D), look.zCoord * 1.5D + look.xCoord * (event.player.width * 0.2D));
            }
            for(int i = 0; i < 4; i++)
            {
                double d0 = event.player.worldObj.rand.nextGaussian() * 0.02D;
                double d1 = event.player.worldObj.rand.nextGaussian() * 0.02D + event.player.worldObj.rand.nextFloat() * 0.1D;
                double d2 = event.player.worldObj.rand.nextGaussian() * 0.02D;
                event.player.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.xCoord, pos.yCoord, pos.zCoord, look.xCoord * 0.3D + d0, look.yCoord * 0.3D + d1, look.zCoord * 0.3D + d2);
                event.player.worldObj.spawnParticle(EnumParticleTypes.FLAME, pos.xCoord, pos.yCoord, pos.zCoord, look.xCoord * 0.3D + d0, look.yCoord * 0.3D + d1, look.zCoord * 0.3D + d2);
            }
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
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

    public ArrayList<String> pressState = new ArrayList<String>();
}
