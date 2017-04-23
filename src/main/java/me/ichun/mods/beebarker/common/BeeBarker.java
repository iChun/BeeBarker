package me.ichun.mods.beebarker.common;

import me.ichun.mods.beebarker.client.core.EventHandlerClient;
import me.ichun.mods.beebarker.common.core.BarkHelper;
import me.ichun.mods.beebarker.common.core.Config;
import me.ichun.mods.beebarker.common.core.ProxyCommon;
import me.ichun.mods.beebarker.common.packet.PacketBark;
import me.ichun.mods.beebarker.common.packet.PacketKeyState;
import me.ichun.mods.beebarker.common.packet.PacketSpawnParticles;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.network.PacketChannel;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.update.UpdateChecker;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = BeeBarker.MOD_ID, name = BeeBarker.MOD_NAME,
        version = BeeBarker.VERSION,
        guiFactory = "me.ichun.mods.ichunutil.common.core.config.GenericModGuiFactory",
        dependencies = "required-after:ichunutil@[" + iChunUtil.VERSION_MAJOR +".2.0," + (iChunUtil.VERSION_MAJOR + 1) + ".0.0)",
        acceptableRemoteVersions = "[" + iChunUtil.VERSION_MAJOR +".0.0," + iChunUtil.VERSION_MAJOR + ".1.0)"
)
public class BeeBarker
{
    public static final String MOD_NAME = "BeeBarker";
    public static final String MOD_ID = "beebarker";
    public static final String VERSION = iChunUtil.VERSION_MAJOR + ".0.1";

    @Mod.Instance(MOD_ID)
    public static BeeBarker instance;

    @SidedProxy(clientSide = "me.ichun.mods.beebarker.client.core.ProxyClient", serverSide = "me.ichun.mods.beebarker.common.core.ProxyCommon")
    public static ProxyCommon proxy;

    public static PacketChannel channel;

    public static Config config;

    public static Item itemBeeBarker;

    public static boolean isForestryInstalled;

    public static EventHandlerClient eventHandlerClient;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        config = ConfigHandler.registerConfig(new Config(event.getSuggestedConfigurationFile()));

        proxy.preInit();

        channel = new PacketChannel(MOD_NAME, PacketBark.class, PacketSpawnParticles.class, PacketKeyState.class);

        UpdateChecker.registerMod(new UpdateChecker.ModVersionInfo(MOD_NAME, iChunUtil.VERSION_OF_MC, VERSION, false));
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event)
    {
        proxy.init();

        isForestryInstalled = Loader.isModLoaded("Forestry");
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event)
    {
        BarkHelper.cooldown.clear();
        BarkHelper.pressState.clear();
    }
}
