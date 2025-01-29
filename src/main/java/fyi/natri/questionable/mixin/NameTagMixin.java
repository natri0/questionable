package fyi.natri.questionable.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.NameTagItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NameTagItem.class)
public class NameTagMixin {

    @ModifyExpressionValue(method = "useOnEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;isSaveable()Z"))
    public boolean makePlayersNameable(boolean original, @Local(argsOnly = true) LivingEntity entity) {
        return original || entity instanceof PlayerEntity; // :p
    }
}
