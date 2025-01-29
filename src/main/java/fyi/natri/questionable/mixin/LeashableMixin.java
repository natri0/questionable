package fyi.natri.questionable.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.Leashable;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Leashable.class)
public interface LeashableMixin {

    // todo: will this work when multiple players should see the leash? only tested with attacher & attachee

    @Inject(method = "attachLeash(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkManager;sendToOtherNearbyPlayers(Lnet/minecraft/entity/Entity;Lnet/minecraft/network/packet/Packet;)V", shift = At.Shift.AFTER))
    private static <E extends Entity & Leashable> void sendPacketToPlayerOnAttachLeash(E entity, Entity leashHolder, boolean sendPacket, CallbackInfo ci) {
        if (entity instanceof ServerPlayerEntity plr) {
            plr.networkHandler.sendPacket(new EntityAttachS2CPacket(entity, leashHolder));
        }
    }

    @Inject(method = "detachLeash(Lnet/minecraft/entity/Entity;ZZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkManager;sendToOtherNearbyPlayers(Lnet/minecraft/entity/Entity;Lnet/minecraft/network/packet/Packet;)V", shift = At.Shift.AFTER))
    private static <E extends Entity & Leashable> void sendPacketToPlayerOnDetachLeash(E entity, boolean sendPacket, boolean dropItem, CallbackInfo ci) {
        if (entity instanceof ServerPlayerEntity plr) {
            plr.networkHandler.sendPacket(new EntityAttachS2CPacket(entity, null));
        }
    }
}
