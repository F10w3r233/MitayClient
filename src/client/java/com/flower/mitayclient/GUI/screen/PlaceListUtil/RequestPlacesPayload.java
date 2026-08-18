package com.flower.mitayclient.GUI.screen.PlaceListUtil;


import com.flower.mitayclient.util.ModIdentifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record RequestPlacesPayload() implements CustomPacketPayload {
    public static final Type<RequestPlacesPayload> TYPE = new Type<>(ModIdentifier.get("request_places"));
    public static final StreamCodec<FriendlyByteBuf, RequestPlacesPayload> STREAM_CODEC = StreamCodec.ofMember(
            (payload, buf) -> {},
            buf -> new RequestPlacesPayload()
    );

    @Override
    public Type<RequestPlacesPayload> type() { return TYPE; }
}