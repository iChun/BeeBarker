package me.ichun.mods.beebarker.common;

import me.ichun.mods.beebarker.client.core.EventHandlerClient;
import me.ichun.mods.beebarker.client.fx.ParticleBuzz;
import me.ichun.mods.beebarker.client.render.ItemRenderBeeBarker;
import me.ichun.mods.beebarker.client.render.RenderBee;
import me.ichun.mods.beebarker.common.config.ConfigCommon;
import me.ichun.mods.beebarker.common.config.ConfigServer;
import me.ichun.mods.beebarker.common.core.EventHandlerServer;
import me.ichun.mods.beebarker.common.entity.EntityBee;
import me.ichun.mods.beebarker.common.item.ItemBeeBarker;
import me.ichun.mods.beebarker.common.packet.PacketBark;
import me.ichun.mods.beebarker.common.packet.PacketKeyState;
import me.ichun.mods.beebarker.common.packet.PacketSpawnParticles;
import me.ichun.mods.ichunutil.client.item.ItemEffectHandler;
import me.ichun.mods.ichunutil.client.model.item.ItemModelRenderer;
import me.ichun.mods.ichunutil.common.network.PacketChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(BeeBarker.MOD_ID)
public class BeeBarker
{
    public static final String MOD_NAME = "BeeBarker";
    public static final String MOD_ID = "beebarker";
    public static final String PROTOCOL = "1";

    public static ConfigCommon configCommon;
    public static ConfigServer configServer;

    public static EventHandlerClient eventHandlerClient;

    public static PacketChannel channel;

    public static boolean isForestryInstalled;

    public BeeBarker()
    {
        configCommon = new ConfigCommon().init();
        configServer = new ConfigServer().init();

        MinecraftForge.EVENT_BUS.register(new EventHandlerServer());

        channel = new PacketChannel(new ResourceLocation(MOD_ID, "channel"), PROTOCOL, PacketBark.class, PacketSpawnParticles.class, PacketKeyState.class);

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        EntityTypes.REGISTRY.register(bus);
        Items.REGISTRY.register(bus);
        Particles.REGISTRY.register(bus);
        bus.addListener(this::onCommonSetup);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(this::onClientSetup);
            bus.addListener(this::onRegisterParticleFactory);
            bus.addListener(this::onModelBake);

            MinecraftForge.EVENT_BUS.register(eventHandlerClient = new EventHandlerClient());

            ItemEffectHandler.init();
        });
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        isForestryInstalled = ModList.get().isLoaded("forestry");
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityTypes.BEE.get(), new RenderBee.RenderFactory());

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> me.ichun.mods.ichunutil.client.core.EventHandlerClient::getConfigGui);
    }

    @OnlyIn(Dist.CLIENT)
    private void onRegisterParticleFactory(ParticleFactoryRegisterEvent event)
    {
        Minecraft.getInstance().particles.registerFactory(Particles.BUZZ.get(), ParticleBuzz.Factory::new);
    }

    @OnlyIn(Dist.CLIENT)
    private void onModelBake(ModelBakeEvent event)
    {
        event.getModelRegistry().put(new ModelResourceLocation("beebarker:bee_barker", "inventory"), new ItemModelRenderer(ItemRenderBeeBarker.INSTANCE));
    }

    public static class EntityTypes
    {
        private static final DeferredRegister<EntityType<?>> REGISTRY = new DeferredRegister<>(ForgeRegistries.ENTITIES, MOD_ID);

        public static final RegistryObject<EntityType<EntityBee>> BEE = REGISTRY.register("bee", () -> EntityType.Builder.create(EntityBee::new, EntityClassification.MISC)
                .size(0.1F, 0.1F)
                .setTrackingRange(64)
                .setUpdateInterval(1)
                .setShouldReceiveVelocityUpdates(true)
                .build("from " + MOD_NAME + ". Ignore this.")
        );
    }

    public static class Items
    {
        private static final DeferredRegister<Item> REGISTRY = new DeferredRegister<>(ForgeRegistries.ITEMS, MOD_ID);

        public static final RegistryObject<ItemBeeBarker> BEE_BARKER = REGISTRY.register("bee_barker", () -> new ItemBeeBarker(new Item.Properties().maxDamage(251).group(ItemGroup.TOOLS))); //Maxdmg = Max + 2
    }

    public static class Particles
    {
        private static final DeferredRegister<ParticleType<?>> REGISTRY = new DeferredRegister<>(ForgeRegistries.PARTICLE_TYPES, MOD_ID);

        public static final RegistryObject<BasicParticleType> BUZZ = REGISTRY.register("buzz", () -> new BasicParticleType(false));
    }
}
