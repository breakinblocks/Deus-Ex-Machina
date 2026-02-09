package com.breakinblocks.deus_ex_machina.mixin;

import com.breakinblocks.deus_ex_machina.client.DeathScreenData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

        if (DeathScreenData.isResistanceEnabled()) {
            Component resistanceLine = Component.translatable("deus_ex_machina.death_screen.resistance", DeathScreenData.getResistanceGain())
                    .withStyle(style -> style.withColor(0x55FF55))
                    .append(Component.translatable("deus_ex_machina.death_screen.resistance.now", DeathScreenData.getNewResistance())
                            .withStyle(style -> style.withColor(0xAAAAAA)));
            graphics.drawCenteredString(this.font, resistanceLine, centerX, currentY, 0xFFFFFF);
            currentY += 12;
        }

        if (DeathScreenData.isAttackEnabled()) {
            Component attackLine = Component.translatable("deus_ex_machina.death_screen.attack", DeathScreenData.getAttackBoostGain())
                    .withStyle(style -> style.withColor(0xFF5555))
                    .append(Component.translatable("deus_ex_machina.death_screen.attack.now", DeathScreenData.getNewAttackBoost())
                            .withStyle(style -> style.withColor(0xAAAAAA)));
            graphics.drawCenteredString(this.font, attackLine, centerX, currentY, 0xFFFFFF);
        }
    }
}
