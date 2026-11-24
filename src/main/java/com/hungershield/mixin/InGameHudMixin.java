package com.hungershield.mixin;

import com.hungershield.client.HungerHudRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InGameHud.class, priority = 2000)
public class InGameHudMixin {
    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void hungerShield$renderFood(
            DrawContext context,
            PlayerEntity player,
            int top,
            int right,
            CallbackInfo ci
    ) {
        HungerHudRenderer.render(context, player, top, right);
        ci.cancel();
    }
}
