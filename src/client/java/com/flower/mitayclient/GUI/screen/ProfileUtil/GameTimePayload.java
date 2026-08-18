package com.flower.mitayclient.GUI.screen.ProfileUtil;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record GameTimePayload(int gameTime) implements CustomPacketPayload {

    // 通道 ID，必须与 Bukkit 插件保持一致
    public static final Type<GameTimePayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("mitayclient", "gametime"));

    // 编解码器：告诉游戏如何把这个 Payload 写入/读出网络包
    public static final StreamCodec<FriendlyByteBuf, GameTimePayload> STREAM_CODEC =
            StreamCodec.ofMember(
                    // 编码：将 payload 写入 buf
                    (payload, buf) -> buf.writeInt(payload.gameTime),
                    // 解码：从 buf 读出数据并构造 payload
                    buf -> new GameTimePayload(buf.readInt())
            );

    @Override
    public Type<GameTimePayload> type() {
        return TYPE;
    }
}