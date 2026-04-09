package kira.potentwounds;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WoundSavedData extends SavedData {
    private static final Codec<Map<UUID, Float>> WOUNDS_MAP_CODEC = Codec.unboundedMap(
            Codec.STRING.xmap(UUID::fromString, UUID::toString),
            Codec.FLOAT
    );

    private static final Codec<Map<UUID, Integer>> DELAY_MAP_CODEC = Codec.unboundedMap(
            Codec.STRING.xmap(UUID::fromString, UUID::toString),
            Codec.INT
    );

    private static final Codec<WoundSavedData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    WOUNDS_MAP_CODEC.fieldOf("wounds").forGetter(data -> data.woundsByPlayer),
                    DELAY_MAP_CODEC.fieldOf("decay_delay").forGetter(data -> data.decayDelayByPlayer)
            ).apply(instance, WoundSavedData::new)
    );

    private static final SavedDataType<WoundSavedData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(PotentWounds.MOD_ID, "wounds"),
            WoundSavedData::new,
            CODEC,
            null
    );

    private final Map<UUID, Float> woundsByPlayer;
    private final Map<UUID, Integer> decayDelayByPlayer;

    public WoundSavedData() {
        this.woundsByPlayer = new HashMap<>();
        this.decayDelayByPlayer = new HashMap<>();
    }

    public WoundSavedData(Map<UUID, Float> woundsByPlayer, Map<UUID, Integer> decayDelayByPlayer) {
        this.woundsByPlayer = new HashMap<>(woundsByPlayer);
        this.decayDelayByPlayer = new HashMap<>(decayDelayByPlayer);
    }

    public static WoundSavedData get(MinecraftServer server) {
        ServerLevel level = server.getLevel(ServerLevel.OVERWORLD);

        if (level == null) {
            return new WoundSavedData();
        }

        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public static WoundSavedData get(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();

        if (server == null) {
            return new WoundSavedData();
        }

        return get(server);
    }

    public float getWounds(ServerPlayer player) {
        return woundsByPlayer.getOrDefault(player.getUUID(), 0.0f);
    }

    public void setWounds(ServerPlayer player, float amount) {
        float clamped = Math.max(0.0f, amount);

        if (clamped <= 0.0f) {
            woundsByPlayer.remove(player.getUUID());
        } else {
            woundsByPlayer.put(player.getUUID(), clamped);
        }

        setDirty();
        // DEBUG: data load verification
        // PotentWounds.LOGGER.info("Loaded WoundSavedData entries: {}", this.woundsByPlayer);
    }

    public void clearWounds(ServerPlayer player) {
        if (woundsByPlayer.remove(player.getUUID()) != null) {
            setDirty();
            // DEBUG: clear-on-death
            // PotentWounds.LOGGER.info("Cleared wounds for {}", player.getName().getString());
        }
    }
    public Map<UUID, Float> debugEntries() {
        return woundsByPlayer;
    }
    public int getDecayDelay(ServerPlayer player) {
        return decayDelayByPlayer.getOrDefault(player.getUUID(), 0);
    }

    public void setDecayDelay(ServerPlayer player, int ticks) {
        int clamped = Math.max(0, ticks);

        if (clamped <= 0) {
            decayDelayByPlayer.remove(player.getUUID());
        } else {
            decayDelayByPlayer.put(player.getUUID(), clamped);
        }

        setDirty();
    }

    public void clearDecayDelay(ServerPlayer player) {
        if (decayDelayByPlayer.remove(player.getUUID()) != null) {
            setDirty();
        }
    }
}