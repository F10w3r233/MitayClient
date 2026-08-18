package com.flower.mitayclient.mixin;

import com.flower.Mitayclient;
import com.flower.mitayclient.GUI.HUD.Tab.network.TabInfoCache;
import com.flower.mitayclient.GUI.HUD.Tab.network.TabInfoPayload;
import com.flower.mitayclient.util.ModIdentifier;
import com.flower.mitayclient.util.Skin.SkinCacheHelper;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

import static com.flower.mitayclient.util.MitayUtils.getWorldIcon;
import static com.flower.mitayclient.util.Resource.getStringWidth;

@Mixin(PlayerTabOverlay.class)
public class PlayerListHudMixin
{

    private static final Identifier UP = ModIdentifier.get("textures/gui/hud/player_list/rect/up.png");
    private static final Identifier UP_DARK = ModIdentifier.get("textures/gui/hud/player_list/rect/up_dark.png");
    private static final Identifier RECT = ModIdentifier.get("textures/gui/hud/player_list/rect/rect.png");
    private static final Identifier RECT_DARK = ModIdentifier.get("textures/gui/hud/player_list/rect/rect_dark.png");
    private static final Identifier DOWN = ModIdentifier.get("textures/gui/hud/player_list/rect/down.png");
    private static final Identifier DOWN_DARK = ModIdentifier.get("textures/gui/hud/player_list/rect/down_dark.png");


    private static final Identifier AFK = ModIdentifier.get("textures/gui/hud/player_list/afk.png");



    @Inject(at = @At("HEAD"), method = "extractRenderState", cancellable = true)
    private void render(final GuiGraphicsExtractor context, final int screenWidth, final Scoreboard scoreboard, final @Nullable Objective displayObjective, CallbackInfo ci)
    {
        ci.cancel();


        Identifier up;
        Identifier rect;
        Identifier down;
        float alpha;
        if(Mitayclient.getConfig().isDarkShown())
        {
            up = UP_DARK;
            rect = RECT_DARK;
            down = DOWN_DARK;
            alpha = 0.88f;
        }else {
            up = UP;
            rect = RECT;
            down = DOWN;
            alpha = 0.88f;
        }


        int index = 0;
        int oriY = 15;
        int curY;
        int gap = 3;

        int rectWidth;

        Map<String, TabInfoPayload.PlayerTabInfo> stringPlayerTabInfoMap = TabInfoCache.get();



        Collection<PlayerInfo> players = Minecraft.getInstance().getConnection().getOnlinePlayers();
        if (Minecraft.getInstance().options.keyPlayerList.isDown())
        {
            int biaX = getBiaX();
            rectWidth = 155 + biaX;

            //玩家列表 白框
            int playerNumber = players.size();
            int rectHeight;

            context.blit(RenderPipelines.GUI_TEXTURED, up, screenWidth-rectWidth-10,5, 0,0,rectWidth, 5,rectWidth,5, ARGB.white(alpha));
            if(playerNumber == 0)
                return;
            if(playerNumber == 1)
            {
                context.blit(RenderPipelines.GUI_TEXTURED,rect, screenWidth-rectWidth-10,10, 0,0,rectWidth, 17,rectWidth,17, ARGB.white(alpha));
                context.blit(RenderPipelines.GUI_TEXTURED,down, screenWidth-rectWidth-10,27, 0,0,rectWidth, 5,rectWidth,5, ARGB.white(alpha));

            }else
            {   rectHeight = (playerNumber-1) * 47/2;
                context.blit(RenderPipelines.GUI_TEXTURED,rect, screenWidth-rectWidth-10,10, 0,0,rectWidth, 17 + rectHeight,rectWidth,17 + rectHeight, ARGB.white(alpha));
                context.blit(RenderPipelines.GUI_TEXTURED,down, screenWidth-rectWidth-10,27 + rectHeight, 0,0,rectWidth, 5,rectWidth,5, ARGB.white(alpha));
            }

            for (Map.Entry<String, TabInfoPayload.PlayerTabInfo> stringPlayerTabInfoEntry : stringPlayerTabInfoMap.entrySet())
            {
                index++;
                curY = oriY + 20 * (index - 1) + gap * (index - 1);

                TabInfoPayload.PlayerTabInfo info = stringPlayerTabInfoEntry.getValue();
                String name = stringPlayerTabInfoEntry.getKey();
                String world = info.world();
                Identifier worldIcon = getWorldIcon(world);

                boolean isAfk = info.afk();
                int afkMinute;

                PlayerInfo player = Minecraft.getInstance().getConnection().getPlayerInfo(name);
                Component displayName = null;
                if (player != null)
                {
                    displayName = player.getTabListDisplayName();
                }

                String botTag = name.startsWith("Mod_") || name.startsWith("bot_") ?
                        "§7[机器人] " :
                        "";
                Component name_text = Component.literal(botTag + name);


//                int AFK_HEIGHT = isAfk ? 4 : 0; //挂机玩家名字显示高度
                context.text(Minecraft.getInstance().font, displayName == null ? name_text : displayName, screenWidth - rectWidth + 32, curY - 1, 0xFFDCDCDC);
                if (player != null)
                    if (player.getSkin() != null)
//                        PlayerFaceExtractor.extractRenderState(context, player.getSkin(), screenWidth - rectWidth - 2, curY - 5, 16);
                        SkinCacheHelper.renderHeadWith3D(context, player.getProfile().name(), screenWidth - rectWidth - 2, curY - 5, 16, 0.5f);

                if (isAfk)
                {
                    afkMinute = info.afkSeconds()/60;
                    context.blit(RenderPipelines.GUI_TEXTURED, AFK, screenWidth - rectWidth + 8, curY - 13, 0, 0, 16, 16, 16, 16);
                    context.text(Minecraft.getInstance().font, String.valueOf(afkMinute), screenWidth - rectWidth - 2, curY-8, 0xFFDCDCDC);
                }
                if(worldIcon != null)
                    context.blit(RenderPipelines.GUI_TEXTURED, worldIcon, screenWidth-rectWidth+18 , curY - 1, 0 ,0, 10 , 10, 10, 10);
            }
        }
    }

    private int getBiaX()
    {
        Map<String, TabInfoPayload.PlayerTabInfo> infoMap = TabInfoCache.get();
        String longest = "";
        for (String name : infoMap.keySet())
        {
            boolean isBot = name.startsWith("Mod_") || name.startsWith("bot_");
            PlayerInfo player = Minecraft.getInstance().getConnection().getPlayerInfo(name);
            Component displayName = null;
            if (player != null)
            {
                displayName = player.getTabListDisplayName();
            }
            Component nameText = Component.literal(isBot ? "[机器人] " + name : name);
            Component finalName = displayName == null ? nameText : displayName;
            if (finalName.getString().length() > longest.length())
            {
                longest = finalName.getString();
            }
        }
        return getStringWidth(longest) - getStringWidth("[ASTRA] Mod_TestBot");
    }
}
