package com.flower.mitayclient.GUI.screen.ProfileUtil;

import net.minecraft.world.entity.player.PlayerSkin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.flower.mitayclient.util.MitayUtils.format;

public class PlayerProfile
{
    public String name;
    public String time;
    public PlayerProfile(String name, double time)
    {
        this.name = name;
        this.time = format(time);
    }

    public PlayerProfile (String name, String time)
    {
        this.name = name;
        this.time = time;
    }
    public static Map<String, Double> leaderboardMap = new LinkedHashMap<>();
    public static List<String> titles = new ArrayList<>();
    // 所有玩家的称号缓存（玩家名 -> 称号列表）
    public static Map<String, List<String>> allPlayerTitles = new LinkedHashMap<>();
    public static Set<String> playerNameSet = ConcurrentHashMap.newKeySet();
    public static String selfGameTime;

}
