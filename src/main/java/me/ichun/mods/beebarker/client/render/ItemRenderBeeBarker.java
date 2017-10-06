package me.ichun.mods.beebarker.client.render;

import me.ichun.mods.beebarker.client.core.EventHandlerClient;
import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.item.ItemBeeBarker;
import me.ichun.mods.ichunutil.client.model.item.IModelBase;
import me.ichun.mods.ichunutil.client.model.item.ModelBaseWrapper;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

@SuppressWarnings("deprecation")
public class ItemRenderBeeBarker implements IModelBase
{
    public static final ItemCameraTransforms itemCameraTransforms = new ItemCameraTransforms(
            new ItemTransformVec3f(new Vector3f(0F, 0F, 0F), new Vector3f(0.05F, 0.7F, -0.64F), new Vector3f(0.95F, 0.95F, 0.95F)), //tp left
            new ItemTransformVec3f(new Vector3f(0F, 0F, 0F), new Vector3f(-0.08F, 0.7F, -0.64F), new Vector3f(0.95F, 0.95F, 0.95F)), //tp right
            new ItemTransformVec3f(new Vector3f(5F, 5F, 2F), new Vector3f(0.2F, 0.775F, -0.7525F), new Vector3f(1F, 1F, 0.8F)), //fp left
            new ItemTransformVec3f(new Vector3f(5F, 5F, 2F), new Vector3f(0.2F, 0.775F, -0.7525F), new Vector3f(1F, 1F, 0.8F)), //fp right
            new ItemTransformVec3f(new Vector3f(0F, 0F, 0F), new Vector3f(0F, 0.4F, 0F), new Vector3f(1F, 1F, 1F)), //head
            new ItemTransformVec3f(new Vector3f(25F, 212.5F, 0F), new Vector3f(0.075F, 0.6F, -0.05F), new Vector3f(0.7F, 0.7F, 0.7F)), //gui
            new ItemTransformVec3f(new Vector3f(25F, 212.5F, 0F), new Vector3f(0.075F, 0.6F, -0.05F), new Vector3f(0.7F, 0.7F, 0.7F)), //ground
            new ItemTransformVec3f(new Vector3f(25F, 212.5F, 0F), new Vector3f(0.075F, 0.6F, -0.05F), new Vector3f(0.7F, 0.7F, 0.7F)) //fixed
    );

    //Stuff to do in relation to getting the current perspective and the current player holding it
    private ItemStack heldStack;
    private ItemCameraTransforms.TransformType currentPerspective;
    private EntityPlayer lastPlayer;

    //Models
    private ModelWolf modelWolf;

    public ItemRenderBeeBarker()
    {
        modelWolf = new ModelWolf()
        {
            {
                float f = 0.0F;
                float f1 = 13.5F;
                this.wolfHeadMain = new ModelRenderer(this, 0, 0);
                this.wolfHeadMain.addBox(-3.0F, -3.0F, -2.0F, 6, 6, 4, f);
                this.wolfHeadMain.setRotationPoint(-1.0F, f1, -7.0F);
                this.wolfBody = new ModelRenderer(this, 18, 14);
                this.wolfBody.addBox(-4.0F, -2.0F, -3.0F, 6, 9, 6, f);
                this.wolfBody.setRotationPoint(0.0F, 14.0F, 2.0F);
                this.wolfMane = new ModelRenderer(this, 21, 0);
                this.wolfMane.addBox(-4.0F, -3.0F, -3.0F, 8, 6, 7, f);
                this.wolfMane.setRotationPoint(-1.0F, 14.0F, 2.0F);
                this.wolfLeg1 = new ModelRenderer(this, 0, 18);
                this.wolfLeg1.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
                this.wolfLeg1.setRotationPoint(-2.5F, 16.0F, 7.0F);
                this.wolfLeg2 = new ModelRenderer(this, 0, 18);
                this.wolfLeg2.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
                this.wolfLeg2.setRotationPoint(0.5F, 16.0F, 7.0F);
                this.wolfLeg3 = new ModelRenderer(this, 0, 18);
                this.wolfLeg3.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
                this.wolfLeg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
                this.wolfLeg4 = new ModelRenderer(this, 0, 18);
                this.wolfLeg4.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
                this.wolfLeg4.setRotationPoint(0.5F, 16.0F, -4.0F);
                this.wolfTail = new ModelRenderer(this, 9, 18);
                this.wolfTail.addBox(-1.0F, 0.0F, -1.0F, 2, 8, 2, f);
                this.wolfTail.setRotationPoint(-1.0F, 12.0F, 8.0F);
                this.wolfHeadMain.setTextureOffset(16, 14).addBox(-3.0F, -5.0F, 0.0F, 2, 2, 1, f);
                this.wolfHeadMain.setTextureOffset(16, 14).addBox(1.0F, -5.0F, 0.0F, 2, 2, 1, f);
                this.wolfHeadMain.setTextureOffset(0, 10).addBox(-1.5F, 0.0F, -5.0F, 3, 3, 4, f);
            }
        };
        modelWolf.isChild = false;
        modelWolf.wolfBody.setRotationPoint(0.0F, 14.0F, 2.0F);
        modelWolf.wolfBody.rotateAngleX = ((float)Math.PI / 2F);
        modelWolf.wolfMane.setRotationPoint(-1.0F, 14.0F, -3.0F);
        modelWolf.wolfMane.rotateAngleX = modelWolf.wolfBody.rotateAngleX;
        modelWolf.wolfTail.setRotationPoint(-1.0F, 12.0F, 8.0F);
        modelWolf.wolfLeg1.setRotationPoint(-2.5F, 16.0F, 7.0F);
        modelWolf.wolfLeg2.setRotationPoint(0.5F, 16.0F, 7.0F);
        modelWolf.wolfLeg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
        modelWolf.wolfLeg4.setRotationPoint(0.5F, 16.0F, -4.0F);
        modelWolf.wolfTail.rotateAngleY = (float)Math.toRadians(90F);
        modelWolf.wolfTail.rotateAngleX = (float)Math.toRadians(0F);
    }

