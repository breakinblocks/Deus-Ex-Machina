package com.breakinblocks.deus_ex_machina.item;

import com.breakinblocks.deus_ex_machina.Config;
import com.breakinblocks.deus_ex_machina.data.DeusExBuffsHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import static com.breakinblocks.deus_ex_machina.registry.EffectRegistry.DEUS_EX_MACHINA_EFFECT;

public class AmbrosiaItem extends Item {
    public static final FoodProperties AMBROSIA = new FoodProperties.Builder()
            .alwaysEat()
            .build();

    public AmbrosiaItem(Properties properties) {
        super(properties.food(AMBROSIA));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide) {
            if (DeusExBuffsHelper.isEnabled(entity)) {
                entity.removeEffect(DEUS_EX_MACHINA_EFFECT.get());
            } else {
                entity.addEffect(new MobEffectInstance(DEUS_EX_MACHINA_EFFECT.get(), -1, 0, false, false, Config.showIcon));
            }
        }
        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            stack.shrink(1);
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }
            player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
        }
        return stack;
    }
    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }
}
