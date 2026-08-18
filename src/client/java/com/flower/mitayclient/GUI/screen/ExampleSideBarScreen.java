package com.flower.mitayclient.GUI.screen;

import com.flower.mitayclient.GUI.buttons.PlaceList.Large.PlaceListButton;
import com.flower.mitayclient.GUI.screen.SideBarUtil.SideType;
import com.flower.mitayclient.GUI.screen.SideBarUtil.SubMenuType;
import com.flower.mitayclient.util.Resource;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.*;

public class ExampleSideBarScreen extends SideBarScreen
{
    public ExampleSideBarScreen(Component title)
    {
        super(title, Type.NORMAL);
    }

    SideType overworldSide;
    SideType netherSide;


    Map<String, SideType> sideTypeMap = new LinkedHashMap<>();
    Map<String, List<PlaceListButton>> subMenuButtonsMap = new LinkedHashMap<>();

    //SubMenuButtons
    List<PlaceListButton> subMenuButtons1 = Arrays.asList(
            createContentButton(Component.literal("确认"), "", "tpplus"),
            createContentButton(Component.literal("返回"), "", () -> super.switchContent(overworldSide))
    );
    //SubMenuType



    //ContentButtons
    List<PlaceListButton> contentButtons1 = Arrays.asList(
            createContentButton(Component.literal("二级菜单"), "", () -> openSubMenu(subMenuButtons1)),
            createContentButton(Component.literal("家"), "", "tpplus")
    );

    List<PlaceListButton> contentButtons2 = Arrays.asList(
            createContentButton(Component.literal("猪灵"), Resource.NETHER_icon, "tpplus"),
            createContentButton(Component.literal("堡垒"), Resource.NETHER_icon, "tpplus") //super.switchContent()
    );




    //初始化this.sideTypeMap
    public void initializeMaps()
    {
        //SideType
        overworldSide = SideType.withPlaceButtons(Resource.OVERWORLD_icon, "主世界", contentButtons1);
        netherSide = SideType.withPlaceButtons(Resource.NETHER_icon, "地狱", contentButtons2);

        sideTypeMap.put("主世界", overworldSide);
        sideTypeMap.put("地狱", netherSide);

        subMenuButtonsMap.put("第一个二级菜单",subMenuButtons1);

    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float a)
    {
        super.extractRenderState(context, mouseX, mouseY, a);
    }

    @Override
    public void init()
    {
        //初始化父类sideTypeMap
        initializeMaps();
        super.sideTypeMap = this.sideTypeMap;
        super.subMenuButtonsMap = this.subMenuButtonsMap;
//        super.currentScreen = NavigationScreen.ScreenType.; //这里要在NavigationScreen中添加枚举类型
        super.init();
    }
}
