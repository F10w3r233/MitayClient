package com.flower.mitayclient.GUI.buttons.TeleportPlayer;


import com.flower.Mitayclient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public abstract class TeleportPlayerPressable extends AbstractWidget
{



    private static final Identifier BUTTON = Identifier.fromNamespaceAndPath("mitayclient","textures/gui/sprites/widget/button_large.png");
    private static final Identifier BUTTON_DARK = Identifier.fromNamespaceAndPath("mitayclient","textures/gui/sprites/widget/button_large_dark.png");
    private static final Identifier BUTTON_FOCUS = Identifier.fromNamespaceAndPath("mitayclient","textures/gui/sprites/widget/button_large_hovered.png");
    private static final Identifier BUTTON_FOCUS_DARK = Identifier.fromNamespaceAndPath("mitayclient","textures/gui/sprites/widget/button_large_hovered_dark.png");




    PlayerInfo player;

    public TeleportPlayerPressable(int i, int j, int k, int l, Component text, PlayerInfo player)
    {
        super(i, j, k, l, text);
        this.player = player;
    }
    public abstract void onPress();


    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float a)
    {
        Minecraft client = Minecraft.getInstance();

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



        context.blit(RenderPipelines.GUI_TEXTURED,button, this.getX(), this.getY(), 0, 0,160, 34,160,34);
        drawProfile(context,client);

//        drawLabel(context.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
    }


    private void drawProfile(GuiGraphicsExtractor context, Minecraft client)
    {
        if(player != null)
        {
            PlayerFaceExtractor.extractRenderState(context, player.getSkin(), getX()+5+5, getY()+5+3,16);
            if(player.getTabListDisplayName() != null)
            {
                context.text(client.font, player.getTabListDisplayName(), getX()+5+16+5+5+2, getY()+5+7, 0xFFDCDCDC);
            }else context.text(client.font, player.getProfile().name(), getX()+5+16+5+5+2, getY()+5+7, 0xFFDCDCDC);
        }
    }

//    protected void drawLabel(ActiveTextCollector drawer) {
//        this.renderScrollingStringOverContents(drawer, this.getMessage(), 2);
//    }

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

