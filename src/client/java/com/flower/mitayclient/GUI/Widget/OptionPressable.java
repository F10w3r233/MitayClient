package com.flower.mitayclient.GUI.Widget;

import com.flower.Mitayclient;
import com.flower.mitayclient.GUI.buttons.Switch.SwitchButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public abstract class OptionPressable extends AbstractWidget
{
    String description;
    public OptionPressable(int x, int y, int width, int height, String description)
    {
        super(x, y, width, height, Component.empty());
        this.description = description;
    }



    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        int color = 0;
        if(Mitayclient.getConfig().isDarkShown())
        {
            color = CommonColors.WHITE;
        }else color = CommonColors.BLACK;


        graphics.text(Minecraft.getInstance().font, description, 1,1, color);
    }

    public abstract void onPress();
    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        this.onPress();
    }
    @Override
    protected void updateWidgetNarration(NarrationElementOutput output)
    {

    }

}
