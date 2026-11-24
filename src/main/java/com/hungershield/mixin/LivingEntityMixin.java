package com.hungershield.mixin;

import com.hungershield.damage.HungerShieldHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void hungerShield$beforeDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!HungerShieldHandler.allowDamage((LivingEntity) (Object) this, source, amount)) {
            cir.setReturnValue(false);
        }
    }
}
