package me.ichun.mods.beebarker.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.beebarker.common.entity.EntityBee;
import me.ichun.mods.ichunutil.client.model.ModelBee;
import me.ichun.mods.ichunutil.common.item.DualHandedItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderBee extends EntityRenderer<EntityBee>
{
    public ModelBee modelBee;

    public RenderBee(EntityRendererManager manager)
    {
        super(manager);
        modelBee = new ModelBee();
    }

    @Override
    public void render(EntityBee bee, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        double offX = 0D;
        double offY = 0D;
        double offZ = 0D;

        if(bee.getDataManager().get(EntityBee.SHOOTER_ID) == Minecraft.getInstance().getRenderViewEntity().getEntityId() && Minecraft.getInstance().getRenderViewEntity() instanceof LivingEntity && Minecraft.getInstance().gameSettings.thirdPersonView == 0)
        {
            LivingEntity player = (LivingEntity)Minecraft.getInstance().getRenderViewEntity();
            LivingEntity renderedShooter = (LivingEntity)Minecraft.getInstance().getRenderViewEntity();
            //            if(iChunUtil.hasMorphMod() && player instanceof PlayerEntity && MorphApi.getApiImpl().getMorphEntity(player.getEntityWorld(), player.getName(), Side.CLIENT) != null) //TODO morph
            //            {
            //                renderedShooter = MorphApi.getApiImpl().getMorphEntity(player.getEntityWorld(), player.getName(), Side.CLIENT);
            //            }

            Vector3d look = player.getLookVec();
            Vector3d posTP = renderedShooter.getPositionVec().add(look.x * 1.3D - look.z * (renderedShooter.getWidth() * 0.2D), look.y * 1.3D + (renderedShooter.getEyeHeight() * 0.8D), look.z * 1.3D + look.x * (renderedShooter.getWidth() * 0.2D));
            Vector3d posFP = renderedShooter.getPositionVec().add(look.x * 1.3D - look.z * 0.9D, look.y * 1.3D + 1.15D, look.z * 1.3D + look.x * 0.9D);
            Vector3d offset = posTP.subtract(posFP);
            float prog = 1F - MathHelper.clamp((bee.ticksExisted + partialTicks) / 10F, 0F, 1F);
            offY = offset.y * prog;

            switch(DualHandedItem.getHandSide(player, DualHandedItem.getUsableDualHandedItem(player)))
            {
                case RIGHT:
                {
                    offX = offset.x * prog;
                    offZ = offset.z * prog;
                    break;
                }
                case LEFT:
                {
                    offX = -offset.x * prog;
                    offZ = -offset.z * prog;
                    break;
                }
            }
        }

        stack.translate(-offX, -offY, -offZ);
        stack.rotate(Vector3f.ZP.rotationDegrees(MathHelper.rotLerp(bee.prevRotationPitch, bee.rotationPitch, partialTicks)));
        stack.rotate(Vector3f.YP.rotationDegrees(180f + entityYaw));
        float scale = 0.8F;
        stack.scale(scale, scale, scale);
        stack.scale(-1.0F, -1.0F, 1.0F);
        modelBee.render(stack, bufferIn.getBuffer(RenderType.getEntityTranslucentCull(getEntityTexture(bee))), packedLightIn, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
    }

    @Override
    public ResourceLocation getEntityTexture(EntityBee entity)
    {
        return ModelBee.TEX_BEE;
    }

    public static class RenderFactory implements IRenderFactory<EntityBee>
    {
        @Override
        public EntityRenderer<? super EntityBee> createRenderFor(EntityRendererManager manager)
        {
            return new RenderBee(manager);
        }
    }
}
