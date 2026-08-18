package com.flower.mitayclient.util;

import com.flower.mitayclient.GUI.screen.ProfileUtil.PlayerProfile;
import com.flower.mitayclient.GUI.screen.ProfileUtil.title.RequestTitlesPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class NameFinder
{
    //检测该字符是否为空格，用于检测名字边界
    private static boolean isWordCharacter(int codePoints) {
        return Character.isLetterOrDigit(codePoints)
                || codePoints == '_'
                || Character.getNumericValue(codePoints) != -1;
    }

    public static boolean isCharLegal(int cp)
    {
        return (cp >= 'a' && cp <= 'z') ||
                (cp >= 'A' && cp <= 'Z') ||
                (cp >= '0' && cp <= '9') ||
                cp == '_';
    }

    public static List<String> findPureNames(String message)
    {
        updateIfLeaveOrJoin();
        List<String> found = new ArrayList<>();
        int[] codePoints = message.codePoints().toArray(); //codePoints储存的是字符串中每一个字符的unicode码
        Set<String> playerNames = PlayerProfile.playerNameSet; //纯玩家名集合

        for (int index = 0; index < codePoints.length;)
        {
            if(!isCharLegal(codePoints[index]))
            {
                index++;
                continue;
            }

            //一旦以下代码执行，说明找到了 玩家名开头的
            int nameStartX = index;
            while (index < codePoints.length && isCharLegal(codePoints[index]))
            {
                index++;
            }
            char chara = message.charAt(index-1);
            int nameEndX = index;


            //提取玩家名字
            //第二个参数相当于subString()的第一个参数，第三个参数是从startX的位置开始取多长的字符串
            String name = new String(codePoints, nameStartX, nameEndX - nameStartX);
            if (playerNames.contains(name))
                found.add(name);
        }
        return found;
    }

    public static int getHeadX(String message, String pureName)
    {
        int nameIndex = message.indexOf(pureName);
        if (nameIndex == -1) return -2;
        if (nameIndex == 1) return -2;
        if (nameIndex == 0) return -10;

        char chara = message.charAt(nameIndex-2);
        if (message.charAt(nameIndex-2) == ' ')
            return -10;

        if (message.charAt(nameIndex-2) == ']')
        {
              for (int i = nameIndex-2; i > 0;)
              {
                  i--;
                  int character = message.charAt(i);
                  if (character == '[')
                  {
                      nameIndex = i;
                      break;
                  }
              }
        }

        String prefix = message.substring(0, nameIndex);
        return Minecraft.getInstance().font.width(prefix) - 7;
    }

    public static int onlinePlayers = 0;
    public static void updateIfLeaveOrJoin()
    {
        Collection<PlayerInfo> players = null;
        if (Minecraft.getInstance().getConnection() != null)
        {
            players = Minecraft.getInstance().getConnection().getOnlinePlayers();
        }
        if (players == null) return;
        int currentCount = players.size();
        if (onlinePlayers != currentCount) {
            ClientPlayNetworking.send(new RequestTitlesPayload()); // 发送请求
            onlinePlayers = currentCount;
        }
    }
}
