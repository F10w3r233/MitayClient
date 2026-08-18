package com.flower.mitayclient.GUI.buttons.Badge;

import com.flower.mitayclient.GUI.screen.BadgeUtil.networking.BadgesPayload;
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
public abstract class BadgePressable extends AbstractWidget
{


    int size = 30;
    int MAX_SIZE = 43;
    int MIN_SIZE = 30;
    String name;

    Identifier identifier;



    String type;
    BadgesPayload.BadgeInfo badgeInfo;

    public BadgePressable(int i, int j, int k, int l, Component text, String type, BadgesPayload.BadgeInfo badgeInfo)
    {
        super(i, j, k, l, text);
        this.type = type;
        this.badgeInfo = badgeInfo;
    }
    public abstract void onPress();



    int opacity = 45;

    public Identifier getBadgeTexture(String name)
    {
        Identifier texture = null;
        switch (name)
        {
            case "\"刷铁机\"勋章" -> texture = ModIdentifier.get("textures/gui/screen/profile/badge/iron.png");
            case "\"建筑师\"勋章" -> texture = ModIdentifier.get("textures/gui/screen/profile/badge/architect.png");
            case "\"紫水晶簇\"勋章" -> texture = ModIdentifier.get("textures/gui/screen/profile/badge/amethyst_cluster.png");
            case "\"监守者\"勋章" -> texture = ModIdentifier.get("textures/gui/screen/profile/badge/warden.png");
        }
        return texture;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float a)
    {
        int biaX = 0;
        int biaY = 0;
        if (badgeInfo.name().equals("\"监守者\"勋章"))
        {
            size = 43;
            MAX_SIZE = 100;
            MIN_SIZE = 43;
            biaX = 10;
            biaY = 7;
        }
        if(isHovered)
        {
            if(size <= MAX_SIZE)
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
            if(size > MIN_SIZE)
                size--;
            if(opacity > 25)
            {
                opacity -= 15;
            }
        }

        if(getBadgeTexture(badgeInfo.name()) != null)
            context.blit(RenderPipelines.GUI_TEXTURED,getBadgeTexture(badgeInfo.name()), this.getX(), this.getY()-biaY, 0, 0, size,size,size,size);
//        if(badgeInfo.name() != null)
//            context.text(Minecraft.getInstance().font, badgeInfo.name(), this.getX()+1, this.getY()-11, ARGB.color(opacity, 16777215),false);
    }

    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        this.onPress();
    }
}

