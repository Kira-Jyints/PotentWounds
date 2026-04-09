package kira.potentwounds;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public final class WoundDecayHandler {
    private static final float BASE_WOUND_DECAY_PER_SECOND = 0.025f;
    private static final float BONUS_WOUND_DECAY_PER_SECOND = 0.10f;

    private WoundDecayHandler() {
    }

    public static void tickPlayer(ServerPlayer player) {
        float rawWounds = WoundManager.getWounds(player);
        int decayDelay = WoundManager.getDecayDelay(player);

//        PotentWounds.LOGGER.info(
//                "DECAY tick start -> player: {}, raw: {}, applied: {}, delay: {}",
//                player.getName().getString(),
//                rawWounds,
//                WoundManager.getAppliedWounds(player),
//                decayDelay
//        );

        if (rawWounds <= 0.0f) {
//            PotentWounds.LOGGER.info("DECAY early return (no wounds) -> {}", player.getName().getString());
            return;
        }

        if (decayDelay > 0) {
            int newDelay = Math.max(0, decayDelay - 20);
            WoundManager.setDecayDelay(player, newDelay);

            float currentRawWounds = WoundManager.getWounds(player);
            float appliedWounds = WoundManager.getAppliedWounds(player);

//            PotentWounds.LOGGER.info(
//                    "DECAY delay branch -> player: {}, raw: {}, applied: {}, old delay: {}, new delay: {}",
//                    player.getName().getString(),
//                    currentRawWounds,
//                    appliedWounds,
//                    decayDelay,
//                    newDelay
//            );

            ServerPlayNetworking.send(player, new WoundSyncPayload(currentRawWounds, appliedWounds, newDelay));
            return;
        }

        rawWounds = WoundManager.getWounds(player);
        if (rawWounds <= 0.0f) {
//            PotentWounds.LOGGER.info("DECAY re-read return (cleared before decay) -> {}", player.getName().getString());
            return;
        }

        float decayAmount = BASE_WOUND_DECAY_PER_SECOND;

        if (player.hasEffect(ModEffects.RECUPERATION)) {
            int amplifier = Objects.requireNonNull(player.getEffect(ModEffects.RECUPERATION)).getAmplifier();
            decayAmount += BONUS_WOUND_DECAY_PER_SECOND * (amplifier + 1);
        }

        float newRawWounds = Math.max(0.0f, rawWounds - decayAmount);

        if (newRawWounds == rawWounds) {
//            PotentWounds.LOGGER.info("DECAY no-op -> {}", player.getName().getString());
            return;
        }

        WoundManager.setWounds(player, newRawWounds);

        float currentRawWounds = WoundManager.getWounds(player);
        float appliedWounds = WoundManager.getAppliedWounds(player);
        int currentDelay = WoundManager.getDecayDelay(player);

//        PotentWounds.LOGGER.info(
//                "DECAY send -> player: {}, new raw local: {}, saved raw: {}, applied: {}, delay: {}",
//                player.getName().getString(),
//                newRawWounds,
//                currentRawWounds,
//                appliedWounds,
//                currentDelay
//        );

        ServerPlayNetworking.send(player, new WoundSyncPayload(currentRawWounds, appliedWounds, currentDelay));
    }
}