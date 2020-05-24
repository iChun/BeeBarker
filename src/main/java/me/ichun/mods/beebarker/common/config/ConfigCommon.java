package me.ichun.mods.beebarker.common.config;

import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.CategoryDivider;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import net.minecraftforge.fml.ModLoadingContext;

import javax.annotation.Nonnull;

public class ConfigCommon extends ConfigBase
{
    @CategoryDivider(name = "gameplay")
    @Prop(min = 0, max = 100)
    public int beeBarkerChance = 20;

    @Prop(min = 1, max = 250)
    public int maxBeeCharge = 50;

    @Prop(min = 1, max = 100)
    public int beeDamage = 2;

    @Prop(min = 1, max = 500)
    public int beeCount = 7;

    public ConfigCommon()
    {
        super(ModLoadingContext.get().getActiveContainer().getModId() + "-common.toml");
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
}
