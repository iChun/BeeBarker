package me.ichun.mods.beebarker.common.packet;

import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.core.EventHandlerServer;
import me.ichun.mods.ichunutil.common.network.AbstractPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketSpawnParticles extends AbstractPacket
{
    public int entityId;
    public int playerId;
    public boolean isSmokeParticles;

    public PacketSpawnParticles(){}

    public PacketSpawnParticles(int entityId, int playerId, boolean isSmokeParticles)
    {
        this.entityId = entityId;
        this.playerId = playerId;
        this.isSmokeParticles = isSmokeParticles;
    }

    @Override
    public void writeTo(PacketBuffer buffer)
    {
        buffer.writeInt(entityId);
        buffer.writeInt(playerId);
        buffer.writeBoolean(isSmokeParticles);
    }

    @Override
    public void readFrom(PacketBuffer buffer)
    {
        entityId = buffer.readInt();
        playerId = buffer.readInt();
        isSmokeParticles = buffer.readBoolean();
    }

    @Override
    public void process(NetworkEvent.Context context) //receivingSide CLIENT
    {
        context.enqueueWork(this::handleClient);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleClient()
    {
        Minecraft mc = Minecraft.getInstance();

        if(entityId == -1)
        {
            Entity ply = mc.world.getEntityByID(playerId);
            if(ply instanceof LivingEntity && !(ply == mc.getRenderViewEntity() && mc.gameSettings.thirdPersonView == 0))
            {
                LivingEntity living = (LivingEntity)ply;
                double d0 = living.world.rand.nextGaussian() * 0.02D;
                double d1 = living.world.rand.nextGaussian() * 0.02D;
                double d2 = living.world.rand.nextGaussian() * 0.02D;
                Vec3d look = living.getLookVec();
                Vec3d pos = living.getPositionVector().add(look.x * 0.5D - look.z * (living.getWidth() * 0.67D), look.y * 0.5D + (living.getEyeHeight() * 0.8D), look.z * 0.5D + look.x * (living.getWidth() * 0.67D));
                living.world.addParticle(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, d0, d1, d2);
            }
        }
        else
        {
            Entity ent = mc.world.getEntityByID(entityId);
            if(ent != null)
            {
                for(int i = 0; i < 7; ++i)
                {
                    double d0 = mc.world.rand.nextGaussian() * 0.02D;
                    double d1 = mc.world.rand.nextGaussian() * 0.02D;
                    double d2 = mc.world.rand.nextGaussian() * 0.02D;
                    if(isSmokeParticles)
                    {
                        mc.world.addParticle(ParticleTypes.SMOKE, ent.getPosX() + (double)(mc.world.rand.nextFloat() * ent.getWidth() * 2.0F) - (double)ent.getWidth(), ent.getPosY() + 0.5D + (double)(mc.world.rand.nextFloat() * ent.getHeight()), ent.getPosZ() + (double)(mc.world.rand.nextFloat() * ent.getWidth() * 2.0F) - (double)ent.getWidth(), d0, d1, d2);
                    }
                    else
                    {
                        ent.getPersistentData().putBoolean(EventHandlerServer.BARKABLE_STRING, true);
                        mc.world.addParticle(BeeBarker.Particles.BUZZ.get(), ent.getPosX() + (double)(mc.world.rand.nextFloat() * ent.getWidth() * 2.0F) - (double)ent.getWidth(), ent.getPosY() + 0.5D + (double)(mc.world.rand.nextFloat() * ent.getHeight()), ent.getPosZ() + (double)(mc.world.rand.nextFloat() * ent.getWidth() * 2.0F) - (double)ent.getWidth(), d0, d1, d2);
                    }
                }
            }
            Entity ply = mc.world.getEntityByID(playerId);
            if(ply instanceof LivingEntity)
            {
                ((LivingEntity)ply).swingArm(Hand.MAIN_HAND);
            }
        }
    }
}
