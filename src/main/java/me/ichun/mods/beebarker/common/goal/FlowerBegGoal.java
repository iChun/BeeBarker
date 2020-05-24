package me.ichun.mods.beebarker.common.goal;

import net.minecraft.block.Block;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.ai.goal.BegGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class FlowerBegGoal extends BegGoal
{
    public final BegGoal originalGoal;

    public FlowerBegGoal(BegGoal goal)
    {
        super(goal.wolf, goal.minPlayerDistance);
        this.originalGoal = goal;
    }

    @Override
    public boolean shouldExecute() {
        this.player = this.world.getClosestPlayer(this.field_220688_f, this.wolf);
        return (this.player != null && this.hasTemptationItemInHand(this.player)) | originalGoal.shouldExecute();
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (!this.player.isAlive()) {
            return false;
        } else if (this.wolf.getDistanceSq(this.player) > (double)(this.minPlayerDistance * this.minPlayerDistance)) {
            return false;
        } else {
            return (this.timeoutCounter > 0 && this.hasTemptationItemInHand(this.player)) | originalGoal.shouldContinueExecuting();
        }
    }

    @Override
    public void startExecuting()
    {
        super.startExecuting();
        originalGoal.startExecuting();
    }

    @Override
    public void resetTask() {
        super.resetTask();
        originalGoal.resetTask();
    }

    @Override
    public void tick() {
        super.tick();
        originalGoal.tick();
    }

    private boolean hasTemptationItemInHand(PlayerEntity player) {
        if(wolf.isTamed() && !wolf.isInvisible() && wolf.getOwner() == player && !player.isSneaking())
        {
            for(Hand hand : Hand.values())
            {
                ItemStack itemstack = player.getHeldItem(hand);
                if(this.wolf.isTamed() && Block.getBlockFromItem(itemstack.getItem()) instanceof FlowerBlock)
                {
                    return true;
                }
            }
        }

        return false;
    }
}
