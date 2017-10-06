package me.ichun.mods.beebarker.common.core;

import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.entity.EntityBee;
import me.ichun.mods.beebarker.common.item.ItemBeeBarker;
import me.ichun.mods.ichunutil.common.item.ItemHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ProxyCommon
{
    public void preInit()
    {
        ItemHandler.registerDualHandedItem(ItemBeeBarker.class);

        EntityRegistry.registerModEntity(new ResourceLocation("beebarker", "entity_bee"), EntityBee.class, "BeeEnt", 90, BeeBarker.instance, 64, 1, true);

        MinecraftForge.EVENT_BUS.register(new EventHandlerServer());
    }
}
