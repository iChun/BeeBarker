package me.ichun.mods.beebarker.common;

import me.ichun.mods.beebarker.common.core.BarkHelper;
import me.ichun.mods.beebarker.common.core.CommonProxy;
import me.ichun.mods.beebarker.common.core.Config;
import me.ichun.mods.beebarker.common.packet.PacketBark;
import me.ichun.mods.beebarker.common.packet.PacketSpawnParticles;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import us.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import us.ichun.mods.ichunutil.common.core.network.ChannelHandler;
import us.ichun.mods.ichunutil.common.core.network.PacketChannel;
import us.ichun.mods.ichunutil.common.core.updateChecker.ModVersionChecker;
import us.ichun.mods.ichunutil.common.core.updateChecker.ModVersionInfo;
import us.ichun.mods.ichunutil.common.iChunUtil;

@Mod(modid = BeeBarker.MOD_NAME, name = BeeBarker.MOD_NAME,
        version = BeeBarker.VERSION,
        guiFactory = "us.ichun.mods.ichunutil.common.core.config.GenericModGuiFactory",
        dependencies = "required-after:iChunUtil@[" + iChunUtil.versionMC +".4.0," + (iChunUtil.versionMC + 1) + ".0.0)",
        acceptableRemoteVersions = "[" + iChunUtil.versionMC +".0.0," + iChunUtil.versionMC + ".1.0)"
)
public class BeeBarker
{
    public static final String MOD_NAME = "BeeBarker";
    public static final String VERSION = iChunUtil.versionMC + ".0.0";

    @Mod.Instance(MOD_NAME)
    public static BeeBarker instance;

    @SidedProxy(clientSide = "me.ichun.mods.beebarker.client.core.ClientProxy", serverSide = "me.ichun.mods.beebarker.common.core.CommonProxy")
    public static CommonProxy proxy;

    public static PacketChannel channel;

    public static Config config;

    public static Item itemBeeBarker;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        config = (Config)ConfigHandler.registerConfig(new Config(event.getSuggestedConfigurationFile()));

        proxy.preInit();

        channel = ChannelHandler.getChannelHandlers(MOD_NAME, PacketBark.class, PacketSpawnParticles.class);

        ModVersionChecker.register_iChunMod(new ModVersionInfo(MOD_NAME, iChunUtil.versionOfMC, VERSION, false));
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event)
    {
        proxy.init();
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event)
    {
        BarkHelper.cooldown.clear();
    }
}
