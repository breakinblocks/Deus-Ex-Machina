package com.breakinblocks.deus_ex_machina.registry;

import com.breakinblocks.deus_ex_machina.DeusExMachina;
import com.breakinblocks.deus_ex_machina.item.AmbrosiaItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DeusExMachina.MODID);

    public static final DeferredItem<AmbrosiaItem> AMBROSIA = ITEMS.register("ambrosia",
            () -> new AmbrosiaItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
            ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
