package kira.potentwounds;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record WoundSyncPayload(float rawWounds, float appliedWounds, int decayDelayTicks) implements CustomPacketPayload {
    public static final Identifier WOUND_SYNC_PAYLOAD_ID =
            Identifier.fromNamespaceAndPath(PotentWounds.MOD_ID, "wound_sync");

    public static final CustomPacketPayload.Type<WoundSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(WOUND_SYNC_PAYLOAD_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, WoundSyncPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.FLOAT, WoundSyncPayload::rawWounds,
                    ByteBufCodecs.FLOAT, WoundSyncPayload::appliedWounds,
                    ByteBufCodecs.INT, WoundSyncPayload::decayDelayTicks,
                    WoundSyncPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}