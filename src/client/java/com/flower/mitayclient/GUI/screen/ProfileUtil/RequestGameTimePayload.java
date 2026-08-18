package com.flower.mitayclient.GUI.screen.ProfileUtil;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record RequestGameTimePayload() implements CustomPacketPayload
{

    public static final Type<RequestGameTimePayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("mitayclient", "request_gametime"));

    // 空包，encode 什么都不写，decode 直接创建
    public static final StreamCodec<FriendlyByteBuf, RequestGameTimePayload> STREAM_CODEC =
            StreamCodec.ofMember(
                    (payload, buf) -> {},
                    buf -> new RequestGameTimePayload()
            );

    @Override
    public Type<RequestGameTimePayload> type() {
        return TYPE;
    }
}
