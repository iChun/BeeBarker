package me.ichun.mods.beebarker.client.fx;

import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.world.World;

public class EntityBuzzFX extends EntitySmokeFX
{
    public EntityBuzzFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double motionX, double motionY, double motionZ)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, motionX, motionY, motionZ, 1.0F);
        double colour = Math.random();
        if(colour < 0.5D) //black
        {
            this.particleRed = this.particleGreen = this.particleBlue = (float)(Math.random() * 0.15D);
        }
        else //yellow
        {
            this.particleBlue = 0.0F;
            this.particleGreen = this.particleRed = 0.85F + (float)(Math.random() * 0.15D);
        }
    }
}
