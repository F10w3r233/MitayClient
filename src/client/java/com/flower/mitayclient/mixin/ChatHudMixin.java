package com.flower.mitayclient.mixin;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import static com.flower.mitayclient.util.ChatHistory.ChatKeywordDetector.*;
import static com.flower.mitayclient.util.NameFinder.findPureNames;
import static com.flower.mitayclient.util.NameFinder.getHeadX;
import static com.flower.mitayclient.util.Skin.SkinCacheHelper.renderHead;

@Mixin(ChatComponent.class)
public class ChatHudMixin
{

    @Shadow private final List<GuiMessage.Line> trimmedMessages = Lists.newArrayList();
    @Shadow public boolean isChatFocused() {
        return Minecraft.getInstance().screen instanceof ChatScreen;
    }
    @Shadow public int getHeight() {
        return getHeight(this.isChatFocused() ? Minecraft.getInstance().options.chatHeightFocused().get() : Minecraft.getInstance().options.chatHeightUnfocused().get());
    }
    @Shadow public static int getHeight(double heightOption)
    {
        return Mth.floor(heightOption * 160.0 + 20.0);
    }
    @Shadow public int getWidth() {
        return getWidth(Minecraft.getInstance().options.chatWidth().get());
    }
    @Shadow public static int getWidth(double widthOption) {
        int i = 320;
        int j = 40;
        return Mth.floor(widthOption * 280.0 + 40.0);
    }
    private double getScale() {
        return Minecraft.getInstance().options.chatScale().get();
    }
    @Shadow private int getLineHeight() {
        Objects.requireNonNull(Minecraft.getInstance().font);
        return (int)(9.0 * (Minecraft.getInstance().options.chatLineSpacing().get() + 1.0));
    }
    @Shadow public int getLinesPerPage() {
        return this.getHeight() / this.getLineHeight();
    }
    @Shadow private int chatScrollbarPos;

    @Unique
    public float calculate(GuiMessage.Line message, int currentTickTime)
    {
        int tickDelta = currentTickTime - message.addedTime();
        double t = (double) tickDelta / 200.0;
        t = 1.0 - t;
        t *= 10.0;
        t = Mth.clamp(t, 0.0, 1.0);
        t *= t;
        return (float) t;
    }



    //====================================================RENDER==============================================================================================================
    @Inject(at = @At("RETURN"), method = "extractRenderState", cancellable = true)
    public void render(final GuiGraphicsExtractor context, final Font font, final int ticks, final int mouseX, final int mouseY, final ChatComponent.DisplayMode displayMode, final boolean changeCursorOnInsertions, CallbackInfo ci)
    {
        final int[] y = new int[1];
        final int chatBottom = Mth.floor((float)(context.guiHeight() - 40) / this.getScale());
        double chatLineSpacing = Minecraft.getInstance().options.chatLineSpacing().get();
        final int MESSAGE_HEIGHT = 9;
        final int entryHeight = (int)((double) MESSAGE_HEIGHT * (chatLineSpacing + 1.0));
        boolean isForeground = displayMode.foreground;

        int linesPerPage = getLinesPerPage(); // 或 getLinesPerPage()
        int visibleStart = chatScrollbarPos;
        int visibleEnd = Math.min(trimmedMessages.size(), visibleStart + linesPerPage);

        for (int i = visibleStart; i < visibleEnd; i++)
        {
            GuiMessage.Line trimmedMessage = trimmedMessages.get(i);
            int lineIndex = i - visibleStart; // 相对于第一个可见消息的索引（0-based）

            float alpha = isForeground ? 1.0f : this.calculate(trimmedMessage, ticks);
            int entryBottom = chatBottom - lineIndex * entryHeight;
            int entryTop = entryBottom - entryHeight;
            int yPos = entryTop + 1;


            String content = getString(trimmedMessage.content());
            if(!Minecraft.getInstance().isLocalServer())
            {
                //=============================================玩家名=====================================================
//                if(containsPlayerName(content))
//                {
//                    //同时有两个玩家
//                    if(containsTwoPlayerNames(content))
//                    {
//                        for(String name : getContainedPlayerNameList(content))
//                        {
//                            boolean hasDisplayName = name != null ? name.contains("[") : false;
//                            renderHead(context, hasDisplayName ? name.split("]")[1].trim() : name, getIconX(content, name) + 4, yPos - 1, 8, ARGB.color(alpha, CommonColors.WHITE));
//                        }
//                    }else
//                    {
//                        //一个玩家
//                        String name = getContainedPlayerName(content);
//                        boolean hasDisplayName = name != null && name.contains("]");
//                        int x = getIconX(content, name);
////                        System.out.println(name);
//                        renderHead(context, hasDisplayName ? name.split("]")[1].trim() : name, getIconX(content, name) + 4, yPos - 1, 8, ARGB.color(alpha, CommonColors.WHITE));
//                    }
//                }

                List<String> nameList = findPureNames(content);
                if (!nameList.isEmpty())
                {
                    for (String pureName : nameList)
                    {
                        int headX = getHeadX(content, pureName);
                        renderHead(context, pureName, headX, yPos - 1, 8, ARGB.color(alpha, CommonColors.WHITE));
                    }
                }

                //地点名
                if(containsPlace(content))
                {
                    Identifier icon = getPlaceIcon(getContainedPlaceName(content));
                    int startIndex = content.indexOf(getContainedPlaceName(content));
                    String prefix = content.substring(0, startIndex);
                    int iconX = Minecraft.getInstance().font.width(prefix)-10; //显示在<>前面:MinecraftClient.getInstance().textRenderer.getWidth(prefix)-14
                    context.blit(RenderPipelines.GUI_TEXTURED, icon, iconX+4, yPos, 0,0,8,8,8,8, ARGB.color(alpha, CommonColors.WHITE));
                }

                //维度名
                if(containsDimensionName(content))
                {
                    if(!content.contains("末地主岛"))
                    {
                        Identifier icon = getDimensionIcon(getContainedDimensionName(content));
                        int startIndex = content.indexOf(getContainedDimensionName(content));
                        String prefix = content.substring(0, startIndex);
                        int iconX = Minecraft.getInstance().font.width(prefix)-10;
                        context.blit(RenderPipelines.GUI_TEXTURED, icon, iconX+4, yPos, 0,0,8,8,8,8, ARGB.color(alpha, CommonColors.WHITE));
                    }
                }
            }
        }
    }


    @Unique
    public String getString(FormattedCharSequence text) {
        StringBuilder stringBuilder = new StringBuilder();
        text.accept((charIndex, style, codePoint) -> {
            stringBuilder.appendCodePoint(codePoint);
            return true;
        });
        return stringBuilder.toString(); // 直接返回，跳过 Component.literal
    }

    @Unique
//    private static int getIconX(String message, String name)
//    {
//        int startIndex = message.indexOf(name);
//        // 计算从消息开头到玩家名字开始（即三个空格的位置）的字符串
//        String prefix = message.substring(0, startIndex);
//        int iconX = Minecraft.getInstance().font.width(prefix)-10;
//        return iconX;
//    }

    private static int getIconX(String message, String name) {
        int startIndex = message.indexOf(name);
        if (startIndex == -1) {
            return 0; // 或根据需求返回默认值
        }
        String prefix = message.substring(0, startIndex);
        return Minecraft.getInstance().font.width(prefix) - 10;
    }
}
