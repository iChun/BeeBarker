package me.ichun.mods.beebarker.common.entity;

import com.google.common.collect.Iterables;
import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.ichunutil.common.entity.util.EntityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityBee extends Entity
{
    public static final DataParameter<Integer> SHOOTER_ID = EntityDataManager.createKey(EntityBee.class, DataSerializers.VARINT);

    public int life;
    public LivingEntity shooter;

    public EntityBee(EntityType<?> entityTypeIn, World worldIn)
    {
        super(entityTypeIn, worldIn);
        life = 60 + rand.nextInt(40); //3 - 5 seconds life
    }

    public EntityBee setShooter(LivingEntity shooter)
    {
        this.shooter = shooter;

        LivingEntity renderedShooter = shooter;
        //        if(iChunUtil.hasMorphMod() && shooter instanceof PlayerEntity && MorphApi.getApiImpl().getMorphEntity(shooter.getEntityWorld(), shooter.getName(), Side.SERVER) != null) //TODO morph
        //        {
        //            renderedShooter = MorphApi.getApiImpl().getMorphEntity(shooter.getEntityWorld(), shooter.getName(), Side.SERVER);
        //        }

        Vec3d look = shooter.getLookVec();
        Vec3d pos = shooter.getPositionVector().add(look.x * 1.3D - look.z * (renderedShooter.getWidth() * 0.2D), look.y * 1.3D + (renderedShooter.getEyeHeight() * 0.8D), look.z * 1.3D + look.x * (renderedShooter.getWidth() * 0.2D));
        double gausAmount = 0.02D;
        double d0 = rand.nextGaussian() * gausAmount;
        double d1 = rand.nextGaussian() * gausAmount;
        double d2 = rand.nextGaussian() * gausAmount;

        setPosition(pos.x, pos.y, pos.z);
        double mag = shooter instanceof WolfEntity ? 0.15D : 0.4D;
        setMotion(look.x * mag + d0, look.y * mag + d1, look.z * mag + d2);
        Vec3d motion = getMotion();

        float var20 = MathHelper.sqrt(motion.x * motion.x + motion.z * motion.z);
        this.rotationYaw = (float)(Math.atan2(motion.x, motion.z) * 180.0D / Math.PI);

        for (this.rotationPitch = (float)(Math.atan2(motion.y, (double)var20) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
        {
            ;
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
        {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F)
        {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
        {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;

        getDataManager().set(SHOOTER_ID, shooter.getEntityId());

        return this;
    }

    @Override
    protected void registerData()
    {
        getDataManager().register(SHOOTER_ID, -1);
    }

    @Override
    public void tick()
    {
        super.tick();
        life--;
        if(life < 0 && !world.isRemote || life < -200 || isInWater() || isInLava() || isBurning())
        {
            remove();
            return;
        }

        double gausAmount = 0.02D;
        double d0 = rand.nextGaussian() * gausAmount;
        double d1 = rand.nextGaussian() * gausAmount;
        double d2 = rand.nextGaussian() * gausAmount;

        setMotion(getMotion().add(d0, d1, d2));
        Vec3d motion = getMotion();

        if(!world.isRemote)
        {
            RayTraceResult result = EntityHelper.rayTrace(world, getPositionVec(), getPositionVec().add(getMotion()), this, true, RayTraceContext.BlockMode.COLLIDER, b -> true, RayTraceContext.FluidMode.ANY, e -> !(e instanceof EntityBee || ticksExisted < 7 && e == shooter));

            if(result.getType() == RayTraceResult.Type.ENTITY)
            {
                Entity collidedEnt = ((EntityRayTraceResult)result).getEntity();
                boolean doNotHarm = false;
                Iterable<ItemStack> equipment = collidedEnt.getArmorInventoryList();
                if(!Iterables.isEmpty(equipment))
                {
                    doNotHarm = true;
                    for(ItemStack armor : equipment)
                    {
                        if(!(armor != null && armor.getItem().getClass().getSimpleName().contains("bee"))) //If not wearing full bee suit, STING.
                        {
                            doNotHarm = false;
                            break;
                        }
                    }
                }
                if(!doNotHarm)
                {
                    collidedEnt.hurtResistantTime = 0;
                    collidedEnt.attackEntityFrom(new IndirectEntityDamageSource("beestung", collidedEnt, shooter), BeeBarker.configCommon.beeDamage);
                }

                remove();
                return;
            }
            else if(result.getType() == RayTraceResult.Type.BLOCK)
            {
                remove();
                return;
            }
        }

        setPosition(getPosX() + motion.x, getPosY() + motion.y, getPosZ() + motion.z);

        float var20 = MathHelper.sqrt(motion.x * motion.x + motion.z * motion.z);
        this.rotationYaw = (float)(Math.atan2(motion.x, motion.z) * 180.0D / Math.PI);

        for (this.rotationPitch = (float)(Math.atan2(motion.y, (double)var20) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
        {
            ;
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
        {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F)
        {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
        {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;

        this.setPosition(this.getPosX(), this.getPosY(), this.getPosZ());
        this.doBlockCollisions();
    }

    @Override
    public void remove()
    {
        super.remove();
        if(world.isRemote)
        {
            spawnParticle();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void spawnParticle()
    {
        Particle particle = Minecraft.getInstance().particles.addParticle(ParticleTypes.FIREWORK, prevPosX, prevPosY, prevPosZ, 0D, 0D, 0D);
        if(particle instanceof SimpleAnimatedParticle)
        {
            ((SimpleAnimatedParticle)particle).setColor(0xedb200);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double distance) {
        double d0 = this.getBoundingBox().getAverageEdgeLength() * 10D;
        if (Double.isNaN(d0)) {
            d0 = 1.0D;
        }

        d0 = d0 * 64.0D * getRenderDistanceWeight();
        return distance < d0 * d0;
    }

    @Override
    public void setPortal(BlockPos pos)
    {
        remove();
    }

    @Override
    public Entity changeDimension(DimensionType destination)
    {
        remove();
        return null;
    }

    @Override
    public boolean writeUnlessRemoved(CompoundNBT compound) { return false; } //disable saving of entity

    @Override
    protected void readAdditional(CompoundNBT compound){}

    @Override
    protected void writeAdditional(CompoundNBT compound){}

    @Override
    public IPacket<?> createSpawnPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
