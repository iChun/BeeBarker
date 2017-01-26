package me.ichun.mods.beebarker.client.core;

import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.ichunutil.client.render.item.ItemRenderingHelper;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;

public class EquipBeeBarkerHandler implements ItemRenderingHelper.SwingProofHandler.IItemEquippedHandler
{
    @Override
    public void handleEquip(EntityPlayerSP player, ItemStack stack)
    {
        BeeBarker.eventHandlerClient.prevYaw = BeeBarker.eventHandlerClient.prevPitch = BeeBarker.eventHandlerClient.currentYaw = BeeBarker.eventHandlerClient.currentPitch = BeeBarker.eventHandlerClient.targetYaw = BeeBarker.eventHandlerClient.targetPitch = 0F;
    }

    @Override
    public boolean hideName()
    {
        return true;
    }
}
