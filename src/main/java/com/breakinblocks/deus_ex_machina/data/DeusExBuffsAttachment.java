package com.breakinblocks.deus_ex_machina.data;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class DeusExBuffsAttachment {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, DeusExMachina.MODID);

    public static final Supplier<AttachmentType<DeusExBuffs>> DEUS_EX_BUFFS =
            ATTACHMENT_TYPES.register("deus_ex_buffs", () ->
                    AttachmentType.serializable(DeusExBuffs::new).build()
            );
}
