package com.flower.mitayclient.GUI.screen.ProfileUtil.LeaderBoard;

import com.flower.mitayclient.util.ModIdentifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public record  LeaderboardPayload(Map<String, Double> leaderboard) implements CustomPacketPayload {
    public static final Type<LeaderboardPayload> TYPE =
            new Type<>(ModIdentifier.get("leaderboard"));

    public static final StreamCodec<FriendlyByteBuf, LeaderboardPayload> STREAM_CODEC =
            StreamCodec.ofMember(
                    (payload, buf) -> {
                        Map<String, Double> map = payload.leaderboard;
                        buf.writeInt(map.size());
                        for (Map.Entry<String, Double> entry : map.entrySet()) {
                            byte[] nameBytes = entry.getKey().getBytes(StandardCharsets.UTF_8);
                            buf.writeInt(nameBytes.length);
                            buf.writeBytes(nameBytes);
                            buf.writeDouble(entry.getValue());
                        }
                    },
                    buf -> {
                        int size = buf.readInt();
                        Map<String, Double> map = new LinkedHashMap<>();
                        for (int i = 0; i < size; i++) {
                            int nameLength = buf.readInt();
                            byte[] nameBytes = new byte[nameLength];
                            buf.readBytes(nameBytes);
                            String name = new String(nameBytes, StandardCharsets.UTF_8);
                            double hours = buf.readDouble();
                            map.put(name, hours);
                        }
                        return new LeaderboardPayload(map);
                    }
            );

    @Override
    public Type<LeaderboardPayload> type() {
        return TYPE;
    }
}