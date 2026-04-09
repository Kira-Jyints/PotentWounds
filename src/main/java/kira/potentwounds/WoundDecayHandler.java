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

        if (rawWounds <= 0.0f) {
            return;
        }

        int decayDelay = WoundManager.getDecayDelay(player);
        if (decayDelay > 0) {
            int newDelay = Math.max(0, decayDelay - 20);
            WoundManager.setDecayDelay(player, newDelay);

            float appliedWounds = WoundManager.getAppliedWounds(player);
            ServerPlayNetworking.send(player, new WoundSyncPayload(rawWounds, appliedWounds, newDelay));
            return;
        }

        float decayAmount = BASE_WOUND_DECAY_PER_SECOND;

        if (player.hasEffect(ModEffects.RECUPERATION)) {
            int amplifier = Objects.requireNonNull(player.getEffect(ModEffects.RECUPERATION)).getAmplifier();
            decayAmount += BONUS_WOUND_DECAY_PER_SECOND * (amplifier + 1);
        }

        float newRawWounds = Math.max(0.0f, rawWounds - decayAmount);

        if (newRawWounds == rawWounds) {
            return;
        }

        WoundManager.setWounds(player, newRawWounds);

        float appliedWounds = WoundManager.getAppliedWounds(player);
        ServerPlayNetworking.send(player, new WoundSyncPayload(newRawWounds, appliedWounds, 0));

        // DEBUG: passive wound decay tick
        // PotentWounds.LOGGER.info(
        //         "Decay tick for {} -> raw: {}, applied: {}",
        //         player.getName().getString(),
        //         newRawWounds,
        //         appliedWounds
        // );
    }
}