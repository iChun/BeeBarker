package me.ichun.mods.beebarker.client.core;

import me.ichun.mods.beebarker.client.render.RenderBee;
import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.core.ProxyCommon;
import me.ichun.mods.beebarker.common.entity.EntityBee;
import me.ichun.mods.beebarker.common.item.ItemBeeBarker;
import me.ichun.mods.ichunutil.client.render.item.ItemRenderingHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.item.ItemHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ProxyClient extends ProxyCommon
{
    @Override
    public void preInit()
    {
        super.preInit();

        iChunUtil.proxy.registerMinecraftKeyBind(Minecraft.getMinecraft().gameSettings.keyBindUseItem);

        BeeBarker.eventHandlerClient = new EventHandlerClient();
        MinecraftForge.EVENT_BUS.register(BeeBarker.eventHandlerClient);

        ModelLoader.setCustomModelResourceLocation(BeeBarker.itemBeeBarker, 0, new ModelResourceLocation("beebarker:bee_barker", "inventory"));

        ItemHandler.registerDualHandedItem(ItemBeeBarker.class);
        ItemRenderingHelper.registerSwingProofItem(new ItemRenderingHelper.SwingProofHandler(ItemBeeBarker.class, new EquipBeeBarkerHandler()));

        RenderingRegistry.registerEntityRenderingHandler(EntityBee.class, new RenderBee.RenderFactory());
    }
}
