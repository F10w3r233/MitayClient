package com.flower.mitayclient.GUI.buttons.PlaceList.Small;

import com.flower.Mitayclient;
import com.flower.mitayclient.util.ModIdentifier;
import com.flower.mitayclient.util.Resource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;

/**
 * A pressable widget has a press action. It is pressed when it is clicked. It is
 * also pressed when enter or space keys are pressed when it is selected.
 */
@Environment(EnvType.CLIENT)
public abstract class SmallPressable extends AbstractWidget
{

//    private static final ButtonTextures TEXTURES = new ButtonTextures(
//            Identifier.of("mitayclient","textures/gui/sprites/screen/button_small.png"), Identifier.of("mitayclient","textures/gui/sprites/screen/button_large.png"), Identifier.of("mitayclient","textures/gui/sprites/screen/button_small_pressed.png")
//    );


    static Identifier NPC = ModIdentifier.get("textures/gui/sprites/hud/admin/npc.png");

    private static final Identifier END = Resource.END_icon;
    private static final Identifier NETHER = Resource.NETHER_icon;
    private static final Identifier OVERWORLD = Resource.OVERWORLD_icon;
    private static final Identifier CREATIVE_WORLD = Resource.CREATIVE_WORLD_icon;


    private static final Identifier ITEMS = ModIdentifier.get("textures/gui/hud/places/items.png");
    private static final Identifier MOBS = ModIdentifier.get("textures/gui/hud/places/mobs.png");
    private static final Identifier GENERAL = ModIdentifier.get("textures/gui/hud/places/grass.png");






    private static final Identifier BUTTON = ModIdentifier.get("textures/gui/widget/side_bar_small/button_small.png");
    private static final Identifier BUTTON_DARK = ModIdentifier.get("textures/gui/widget/side_bar_small/button_small_dark.png");
    private static final Identifier BUTTON_FOCUS = ModIdentifier.get("textures/gui/widget/side_bar_small/button_small_hovered.png");
    private static final Identifier BUTTON_FOCUS_DARK = ModIdentifier.get("textures/gui/widget/side_bar_small/button_small_hovered_dark.png");




    String iconName;
    Identifier iconIdentifier;

    public SmallPressable(int i, int j, int k, int l, Component text, String iconName, Identifier iconIdentifier)
    {
        super(i, j, k, l, text);
        this.iconName = iconName;
        this.iconIdentifier = iconIdentifier;
    }
    public abstract void onPress();

    public static Identifier getIdentifier(String iconName)
    {
        if(iconName == null)
        {
            return null;
        }
        return switch (iconName) {
            case "end" -> END;
            case "overworld" -> OVERWORLD;
            case "nether" -> NETHER;
            case "creative" -> CREATIVE_WORLD;
            case "npc" -> NPC;

            default -> null;
        };
    }


    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float a)
    {
        Identifier button;
        if(Mitayclient.getConfig().isDarkShown())
        {
            if(this.isHovered)
            {
                button = BUTTON_FOCUS_DARK;
            }else button = BUTTON_DARK;
        }else {
            if(this.isHovered)
            {
                button = BUTTON_FOCUS;
            }else button = BUTTON;
        }

        if(this.isHovered)
        {
            context.blit(RenderPipelines.GUI_TEXTURED, button, this.getX(), this.getY(), 0, 0,112, 22,112,22);
        }


        if(getIdentifier(iconName) != null)
        {
            context.blit(RenderPipelines.GUI_TEXTURED,getIdentifier(iconName), this.getX()+8, this.getY()+4, 0, 0,14, 14,14,14);
        }else if(iconIdentifier != null)
        {
            context.blit(RenderPipelines.GUI_TEXTURED, iconIdentifier, this.getX()+8, this.getY()+4, 0, 0,14, 14,14,14);
        }

        int color;
        if(Mitayclient.getConfig().isDarkShown())
        {
            color = CommonColors.WHITE;
        }else color = 0xFF0B242E;

        context.text(Minecraft.getInstance().font, getMessage(), (width-Resource.getStringWidth(getMessage().getString())+6)/2+getX(), getY()+7, color, false);
    }

    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        this.onPress();
    }


//    @Override
//    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//        if (!this.active || !this.visible) {
//            return false;
//        } else if (KeyCodes.isToggle(keyCode)) {
//            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
//            this.onPress();
//            return true;
//        } else {
//            return false;
//        }
//    }
}

