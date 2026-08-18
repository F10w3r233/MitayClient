package com.flower.mitayclient.GUI.buttons.Wallpaper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public abstract class WallpaperPressable extends AbstractWidget
{
    Identifier wallpaper;

    public WallpaperPressable(int i, int j, int k, int l, Component text, Identifier wallpaper)
    {
        super(i, j, k, l, text);
        this.wallpaper = wallpaper;
    }
    public abstract void onPress();

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float a)
    {
        if(wallpaper != null)
        {
            context.blit(RenderPipelines.GUI_TEXTURED, wallpaper, this.getX(), this.getY(), 0,0,120,60,120,60);
        }

        int i = this.active ? 16843008 : 16777215;
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

