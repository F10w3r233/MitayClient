package com.flower.mitayclient.GUI.screen.ProfileUtil.LeaderBoard;

import com.flower.mitayclient.util.ModIdentifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

// 客户端请求排行榜数据
public record RequestLeaderboardPayload() implements CustomPacketPayload
{
    public static final Type<RequestLeaderboardPayload> TYPE =
            new Type<>(ModIdentifier.get("request_leaderboard"));

    public static final StreamCodec<FriendlyByteBuf, RequestLeaderboardPayload> STREAM_CODEC =
            StreamCodec.ofMember((payload, buf) -> {}, buf -> new RequestLeaderboardPayload());

    @Override
    public Type<RequestLeaderboardPayload> type() { return TYPE; }
}
