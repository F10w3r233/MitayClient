package com.flower.mitayclient.GUI.screen.ProfileUtil.title;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record RequestTitlesPayload() implements CustomPacketPayload {

    public static final Type<RequestTitlesPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("mitayclient", "request_titles"));

    public static final StreamCodec<FriendlyByteBuf, RequestTitlesPayload> STREAM_CODEC =
            StreamCodec.ofMember(
                    (payload, buf) -> {},
                    buf -> new RequestTitlesPayload()
            );

    @Override
    public Type<RequestTitlesPayload> type() {
        return TYPE;
    }
}