package com.flower.mitayclient.GUI.screen.ProfileUtil.title;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record PlayerTitlesPayload(Map<String, List<String>> titleMap) implements CustomPacketPayload {

    public static final Type<PlayerTitlesPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("mitayclient", "player_titles"));

    public static final StreamCodec<FriendlyByteBuf, PlayerTitlesPayload> STREAM_CODEC =
            StreamCodec.ofMember(
                    (payload, buf) -> {
                        Map<String, List<String>> map = payload.titleMap();
                        buf.writeInt(map.size());
                        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                            byte[] nameBytes = entry.getKey().getBytes(StandardCharsets.UTF_8);
                            buf.writeInt(nameBytes.length);
                            buf.writeBytes(nameBytes);
                            buf.writeInt(entry.getValue().size());
                            for (String title : entry.getValue()) {
                                byte[] titleBytes = title.getBytes(StandardCharsets.UTF_8);
                                buf.writeInt(titleBytes.length);
                                buf.writeBytes(titleBytes);
                            }
                        }
                    },
                    buf -> {
                        int mapSize = buf.readInt();
                        Map<String, List<String>> map = new LinkedHashMap<>();
                        for (int i = 0; i < mapSize; i++) {
                            int nameLen = buf.readInt();
                            byte[] nameBytes = new byte[nameLen];
                            buf.readBytes(nameBytes);
                            String name = new String(nameBytes, StandardCharsets.UTF_8);

                            int titleCount = buf.readInt();
                            List<String> titles = new ArrayList<>();
                            for (int j = 0; j < titleCount; j++) {
                                int titleLen = buf.readInt();
                                byte[] titleBytes = new byte[titleLen];
                                buf.readBytes(titleBytes);
                                titles.add(new String(titleBytes, StandardCharsets.UTF_8));
                            }
                            map.put(name, titles);
                        }
                        return new PlayerTitlesPayload(map);
                    }
            );
    @Override
    public Type<PlayerTitlesPayload> type() {
        return TYPE;
    }
}