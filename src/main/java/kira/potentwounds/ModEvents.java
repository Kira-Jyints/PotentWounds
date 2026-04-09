package kira.potentwounds;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public final class ModEvents {
    private static final float MIN_DAMAGE_FOR_WOUNDS = 6.0f;
    private static final float WOUND_RATIO = 0.167f;
    private static final int DECAY_DELAY_SECONDS = 8;
    private static final int DECAY_DELAY_TICKS = 20 * DECAY_DELAY_SECONDS;

    private ModEvents() {
    }

    public static void register() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
            if (!(entity instanceof ServerPlayer player)) {
                return;
            }

            // DEBUG: damage intake log
            // PotentWounds.LOGGER.info(
            //         "Player {} took {} damage (base: {}, blocked: {}, source: {})",
            //         player.getName().getString(),
            //         damageTaken,
            //         baseDamageTaken,
            //         blocked,
            //         source
            // );

            if (damageTaken < MIN_DAMAGE_FOR_WOUNDS) {
                return;
            }

            float woundGain = damageTaken * WOUND_RATIO;
            WoundManager.addWounds(player, woundGain);
            WoundManager.setDecayDelay(player, DECAY_DELAY_TICKS);

            float rawWounds = WoundManager.getWounds(player);
            float appliedWounds = WoundManager.getAppliedWounds(player);
            int decayDelayTicks = WoundManager.getDecayDelay(player);

            ServerPlayNetworking.send(player, new WoundSyncPayload(rawWounds, appliedWounds, decayDelayTicks));

            // DEBUG: wound calculation log
            // PotentWounds.LOGGER.info(
            //         "Player {} gained {} raw wounds. Total raw: {}. Applied wounds: {}",
            //         player.getName().getString(),
            //         woundGain,
            //         rawWounds,
            //         appliedWounds
            // );
        });
    }
}