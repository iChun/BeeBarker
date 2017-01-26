package me.ichun.mods.beebarker.common.core;

import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;

import java.io.File;

public class Config extends ConfigBase
{
    @ConfigProp
    @IntMinMax(min = 0, max = 100)
    public int beeBarkerChance = 10;

    @ConfigProp
    @IntMinMax(min = 1, max = 250)
    public int maxBeeCharge = 50;

    @ConfigProp
    @IntMinMax(min = 1, max = 100)
    public int beeDamage = 2;

    @ConfigProp
    @IntMinMax(min = 1, max = 500)
    public int beeCount = 7;

    @ConfigProp(useSession = true)
    @IntBool
    public int easterEgg = 1;

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
