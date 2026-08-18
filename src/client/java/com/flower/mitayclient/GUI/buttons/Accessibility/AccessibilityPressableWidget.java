package com.flower.mitayclient.GUI.buttons.Accessibility;

import com.flower.mitayclient.util.ModIdentifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;


@Environment(EnvType.CLIENT)
public abstract class AccessibilityPressableWidget extends AbstractWidget
{


    int size = 15;
    String name;
    private static final Identifier LANGUAGE = ModIdentifier.get("textures/gui/widget/accessibility_button/language.png");
    private static final Identifier ACCESSIBILITY = ModIdentifier.get("textures/gui/widget/accessibility_button/accessibility.png");
    private static final Identifier WALLPAPER = ModIdentifier.get("textures/gui/widget/accessibility_button/wallpaper.png");
    private static final Identifier ABOUT = ModIdentifier.get("textures/gui/widget/accessibility_button/about.png");
    public static final Identifier TICK = ModIdentifier.get("textures/gui/widget/accessibility_button/tick.png");
    private static final Identifier REFRESH = ModIdentifier.get("textures/gui/widget/accessibility_button/refresh.png");
    private static final Identifier ADD = ModIdentifier.get("textures/gui/widget/accessibility_button/add.png");



    public static final Identifier PLACE = ModIdentifier.get("textures/gui/screen/side_bar/icons/place.png");
    public static final Identifier PLAYER_LIST = ModIdentifier.get("textures/gui/screen/side_bar/icons/player_list.png");
    private static final Identifier SETTINGS = ModIdentifier.get("textures/gui/screen/side_bar/icons/settings.png");
    private static final Identifier CHAT_HISTORY = ModIdentifier.get("textures/gui/screen/side_bar/icons/chat_history.png");
    private static final Identifier PROFILE = ModIdentifier.get("textures/gui/screen/side_bar/icons/profile.png");

    Identifier identifier;



    String type;

    public AccessibilityPressableWidget(int i, int j, int k, int l, Component text, String type)
    {
        super(i, j, k, l, text);
        this.type = type;
    }
    public abstract void onPress();



    int opacity = 25;

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float a)
    {
        switch (type)
        {
            case "language" ->
            {
                identifier = LANGUAGE;
                name = "语言";
            }
            case "accessibility" ->
            {
                identifier = ACCESSIBILITY;
                name = "通用";
            }
            case "wallpaper" ->
            {
                identifier = WALLPAPER;
                name = "壁纸";
            }
            case "about" ->
            {
                identifier = ABOUT;
                name = "关于";
            }
            case "place" ->
            {
                identifier = PLACE;
                name = "";
            }
            case "player_list" ->
            {
                identifier = PLAYER_LIST;
                name = "";
            }
            case "settings" ->
            {
                identifier = SETTINGS;
                name = "";
            }
            case "chat_history" ->
            {
                identifier = CHAT_HISTORY;
                name = "";
            }
            case "profile" ->
            {
                identifier = PROFILE;
                name = "";
            }
            case "tick" ->
            {
                identifier = TICK;
                name = "";
            }
            case "refresh" ->
            {
                identifier = REFRESH;
                name = "刷新";
            }
            case "add" ->
            {
                identifier = ADD;
                name = "添加一个地点";
            }

        }

        if(isHovered)
        {
            if(size <= 17)
                size++;
            if(opacity < 255)
            {
                if(opacity + 23 > 255)
                {
                    opacity += 255-opacity;
                }else opacity += 23;
            }
        }else
        {
            if(size > 15)
                size--;
            if(opacity > 25)
            {
                opacity -= 15;
            }
        }

        if(identifier != null)
        {
            context.blit(RenderPipelines.GUI_TEXTURED,identifier, this.getX(), this.getY(), 0, 0, size,size,size,size);
        }
        if(name != null)
            context.text(Minecraft.getInstance().font, name, this.getX()+1, this.getY()-11, ARGB.color(opacity, 16777215),false);
    }

    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        this.onPress();
    }
}

