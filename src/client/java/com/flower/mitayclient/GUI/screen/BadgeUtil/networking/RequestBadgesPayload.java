package com.flower.mitayclient.GUI.screen.BadgeUtil.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record RequestBadgesPayload() implements CustomPacketPayload {
    public static final Type<RequestBadgesPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("mitayclient", "request_badges"));
    public static final StreamCodec<FriendlyByteBuf, RequestBadgesPayload> STREAM_CODEC =
            StreamCodec.ofMember((payload, buf) -> {}, buf -> new RequestBadgesPayload());

    @Override
    public Type<RequestBadgesPayload> type() { return TYPE; }
}