package com.flower.mitayclient.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin
{

    @Shadow @Final protected Component title;

    @Inject(at = @At("HEAD"), method = "extractBackground", cancellable = true)
    public void render(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a, CallbackInfo ci)
    {
        if(title.contains(Component.literal("Place_List1")) || title.contains(Component.literal("Teleport_Player")) || title.contains(Component.literal("Admin")) || title.contains(Component.literal("PlayerInfo")) || title.contains(Component.literal("ChatHistory")))
            ci.cancel();
    }
}
