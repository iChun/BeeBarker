package me.ichun.mods.beebarker.client.core;

import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.core.CommonProxy;
import me.ichun.mods.beebarker.common.item.ItemBeeBarker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import us.ichun.mods.ichunutil.common.iChunUtil;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        super.preInit();

        iChunUtil.proxy.registerMinecraftKeyBind(Minecraft.getMinecraft().gameSettings.keyBindUseItem);

        tickHandlerClient = new TickHandlerClient();
        FMLCommonHandler.instance().bus().register(tickHandlerClient);
    }

    @Override
    public void init()
    {
        super.init();

        for(int i = 0; i < 16; i++)
        {
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(BeeBarker.itemBeeBarker, i, new ModelResourceLocation("beebarker:BeeBarker", "inventory"));
        }

        iChunUtil.proxy.tickHandlerClient.registerBowAnimationLockedItem(ItemBeeBarker.class);
        iChunUtil.proxy.tickHandlerClient.registerSwingProofItem(new us.ichun.mods.ichunutil.client.core.TickHandlerClient.SwingProofHandler(ItemBeeBarker.class, new EquipBeeBarkerHandler()));
    }
}
