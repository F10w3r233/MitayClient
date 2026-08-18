package com.flower.mitayclient.GUI.HUD;

import com.flower.Mitayclient;
import com.flower.mitayclient.util.MitayUtils;
import com.flower.mitayclient.util.Resource;
import com.flower.mitayclient.util.Skin.SkinCacheHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class TargetPlayerInfoHudRenderer
{
    static Identifier status_bar;
    static PlayerInfo targetPlayer;

    public static void render(GuiGraphicsExtractor context)
    {
        if(!Minecraft.getInstance().options.hideGui)
        {
            Minecraft client = Minecraft.getInstance();

            if(Mitayclient.getConfig().isDarkShown())
            {
                status_bar = Resource.STATUS_BAR_DARK;
            }else status_bar = Resource.STATUS_BAR;

            // ============================目标玩家血量条==============================
            if(client != null)
            {
                if(client.crosshairPickEntity != null)
                {
                    if(client.crosshairPickEntity.isAlwaysTicking())
                        context.blit(RenderPipelines.GUI_TEXTURED,status_bar, 15, client.getWindow().getGuiScaledHeight()-107, 0, 0,120, 40, 120, 40);
                    if(client.getConnection().getPlayerInfo(client.crosshairPickEntity.getUUID()) != null)
                        targetPlayer = client.getConnection().getPlayerInfo(client.crosshairPickEntity.getUUID());

                    if(client.crosshairPickEntity.isAlwaysTicking() && client.crosshairPickEntity != null && targetPlayer != null)
                    {
                        //玩家头像渲染
                        SkinCacheHelper.renderHeadWith3D(context, targetPlayer.getProfile().name(), 25, client.getWindow().getGuiScaledHeight()-100, 24, 0.5f);
                        Player player = (Player) client.crosshairPickEntity;
                        //玩家名渲染
                        context.text(Minecraft.getInstance().font, String.valueOf(player.getScoreboardName()), 60, client.getWindow().getGuiScaledHeight()-97, MitayUtils.getNameColor(targetPlayer.getProfile().name()));
                        //玩家血量渲染
                        context.text(Minecraft.getInstance().font, "血量:" + (int) player.getHealth(), 60, client.getWindow().getGuiScaledHeight()-87, MitayUtils.getFontColor());
                    }
                }
            }
        }
    }
}
