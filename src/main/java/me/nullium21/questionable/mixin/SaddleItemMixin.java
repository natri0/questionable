package me.nullium21.questionable.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SaddleItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SaddleItem.class)
public class SaddleItemMixin {

    @ModifyExpressionValue(method = "useOnEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Saddleable;isSaddled()Z"))
    public boolean playersCanBeSaddled(boolean original, @Local(argsOnly = true) LivingEntity entity) {
        return original || entity instanceof PlayerEntity;
    }
}
