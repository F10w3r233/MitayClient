package com.flower.mitayclient.GUI.screen;

import com.flower.Mitayclient;
import com.flower.mitayclient.GUI.buttons.Accessibility.AccessibilityButton;
import com.flower.mitayclient.GUI.screen.BadgeUtil.networking.RequestBadgesPayload;
import com.flower.mitayclient.util.ModIdentifier;
import com.flower.mitayclient.util.Resource;

import java.util.HashMap;
import java.util.Map;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import org.lwjgl.glfw.GLFW;

public class NavigationScreen extends Screen
{
    public NavigationScreen(Component title)
    {
        super(title);
    }
    private static final Identifier SELECTED = Resource.SELECTED_icon;

    public Identifier navigation_bar = ModIdentifier.get("textures/gui/screen/side_bar/navigation_bar.png");


    int navigation_bar_Y = 0;

    enum ScreenType
    {
        PLACE(Resource.PLACE, "place", new PlaceListScreen()),
        PLAYER(Resource.PLAYER_LIST, "player_list", new TeleportScreen()),
        SETTINGS(Resource.SETTINGS, "settings", new SettingsScreen()),
        CHAT_HISTORY(Resource.CHAT_HISTORY, "chat_history", new ChatHistoryScreen()),
        PROFILE(Resource.PROFILE, "profile", new ProfileScreen());


        final Component displayName;
        final String icon;
        final Screen screen;
        ScreenType(Component displayName, String icon, Screen screen)
        {
            this.displayName = displayName;
            this.icon = icon;
            this.screen = screen;
        }
    }


    private final Map<ScreenType, Screen> screenMap = new HashMap<>();
    private final Map<ScreenType, AccessibilityButton> sideButtonMap = new HashMap<>();
    public ScreenType currentScreen = ScreenType.PLACE; // 默认为地点列表

    private AccessibilityButton createSideButton(Component name, String icon)
    {
        return AccessibilityButton.builder(name, button -> {

        }).type(icon).dimensions(0, 0, 210, 30).build();
    }

    private void switchScreen(ScreenType screenType)
    {
        currentScreen = screenType;
        Minecraft.getInstance().setScreen(screenType.screen);
    }


    public void renderIndicator(GuiGraphicsExtractor context)
    {
        AccessibilityButton selectedButton = sideButtonMap.get(currentScreen);
        if (selectedButton != null)
        {
            int selectedX = selectedButton.getX()-7;
            int selectedY = selectedButton.getY()+4;


            context.blit(RenderPipelines.GUI_TEXTURED, SELECTED,
                    selectedX, selectedY, 0, 0, 3, 7,
                    3, 7,
                    ARGB.color(1f, Mitayclient.getConfig().isDarkShown() ? CommonColors.HIGH_CONTRAST_DIAMOND : 0xFF2C91F5));
        }
    }

    @Override
    protected void init()
    {
        super.init();
//        drawables.clear();
//        elements.clear();

        ClientPlayNetworking.send(new RequestBadgesPayload());


        ScreenType[] screenTypes = ScreenType.values();


        int gap = 22;
        int iconHeight = 15;
        int startY = (Minecraft.getInstance().getWindow().getGuiScaledHeight()-((screenTypes.length-1)*gap + iconHeight * 1))/2;
        navigation_bar_Y = startY - 8;


        for (int i = 0; i < screenTypes.length; i++)
        {
            ScreenType screenType = screenTypes[i];
            AccessibilityButton button = AccessibilityButton.builder(screenType.displayName, button1 -> {
                switchScreen(screenType);
            }).type(screenType.icon).dimensions(13,startY + i * gap,16,16).build();



            screenMap.put(screenType, screenType.screen);
            sideButtonMap.put(screenType, button);

            addRenderableWidget(button);
//            addElement(button);
        }

    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float a)
    {
        //        for (Drawable drawable : this.elements)
//        {
//            drawable.render(context, mouseX, mouseY, deltaTicks);
//        }
        context.blit(RenderPipelines.GUI_TEXTURED, navigation_bar, 3, navigation_bar_Y, 0,0,35,122,35,122 , ARGB.color(0.87f, Mitayclient.getConfig().isDarkShown() ? CommonColors.DARK_GRAY : CommonColors.WHITE));
        renderIndicator(context);
        super.extractRenderState(context, mouseX, mouseY, a);
    }

    @Override
    public boolean keyPressed(KeyEvent input)
    {
        if(input.input() == GLFW.GLFW_KEY_E)
        {
            Minecraft.getInstance().player.closeContainer();
            return true;
        }
        return super.keyPressed(input);
    }
}
