package me.ichun.mods.beebarker.client.render;

import me.ichun.mods.beebarker.client.model.ModelBee;
import me.ichun.mods.beebarker.common.entity.EntityBee;
import me.ichun.mods.morph.api.MorphApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.relauncher.Side;
import us.ichun.mods.ichunutil.common.core.EntityHelperBase;
import us.ichun.mods.ichunutil.common.iChunUtil;

public class RenderBee extends Render
{
    public ModelBase modelBee;
    public final ResourceLocation txBee = new ResourceLocation("beebarker","textures/model/bee.png");

    public RenderBee()
    {
        super(Minecraft.getMinecraft().getRenderManager());
        modelBee = new ModelBee();
    }

    public void renderBee(EntityBee bee, double d, double d1, double d2, float f, float f1)
    {
        GlStateManager.pushMatrix();
        double offX = 0D;
        double offY = 0D;
        double offZ = 0D;

        if(bee.getDataWatcher().getWatchableObjectInt(17) == Minecraft.getMinecraft().getRenderViewEntity().getEntityId() && Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityLivingBase && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
        {
            EntityLivingBase player = (EntityLivingBase)Minecraft.getMinecraft().getRenderViewEntity();
            EntityLivingBase renderedShooter = (EntityLivingBase)Minecraft.getMinecraft().getRenderViewEntity();
            if(iChunUtil.hasMorphMod && player instanceof EntityPlayer && MorphApi.getApiImpl().getMorphEntity(player.getEntityWorld(), player.getCommandSenderName(), Side.CLIENT) != null)
            {
                renderedShooter = MorphApi.getApiImpl().getMorphEntity(player.getEntityWorld(), player.getCommandSenderName(), Side.CLIENT);
            }

            Vec3 look = player.getLookVec();
            Vec3 posTP = renderedShooter.getPositionVector().addVector(look.xCoord * 1.3D - look.zCoord * (renderedShooter.width * 0.2D), look.yCoord * 1.3D + (renderedShooter.getEyeHeight() * 0.8D), look.zCoord * 1.3D + look.xCoord * (renderedShooter.width * 0.2D));
            Vec3 posFP = renderedShooter.getPositionVector().addVector(look.xCoord * 1.3D - look.zCoord * 0.75D, look.yCoord * 1.3D + 1.15D, look.zCoord * 1.5D + look.xCoord * 0.75D);
            Vec3 offset = posTP.subtract(posFP);
            float prog = 1F - MathHelper.clamp_float((bee.ticksExisted + f1) / 10F, 0F, 1F);
            offX = offset.xCoord * prog;
            offY = offset.yCoord * prog;
            offZ = offset.zCoord * prog;
        }

        GlStateManager.translate((float)d - offX, (float)d1 - offY, (float)d2 - offZ);
        GlStateManager.rotate(EntityHelperBase.interpolateRotation(bee.prevRotationPitch, bee.rotationPitch, f1), 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(180f + f, 0.0F, 1.0F, 0.0F);
        float scale = 0.8F;
        GlStateManager.scale(scale, scale, scale);
        this.bindEntityTexture(bee);
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        this.modelBee.render(bee, 0.0F, 0.0F, 0F, 0.0F, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
    }

    @Override
    public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9)
    {
        this.renderBee((EntityBee)var1, var2, var4, var6, var8, var9);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return txBee;
    }
}
