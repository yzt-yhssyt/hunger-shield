package com.hungershield.client;

import com.hungershield.HungerShieldConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public final class HungerHudRenderer {
    private static final int BAR_WIDTH = 81;
    private static final int BAR_HEIGHT = 9;
    private static final int BAR_BACKGROUND = 0xFF101010;
    private static final int HUNGER_BORDER = 0xFF2B2B2B;
    private static final int SATURATION_BORDER = 0xFF6B4E00;
    private static final int SATURATION_FILL = 0xFFFFEB3B;
    private static final int TEXT_COLOR = 0xFFFFFFFF;

    private HungerHudRenderer() {
    }

    public static void render(DrawContext context, PlayerEntity player, int top, int right) {
        HungerManager hunger = player.getHungerManager();
        int food = MathHelper.clamp(hunger.getFoodLevel(), 0, HungerShieldConfig.MAX_FOOD);
        int saturation = MathHelper.clamp(
                Math.round(hunger.getSaturationLevel()),
                0,
                HungerShieldConfig.MAX_FOOD
        );

        int x = right - BAR_WIDTH;
        int saturationY = top - BAR_HEIGHT - 1;

        drawBar(
                context,
                x,
                saturationY,
                saturation / (float) HungerShieldConfig.MAX_FOOD,
                SATURATION_FILL,
                SATURATION_BORDER,
                saturation + "/" + HungerShieldConfig.MAX_FOOD
        );
        drawBar(
                context,
                x,
                top,
                food / (float) HungerShieldConfig.MAX_FOOD,
                hungerColor(food),
                HUNGER_BORDER,
                food + "/" + HungerShieldConfig.MAX_FOOD
        );
    }

    private static void drawBar(
            DrawContext context,
            int x,
            int y,
            float fill,
            int fillColor,
            int borderColor,
            String label
    ) {
        context.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, BAR_BACKGROUND);

        int innerWidth = BAR_WIDTH - 2;
        int filled = Math.round(innerWidth * MathHelper.clamp(fill, 0.0F, 1.0F));
        if (filled > 0) {
            context.fill(x + 1, y + 1, x + 1 + filled, y + BAR_HEIGHT - 1, fillColor);
        }

        context.drawBorder(x, y, BAR_WIDTH, BAR_HEIGHT, borderColor);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int textWidth = textRenderer.getWidth(label);
        context.drawTextWithShadow(
                textRenderer,
                label,
                x + (BAR_WIDTH - textWidth) / 2,
                y + 1,
                TEXT_COLOR
        );
    }

    private static int hungerColor(int food) {
        float percentage = food / (float) HungerShieldConfig.MAX_FOOD;
        if (percentage > 0.8F) {
            return 0xFF35D435;
        }
        if (percentage >= 0.5F) {
            return 0xFFFFD500;
        }
        return 0xFFFF3B30;
    }
}
