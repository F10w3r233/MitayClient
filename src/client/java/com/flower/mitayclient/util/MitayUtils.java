package com.flower.mitayclient.util;

import com.flower.Mitayclient;
import com.flower.mitayclient.GUI.screen.ProfileUtil.PlayerProfile;
import com.flower.mitayclient.GUI.screen.ProfileUtil.title.RankTitle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.player.Player;

import java.text.DecimalFormat;
import java.util.List;

public class MitayUtils
{
    private static final Identifier END = Resource.END_icon;
    private static final Identifier NETHER = Resource.NETHER_icon;
    private static final Identifier OVERWORLD = Resource.OVERWORLD_icon;
    private static final Identifier CREATIVE_WORLD = Resource.CREATIVE_WORLD_icon;

    public static String format(double number)
    {
        DecimalFormat df = new DecimalFormat(".0");
        String result = df.format(number);
        return result.startsWith(".") ? "0" + result : result;
    }

//    public static Double format(double number)
//    {
//        DecimalFormat df = new DecimalFormat(".0");
//        String result = df.format(number);
//        return result.startsWith(".") ? Double.parseDouble("0" + result) : Double.parseDouble(result);
//    }
//    public static double format(double number)
//    {
//        DecimalFormat df = new DecimalFormat(".0");
//        return Double.parseDouble(df.format(number));
//    }


    public static Player getCameraPlayer()
    {
        return Minecraft.getInstance().getCameraEntity() instanceof Player playerEntity ? playerEntity : null;
    }

    public static void sendChatCommand(String msg)
    {
        Minecraft client = Minecraft.getInstance();
        ClientPacketListener handler = client.getConnection();

        if (handler == null) return;
        handler.sendCommand(msg);
    }

    public static int getFontColor()
    {
        return Mitayclient.getConfig().isDarkShown() ? CommonColors.WHITE : CommonColors.BLACK;
    }
    public static int getTextureColor()
    {
        return Mitayclient.getConfig().isDarkShown() ? CommonColors.GRAY : CommonColors.WHITE;
    }

    public static int getNameColor(String name)
    {
        int color = Mitayclient.getConfig().isDarkShown() ? CommonColors.WHITE : 0xFFDCDCDC;
        List<String> playerTitles = PlayerProfile.allPlayerTitles.get(name);
        if(playerTitles != null)
        {
            if(!playerTitles.isEmpty())
            {
                color = RankTitle.getTitleColor(playerTitles.get(0));
            }
        }
        return color;
    }

    public static Identifier getWorldIcon(String worldName)
    {
        Identifier worldIcon = null;
        switch (worldName)
        {
            case "overworld" -> worldIcon = OVERWORLD;
            case "nether" -> worldIcon = NETHER;
            case "end" -> worldIcon = END;
            case "creativeWorld" -> worldIcon = CREATIVE_WORLD;
        }
        return worldIcon;
    }

    public static String getEnWorldName(String cnWorldName)
    {
        String enName = "";
        switch (cnWorldName.trim())
        {
            case "主世界" ->
            {
                enName = "overworld";
            }
            case "地狱" ->
            {
                enName = "nether";
            }
            case "末地" ->
            {
                enName = "end";
            }
            case "创造世界" ->
            {
                enName = "creativeWorld";
            }
        }
        return enName;
    }

    public static String getWorldName(String registerWorldName)
    {
        String result = "";
        switch (registerWorldName)
        {
            case "overworld" -> result = "overworld";
            case "the_nether" -> result = "nether";
            case "the_end" -> result = "end";
            case "" -> result = "creativeWorld";
        }
        return result;
    }

    public static int getIntegerAlpha(float alpha)
    {
        if (alpha <= 0.0f) return 0;
        if (alpha >= 1.0f) return 255;
        return (int)(alpha * 255.0f + 0.5f);
    }

    public static int getTitleColor(String title)
    {
        return RankTitle.getTitleColor(title);
    }

    public static Identifier getWorldIdentifier(String iconName)
    {
        if(iconName == null)
        {
            return null;
        }
        return switch (iconName)
        {
            case "end", "末地" -> END;
            case "overworld", "主世界" -> OVERWORLD;
            case "nether", "地狱" -> NETHER;
            case "creative", "创造世界" -> CREATIVE_WORLD;

            default -> null;
        };
    }
}
