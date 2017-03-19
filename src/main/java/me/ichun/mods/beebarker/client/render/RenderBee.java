package me.ichun.mods.beebarker.client.render;

import me.ichun.mods.beebarker.common.entity.EntityBee;
import me.ichun.mods.ichunutil.client.model.ModelBee;
import me.ichun.mods.ichunutil.client.module.patron.LayerPatronEffect;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.morph.api.MorphApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;

public class RenderBee extends Render<EntityBee>
{
    public ModelBee modelBee;

    public RenderBee(RenderManager manager)
    {
        super(manager);
        modelBee = new ModelBee();
    }

    @Override
    public void doRender(EntityBee bee, double d, double d1, double d2, float f, float f1)
    {
        GlStateManager.pushMatrix();
        double offX = 0D;
        double offY = 0D;
        double offZ = 0D;

        if(bee.getDataManager().get(EntityBee.SHOOTER_ID) == Minecraft.getMinecraft().getRenderViewEntity().getEntityId() && Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityLivingBase && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
        {
            EntityLivingBase player = (EntityLivingBase)Minecraft.getMinecraft().getRenderViewEntity();
            EntityLivingBase renderedShooter = (EntityLivingBase)Minecraft.getMinecraft().getRenderViewEntity();
            if(iChunUtil.hasMorphMod() && player instanceof EntityPlayer && MorphApi.getApiImpl().getMorphEntity(player.getEntityWorld(), player.getName(), Side.CLIENT) != null)
            {
                renderedShooter = MorphApi.getApiImpl().getMorphEntity(player.getEntityWorld(), player.getName(), Side.CLIENT);
            }

            Vec3d look = player.getLookVec();
            Vec3d posTP = renderedShooter.getPositionVector().addVector(look.xCoord * 1.3D - look.zCoord * (renderedShooter.width * 0.2D), look.yCoord * 1.3D + (renderedShooter.getEyeHeight() * 0.8D), look.zCoord * 1.3D + look.xCoord * (renderedShooter.width * 0.2D));
            Vec3d posFP = renderedShooter.getPositionVector().addVector(look.xCoord * 1.3D - look.zCoord * 0.75D, look.yCoord * 1.3D + 1.15D, look.zCoord * 1.5D + look.xCoord * 0.75D);
            Vec3d offset = posTP.subtract(posFP);
            float prog = 1F - MathHelper.clamp_float((bee.ticksExisted + f1) / 10F, 0F, 1F);
            offX = offset.xCoord * prog;
            offY = offset.yCoord * prog;
            offZ = offset.zCoord * prog;
        }

        GlStateManager.translate((float)d - offX, (float)d1 - offY, (float)d2 - offZ);
        GlStateManager.rotate(EntityHelper.interpolateRotation(bee.prevRotationPitch, bee.rotationPitch, f1), 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(180f + f, 0.0F, 1.0F, 0.0F);
        float scale = 0.8F;
        GlStateManager.scale(scale, scale, scale);
        this.bindEntityTexture(bee);
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        modelBee.render(0.0625F);
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBee entity)
    {
        return LayerPatronEffect.texBee;
    }

    public static class RenderFactory implements IRenderFactory<EntityBee>
    {
        @Override
        public Render<? super EntityBee> createRenderFor(RenderManager manager)
        {
            return new RenderBee(manager);
        }
    }
}
