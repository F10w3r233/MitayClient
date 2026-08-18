package com.flower.mitayclient.util.ChatHistory;

import com.flower.mitayclient.GUI.screen.ChatHistoryScreen;
import com.flower.mitayclient.GUI.screen.ProfileUtil.PlayerProfile;
import com.flower.mitayclient.util.MitayUtils;
import com.flower.mitayclient.util.ModIdentifier;
import com.flower.mitayclient.util.Resource;
import java.io.IOException;
import java.util.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;

import static com.flower.mitayclient.util.ChatHistory.ChatKeywordDetector.*;
import static com.flower.mitayclient.util.ChatHistory.TextSerializer.removeTrailingText;
import static com.flower.mitayclient.util.MitayUtils.getFontColor;
import static com.flower.mitayclient.util.Resource.getStringWidth;
import static com.flower.mitayclient.util.Skin.SkinCacheHelper.renderHead;

enum MessageType
{
    ACHIEVEMENT,
    SURFACE,
    TP,
    F,
    DEATH,
    AT,
    JOIN_LEFT,
    TIME,

    CHAT
}

public class ChatRenderer
{
    static MessageType type;
    static float delta;
    static int y = 100;
    static int currentWidgetHeight = 10;
    static int START_Y = Minecraft.getInstance().getWindow().getGuiScaledHeight()-100;
    static int totalHeight = 0;
    static int toStopIndex = 0;
    static final int GAP = 6;
    public static void render(GuiGraphicsExtractor context, Component message, int index, int mouseX, int mouseY)
    {
        if (index == 1)
        {
            totalHeight = 0;
            y = Minecraft.getInstance().getWindow().getGuiScaledHeight() - 100;
        }
        MessageType messageType = getType(message);
        if(messageType != null)
        {
            switch (messageType)
            {
                case CHAT ->
                {
                    calculateHeight(index, 24);
                    drawChatMessage(context, message, y, MessageType.CHAT, mouseX, mouseY);
                }
                case SURFACE, TP, JOIN_LEFT, ACHIEVEMENT, AT ->
                {
                    calculateHeight(index, 10);
                    drawCenteredMessage(context, message, y);
                }
                case F, DEATH ->
                {
                    calculateHeight(index, 52);
                    drawChatMessage(context, message, y, getType(message), mouseX, mouseY);
                }
                case TIME ->
                {
                    calculateHeight(index, 10);
                    drawCenteredMessage(context, removeTrailingText(message, "T!I!M!E!"), y);
                }
                default -> throw new IllegalStateException("Unexpected value: " + getType(message));
            }
        }
    }

    public static void calculateHeight(int index, int widgetHeight)
    {
        currentWidgetHeight = widgetHeight;
        if(index == 1) {
            // y 已在 render 中设置，这里不再修改
            totalHeight += widgetHeight;
        } else {
            totalHeight += widgetHeight;
            if (totalHeight > 348)
                toStopIndex = index;
            y -= (GAP + currentWidgetHeight);
        }
    }

    private static final Identifier BUTTON = ModIdentifier.get("textures/gui/screen/chat_history/teleport_button.png");
    private static final Identifier BUTTON_FOCUS = ModIdentifier.get("textures/gui/screen/chat_history/teleport_button_focus.png");

