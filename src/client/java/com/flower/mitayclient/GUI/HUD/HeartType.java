package com.flower.mitayclient.GUI.HUD;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

@Environment(EnvType.CLIENT)
public enum HeartType
{
    CONTAINER(
            Identifier.withDefaultNamespace("hud/heart/container"),
            Identifier.withDefaultNamespace("hud/heart/container_blinking"),
            Identifier.withDefaultNamespace("hud/heart/container"),
            Identifier.withDefaultNamespace("hud/heart/container_blinking"),
            Identifier.withDefaultNamespace("hud/heart/container_hardcore"),
            Identifier.withDefaultNamespace("hud/heart/container_hardcore_blinking"),
            Identifier.withDefaultNamespace("hud/heart/container_hardcore"),
            Identifier.withDefaultNamespace("hud/heart/container_hardcore_blinking")
    ),
    NORMAL(
            Identifier.withDefaultNamespace("hud/heart/full"),
            Identifier.withDefaultNamespace("hud/heart/full_blinking"),
            Identifier.withDefaultNamespace("hud/heart/half"),
            Identifier.withDefaultNamespace("hud/heart/half_blinking"),
            Identifier.withDefaultNamespace("hud/heart/hardcore_full"),
            Identifier.withDefaultNamespace("hud/heart/hardcore_full_blinking"),
            Identifier.withDefaultNamespace("hud/heart/hardcore_half"),
            Identifier.withDefaultNamespace("hud/heart/hardcore_half_blinking")
    ),
    POISONED(
            Identifier.withDefaultNamespace("hud/heart/poisoned_full"),
            Identifier.withDefaultNamespace("hud/heart/poisoned_full_blinking"),
            Identifier.withDefaultNamespace("hud/heart/poisoned_half"),
            Identifier.withDefaultNamespace("hud/heart/poisoned_half_blinking"),
            Identifier.withDefaultNamespace("hud/heart/poisoned_hardcore_full"),
            Identifier.withDefaultNamespace("hud/heart/poisoned_hardcore_full_blinking"),
            Identifier.withDefaultNamespace("hud/heart/poisoned_hardcore_half"),
            Identifier.withDefaultNamespace("hud/heart/poisoned_hardcore_half_blinking")
    ),
    WITHERED(
            Identifier.withDefaultNamespace("hud/heart/withered_full"),
            Identifier.withDefaultNamespace("hud/heart/withered_full_blinking"),
            Identifier.withDefaultNamespace("hud/heart/withered_half"),
            Identifier.withDefaultNamespace("hud/heart/withered_half_blinking"),
            Identifier.withDefaultNamespace("hud/heart/withered_hardcore_full"),
            Identifier.withDefaultNamespace("hud/heart/withered_hardcore_full_blinking"),
            Identifier.withDefaultNamespace("hud/heart/withered_hardcore_half"),
            Identifier.withDefaultNamespace("hud/heart/withered_hardcore_half_blinking")
    ),
    ABSORBING(
            Identifier.withDefaultNamespace("hud/heart/absorbing_full"),
            Identifier.withDefaultNamespace("hud/heart/absorbing_full_blinking"),
            Identifier.withDefaultNamespace("hud/heart/absorbing_half"),
            Identifier.withDefaultNamespace("hud/heart/absorbing_half_blinking"),
            Identifier.withDefaultNamespace("hud/heart/absorbing_hardcore_full"),
            Identifier.withDefaultNamespace("hud/heart/absorbing_hardcore_full_blinking"),
            Identifier.withDefaultNamespace("hud/heart/absorbing_hardcore_half"),
            Identifier.withDefaultNamespace("hud/heart/absorbing_hardcore_half_blinking")
    ),
    FROZEN(
            Identifier.withDefaultNamespace("hud/heart/frozen_full"),
            Identifier.withDefaultNamespace("hud/heart/frozen_full_blinking"),
            Identifier.withDefaultNamespace("hud/heart/frozen_half"),
            Identifier.withDefaultNamespace("hud/heart/frozen_half_blinking"),
            Identifier.withDefaultNamespace("hud/heart/frozen_hardcore_full"),
            Identifier.withDefaultNamespace("hud/heart/frozen_hardcore_full_blinking"),
            Identifier.withDefaultNamespace("hud/heart/frozen_hardcore_half"),
            Identifier.withDefaultNamespace("hud/heart/frozen_hardcore_half_blinking")
    );

    private final Identifier fullTexture;
    private final Identifier fullBlinkingTexture;
    private final Identifier halfTexture;
    private final Identifier halfBlinkingTexture;
    private final Identifier hardcoreFullTexture;
    private final Identifier hardcoreFullBlinkingTexture;
    private final Identifier hardcoreHalfTexture;
    private final Identifier hardcoreHalfBlinkingTexture;

    private HeartType(
            final Identifier fullTexture,
            final Identifier fullBlinkingTexture,
            final Identifier halfTexture,
            final Identifier halfBlinkingTexture,
            final Identifier hardcoreFullTexture,
            final Identifier hardcoreFullBlinkingTexture,
            final Identifier hardcoreHalfTexture,
            final Identifier hardcoreHalfBlinkingTexture
    ) {
        this.fullTexture = fullTexture;
        this.fullBlinkingTexture = fullBlinkingTexture;
        this.halfTexture = halfTexture;
        this.halfBlinkingTexture = halfBlinkingTexture;
        this.hardcoreFullTexture = hardcoreFullTexture;
        this.hardcoreFullBlinkingTexture = hardcoreFullBlinkingTexture;
        this.hardcoreHalfTexture = hardcoreHalfTexture;
        this.hardcoreHalfBlinkingTexture = hardcoreHalfBlinkingTexture;
    }

    public Identifier getTexture(boolean hardcore, boolean half, boolean blinking) {
        if (!hardcore) {
            if (half) {
                return blinking ? this.halfBlinkingTexture : this.halfTexture;
            } else {
                return blinking ? this.fullBlinkingTexture : this.fullTexture;
            }
        } else if (half) {
            return blinking ? this.hardcoreHalfBlinkingTexture : this.hardcoreHalfTexture;
        } else {
            return blinking ? this.hardcoreFullBlinkingTexture : this.hardcoreFullTexture;
        }
    }

    public static HeartType fromPlayerState(Player player) {
        HeartType heartType;
        if (player.hasEffect(MobEffects.POISON)) {
            heartType = POISONED;
        } else if (player.hasEffect(MobEffects.WITHER)) {
            heartType = WITHERED;
        } else if (player.isFullyFrozen()) {
            heartType = FROZEN;
        } else {
            heartType = NORMAL;
        }

        return heartType;
    }
}
