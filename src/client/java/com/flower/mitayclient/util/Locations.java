package com.flower.mitayclient.util;

import net.minecraft.network.chat.Component;

public class Locations
{
    public static boolean isAtOverWorldRegion(double x1, double x2, double z1, double z2, double playerX, double playerZ)
    {
        if(playerX >= x1 && playerX <= x2 && playerZ >= z1 && playerZ <= z2)
        {
            return true;
        }else return false;
    }
    public static boolean isAtNetherRegion(double x1, double x2, double z1, double z2, double playerX, double playerZ)
    {
        if(playerX >= x1 && playerX <= x2 && playerZ >= z1 && playerZ <= z2)
        {
            return true;
        }else return false;
    }
    public static boolean isAtEndRegion(double x1, double x2, double z1, double z2, double playerX, double playerZ)
    {
        if(playerX >= x1 && playerX <= x2 && playerZ >= z1 && playerZ <= z2)
        {
            return true;
        }else return false;
    }
    public static Component getPlace(double x, double z, String worldName)
    {
        if(worldName.equals("主世界"))
        {
            if(isAtOverWorldRegion(114,171,69,159,x,z))
            {
                return Resource.EXCHANGE_text;
            }else if (isAtOverWorldRegion(16, 71, -247, -193,x,z))
            {
                return Resource.IRON_text;
            } else if(isAtOverWorldRegion(-191, -40, -319, -170, x, z))
            {
                return Resource.ZOO_text;
            }else if(isAtOverWorldRegion(-3, 68, -281, -259, x, z))
            {
                return Resource.SUGAR_CANE_text;
            }else if(isAtOverWorldRegion(-24, -5, -282, -260, x, z))
            {
                return Resource.STONE_text;
            }else if(isAtOverWorldRegion(11, 54, -307, -285, x, z))
            {
                return Resource.SUGAR_CANE_text;
            }else if(isAtOverWorldRegion(-11, 1, -306, -291, x, z))
            {
                return Resource.STONE_text;
            }else return null;
        }else if(worldName.equals("地狱"))
        {
            if(isAtNetherRegion(-14,53,101,141,x,z))
            {
                return Resource.PIG_MAN_text;
            }else return null;
        }else if(worldName.equals("末地"))
        {
            if(isAtEndRegion(0,0,0,0,x,z))
            {
                return Resource.ENDER_MAN_text;
            }else return null;
        }else return null;
    }
}
