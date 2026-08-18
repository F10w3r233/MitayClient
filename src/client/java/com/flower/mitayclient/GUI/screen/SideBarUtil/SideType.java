package com.flower.mitayclient.GUI.screen.SideBarUtil;

import com.flower.mitayclient.GUI.buttons.PlaceList.Large.PlaceListButton;
import com.flower.mitayclient.GUI.buttons.Switch.SwitchButton;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class SideType
{
    public final Identifier icon;
    public final String typeName;
    public final List<PlaceListButton> contentButtonList;
    public final List<SwitchButton> switchButtonList;

    // 私有构造器，接收泛型列表并存储到不同字段
    private SideType(Identifier icon, String typeName, List<?> list, Class<?> type)
    {
        this.icon = icon;
        this.typeName = typeName;
        if (type == PlaceListButton.class)
        {
            this.contentButtonList = (List<PlaceListButton>) list;
            this.switchButtonList = new ArrayList<>();
        } else if (type == SwitchButton.class)
        {
            this.contentButtonList = new ArrayList<>();
            this.switchButtonList = (List<SwitchButton>) list;
        } else
        {
            throw new IllegalArgumentException("Unsupported type");
        }
    }

    // 静态工厂方法：用于 PlaceListButton 列表
    public static SideType withPlaceButtons(Identifier icon, String typeName, List<PlaceListButton> buttons)
    {
        return new SideType(icon, typeName, buttons, PlaceListButton.class);
    }

    // 静态工厂方法：用于 SwitchButton 列表
    public static SideType withSwitchButtons(Identifier icon, String typeName, List<SwitchButton> buttons)
    {
        return new SideType(icon, typeName, buttons, SwitchButton.class);
    }

    public static SideType withCustomStyle(Identifier icon, String typeName)
    {
        return new SideType(icon, typeName, new ArrayList<>(), SwitchButton.class);
    }
}