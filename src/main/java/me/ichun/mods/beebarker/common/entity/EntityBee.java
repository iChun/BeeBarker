package me.ichun.mods.beebarker.common.entity;

import com.google.common.collect.Iterables;
import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.morph.api.MorphApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

public class EntityBee extends Entity
{
    public static final DataParameter<Integer> SHOOTER_ID = EntityDataManager.createKey(EntityBee.class, DataSerializers.VARINT);

    public int life;
    public EntityLivingBase shooter;

    public EntityBee(World worldIn)
    {
        super(worldIn);
        life = 60 + rand.nextInt(40); //3 - 5 seconds life
        setSize(0.1F, 0.1F);
    }

    public EntityBee(World world, EntityLivingBase shooter)
    {
        this(world);
        this.shooter = shooter;

        EntityLivingBase renderedShooter = shooter;
        if(iChunUtil.hasMorphMod() && shooter instanceof EntityPlayer && MorphApi.getApiImpl().getMorphEntity(shooter.getEntityWorld(), shooter.getName(), Side.SERVER) != null)
        {
            renderedShooter = MorphApi.getApiImpl().getMorphEntity(shooter.getEntityWorld(), shooter.getName(), Side.SERVER);
        }

        Vec3d look = shooter.getLookVec();
        Vec3d pos = shooter.getPositionVector().addVector(look.xCoord * 1.3D - look.zCoord * (renderedShooter.width * 0.2D), look.yCoord * 1.3D + (renderedShooter.getEyeHeight() * 0.8D), look.zCoord * 1.3D + look.xCoord * (renderedShooter.width * 0.2D));
        double gausAmount = 0.02D;
        double d0 = rand.nextGaussian() * gausAmount;
        double d1 = rand.nextGaussian() * gausAmount;
        double d2 = rand.nextGaussian() * gausAmount;

        setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
        double mag = shooter instanceof EntityWolf ? 0.15D : 0.4D;
        motionX = look.xCoord * mag + d0;
        motionY = look.yCoord * mag + d1;
        motionZ = look.zCoord * mag + d2;

        float var20 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

        for (this.rotationPitch = (float)(Math.atan2(this.motionY, (double)var20) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
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
    }

    @Override
    protected void entityInit()
    {
        getDataManager().register(SHOOTER_ID, -1);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        life--;
        if(life < 0 && !worldObj.isRemote || life < -200 || isInWater() || isInLava() || isBurning())
        {
            setDead();
            return;
        }

        double gausAmount = 0.02D;
        double d0 = rand.nextGaussian() * gausAmount;
        double d1 = rand.nextGaussian() * gausAmount;
        double d2 = rand.nextGaussian() * gausAmount;

        motionX += d0;
        motionY += d1;
        motionZ += d2;

        if(!worldObj.isRemote)
        {
            Vec3d var17 = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d var3 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            RayTraceResult mop = this.worldObj.rayTraceBlocks(var17, var3, false, true, false);
            var17 = new Vec3d(this.posX, this.posY, this.posZ);
            var3 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if(mop != null)
            {
                var3 = new Vec3d(mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord);
            }

            Entity collidedEnt = null;
            List var6 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double var7 = 0.0D;
            int var9;
            float var11;

            for(var9 = 0; var9 < var6.size(); ++var9)
            {
                Entity var10 = (Entity)var6.get(var9);

                if(var10 instanceof EntityBee || ticksExisted < 7 && var10 == shooter)
                {
                    continue;
                }

                if(var10.canBeCollidedWith())
                {
                    var11 = 0.3F;
                    AxisAlignedBB var12 = var10.getEntityBoundingBox().expand((double)var11, (double)var11, (double)var11);
                    RayTraceResult var13 = var12.calculateIntercept(var17, var3);

                    if(var13 != null)
                    {
                        double var14 = var17.distanceTo(var13.hitVec);

                        if(var14 < var7 || var7 == 0.0D)
                        {
                            collidedEnt = var10;
                            var7 = var14;
                        }
                    }
                }
            }

            if(collidedEnt != null)
            {
                boolean doNotHarm = false;
                Iterable<ItemStack> equipment = collidedEnt.getArmorInventoryList();
                if(!Iterables.isEmpty(equipment))
                {
                    doNotHarm = true;
                    for(ItemStack armor : equipment)
                    {
                        if(!(armor != null && armor.getItem() != null && armor.getItem().getClass().getSimpleName().contains("bee"))) //If not wearing full bee suit, STING.
                        {
                            doNotHarm = false;
                            break;
                        }
                    }
                }
                if(!doNotHarm)
                {
                    collidedEnt.hurtResistantTime = 0;
                    collidedEnt.attackEntityFrom(new EntityDamageSourceIndirect("beestung", collidedEnt, shooter), BeeBarker.config.beeDamage);
                }

                setDead();
                return;
            }
            else if(mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                setDead();
                return;
            }
        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;

        float var20 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

        for (this.rotationPitch = (float)(Math.atan2(this.motionY, (double)var20) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
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

        this.setPosition(this.posX, this.posY, this.posZ);
        this.doBlockCollisions();
    }

    @Override
    public void setDead()
    {
        super.setDead();
        if(worldObj.isRemote)
        {
            worldObj.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, prevPosX, prevPosY, prevPosZ, 0D, 0D, 0D, 0xedb200);
        }
    }


    @Override
    public void setPortal(BlockPos pos)
    {
        setDead();
    }

    @Override
    public Entity changeDimension(int dimensionIn)
    {
        setDead();
        return null;
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound tagCompund)
    {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompund)
    {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound)
    {
    }
}