    @Override
    public ResourceLocation getTexture()
    {
        return ResourceHelper.texTamedWolf;
    }

    @Override
    public void renderModel(float renderTick)
    {
        modelWolf.wolfLeg1.rotateAngleZ = modelWolf.wolfLeg1.rotateAngleX = 0.0F;
        modelWolf.wolfLeg2.rotateAngleZ = modelWolf.wolfLeg2.rotateAngleX = 0.0F;
        modelWolf.wolfLeg3.rotateAngleZ = modelWolf.wolfLeg3.rotateAngleX = 0.0F;
        modelWolf.wolfLeg4.rotateAngleZ = modelWolf.wolfLeg4.rotateAngleX = 0.0F;
        modelWolf.wolfTail.rotateAngleZ = (float)Math.toRadians(84.5F);
        modelWolf.wolfHeadMain.rotateAngleX = modelWolf.wolfHeadMain.rotateAngleY = 0.0F;
        modelWolf.wolfTail.rotateAngleX = (float)Math.toRadians(200F);

        GlStateManager.pushMatrix();

        Minecraft mc = Minecraft.getMinecraft();

        boolean isItemRender = ModelBaseWrapper.isItemRender(currentPerspective) || currentPerspective == ItemCameraTransforms.TransformType.GUI;
        boolean isFirstPerson = ModelBaseWrapper.isFirstPerson(currentPerspective) && lastPlayer == mc.player;

        float pullTime = ((EventHandlerClient.PULL_TIME - BeeBarker.eventHandlerClient.pullTime) + renderTick);
        if(isFirstPerson && BeeBarker.eventHandlerClient.pressState.contains(mc.player.getName()) && BeeBarker.eventHandlerClient.pullTime == 7)
        {
            pullTime = (EventHandlerClient.PULL_TIME - BeeBarker.eventHandlerClient.pullTime);
        }

        float curveProg = (float)Math.sin(Math.toRadians(MathHelper.clamp((float)Math.pow((pullTime / EventHandlerClient.PULL_TIME), 0.5D), 0.0F, 1.0F) * 180F));

        if(ModelBaseWrapper.isEntityRender(currentPerspective))
        {
            int ticks = iChunUtil.eventHandlerClient.ticks;
            modelWolf.wolfLeg1.rotateAngleZ += MathHelper.cos(ticks * 0.09F) * 0.025F;
            modelWolf.wolfLeg2.rotateAngleZ -= MathHelper.cos(ticks * 0.09F) * 0.025F;
            modelWolf.wolfLeg1.rotateAngleX += MathHelper.sin(ticks * 0.067F) * 0.025F;
            modelWolf.wolfLeg2.rotateAngleX -= MathHelper.sin(ticks * 0.067F) * 0.025F;
            modelWolf.wolfLeg4.rotateAngleZ += MathHelper.cos(ticks * 0.09F) * 0.025F;
            modelWolf.wolfLeg3.rotateAngleZ -= MathHelper.cos(ticks * 0.09F) * 0.025F;
            modelWolf.wolfLeg4.rotateAngleX += MathHelper.sin(ticks * 0.067F) * 0.025F;
            modelWolf.wolfLeg3.rotateAngleX -= MathHelper.sin(ticks * 0.067F) * 0.025F;
            float yaw = MathHelper.cos(ticks * 0.09F) * 1F;
            float pitch = MathHelper.sin(ticks * 0.067F) * 1F;
            if(isFirstPerson)
            {
                modelWolf.wolfTail.rotateAngleZ += (float)Math.toRadians(5F * curveProg);
                GlStateManager.rotate(-1.5F * curveProg, 1F, 0F, 0F);
                GlStateManager.rotate(-1F * curveProg, 0F, 1F, 0F);
                if(BeeBarker.eventHandlerClient.pullTime == 0)
                {
                    yaw += EntityHelper.interpolateRotation(BeeBarker.eventHandlerClient.prevYaw, BeeBarker.eventHandlerClient.currentYaw, renderTick);
                    pitch += EntityHelper.interpolateRotation(BeeBarker.eventHandlerClient.prevPitch, BeeBarker.eventHandlerClient.currentPitch, renderTick);
                }
            }

            modelWolf.render(null, 0.0F, 0.0F, 0.0F, yaw, pitch, 0.0625F);
        }
        else if(isItemRender)
        {
            modelWolf.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        }

        //Render collar
        mc.getTextureManager().bindTexture(ResourceHelper.texWolfCollar);
        EnumDyeColor enumdyecolor = EnumDyeColor.byDyeDamage(heldStack != null && heldStack.getTagCompound() != null && heldStack.getTagCompound().hasKey(ItemBeeBarker.WOLF_DATA_STRING) ? ((NBTTagCompound)heldStack.getTagCompound().getTag(ItemBeeBarker.WOLF_DATA_STRING)).getByte("CollarColor") : 12);
        float[] afloat = EntitySheep.getDyeRgb(enumdyecolor);
        GlStateManager.color(afloat[0], afloat[1], afloat[2]);
        modelWolf.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

        if(isFirstPerson && lastPlayer instanceof AbstractClientPlayer && !lastPlayer.isInvisible())
        {
            Render render = mc.getRenderManager().getEntityRenderObject(lastPlayer);
            if(render instanceof RenderPlayer)
            {
                mc.getTextureManager().bindTexture(((AbstractClientPlayer)lastPlayer).getLocationSkin());
                RenderPlayer renderPlayer = (RenderPlayer)render;

                if(ModelBaseWrapper.isLeftHand(currentPerspective))
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.rotate(-70F, 0F, 1F, 0F);
                    GlStateManager.rotate(-100F, 1F, 0F, 0F);
                    GlStateManager.translate(0.35F, -1.0F, 1.225F);
                    renderPlayer.renderRightArm((AbstractClientPlayer)lastPlayer);
                    GlStateManager.popMatrix();

                    GlStateManager.pushMatrix();
                    GlStateManager.scale(0.9F, 0.9F, 0.9F);
                    GlStateManager.rotate(-4F, 0F, 1F, 0F);
                    GlStateManager.rotate(-75F, 1F, 0F, 0F);

                    float yOffset = -0.2F * curveProg;
                    float zOffset = 0.025F * curveProg;
                    GlStateManager.translate(-0.4F, -1.25F + yOffset, 1.275F + zOffset);
                    GlStateManager.rotate(-17F, 1F, 0F, 0F);
                    renderPlayer.renderLeftArm((AbstractClientPlayer)lastPlayer);
                    GlStateManager.popMatrix();
                }
                else
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.rotate(70F, 0F, 1F, 0F);
                    GlStateManager.rotate(-100F, 1F, 0F, 0F);
                    GlStateManager.translate(-0.55F, -1.0F, 1.125F);
                    renderPlayer.renderLeftArm((AbstractClientPlayer)lastPlayer);
                    GlStateManager.popMatrix();

                    GlStateManager.pushMatrix();
                    GlStateManager.scale(0.9F, 0.9F, 0.9F);
                    GlStateManager.rotate(-4F, 0F, 1F, 0F);
                    GlStateManager.rotate(-75F, 1F, 0F, 0F);

                    float yOffset = -0.2F * curveProg;
                    float zOffset = 0.025F * curveProg;
                    GlStateManager.translate(0.4F, -1.25F + yOffset, 1.275F + zOffset);
                    GlStateManager.rotate(-17F, 1F, 0F, 0F);
                    renderPlayer.renderRightArm((AbstractClientPlayer)lastPlayer);
                    GlStateManager.popMatrix();
                }
            }
        }

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.popMatrix();
    }

    @Override
    public void postRender()
    {
        lastPlayer = null;
        currentPerspective = null;
    }

    @Override
    public ModelBase getModel()
    {
        return modelWolf;
    }

    @Override
    public ItemCameraTransforms getCameraTransforms()
    {
        return itemCameraTransforms;
    }

    @Override
    public void handleBlockState(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){}

    @Override
    public void handleItemState(ItemStack stack, World world, EntityLivingBase entity)
    {
        if(entity instanceof EntityPlayer)
        {
            lastPlayer = (EntityPlayer)entity;
        }
        heldStack = stack;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, Pair<? extends IBakedModel, Matrix4f> pair)
    {
        currentPerspective = cameraTransformType;
        return pair;
    }

    @Override
    public boolean useVanillaCameraTransform()
    {
        return true;
    }
}
