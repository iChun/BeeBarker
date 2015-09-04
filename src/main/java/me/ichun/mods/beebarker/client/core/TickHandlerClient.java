package me.ichun.mods.beebarker.client.core;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import us.ichun.mods.ichunutil.common.core.EntityHelperBase;

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
                    pullTime--;
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

    public static final int PULL_TIME = 10;
    public int pullTime;
    public int idleTime;
    public float prevYaw;
    public float prevPitch;
    public float currentYaw;
    public float currentPitch;
    public float targetYaw;
    public float targetPitch;
}
