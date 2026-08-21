package com.flower.mitayclient.util.ChatHistory;

import com.flower.mitayclient.GUI.buttons.PlaceList.Large.PlaceListPressable;
import com.flower.mitayclient.GUI.buttons.PlaceList.Small.SmallPressable;
import java.io.IOException;
import java.util.*;

import com.flower.mitayclient.GUI.screen.ProfileUtil.PlayerProfile;
import com.flower.mitayclient.GUI.screen.ProfileUtil.title.RankTitle;
import com.flower.mitayclient.GUI.screen.ProfileUtil.title.RequestTitlesPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;

import static com.flower.mitayclient.util.ChatHistory.TextSerializer.concat;
import static com.flower.mitayclient.util.ChatHistory.TextSerializer.setColor;

public class ChatKeywordDetector
{
    //===================================玩家名===========================================
    public static Collection<PlayerInfo> players = Minecraft.getInstance().getConnection().getOnlinePlayers();
    public static int onlinePlayers = 0;
    static Set<String> dimensionList = Set.of(
            "主世界",
            "地狱",
            "末地",
            "创造世界"
    );


    private static Map<String, List<String>> getTitledNameMap() {
        Map<String, List<String>> map = PlayerProfile.allPlayerTitles;
        return map != null ? map : Collections.emptyMap();
    }



    public static String getContainedDimensionName(String content)
    {
        if(containsDimensionName(content))
        {
            for(String dimension : dimensionList)
            {
                if(content.contains(dimension))
                    return dimension;
            }
        }
        return "";
    }


    public static boolean containsDimensionName(String content)
    {
        for(String dimension : dimensionList)
        {
            if(content.contains(dimension))
                return true;
        }
        return false;
    }

    public static Identifier getDimensionIcon(String placeName)
    {
        return switch (placeName)
        {
            case "主世界" -> SmallPressable.getIdentifier("overworld");
            case "地狱" -> SmallPressable.getIdentifier("nether");
            case "末地" -> SmallPressable.getIdentifier("end");
            case "创造世界" -> SmallPressable.getIdentifier("creative");
            default -> null;
        };
    }

    //===========================玩家名===========================
    public static boolean containsPlayerName(String content)
    {
        updateIfLeaveOrJoin(players);
//        updatePlayerList();
//        boolean flag = false;
//        System.out.println(titledNameMap.isEmpty());

        for (String name : PlayerProfile.playerNameSet)
        {
            if (content.contains(name))
            {
                return true;
            }
        }
//        for (Map.Entry<String, List<String>> entry : getTitledNameMap().entrySet())
//        {
//            String entryName = entry.getKey();
//
//            if(content.contains(entryName))
//            {
//                flag = true;
//            }
//        }
//        return flag;
        return false;
    }

    public static boolean containsTwoPlayerNames(String content)
    {
        updateIfLeaveOrJoin(players);
//        updatePlayerList();
        int number = 0;
        for (Map.Entry<String, List<String>> entry : getTitledNameMap().entrySet())
        {
            String entryName = entry.getKey();
             if(content.contains(entryName))
                 number += 1;
        }
        return number == 2;
    }


    //若有DisplayName就会取displayName
    public static String getContainedPlayerName(String content)
    {
        if (!containsPlayerName(content))
            return null;       //快速获取失败（学到了！以下的这些遍历全都不用执行）

//        for (Map.Entry<String, List<String>> entry : getTitledNameMap().entrySet())
//        {
//            String entryName = entry.getKey();
//
//            if(content.contains(entryName))
//            {
//                if(entry.getValue().isEmpty())
//                {
//                    return entryName;
//                } else return getDisplayNameInContext(entryName, content);
//            }
//        }

        for (String name : getTitledNameMap().keySet())
        {
            if(content.contains(name))
            {
                List<String> titles = getTitledNameMap().get(name);
                if(titles == null || titles.isEmpty())
                    return name; //无display Name

                return  "[" + titles.get(0) + "] " + name;
            }
        }
        return null;
    }


    //若有DisplayName就会取displayName
    public static List<String> getContainedPlayerNameList(String content)
    {
        List<String> containedPlayerNameList = new ArrayList<>();
        if (containsTwoPlayerNames(content))
        {
            for (Map.Entry<String, List<String>> entry : getTitledNameMap().entrySet())
            {
                if(containedPlayerNameList.size() == 2)
                    break;
                String entryName = entry.getKey();
                if(content.contains(entryName))
                {
                    if(entry.getValue().isEmpty()) //如果titles为空说明没有displayName
                    {
                        containedPlayerNameList.add(entryName);
                    }else { //有displayName
                        containedPlayerNameList.add(getDisplayNameInContext(entryName, content));
                    }
                }
            }
        }
        return containedPlayerNameList;
    }

    public static String getDisplayNameInContext(String name, String content)
    {
        String displayName = name;
        for (Map.Entry<String, List<String>> entry : getTitledNameMap().entrySet())
        {
            List<String> titles = entry.getValue();
            for (String title : titles)
            {
                if(content.contains(title))
                {
                    displayName = "[" + title + "] " + name;
                    return displayName;
                }
            }
        }
        return displayName;
    }


    //===========================地点名=========================================================
    public static boolean containsPlace(String message)
    {
        for (String place : placeList)
        {
            if (message.contains(place))
            {
                return true;
            }
        }
        return false;
    }

    public static String getContainedPlaceName(String message)
    {
        if (containsPlace(message))
        {
            for (String place : placeList)
            {
                if (message.contains(place))
                    return place;
            }
        }
        return "";
    }


    static Set<String> placeList = Set.of(
            "重生点",
            "交易所",
            "大本营",
            "刷怪塔·资源点",
            "刷怪塔·挂机点",
            "刷铁机",
            "100倍速熔炉组",
            "刷石机",
            "守卫者农场",

            "猪人塔",
            "凋零骷髅塔",
            "恶魂塔",

            "主世界末地传送门",
            "末地主岛",
            "小黑塔",

            "创造世界"
    );

    private static final Map<String, String> PLACE_TO_NAME = Map.ofEntries(
            Map.entry("重生点", "spawnpoint"),
            Map.entry("交易所", "exchange"),
            Map.entry("大本营", "home"),
            Map.entry("刷怪塔·资源点", "mob_resource"),
            Map.entry("刷怪塔·挂机点", "mob_afk"),
            Map.entry("刷铁机", "iron"),
            Map.entry("100倍速熔炉组", "furnace"),
            Map.entry("刷石机", "stone"),
            Map.entry("守卫者农场", "guardian"),
            Map.entry("猪人塔", "pig_man"),
            Map.entry("凋零骷髅塔", "wither_skull"),
            Map.entry("恶魂塔", "ghast_farm"),
            Map.entry("主世界末地传送门", "end_portal"),
            Map.entry("末地主岛", "end_mainland"),
            Map.entry("小黑塔", "ender_man_farm"),
            Map.entry("创造世界", "creative")
    );

    public static Identifier getPlaceIcon(String place) {
        String enName = PLACE_TO_NAME.get(place);
        return PlaceListPressable.getIdentifier(enName);
    }

    public static void updatePlayerList()
    {
//        ClientPlayNetworking.send(new RequestTitlesPayload());
    }

    public static void updateIfLeaveOrJoin(Collection<PlayerInfo> players) {
        if (players == null) return;
        int currentCount = players.size();
        if (onlinePlayers != currentCount) {
            updatePlayerList(); // 发送请求
            onlinePlayers = currentCount;
        }
    }
}