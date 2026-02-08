package com.breakinblocks.deus_ex_machina.registry;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.effect.DeusExMachinaEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EffectRegistry {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, DeusExMachina.MODID);

    public static final Holder<MobEffect> DEUS_EX_MACHINA_EFFECT =
            MOB_EFFECTS.register("deus_ex_machina_effect",
                    () -> new DeusExMachinaEffect(MobEffectCategory.BENEFICIAL, 0xFFFFFF));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
