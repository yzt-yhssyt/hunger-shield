package com.hungershield.damage;

import com.hungershield.HungerShieldConfig;
import com.hungershield.mixin.LivingEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

/**
 * Low-health damage absorption.
 *
 * <p>When health is at or below 50% of max, or when a hit would be lethal,
 * incoming damage is paid from saturation + hunger at 3 shield points per
 * point of damage. Health is not touched while the pool has enough points.
 * If the pool runs dry, the leftover is converted back to real damage at one
 * third rate.</p>
 */
public final class HungerShieldHandler {
    private HungerShieldHandler() {
    }

    public static boolean allowDamage(LivingEntity entity, DamageSource source, float amount) {
        if (!(entity instanceof ServerPlayerEntity player)) {
            return true;
        }
        if (amount <= 0.0F || player.isDead() || player.isSpectator()) {
            return true;
        }
        // The mixin injects at the very start of LivingEntity.damage(), before
        // vanilla's own invulnerability checks.
        if (player.isInvulnerableTo(source)) {
            return true;
        }
        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return true;
        }
        // Let vanilla absorption finish its job first.
        if (player.getAbsorptionAmount() >= amount) {
            return true;
        }
        // A raised vanilla shield takes priority over the hunger shield.
        if (player.blockedByShield(source)) {
            return true;
        }

        HungerManager hunger = player.getHungerManager();
        float pool = hunger.getSaturationLevel() + hunger.getFoodLevel();
        if (pool <= 0.0F) {
            return true;
        }

        LivingEntityAccessor accessor = (LivingEntityAccessor) player;
        float effective = amount;
        if (player.timeUntilRegen > 10) {
            float last = accessor.hungerShield$getLastDamageTaken();
            if (amount <= last) {
                // Vanilla would ignore this hit entirely.
                return true;
            }
            effective = amount - last;
        }
        if (effective <= 0.0F) {
            return true;
        }

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        boolean alreadyLow = health <= maxHealth * HungerShieldConfig.LOW_HEALTH_FRACTION;
        boolean wouldKill = effective >= health;
        if (!alreadyLow && !wouldKill) {
            return true;
        }

        float need = effective * HungerShieldConfig.SHIELD_DAMAGE_MULTIPLIER;
        float absorbed = Math.min(pool, need);
        float remaining = pool - absorbed;
        float overflow = need - absorbed;

        if (remaining <= 0.0F) {
            hunger.setSaturationLevel(0.0F);
            hunger.setFoodLevel(0);
        } else if (remaining <= hunger.getSaturationLevel()) {
            hunger.setSaturationLevel(remaining);
        } else {
            float foodRemaining = remaining - hunger.getSaturationLevel();
            hunger.setSaturationLevel(0.0F);
            hunger.setFoodLevel(MathHelper.clamp(
                    (int) Math.ceil(foodRemaining),
                    0,
                    HungerShieldConfig.MAX_FOOD
            ));
        }

        if (overflow > 0.0F) {
            // The pool ran dry. The recursive call sees an empty pool and is
            // allowed through, so vanilla handles armor, totems and death.
            player.damage(source, overflow / HungerShieldConfig.SHIELD_DAMAGE_MULTIPLIER);
            return false;
        }

        player.timeUntilRegen = 20;
        accessor.hungerShield$setLastDamageTaken(amount);
        player.maxHurtTime = 10;
        player.hurtTime = 10;
        player.getWorld().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ITEM_SHIELD_BLOCK,
                SoundCategory.PLAYERS,
                1.0F,
                0.8F + player.getRandom().nextFloat() * 0.4F
        );
        return false;
    }
}
