package com.flower.mitayclient.GUI.HUD.Tab.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public record TabInfoPayload(Map<String, PlayerTabInfo> playerInfoMap) implements CustomPacketPayload {
    public static final Type<TabInfoPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("mitayclient", "tab_info"));

    public record PlayerTabInfo(String world, boolean afk, int afkSeconds) {}

    public static final StreamCodec<FriendlyByteBuf, TabInfoPayload> STREAM_CODEC =
            StreamCodec.ofMember(
                    (payload, buf) -> {
                        Map<String, PlayerTabInfo> map = payload.playerInfoMap();
                        buf.writeInt(map.size());
                        for (Map.Entry<String, PlayerTabInfo> entry : map.entrySet()) {
                            byte[] nameBytes = entry.getKey().getBytes(StandardCharsets.UTF_8);
                            buf.writeInt(nameBytes.length);
                            buf.writeBytes(nameBytes);
                            PlayerTabInfo info = entry.getValue();
                            byte[] worldBytes = info.world().getBytes(StandardCharsets.UTF_8);
                            buf.writeInt(worldBytes.length);
                            buf.writeBytes(worldBytes);
                            buf.writeBoolean(info.afk());
                            buf.writeInt(info.afkSeconds());
                        }
                    },
                    buf -> {
                        int size = buf.readInt();
                        Map<String, PlayerTabInfo> map = new LinkedHashMap<>();
                        for (int i = 0; i < size; i++) {
                            int nameLen = buf.readInt();
                            byte[] nameBytes = new byte[nameLen];
                            buf.readBytes(nameBytes);
                            String name = new String(nameBytes, StandardCharsets.UTF_8);
                            int worldLen = buf.readInt();
                            byte[] worldBytes = new byte[worldLen];
                            buf.readBytes(worldBytes);
                            String world = new String(worldBytes, StandardCharsets.UTF_8);
                            boolean afk = buf.readBoolean();
                            int afkSeconds = buf.readInt();
                            map.put(name, new PlayerTabInfo(world, afk, afkSeconds));
                        }
                        return new TabInfoPayload(map);
                    }
            );

    @Override
    public Type<TabInfoPayload> type() { return TYPE; }
}
