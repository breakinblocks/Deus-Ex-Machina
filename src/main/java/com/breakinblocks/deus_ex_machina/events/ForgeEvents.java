package com.breakinblocks.deus_ex_machina.events;

import com.breakinblocks.deus_ex_machina.Config;
import com.breakinblocks.deus_ex_machina.command.DeusExCommand;
import com.breakinblocks.deus_ex_machina.data.DeusExBuffsProvider;
import com.breakinblocks.deus_ex_machina.data.DeusExMobData;
import com.breakinblocks.deus_ex_machina.data.DeusExMobDataProvider;
import com.breakinblocks.deus_ex_machina.data.IDeusExBuffs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.breakinblocks.deus_ex_machina.DeusExMachina.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {

    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(DeusExBuffsProvider.DEUS_EX_BUFFS).isPresent()) {
                event.addCapability(new ResourceLocation(MODID, "deus_ex_buffs"), new DeusExBuffsProvider());
            }
        }

        // Attach mob data capability to all living entities for instance mode tracking
        if (event.getObject() instanceof LivingEntity && !(event.getObject() instanceof Player)) {
            if (Config.isDeusExMob(event.getObject().getType())) {
                event.addCapability(new ResourceLocation(MODID, "deus_ex_mob_data"), new DeusExMobDataProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        DeusExCommand.register(event.getDispatcher());
    }

}
