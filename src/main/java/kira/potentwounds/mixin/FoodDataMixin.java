package kira.potentwounds.mixin;

import kira.potentwounds.WoundManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FoodData.class)
public class FoodDataMixin {

	@Redirect(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerPlayer;heal(F)V"
			)
	)
	private void potentwounds$capNaturalRegen(ServerPlayer player, float amount) {
		float currentHealth = player.getHealth();
		float regenCap = WoundManager.getNaturalRegenCap(player);

		float allowedHealing = regenCap - currentHealth;
		if (allowedHealing <= 0.0f) {
			return;
		}

		float actualHealing = Math.min(amount, allowedHealing);
		player.heal(actualHealing);
	}
}