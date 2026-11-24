package com.hungershield.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Unique
    private int hungerShield$syncTimer;

    @Inject(method = "playerTick", at = @At("TAIL"))
    private void hungerShield$syncHunger(CallbackInfo ci) {
        if (++hungerShield$syncTimer >= 5) {
            hungerShield$syncTimer = 0;
            ((ServerPlayerEntity) (Object) this).markHealthDirty();
        }
    }
}
