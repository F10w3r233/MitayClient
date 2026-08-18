package com.flower.mitayclient.GUI.screen;

import com.flower.Mitayclient;
import com.flower.mitayclient.GUI.buttons.Menu.MenuButton;
import com.flower.mitayclient.GUI.buttons.Wallpaper.WallpaperButton;
import com.flower.mitayclient.util.ModIdentifier;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class WallpaperScreen extends Screen implements ContainerEventHandler
{
    //===============================壁纸路径===================================
    //mc
    public static final Identifier bg0 = ModIdentifier.get("textures/gui/bg/bg0.png");
    public static final Identifier bg1 = ModIdentifier.get("textures/gui/bg/bg1.png");
    public static final Identifier bg2 = ModIdentifier.get("textures/gui/bg/bg2.png");
    public static final Identifier bg3 = ModIdentifier.get("textures/gui/bg/bg3.png");
    public static final Identifier bg4 = ModIdentifier.get("textures/gui/bg/bg4.png");
    public static final Identifier bg5 = ModIdentifier.get("textures/gui/bg/bg5.png");

    //anime
    public static final Identifier Ani0 = ModIdentifier.get("textures/gui/wallpaper/anime/1.png");
    public WallpaperScreen()
    {
        super(Component.literal("wallpaper"));
//        initializeWallpaperButtons();
    }

    static ServerStatusPinger pinger = new ServerStatusPinger();

    //-------------------------------------------------TYPE---------------------------------------------
    public enum Type
    {
        MC("MC"),
        ANIME("动漫");

        final Component displayName;

        Type(String displayName)
        {
            this.displayName = Component.literal(displayName);
        }
    }


    private Type currentType = Type.MC;


    private final Map<Type, MenuButton> typeButtons = new HashMap<>();
    //按钮映射
    private final Map<Type, List<WallpaperButton>> wallpaperButtons = new HashMap<>();

    public static int wallpaperIndex;


    //-------------------------------------------Identifiers------------------------------------------------------------------
    public static final Identifier Background = ModIdentifier.get("textures/gui/screen/wallpaper_screen/white_background.png");
    public static final Identifier Background_Dark = ModIdentifier.get("textures/gui/screen/wallpaper_screen/black_bg.png");

    public static Identifier getCurrentWallpaper(int wallpaperIndex)
    {
        return switch (wallpaperIndex)
        {
            case 0 -> currentWallpaper = bg0;
            case 1 -> currentWallpaper = bg1;
            case 2 -> currentWallpaper = bg2;
            case 3 -> currentWallpaper = bg3;
            case 4 -> currentWallpaper = bg4;
            case 5 -> currentWallpaper = bg5;
            default -> bg1;
        };
    }

    public static Identifier currentWallpaper = getCurrentWallpaper(wallpaperIndex);

    public void initializeWallpaperButtons()
    {
        if(Mitayclient.getConfig().isAnimeShown())
        {
            currentType = Type.ANIME;
        }else currentType = Type.MC;

        wallpaperIndex = Mitayclient.getConfig().getBG_Index();

        switch(wallpaperIndex)
        {
            case 0 -> currentWallpaper = bg0;
            case 1 -> currentWallpaper = bg1;
            case 2 -> currentWallpaper = bg2;
            case 3 -> currentWallpaper = bg3;
            case 4 -> currentWallpaper = bg4;
            case 5 -> currentWallpaper = bg5;

        }

        wallpaperButtons.clear();

        List<WallpaperButton> mcWallpapers = Arrays.asList(
                createWallpaperButton(bg0,0),
                createWallpaperButton(bg1,1),
                createWallpaperButton(bg2,2),
                createWallpaperButton(bg3,3),
                createWallpaperButton(bg4,4),
                createWallpaperButton(bg5,5)

        );
        wallpaperButtons.put(Type.MC, mcWallpapers);

        List<WallpaperButton> animeWallpapers = Arrays.asList(
                createWallpaperButton(bg2, 0)
        );
        wallpaperButtons.put(Type.ANIME, animeWallpapers);

    }

    public void switchType(Type type)
    {
        //初始化壁纸type
        currentType = type;
        showWallpapers(wallpaperButtons.get(type));
    }

    private void showWallpapers(List<WallpaperButton> wallpapers)
    {
        // 移除旧的wallpaperButton
        for (GuiEventListener child : new ArrayList<>(children()))
        {
            if (child instanceof WallpaperButton)
            {
                removeWidget(child);
            }
        }

        // 添加新的wallpaperButton
        int startX = 200;
        int startY = 50;

        int index = 0;
        for (int i = 0; i < wallpapers.size(); i++)
        {
            int line = 0;
            if(i % 5 != 0)
            {
                line = i / 5;
            }

            if(i == 4 || i == 8)
            {
                index = 0;
                startY = 130;
            }
            WallpaperButton button = wallpapers.get(i);
            button.setPosition(startX + (index * 150), startY + (line * 90));
            addRenderableWidget(button);
            index++;
        }
    }


    //-----------------------------------------render-------------------------------------------------------------------
    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta)
    {
        Identifier bg;
        if(Mitayclient.getConfig().isDarkShown())
        {
            bg = Background_Dark;
        }else bg = Background;
        context.blit(RenderPipelines.GUI_TEXTURED, bg, 0,0,0,0, Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight(), Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight());
//        for(Renderable drawable : renderables)
//        {
//            drawable.render(context, mouseX, mouseY, delta);
//        }

        super.extractRenderState(context, mouseX, mouseY, delta);
    }


    //---------------------------------------init------------------------------------------------------
    @Override
    protected void init()
    {
        super.init();
        clearWidgets();
        typeButtons.clear();
        wallpaperButtons.clear();

        initializeWallpaperButtons();

        Type[] types = Type.values();
        for (int i = 0; i < types.length; i++)
        {
            Type type = types[i];
            final Type currentTypeRef = type; // 用于lambda表达式

            MenuButton typeButton = MenuButton.builder(type.displayName, button ->
            {
                switchType(currentTypeRef);
                if(button.getMessage().getString().equals("MC"))
                {
                    if(Mitayclient.getConfig().isAnimeShown())
                    {
                        Mitayclient.getConfig().toggleAnimeShown();
                    }
                }else if(button.getMessage().getString().equals("动漫")){
                    if(!Mitayclient.getConfig().isAnimeShown())
                    {
                        Mitayclient.getConfig().toggleAnimeShown();
                    }
                }

            }).dimensions(8, 50 + (i * 42), 162, 27).build();

            typeButtons.put(type, typeButton);
            addRenderableWidget(typeButton);
        }

        switchType(currentType);
    }

    public WallpaperButton createWallpaperButton(Identifier wallpaper, int index)
    {
        return WallpaperButton.builder(Component.literal(""), button ->
        {
            wallpaperIndex = index;
            Mitayclient.getConfig().setBgIndex(index);
        }).wallpaper(wallpaper).dimensions(0,0,100,50).build();
    }
}
