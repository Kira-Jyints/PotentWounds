package kira.potentwounds;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class WoundFoodHandler {
    private static final int RECUPERATION_DURATION_TICKS = 20 * 120; // 2 minutes
    private static final int WEAK_RECUPERATION_DURATION_TICKS = 20 * 60; // 1 minute
    private static final int SOOTHED_DURATION_TICKS = 20 * 90; // 90 seconds
    private static final int SOOTHED_LITE_DURATION_TICKS = 20 * 30; // 30 seconds
    public static boolean isWoundFood(Item item) {
        return item == Items.HONEY_BOTTLE
                || item == Items.DRIED_KELP
                || item == Items.BAKED_POTATO
                || item == Items.PUMPKIN_PIE
                || item == Items.GOLDEN_APPLE
                || item == Items.ENCHANTED_GOLDEN_APPLE
                || item == Items.GOLDEN_CARROT;
    }

    private WoundFoodHandler() {
    }

    public static void handleConsumedFood(ServerPlayer player, Item item) {
        // DEBUG: confirm handler receives consumed item
        // PotentWounds.LOGGER.info("Handling consumed item for {}: {}", player.getName().getString(), item);

        if (item == Items.HONEY_BOTTLE) {
            player.addEffect(new MobEffectInstance(
                    ModEffects.SOOTHED,
                    SOOTHED_DURATION_TICKS,
                    1
            ));

            float rawWounds = WoundManager.getWounds(player);
            float appliedWounds = WoundManager.getAppliedWounds(player);
            int decayDelayTicks = WoundManager.getDecayDelay(player);
            ServerPlayNetworking.send(player, new WoundSyncPayload(rawWounds, appliedWounds, decayDelayTicks));

            // DEBUG: soothed trigger
            // PotentWounds.LOGGER.info("Soothed triggered by honey bottle for {}", player.getName().getString());
            return;
        }
        if (item == Items.DRIED_KELP) {
            player.addEffect(new MobEffectInstance(
                    ModEffects.SOOTHED,
                    SOOTHED_LITE_DURATION_TICKS,
                    0
            ));

            float rawWounds = WoundManager.getWounds(player);
            float appliedWounds = WoundManager.getAppliedWounds(player);
            int decayDelayTicks = WoundManager.getDecayDelay(player);
            ServerPlayNetworking.send(player, new WoundSyncPayload(rawWounds, appliedWounds, decayDelayTicks));

            // DEBUG: soothed lite trigger
            // PotentWounds.LOGGER.info("Soothed Lite triggered by dried kelp for {}", player.getName().getString());
            return;
        }

        if (item == Items.GOLDEN_APPLE
                || item == Items.ENCHANTED_GOLDEN_APPLE
                || item == Items.GOLDEN_CARROT) {
//            PotentWounds.LOGGER.info(
//                    "GOLDEN CLEAR start -> player: {}, raw before: {}, applied before: {}, delay before: {}",
//                    player.getName().getString(),
//                    WoundManager.getWounds(player),
//                    WoundManager.getAppliedWounds(player),
//                    WoundManager.getDecayDelay(player)
//            );

            WoundManager.clearWounds(player);

//            PotentWounds.LOGGER.info(
//                    "GOLDEN CLEAR after clear -> player: {}, raw after: {}, applied after: {}, delay after: {}",
//                    player.getName().getString(),
//                    WoundManager.getWounds(player),
//                    WoundManager.getAppliedWounds(player),
//                    WoundManager.getDecayDelay(player)
//            );

            ServerPlayNetworking.send(player, new WoundSyncPayload(0.0f, 0.0f, 0));

//            PotentWounds.LOGGER.info(
//                    "GOLDEN CLEAR packet sent -> player: {}, raw: 0.0, applied: 0.0, delay: 0",
//                    player.getName().getString()
//            );

            return;
        }

        if (item == Items.BAKED_POTATO) {
            // DEBUG: weak recuperation trigger
            // PotentWounds.LOGGER.info("Weak recuperation triggered by baked potato");

            player.addEffect(new MobEffectInstance(
                    ModEffects.RECUPERATION,
                    WEAK_RECUPERATION_DURATION_TICKS,
                    0
            ));
            return;
        }

        if (item == Items.PUMPKIN_PIE) {
            // DEBUG: strong recuperation trigger
            // PotentWounds.LOGGER.info("Strong recuperation triggered by pumpkin pie");

            player.addEffect(new MobEffectInstance(
                    ModEffects.RECUPERATION,
                    RECUPERATION_DURATION_TICKS,
                    1
            ));
        }
    }
}