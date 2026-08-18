package com.flower.mitayclient.GUI.buttons.Menu;

import com.flower.Mitayclient;
import com.flower.mitayclient.util.ModIdentifier;
import com.flower.mitayclient.util.Resource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.Nullable;


@Environment(EnvType.CLIENT)
public abstract class MenuPressable extends AbstractWidget
{
    private static final Identifier BUTTON = ModIdentifier.get("textures/gui/widget/menu_button/button_large.png");
    private static final Identifier BUTTON_DARK = ModIdentifier.get("textures/gui/widget/menu_button/button_large_dark.png");
    private static final Identifier BUTTON_FOCUS = ModIdentifier.get("textures/gui/widget/menu_button/button_large_hovered.png");
    private static final Identifier BUTTON_FOCUS_DARK = ModIdentifier.get("textures/gui/widget/menu_button/button_large_hovered_dark.png");




    public MenuPressable(int i, int j, int k, int l, Component text)
    {
        super(i, j, k, l, text);
    }
    public abstract void onPress();


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

        int textureColor;
        float alpha;
        if(Mitayclient.getConfig().isDarkShown())
        {
            alpha = 0.85f;
            textureColor = CommonColors.GRAY;
        }else {
            alpha = 0.75f;
            textureColor = CommonColors.WHITE;
        }
        context.blit(RenderPipelines.GUI_TEXTURED, button, this.getX(), this.getY(), 0, 0,162, 27,162,27, ARGB.color(alpha, textureColor));

        int color;
        if(Mitayclient.getConfig().isDarkShown())
        {
            color = CommonColors.WHITE;
        }else color = CommonColors.WHITE;

//        setTooltip(Tooltip.create());
        context.text(Minecraft.getInstance().font, this.getMessage(), (width- Resource.getStringWidth(getMessage().getString())+4)/2+getX(), getY()+8, color);
    }

    @Override
    public void setTooltip(@Nullable Tooltip tooltip)
    {
        super.setTooltip(tooltip);
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

