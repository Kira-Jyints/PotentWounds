package kira.potentwounds.mixin;

import kira.potentwounds.WoundFoodHandler;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemUseMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void potentwounds$allowEatingWhenFull(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {

        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();

        // Only apply on server
        if (level.isClientSide()) {
            return;
        }

        // If player is full
        if (player.getFoodData().getFoodLevel() >=20
                && WoundFoodHandler.isWoundFood(item)) {

            player.startUsingItem(hand);
                cir.setReturnValue(InteractionResult.CONSUME);
            }
        }
    }