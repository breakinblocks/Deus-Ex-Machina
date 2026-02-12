package com.breakinblocks.deus_ex_machina.mixin;

import com.breakinblocks.deus_ex_machina.api.buff.BuffType;
import com.breakinblocks.deus_ex_machina.api.registry.BuffRegistry;
import com.breakinblocks.deus_ex_machina.client.DeathScreenData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen {

    protected DeathScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderBuffGains(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!DeathScreenData.hasData()) return;

        int centerX = this.width / 2;
        int currentY = 10;

        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(DeathScreenData.getKillerType());
        Component mobName = entityType.getDescription();

        Component header = Component.translatable("deus_ex_machina.death_screen.header", mobName)
                .withStyle(style -> style.withColor(0xFFAA00));

        graphics.drawCenteredString(this.font, header, centerX, currentY, 0xFFFFFF);
        currentY += 12;

        // Iterate over all buff changes
        for (Map.Entry<ResourceLocation, int[]> entry : DeathScreenData.getBuffChanges().entrySet()) {
            ResourceLocation buffId = entry.getKey();
            int gain = entry.getValue()[0];
            int newValue = entry.getValue()[1];

            // Get buff type info from registry
            BuffType buffType = BuffRegistry.get(buffId).orElse(null);
            Component buffName;
            int color;

            if (buffType != null) {
                buffName = buffType.getDisplayName();
                color = buffType.getColor();
            } else {
                // Fallback for unknown buff types
                buffName = Component.literal(buffId.toString());
                color = 0xFFFFFFFF;
            }

            Component buffLine = Component.literal("  ")
                    .append(buffName)
                    .append(": +")
                    .append(String.valueOf(gain))
                    .append("% ")
                    .withStyle(style -> style.withColor(color))
                    .append(Component.literal("(now " + newValue + "%)")
                            .withStyle(style -> style.withColor(0xAAAAAA)));

            graphics.drawCenteredString(this.font, buffLine, centerX, currentY, 0xFFFFFF);
            currentY += 12;
        }
    }
}
