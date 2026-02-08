package com.breakinblocks.deus_ex_machina.registry;

import com.breakinblocks.deus_ex_machina.item.AmbrosiaItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "deus_ex_machina");

    public static final RegistryObject<Item> AMBROSIA = ITEMS.register("ambrosia",
            () -> new AmbrosiaItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
            ));
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
