package com.flower.mitayclient.GUI.screen.PlaceListUtil;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import java.nio.charset.StandardCharsets;
import java.util.*;

public record PlacesPayload(Map<Integer, PlaceInfo> places) implements CustomPacketPayload
{
    public static final Type<PlacesPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath("mitayclient", "places"));

    public record PlaceInfo(String world, double x, double y, double z, String uploader, String desc) {}

    public static final StreamCodec<FriendlyByteBuf, PlacesPayload> STREAM_CODEC = StreamCodec.ofMember(
            (payload, buf) -> {
                Map<Integer, PlaceInfo> map = payload.places();
                buf.writeInt(map.size());
                for (Map.Entry<Integer, PlaceInfo> entry : map.entrySet()) {
                    buf.writeInt(entry.getKey());
                    PlaceInfo info = entry.getValue();
                    byte[] worldBytes = info.world().getBytes(StandardCharsets.UTF_8);
                    buf.writeInt(worldBytes.length);
                    buf.writeBytes(worldBytes);
                    buf.writeDouble(info.x());
                    buf.writeDouble(info.y());
                    buf.writeDouble(info.z());
                    byte[] uploaderBytes = info.uploader().getBytes(StandardCharsets.UTF_8);
                    buf.writeInt(uploaderBytes.length);
                    buf.writeBytes(uploaderBytes);
                    byte[] descBytes = info.desc().getBytes(StandardCharsets.UTF_8);
                    buf.writeInt(descBytes.length);
                    buf.writeBytes(descBytes);
                }
            },
            buf -> {
                int size = buf.readInt();
                Map<Integer, PlaceInfo> map = new LinkedHashMap<>();
                for (int i = 0; i < size; i++) {
                    int id = buf.readInt();
                    int worldLen = buf.readInt();
                    byte[] worldBytes = new byte[worldLen];
                    buf.readBytes(worldBytes);
                    String world = new String(worldBytes, StandardCharsets.UTF_8);
                    double x = buf.readDouble();
                    double y = buf.readDouble();
                    double z = buf.readDouble();
                    int uploaderLen = buf.readInt();
                    byte[] uploaderBytes = new byte[uploaderLen];
                    buf.readBytes(uploaderBytes);
                    String uploader = new String(uploaderBytes, StandardCharsets.UTF_8);
                    int descLen = buf.readInt();
                    byte[] descBytes = new byte[descLen];
                    buf.readBytes(descBytes);
                    String desc = new String(descBytes, StandardCharsets.UTF_8);
                    map.put(id, new PlaceInfo(world, x, y, z, uploader, desc));
                }
                return new PlacesPayload(map);
            }
    );

    @Override
    public Type<PlacesPayload> type() { return TYPE; }
}