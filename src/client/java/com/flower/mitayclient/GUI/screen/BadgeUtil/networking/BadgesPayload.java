package com.flower.mitayclient.GUI.screen.BadgeUtil.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public record BadgesPayload(Map<String, BadgeInfo> badges) implements CustomPacketPayload {
    public static final Type<BadgesPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("mitayclient", "badges"));

    public record BadgeInfo(String key, String name, String owner, String time, String desc) {}

    public static final StreamCodec<FriendlyByteBuf, BadgesPayload> STREAM_CODEC =
            StreamCodec.ofMember(
                    (payload, buf) -> {
                        Map<String, BadgeInfo> map = payload.badges();
                        buf.writeInt(map.size());
                        for (Map.Entry<String, BadgeInfo> entry : map.entrySet()) {
                            BadgeInfo info = entry.getValue();
                            // 写入 key（与 map key 相同，这里再用字符串传递以确保顺序）
                            byte[] keyBytes = info.key().getBytes(StandardCharsets.UTF_8);
                            buf.writeInt(keyBytes.length);
                            buf.writeBytes(keyBytes);

                            byte[] nameBytes = info.name().getBytes(StandardCharsets.UTF_8);
                            buf.writeInt(nameBytes.length);
                            buf.writeBytes(nameBytes);

                            byte[] ownerBytes = info.owner().getBytes(StandardCharsets.UTF_8);
                            buf.writeInt(ownerBytes.length);
                            buf.writeBytes(ownerBytes);

                            byte[] timeBytes = info.time().getBytes(StandardCharsets.UTF_8);
                            buf.writeInt(timeBytes.length);
                            buf.writeBytes(timeBytes);

                            byte[] descBytes = info.desc().getBytes(StandardCharsets.UTF_8);
                            buf.writeInt(descBytes.length);
                            buf.writeBytes(descBytes);
                        }
                    },
                    buf -> {
                        int size = buf.readInt();
                        Map<String, BadgeInfo> map = new LinkedHashMap<>();
                        for (int i = 0; i < size; i++) {
                            int keyLen = buf.readInt();
                            byte[] keyBytes = new byte[keyLen];
                            buf.readBytes(keyBytes);
                            String key = new String(keyBytes, StandardCharsets.UTF_8);

                            int nameLen = buf.readInt();
                            byte[] nameBytes = new byte[nameLen];
                            buf.readBytes(nameBytes);
                            String name = new String(nameBytes, StandardCharsets.UTF_8);

                            int ownerLen = buf.readInt();
                            byte[] ownerBytes = new byte[ownerLen];
                            buf.readBytes(ownerBytes);
                            String owner = new String(ownerBytes, StandardCharsets.UTF_8);

                            int timeLen = buf.readInt();
                            byte[] timeBytes = new byte[timeLen];
                            buf.readBytes(timeBytes);
                            String time = new String(timeBytes, StandardCharsets.UTF_8);

                            int descLen = buf.readInt();
                            byte[] descBytes = new byte[descLen];
                            buf.readBytes(descBytes);
                            String desc = new String(descBytes, StandardCharsets.UTF_8);

                            map.put(key, new BadgeInfo(key, name, owner, time, desc));
                        }
                        return new BadgesPayload(map);
                    }
            );

    @Override
    public Type<BadgesPayload> type() { return TYPE; }
}