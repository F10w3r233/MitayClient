package com.flower.mitayclient.GUI.buttons.Switch;

import com.flower.Mitayclient;
import com.flower.mitayclient.util.ModIdentifier;
import com.mojang.blaze3d.systems.RenderSystem;
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


@Environment(EnvType.CLIENT)
public abstract class SwitchPressable extends AbstractWidget
{
    private static final Identifier ON = ModIdentifier.get("textures/gui/widget/switch_button/on.png");
    private static final Identifier OFF = ModIdentifier.get("textures/gui/widget/switch_button/off.png");



    boolean flag;
    public SwitchPressable(int i, int j, int k, int l, Component text, boolean flag)
    {
        super(i, j, k, l, text);
        this.flag = flag;
    }
    public abstract void onPress();


    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float a)
    {
        int color = 0;
        if(Mitayclient.getConfig().isDarkShown())
        {
            color = CommonColors.WHITE;
        }else color = CommonColors.BLACK;


        context.text(Minecraft.getInstance().font, message, this.getX(),this.getY(), color, false);

        if(this.flag)
        {
            context.blit(RenderPipelines.GUI_TEXTURED, ON, this.getX()+161,this.getY(),0,0,246/8,132/8,246/8,132/8);
        }else {
            context.blit(RenderPipelines.GUI_TEXTURED, OFF, this.getX()+161,this.getY(),0,0,246/8,132/8,246/8,132/8);
        }
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

