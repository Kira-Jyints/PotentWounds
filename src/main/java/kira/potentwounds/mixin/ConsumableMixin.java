package kira.potentwounds.mixin;

import kira.potentwounds.WoundFoodHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Consumable.class)
public class ConsumableMixin {
    @Inject(method = "onConsume", at = @At("HEAD"))
    private void potentwounds$handleFoodEffects(Level level, LivingEntity user, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (level.isClientSide()) {
            return;
        }

        if (!(user instanceof ServerPlayer player)) {
            return;
        }

        Item item = stack.getItem();

        // DEBUG: confirm onConsume mixin fires
        //PotentWounds.LOGGER.info(
        //        "Consumable hook fired for player {} with item {}",
        //        player.getName().getString(),
        //        item
        //);

        WoundFoodHandler.handleConsumedFood(player, item);
    }
}