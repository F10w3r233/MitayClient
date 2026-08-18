package com.flower.mitayclient.GUI.screen;

import com.flower.Mitayclient;
import com.flower.mitayclient.GUI.buttons.Switch.SwitchButton;
import com.flower.mitayclient.GUI.screen.SideBarUtil.SideType;
import com.flower.mitayclient.util.ModIdentifier;
import com.flower.mitayclient.util.Resource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SettingsScreen extends SideBarScreen
{
    int rectX;
    int rectY;
    private static final Identifier APPEARANCE = ModIdentifier.get("textures/gui/widget/accessibility_button/wallpaper.png");
    public SettingsScreen()
    {
        super(Component.empty(), Type.OPTION);
    }

    SideType appearanceSide;
    SideType functionSide;


    Map<String, SideType> sideTypeMap = new LinkedHashMap<>();




    //ContentButtons
    List<SwitchButton> switchButtons1 = Arrays.asList(
            createSettingsButton(Component.literal("深色UI"), Mitayclient.getConfig().isDarkShown(), () -> Mitayclient.getConfig().toggleDarkShown()),
            createSettingsButton(Component.literal("扁平化物品栏"), Mitayclient.getConfig().isHotbarShown(), () -> Mitayclient.getConfig().toggleHotbarShown()),
            createSettingsButton(Component.literal("扁平化药水效果UI"), Mitayclient.getConfig().isEffectShown(), () -> Mitayclient.getConfig().toggleEffectShown())
    );

    List<SwitchButton> switchButtons2 = Arrays.asList(
            createSettingsButton(Component.literal("显示正在播放的音乐（已半废弃）"), Mitayclient.getConfig().isMusicShown(), () -> Mitayclient.getConfig().toggleMusicShown()),
            createSettingsButton(Component.literal("显示顶部玩家信息栏"), Mitayclient.getConfig().isTopShown(), () -> Mitayclient.getConfig().toggleTopShown()),
            createSettingsButton(Component.literal("显示漏斗内容物"), Mitayclient.getConfig().isContainerShown(), () -> Mitayclient.getConfig().toggleContainerShown()),
            createSettingsButton(Component.literal("显示工具信息"), Mitayclient.getConfig().isToolbarShown(), () -> Mitayclient.getConfig().toggleToolbarShown())
    );




    //初始化this.sideTypeMap
    public void initializeMaps()
    {
        //SideType
        appearanceSide = SideType.withSwitchButtons(APPEARANCE, "外观", switchButtons1);
        functionSide = SideType.withSwitchButtons(Resource.CREATIVE_WORLD_icon, "功能", switchButtons2);

        sideTypeMap.put("外观", appearanceSide);
        sideTypeMap.put("功能", functionSide);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float a)
    {
        rectX = (Minecraft.getInstance().getWindow().getGuiScaledWidth()-382)/2+7+115+5;
        rectY = (Minecraft.getInstance().getWindow().getGuiScaledHeight()-292)/2+10;
        super.extractRenderState(context, mouseX, mouseY, a);
    }

    @Override
    public void init()
    {
        //初始化父类sideTypeMap
        initializeMaps();
        super.sideTypeMap = this.sideTypeMap;
        super.currentScreen = ScreenType.SETTINGS; //这里要在NavigationScreen中添加枚举类型
        super.init();
    }
}
