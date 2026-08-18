package com.flower.mitayclient.mixin;

import com.flower.mitayclient.util.Data.PlayerDataHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.flower.mitayclient.util.MitayUtils.sendChatCommand;


@Mixin(ChatListener.class)
public class MessageHandlerMixin
{
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "handleSystemMessage(Lnet/minecraft/network/chat/Component;Z)V", at = @At("HEAD"), cancellable = true)
    public void onGameMessage(final Component message, final boolean remote, CallbackInfo ci)
    {
        String content = message.getString();

        String[] contents;


        if(content.contains("[Mitay Security]验证程序"))
        {
            ci.cancel();
            sendChatCommand("verifymod K8#m@8x!L3p$vNq");
        }
    }
}
