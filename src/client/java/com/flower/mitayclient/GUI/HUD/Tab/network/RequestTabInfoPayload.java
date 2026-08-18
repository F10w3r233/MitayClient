package com.flower.mitayclient.GUI.HUD.Tab.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record RequestTabInfoPayload() implements CustomPacketPayload
{
    public static final Type<RequestTabInfoPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("mitayclient", "request_tab_info"));
    public static final StreamCodec<FriendlyByteBuf, RequestTabInfoPayload> STREAM_CODEC =
            StreamCodec.ofMember((payload, buf) -> {}, buf -> new RequestTabInfoPayload());

    @Override
    public Type<RequestTabInfoPayload> type() { return TYPE; }
}