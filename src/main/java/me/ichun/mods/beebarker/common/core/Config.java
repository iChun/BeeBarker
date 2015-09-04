package me.ichun.mods.beebarker.common.core;

import me.ichun.mods.beebarker.common.BeeBarker;
import us.ichun.mods.ichunutil.common.core.config.ConfigBase;
import us.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import us.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;

import java.io.File;

public class Config extends ConfigBase
{
    @ConfigProp
    @IntMinMax(min = 0, max = 100)
    public int beeBarkerChance = 5;

    @ConfigProp
    @IntMinMax(min = 1, max = 500)
    public int beeCount = 7;

    public Config(File file)
    {
        super(file);
    }

    @Override
    public String getModId()
    {
        return BeeBarker.MOD_NAME.toLowerCase();
    }

    @Override
    public String getModName()
    {
        return "Bee Barker";
    }
}
