package kira.potentwounds.client;

import kira.potentwounds.PotentWounds;
import kira.potentwounds.WoundSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class PotentWoundsClient implements ClientModInitializer {
	private static float clientRawWounds = 0.0f;
	private static float clientAppliedWounds = 0.0f;
	private static int clientDecayDelayTicks = 0;

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(WoundSyncPayload.TYPE, (payload, context) -> {
			clientRawWounds = payload.rawWounds();
			clientAppliedWounds = payload.appliedWounds();
			clientDecayDelayTicks = payload.decayDelayTicks();
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			clientRawWounds = 0.0f;
			clientAppliedWounds = 0.0f;
			clientDecayDelayTicks = 0;
		});

		HudElementRegistry.addLast(
				Identifier.fromNamespaceAndPath(PotentWounds.MOD_ID, "debug_wound_text"),
				(graphics, deltaTracker) -> {
					Minecraft minecraft = Minecraft.getInstance();

					if (minecraft.player == null || minecraft.options.hideGui) {
						return;
					}

					int x = 8;
					int y = 8;
					int lineHeight = 10;

					float delaySeconds = clientDecayDelayTicks / 20.0f;

					graphics.text(minecraft.font, "Raw Wounds: " + String.format("%.2f", clientRawWounds), x, y, 0xFFFFFFFF, true);
					graphics.text(minecraft.font, "Applied Wounds: " + String.format("%.1f", clientAppliedWounds), x, y + lineHeight, 0xFFFFFFFF, true);
					graphics.text(minecraft.font, "Decay Delay: " + String.format("%.1fs", delaySeconds), x, y + (lineHeight * 2), 0xFFFFFFFF, true);
				}
		);
	}
}