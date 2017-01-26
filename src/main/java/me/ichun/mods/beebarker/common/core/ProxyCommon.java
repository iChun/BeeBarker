package me.ichun.mods.beebarker.common.core;

import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.entity.EntityBee;
import me.ichun.mods.beebarker.common.item.ItemBeeBarker;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ProxyCommon
{
    public void preInit()
    {
        BeeBarker.itemBeeBarker = GameRegistry.register((new ItemBeeBarker()).setFull3D().setRegistryName(new ResourceLocation("beebarker", "bee_barker")).setUnlocalizedName("beebarker.bee_barker").setCreativeTab(CreativeTabs.TOOLS));

        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    public void init()
    {
        MinecraftForge.EVENT_BUS.register(new BarkHelper());

        EntityRegistry.registerModEntity(EntityBee.class, "BeeEnt", 90, BeeBarker.instance, 64, 1, true);
    }
}
