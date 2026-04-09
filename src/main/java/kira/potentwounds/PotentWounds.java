package kira.potentwounds;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PotentWounds implements ModInitializer {
	public static final String MOD_ID = "potentwounds";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Potent Wounds mod loaded!");

		ModEffects.register();
		PayloadTypeRegistry.clientboundPlay().register(WoundSyncPayload.TYPE, WoundSyncPayload.CODEC);
		ModEvents.register();

		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			if (!alive) {
				WoundManager.clearWounds(newPlayer);
				ServerPlayNetworking.send(newPlayer, new WoundSyncPayload(0.0f, 0.0f, 0));
			}
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			var player = handler.player;
			float rawWounds = WoundManager.getWounds(player);
			float appliedWounds = WoundManager.getAppliedWounds(player);
			int decayDelayTicks = WoundManager.getDecayDelay(player);

			// DEBUG: join + persistence inspection
			// PotentWounds.LOGGER.info("Current player UUID on join: {}", player.getUUID());
			// PotentWounds.LOGGER.info("SavedData map on join: {}", WoundSavedData.get(player).debugEntries());

			// DEBUG: join sync verification
			// PotentWounds.LOGGER.info(
			//         "JOIN sync for {} -> raw: {}, applied: {}, delay: {}",
			//         player.getName().getString(),
			//         rawWounds,
			//         appliedWounds,
			//         decayDelayTicks
			// );

			ServerPlayNetworking.send(player, new WoundSyncPayload(rawWounds, appliedWounds, decayDelayTicks));
		});

		ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> {
			if (minecraftServer.getTickCount() % 20 != 0) {
				return;
			}

			for (ServerPlayer onlinePlayer : minecraftServer.getPlayerList().getPlayers()) {
				WoundDecayHandler.tickPlayer(onlinePlayer);
			}
		});
	}
}