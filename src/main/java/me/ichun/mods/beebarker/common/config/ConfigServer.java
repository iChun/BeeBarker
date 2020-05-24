package me.ichun.mods.beebarker.common.config;

import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.CategoryDivider;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;

public class ConfigServer extends ConfigBase
{
    @CategoryDivider
    @Prop
    public boolean easterEgg = true;

    public ConfigServer()
    {
        super(ModLoadingContext.get().getActiveContainer().getModId() + "-server.toml");
    }

    @Nonnull
    @Override
    public String getModId()
    {
        return BeeBarker.MOD_ID;
    }

    @Nonnull
    @Override
    public String getConfigName()
    {
        return BeeBarker.MOD_NAME;
    }

    @Nonnull
    @Override
    public ModConfig.Type getConfigType()
    {
        return ModConfig.Type.SERVER;
    }
}
