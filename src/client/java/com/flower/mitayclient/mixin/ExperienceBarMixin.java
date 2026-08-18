package com.flower.mitayclient.mixin;

import com.flower.Mitayclient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.gui.contextualbar.ExperienceBarRenderer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceBarRenderer.class)
public class ExperienceBarMixin implements ContextualBarRenderer
{
    private static final Identifier BACKGROUND = Identifier.withDefaultNamespace("hud/experience_bar_background");
    private static final Identifier PROGRESS = Identifier.withDefaultNamespace("hud/experience_bar_progress");
    @Inject(at = @At("HEAD"), method = "extractBackground", cancellable = true)
    public void render(final GuiGraphicsExtractor context, final DeltaTracker deltaTracker, CallbackInfo ci)
    {
        if(Mitayclient.getConfig().isHotbarShown())
        {
            ci.cancel();
//            LocalPlayer clientPlayerEntity = Minecraft.getInstance().player;
//            int i = this.left(Minecraft.getInstance().getWindow());
//            int j = this.top(Minecraft.getInstance().getWindow());
//            int k = clientPlayerEntity.getXpNeededForNextLevel();
//            if (k > 0) {
//                int l = (int)(clientPlayerEntity.experienceProgress * 183.0F);
//                context.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, i, j-3, 182, 5);
//                if (l > 0) {
//                    context.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS, 182, 5, 0, 0, i, j-3, l, 5);
//                }
//            }
            render(context, 0);
        }else {
            render(context, -3);
        }
    }

    public void render(GuiGraphicsExtractor context, int biaY)
    {
        LocalPlayer clientPlayerEntity = Minecraft.getInstance().player;
        int i = this.left(Minecraft.getInstance().getWindow());
        int j = this.top(Minecraft.getInstance().getWindow());
        int k = clientPlayerEntity.getXpNeededForNextLevel();
        if (k > 0) {
            int l = (int)(clientPlayerEntity.experienceProgress * 183.0F);
            context.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, i, j-3 - biaY, 182, 5);
            if (l > 0) {
                context.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS, 182, 5, 0, 0, i, j-3 - biaY, l, 5);
            }
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker)
    {

    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker)
    {

    }
}
