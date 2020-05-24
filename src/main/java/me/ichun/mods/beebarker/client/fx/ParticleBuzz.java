package me.ichun.mods.beebarker.client.fx;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleBuzz extends SmokeParticle
{
    public ParticleBuzz(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double motionX, double motionY, double motionZ, IAnimatedSprite spriteSet)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, motionX, motionY, motionZ, 1.0F, spriteSet);
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

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType>
    {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite p_i51045_1_) {
            this.spriteSet = p_i51045_1_;
        }

        public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ParticleBuzz(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }
}
