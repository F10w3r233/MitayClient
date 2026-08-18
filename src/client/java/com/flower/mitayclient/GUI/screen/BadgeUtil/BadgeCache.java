package com.flower.mitayclient.GUI.screen.BadgeUtil;

import com.flower.mitayclient.GUI.screen.BadgeUtil.networking.BadgesPayload;

import java.util.LinkedHashMap;
import java.util.Map;

public class BadgeCache {
    private static Map<String, BadgesPayload.BadgeInfo> badges = new LinkedHashMap<>();

    public static void set(Map<String, BadgesPayload.BadgeInfo> newBadges) {
        badges = newBadges != null ? newBadges : new LinkedHashMap<>();
    }

    public static Map<String, BadgesPayload.BadgeInfo> get() {
        return badges;
    }

    public static int size() { return badges.size(); }
    public static boolean isEmpty() { return badges.isEmpty(); }
}