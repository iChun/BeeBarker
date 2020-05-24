package me.ichun.mods.beebarker.common.item;

import me.ichun.mods.beebarker.common.BeeBarker;
import me.ichun.mods.beebarker.common.core.EventHandlerServer;
import me.ichun.mods.ichunutil.common.entity.util.EntityHelper;
import me.ichun.mods.ichunutil.common.item.DualHandedItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBeeBarker extends Item
        implements DualHandedItem
{
    public static final String WOLF_DATA_STRING = "WolfData";

    public ItemBeeBarker(Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player)
    {
        return true;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity)
    {
        if(player.world.isRemote)
        {
            EntityHelper.nudgeHand(-50F);
        }
        EntityHelper.playSoundAtEntity(entity, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.NEUTRAL, 0.6F, (entity.world.rand.nextFloat() - entity.world.rand.nextFloat()) * 0.2F + 1F);
        return false;
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context)
    {
        return ActionResultType.FAIL; // Return PASS to allow vanilla handling, any other to skip normal code.
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand)
    {
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
//        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
        return slotChanged; // || !ItemStack.areItemStacksEqual(oldStack, newStack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> items)
    {
        if (this.isInGroup(group))
        {
            ItemStack wolfStack = new ItemStack(BeeBarker.Items.BEE_BARKER.get()); //Special unlimited BeeBarker
            CompoundNBT tag = new CompoundNBT();
            CompoundNBT wolfTag = new CompoundNBT();
            wolfTag.putByte("CollarColor", (byte)DyeColor.RED.getId());
            wolfStack.setTag(tag);
            wolfStack.getTag().put(WOLF_DATA_STRING, wolfTag);
            items.add(wolfStack);

            if(BeeBarker.configServer.easterEgg)
            {
                wolfStack = new ItemStack(BeeBarker.Items.BEE_BARKER.get()); //Special unlimited BeeBarker
                tag = new CompoundNBT();
                wolfTag = new CompoundNBT();
                wolfTag.putString("CustomName", "iChun");
                wolfTag.putByte("CollarColor", (byte)DyeColor.BLUE.getId());
                wolfStack.setTag(tag);
                wolfStack.getTag().put(WOLF_DATA_STRING, wolfTag);
                items.add(wolfStack);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack is, @Nullable World worldIn, List<ITextComponent> list, ITooltipFlag flag)
    {
        if(is.getTag() != null && is.getTag().contains(WOLF_DATA_STRING))
        {
            CompoundNBT wolfDataTag = is.getTag().getCompound(WOLF_DATA_STRING);
            if(wolfDataTag.contains("CustomName"))
            {
                if(wolfDataTag.getString("CustomName").equals("iChun") && BeeBarker.configServer.easterEgg)
                {
                    list.add(new TranslationTextComponent("beebarker.easteregg.item"));
                }
                list.add(new StringTextComponent(wolfDataTag.getString("CustomName")));
            }
            StringBuilder sb = new StringBuilder();
            sb.append(I18n.format("beebarker.beeCharge")).append(": ");
            if(is.getDamage() == 0)
            {
                sb.append(I18n.format("beebarker.beeCharge.unlimited"));
            }
            else
            {
                sb.append(wolfDataTag.getCompound("ForgeData").getInt(EventHandlerServer.BEE_CHARGE_STRING));
            }
            list.add(new StringTextComponent(sb.toString()));
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack)
    {
        return UseAction.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return Integer.MAX_VALUE;
    }
}
