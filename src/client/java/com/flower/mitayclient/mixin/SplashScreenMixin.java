package com.flower.mitayclient.mixin;

import com.flower.mitayclient.util.Resource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LoadingOverlay.class)
public class SplashScreenMixin
{
    @Shadow private final boolean fadeIn;
    @Shadow private float currentProgress;
    @Shadow private long fadeOutStart = -1L;
    @Shadow private long fadeInStart = -1L;
    @Shadow private final ReloadInstance reload;


    public SplashScreenMixin(boolean reloading, float progress, ReloadInstance reload)
    {
        this.fadeIn = reloading;
        this.currentProgress = progress;
        this.reload = reload;
    }

    @Inject(at = @At("INVOKE"), method = "extractRenderState")
    public void render(final GuiGraphicsExtractor context, final int mouseX, final int mouseY, final float a, CallbackInfo ci)
    {
        long l = Util.getMillis();

        int scaledWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();

        float f = this.fadeOutStart > -1L ? (float)(l - this.fadeOutStart) / 1000.0F : -1.0F;
        float g = this.fadeInStart > -1L ? (float)(l - this.fadeInStart) / 500.0F : -1.0F;
        float h;
        if (f >= 1.0F)
        {
            h = 1.0F - Mth.clamp(f - 1.0F, 0.0F, 1.0F);
        } else if (this.fadeIn)
        {
            h = Mth.clamp(g, 0.0F, 1.0F);
        } else
        {
            h = 1.0F;
        }

        int p = (int)((double)context.guiHeight() * 0.5);
        double d = Math.min((double)context.guiWidth() * 0.75, context.guiHeight()) * 0.25;
        int q = (int)(d * 0.5);
        int s = ARGB.white(h);

        context.blit(RenderPipelines.GUI_TEXTURED, Resource.MITAY, (scaledWidth-114)/2,p-q+150,0,0,114,17,114,17,s);
    }
}
