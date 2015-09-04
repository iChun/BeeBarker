package me.ichun.mods.beebarker.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.beebarker.client.fx.EntityBuzzFX;
import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.core.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.ichun.mods.ichunutil.common.core.network.AbstractPacket;

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
    public void writeTo(ByteBuf buffer, Side side)
    {
        buffer.writeInt(entityId);
        buffer.writeInt(playerId);
        buffer.writeBoolean(isSmokeParticles);
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        entityId = buffer.readInt();
        playerId = buffer.readInt();
        isSmokeParticles = buffer.readBoolean();
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        handleClient();
    }

    @SideOnly(Side.CLIENT)
    public void handleClient()
    {
        Minecraft mc = Minecraft.getMinecraft();

        if(entityId == -1)
        {
            Entity ply = mc.theWorld.getEntityByID(playerId);
            if(ply instanceof EntityLivingBase && !(ply == mc.getRenderViewEntity() && mc.gameSettings.thirdPersonView == 0))
            {
                EntityLivingBase living = (EntityLivingBase)ply;
                double d0 = living.worldObj.rand.nextGaussian() * 0.02D;
                double d1 = living.worldObj.rand.nextGaussian() * 0.02D;
                double d2 = living.worldObj.rand.nextGaussian() * 0.02D;
                Vec3 look = living.getLookVec();
                Vec3 pos = living.getPositionVector().addVector(look.xCoord * 0.5D - look.zCoord * (living.width * 0.67D), look.yCoord * 0.5D + (living.getEyeHeight() * 0.8D), look.zCoord * 0.5D + look.xCoord * (living.width * 0.67D));
                living.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.xCoord, pos.yCoord, pos.zCoord, d0, d1, d2);
            }
        }
        else
        {
            Entity ent = mc.theWorld.getEntityByID(entityId);
            if(ent != null)
            {
                for(int i = 0; i < 7; ++i)
                {
                    double d0 = mc.theWorld.rand.nextGaussian() * 0.02D;
                    double d1 = mc.theWorld.rand.nextGaussian() * 0.02D;
                    double d2 = mc.theWorld.rand.nextGaussian() * 0.02D;
                    if(isSmokeParticles)
                    {
                        mc.theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, ent.posX + (double)(mc.theWorld.rand.nextFloat() * ent.width * 2.0F) - (double)ent.width, ent.posY + 0.5D + (double)(mc.theWorld.rand.nextFloat() * ent.height), ent.posZ + (double)(mc.theWorld.rand.nextFloat() * ent.width * 2.0F) - (double)ent.width, d0, d1, d2);
                    }
                    else
                    {
                        ent.getEntityData().setBoolean(EventHandler.BARKABLE_STRING, true);
                        mc.effectRenderer.addEffect(new EntityBuzzFX(mc.theWorld, ent.posX + (double)(mc.theWorld.rand.nextFloat() * ent.width * 2.0F) - (double)ent.width, ent.posY + 0.5D + (double)(mc.theWorld.rand.nextFloat() * ent.height), ent.posZ + (double)(mc.theWorld.rand.nextFloat() * ent.width * 2.0F) - (double)ent.width, d0, d1, d2));
                    }
                }
            }
            Entity ply = mc.theWorld.getEntityByID(playerId);
            if(ply instanceof EntityLivingBase)
            {
                ((EntityLivingBase)ply).swingItem();
            }
        }
    }
}
