package com.flower.mitayclient.event;

import com.flower.mitayclient.GUI.screen.*;
import com.flower.mitayclient.GUI.HUD.Tab.network.RequestTabInfoPayload;
import com.flower.mitayclient.util.ModIdentifier;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.flower.mitayclient.util.MitayUtils.sendChatCommand;

public class KeyInputHandler {

    public static final KeyMapping.Category MITAY_CATEGORY =
            KeyMapping.Category.register(ModIdentifier.get("mitay"));

    public static final String KEY_OPEN_PLACES_LIST = "key.mitayclient.open_places_list";
    public static final String KEY_OPEN_TELEPORT_PLAYER = "key.mitayclient.open_teleport_player";
    public static final String KEY_SURFACE = "key.mitayclient.surface";
    public static final String KEY_BROADCAST_LOCATION = "key.mitayclient.broadcast_location";
    public static final String KEY_HOME = "key.mitayclient.home";
    public static final String KEY_ADMIN = "key.mitayclient.admin";
    public static final String KEY_SETTINGS = "key.mitayclient.settings";
    public static final String KEY_CHAT_HISTORY = "key.mitayclient.history";
    public static final String KEY_TAB = "key.mitayclient.tab";

    public static KeyMapping openPlacesListKey;
    public static KeyMapping openTeleportPlayerKey;
    public static KeyMapping surfaceKey;
    public static KeyMapping broadcastLocationKey;
    public static KeyMapping homeKey;
    public static KeyMapping adminKey;
    public static KeyMapping settingsKey;
    public static KeyMapping chatHistoryKey;
    public static KeyMapping tabKey;

    /**
     * 统一处理所有按键输入（仅在非本地服务器时生效）
     */
    public static void registerKeyInputs()
    {
        if (!Minecraft.getInstance().isLocalServer())
        {
            // 按键与对应动作的映射表，保持插入顺序
            Map<KeyMapping, Runnable> keyActions = new LinkedHashMap<>();

            //--屏幕--
            keyActions.put(openPlacesListKey, () -> Minecraft.getInstance().setScreen(new PlaceListScreen()));
            keyActions.put(openTeleportPlayerKey, () -> Minecraft.getInstance().setScreen(new TeleportScreen()));
            keyActions.put(adminKey, () -> Minecraft.getInstance().setScreen(new TESTScreen()));
            keyActions.put(settingsKey, () -> Minecraft.getInstance().setScreen(new SettingsScreen()));
            keyActions.put(chatHistoryKey, () -> Minecraft.getInstance().setScreen(new ChatHistoryScreen()));
            //--功能--
            keyActions.put(surfaceKey, () -> sendChatCommand("surface"));
            keyActions.put(broadcastLocationKey, () -> sendChatCommand("f"));
            keyActions.put(homeKey, () -> sendChatCommand("home"));
            //--数据包--
            keyActions.put(tabKey, () -> ClientPlayNetworking.send(new RequestTabInfoPayload()));

            // 只注册一个 tick 事件，统一检测并触发
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                keyActions.forEach((key, action) -> {
                    if (key.consumeClick()) {
                        action.run();
                    }
                });
            });
        }
    }

    public static void register() {
        openPlacesListKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                KEY_OPEN_PLACES_LIST,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                MITAY_CATEGORY
        ));

        openTeleportPlayerKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                KEY_OPEN_TELEPORT_PLAYER,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                MITAY_CATEGORY
        ));

        surfaceKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                KEY_SURFACE,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                MITAY_CATEGORY
        ));

        broadcastLocationKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                KEY_BROADCAST_LOCATION,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                MITAY_CATEGORY
        ));

        homeKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                KEY_HOME,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT,
                MITAY_CATEGORY
        ));

        adminKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                KEY_ADMIN,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_MINUS,
                MITAY_CATEGORY
        ));

        settingsKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                KEY_SETTINGS,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                MITAY_CATEGORY
        ));

        chatHistoryKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                KEY_CHAT_HISTORY,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                MITAY_CATEGORY
        ));

        tabKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                KEY_TAB,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_TAB,
                MITAY_CATEGORY
        ));

        registerKeyInputs();
    }
}