    public static void drawChatMessage(GuiGraphicsExtractor context, Component message, int y, MessageType type, int mouseX, int mouseY)
    {
        int iconX = isSelfMessage(message) ? Minecraft.getInstance().getWindow().getGuiScaledWidth()-100 : 100;
        String nameStr = message.getString().split(":")[0];
        if(type == MessageType.DEATH)
        {
            nameStr = message.getString().split("死于")[0].trim();
//            System.out.println(nameStr);
        }
        try {

            renderHead(context, nameStr.contains("]") ? nameStr.split("]")[1].trim() : nameStr.trim(), iconX, y, 24);
        } catch (ArrayIndexOutOfBoundsException e)
        {
            System.out.println(nameStr);
            System.out.println(message);
        }


        Component[] nameAndContent = TextSerializer.splitComponent(message, ":");
        Component name = TextSerializer.stripLeadingSpaces(nameAndContent[0], 3);
        int nameX = isSelfMessage(message) ? iconX - 6 - getStringWidth(type == MessageType.DEATH ? removeTrailingText(name, " 死于") : name) : iconX + 30;
        context.text(Minecraft.getInstance().font, type == MessageType.DEATH ? removeTrailingText(name, " 死于") : name, nameX, y+2, getFontColor()); //y = iconY + 2

        Component content = nameAndContent[1];
        int textX = isSelfMessage(message) ? iconX-6-Minecraft.getInstance().font.width(content) : iconX + 30;
        switch (type)
        {
            case CHAT ->
            {
                context.text(Minecraft.getInstance().font, content, textX, y+12, getFontColor()); // y = iconY + 12
                //地点名
                if(containsPlace(message.getString()))
                {

                    Identifier icon = getPlaceIcon(getContainedPlaceName(message.getString()));
                    context.blit(RenderPipelines.GUI_TEXTURED, icon, getIconX(message.getString(), getContainedPlaceName(message.getString()), false, 0), y+12, 0,0,8,8,8,8);
                }

                //维度名
                if(containsDimensionName(message.getString().trim()))
                {
                    if(!message.getString().contains("末地主岛"))
                    {
                        Identifier icon = getDimensionIcon(getContainedDimensionName(message.getString()));
                        context.blit(RenderPipelines.GUI_TEXTURED, icon, getIconX(message.getString(), getContainedDimensionName(message.getString()), false, 0), y, 0,0,8,8,8,8);
                    }
                }
            }
            case F, DEATH ->
            {
                String placeName = "";
                String dimensionName = "";
                int color = 1;
                Map<String, Integer> dimensionMap = getDimensionMap(message);
                Set<Map.Entry<String, Integer>> entries1 = dimensionMap.entrySet();
                for (Map.Entry<String, Integer> entry1 : entries1)
                {
                    dimensionName = entry1.getKey();
                    color = entry1.getValue();
                }






                String coordination = getCoordination(message);

                int placeX = isSelfMessage(message) ? iconX-6-Minecraft.getInstance().font.width(dimensionName + coordination) - 2 : iconX + 30 + 6;
                int coordinationX = isSelfMessage(message) ? iconX-6-Minecraft.getInstance().font.width(coordination) - 2 : iconX + 30 + getStringWidth(dimensionName) + 6;


                Component desc = null;
                if(type == MessageType.F)
                {
                    desc = Component.literal("   分享了一个位置");
                }else if(type == MessageType.DEATH)
                {
                    desc = Component.literal("   死于");
                }

                boolean hovered = false;
                if(mouseY >= y && mouseY <= y + 34
                        && mouseX >= placeX && mouseX <= placeX + getStringWidth(coordination + placeName + "   "))
                {
                    hovered = true;
                }

//                context.drawTexture(RenderLayer::getGuiTextured, hovered ? BUTTON_FOCUS_DARK : BUTTON_DARK, placeX-4, y+12, 0,0,Resource.getStringWidth(dimensionName + coordination + "   "), 38, Resource.getStringWidth(placeName + coordination), 38);
                context.blit(RenderPipelines.GUI_TEXTURED, hovered ? BUTTON_FOCUS : BUTTON, placeX-10 +2, y+12, 0,0, getStringWidth(dimensionName + coordination + "   "), 38, getStringWidth(dimensionName + coordination + "   "), 38, ARGB.color(160, 1));
//                context.fill(placeX-2, y-2, placeX + Resource.getStringWidth(placeName + coordination), y-2 + 30, hovered ? Resource.WHITE : 0);
                context.text(Minecraft.getInstance().font, desc, placeX, y + 17, Resource.WHITE);
                context.text(Minecraft.getInstance().font, dimensionName , placeX, y + 27, color);
                context.text(Minecraft.getInstance().font, coordination , coordinationX, y + 27, Resource.GREY);
                context.text(Minecraft.getInstance().font, Component.literal("   无备注"), placeX, y + 37, Resource.WHITE);

                if(containsDimensionName(message.getString()))
                {
                    Identifier icon = getDimensionIcon(getContainedDimensionName(message.getString()));
                    if(icon != null)
                    {
//                        context.drawTexture(RenderLayer::getGuiTextured, icon, getIconX(message.getString(), getContainedPlaceName(message.getString())+10, false, 0) + placeX, y + 27, 0,0,8,8,8,8);
                        context.blit(RenderPipelines.GUI_TEXTURED, icon, placeX-1, y + 27, 0,0,8,8,8,8);
                    }
                }
                if(y > 0)
                    updateClickEvents(placeX, y, dimensionName, coordination);

            }
        }
    }

    public static void drawCenteredMessage(GuiGraphicsExtractor context, Component message, int y)
    {

        int x = (Minecraft.getInstance().getWindow().getGuiScaledWidth() - getStringWidth(message)) / 2;
        context.text(Minecraft.getInstance().font, message, x, y, Resource.GREY);
//        System.out.println(message.getString());

        //玩家名
        Set<Map.Entry<String, Double>> entrySet = PlayerProfile.leaderboardMap.entrySet();
        for (Map.Entry<String, Double> entry : entrySet)
        {
            if (message.getString().contains(entry.getKey()))
            {
                String name = entry.getKey();
                renderHead(context, name, getIconX(message.getString(), getContainedPlayerName(message.getString()), true, x), y, 8);
            }
        }
        //地点名
        if(containsPlace(message.getString()))
        {

            Identifier icon = getPlaceIcon(getContainedPlaceName(message.getString()));
            context.blit(RenderPipelines.GUI_TEXTURED, icon, getIconX(message.getString(), getContainedPlaceName(message.getString()), true, 0)   , y, 0,0,8,8,8,8);
        }

        //维度名
        if(containsDimensionName(message.getString()))
        {
            if(!message.getString().contains("末地主岛"))
            {
                Identifier icon = getDimensionIcon(getContainedDimensionName(message.getString()));
                context.blit(RenderPipelines.GUI_TEXTURED, icon, getIconX(message.getString(), getContainedDimensionName(message.getString()), true, 0), y, 0,0,8,8,8,8);
            }
        }
    }

