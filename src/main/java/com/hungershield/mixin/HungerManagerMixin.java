package com.hungershield.mixin;

import com.hungershield.HungerShieldConfig;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {
    @Shadow
    private int foodLevel;

    @Shadow
    private float saturationLevel;

    @Shadow
    private float exhaustion;

    @Shadow
    private int foodTickTimer;

    @Shadow
    private int prevFoodLevel;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void hungerShield$init(CallbackInfo ci) {
        this.foodLevel = HungerShieldConfig.MAX_FOOD;
        this.prevFoodLevel = HungerShieldConfig.MAX_FOOD;
        this.saturationLevel = 250.0F;
    }

    @Inject(method = "addInternal", at = @At("HEAD"), cancellable = true)
    private void hungerShield$addInternal(int food, float saturation, CallbackInfo ci) {
        this.foodLevel = MathHelper.clamp(this.foodLevel + food, 0, HungerShieldConfig.MAX_FOOD);
        this.saturationLevel = MathHelper.clamp(
                this.saturationLevel + saturation,
                0.0F,
                HungerShieldConfig.MAX_SATURATION
        );
        ci.cancel();
    }

    @Inject(method = "addExhaustion", at = @At("HEAD"), cancellable = true)
    private void hungerShield$addExhaustion(float amount, CallbackInfo ci) {
        this.exhaustion = MathHelper.clamp(
                this.exhaustion + amount * HungerShieldConfig.EXHAUSTION_MULTIPLIER,
                0.0F,
                HungerShieldConfig.MAX_EXHAUSTION
        );
        ci.cancel();
    }

    @Inject(method = "isNotFull", at = @At("HEAD"), cancellable = true)
    private void hungerShield$isNotFull(CallbackInfoReturnable<Boolean> cir) {
        // Saturation is an independent bar now, so food must remain edible
        // while saturation is missing even if hunger itself is full.
        cir.setReturnValue(this.foodLevel < HungerShieldConfig.MAX_FOOD
                || this.saturationLevel < HungerShieldConfig.MAX_SATURATION);
    }

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void hungerShield$update(PlayerEntity player, CallbackInfo ci) {
        this.prevFoodLevel = this.foodLevel;

        if (this.exhaustion > 4.0F) {
            this.exhaustion -= 4.0F;
            if (this.saturationLevel > 0.0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
            } else if (player.getWorld().getDifficulty() != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        if (player.getWorld().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION)) {
            if (this.saturationLevel > 0.0F
                    && player.canFoodHeal()
                    && this.foodLevel > HungerShieldConfig.FAST_REGEN_THRESHOLD) {
                ++this.foodTickTimer;
                if (this.foodTickTimer >= 10) {
                    float amount = Math.min(this.saturationLevel, 6.0F);
                    player.heal(amount / 6.0F);
                    ((HungerManager) (Object) this).addExhaustion(amount);
                    this.foodTickTimer = 0;
                }
            } else if (player.canFoodHeal() && this.foodLevel >= HungerShieldConfig.SLOW_REGEN_THRESHOLD) {
                ++this.foodTickTimer;
                if (this.foodTickTimer >= 80) {
                    player.heal(1.0F);
                    ((HungerManager) (Object) this).addExhaustion(6.0F);
                    this.foodTickTimer = 0;
                }
            } else if (this.foodLevel <= 0) {
                ++this.foodTickTimer;
                if (this.foodTickTimer >= 80) {
                    if (player.getHealth() > 10.0F
                            || player.getWorld().getDifficulty() == Difficulty.HARD
                            || (player.getHealth() > 1.0F && player.getWorld().getDifficulty() == Difficulty.NORMAL)) {
                        player.damage(player.getDamageSources().starve(), 1.0F);
                    }
                    this.foodTickTimer = 0;
                }
            } else {
                this.foodTickTimer = 0;
            }
        }

        ci.cancel();
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void hungerShield$readNbt(NbtCompound nbt, CallbackInfo ci) {
        if (!nbt.getBoolean("HungerShieldMigrated")) {
            // Any vanilla-scale save has food <= 20. Do not require a low
            // saturation too: modded food could have raised it above 5.
            if (this.foodLevel <= 20) {
                this.foodLevel = MathHelper.clamp(
                        this.foodLevel * 50,
                        0,
                        HungerShieldConfig.MAX_FOOD
                );
                this.saturationLevel = MathHelper.clamp(
                        this.saturationLevel * 50.0F,
                        0.0F,
                        HungerShieldConfig.MAX_SATURATION
                );
            }
            this.prevFoodLevel = this.foodLevel;
        }
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void hungerShield$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("HungerShieldMigrated", true);
    }
}
