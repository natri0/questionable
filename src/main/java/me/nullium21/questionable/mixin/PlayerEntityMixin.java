package me.nullium21.questionable.mixin;

import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements Leashable, Saddleable {

    @Unique
    private boolean isSaddled;
    @Unique
    private LeashData leashData;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void interact(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (isSpectator()) return; // fall-through to method

        if (!(entity instanceof PlayerEntity other)) return;
        ItemStack item = getStackInHand(hand);

        if (item.isOf(Items.SHEARS) && other.hasCustomName()) {
            item.damage(1, (PlayerEntity) (Object) this);
            other.setCustomName(null);

            cir.setReturnValue(ActionResult.SUCCESS);
            cir.cancel();
        } else if (!getWorld().isClient() && item.isOf(Items.SADDLE) && !other.hasPassengers()) {
            startRiding(other);

            cir.setReturnValue(ActionResult.SUCCESS);
            cir.cancel();
        }
    }

    @Inject(method = "getName", at = @At("TAIL"), cancellable = true)
    private void getName(CallbackInfoReturnable<Text> cir) {
        PlayerEntity self = (PlayerEntity) (Object) this;

        if (self.hasCustomName() && self.getCustomName() != null) {
            MutableText prefix = self.getCustomName().copy().append(" ");
            cir.setReturnValue(prefix.append(cir.getReturnValue()));
        }
    }

    @Override
    public boolean canBeSaddled() {
        return !isSaddled;
    }

    @Override
    public void saddle(ItemStack stack, @Nullable SoundCategory sound) {
        isSaddled = true;
    }

    @Override
    public boolean isSaddled() {
        return isSaddled;
    }

    @Override
    public @Nullable LeashData getLeashData() {
        return leashData;
    }

    @Override
    public void setLeashData(@Nullable Leashable.LeashData leashData) {
        this.leashData = leashData;
    }

    @Override
    public boolean beforeLeashTick(Entity leashHolder, float distance) {
        if (distance >= 20) {
            teleport(leashHolder.getX(), leashHolder.getY(), leashHolder.getZ(), false);
        } else if (distance >= 7) {
            applyLeashElasticity(leashHolder, distance);
            limitFallDistance();
            velocityDirty = velocityModified = true;
        }

        return false;
    }
}