    //获取消息类型
    public static MessageType getType(Component message)
    {
//        System.out.println(message.getString());
        if(Resource.containsOneOfBoth(message.getString(), "取得了进度", "has made the advancement")) //成就
        {
//            System.out.println("是成就");
            return MessageType.ACHIEVEMENT;
        }else if(Resource.containsOneOfBoth(message.getString(), "传送到地面", "已在地面"))
        {
//            System.out.println("是surface");
            return MessageType.SURFACE;
        }else if(Resource.containsBoth(message.getString(), "将" , "传送至") || Resource.containsBoth(message.getString(), "将", "传送到"))
        {
//            System.out.println("是tp");
            return MessageType.TP;
        }else if (Resource.containsBoth(message.getString(),"死于", "["))
        {
            if(message.getString().split("]")[1].contains("["))
            {
                return MessageType.DEATH;
            }else return MessageType.SURFACE;
//            System.out.println("是death");

        }else if(message.getString().contains("T!I!M!E!"))
        {
            return MessageType.TIME;
        } else if(message.getString().contains(":"))
        {
            if(message.getString().contains("分享了一个位置"))
                return MessageType.F;
            Set<Map.Entry<String, List<String>>> entries = PlayerProfile.allPlayerTitles.entrySet();
            for (Map.Entry<String, List<String>> entry : entries)
            {
//                System.out.println("名字：" + entry.getKey().getString());
                if(message.getString().contains(entry.getKey()))
                {
                    return MessageType.CHAT;
                }
            }
        }else if(message.getString().contains("@"))
        {
            return MessageType.AT;
        } else if(Resource.containsOneOfBoth(message.getString(), "加入了游戏", "退出了游戏"))
        {
            return MessageType.JOIN_LEFT;
        }
//        return MessageType.TP;
        return null;
    }

    public static boolean isSelfMessage(Component message)
    {
        return message.getString().contains(Resource.getCameraPlayer().getScoreboardName());
    }

    private static int getIconX(String message, String name, boolean centered, int textX)
    {
        String oriName = name.contains("[") ? name.split("]")[1].trim() : name;
        int startIndex = message.indexOf(oriName);
        if(message.contains("] " + oriName)) //用于历史记录中 修复玩家称号发生变化
        {
            startIndex = message.indexOf("[");
        }
        if(startIndex == -1) //说明没有 display name
        {
            if (!oriName.equals(name))
            {
                startIndex = message.indexOf(oriName);
            } else return 0;
        }
        String prefix = message.substring(0, startIndex);
        int iconX;
        if(centered)
        {
            iconX = Minecraft.getInstance().font.width(prefix) - 10 + (Minecraft.getInstance().getWindow().getGuiScaledWidth() - getStringWidth(message)) / 2;
        }else
        {
            iconX = Minecraft.getInstance().font.width(prefix) - 10 + textX;
        }
        return iconX;
    }

    public static Map<String, Integer> getDimensionMap(Component mes)
    {
        String message = mes.getString();
        String placeName = "";
        int color = 0;
        Map<String, Integer> map = new HashMap<>();
        if(message.contains("主世界"))
        {
            placeName = "   主世界";
            color = 0xFF01B207;
        }else if(message.contains("地狱"))
        {
            placeName = "   地狱";
            color = 0xFFBD3737;
        }else if(message.contains("末地"))
        {
            placeName = "   末地";
            color = 0xFF890D89;
        }else if(message.contains("创造世界"))
        {
            placeName = "   创造世界";
            color = 0xFF01B207;
        }
        map.put(placeName, color);
        return map;
    }

    public static String getCoordination(Component message)
    {
        return message.getString().split(":")[1].split("]")[1].replace("[", "").replace("]", "").replace("点击传送", "");
    }

    public static void updateClickEvents(int x, int y, String worldName, String coordinate)
    {
//        System.out.println("列表已更新");
//        System.out.println("x:" + x + " ,y: " + y + " ,worldName : " + worldName + " ,coordinate: " + coordinate);
        if(y > 0)
        {
            ChatHistoryScreen.clickCoordinateMap.put(y, new ClickCoordinate(x, worldName, coordinate));
        }
    }
}
