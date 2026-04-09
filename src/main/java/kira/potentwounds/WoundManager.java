package kira.potentwounds;

import net.minecraft.server.level.ServerPlayer;

public final class WoundManager {
    private static final float SOOTHED_RELIEF_AMOUNT = 2.0f;

    private WoundManager() {
    }

    public static float ceilToHeart(float value) {
        return (float) Math.ceil(value / 2.0f) * 2.0f;
    }

    public static float getWounds(ServerPlayer player) {
        return WoundSavedData.get(player).getWounds(player);
    }

    public static void setWounds(ServerPlayer player, float amount) {
        WoundSavedData.get(player).setWounds(player, Math.max(0.0f, amount));
    }

    public static void addWounds(ServerPlayer player, float amount) {
        float current = getWounds(player);
        setWounds(player, current + amount);
    }

    public static void clearWounds(ServerPlayer player) {
        WoundSavedData.get(player).clearWounds(player);
        WoundSavedData.get(player).clearDecayDelay(player);
    }

    public static float getAppliedWounds(float rawWounds) {
        return ceilToHeart(rawWounds);
    }

    public static float getAppliedWounds(ServerPlayer player) {
        float rawWounds = getWounds(player);
        float appliedWounds = getAppliedWounds(rawWounds);

        if (player.hasEffect(ModEffects.SOOTHED)) {
            int amplifier = player.getEffect(ModEffects.SOOTHED).getAmplifier();

            float soothedReliefAmount = switch (amplifier) {
                case 0 -> 2.0f; // one heart
                case 1 -> 4.0f; // two hearts
                default -> 2.0f;
            };

            appliedWounds = Math.max(0.0f, appliedWounds - soothedReliefAmount);
        }

        return appliedWounds;
    }

    public static float getNaturalRegenCap(ServerPlayer player) {
        float appliedWounds = getAppliedWounds(player);
        float maxHealth = player.getMaxHealth();

        return Math.max(1.0f, maxHealth - appliedWounds);
    }

    public static int getDecayDelay(ServerPlayer player) {
        return WoundSavedData.get(player).getDecayDelay(player);
    }

    public static void setDecayDelay(ServerPlayer player, int ticks) {
        WoundSavedData.get(player).setDecayDelay(player, ticks);
    }

    public static void clearDecayDelay(ServerPlayer player) {
        WoundSavedData.get(player).clearDecayDelay(player);
    }
}