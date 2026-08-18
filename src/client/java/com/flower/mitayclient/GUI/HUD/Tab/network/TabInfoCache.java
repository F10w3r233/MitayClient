package com.flower.mitayclient.GUI.HUD.Tab.network;

import java.util.LinkedHashMap;
import java.util.Map;

public class TabInfoCache
{
    private static Map<String, TabInfoPayload.PlayerTabInfo> cache = new LinkedHashMap<>();

    public static void set(Map<String, TabInfoPayload.PlayerTabInfo> data) {
        cache = data != null ? data : new LinkedHashMap<>();
    }

    public static Map<String, TabInfoPayload.PlayerTabInfo> get() {
        return cache;
    }
}