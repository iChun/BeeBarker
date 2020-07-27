package me.ichun.mods.beebarker.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.beebarker.client.core.EventHandlerClient;
import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.item.ItemBeeBarker;
import me.ichun.mods.ichunutil.client.core.ResourceHelper;
import me.ichun.mods.ichunutil.client.model.item.IModel;
import me.ichun.mods.ichunutil.client.model.item.ItemModelRenderer;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.WolfModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

@SuppressWarnings("deprecation")
public class ItemRenderBeeBarker extends ItemStackTileEntityRenderer
        implements IModel
{
    public static final ItemCameraTransforms ITEM_CAMERA_TRANSFORMS = new ItemCameraTransforms(
            new ItemTransformVec3f(new Vector3f(0F, 0F, 0F), new Vector3f(-0.0085F, -0.70F, -0.6F), new Vector3f(0.95F, 0.95F, 0.95F)), //tp left
            new ItemTransformVec3f(new Vector3f(0F, 0F, 0F), new Vector3f(0.0085F, -0.70F, -0.6F), new Vector3f(0.95F, 0.95F, 0.95F)), //tp right
            new ItemTransformVec3f(new Vector3f(3F, 0F, 2F), new Vector3f(0.3F, -0.6F, -0.6F), new Vector3f(1F, 1F, 0.8F)), //fp left
            new ItemTransformVec3f(new Vector3f(3F, 0F, 2F), new Vector3f(0.3F, -0.6F, -0.6F), new Vector3f(1F, 1F, 0.8F)), //fp right
            new ItemTransformVec3f(new Vector3f(0F, 0F, 0F), new Vector3f(0F, -0.25F, -0.25F), new Vector3f(1.5F, 1.5F, 1.5F)), //head
            new ItemTransformVec3f(new Vector3f(25F, 212.5F, 0F), new Vector3f(0.075F, -0.325F, -0.05F), new Vector3f(0.7F, 0.7F, 0.7F)), //gui
            new ItemTransformVec3f(new Vector3f(0F, 90F, 0F), new Vector3f(0F, 0F, 0F), new Vector3f(0.3F, 0.3F, 0.3F)), //ground
            new ItemTransformVec3f(new Vector3f(0F, 90F, 0F), new Vector3f(-0.075F, -0.325F, -0.05F), new Vector3f(0.6F, 0.6F, 0.6F)) //fixed
    );

    public static final ItemRenderBeeBarker INSTANCE = new ItemRenderBeeBarker();

    //Stuff to do in relation to getting the current perspective and the current player holding it
    private ItemCameraTransforms.TransformType currentPerspective;
    private AbstractClientPlayerEntity lastPlayer;

    //Models
    private WolfModel modelWolf;

    private ItemRenderBeeBarker()
    {
        modelWolf = new WolfModel()
        {
            {
                float f = 0.0F;
                this.headChild.cubeList.clear();
                this.headChild.setTextureOffset(0, 0).addBox(-3.0F, -3.0F, -2.0F, 6, 6, 4, f);
                this.body.cubeList.clear();
                this.body.addBox(-4.0F, -2.0F, -3.0F, 6, 9, 6, f);
                this.mane.cubeList.clear();
                this.mane.addBox(-4.0F, -3.0F, -3.0F, 8, 6, 7, f);
                this.legBackRight.cubeList.clear();
                this.legBackRight.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
                this.legBackLeft.cubeList.clear();
                this.legBackLeft.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
                this.legFrontRight.cubeList.clear();
                this.legFrontRight.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
                this.legFrontLeft.cubeList.clear();
                this.legFrontLeft.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
                this.tailChild.cubeList.clear();
                this.tailChild.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
                this.headChild.setTextureOffset(16, 14).addBox(-3.0F, -5.0F, 0.0F, 2, 2, 1, f);
                this.headChild.setTextureOffset(16, 14).addBox(1.0F, -5.0F, 0.0F, 2, 2, 1, f);
                this.headChild.setTextureOffset(0, 10).addBox(-1.5F, 0.0F, -5.0F, 3, 3, 4, f);
            }
        };
        modelWolf.isChild = false;
        modelWolf.body.setRotationPoint(0.0F, 14.0F, 2.0F);
        modelWolf.body.rotateAngleX = ((float)Math.PI / 2F);
        modelWolf.mane.setRotationPoint(-1.0F, 14.0F, -3.0F);
        modelWolf.mane.rotateAngleX = modelWolf.body.rotateAngleX;
        modelWolf.tail.setRotationPoint(-1.0F, 12.0F, 8.0F);
        modelWolf.legBackRight.setRotationPoint(-2.5F, 16.0F, 7.0F);
        modelWolf.legBackLeft.setRotationPoint(0.5F, 16.0F, 7.0F);
        modelWolf.legFrontRight.setRotationPoint(-2.5F, 16.0F, -4.0F);
        modelWolf.legFrontLeft.setRotationPoint(0.5F, 16.0F, -4.0F);
        modelWolf.tail.rotateAngleY = (float)Math.toRadians(90F);
        modelWolf.tail.rotateAngleX = (float)Math.toRadians(0F);
    }

    @Override
    public void func_239207_a_(ItemStack is, ItemCameraTransforms.TransformType transformType, MatrixStack stack, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        float partialTick = iChunUtil.eventHandlerClient.partialTick;

        modelWolf.legBackRight.rotateAngleZ = modelWolf.legBackRight.rotateAngleX = 0.0F;
        modelWolf.legBackLeft.rotateAngleZ = modelWolf.legBackLeft.rotateAngleX = 0.0F;
        modelWolf.legFrontRight.rotateAngleZ = modelWolf.legFrontRight.rotateAngleX = 0.0F;
        modelWolf.legFrontLeft.rotateAngleZ = modelWolf.legFrontLeft.rotateAngleX = 0.0F;
        modelWolf.tailChild.rotateAngleZ = (float)Math.toRadians(84.5F);
        modelWolf.head.rotateAngleX = modelWolf.head.rotateAngleY = 0.0F;
        modelWolf.tail.rotateAngleX = (float)Math.toRadians(200F);

        setToOrigin(stack);
        stack.translate(1F/16F, 0F, 0F); //retranslate cause of our "correction" of the model, shifting everything off by one voxel

        Minecraft mc = Minecraft.getInstance();

        boolean isFirstPerson = ItemModelRenderer.isFirstPerson(currentPerspective) && lastPlayer == mc.player;

        float pullTime = ((EventHandlerClient.PULL_TIME - BeeBarker.eventHandlerClient.pullTime) + partialTick);
        if(isFirstPerson && BeeBarker.eventHandlerClient.pressState.contains(mc.player.getName().getUnformattedComponentText()) && BeeBarker.eventHandlerClient.pullTime == 7)
        {
            pullTime = (EventHandlerClient.PULL_TIME - BeeBarker.eventHandlerClient.pullTime);
        }

        float curveProg = (float)Math.sin(Math.toRadians(MathHelper.clamp((float)Math.pow((pullTime / EventHandlerClient.PULL_TIME), 0.5D), 0.0F, 1.0F) * 180F));

        if(ItemModelRenderer.isEntityRender(currentPerspective))
        {
            int ticks = iChunUtil.eventHandlerClient.ticks;
            modelWolf.legBackRight.rotateAngleZ += MathHelper.cos(ticks * 0.09F) * 0.025F;
            modelWolf.legBackLeft.rotateAngleZ -= MathHelper.cos(ticks * 0.09F) * 0.025F;
            modelWolf.legBackRight.rotateAngleX += MathHelper.sin(ticks * 0.067F) * 0.025F;
            modelWolf.legBackLeft.rotateAngleX -= MathHelper.sin(ticks * 0.067F) * 0.025F;
            modelWolf.legFrontLeft.rotateAngleZ += MathHelper.cos(ticks * 0.09F) * 0.025F;
            modelWolf.legFrontRight.rotateAngleZ -= MathHelper.cos(ticks * 0.09F) * 0.025F;
            modelWolf.legFrontLeft.rotateAngleX += MathHelper.sin(ticks * 0.067F) * 0.025F;
            modelWolf.legFrontRight.rotateAngleX -= MathHelper.sin(ticks * 0.067F) * 0.025F;
            float yaw = MathHelper.cos(ticks * 0.09F) * 1F;
            float pitch = MathHelper.sin(ticks * 0.067F) * 1F;
            if(isFirstPerson)
            {
                modelWolf.tailChild.rotateAngleZ += (float)Math.toRadians(5F * curveProg);
                stack.rotate(Vector3f.XP.rotationDegrees(-1.5F * curveProg));
                stack.rotate(Vector3f.YP.rotationDegrees(-1F * curveProg));
                if(BeeBarker.eventHandlerClient.pullTime == 0)
                {
                    yaw += MathHelper.rotLerp(BeeBarker.eventHandlerClient.prevYaw, BeeBarker.eventHandlerClient.currentYaw, partialTick);
                    pitch += MathHelper.rotLerp(BeeBarker.eventHandlerClient.prevPitch, BeeBarker.eventHandlerClient.currentPitch, partialTick);
                }
            }

            modelWolf.setRotationAngles(null, 0F, 0F, 0F, yaw, pitch);
            modelWolf.render(stack, bufferIn.getBuffer(RenderType.getEntityCutout(ResourceHelper.TEX_TAMED_WOLF)), combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
        }
        else
        {
            modelWolf.setRotationAngles(null, 0F, 0F, 0F, 0F, 0F);
            modelWolf.render(stack, bufferIn.getBuffer(RenderType.getEntityCutout(ResourceHelper.TEX_TAMED_WOLF)), combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
        }

        //Render collar
        DyeColor enumdyecolor = DyeColor.byId(!is.isEmpty() && is.getTag() != null && is.getTag().contains(ItemBeeBarker.WOLF_DATA_STRING) ? is.getTag().getCompound(ItemBeeBarker.WOLF_DATA_STRING).getByte("CollarColor") : 12);
        float[] afloat = SheepEntity.getDyeRgb(enumdyecolor);
        modelWolf.render(stack, bufferIn.getBuffer(RenderType.getEntityCutout(ResourceHelper.TEX_WOLF_COLLAR)), combinedLightIn, combinedOverlayIn, afloat[0], afloat[1], afloat[2], 1F);

        if(isFirstPerson && lastPlayer != null && !lastPlayer.isInvisible())
        {
            EntityRenderer render = mc.getRenderManager().getRenderer(lastPlayer);
            if(render instanceof PlayerRenderer)
            {
                mc.getTextureManager().bindTexture(lastPlayer.getLocationSkin());
                PlayerRenderer renderPlayer = (PlayerRenderer)render;

                if(ItemModelRenderer.isLeftHand(currentPerspective))
                {
                    stack.push();
                    stack.rotate(Vector3f.YP.rotationDegrees(-70F));
                    stack.rotate(Vector3f.XP.rotationDegrees(-100F));
                    stack.translate(0.35F, -1.0F, 1.225F);
                    renderPlayer.renderRightArm(stack, bufferIn, combinedLightIn, lastPlayer);
                    stack.pop();

                    stack.push();
                    stack.scale(0.9F, 0.9F, 0.9F);
                    stack.rotate(Vector3f.YP.rotationDegrees(-4F));
                    stack.rotate(Vector3f.XP.rotationDegrees(-75F));

                    float yOffset = -0.2F * curveProg;
                    float zOffset = 0.025F * curveProg;
                    stack.translate(-0.4F, -1.25F + yOffset, 1.275F + zOffset);
                    stack.rotate(Vector3f.XP.rotationDegrees(-17F));
                    renderPlayer.renderLeftArm(stack, bufferIn, combinedLightIn, lastPlayer);
                    stack.pop();
                }
                else
                {
                    stack.push();
                    stack.rotate(Vector3f.YP.rotationDegrees(70F));
                    stack.rotate(Vector3f.XP.rotationDegrees(-100F));
                    stack.translate(-0.55F, -1.0F, 1.125F);
                    renderPlayer.renderLeftArm(stack, bufferIn, combinedLightIn, lastPlayer);
                    stack.pop();

                    stack.push();
                    stack.scale(0.9F, 0.9F, 0.9F);
                    stack.rotate(Vector3f.YP.rotationDegrees(-4F));
                    stack.rotate(Vector3f.XP.rotationDegrees(-75F));

                    float yOffset = -0.2F * curveProg;
                    float zOffset = 0.025F * curveProg;
                    stack.translate(0.4F, -1.25F + yOffset, 1.275F + zOffset);
                    stack.rotate(Vector3f.XP.rotationDegrees(-17F));
                    renderPlayer.renderRightArm(stack, bufferIn, combinedLightIn, lastPlayer);
                    stack.pop();
                }
            }
        }

        //reset these vars. they should be set per render.
        lastPlayer = null;
        currentPerspective = null;
    }

    @Override
    public void setToOrigin(MatrixStack stack)
    {
        stack.translate(0.5D, 0.5D, 0.5D); //reset the translation in ItemRenderer
        stack.translate(0.0D, 1.5D, 0.0D); //translate down to the base of models
        stack.scale(-1F, -1F, 1F); //flip the models so it renders upright
    }

    @Override
    public ItemCameraTransforms getCameraTransforms()
    {
        return ITEM_CAMERA_TRANSFORMS;
    }

    @Override
    public void handleItemState(ItemStack stack, ClientWorld world, LivingEntity entity)
    {
        if(entity instanceof AbstractClientPlayerEntity)
        {
            lastPlayer = (AbstractClientPlayerEntity)entity;
        }
    }

    @Override
    public void handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat)
    {
        currentPerspective = cameraTransformType;
    }
}
