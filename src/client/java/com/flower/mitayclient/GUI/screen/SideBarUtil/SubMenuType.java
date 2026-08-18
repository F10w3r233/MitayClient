package com.flower.mitayclient.GUI.screen.SideBarUtil;

import com.flower.mitayclient.GUI.buttons.PlaceList.Large.PlaceListButton;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class SubMenuType
{
    public String typeName;

    public List<PlaceListButton> contentButtonList = new ArrayList<>();

    public SubMenuType(String typeName, List<PlaceListButton> contentButtonList)
    {
        this.typeName = typeName;
        this.contentButtonList = contentButtonList;
    }
}
