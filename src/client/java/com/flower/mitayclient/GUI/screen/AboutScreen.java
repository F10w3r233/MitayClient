package com.flower.mitayclient.GUI.screen;

import com.flower.Mitayclient;
import com.flower.mitayclient.util.ModIdentifier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class AboutScreen extends Screen
{
    Identifier bright = ModIdentifier.get("textures/gui/screen/about.png");
    Identifier dark = ModIdentifier.get("textures/gui/screen/about_dark.png");
    public AboutScreen(Component title)
    {
        super(title);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float a)
    {
        Identifier bg;
        if(Mitayclient.getConfig().isDarkShown())
        {
            bg = dark;
        }else bg = bright;
        super.extractRenderState(context, mouseX, mouseY, a);
        context.blit(RenderPipelines.GUI_TEXTURED, bg, -5  ,-5,0,0,context.guiWidth()+10, context.guiHeight()+10, context.guiWidth()+10, context.guiHeight()+10);    }



    @Override
    protected void init()
    {

        super.init();
    }
}
