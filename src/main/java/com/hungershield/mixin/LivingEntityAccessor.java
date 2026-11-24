package com.hungershield.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("lastDamageTaken")
    float hungerShield$getLastDamageTaken();

    @Accessor("lastDamageTaken")
    void hungerShield$setLastDamageTaken(float value);
}
