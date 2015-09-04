package me.ichun.mods.beebarker.common.core;

import me.ichun.mods.beebarker.client.core.TickHandlerClient;
import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.item.ItemBeeBarker;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy
{
    public void preInit()
    {
        BeeBarker.itemBeeBarker = GameRegistry.registerItem((new ItemBeeBarker()).setFull3D().setUnlocalizedName("BeeBarker").setCreativeTab(CreativeTabs.tabTools), "BeeBarker", BeeBarker.MOD_NAME);

        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public void init()
    {
        FMLCommonHandler.instance().bus().register(new BarkHelper());
    }

    public TickHandlerClient tickHandlerClient;
}
