package me.ichun.mods.beebarker.client.core;

import me.ichun.mods.beebarker.client.render.ItemRenderBeeBarker;
import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.item.ItemBeeBarker;
import me.ichun.mods.beebarker.common.packet.PacketBark;
import me.ichun.mods.ichunutil.client.keybind.KeyEvent;
import me.ichun.mods.ichunutil.client.model.item.ModelBaseWrapper;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.item.ItemHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.ArrayList;

public class EventHandlerClient
{
    @SubscribeEvent
    public void onModelRegistry(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(BeeBarker.itemBeeBarker, 0, new ModelResourceLocation("beebarker:bee_barker", "inventory"));
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event)
    {
        event.getModelRegistry().putObject(new ModelResourceLocation("beebarker:bee_barker", "inventory"), new ModelBaseWrapper(new ItemRenderBeeBarker()).setItemDualHanded());
    }

    @SubscribeEvent
    public void onKeyEvent(KeyEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if(mc.currentScreen == null && !iChunUtil.eventHandlerClient.hasScreen)
        {
            ItemStack currentInv = ItemHandler.getUsableDualHandedItem(mc.player);
            if(currentInv.getItem() instanceof ItemBeeBarker)
            {
                if(event.keyBind.isMinecraftBind() && event.keyBind.keyIndex == mc.gameSettings.keyBindUseItem.getKeyCode())
                {
                    BeeBarker.channel.sendToServer(new PacketBark(BeeBarker.config.easterEgg != 1 || event.keyBind.isPressed()));
                    if(event.keyBind.isPressed())
                    {
                        BeeBarker.eventHandlerClient.pullTime = EventHandlerClient.PULL_TIME;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            Minecraft mc = Minecraft.getMinecraft();
            if(mc.world != null && !mc.isGamePaused())
            {
                prevYaw = currentYaw;
                prevPitch = currentPitch;
                currentYaw = EntityHelper.updateRotation(currentYaw, targetYaw, 10F);
                currentPitch = EntityHelper.updateRotation(currentPitch, targetPitch, 10F);
                if(pullTime > 0)
                {
                    if(!(pressState.contains(mc.player.getName()) && pullTime == 7))
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
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END && event.side.isClient() && pressState.contains(event.player.getName()))
        {
            Vec3d look = event.player.getLookVec();
            Vec3d pos;
            EnumHandSide side = ItemHandler.getHandSide(event.player, ItemHandler.getUsableDualHandedItem(event.player));
            if(event.player == Minecraft.getMinecraft().getRenderViewEntity() && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) //is first person
            {
                double d = - look.z * 0.75D;
                double d1 = look.y * 1.5D + 1.15D;
                double d2 = + look.x * 0.75D;
                if(side == EnumHandSide.LEFT)
                {
                    d = -d;
                    d2 = -d2;
                }
                pos = event.player.getPositionVector().addVector(look.x * 1.5D + d, d1, look.z * 1.5D + d2);
            }
            else
            {
                double d = - look.z * (event.player.width * 0.2D);
                double d1 = look.y * 1.5D + (event.player.getEyeHeight() * 0.8D);
                double d2 = + look.x * (event.player.width * 0.2D);
                if(side == EnumHandSide.LEFT)
                {
                    d = -d;
                    d2 = -d2;
                }
                pos = event.player.getPositionVector().addVector(look.x * 1.5D + d, d1, look.z * 1.5D + d2);
            }
            for(int i = 0; i < 4; i++)
            {
                double d0 = event.player.world.rand.nextGaussian() * 0.02D;
                double d1 = event.player.world.rand.nextGaussian() * 0.02D + event.player.world.rand.nextFloat() * 0.1D;
                double d2 = event.player.world.rand.nextGaussian() * 0.02D;
                event.player.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.x, pos.y, pos.z, look.x * 0.3D + d0, look.y * 0.3D + d1, look.z * 0.3D + d2);
                event.player.world.spawnParticle(EnumParticleTypes.FLAME, pos.x, pos.y, pos.z, look.x * 0.3D + d0, look.y * 0.3D + d1, look.z * 0.3D + d2);
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
