package kira.potentwounds;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

public final class ModEffects {
    public static final Holder<MobEffect> RECUPERATION =
            Registry.registerForHolder(
                    BuiltInRegistries.MOB_EFFECT,
                    Identifier.fromNamespaceAndPath(PotentWounds.MOD_ID, "recuperation"),
                    new RecuperationEffect()
            );

    public static final Holder<MobEffect> SOOTHED =
            Registry.registerForHolder(
                    BuiltInRegistries.MOB_EFFECT,
                    Identifier.fromNamespaceAndPath(PotentWounds.MOD_ID, "soothed"),
                    new SoothedEffect()
            );

    private ModEffects() {
    }

    public static void register() {
        // Intentionally empty.
        // Calling this method ensures the class loads and static fields initialize.
    }
}