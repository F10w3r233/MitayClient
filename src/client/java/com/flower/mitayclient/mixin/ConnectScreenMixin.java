package com.flower.mitayclient.mixin;

import com.flower.mitayclient.GUI.HUD.ToolBarHudRenderer;
import com.flower.mitayclient.util.Resource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class ConnectScreenMixin extends Screen
{
    @Unique
    private static final Identifier BACKGROUND_TEXTURES = Identifier.fromNamespaceAndPath("mitayclient","textures/gui/background6.png");

    protected ConnectScreenMixin(Component title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "extractRenderState")
    public void extractRenderState(final GuiGraphicsExtractor context, final int mouseX, final int mouseY, final float a, CallbackInfo ci)
    {
        context.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURES, 0,0,0,0,this.width,this.height,this.width,this.height);
        ToolBarHudRenderer.drawScaledText(context, Minecraft.getInstance().font, Component.literal("正在连接到 Mitay..."), (int)(context.guiWidth()- Resource.getStringWidth("正在连接到 Mitay...")*1.5)/2, context.guiHeight()/2-5, 1.5f, CommonColors.WHITE, true);
    }
}
