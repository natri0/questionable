package fyi.natri.questionable.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @ModifyExpressionValue(method = "startRiding(Lnet/minecraft/entity/Entity;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;isSaveable()Z"))
    public boolean makePlayersSaddleableForReal(boolean original, @Local(argsOnly = true) Entity entity) {
        return entity instanceof PlayerEntity || original;
    }

    @Inject(method = "startRiding(Lnet/minecraft/entity/Entity;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addPassenger(Lnet/minecraft/entity/Entity;)V", shift = At.Shift.AFTER))
    public void sendS2CPacketOnStartRiding(Entity entity, boolean force, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof ServerPlayerEntity plr) plr.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(entity));
    }

    @Inject(method = "dismountVehicle", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;removePassenger(Lnet/minecraft/entity/Entity;)V", shift = At.Shift.AFTER))
    public void sendS2CPacketOnStopRiding(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self.getVehicle() instanceof ServerPlayerEntity plr) plr.networkHandler.sendPacket(new EntityPassengersSetS2CPacket(self));
    }
}
