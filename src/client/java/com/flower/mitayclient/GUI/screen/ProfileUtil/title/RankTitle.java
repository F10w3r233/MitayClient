package com.flower.mitayclient.GUI.screen.ProfileUtil.title;

import net.minecraft.util.CommonColors;

public enum RankTitle
{
    EMBER("EMBER", 10, CommonColors.GREEN),             // 假设EMBER用金色
    EMBER_PLUS("EMBER+", 30, 0xFF16A21B),     // DD9901 接近金色粗体
    ASTRA("ASTRA", 50, 0xFF6CCDEA),
    ASTRA_PLUS("ASTRA+", 100, 0xFF6CCDEA),      // 6CCDEA 天蓝
    QUASAR("QUASAR", 300, 0xFFDD9901),
    QUASAR_PLUS("QUASAR+", 500, 0xFFDD9901),
    ECLIPSE("ECLIPSE", 750, 0xFF8422E7),        // 8422E7 深紫
    ECLIPSE_PLUS("ECLIPSE+", 1000, 0xFF3D0B6F); // 3D0B6F 暗蓝

    public final String displayName;
    public final int hoursRequired;
    public final int colorCode;

    RankTitle(String displayName, int hoursRequired, int colorCode) {
        this.displayName = displayName;
        this.hoursRequired = hoursRequired;
        this.colorCode = colorCode;
    }

    // 根据小时数获取最高可达称号
    public static RankTitle getTitleForHours(double hours) {
        RankTitle best = null;
        for (RankTitle t : values()) {
            if (hours >= t.hoursRequired) {
                best = t;
            }
        }
        return best; // 可能为null（不足10小时）
    }

    public static RankTitle getTitleByStr(String title)
    {
        for (RankTitle value : RankTitle.values())
        {
//            System.out.println(value.displayName);
            if(value.displayName.equals(title))
                return value;
        }
        return null;
    }

    public static int getTitleColor(RankTitle title)
    {
        return title == null ? 0 : title.colorCode;
    }

    public static int getTitleColor(String title)
    {
        return getTitleByStr(title) == null ? 0 : getTitleByStr(title).colorCode;
    }
}