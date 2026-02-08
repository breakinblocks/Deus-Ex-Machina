package com.breakinblocks.deus_ex_machina.registry;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.effect.DeusExMachinaEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EffectRegistry {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, DeusExMachina.MODID);

    public static final RegistryObject<MobEffect> DEUS_EX_MACHINA_EFFECT =
            MOB_EFFECTS.register("deus_ex_machina_effect", () -> new DeusExMachinaEffect(MobEffectCategory.BENEFICIAL, 0xFFFFFF));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
